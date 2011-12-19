package grails.plugins.facebooksdk

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST
import static java.net.HttpURLConnection.HTTP_FORBIDDEN
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR
import static java.net.HttpURLConnection.HTTP_OK
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED
import static java.util.logging.Level.INFO

import com.restfb.DefaultFacebookClient
import com.restfb.DefaultWebRequestor
import com.restfb.DefaultFacebookClient.DefaultGraphFacebookExceptionMapper
import com.restfb.DefaultFacebookClient.Requestor
import com.restfb.WebRequestor.Response
import com.restfb.exception.FacebookGraphException
import com.restfb.exception.FacebookJsonMappingException
import com.restfb.exception.FacebookNetworkException
import com.restfb.exception.FacebookResponseStatusException
import com.restfb.json.JsonException
import com.restfb.json.JsonObject
import com.restfb.types.User
import grails.plugins.facebooksdk.util.*

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.beans.factory.InitializingBean
import org.springframework.web.context.request.RequestContextHolder

class FacebookAppService {
	
	final static List DROP_QUERY_PARAMS = ["code","state","signed_request"]
	final static String VERSION = "3.1.1"
	
	boolean transactional = false
	
	def appId = 0
	def appPermissions = ""
	def appSecret = ""
	def facebookAppCookieScope
	def facebookAppPersistentScope // Any persistentScope class with the following methods : deleteData, deleteAllData, getData, isEnabled, setData
	def facebookAppRequestScope

	GrailsWebRequest getRequest() {
		return RequestContextHolder.getRequestAttributes()
	}
	
	/*
	* @description Invalidate current user (persistent data and cookie)
	* @hint
	*/
	void invalidateUser() {
		facebookAppRequestScope.deleteData("access_token")
		facebookAppRequestScope.deleteData("user_id")
		if (facebookAppCookieScope.hasCookie()) {
			facebookAppCookieScope.deleteCookie()
		}
		if (facebookAppPersistentScope.isEnabled()) {
			facebookAppPersistentScope.deleteAllData()
		}
	}
	
	/*
	* @description Get OAuth accessToken
	* @hint Determines the access token that should be used for API calls. The first time this is called, accessToken is set equal to either a valid user access token, or it"s set to the application access token if a valid user access token wasn"t available. Subsequent calls return whatever the first call returned.
	*/
	public String getAccessToken() {
		if (!facebookAppRequestScope.hasData("access_token")) {
			String accessToken = getUserAccessToken()
			if (!accessToken) {
				// No user access token, establish access token to be the application access token, in case we navigate to the /oauth/access_token endpoint, where SOME access token is required.
				accessToken = getApplicationAccessToken()
			}
			facebookAppRequestScope.setData("access_token", accessToken)
		}
		return facebookAppRequestScope.getData("access_token")
	}
	
