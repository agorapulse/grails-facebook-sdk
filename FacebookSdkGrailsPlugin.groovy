import grails.plugins.facebooksdk.FacebookAppService
import grails.plugins.facebooksdk.FacebookAppCookieScope
import grails.plugins.facebooksdk.FacebookAppRequestScope
import grails.plugins.facebooksdk.FacebookAppSessionScope

class FacebookSdkGrailsPlugin {
	
	def version = "0.1.2"
	def grailsVersion = "2.0 > *"
	def dependsOn = [:]
	//def loadAfter                = ['services', 'controllers']
	//def observe                  = ['services', 'controllers']
	//def watchedResources         = ["grails-app/services/**/*Service.groovy", "grails-app/controllers/**/*Controller.groovy"]
	def pluginExcludes = [
		"grails-app/controllers/**",
		"grails-app/views/app/*",
		"grails-app/views/error.gsp",
		"grails-app/views/layouts/*",
		"grails-app/views/website/*",
		"web-app/**",
	]

	def author = "Benoit Hediard"
	def authorEmail = "ben@benorama.com"
	def title = "Facebook SDK Grails Plugin"
	def description = '''The Facebook SDK Plugin allows your Grails application to use the Facebook Platform and develop Facebook apps on Facebook.com or on web sites (with Facebook Connect).
It is a port of the official Facebook PHP SDK V3.1.1 to Grails 2.0.
''' 
	
	def documentation = "http://grails.org/plugin/facebook-sdk-grails-plugin/blob/master/README.md"
	def license = "APACHE"
	def issueManagement = [ system: "github", url: "https://github.com/benorama/facebook-sdk-grails-plugin/issues" ]
	def scm = [  url: "https://github.com/benorama/facebook-sdk-grails-plugin" ]

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
		if (application.config.grails.plugins.facebooksdk) {
			def facebookAppService = applicationContext.getBean("facebookAppService")
			facebookAppService.appId  = application.config.grails.plugins.facebooksdk.appId
			facebookAppService.appSecret  = application.config.grails.plugins.facebooksdk.appSecret
			facebookAppService.appPermissions = application.config.grails.plugins.facebooksdk.appPermissions
			def facebookAppCookieScope = applicationContext.getBean("facebookAppCookieScope")
			facebookAppCookieScope.appId = application.config.grails.plugins.facebooksdk.appId
			def facebookAppRequestScope = applicationContext.getBean("facebookAppRequestScope")
			facebookAppRequestScope.appId = application.config.grails.plugins.facebooksdk.appId
			def facebookAppPersistentScope = applicationContext.getBean("facebookAppPersistentScope")
			facebookAppPersistentScope.appId = application.config.grails.plugins.facebooksdk.appId
		} 
	}

	def onChange = { event ->
	}

	def onConfigChange = { event ->
		if (application.config.grails.plugins.facebooksdk) {
			def facebookAppService = applicationContext.getBean("facebookAppService")
			facebookAppService.appId  = application.config.grails.plugins.facebooksdk.appId
			facebookAppService.appSecret  = application.config.grails.plugins.facebooksdk.appSecret
			facebookAppService.appPermissions = application.config.grails.plugins.facebooksdk.appPermissions
			def facebookAppCookieScope = applicationContext.getBean("facebookAppCookieScope")
			facebookAppCookieScope.appId = application.config.grails.plugins.facebooksdk.appId
			def facebookAppRequestScope = applicationContext.getBean("facebookAppRequestScope")
			facebookAppRequestScope.appId = application.config.grails.plugins.facebooksdk.appId
			def facebookAppPersistentScope = applicationContext.getBean("facebookAppPersistentScope")
			facebookAppPersistentScope.appId = application.config.grails.plugins.facebooksdk.appId
		}
	}

	def onShutdown = { event ->
	}

}