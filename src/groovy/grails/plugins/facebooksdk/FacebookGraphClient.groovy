package grails.plugins.facebooksdk

import com.restfb.batch.BatchRequest
import com.restfb.batch.BatchRequest.BatchRequestBuilder
import com.restfb.Connection
import com.restfb.DefaultFacebookClient
import com.restfb.DefaultJsonMapper
import com.restfb.Parameter
import com.restfb.JsonMapper
import com.restfb.json.JsonObject

import grails.converters.JSON

class FacebookGraphClient extends DefaultFacebookClient {

	FacebookGraphClient(String accessToken = "") {
		super(accessToken)
	}

	boolean deleteObject(String objectId) {
		return super.deleteObject(objectId)
	}

	List fetchConnection(String objectId, String connectionType, Map parameters = [:]) {
		Connection connection = super.fetchConnection("${objectId}/${connectionType}", JsonObject, buildVariableArgs(parameters))
		return (connection && connection.data) ? JSON.parse(connection.data.toString()) : []
	}

	List fetchTypedConnection(String objectId, String connectionType, Class objectType, Map parameters = [:]) {
		Connection connection = super.fetchConnection("${objectId}/${connectionType}", buildObjectClass(objectType), buildVariableArgs(parameters))
		return (connection && connection.data) ? new ArrayList(connection.data) : []
	}

	def fetchObject(String objectId, Map parameters = [:]) {
		return fetchTypedObject(objectId, JsonObject, parameters)
	}

	def fetchTypedObject(String objectId, Class objectType, Map parameters = [:]) {
		return super.fetchObject(objectId, buildObjectClass(objectType), buildVariableArgs(parameters))
	}

	Map fetchObjects(List objectIds, Map parameters = [:], int batchSize = 20) {
		List batchIds = []
		JsonObject jsonObject
		Map objects = [:]
		objectIds.each { batchId ->
			batchIds << batchId
			if (batchIds.size() == batchSize || batchId == objectIds[-1]) {
				jsonObject = super.fetchObjects(batchIds, JsonObject, buildVariableArgs(parameters))
				objects += JSON.parse(jsonObject.toString())
				batchIds = []
			}
		}
		return objects
	}

	Map fetchTypedObjects(List objectIds, Class objectType, Map parameters = [:], int batchSize = 20) {
		List batchIds = []
		JsonObject jsonObject
		JsonMapper jsonMapper = new DefaultJsonMapper()
		Map objects = [:]
		objectIds.each { batchId ->
			batchIds << batchId
			if (batchIds.size() == batchSize || batchId == objectIds[-1]) {
				jsonObject = super.fetchObjects(batchIds, JsonObject, buildVariableArgs(parameters))
				objectIds.each { id ->
					objects[id] = jsonMapper.toJavaObject(jsonObject.getString(id), objectType.superclass)
				}
				batchIds = []
			}
		}
		return objects
	}

	def publish() {
		// TODO
	}

	List executeBatch(List requests, int batchSize = 20) {
		List batchRequests = []
		List responses = []
		requests.each {
			batchRequests << new BatchRequestBuilder(it).build()
			if (batchRequests.size() == batchSize || it == requests[-1]) {
				def batchResponses = super.executeBatch(batchRequests, [])
				batchResponses.eachWithIndex { batchResponse, index ->
					if (batchResponse.code == 200) {
						responses << JSON.parse(batchResponse.body)
					} else {
						responses << "Bad request (error code ${batchResponse.code})"
					}
				}
				batchRequests = []
			}
		}
		return responses
	}

	Map executeMultiquery(Map queries, Map parameters = [:]) {
		def results = super.executeMultiquery(queries, JsonObject, buildVariableArgs(parameters))
		return JSON.parse(results.toString())
	}

	List executeQuery(String query, Map parameters = [:]) {
		def result = super.executeQuery(query, JsonObject, buildVariableArgs(parameters))
		return JSON.parse(result.toString())
	}

	// PRIVATE

	private Class buildObjectClass(Class objectClass) {
		if (objectClass.name.startsWith("com.restfb.")) {
			return objectClass
		} else {
			return objectClass.superclass
		}
	}

	private Parameter[] buildVariableArgs(Map parameters) {
		Parameter[] variableArgs = new Parameter[parameters.size()]
		parameters.eachWithIndex { key, value, index ->
			variableArgs[index-1] = Parameter.with(key, value)
		}
		return variableArgs
	}

}