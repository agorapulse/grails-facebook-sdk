package grails.plugins.facebooksdk.scope

class FacebookAppRequestScope extends FacebookAppScope {
	
	final static List REQUEST_KEYS = ['access_token','code','state','user_id','signed_request']
	
	/**
	* Uses HTTP request attributes scope to cache data during the duration of the request.
	*/
	Boolean deleteData(String key) {
		assert PERSISTENT_KEYS.contains(key), "Unsupported key passed to deleteData"
		return request.getCurrentRequest().removeAttribute(getKeyVariableName(key))
	}
	
	def getData(String key, defaultValue = '') {
		assert PERSISTENT_KEYS.contains(key), "Unsupported key passed to getData"
		return request.getCurrentRequest().getAttribute(getKeyVariableName(key)) ?: defaultValue
	}
 
	Boolean hasData(String key) {
		request.getCurrentRequest().getAttribute(getKeyVariableName(key)) ? true : false
	}
		
	void setData(String key, value) {
		assert PERSISTENT_KEYS.contains(key), "Unsupported key passed to setData"
		request.getCurrentRequest().setAttribute(getKeyVariableName(key), value)
	}
	
}
