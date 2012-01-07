package grails.plugins.facebooksdk

import com.restfb.exception.FacebookOAuthException

class AppController {
	
	def facebookAppService

	def beforeInterceptor =  {
		request.appId = facebookAppService.appId
		request.appPermissions = facebookAppService.appPermissions
		if (request.appId) {
			request.userId = facebookAppService.userId
		}
	}

	def index = {
		// See if there is a user from a cookie or session
		FacebookGraphClient facebookGraphClient = new FacebookGraphClient()
		def user
		List userFriends = []
		if (request.userId) {
			try {
				String userAccessToken = facebookAppService.getUserAccessToken()
				facebookGraphClient = new FacebookGraphClient(userAccessToken)
				user = facebookGraphClient.fetchObject(request.userId.toString())
				userFriends = facebookGraphClient.fetchConnection("${request.userId}/friends", [limit:10])
			} catch (FacebookOAuthException exception) {
				// Usually an invalid session (OAuthInvalidTokenException), for example if the user logged out from facebook.com
				facebookGraphClient = new FacebookGraphClient()
			}
		}
		
		// This call will always work since we are fetching public data.
		def benorama = facebookGraphClient.fetchObject("benorama")
		return 	[benorama:benorama,
				user:user,
				userFriends:userFriends]
	}

}
