package grails.plugins.facebooksdk

/**
* Uses HTTP request session attributes scope to provide a primitive persistent store, but another subclass of FacebookAppScope --one that you implement-- might use a database, memcache, or an in-memory cache.
*/
class FacebookAppSessionScope extends FacebookAppScope {
	
	final static List PERSISTENT_KEYS = ['accessToken','code','expirationTime','state','userId']
	
	void deleteData(String key) {
		assert PERSISTENT_KEYS.contains(key), "Unsupported key '$key' passed to deleteData"
		request.session.removeAttribute(getKeyVariableName(key))
	}
 
	void deleteAllData() {
		PERSISTENT_KEYS.each {key ->
			deleteData(key)
		}
	}
	
	def getData(String key, defaultValue = "") {
		log.debug("getData key=$key defaultValue=$defaultValue")
		assert PERSISTENT_KEYS.contains(key), "Unsupported key '$key' passed to getData"
		request.session.getAttribute(getKeyVariableName(key)) ?: defaultValue
	}
	
	Boolean hasData(String key) {
		request.session.getAttribute(getKeyVariableName(key)) ? true : false
	}
	
	Boolean isEnabled() {
		true
	}
		
	void setData(String key, value) {
		log.debug("setData key=$key value=$value")
		assert PERSISTENT_KEYS.contains(key), "Unsupported key '$key' passed to setData"
		request.session.setAttribute(getKeyVariableName(key), value)
	}
	
}