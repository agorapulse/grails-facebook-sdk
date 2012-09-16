package grails.plugin.facebooksdk

import com.restfb.exception.FacebookOAuthException
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.web.context.request.RequestContextHolder

class FacebookAppService {

	boolean transactional = false
	
	//FacebookContextService facebookContextProxyService
	//FacebookAppCookieScope facebookAppCookieScope
	//def facebookAppPersistentScope // Any persistentScope class with the following methods : deleteData, deleteAllData, getData, isEnabled, setData
	//FacebookAppRequestScope facebookAppRequestScope
	//def grailsApplication
    //def grailsLinkGenerator

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
				Map parameters = [
                        client_id: facebookContextProxyService.app.id,
						client_secret: facebookContextProxyService.app.secret,
						grant_type: 'fb_exchange_token',
						fb_exchange_token: oldAccessToken
                ] //.encodeAsURL()
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
							client_secret:facebookContextProxyService.app.secret,
							grant_type:'client_credentials']
			def result = facebookGraphClient.fetchObject('oauth/access_token', parameters)
			if (result['access_token']) accessToken = result['access_token']
		} else {
			accessToken = facebookContextProxyService.app.id + "|" + facebookContextProxyService.app.secret
		}
		return accessToken
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
				Map parameters = [client_id: facebookContextProxyService.app.id,
								client_secret: facebookContextProxyService.app.secret,
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
	


}