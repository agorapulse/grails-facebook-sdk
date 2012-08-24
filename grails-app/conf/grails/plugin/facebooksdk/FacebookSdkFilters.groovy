package grails.plugin.facebooksdk

class FacebookSdkFilters {

	FacebookAppService facebookAppService
	
	def filters = {
		
		facebook(controller:'*', action:'*') {
			before = {
				log.debug "Executing..."
				// Create facebook app
                FacebookApp facebookApp = new FacebookApp()
                //if (request.params?.appFacebookId) {
                    // TODO
                //} else if (grailsApplication.config.grails?.plugin?.facebooksdk) {
                    facebookApp.id = grailsApplication.config.grails.plugin.facebooksdk.appId
                    facebookApp.secret = grailsApplication.config.grails.plugin.facebooksdk.appSecret
                    facebookApp.permissions = grailsApplication.config.grails.plugin.facebooksdk.appPermissions ?: ''
                //}

				// Create facebook data
                request.facebook = [
                        authenticated: false,
                        app: facebookApp,
                        user: [id: 0]
                ]

				if (request.facebook.app.id) {
					request.facebook.user.id = facebookAppService.userId
					if (request.facebook.user.id) {
						request.facebook.authenticated = true
					}
				}
                return true
			}

			after = { Map model ->
				// Check if user has not been invalidated during controllers execution
				if (request.facebook.app.id) {
					request.facebook.user.id = facebookAppService.userId
					if (!request.facebook.user.id) {
						request.facebook.authenticated = false
					}
				}
				return true
			}
		}
	}
} 
