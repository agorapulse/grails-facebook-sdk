package grails.plugins.facebooksdk.scope

/**
* Uses HTTP request session attributes scope to provide a primitive persistent store, but another subclass of FacebookApp --one that you implement-- might use a database, memcache, or an in-memory cache.
*/

class FacebookAppSessionScope extends FacebookAppScope {
	
	final static List PERSISTENT_KEYS = ['access_token','code','state','user_id']
	
	void deleteData(String key) {
		assert PERSISTENT_KEYS.contains(key), "Unsupported key passed to deleteData"
		request.session.removeAttribute(getKeyVariableName(key))
	}
 
	void deleteAllData() {
		PERSISTENT_KEYS.each {key ->
			deleteData(key)
		}
	}
	
	def getData(String key, defaultValue = "") {
		assert PERSISTENT_KEYS.contains(key), "Unsupported key passed to getData"
		return request.session.getAttribute(getKeyVariableName(key)) ?: defaultValue
	}
	
	Boolean hasData(String key) {
		return request.session.getAttribute(getKeyVariableName(key)) ? true : false
	}
	
	Boolean isEnabled() {
		return true
	}
		
	void setData(String key, value) {
		assert PERSISTENT_KEYS.contains(key), "Unsupported key passed to setData"
		request.session.setAttribute(getKeyVariableName(key), value)
	}
	
}
