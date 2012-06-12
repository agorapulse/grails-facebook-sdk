package grails.plugin.facebooksdk

class FacebookSdkFilters {
	
	FacebookApp facebookApp
	FacebookAppService facebookAppService
	
	def filters = {
		
		facebook(controller:'*', action:'*') {
			before = {
				log.debug "Facebook SDK filter running..."
				// Create facebook data
				request.facebook = [:]
				request.facebook.app = facebookApp
				request.facebook.user = [id:0]
				request.facebook.authenticated = false

				if (request.facebook.app.id) {
					request.facebook.user.id = facebookAppService.userId
					if (request.facebook.user.id) {
						request.facebook.authenticated = true
					}
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
