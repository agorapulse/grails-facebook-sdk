package grails.plugin.facebooksdk

import org.springframework.beans.factory.InitializingBean
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.web.context.request.RequestContextHolder

class FacebookContextService implements InitializingBean {

    static scope = 'request'

    private final static List DROP_QUERY_PARAMS = ['code','state','signed_request']
    private final static long EXPIRATION_PREVENTION_THRESHOLD = 600000 // 10 minutes

    boolean transactional = false

    FacebookAppCookieScope facebookAppCookieScope
    def facebookAppPersistentScope // Any persistentScope class with the following methods : deleteData, deleteAllData, getData, isEnabled, setData
    FacebookAppService facebookAppService // Injected by Spring
    def grailsApplication // Injected by Spring

    boolean authenticated
    FacebookApp app
    FacebookSignedRequest signedRequest
    FacebookUser user

    private String stateToken = '' // CSRF state token

    FacebookContextService() {
        log.debug "Constructor..."
    }

    void afterPropertiesSet() {
        log.debug "afterPropertiesSet..."
        authenticated = facebookAppService.userId ? true : false
        app = new FacebookApp(
                id: config.appId,
                permissions: config.appPermissions?.tokenize(',') ?: [],
                secret: config.appSecret,
                facebookAppService: facebookAppService
        )
        user = new FacebookUser(
                id:  facebookAppService.userId,
                facebookAppService: facebookAppService
        )
    }

    boolean isAuthenticated() {
        user.id ? true : false
    }

    /*
     * @description Get a login status URL to fetch the status from facebook.
     * @hint
     * Available parameters:
     * - ok_session: the URL to go to if a session is found
     * - no_session: the URL to go to if the user is not connected
     * - no_user: the URL to go to if the user is not signed into facebook
          */
    String getLoginStatusURL(Map parameters = [:]) {
        if (!request.params['api_key']) parameters['api_key'] = app.id
        if (!request.params['no_session']) parameters['no_session'] = getCurrentURL()
        if (!request.params['no_user']) parameters['no_user'] = getCurrentURL()
        if (!request.params['ok_session']) parameters['ok_session'] = getCurrentURL()
        if (!request.params['session_version']) parameters['session_version'] =3
        return getURL("extern/login_status.php", parameters)
    }

    /*
     * @description Get a Login URL for use with redirects.
     * @hint By default, full page redirect is assumed. If you are using the generated URL with a window.open() call in JavaScript, you can pass in display=popup as part of the parameters.
     * Available parameters:
          * - redirect_uri: the url to go to after a successful login
          * - scope: comma separated list of requested extended perms
     */
    String getLoginURL(Map parameters = [:]) {
        establishCSRFStateToken()
        if (!parameters['client_id']) parameters['client_id'] = app.id
        if (!parameters['redirect_uri']) parameters['redirect_uri'] = getCurrentURL()
        if (!parameters['state']) parameters['state'] = getCSRFStateToken()
        return getURL('dialog/oauth', parameters)
    }

    /*
     * @description Get a Logout URL suitable for use with redirects.
     * @hint
     * Available parameters:
          * - next: the url to go to after a successful logout
     */
    String getLogoutURL(Map parameters = [:]) {
        if (!parameters['access_token']) parameters['access_token'] = user.token
        if (!parameters['next']) parameters['next'] = getCurrentURL()
        return getURL('logout.php', parameters)
    }

    /*
     * @description Get decoded signed request data
     * @hint Might return null if no signed request is found
     */
    FacebookSignedRequest getSignedRequest() {
        if (!facebookAppRequestScope.hasData('signedRequest')) {
            FacebookSignedRequest signedRequest
            if (request.getParameter('signed_request')) {
                // apps.facebook.com (default iframe page)
                signedRequest = new FacebookSignedRequest(app.secret, request.getParameter('signed_request'))
            } else if (facebookAppCookieScope.hasCookie()) {
                // Cookie created by Facebook Connect Javascript SDK
                signedRequest = new FacebookSignedRequest(app.secret, facebookAppCookieScope.getValue())
            }

            if (signedRequest) {
                facebookAppRequestScope.setData('signedRequest', signedRequest)
            }
        }
        return facebookAppRequestScope.getData('signedRequest', null)
    }

