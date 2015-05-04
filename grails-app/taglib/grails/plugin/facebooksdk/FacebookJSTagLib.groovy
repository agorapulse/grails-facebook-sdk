package grails.plugin.facebooksdk

import org.springframework.web.servlet.support.RequestContextUtils

class FacebookJSTagLib {

    static final String TYPE_LARGE = 'large'
    static final String TYPE_MINI = 'mini'
    static final String TYPE_SMALL = 'small'
    static final String TYPE_SQUARE = 'square'

    static Map SIZES = [
            (TYPE_LARGE):  [width:  200],
            (TYPE_MINI): [height: 32, width: 32],
            (TYPE_SMALL):  [width:  50],
            (TYPE_SQUARE): [height: 50, width: 50]
    ]

    static namespace = 'facebook'

    def grailsApplication
    def grailsLinkGenerator // Injected by Spring

    /**
     * Initialize Facebook JS SDK
     *
     * @attr appId REQUIRED
     * @attr autoGrow (Default to false)
     * @attr channel (Default to true)
     * @attr customSelector (Default to '$')
     * @attr channelUrl (Default to provided facebook sdk channel)
     * @attr cookie (Default to true)
     * @attr frictionlessRequests (Default to false)
     * @attr locale (Default to server locale)
     * @attr status (Default to false)
     * @attr xfbml (Default to false)
     * @attr version (Default to v2.3 or apiVersion config settings)
     */
    def initJS = { attrs, body ->
        if (!attrs.locale) attrs.locale = RequestContextUtils.getLocale(request)
        if (!FacebookLocalization.isLocaleSupported(attrs.locale)) {
            log.warn "Locale $attrs.locale is not supported by Facebook, default locale en_US will be used"
            attrs.locale = Locale.US
        }
        if (!attrs.containsKey('cookie')) attrs.cookie = true
        if (!attrs.containsKey('channel')) attrs.channel = true
        if (attrs.channel && !attrs.containsKey("channelUrl")) {
            attrs.channelUrl = grailsLinkGenerator.link(
                    absolute: true,
                    action: 'channel',
                    controller: 'facebookSdk',
                    params: [locale: attrs.locale.toString()]
            )
        }
        if (!attrs.containsKey('version')) attrs.version = config.apiVersion ?: 'v2.3'
        Map model = [body:body()]
        attrs.each { key, value ->
            model[key] = value
        }
        if (!model['customSelector']) {
            model['customSelector'] = config.customSelector ?: '$'
        }
        includeScriptOnce('init-js', model)
        out << render(template: '/tags/init-js', model: model, plugin: 'facebook-sdk')
    }

    /**
     * Add to page link (https://developers.facebook.com/docs/reference/dialogs/add_to_page/)
     *
     * @attr callback Optional javascript function name to call when dialog is confirmed or closed.
     * @attr customSelector (Default to '$')
     * @attr disabled Disable click on the link.
     * @attr display Display mode in which to render the Dialog. Can be page (default), popup, iframe, or touch.
     * @attr elementClass HTML element 'class' attribute value.
     * @attr elementId HTML element 'id' attribute value.
     * @attr returnUrl Redirect URL after the page is added
     */
    def addToPageLink = {attrs, body ->
        Map model = [body:body()]
        attrs.each { key, value ->
            model[key] = value
        }
        if (!model['customSelector']) {
            model['customSelector'] = config.customSelector ?: '$'
        }
        includeScriptOnce('add-to-page-link', model)
        out << render(template: '/tags/add-to-page-link', model: model, plugin: 'facebook-sdk')
    }

    /**
     * Login link
     *
     * @attr appPermissions Facebook app permissions/scope
     * @attr callback Optional javascript function name to call when dialog is confirmed or closed.
     * @attr cancelUrl Cancel URL for redirect if login is canceled (if not defined, nothing happens)
     * @attr customSelector (Default to '$')
     * @attr elementClass HTML element 'class' attribute value
     * @attr elementId HTML element 'id' attribute value
     * @attr returnUrl Return URL for redirect after login (if not defined page will be reloaded)
     */
    def loginLink = { attrs, body ->
        Map model = [body:body()]
        attrs.each { key, value ->
            model[key] = value
        }
        if (!model['customSelector']) {
            model['customSelector'] = config.customSelector ?: '$'
        }
        includeScriptOnce('login-link', model)
        out << render(template: '/tags/login-link', model: model, plugin: 'facebook-sdk')
    }