	/*
	* @description Get application OAuth accessToken
	* @hint
	*/
	public String getApplicationAccessToken(Boolean apiEnabled = false) {
		String accessToken = ""
		if (apiEnabled) {
			accessToken = callOAuthAppAccessTokenService()
		} else {
			accessToken = this.appId + "|" + this.appSecret
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
		String currentUrl = getCurrentUrl()
		if (!request.params["api_key"]) parameters["api_key"] = this.appId
		if (!request.params["no_session"]) parameters["no_session"] = currentUrl
		if (!request.params["no_user"]) parameters["no_user"] = currentUrl
		if (!request.params["ok_session"]) parameters["ok_session"] = currentUrl
		if (!request.params["session_version"]) parameters["session_version"] =3
		return getUrl("extern/login_status.php", parameters)
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
		if (!parameters["client_id"]) parameters["client_id"] = this.appId
		if (!parameters["redirect_uri"]) parameters["redirect_uri"] = getCurrentUrl()
		if (!parameters["state"]) parameters["state"] = getCSRFStateToken()
		return getUrl("dialog/oauth", parameters)
	}
	 
	 /*
	* @description Get a Logout URL suitable for use with redirects.
	* @hint
	* Available parameters:
		 * - next: the url to go to after a successful logout
	*/
	String getLogoutURL(Map parameters = [:]) {
		if (!parameters["access_token"]) parameters["access_token"] = getUserAccessToken()
		if (!parameters["next"]) parameters["next"] = getCurrentUrl()
		return getUrl("logout.php", parameters)
	}
	
	/*
	* @description Get user OAuth accessToken
	* @hint Determines and returns the user access token, first using the signed request if present, and then falling back on the authorization code if present.	The intent is to return a valid user access token, or " if one is determined to not be available.
	*/
	public String getUserAccessToken() {
		String accessToken = ""
		// First, consider a signed request if it"s supplied. if there is a signed request, then it alone determines the access token.
		Map signedRequest = getSignedRequestData()
		if (signedRequest) {
			if (signedRequest["oauth_token"]) {
				// apps.facebook.com hands the access_token in the signed_request
				accessToken = signedRequest["oauth_token"]
				facebookAppPersistentScope.setData("access_token", accessToken)
			} else if (signedRequest["code"]) {
				// Facebook Javascript SDK puts an authorization code in signed request
				if (signedRequest["code"] == facebookAppPersistentScope.getData("code")) {
					accessToken = facebookAppPersistentScope.getData("access_token")
				} else {
					accessToken = getAccessTokenFromCode(signedRequest["code"], "")
					if (accessToken) {
						facebookAppPersistentScope.setData("code", signedRequest["code"])
						facebookAppPersistentScope.setData("access_token", accessToken)
					}
				}
			}
			
			if (!accessToken) {
				// Signed request states there"s no access token, so anything stored should be invalidated.
				invalidateUser()
			}
		} else {
			// Falling back on the authorization code if present
			String code = getAuthorizationCode()
			if (code && code != facebookAppPersistentScope.getData("code")) {
				accessToken = getAccessTokenFromCode(code)
				if (accessToken) {
					facebookAppPersistentScope.setData("code", code)
					facebookAppPersistentScope.setData("access_token", accessToken)
				} else {
					// Code was bogus, so everything based on it should be invalidated.
					invalidateUser()
				}
			} else {
				// Falling back on persistent store, knowing nothing explicit (signed request, authorization code, etc.) was present to shadow it (or we saw a code in URL/FORM scope, but it"s the same as what"s in the persistent store)
				accessToken = facebookAppPersistentScope.getData("access_token")
				if (!accessToken) {
					// Invalid session, so everything based on it should be invalidated.
					invalidateUser()
				}
			}
		}
		return accessToken
	}
	
	/*
	* @description Get the UID of the connected user, or 0 if the Facebook user is not connected.
	* @hint Determines the connected user by first examining any signed requests, then considering an authorization code, and then falling back to any persistent store storing the user.
	*/
	public Long getUserId() {
		if (!facebookAppRequestScope.hasData("user_id")) {
			Long userId = 0
			// If a signed request is supplied, then it solely determines who the user is.
			Map signedRequestData = getSignedRequestData()
			if (signedRequestData) {
				if (signedRequestData["user_id"]) {
					userId = signedRequestData["user_id"].toLong()
					facebookAppPersistentScope.setData("user_id", userId)
				} else {
					// If the signed request didn"t present a user id, then invalidate all entries in any persistent store.
					invalidateUser()
				}
			} else {
				userId = facebookAppPersistentScope.getData("user_id", 0)
				// Use access_token to fetch user id if we have a user access_token, or if the cached access token has changed.
				String accessToken = getAccessToken()
				if (accessToken && accessToken != getApplicationAccessToken() && !(userId > 0 && accessToken == facebookAppPersistentScope.getData("access_token"))) {
					DefaultFacebookClient facebookClient = new DefaultFacebookClient(accessToken)
					User user = DefaultFacebookClient.fetchObject("me", User.class, Parameter.with("fields", "id"))
					if (user?.id) {
						userId = user.id
						facebookAppPersistentScope.setData("user_id", userId)
					} else {
						invalidateUser()
				 	}
				}
			}
			facebookAppRequestScope.setData("user_id", userId)
		}
		return facebookAppRequestScope.getData("user_id") ?: 0
	}
	
	// PRIVATE
	
	private String callOAuthAccessTokenService(String code = "", String grantType = "", String redirectUri = "") {
		String accessToken = ""
		String url = "https://graph.facebook.com/oauth/access_token?client_id=${this.appId}&client_secret=${this.appSecret}"
		if (code) {
			url += "&code=${code}"
		}
		if (grantType) {
			grantType += "&grant_type=${grantType}"
		}
		url += "&redirect_uri=${redirectUri.encodeAsURL()}"
		
		String result = makeRequest(url)
			
		result.tokenize("&").each {
			List keyValue = it.tokenize("=")
			if (keyValue[0] == "access_token") {
				return accessToken = keyValue[1]
			}
		}
		return accessToken
	}
	
	private String callOAuthAppAccessTokenService() {
		return callOAuthAccessTokenService("client_credentials");
	}
	
	private String callOAuthUserAccessTokenService(String code = "", String redirectUri = "") {
		return callOAuthAccessTokenService(code, "", redirectUri);
	}
	
	private void establishCSRFStateToken() {
		if (getCSRFStateToken() == "") {
			String stateToken = UUID.randomUUID().encodeAsMD5()
			facebookAppRequestScope.setData("state", stateToken)
			facebookAppPersistentScope.setData("state", stateToken)
		}
	}
	
	
	private String getAccessTokenFromCode(String code, String redirectUri = "") {
		String accessToken = ""
		if (code) {
			try {
				accessToken = callOAuthUserAccessTokenService(code, redirectUri)
			} catch (Exception exception) {
				if (exception.message.find("Code was invalid or expired")) {
					invalidateUser()
				}
				throw exception
			}
		}
		return accessToken
	}
	
	private String getAuthorizationCode() {
		String code = ""
		if (request.params["code"] && request.params["state"]) {
			String stateToken = getCSRFStateToken()
			if (stateToken != "" && stateToken == request.params["state"]) {
				// CSRF state token has done its job, so delete it
				facebookAppRequestScope.deleteData("state")
				facebookAppPersistentScope.deleteData("state")
				code = request.params["code"]
			} else {
				// Ignore (CSRF state token does not match one provided)
			}
		}
		return code
	}
	
	private String getCSRFStateToken() {
		if (!facebookAppRequestScope.hasData("state")) {
			facebookAppRequestScope.setData("state", facebookAppPersistentScope.getData("state"))
		}
		return facebookAppRequestScope.getData("state")
	}
	
	private String getCurrentUrl(String queryString = "") {
		String currentUrl = request.getCurrentRequest().getRequestURL().toString()
		String currentQueryString = request.getCurrentRequest().getQueryString()
		if (currentQueryString) {
			List keyValue
			List keyValues = currentQueryString.tokenize("&")
			if (keyValues) {
				keyValues.each {
					keyValue = it.tokenize("=")
					if (!DROP_QUERY_PARAMS.contains(keyValue[0])) {
						if (!queryString) {
							queryString += "&"
						}
						queryString += it
					}
				}
			}	
		}
		if (queryString) {
			currentUrl += "?" + queryString
		}
		if (request.getCurrentRequest().getHeader("X-Forwarded-Proto")) {
			// Detect forwarded protocol (for example from EC2 Load Balancer)
			URL url = new URL(currentUrl)
			currentUrl.replace(url.getProtocol(), request.getCurrentRequest().getHeader("X-Forwarded-Proto"))
		}
		return currentUrl
	}
	
	private Map getSignedRequestData() {
		if (!facebookAppRequestScope.hasData("signed_request")) {
			if (request.params["signed_request"]) {
				// apps.facebook.com (default iframe page)
				facebookAppRequestScope.setData("signed_request", SecurityUtils.parseSignedRequest(request.params.signed_request, this.appSecret))
			} else if (facebookAppCookieScope.hasCookie()) {
				// Cookie created by Facebook Connect Javascript SDK
				facebookAppRequestScope.setData("signed_request", SecurityUtils.parseSignedRequest(facebookAppCookieScope.getData(), this.appSecret))
			}
		}
		return facebookAppRequestScope.getData("signed_request") ?: [:]
	}
	
	private String getUrl(path = "", parameters = [:]) {
		 String url = "https://www.facebook.com/"
		 if (path) {
			 if (path[0] == "/") {
				 path = path.substring(1)
			 }
			 url += path
		 }
		 if (parameters) {
			 url += "?" + StringUtils.serializeQueryString(parameters)
		 }
		 return url
	}
	
	private String makeRequest(String url) {
		DefaultWebRequestor webRequestor = new DefaultWebRequestor()
		Response response = null;
	
		// Perform a GET to the API endpoint
		try {
			response = webRequestor.executeGet(url)
		} catch (Throwable t) {
			throw new FacebookNetworkException("Facebook request failed", t);
		}
	
		// If we get any HTTP response code other than a 200 OK or 400 Bad Request
		// or 401 Not Authorized or 403 Forbidden or 500 Internal Server Error,
		// throw an exception.
		if (HTTP_OK != response.getStatusCode() && HTTP_BAD_REQUEST != response.getStatusCode()
			&& HTTP_UNAUTHORIZED != response.getStatusCode() && HTTP_INTERNAL_ERROR != response.getStatusCode()
			&& HTTP_FORBIDDEN != response.getStatusCode())
			throw new FacebookNetworkException("Facebook request failed", response.getStatusCode());
			
		// If the response contained an error code, throw an exception.
		if (response.body.startsWith("{")) {
			try {
				JsonObject errorObject = new JsonObject(response.body);
		
				if (errorObject == null || !errorObject.has(DefaultFacebookClient.ERROR_ATTRIBUTE_NAME))
				return;
		
				JsonObject innerErrorObject = errorObject.getJsonObject(DefaultFacebookClient.ERROR_ATTRIBUTE_NAME);
		
				throw new DefaultGraphFacebookExceptionMapper().exceptionForTypeAndMessage(null, innerErrorObject.getString(DefaultFacebookClient.ERROR_TYPE_ATTRIBUTE_NAME), innerErrorObject.getString(DefaultFacebookClient.ERROR_MESSAGE_ATTRIBUTE_NAME));
			} catch (JsonException e) {
				throw new FacebookJsonMappingException("Unable to process the Facebook API response", e);
			}
		}
	
		// If there was no response error information and this was a 500 or 401
		// error, something weird happened on Facebook's end. Bail.
		if (HTTP_INTERNAL_ERROR == response.getStatusCode() || HTTP_UNAUTHORIZED == response.getStatusCode())
			throw new FacebookNetworkException("Facebook request failed", response.getStatusCode());
	
		return response.body;
		}

}
