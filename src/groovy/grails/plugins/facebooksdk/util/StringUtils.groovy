package grails.plugins.facebooksdk.util

import java.util.Map;

class StringUtils {

	static String serializeQueryString(Map parameters, Boolean urlEncoded = true) {
		if (urlEncoded) {
			return parameters.collect {key, value -> key.toLowerCase().encodeAsURL() + '=' + value.encodeAsURL() }.join('&')
		} else {
			return parameters.collect {key, value -> key.toLowerCase().encodeAsURL() + '=' + value }.join('&')
		}
	}
	
}
