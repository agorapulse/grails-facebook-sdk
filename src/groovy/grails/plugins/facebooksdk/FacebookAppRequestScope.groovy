package grails.plugins.facebooksdk

/**
* Uses HTTP request attributes scope to cache data during the duration of the request.
*/
class FacebookAppRequestScope extends FacebookAppScope {
	
	final static List REQUEST_KEYS = ['accessToken','code','state','signedRequest','userId']
	
	Boolean deleteData(String key) {
		assert REQUEST_KEYS.contains(key), "Unsupported key '$key' passed to deleteData"
		request.getCurrentRequest().removeAttribute(getKeyVariableName(key))
	}
	
	def getData(String key, defaultValue = '') {
		assert REQUEST_KEYS.contains(key), "Unsupported key '$key' passed to getData"
		request.getCurrentRequest().getAttribute(getKeyVariableName(key)) ?: defaultValue
	}
 
	Boolean hasData(String key) {
		request.getCurrentRequest().getAttribute(getKeyVariableName(key)) ? true : false
	}
		
	void setData(String key, value) {
		assert REQUEST_KEYS.contains(key), "Unsupported key '$key' passed to setData"
		request.getCurrentRequest().setAttribute(getKeyVariableName(key), value)
	}
	
}