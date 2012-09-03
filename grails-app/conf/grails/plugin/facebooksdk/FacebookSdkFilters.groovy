package grails.plugin.facebooksdk

class FacebookSdkFilters {
	
	FacebookApp facebookApp
	FacebookAppService facebookAppService
	
	def filters = {
		
		facebook(controller:'*', action:'*') {
			before = {
				log.debug "Facebook SDK filter running..."

				if (facebookApp.id) {
                    request.facebook = [
                            app: facebookApp,
                            authenticated: facebookAppService.userId ? true : false,
                            signedRequest: facebookAppService.signedRequest ?: new FacebookSignedRequest(),
                            user: [id: facebookAppService.userId]
                    ]
                } else {
                    request.facebook = [
                            app: new FacebookApp(),
                            authenticated: false,
                            user: [id: 0]
                    ]
                }
				return true
			}

			after = {  Map model ->
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
