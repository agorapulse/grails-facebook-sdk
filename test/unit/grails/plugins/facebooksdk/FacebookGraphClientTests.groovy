package grails.plugins.facebooksdk

import grails.plugins.facebooksdk.types.FacebookUser

import grails.test.mixin.*
import org.junit.*

class FacebookGraphClientTests {

	// Replace with your own user id and access token before executing tests
	def USER_ID = "594317994"
	def USER_ID2 = "5526183"
	def USER_ACCESS_TOKEN = "AAACEdEose0cBAA9hE0tLoYf06kEmN9ycKHWOb4uPLjuFGi69DGIOc1fiozaKmAIksRn1HpCh0dhCaq9WnWX7qxUqeBM7KioJQYvShAZDZD"

	void testDeleteObject() {
		// TODO
	}

	void testFetchConnection() {
		def client = new FacebookGraphClient(USER_ACCESS_TOKEN)

		def friendsConnectionObjects = client.fetchConnection(USER_ID, "friends", [limit:10])
		assert friendsConnectionObjects.size() == 10, "Friends connection should return 10 friends"
	}

	void testFetchTypedConnection() {
		def client = new FacebookGraphClient(USER_ACCESS_TOKEN)

		def friendsConnectionUsers = client.fetchTypedConnection(USER_ID, "friends", FacebookUser, [limit:10])
		assert friendsConnectionUsers.size() == 10, "Friends connection should return 10 friends"
		assert friendsConnectionUsers[0].class in com.restfb.types.User, "Json user friend class should be com.restfb.types.User instead of ${friendsConnectionUsers[0].class}"
	}

	void testFetchObject() {
		def client = new FacebookGraphClient()

		def userObject = client.fetchObject(USER_ID)
		assert userObject.id == USER_ID, "Json user id doesn't match"
	}

	void testFetchTypedObject() {
		def client = new FacebookGraphClient()

		def facebookUser = client.fetchTypedObject(USER_ID, FacebookUser)
		assert facebookUser.class in com.restfb.types.User, "Facebook user class should be com.restfb.types.User instead of ${facebookUser.class}"
		assert facebookUser.id == USER_ID, "Facebook user id doesn't match"
	}

	void testFetchObjects() {
		def client = new FacebookGraphClient()

		def userObjects = client.fetchObjects([USER_ID, USER_ID2])
		assert userObjects.size() == 2, "It should return 2 user objects"
		assert userObjects[USER_ID], "User ${USER_ID} should be returned"
		assert userObjects[USER_ID].id == USER_ID, "Json user id doesn't match"
	}

	void testFetchTypedObjects() {
		def client = new FacebookGraphClient()

		def facebookUsers = client.fetchTypedObjects([USER_ID, USER_ID2], FacebookUser)
		assert facebookUsers.size() == 2, "It should return 2 facebook users"
		assert facebookUsers[USER_ID], "User ${USER_ID} should be returned"
		assert facebookUsers[USER_ID].class in com.restfb.types.User, "Facebook user class should be com.restfb.types.User instead of ${facebookUsers.class}"
		assert facebookUsers[USER_ID].id == USER_ID, "Facebook user id doesn't match"
	}

	def testPublish() {
		// TODO
	}

	def testExecuteMultiquery() {
		def client = new FacebookGraphClient()
		String query = "SELECT uid, name FROM user WHERE uid=${USER_ID} OR uid=${USER_ID2}"
		String query2 = "SELECT user_id FROM like WHERE object_id=40796308305"
		Map queries = [users:query,
						  likes:query2]

		def fqlResults = client.executeMultiquery(queries, [limit:10])
		assert fqlResults.size() == 2, "It should return 2 fql results"
		assert fqlResults["users"], "It should return users fql results"
		assert fqlResults["users"][0].uid.toString() == USER_ID, "Fql user id doesn't match"
	}

	List testExecuteQuery() {
		def client = new FacebookGraphClient()
		String query = "SELECT uid, name FROM user WHERE uid=${USER_ID} OR uid=${USER_ID2}"

		def fqlUsers = client.executeQuery(query)
		assert fqlUsers.size() == 2, "It should return 2 fql users"
		assert fqlUsers[0].uid.toString() == USER_ID, "Fql user id doesn't match"
	}

	def testExecuteBatch() {
		def client = new FacebookGraphClient(USER_ACCESS_TOKEN)
		List requests = [USER_ID, "this-is-a-bad-request/xxx"]
		
		def batchResponses = client.executeBatch(requests)
		assert batchResponses.size() == 2, "It should return 3 batch responses"
		assert batchResponses[0].id.toString() == USER_ID, "Batch user id doesn't match"
		assert batchResponses[1] == "Bad request (error code 400)", "It should return Bad request (error code 400)"
	}

}
