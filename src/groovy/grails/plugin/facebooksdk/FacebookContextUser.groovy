package grails.plugin.facebooksdk

import com.restfb.exception.FacebookOAuthException
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest

class FacebookContextUser {

    private final static long EXPIRATION_PREVENTION_THRESHOLD = 600000 // 10 minutes

    FacebookContext context

    Map age = [:] // Only returned in Canvas apps, it will not be returned for external apps
    String country = ''
    Locale locale

    private long _id = -1
    private String _token = null
    private Logger log = Logger.getLogger(getClass())

    /*
    * @description Exchange a valid access token to get a longer expiration time (59 days)
    * @hint Returns a map with access_token and expires
    */
    void exchangeToken() {
        if (_token) {
            try {
                def result = graphClient.fetchObject('oauth/access_token', [
                        client_id: context.app.id,
                        client_secret: context.app.secret,
                        grant_type: 'fb_exchange_token',
                        fb_exchange_token: _token
                ])
                if (result['access_token'] && result['expires']) {
                    _token = result['access_token']
                    context.session.setData('token', token)
                    if (result['expires']) {
                        int expires = result['expires'] as int
                        long expirationTime = new Date().time + expires * 1000
                        context.session.setData('expirationTime', expirationTime)
                    } else {
                        // Non expiring token
                        context.session.setData('expirationTime', 0)
                    }
                }
            } catch (FacebookOAuthException exception) {
                log.warn "Could not exchange token: $exception.errorMessage"
                invalidate()
            }
        }
    }

    /*
     * @description Get the UID of the connected user, or 0 if the Facebook user is not connected.
     * @hint Determines the connected user by first examining any signed requests, then considering an authorization code, and then falling back to any persistent store storing the user.
     */
    long getId() {
        if (_id == -1) {
            // If a signed request is supplied, then it solely determines who the user is.
            if (context.signedRequest.userId) {
                _id = context.signedRequest.userId
                log.debug "Got userId from signed request (userId=$_id)"
                if (context.session.getData('userId') != _id) {
                    context.session.setData('userId', _id)
                }
            } else {
                _id = context.session.getData('userId', 0).toLong()
                log.debug "Got userId from session (userId=$_id)"
                // Use access_token to fetch user id if we have a user access_token (exchanged from code), or if the access token has changed.
                if (token && (!_id || token != context.session.getData('token'))) {
                    FacebookGraphClient graphClient = getGraphClient(token)
                    def result = graphClient.fetchObject('me', [fields: 'id'])
                    if (result && result['id']) {
                        _id = result['id'].toLong()
                        log.debug "Got userId from token (userId=$_id)"
                        if (context.session.getData('userId') != _id) {
                            context.session.setData('userId', _id)
                        }
                    } else {
                        invalidate()
                    }
                }
            }
        }
        return _id
    }

    /*
    * @description Get user OAuth accessToken
    * @hint Determines and returns the user access token, first using the signed request if present, and then falling back on the authorization code if present. The intent is to return a valid user access token, or " if one is determined to not be available.
    */
    String getToken() {
        if (_token == null) {
            if (context.signedRequest.accessToken || context.signedRequest.code) { // First, consider a signed request if it's supplied. if there is a signed request, then it alone determines the access token.
                if (context.signedRequest.accessToken) {
                    // apps.facebook.com hands the access_token in the signed_request
                    _token = context.signedRequest.accessToken
                    log.debug "Got token from signed request (token=$_token)"
                    context.session.setData('token', token)
                } else if (context.signedRequest.code) {
                    // Facebook Javascript SDK puts an authorization code in signed request
                    if (context.signedRequest.code == context.session.getData('code')) {
                        if (!isTokenExpired()) {
                            _token = context.session.getData('token')
                            log.debug "Got token from code (token=$_token)"
                            if (isTokenExpiredSoon()) {
                                exchangeToken()
                            }
                        }
                    } else {
                        _token = getTokenFromCode(context.signedRequest.code)
                        log.debug "Got token from signed request code (token=$_token)"
                    }
                }

                if (!_token) {
                    // Signed request states there's no access token, so anything stored should be invalidated.
                    invalidate()
                }
            } else {
                // Falling back on the authorization code if present
                String code = ''
                if (context.request.params['code'] && context.request.params['state']) {
                    String state = context.session.getData('state')
                    if (state && state == context.request.params['state']) {
                        // CSRF state token has done its job, so delete it
                        context.session.deleteData('state')
                        code = context.request.params['code']
                    }
                }
                if (code && code != context.session.getData('code')) {
                    _token = getTokenFromCode(code, context.currentURL)
                    log.debug "Got token from server side redirect params code (token=$_token)"
                    if (!_token) {
                        // Code was bogus, so everything based on it should be invalidated.
                        invalidate()
                    }
                } else {
                    // Falling back on persistent store, knowing nothing explicit (signed request, authorization code, etc.) was present to shadow it (or we saw a code in URL/FORM scope, but it's the same as what's in the persistent store)
                    _token = context.session.getData('token', '')
                }
            }
        }
        return _token
    }

    long getTokenExpirationTime() {
        context.session.getData('expirationTime', 0).toLong()
    }

    /*
    * @description Invalidate current user (persistent data and cookie)
    * @hint
    */
    void invalidate() {
        if (context.cookie.cookie) {
            context.cookie.delete()
        }
        context.session.deleteAllData()
        _id = 0
        _token = ''
    }

    boolean isTokenExpired() {
        long expirationTime = context.session.getData('expirationTime', 0).toLong()
        return expirationTime && (new Date().time > expirationTime)
    }

    boolean isTokenExpiredSoon() {
        long expirationTime = context.session.getData('expirationTime', 0).toLong()
        return expirationTime && (!isTokenExpired() && (expirationTime - new Date().time) < EXPIRATION_PREVENTION_THRESHOLD)
    }

    String toString() {
        "FacebookUser(id: $id)"
    }

    // PRIVATE

    private FacebookGraphClient getGraphClient(String token = '') {
        new FacebookGraphClient(
                token,
                context?.config?.timeout ?: FacebookGraphClient.DEFAULT_READ_TIMEOUT_IN_MS,
                context?.config?.proxyHost ?: null,
                context?.config?.proxyPort ?: null
        )
    }

    private String getTokenFromCode(String code, String redirectUri = '') {
        String accessToken = ''
        try {
            def result = graphClient.fetchObject('oauth/access_token', [
                    client_id: context.app.id,
                    client_secret: context.app.secret,
                    code: code,
                    redirect_uri: redirectUri
            ])
            if (result['access_token']) {
                accessToken = result['access_token']
                context.session.setData('token', accessToken)
                context.session.setData('code', code)
                if (result['expires']) {
                    Integer expires = result['expires'] as Integer
                    long expirationTime = new Date().time + expires * 1000
                    context.session.setData('expirationTime', expirationTime)
                }
            }
        } catch (FacebookOAuthException exception) {
            log.warn "Could not get token from code: $exception.errorMessage"
            invalidate()
        }
        return accessToken
    }

}
