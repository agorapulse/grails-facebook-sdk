package grails.plugin.facebooksdk

import org.springframework.web.servlet.support.RequestContextUtils

class FacebookJSTagLib {

    static String TYPE_LARGE = 'large'
    static String TYPE_SMALL = 'small'
    static String TYPE_SQUARE = 'square'
    static String TYPE_MINI = 'mini'

    static Map SIZES = [
            (TYPE_LARGE):  [width:  200],
            (TYPE_SMALL):  [width:  50],
            (TYPE_SQUARE): [height: 50, width: 50],
            (TYPE_MINI):   [height: 32, width: 32],
            'undefined':  [:]
    ]

    static String INVITE_DISPLAY_POPUP = "popup"
    static String INVITE_DISPLAY_DIALOG = "dialog"
    static String INVITE_DISPLAY_IFRAME = "iframe"
    static String INVITE_DISPLAY_ASYNC = "async"
    static String INVITE_DISPLAY_HIDDEN = "hidden"
    static String INVITE_DISPLAY_NONE = "none"
	
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
	*/
	def initJS = { attrs, body ->
		if (!attrs.containsKey("cookie")) attrs.cookie = true
		if (!attrs.locale) attrs.locale = RequestContextUtils.getLocale(request)
		if (!attrs.locale) Locale.getDefault()
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

    def picture = { attrs, body ->
        attrs.protocol = attrs.protocol ?: (request.secure ? 'https' : 'http')
        attrs.target = attrs.target ?: '_target'
        attrs.span = attrs.span ?: 'span1'
        attrs.type = attrs.type ?: 'undefined'

        String link = "http://www.facebook.com/profile.php?id=${attrs.facebookId}"
        String imageLink = "${attrs.protocol}://graph.facebook.com/${attrs.facebookId}/picture"
        if (attrs.type) {
            imageLink += "?type=${attrs.type}"
        } else if (attrs.sizes) {
            imageLink += "?width=${attrs.sizes.width}&height=${attrs.sizes.height}"
        }

        out << "<div class='${attrs.span} facebook user picture'>"
        out << (attrs.linkEnabled ? "<a href='${link}' target='${attrs.target}' title='${attrs.tooltip}'>" : '')
        out << "<img src='${imageLink}' ${toAttributes(SIZES[attrs.type] as Map)}></img>"
        out << (attrs.linkEnabled ? '</a>' : '')
        out << '</div>'
    }

    def comments = {attrs, body ->
        attrs.width = attrs.width ?: 810
        attrs.numPosts = attrs.numPosts ?: 5
        attrs.href = attrs.href ?: ''

        out << """<fb:comments href='${attrs.href}'
                               migrated='true' num_posts='${attrs.numPosts}' width='${attrs.width}'>
                  </fb:comments>"""
    }

    def invite = {attrs, body ->
        attrs.htmlClass = attrs.htmlClass ?: ''
        attrs.disabled = attrs.disabled ?: false
        attrs.filters = attrs.filters ?: 'all'
        attrs.label = attrs.label ?: 'Invite'
        attrs.recipientMaxCount = attrs.recipientMaxCount ?: 0
        attrs.toolType = attrs.toolType ?: 'Invite'

        out << '<script type="text/javascript" charset="utf-8"> var onInviteButtonClick = function () {'
        if (!attrs.disabled) {
            out << "FB.ui({method: 'apprequests', message: '${attrs.message.encodeAsJavaScript()}'"
            if (attrs.data)              out << ", data:'${attrs.data}'"
            if (attrs.display)           out << ", display: '${attrs.display}'"
            if (attrs.excludeIds)        out << ", exclude_ids: '${attrs.excludeIds}'"
            if (attrs.title)             out << ", title: '${attrs.title.encodeAsJavaScript()}'"
            if (attrs.to)                out << ", to: '${attrs.to}'"
            if (attrs.recipientMaxCount) out << ", max_recipients: '${attrs.recipientMaxCount}'"
            out << '});'
        }
        out << "return false; } </script>"
        out << """<a class='${attrs.htmlClass}' onclick='onInviteButtonClick()'
                     style='${attrs.style}' title='${attrs.toolTip}'><span>${attrs.label}</span></a>"""
    }

    def publish = {attrs, body ->
        attrs.htmlClass = attrs.htmlClass ?: ''
        attrs.disabled = attrs.disabled ?: false
        attrs.label = attrs.label ?: 'Publish'
        attrs.toolType = attrs.toolType ?: 'Publish'

        out << '<script type="text/javascript" charset="utf-8"> var onShareButtonClick = function () {'
        if (!attrs.disabled) {
            out << "FB.ui({method: 'feed', message: '${attrs.message.encodeAsJavaScript()}'"
            if (attrs.caption)     out << ", caption: '${attrs.caption.encodeAsJavaScript()}'"
            if (attrs.description) out << ", description:'${attrs.description.encodeAsJavaScript()}'"
            if (attrs.display)     out << ", display: '${attrs.display}'"
            if (attrs.link)        out << ", link: '${attrs.link}'"
            if (attrs.name)        out << ", name: '${attrs.name.encodeAsJavaScript()}'"
            if (attrs.picture)     out << ", picture: '${attrs.picture}'"
            if (attrs.source)      out << ", source: '${attrs.source}'"
            out << '});'
        }
        out << "return false; } </script>"
        out << """<a class='${attrs.htmlClass}' onclick='onShareButtonClick()'
                     style='${attrs.style}' title='${attrs.toolTip}'><span>${attrs.label}</span></a>"""
    }

    private String toAttributes(Map m) {
        String result = ''
        m.each { key, value ->
            result += " ${key}='${value}'"
        }
        result
    }
}
