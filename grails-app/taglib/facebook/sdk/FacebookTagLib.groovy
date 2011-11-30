package facebook.sdk

class FacebookTagLib {
	
	static namespace = 'facebook'
	
	/**
	* Initialize Facebook JS SDK
	*
	* @attr appId REQUIRED
	* @attr autoGrowthEnabled
	* @attr channelUrl
	* @attr	cookieEnabled
	* @attr localeCode
	* @attr oauthEnabled
	* @attr sizeEnabled
	* @attr statusEnabled
	* @attr	xfbmlEnabled
	*/
	def connectJS = { attrs, body ->
		// Default value
		if (!attrs.appId) attrs.appId = 0
		if (!attrs.autoGrowthEnabled) attrs.autoGrowthEnabled = false
		if (!attrs.channelUrl) attrs.channelUrl = ''
		if (!attrs.cookieEnabled) attrs.cookieEnabled = true
		if (!attrs.localeCode) attrs.localeCode = 'en_US'
		if (!attrs.oauthEnabled) attrs.oauthEnabled = true
		if (!attrs.sizeEnabled) attrs.sizeEnabled = true
		if (!attrs.statusEnabled) attrs.statusEnabled = false
		if (!attrs.xfbmlEnabled) attrs.xfbmlEnabled = true
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
		// Default value
		if (!attrs.returnUrl) attrs.returnUrl = ''
		if (!attrs.cancelUrl) attrs.cancelUrl = attrs.returnUrl
		if (!attrs.appPermissions) attrs.appPermissions = ''
		if (!attrs.elementClass) attrs.elementClass = ''
		if (!attrs.elementId) attrs.elementId = ''
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
		// Default value
		if (!attrs.returnUrl) attrs.returnUrl = ''
		if (!attrs.elementClass) attrs.elementClass = ''
		if (!attrs.elementId) attrs.elementId = ''
		Map model = [body:body()]
		attrs.each { key, value ->
			model[key] = value
		}
		out << render(template:"/facebook/sdk/logout-link", model:model, plugin:"facebook-sdk")
	}

}
