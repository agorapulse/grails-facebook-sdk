package grails.plugin.facebooksdk

class FacebookJSTagLib {

    static String TYPE_LARGE = 'large'
    static String TYPE_SMALL = 'small'
    static String TYPE_SQUARE = 'square'

    static Map SIZES = [
            (TYPE_LARGE):  [width:  200],
            (TYPE_SMALL):  [width:  50],
            (TYPE_SQUARE): [height: 50, width: 50]
    ]
	
	static namespace = 'facebook'
	
	/**
	* Initialize Facebook JS SDK
	*
	* @attr appId REQUIRED
	* @attr autoGrow (Default to false)
	* @attr channelUrl
	* @attr	cookie (Default to true)
	* @attr locale (Default to server locale)
	* @attr status (Default to false)
	* @attr	xfbml (Default to false)
	* @attr frictionlessRequests (Default to false)
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
     * Add to page link (https://developers.facebook.com/docs/reference/dialogs/add_to_page/)
     *
     * @attr disabled Disable click on the link.
     * @attr display Display mode in which to render the Dialog. Can be page (default), popup, iframe, or touch.
     * @attr elementClass HTML element 'class' attribute value.
     * @attr elementId HTML element 'id' attribute value.
     * @attr returnUrl Return URL for redirect after login (if not defined page will be reloaded)
     */
    def addToPageLink = {attrs, body ->
        Map model = [body:body()]
        attrs.each { key, value ->
            model[key] = value
        }
        out << render(template:"/facebook-sdk/add-to-page-link", model:model, plugin:"facebook-sdk")
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
		out << render(template:"/facebook-sdk/login-link", model:model, plugin:"facebook-sdk")
	}
	
	/**
	* Logout link
	*
	* @attr elementClass HTML element 'class' attribute value
	* @attr elementId HTML element 'id' attribute value
	* @attr nextUrl next URL for redirect after login (if not defined page will be reloaded)
	*/
	def logoutLink = { attrs, body ->
		Map model = [body:body()]
		attrs.each { key, value ->
			model[key] = value
		}
		out << render(template:"/facebook-sdk/logout-link", model:model, plugin:"facebook-sdk")
	}

    /**
     * Picture
     *
     * @attr elementClass HTML element 'class' attribute value.
     * @attr elementId HTML element 'id' attribute value.
     * @attr facebookId REQUIRED
     * @attr linkEnabled Render the image as clickable (link to facebook profile)
     * @attr protocol Define protocol. Can be http to https (default to current protocol)
     * @attr type Define image size. Can be large, small or square (default).
     * @attr height Specify a custom height.
     * @attr width Specify a custom width.
     */
    def picture = { attrs, body ->
        if (!attrs.protocol) attrs.protocol =  (request.secure ? 'https' : 'http')
        attrs.queryString = ''
        if (SIZES[attrs.type]) {
            attrs.height = SIZES[attrs.type].height
            attrs.width = SIZES[attrs.type].width
            if (attrs.type == 'mini') attrs.queryString = "height=$attrs.height&width=$attrs.width"
            else attrs.queryString = "type=${attrs.type}"
        } else if (attrs.height || attrs.width) {
            if (attrs.height) attrs.queryString = "height=${attrs.height}"
            if (attrs.queryString) attrs.queryString += attrs.queryString + '&'
            if (attrs.width) attrs.queryString = "width=${attrs.width}"
        }
        if (attrs.protocol == 'https') {
            if (attrs.queryString) attrs.queryString += attrs.queryString + '&'
            attrs.queryString += attrs.queryString + 'return_ssl_resources=1'
        }
        Map model = [:]
        attrs.each { key, value ->
            model[key] = value
        }
        out << render(template:"/facebook-sdk/picture", model:model, plugin:"facebook-sdk")
    }

    /**
     * Invite link (https://developers.facebook.com/docs/reference/dialogs/requests/)
     *
     * @attr data Additional data you may pass for tracking. The maximum length is 255 characters.
     * @attr disabled Disable click on the link.
     * @attr display Display mode in which to render the Dialog. Can be 'page' (default), 'popup', 'iframe', or 'touch'.
     * @attr elementClass HTML element 'class' attribute value.
     * @attr elementId HTML element 'id' attribute value.
     * @attr excludeIds List of of user IDs that will be excluded from the Dialog.
     * @attr filters List of filters to apply. Can be 'all', 'app_users' and 'app_non_users'.
     * @attr message REQUIRED The Request string the receiving user will see. The maximum length is 60 characters.
     * @attr maxRecipients An integer that specifies the maximum number of friends that can be chosen.
     * @attr title The title for the Dialog. Maximum length is 50 characters.
     * @attr to A user ID or username.
     */
    def inviteLink = {attrs, body ->
        Map model = [body:body()]
        attrs.each { key, value ->
            model[key] = value
        }
        out << render(template:"/facebook-sdk/invite-link", model:model, plugin:"facebook-sdk")
    }

    /**
     * Publish link (https://developers.facebook.com/docs/reference/dialogs/feed/)
     *
     * @attr disabled Disable click on the link.
     * @attr display Display mode in which to render the Dialog. Can be page (default), popup, iframe, or touch.
     * @attr caption The caption of the link (appears beneath the link name). If not specified, this field is automatically populated with the URL of the link.
     * @attr description The description of the link (appears beneath the link caption). If not specified, this field is automatically populated by information scraped from the link, typically the title of the page.
     * @attr elementClass HTML element 'class' attribute value.
     * @attr elementId HTML element 'id' attribute value.
     * @attr link The link attached to this post.
     * @attr name The name of the link attachment.
     * @attr picture The URL of a picture attached to this post. The picture must be at least 50px by 50px and have a maximum aspect ratio of 3:1.
     * @attr source The URL of a media file (either SWF or MP3) attached to this post. If both source and picture are specified, only source is used.
     */
    def publishLink = {attrs, body ->
        Map model = [body:body()]
        attrs.each { key, value ->
            model[key] = value
        }
        out << render(template:"/facebook-sdk/publish-link", model:model, plugin:"facebook-sdk")
    }

    /**
     * Send link (https://developers.facebook.com/docs/reference/dialogs/send/)
     *
     * @attr disabled Disable click on the link.
     * @attr display Display mode in which to render the Dialog. Can be page (default), popup, iframe, or touch.
     * @attr description The description of the link (appears beneath the link caption). If not specified, this field is automatically populated by information scraped from the link, typically the title of the page.
     * @attr elementClass HTML element 'class' attribute value.
     * @attr elementId HTML element 'id' attribute value.
     * @attr link REQUIRED The link attached to this post.
     * @attr name The name of the link attachment.
     * @attr picture The URL of a picture attached to this post. The picture must be at least 50px by 50px and have a maximum aspect ratio of 3:1.
     * @attr to REQUIRED A user ID or username to which to send the message.
     */
    def sendLink = {attrs, body ->
        Map model = [body:body()]
        attrs.each { key, value ->
            model[key] = value
        }
        out << render(template:"/facebook-sdk/send-link", model:model, plugin:"facebook-sdk")
    }

}
