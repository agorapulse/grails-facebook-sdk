package grails.plugins.facebooksdk

class FacebookJSTagLib {
	
	static namespace = 'facebook'
	
	/**
	* Initialize Facebook JS SDK
	*
	* @attr appId REQUIRED
	* @attr autoGrowth (Default to false)
	* @attr channelUrl
	* @attr	cookie (Default to true)
	* @attr locale (Default to server locale)
	* @attr status (Default to false)
	* @attr	xfbml (Default to false)
	*/
	def initJS = { attrs, body ->
		if (!attrs.containsKey("cookie")) attrs.cookie = true
		if (!attrs.locale) attrs.locale = Locale.getDefault()
		Map model = [body:body()]
		attrs.each { key, value ->
			model[key] = value	
		}
		out << render(template:"/facebook-sdk/init-js", model:model, plugin:"facebook-sdk")
	}
	
	/**
	* Login link
	*
	* @attr appPermissions Facebook app permissions/scope
	* @attr cancelURL Cancel URL for redirect if login is canceled (if not defined, nothing happens)
	* @attr elementClass HTML element 'class' attribute value
	* @attr elementId HTML element 'id' attribute value
	* @attr returnURL Return URL for redirect after login (if not defined page will be reloaded)
	*/
	def loginLink = { attrs, body ->
		Map model = [body:body()]
		attrs.each { key, value ->
			model[key] = value
		}
		out << render(template:"/facebook-sdk/login-link", model:model, plugin:"facebook-sdk")
	}
	
	/**
	* Logout link
	*
	* @attr elementClass HTML element 'class' attribute value
	* @attr elementId HTML element 'id' attribute value
	* @attr nextURL next URL for redirect after login (if not defined page will be reloaded)
	*/
	def logoutLink = { attrs, body ->
		Map model = [body:body()]
		attrs.each { key, value ->
			model[key] = value
		}
		out << render(template:"/facebook-sdk/logout-link", model:model, plugin:"facebook-sdk")
	}

}
