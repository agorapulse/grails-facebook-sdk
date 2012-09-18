package grails.plugin.facebooksdk

import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.beans.factory.InitializingBean
import org.springframework.web.context.request.RequestContextHolder
import org.codehaus.groovy.grails.commons.GrailsApplication

class FacebookContext implements InitializingBean {

    private final static List DROP_QUERY_PARAMS = ['code','state','signed_request']

    LinkGenerator grailsLinkGenerator // Injected by Spring
    GrailsApplication grailsApplication

    FacebookApp app
    FacebookSignedRequest signedRequest = new FacebookSignedRequest()
    FacebookUser user

    private FacebookAppSessionScope _appSessionScope
    private FacebookAppCookieScope _appCookieScope
    private Logger log = Logger.getLogger(getClass())

    void afterPropertiesSet() {
        def appConfig
        String appIdParamName = config.appIdParamName ?: 'app_id'
        if (config.apps) {
            // Multiple app config
            if (request.params[appIdParamName]) {
                appConfig = config.apps.find { it.id == request.params[appIdParamName].toLong() }
            } else {
                appConfig = config.apps.find { it.controller == request.params.controller }
            }
        }

        if (!appConfig && config.app) {
            // Single app config
            appConfig = config.app
        } else if (config.appId && config.appSecret) {
            // Single app legacy config
            appConfig = [
                    id:  config.appId,
                    secret: config.appSecret
            ]
            if (config.appPermissions) appConfig = config.appPermissions.tokenize(',') ?: []
        }

        if (!appConfig && config.apps) assert request.params[appIdParamName], 'Invalid facebook config, for multiple apps, app_id (or custom appIdParamName) must be passed in params'
        assert appConfig, 'Facebook app config not found'
        assert appConfig.id && appConfig.secret, 'Invalid Facebook app config, appId and appSecret must be defined'

        app = new FacebookApp(
                id: appConfig.id,
                config: config,
                permissions: appConfig.permissions ?: [],
                secret: appConfig.secret
        )

        user = new FacebookUser(
                context: this,
                config: config
        )

        if (request.params['signed_request']) {
            // apps.facebook.com (default iframe page or page tab)
            signedRequest = new FacebookSignedRequest(app.secret, request.params['signed_request'])
            if (signedRequest.accessToken) {
                user.token // Get token to put it in session scope
            }
            log.debug "Got signed request from params"
        } else if (cookie.value) {
            // Cookie created by Facebook Connect Javascript SDK
            signedRequest = new FacebookSignedRequest(app.secret, cookie.value)
            log.debug "Got signed request from cookie"
        }
    }

    boolean isAuthenticated() {
        user?.id ? true : false
    }

    /*
    * @description Cookie scope proxy linked to current facebook app.
    */
    FacebookAppCookieScope getCookie() {
        if (!_appCookieScope) {
            _appCookieScope = new FacebookAppCookieScope(appId: app.id)
        }
        return _appCookieScope
    }

    /*
    * @description Session scope proxy linked to current facebook app.
    */
    FacebookAppSessionScope getSession() {
        if (!_appSessionScope) {
            _appSessionScope = new FacebookAppSessionScope(appId: app.id)
        }
        return _appSessionScope
    }

    /*
    * @description Get a login status URL to fetch the status from facebook.
    * @hint
    * Available parameters:
    * - ok_session: the URL to go to if a session is found
    * - no_session: the URL to go to if the user is not connected
    * - no_user: the URL to go to if the user is not signed into facebook
    */
    String getLoginStatusUrl(Map parameters = [:]) {
        if (!request.params['api_key']) parameters['api_key'] = app.id
        if (!request.params['no_session']) parameters['no_session'] = currentURL
        if (!request.params['no_user']) parameters['no_user'] = currentURL
        if (!request.params['ok_session']) parameters['ok_session'] = currentURL
        if (!request.params['session_version']) parameters['session_version'] = 3
        return buildFacebookURL("extern/login_status.php", parameters)
    }

    /*
     * @description Get a Login URL for use with redirects.
     * @hint By default, full page redirect is assumed. If you are using the generated URL with a window.open() call in JavaScript, you can pass in display=popup as part of the parameters.
     * Available parameters:
          * - redirect_uri: the url to go to after a successful login
          * - scope: comma separated list of requested extended perms
     */
    String getLoginURL(Map parameters = [:]) {
        String state = UUID.randomUUID().encodeAsMD5()
        session.setData('state', state)
        if (!parameters['client_id']) parameters['client_id'] = app.id
        if (!parameters['redirect_uri']) parameters['redirect_uri'] = currentURL
        if (!parameters['scope']) parameters['scope'] = app.permissions.join(',')
        if (!parameters['state']) parameters['state'] = state
        return buildFacebookURL('dialog/oauth', parameters)
    }

    /*
     * @description Get a Logout URL suitable for use with redirects.
     * @hint
     * Available parameters:
          * - next: the url to go to after a successful logout
     */
    String getLogoutURL(Map parameters = [:]) {
        if (!parameters['access_token']) parameters['access_token'] = user.token
        if (!parameters['next']) currentURL
        return buildFacebookURL('logout.php', parameters)
    }

    String toString() {
        "FacebookContext(app: $app, authenticated: $authenticated, signedRequest: $signedRequest, user: $user)"
    }

    // PRIVATE

    private String buildFacebookURL(path = "", parameters = [:]) {
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

    private def getConfig() {
        ApplicationHolder.application.config.grails.plugin.facebooksdk
    }

    private String getCurrentURL(String queryString = '') {
        Map params = request.params.findAll { key, value -> !DROP_QUERY_PARAMS.contains(key) }
        String linkUrl = grailsLinkGenerator.link(
                absolute: true,
                action: request.params.action,
                controller: request.params.controller,
                params: params
        )
        if (request.currentRequest.getHeader('X-Forwarded-Proto')) {
            // Detect forwarded protocol (for example from EC2 Load Balancer)
            linkUrl.replace(new URL(linkUrl).protocol, request.currentRequest.getHeader('X-Forwarded-Proto'))
        }
        return linkUrl
    }

    private GrailsWebRequest getRequest() {
        return RequestContextHolder.getRequestAttributes()
    }

    private String serializeQueryString(Map parameters, boolean urlEncoded = true) {
        if (urlEncoded) {
            return parameters.collect {key, value -> key.toLowerCase().encodeAsURL() + '=' + value.encodeAsURL() }.join('&')
        } else {
            return parameters.collect {key, value -> key.toLowerCase().encodeAsURL() + '=' + value }.join('&')
        }
    }

}
