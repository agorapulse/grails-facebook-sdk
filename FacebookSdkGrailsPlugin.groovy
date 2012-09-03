import grails.plugin.facebooksdk.*

class FacebookSdkGrailsPlugin {
	
	def version = "0.3.6"
	def grailsVersion = "2.0 > *"
	def dependsOn = [:]
	def pluginExcludes = []

	def author = "Benoit Hediard"
	def authorEmail = "ben@benorama.com"
	def title = "Facebook SDK Grails Plugin"
	def description = '''The Facebook SDK Plugin allows your Grails application to use the Facebook Platform and develop Facebook apps on Facebook.com or on web sites (with Facebook Connect).
It is a port of the official Facebook PHP SDK V3.1.1 to Grails 2.0.
''' 
	
	def documentation = "http://benorama.github.com/grails-facebook-sdk/guide/"
	def license = "APACHE"
    def organization = [ name: "AgoraPulse", url: "http://www.agorapulse.com/" ]
    def issueManagement = [ system: "github", url: "https://github.com/benorama/grails-facebook-sdk/issues" ]
	def scm = [  url: "https://github.com/benorama/grails-facebook-sdk" ]

    def doWithSpring = {
        facebookApp(FacebookApp)
        facebookAppService(FacebookAppService) {
            facebookApp = ref("facebookApp")
            facebookAppCookieScope = ref("facebookAppCookieScope")
            facebookAppRequestScope = ref("facebookAppRequestScope")
            facebookAppPersistentScope = ref("facebookAppPersistentScope")
        }
        facebookAppCookieScope(FacebookAppCookieScope) {
            facebookApp = ref("facebookApp")
        }
        facebookAppRequestScope(FacebookAppRequestScope) {
            facebookApp = ref("facebookApp")
        }
        facebookAppPersistentScope(FacebookAppSessionScope) {
            facebookApp = ref("facebookApp")
        }
    }

    def doWithApplicationContext = { applicationContext ->
        def facebooksdk = application.config.grails?.plugin?.facebooksdk
        if (facebooksdk) {
            FacebookApp facebookApp = applicationContext.getBean("facebookApp")
            facebookApp.id  = facebooksdk.appId
            facebookApp.secret  = facebooksdk.appSecret
            facebookApp.permissions = facebooksdk.appPermissions
        }
    }

}