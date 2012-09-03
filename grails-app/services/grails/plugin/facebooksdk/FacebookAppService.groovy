package grails.plugin.facebooksdk

import com.restfb.exception.FacebookOAuthException
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.web.context.request.RequestContextHolder

class FacebookAppService {
	
	final static String VERSION = '3.1.1'
	private final static List DROP_QUERY_PARAMS = ['code','state','signed_request']
	private final static long EXPIRATION_PREVENTION_THRESHOLD = 600000 // 10 minutes
	
	boolean transactional = false
	
	FacebookApp facebookApp
	FacebookAppCookieScope facebookAppCookieScope
	def facebookAppPersistentScope // Any persistentScope class with the following methods : deleteData, deleteAllData, getData, isEnabled, setData
	FacebookAppRequestScope facebookAppRequestScope
	def grailsApplication
    def grailsLinkGenerator

	/*
	* @description Exchange a valid access token to get a longer expiration time (59 days)
	* @hint
	*/
	Map exchangeAccessToken(String oldAccessToken) {
		Map result = [:]
		if (oldAccessToken) {
			//log.debug("exchangeAccessToken oldAccessToken=$oldAccessToken")
			try {
                FacebookGraphClient facebookGraphClient = new FacebookGraphClient(
                        '',
                        config.timeout ?: FacebookGraphClient.DEFAULT_READ_TIMEOUT_IN_MS,
                        config.proxyHost ?: null,
                        config.proxyPort ?: null
                )
				Map parameters = [client_id:facebookApp.id,
								client_secret:facebookApp.secret,
								grant_type:'fb_exchange_token',
								fb_exchange_token:oldAccessToken] //.encodeAsURL()
				result = facebookGraphClient.fetchObject('oauth/access_token', parameters)
				//log.debug("exchangeAccessToken result=$result")
			} catch (FacebookOAuthException exception) {
				//if (exception.message.find('Code was invalid or expired')) {
				//	invalidateUser()
				//}
				throw exception
			}
		}
		return result
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
	
	/*
	* @description Get OAuth accessToken
	* @hint Determines the access token that should be used for API calls. The first time this is called, accessToken is set equal to either a valid user access token, or it's set to the application access token if a valid user access token wasn't available. Subsequent calls return whatever the first call returned.
	*/
	String getAccessToken() {
		if (!facebookAppRequestScope.hasData('accessToken')) {
			String accessToken = getUserAccessToken()
			if (!accessToken) {
				// No user access token, establish access token to be the application access token, in case we navigate to the /oauth/access_token endpoint, where SOME access token is required.
				accessToken = getApplicationAccessToken()
			}
			facebookAppRequestScope.setData('accessToken', accessToken)
		}
		return facebookAppRequestScope.getData('accessToken')
	}

	/*
	* @description Get application OAuth accessToken
	* @hint
	*/
	String getApplicationAccessToken(boolean oauthEnabled = false) {
		String accessToken = ""
		if (oauthEnabled) {
            FacebookGraphClient facebookGraphClient = new FacebookGraphClient(
                    '',
                    config.timeout ?: FacebookGraphClient.DEFAULT_READ_TIMEOUT_IN_MS,
                    config.proxyHost ?: null,
                    config.proxyPort ?: null
            )
			Map parameters = [client_id:facebookApp.id,
							client_secret:facebookApp.secret,
							grant_type:'client_credentials']
			def result = facebookGraphClient.fetchObject('oauth/access_token', parameters)
			if (result['access_token']) accessToken = result['access_token']
		} else {
			accessToken = facebookApp.id + "|" + facebookApp.secret
		}
		return accessToken
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
		if (!request.params['api_key']) parameters['api_key'] = facebookApp.id
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
		if (!parameters['client_id']) parameters['client_id'] = facebookApp.id
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
		if (!parameters['access_token']) parameters['access_token'] = getUserAccessToken()
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
                signedRequest = new FacebookSignedRequest(facebookApp.secret, request.getParameter('signed_request'))
            } else if (facebookAppCookieScope.hasCookie()) {
                // Cookie created by Facebook Connect Javascript SDK
                signedRequest = new FacebookSignedRequest(facebookApp.secret, facebookAppCookieScope.getValue())
            }

            if (signedRequest) {
                facebookAppRequestScope.setData('signedRequest', signedRequest)
            }
        }
        return facebookAppRequestScope.getData('signedRequest', null)
    }

    /*
     * @description Get user OAuth accessToken
     * @hint Determines and returns the user access token, first using the signed request if present, and then falling back on the authorization code if present.	The intent is to return a valid user access token, or " if one is determined to not be available.
     */
	String getUserAccessToken() {
		if (!facebookAppRequestScope.hasData('accessToken')) {
			String accessToken = ''
			// First, consider a signed request if it's supplied. if there is a signed request, then it alone determines the access token.
            FacebookSignedRequest signedRequest = getSignedRequest()
			if (signedRequest) {
				if (signedRequest.accessToken) {
					// apps.facebook.com hands the access_token in the signed_request
					accessToken = signedRequest.accessToken
					log.debug("getUserAccessToken accessToken=$accessToken (from apps.facebook.com signed_request)")
					facebookAppPersistentScope.setData('accessToken', accessToken)
				} else if (signedRequest.code) {
					// Facebook Javascript SDK puts an authorization code in signed request
					if (signedRequest.code == facebookAppPersistentScope.getData('code')) {
						if (!isAccessTokenExpired()) {
							if (isAccessTokenExpiredSoon()) {
								def result = exchangeAccessToken(facebookAppPersistentScope.getData('accessToken'))
								if (result['access_token'] && result['expires']) {
									log.debug("getUserAccessToken accessToken=$accessToken (exchanged from old token, expiring soon)")
									accessToken = result['access_token']
									facebookAppPersistentScope.setData('accessToken', accessToken)
									Integer expires = result['expires'] as Integer
									long expirationTime = new Date().time + expires * 1000
									facebookAppPersistentScope.setData('expirationTime', expirationTime)
								}
							} else {
								accessToken = facebookAppPersistentScope.getData('accessToken')
								log.debug("getUserAccessToken accessToken=$accessToken (from persistent scope)")
							}
						}
					} else {
						accessToken = getAccessTokenFromCode(signedRequest.code, '')
						log.debug("getUserAccessToken accessToken=$accessToken (from code in signed request)")
					}
				}
				
				if (!accessToken) {
					// Signed request states there's no access token, so anything stored should be invalidated.
					invalidateUser()
				}
			} else {
				// Falling back on the authorization code if present
				String code = getCode()
				if (code && code != facebookAppPersistentScope.getData('code')) {
					accessToken = getAccessTokenFromCode(code)
					log.debug("getUserAccessToken accessToken=$accessToken (from code in persistent scope)")
					if (!accessToken) {
						// Code was bogus, so everything based on it should be invalidated.
						invalidateUser()
					}
				} else {
					// Falling back on persistent store, knowing nothing explicit (signed request, authorization code, etc.) was present to shadow it (or we saw a code in URL/FORM scope, but it"s the same as what"s in the persistent store)
					accessToken = facebookAppPersistentScope.getData('accessToken', '')
					log.debug("getUserAccessToken accessToken=$accessToken (from persistent scope)")
				}
			}
			facebookAppRequestScope.setData('accessToken', accessToken)
		}
		return facebookAppRequestScope.getData('accessToken', '')
	}
	
	/*
	* @description Get the UID of the connected user, or 0 if the Facebook user is not connected.
	* @hint Determines the connected user by first examining any signed requests, then considering an authorization code, and then falling back to any persistent store storing the user.
	*/
	long getUserId() {
		if (!facebookAppRequestScope.hasData('userId')) {
			long userId = 0
			// If a signed request is supplied, then it solely determines who the user is.
            FacebookSignedRequest signedRequest = getSignedRequest()
			if (signedRequest) {
				if (signedRequest.userId) {
					userId = signedRequest.userId
					log.debug("getUserId userId=$userId (from signedRequest)")
					if (facebookAppPersistentScope.getData('userId') != userId) {
						facebookAppPersistentScope.setData('userId', userId)
					}
				} else {
					// If the signed request didn't present a user id, then invalidate all entries in any persistent store.
					invalidateUser()
				}
			} else {
				userId = facebookAppPersistentScope.getData('userId', 0)
				log.debug("getUserId from userId=$userId (from persistentScope)")
				// Use access_token to fetch user id if we have a user access_token, or if the cached access token has changed.
				String accessToken = getUserAccessToken()
				if (accessToken && !(userId > 0 && accessToken == facebookAppPersistentScope.getData('accessToken'))) {
                    FacebookGraphClient facebookGraphClient = new FacebookGraphClient(
                            accessToken,
                            config.timeout ?: FacebookGraphClient.DEFAULT_READ_TIMEOUT_IN_MS,
                            config.proxyHost ?: null,
                            config.proxyPort ?: null
                    )
					def result = facebookGraphClient.fetchObject('me', [fields: 'id'])
					if (result?.id) {
						userId = result.id.toLong()
						if (facebookAppPersistentScope.getData('userId') != userId) {
							facebookAppPersistentScope.setData('userId', userId)
						}
					} else {
						invalidateUser()
				 	}
				}
			}
			facebookAppRequestScope.setData('userId', userId)
		}
		return facebookAppRequestScope.getData('userId', 0)
	}
	
	// PRIVATE

    private void establishCSRFStateToken() {
		if (getCSRFStateToken() == '') {
			String stateToken = UUID.randomUUID().encodeAsMD5()
			facebookAppRequestScope.setData('state', stateToken)
			facebookAppPersistentScope.setData('state', stateToken)
		}
	}

    private def getConfig() {
        grailsApplication.config.grails?.plugin?.facebooksdk
    }

    private GrailsWebRequest getRequest() {
        return RequestContextHolder.getRequestAttributes()
    }

	private String getAccessTokenFromCode(String code, String redirectUri = null) {
		String accessToken = ''
		if (code) {

			if (redirectUri == null) {
				redirectUri = getCurrentURL()
			}

			log.debug("getAccessTokenFromCode code=$code redirectUri=$redirectUri")
			try {
                FacebookGraphClient facebookGraphClient = new FacebookGraphClient(
                        '',
                        config.timeout ?: FacebookGraphClient.DEFAULT_READ_TIMEOUT_IN_MS,
                        config.proxyHost ?: null,
                        config.proxyPort ?: null
                )
				Map parameters = [client_id: facebookApp.id,
								client_secret: facebookApp.secret,
								code: code,
								redirect_uri: redirectUri] //.encodeAsURL()
				def result = facebookGraphClient.fetchObject('oauth/access_token', parameters)
				log.debug("getAccessTokenFromCode result=$result")
				if (result['access_token']) {
					accessToken = result['access_token']
					facebookAppPersistentScope.setData('accessToken', accessToken)
					facebookAppPersistentScope.setData('code', code)
					if (result['expires']) {
						Integer expires = result['expires'] as Integer
						long expirationTime = new Date().time + expires * 1000
						facebookAppPersistentScope.setData('expirationTime', expirationTime)
					}
				}
			} catch (FacebookOAuthException exception) {
				if (exception.message.find('Code was invalid or expired')) {
					invalidateUser()
				}
				throw exception
			}
		}
		return accessToken
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
	
	private String getCSRFStateToken() {
		if (!facebookAppRequestScope.hasData('state')) {
			facebookAppRequestScope.setData('state', facebookAppPersistentScope.getData('state'))
		}
		return facebookAppRequestScope.getData('state')
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