    /**
     * Logout link
     *
     * @attr customSelector (Default to '$')
     * @attr elementClass HTML element 'class' attribute value
     * @attr elementId HTML element 'id' attribute value
     * @attr nextUrl next URL for redirect after login (if not defined page will be reloaded)
     */
    def logoutLink = { attrs, body ->
        Map model = [body:body()]
        attrs.each { key, value ->
            model[key] = value
        }
        if (!model['customSelector']) {
            model['customSelector'] = config.customSelector ?: '$'
        }
        includeScriptOnce('logout-link', model)
        out << render(template: '/tags/logout-link', model: model, plugin: 'facebook-sdk')
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
            if (attrs.queryString) attrs.queryString += '&'
            if (attrs.width) attrs.queryString = "width=${attrs.width}"
        }
        if (attrs.protocol == 'https') {
            if (attrs.queryString) attrs.queryString += '&'
            attrs.queryString += 'return_ssl_resources=1'
        }
        Map model = [:]
        attrs.each { key, value ->
            model[key] = value
        }
        includeScriptOnce('picture', model)
        out << render(template: '/tags/picture', model: model, plugin: 'facebook-sdk')
    }

    /**
     * Invite link (https://developers.facebook.com/docs/reference/dialogs/requests/)
     *
     * @attr callback Optional javascript function name to call when dialog is confirmed or closed.
     * @attr customSelector (Default to '$')
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
        if (!model['customSelector']) {
            model['customSelector'] = config.customSelector ?: '$'
        }
        includeScriptOnce('invite-link', model)
        out << render(template: '/tags/invite-link', model: model, plugin: 'facebook-sdk')
    }

    /**
     * Publish link (https://developers.facebook.com/docs/reference/dialogs/feed/)
     *
     * @attr callback Optional javascript function name to call when dialog is confirmed or closed.
     * @attr customSelector (Default to '$')
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
        if (!model['customSelector']) {
            model['customSelector'] = config.customSelector ?: '$'
        }
        includeScriptOnce('publish-link', model)
        out << render(template: '/tags/publish-link', model: model, plugin: 'facebook-sdk')
    }

    /**
     * Send link (https://developers.facebook.com/docs/reference/dialogs/send/)
     *
     * @attr callback Optional javascript function name to call when dialog is confirmed or closed.
     * @attr disabled Disable click on the link.
     * @attr display Display mode in which to render the Dialog. Can be page (default), popup, iframe, or touch.
     * @attr description The description of the link (appears beneath the link caption). If not specified, this field is automatically populated by information scraped from the link, typically the title of the page.
     * @attr elementClass HTML element 'class' attribute value.
     * @attr elementId HTML element 'id' attribute value.
     * @attr link REQUIRED The link attached to this post.
     * @attr name The name of the link attachment.
     * @attr picture The URL of a picture attached to this post. The picture must be at least 50px by 50px and have a maximum aspect ratio of 3:1.
     * @attr customSelector (Default to '$')
     * @attr to REQUIRED A user ID or username to which to send the message.
     */
    def sendLink = {attrs, body ->
        Map model = [body:body()]
        attrs.each { key, value ->
            model[key] = value
        }
        if (!model['customSelector']) {
            model['customSelector'] = config.customSelector ?: '$'
        }
        includeScriptOnce('send-link', model)
        out << render(template: '/tags/send-link', model: model, plugin: 'facebook-sdk')
    }

    /**
     * Share link (https://developers.facebook.com/docs/reference/dialogs/share/)
     *
     * @attr callback Optional javascript function name to call when dialog is confirmed or closed.
     * @attr disabled Disable click on the link.
     * @attr display Display mode in which to render the Dialog. Can be page (default), popup, iframe, or touch.
     * @attr elementClass HTML element 'class' attribute value.
     * @attr elementId HTML element 'id' attribute value.
     * @attr href REQUIRED The link attached to this post.
     * @attr customSelector (Default to '$')
     */
    def shareLink = {attrs, body ->
        Map model = [body:body()]
        attrs.each { key, value ->
            model[key] = value
        }
        if (!model['customSelector']) {
            model['customSelector'] = config.customSelector ?: '$'
        }
        includeScriptOnce('share-link', model)
        out << render(template: '/tags/share-link', model: model, plugin: 'facebook-sdk')
    }


    // PRIVATE

    private def getConfig() {
        grailsApplication.config.grails.plugin.facebooksdk
    }

    private void includeScriptOnce(String tagName, Map model) {
        if (!request.facebookSdkIncludedScripts) {
            request.facebookSdkIncludedScripts = [:]
        }
        if (!request.facebookSdkIncludedScripts[tagName]) {
            request.facebookSdkIncludedScripts[tagName] = true
            model.includeScript = true
        }
    }

}
