package facebook.sdk.util

import grails.converters.JSON
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import org.apache.commons.codec.binary.Base64

class SecurityUtils {
	
	static String base64UrlDecode(String base64Value) {
		byte[] decoded = Base64.decodeBase64(base64Value.getBytes())
		String decodedString = new String(decoded);
		return decodedString
	}
   
   static String base64UrlEncode(String value) {
	   	byte[] encoded = Base64.encodeBase64(value.getBytes())
		String encodedString = new String(encoded)
		return encodedString
	}

	static Map parseSignedRequest(String signedRequest, String appSecret) {
		String encodedParameters = signedRequest.trim().tokenize('.')[-1]
		String encodedSignature = signedRequest.trim().tokenize('.')[0]
		def parameters = JSON.parse(base64UrlDecode(encodedParameters))
		if (parameters['algorithm'] && parameters['algorithm'] != 'HMAC-SHA256') {
			new Exception('Unknown algorithm. Expected HMAC-SHA256')
		}

		String expectedSignature = hashHmacSHA256(encodedParameters, appSecret)
		String signature = base64UrlDecode(encodedSignature)
		if (signature != expectedSignature) {
			new Exception('Invalid signed request')
		}
		return parameters
	}
	
	// PRIVATE
	
	private static String hashHmacSHA256(String value, String secretKey) {
		SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(secretKey.getBytes(), 'HmacSHA256')
		Mac hmacSha256 = Mac.getInstance('HmacSHA256')
		hmacSha256.init(secretKeySpec)
		return hmacSha256.doFinal(value.getBytes()) //'ISO-8859-1'
	}

}