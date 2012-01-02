import grails.plugins.facebooksdk.FacebookAppService
import grails.plugins.facebooksdk.scope.FacebookAppCookieScope
import grails.plugins.facebooksdk.scope.FacebookAppRequestScope
import grails.plugins.facebooksdk.scope.FacebookAppSessionScope

class FacebookSdkGrailsPlugin {
	
	def version = "0.1"
	def grailsVersion = "2.0 > *"
	def dependsOn = [:]
	//def loadAfter                = ['services', 'controllers']
	//def observe                  = ['services', 'controllers']
	//def watchedResources         = ["grails-app/services/**/*Service.groovy", "grails-app/controllers/**/*Controller.groovy"]
	def pluginExcludes = [
		"grails-app/controllers/**",
		"grails-app/i18n/**",
		"grails-app/views/app/*",
		"grails-app/views/error.gsp",
		"grails-app/views/layouts/*",
		"grails-app/views/website/*",
		"web-app/**",
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
	def scm                      = [  url: "https://github.com/affinitiz/facebook-grails-sdk" ]
	//def issueManagement          = [  system: "JIRA", url: "http://jira.grails.org/browse/FBSDK" ]

	def doWithWebDescriptor = { xml ->
	}

	def doWithSpring = {
		facebookAppService(FacebookAppService) {
			facebookAppCookieScope = ref("facebookAppCookieScope")
			facebookAppRequestScope = ref("facebookAppRequestScope")
			facebookAppPersistentScope = ref("facebookAppPersistentScope")
		}
		facebookAppCookieScope(FacebookAppCookieScope)
		facebookAppRequestScope(FacebookAppRequestScope)
		facebookAppPersistentScope(FacebookAppSessionScope)
	}

	def doWithDynamicMethods = { ctx ->
	}

	def doWithApplicationContext = { applicationContext ->
		def facebookAppService = applicationContext.getBean("facebookAppService")
		facebookAppService.appId  = application.config.facebook?.sdk?.app?.id
		facebookAppService.appSecret  = application.config.facebook?.sdk?.app?.secret
		facebookAppService.appPermissions = application.config.facebook?.sdk?.app?.permissions

		def facebookAppCookieScope = applicationContext.getBean("facebookAppCookieScope")
		facebookAppCookieScope.appId = application.config.facebook?.sdk?.app?.id

		def facebookAppRequestScope = applicationContext.getBean("facebookAppRequestScope")
		facebookAppRequestScope.appId = application.config.facebook?.sdk?.app?.id

		def facebookAppPersistentScope = applicationContext.getBean("facebookAppPersistentScope")
		facebookAppPersistentScope.appId = application.config.facebook?.sdk?.app?.id
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