    /*
     * @description Invalidate current user (persistent data and cookie)
     * @hint
     */
    void invalidateUser() {
        facebookAppRequestScope.deleteData('accessToken')
        facebookAppRequestScope.deleteData('userId')
        if (facebookAppCookieScope.hasCookie()) {
            facebookAppCookieScope.deleteCookie()
        }
        if (facebookAppPersistentScope.isEnabled()) {
            facebookAppPersistentScope.deleteAllData()
        }
    }

    // PRIVATE

    private void establishCSRFStateToken() {
        if (getCSRFStateToken() == '') {
            stateToken = UUID.randomUUID().encodeAsMD5()
            //facebookAppPersistentScope.setData('state', stateToken)
        }
    }

    private def getConfig() {
        grailsApplication.config.grails?.plugin?.facebooksdk
    }

    private GrailsWebRequest getRequest() {
        return RequestContextHolder.getRequestAttributes()
    }

    private String getCSRFStateToken() {
        if (!stateToken) {
            stateToken = facebookAppPersistentScope.getData('state')
        }
        return stateToken
    }

    private String getCode() {
        String code = ''
        log.debug("getCode code=${request.params['code']} state=${request.params['state']}")
        if (request.params['code'] && request.params['state']) {
            String stateToken = getCSRFStateToken()
            log.debug("getCode CSRF stateToken=$stateToken")
            if (stateToken != '' && stateToken == request.params['state']) {
                // CSRF state token has done its job, so delete it
                facebookAppRequestScope.deleteData('state')
                facebookAppPersistentScope.deleteData('state')
                code = request.params['code']
                log.debug("getCode code=$code")
            } else {
                log.debug("getCode invalid CSRF state")
            }
        }
        return code
    }

    private String getCurrentURL(String queryString = '') {
        Map params = request.params.findAll { key, value -> !DROP_QUERY_PARAMS.contains(key) }
        String currentURL = grailsLinkGenerator.link(controller:request.params.controller, action:request.params.action, params:params, absolute:true)
        if (request.getCurrentRequest().getHeader("X-Forwarded-Proto")) {
            // Detect forwarded protocol (for example from EC2 Load Balancer)
            URL url = new URL(currentURL)
            currentURL.replace(url.getProtocol(), request.getCurrentRequest().getHeader("X-Forwarded-Proto"))
        }
        return currentURL
    }

    private String getURL(path = "", parameters = [:]) {
        String url = "https://www.facebook.com/"
        if (path) {
            if (path[0] == "/") {
                path = path.substring(1)
            }
            url += path
        }
        if (parameters) {
            url += "?" + serializeQueryString(parameters)
        }
        return url
    }

    private boolean isAccessTokenExpired() {
        long expirationTime = facebookAppPersistentScope.getData('expirationTime', 0)
        return expirationTime && (new Date().time > expirationTime)
    }

    private boolean isAccessTokenExpiredSoon() {
        long expirationTime = facebookAppPersistentScope.getData('expirationTime', 0)
        return expirationTime && (!isAccessTokenExpired() && (expirationTime - new Date().time) < EXPIRATION_PREVENTION_THRESHOLD)
    }

    private String serializeQueryString(Map parameters, boolean urlEncoded = true) {
        if (urlEncoded) {
            return parameters.collect {key, value -> key.toLowerCase().encodeAsURL() + '=' + value.encodeAsURL() }.join('&')
        } else {
            return parameters.collect {key, value -> key.toLowerCase().encodeAsURL() + '=' + value }.join('&')
        }
    }

}
