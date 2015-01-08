package grails.plugin.facebooksdk

import grails.converters.JSON

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class FacebookSignedRequest {

    static final TYPE_COOKIE = 'cookie'
    static final TYPE_PARAMS = 'params'

    // Facebook access token of the current user.
	String accessToken = ''
	// App data query string parameter (only available if your app is an iframe loaded in a Page tab).
	def appData
	// Authorization code
	String code = ''
    // Unix timestamp when the request was signed.
    long creationTime = 0
    // Unix timestamp when the oauth_token expires.
	long expirationTime = 0
	// Page id string, the liked boolean if the user has liked the page, the admin boolean if the user is an admin (only available if your app is an iframe loaded in a Page tab).
	Map page = [:]
    //
    String tokenForBusiness = ''
    // Type (cookie or params)
    String type = ''
    // Locale string, country string and the age object (containing the min and max number range of the age) of the current user.
    Map user = [:]
	// Facebook user identifier (UID) of the current user.
	long userId = 0

    FacebookSignedRequest() {}

	FacebookSignedRequest(String appSecret, String signedRequest, String typeValue) {
		Map data = parseSignedRequest(appSecret, signedRequest)
		assert data['algorithm'] == 'HMAC-SHA256', 'Unknown algorithm. Expected HMAC-SHA256'
        assert typeValue in [TYPE_COOKIE, TYPE_PARAMS]

        type = typeValue

        data.each { key, value ->
            switch(key) {
                case 'oauth_token':
                    this.accessToken = value
                    break
                case 'app_data':
                    this.appData = value
                    break
                case 'code':
                    this.code = value
                    break
                case 'expires':
                    this.expirationTime = value as long
                    break
                case 'issued_at':
                    this.creationTime = value as long
                    break
                case 'page':
                    this.page = value as Map
                    break
                case 'token_for_business':
                    this.tokenForBusiness = value
                    break
                case 'user':
                    this.user = value as Map
                    break
                case 'user_id':
                    this.userId = value as long
                    break
            }
        }
	}

    String sign(String appSecret) {
        Map parameters = [algorithm: 'HMAC-SHA256']
        if (accessToken) {
            parameters['oauth_token'] = accessToken
        }
        if (appData) {
            parameters['app_data'] = appData
        }
        if (code) {
            parameters['code'] = code
        }
        if (expirationTime) {
            parameters['expires'] = expirationTime
        }
        if (creationTime) {
            parameters['issued_at'] = creationTime
        }
        if (page) {
            parameters['page'] = page
        }
        if (tokenForBusiness) {
            parameters['token_for_business'] = tokenForBusiness
        }
        if (user) {
            parameters['user'] = user
        }
        if (userId) {
            parameters['user_id'] = userId
        }

        Mac hmacSha256 = buildMac(appSecret)
        String encodedParameters = new JSON(parameters).toString().encodeAsBase64()
        String encodedSignature = hmacSha256.doFinal(encodedParameters.bytes).encodeAsBase64()

        "${encodeAsUrlSafe(encodedSignature)}.${encodeAsUrlSafe(encodedParameters)}"
    }

	String toString() {
		"FacebookSignedRequest(accessToken: $accessToken, appData: $appData, code: $code, expirationTime: $expirationTime, page: $page, type: $type, user: $user, userId: $userId)"
	}

	// PRIVATE

	private static Mac buildMac(String appSecret) {
        Mac hmacSha256 = Mac.getInstance('HmacSHA256')
        hmacSha256.init(new SecretKeySpec(appSecret.bytes, 'HmacSHA256'))
        hmacSha256
    }

    private static String encodeAsUrlSafe(String s) {
        s.replace('/', '_').replace('+', '-')
    }

    private static String decodeUrlSafe(String s) {
        s.replace('_', '/').replace('-', '+')
    }

    private static Map parseSignedRequest(String appSecret, String signedRequest) {
        String[] signedRequestParts = signedRequest.split('\\.')
        assert (signedRequestParts.length == 2), 'Invalid Signed Request'

        String encodedParameters = decodeUrlSafe(signedRequest.trim().tokenize(".")[-1])
        String encodedSignature = decodeUrlSafe(signedRequest.trim().tokenize(".")[0])

        // Validate signature
        Mac hmacSha256 = buildMac(appSecret)
        byte[] expectedSignature = hmacSha256.doFinal(encodedParameters.bytes)
        assert expectedSignature == encodedSignature.decodeBase64(), 'Invalid signed request'

        // Decode parameters
        return JSON.parse(new String(encodedParameters.decodeBase64())) as Map
    }
    
}