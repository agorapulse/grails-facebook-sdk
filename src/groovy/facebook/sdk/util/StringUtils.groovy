package facebook.sdk.util

import java.util.Map;

class StringUtils {

	static String serializeQueryString(Map parameters, Boolean urlEncoded = true) {
		if (urlEncoded) {
			return parameters.collect {key, value -> key.toLowerCase().encodeAsURL() + '=' + value.encodeAsURL() }.join('&')
		} else {
			return parameters.collect {key, value -> key.toLowerCase().encodeAsURL() + '=' + value }.join('&')
		}
		/*String queryString = ""
		if (parameters) {
			parameters.each{key,value ->
				queryString += key.toLowerCase().encodeAsURL() + '='
				if (urlEncoded) {
					queryString += value.encodeAsURL() + '&'
				} else {
					queryString += value + '&'
				}
			}
			queryString = queryString[0..-2]
		}
		return queryString*/
	}
	
}
