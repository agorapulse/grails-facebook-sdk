package grails.plugins.facebooksdk

class FacebookSdkFilters {
	
	FacebookApp facebookApp
	FacebookAppService facebookAppService
	
	def filters = {
		
		facebook(controller:'*', action:'*') {
			before = {
				// Create facebook data
				request.facebook = [:]
				request.facebook.app = facebookApp
				request.facebook.user = new FacebookUser()
				request.facebook.authenticated = false

				FacebookUser facebookUser = new FacebookUser()
				if (request.facebook.app.id) {
					request.facebook.user.id = facebookAppService.getUserId()
					if (request.facebook.user.id) {
						request.facebook.authenticated = true
					}
				}
				return true
			}

			after = {  Map model ->
				// Check if user has not been invalidated during controllers execution
				if (request.facebook.app.id) {
					request.facebook.user.id = facebookAppService.getUserId()
					if (!request.facebook.user.id) {
						request.facebook.authenticated = false
					}
				}
				return true
			}
		}
	}
} 
