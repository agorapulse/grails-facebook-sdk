package grails.plugins.facebooksdk

import grails.converters.JSON

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class FacebookSignedRequest {

	// Facebook access token of the current user.
	String accessToken = ''
	// App data query string parameter (only available if your app is an iframe loaded in a Page tab).
	Map appData = [:]
	// Authorization code
	String code = ''
	// Unix timestamp when the oauth_token expires.
	Long expirationTime = 0
	// Unix timestamp when the request was signed.
	Long creationTime = 0
	// Page id string, the liked boolean if the user has liked the page, the admin boolean if the user is an admin (only available if your app is an iframe loaded in a Page tab).
	Map page = [:]
	// Locale string, country string and the age object (containing the min and max number range of the age) of the current user.
	Map user = [:]
	// Facebook user identifier (UID) of the current user.
	Long userId = 0
	
	FacebookSignedRequest(String appSecret, String signedRequest) {
		Map data = parseSignedRequest(appSecret, signedRequest)
		assert data['algorithm'] == 'HMAC-SHA256', 'Unknown algorithm. Expected HMAC-SHA256'

		if (data['oauth_token']) this.accessToken = data['oauth_token']
		if (data['app_data']) this.appData = data['app_data'] as Map
		if (data['code']) this.code = data['code']
		if (data['expires']) this.expirationTime = data['expires'] as Long
		if (data['issued_at']) this.creationTime = data['issued_at'] as Long
		if (data['page']) this.page = data['page'] as Map
		if (data['user']) this.user = data['user'] as Map
		if (data['user_id']) this.userId = data['user_id'] as Long
	}

	String toString() {
		"accessToken: $accessToken, appData: $appData, code: $code, expirationTime: $expirationTime, page: $page, user: $user, userId: $userId"
	}

	// PRIVATE

	private Map parseSignedRequest(String appSecret, String signedRequest) {
		String[] signedRequestParts = signedRequest.split('\\.')
		assert (signedRequestParts.length == 2), 'Invalid Signed Request'

		String encodedParameters = signedRequest.trim().tokenize(".")[-1].replace('_', '/').replace('-', '+')
		String encodedSignature = signedRequest.trim().tokenize(".")[0].replace('_', '/').replace('-', '+')
		
		// Validate signature
		Mac hmacSha256 = Mac.getInstance('HmacSHA256')
		hmacSha256.init(new SecretKeySpec(appSecret.bytes, 'HmacSHA256'))
		byte[] expectedSignature = hmacSha256.doFinal(encodedParameters.bytes)
		assert expectedSignature == encodedSignature.decodeBase64(), 'Invalid signed request'

		// Decode parameters
		return JSON.parse(new String(encodedParameters.decodeBase64())) as Map
	}

}