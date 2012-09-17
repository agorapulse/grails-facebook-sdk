package grails.plugin.facebooksdk

class FacebookSdkFilters {
	
	FacebookApp facebookApp
	FacebookAppService facebookAppService
	
	def filters = {
		
		facebook(controller:'*', action:'*') {
			before = {
				log.debug "Facebook SDK filter running..."

                FacebookContext facebookContext
                if (facebookApp.id) {
                    facebookContext = new FacebookContext(
                            app: facebookApp,
                            authenticated: facebookAppService.userId ? true : false,
                            signedRequest: facebookAppService.signedRequest ?: new FacebookSignedRequest(),
                            user: new FacebookUser(
                                    id: facebookAppService.userId,
                                    facebookAppService: facebookAppService // Inject service to be able to get token
                            )
                    )
                } else {
                    facebookContext = new FacebookContext()
                }

                request.facebook = facebookContext
				return true
			}

			after = {  Map model ->
				return true
			}
		}
	}
} 
