package grails.plugin.facebooksdk

/**
* Uses HTTP request attributes scope to cache data during the duration of the request.
*/
class FacebookAppRequestScope extends FacebookAppScope {
	
	final static List REQUEST_KEYS = ['accessToken','code','state','signedRequest','userId']
	
	void deleteData(String key) {
		assert REQUEST_KEYS.contains(key), "Unsupported key '$key' passed to deleteData"
		request.currentRequest.removeAttribute(getKeyVariableName(key))
	}
	
	def getData(String key, defaultValue = '') {
		assert REQUEST_KEYS.contains(key), "Unsupported key '$key' passed to getData"
		request.currentRequest.getAttribute(getKeyVariableName(key)) ?: defaultValue
	}
 
	boolean hasData(String key) {
		request.currentRequest.getAttribute(getKeyVariableName(key)) ? true : false
	}
		
	void setData(String key, value) {
		assert REQUEST_KEYS.contains(key), "Unsupported key '$key' passed to setData"
		request.currentRequest.setAttribute(getKeyVariableName(key), value)
	}
	
}