package grails.plugins.facebooksdk

import com.restfb.Connection
import com.restfb.DefaultFacebookClient
import com.restfb.Parameter
import com.restfb.types.User

class AppController {
	
	def facebookAppService

	def beforeInterceptor =  {
		request.appId = facebookAppService.appId
		request.appPermissions = facebookAppService.appPermissions
		request.userId = facebookAppService.userId
	}

	def index() {
		// See if there is a user from a cookie or session
		DefaultFacebookClient facebookClient = new DefaultFacebookClient()
		User user
		List userFriends = []
		if (request.userId) {
			try {
				String userAccessToken = facebookAppService.getUserAccessToken()
				facebookClient = new DefaultFacebookClient(userAccessToken)
				user = facebookClient.fetchObject(request.userId.toString(), User)
				Connection userFriendsConnection = facebookClient.fetchConnection("${request.userId}/friends", User, Parameter.with("limit", 10))
				userFriends = userFriendsConnection ? userFriendsConnection.data : []
			} catch (Exception exception) {
				// Usually an invalid session (OAuthInvalidTokenException), for example if the user logged out from facebook.com
				facebookClient = new DefaultFacebookClient();
			}
		}
		
		// This call will always work since we are fetching public data.
		User naitik = facebookClient.fetchObject("naitik", User)
		return 	[naitik:naitik,
				user:user,
				userFriends:userFriends]
	}

}
