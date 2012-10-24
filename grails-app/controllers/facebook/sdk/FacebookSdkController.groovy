package facebook.sdk

class FacebookSdkController {

    def channel() {
        if (!params.locale) params.locale = Locale.getDefault().toString()
        response.setHeader('Pragma', 'public')
        response.setHeader('Cache-Control', "maxage=${60 * 60 * 24 * 365}")
        response.setDateHeader('Expires', (new Date() + 365).time)
        render "<script src='//connect.facebook.net/${params.locale}/all.js''>"
    }

}
