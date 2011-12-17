import facebook.sdk.FacebookAppService
import facebook.sdk.scope.FacebookAppCookieScope
import facebook.sdk.scope.FacebookAppRequestScope
import facebook.sdk.scope.FacebookAppSessionScope

class FacebookSdkGrailsPlugin {
	
	def version = "0.1"
	def grailsVersion = "2.0 > *"
	def dependsOn = [:]
	//def loadAfter                = ['services', 'controllers']
	//def observe                  = ['services', 'controllers']
	//def watchedResources         = ["grails-app/services/**/*Service.groovy", "grails-app/controllers/**/*Controller.groovy"]
	def pluginExcludes = [
		"grails-app/views/error.gsp"
	]

	def title = "Facebook SDK Plugin" // Headline display name of the plugin
	def description = '''\
The Facebook SDK plugin allows your Grails application to use the Facebook Platform and develop Facebook apps\
 on Facebook.com or on web sites (with Facebook Connect).
It uses RestFB java library under the cover : http://restfb.com/.
''' 
	def author = "Benoit Hediard"
	def authorEmail = "hediard@affinitiz.com"
	
	def documentation = "http://grails.org/plugin/facebook-sdk"

	def license                  = "APACHE"
	def organization             = [  name: "Affinitiz", url: "http://github.com/affinitiz" ]
	def developers               = [[ name: "Benoit HEDIARD", email: "hediard@affinitiz.com" ]]
	def scm                      = [  url: "https://github.com/affinitiz/grails-facebook-sdk" ]
	//def issueManagement          = [  system: "JIRA", url: "http://jira.grails.org/browse/FBSDK" ]

	def doWithWebDescriptor = { xml ->
	}

	def doWithSpring = {
		/*facebookAppService(FacebookAppService) {
			appId  = application.config.facebook?.sdk?.app?.id
			appSecret  = application.config.facebook?.sdk?.app?.secret
			appPermissions = application.config.facebook?.sdk?.app?.permissions
			facebookAppCookieScope = ref("facebookAppCookieScope")
			facebookAppRequestScope = ref("facebookAppRequestScope")
			facebookAppPersistentScope = ref("facebookAppPersistentScope")
		}*/

		facebookAppCookieScope(FacebookAppCookieScope) {
			appId = application.config.facebook?.sdk?.app?.id
		}
		facebookAppRequestScope(FacebookAppRequestScope) {
			appId = application.config.facebook?.sdk?.app?.id
		}
		facebookAppPersistentScope(FacebookAppSessionScope) {
			appId = application.config.facebook?.sdk?.app?.id
		}
	}

	def doWithDynamicMethods = { ctx ->
	}

	def doWithApplicationContext = { applicationContext ->
	}

	def onChange = { event ->
	}

	def onConfigChange = { event ->
		def facebookAppCookieScope = event.ctx.getBean("facebookAppCookieScope")
		facebookAppCookieScope.appId = application.config.facebook?.sdk?.app?.id

		def facebookAppRequestScope = event.ctx.getBean("facebookAppRequestScope")
		facebookAppRequestScope.appId = application.config.facebook?.sdk?.app?.id

		def facebookAppSessionScope = event.ctx.getBean("facebookAppSessionScope")
		facebookAppSessionScope.appId = application.config.facebook?.sdk?.app?.id
	}

	def onShutdown = { event ->
	}

}