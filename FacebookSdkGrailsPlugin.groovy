class FacebookSdkGrailsPlugin {
	
	def version = "0.2.0"
	def grailsVersion = "2.0 > *"
	def dependsOn = [:]
	//def loadAfter                = ['services', 'controllers']
	//def observe                  = ['services', 'controllers']
	//def watchedResources         = ["grails-app/services/**/*Service.groovy", "grails-app/controllers/**/*Controller.groovy"]
	def pluginExcludes = []

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

	def doWithDynamicMethods = { ctx ->
	}

	def doWithApplicationContext = { applicationContext ->
		if (application.config.grails.plugins.facebooksdk) {
			def facebookApp = applicationContext.getBean("facebookApp")
			facebookApp.id  = application.config.grails.plugins.facebooksdk.appId
			facebookApp.secret  = application.config.grails.plugins.facebooksdk.appSecret
			facebookApp.permissions = application.config.grails.plugins.facebooksdk.appPermissions
		} 
	}

	def onChange = { event ->
	}

	def onConfigChange = { event ->
		if (application.config.grails.plugins.facebooksdk) {
			def facebookApp = applicationContext.getBean("facebookApp")
			facebookApp.id  = application.config.grails.plugins.facebooksdk.appId
			facebookApp.secret  = application.config.grails.plugins.facebooksdk.appSecret
			facebookApp.permissions = application.config.grails.plugins.facebooksdk.appPermissions
		}
	}

	def onShutdown = { event ->
	}

}