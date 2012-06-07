import grails.plugins.facebooksdk.FacebookApp

class FacebookSdkGrailsPlugin {
	
	def version = "0.2.0"
	def grailsVersion = "2.0 > *"
	def dependsOn = [:]
	def pluginExcludes = []

	def author = "Benoit Hediard"
	def authorEmail = "ben@benorama.com"
	def title = "Facebook SDK Grails Plugin"
	def description = '''The Facebook SDK Plugin allows your Grails application to use the Facebook Platform and develop Facebook apps on Facebook.com or on web sites (with Facebook Connect).
It is a port of the official Facebook PHP SDK V3.1.1 to Grails 2.0.
''' 
	
	def documentation = "http://grails.org/plugin/facebook-sdk-grails-plugin/blob/master/README.md"
	def license = "APACHE"
    def organization = [ name: "AgoraPulse", url: "http://www.agorapulse.com/" ]
    def issueManagement = [ system: "github", url: "https://github.com/benorama/facebook-sdk-grails-plugin/issues" ]
	def scm = [  url: "https://github.com/benorama/facebook-sdk-grails-plugin" ]

    def doWithSpring = {
        facebookApp(grails.plugins.facebooksdk.FacebookApp)
        facebookAppService(grails.plugins.facebooksdk.FacebookAppService) {
            facebookApp = ref("facebookApp")
            facebookAppCookieScope = ref("facebookAppCookieScope")
            facebookAppRequestScope = ref("facebookAppRequestScope")
            facebookAppPersistentScope = ref("facebookAppPersistentScope")
        }
        facebookAppCookieScope(grails.plugins.facebooksdk.FacebookAppCookieScope) {
            facebookApp = ref("facebookApp")
        }
        facebookAppRequestScope(grails.plugins.facebooksdk.FacebookAppRequestScope) {
            facebookApp = ref("facebookApp")
        }
        facebookAppPersistentScope(grails.plugins.facebooksdk.FacebookAppSessionScope) {
            facebookApp = ref("facebookApp")
        }
    }

    def doWithApplicationContext = { applicationContext ->
        def facebooksdk = application.config.grails?.plugins?.facebooksdk
        if (facebooksdk) {
            FacebookApp facebookApp = applicationContext.getBean("facebookApp")
            facebookApp.id  = facebooksdk.appId
            facebookApp.secret  = facebooksdk.appSecret
            facebookApp.permissions = facebooksdk.appPermissions
        }
    }

}