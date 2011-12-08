package facebook.sdk

class FacebookTagLib {
	
	static namespace = 'facebook'
	
	/**
	* Initialize Facebook JS SDK
	*
	* @attr appId REQUIRED
	* @attr autoGrowthEnabled
	* @attr channelUrl
	* @attr	cookieEnabled Default to true
	* @attr localeCode
	* @attr sizeEnabled
	* @attr statusEnabled
	* @attr	xfbmlEnabled
	*/
	def connectJS = { attrs, body ->
		if (!attrs.containsKey("cookieEnabled")) attrs.cookieEnabled = true
		if (!attrs.localeCode) attrs.localeCode = 'en_US'
		Map model = [body:body()]
		attrs.each { key, value ->
			model[key] = value	
		}
		out << render(template:"/facebook/sdk/connect-js", model:model, plugin:"facebook-sdk")
	}
	
	/**
	* Login link
	*
	* @attr appPermissions Facebook app permissions/scope
	* @attr cancelUrl Cancel URL for redirect if login is canceled (if not defined, nothing happens)
	* @attr elementClass HTML element 'class' attribute value
	* @attr elementId HTML element 'id' attribute value
	* @attr returnUrl Return URL for redirect after login (if not defined page will be reloaded)
	*/
	def loginLink = { attrs, body ->
		Map model = [body:body()]
		attrs.each { key, value ->
			model[key] = value
		}
		out << render(template:"/facebook/sdk/login-link", model:model, plugin:"facebook-sdk")
	}
	
	/**
	* Logout link
	*
	* @attr elementClass HTML element 'class' attribute value
	* @attr elementId HTML element 'id' attribute value
	* @attr returnUrl Return URL for redirect after login (if not defined page will be reloaded)
	*/
	def logoutLink = { attrs, body ->
		Map model = [body:body()]
		attrs.each { key, value ->
			model[key] = value
		}
		out << render(template:"/facebook/sdk/logout-link", model:model, plugin:"facebook-sdk")
	}

}
