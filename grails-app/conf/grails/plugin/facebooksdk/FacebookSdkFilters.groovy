package grails.plugin.facebooksdk

class FacebookSdkFilters {

    //FacebookApp facebookApp
    //FacebookAppService facebookAppService
    //FacebookUser facebookUser
	
	def filters = {
		
		facebook(controller: '*', action: '*') {
			before = {
				log.debug "Facebook SDK filter running..."

                /*FacebookContext facebookContext
                if (facebookApp.id) {
                    facebookContext = new FacebookContext(
                            app: facebookApp,
                            authenticated: facebookUser.id ? true : false,
                            signedRequest: facebookAppService.signedRequest ?: new FacebookSignedRequest(),
                            user: facebookUser
                    )
                } else {
                    facebookContext = new FacebookContext()
                }

                request.facebook = facebookContext*/
				return true
			}

			after = {  Map model ->
				return true
			}
		}
	}
} 
