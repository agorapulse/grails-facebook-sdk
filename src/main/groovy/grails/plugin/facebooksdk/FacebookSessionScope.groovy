package grails.plugin.facebooksdk

/**
* Uses HTTP request session attributes scope to provide a primitive persistent store, but another subclass of FacebookAppScope --one that you implement-- might use a database, memcache, or an in-memory cache.
*/
class FacebookSessionScope extends FacebookScope {
	
	final static List PERSISTENT_KEYS = ['token','code','expirationTime','state','userId']

    void deleteData(String key) {
		assert PERSISTENT_KEYS.contains(key), "Unsupported key '$key' passed to deleteData"
        request.session.removeAttribute(getKeyVariableName(key))
	}
 
	void deleteAllData() {
		PERSISTENT_KEYS.each { key ->
			deleteData(key as String)
		}
	}
	
	def getData(String key, defaultValue = "") {
		//log.debug("getData key=$key defaultValue=$defaultValue")
		assert PERSISTENT_KEYS.contains(key), "Unsupported key '$key' passed to getData"
        request.session.getAttribute(getKeyVariableName(key)) ?: defaultValue
	}
	
	boolean hasData(String key) {
        request.session.getAttribute(getKeyVariableName(key)) ? true : false
	}
	
	boolean isEnabled() {
		true
	}
		
	void setData(String key, value) {
		//log.debug("setData key=$key value=$value")
		assert PERSISTENT_KEYS.contains(key), "Unsupported key '$key' passed to setData"
        request.session.setAttribute(getKeyVariableName(key), value)
	}
	
}