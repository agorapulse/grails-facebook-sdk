package grails.plugins.facebooksdk

import com.restfb.batch.BatchRequest
import com.restfb.batch.BatchRequest.BatchRequestBuilder
import com.restfb.BinaryAttachment
import com.restfb.Connection
import com.restfb.DefaultFacebookClient
import com.restfb.Parameter
import com.restfb.json.JsonObject

import grails.converters.JSON

class FacebookGraphClient extends DefaultFacebookClient {
	
	FacebookGraphClient(String accessToken = '') {
		super(accessToken)
	}

	boolean deleteObject(String object) {
		return super.deleteObject(object)
	}

	List fetchConnection(String connection, Map parameters = [:]) {
		Connection result = super.fetchConnection(connection, JsonObject, buildVariableArgs(parameters))
		return (result && result.data) ? JSON.parse(result.data.toString()) : []
	}

	def fetchObject(String object, Map parameters = [:]) {
		String result = makeRequest(object, buildVariableArgs(parameters))
		parseResult(result)
	}

	Map fetchObjects(List ids, Map parameters = [:], int batchSize = 20) {
		List batchIds = []
		JsonObject jsonObject
		Map objects = [:]
		ids.each { batchId ->
			batchIds << batchId.toString()
			if (batchIds.size() == batchSize || batchId == ids[-1]) {
				jsonObject = super.fetchObjects(batchIds, JsonObject, buildVariableArgs(parameters))
				objects += JSON.parse(jsonObject.toString())
				batchIds = []
			}
		}
		return objects
	}

	def publish(String connection, Map parameters = [:], String filePath = '') {
		if (filePath) {
			File file = new File(filePath)
			return super.publish(connection, JsonObject, BinaryAttachment.with(file.name, new FileInputStream(file)), buildVariableArgs(parameters))
		} else {
			return super.publish(connection, JsonObject, buildVariableArgs(parameters))	
		}
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

	def makeRequest(String endPoint, Map parameters = [:]) {
		def result = super.makeRequest(endPoint, false, false, null, buildVariableArgs(parameters))
		return parseResult(result)
	}

	def makePostRequest(String endPoint, Map parameters = [:]) {
		def result = super.makeRequest(endPoint, true, false, null, buildVariableArgs(parameters))
		return parseResult(result)
	}

	def makeDeleteRequest(String endPoint, Map parameters = [:]) {
		def result = super.makeRequest(endPoint, false, true, null, buildVariableArgs(parameters))
		return parseResult(result)
	}

	// PRIVATE

	private Parameter[] buildVariableArgs(Map parameters) {
		Parameter[] variableArgs = new Parameter[parameters.size()]
		parameters.eachWithIndex { key, value, index ->
			variableArgs[index-1] = Parameter.with(key, value)
		}
		return variableArgs
	}
	
	private def parseResult(String result) {
		if (result.startsWith('{')) {
			return JSON.parse(result)
		} else if (result.find('=')) {
			Map resultMap = [:]
			result.tokenize('&').each {
				resultMap[it.tokenize('=')[0]] = it.tokenize('=')[1]
			}
			return resultMap
		} else if (result == 'false') {
			return false
		} else if (result == 'true') {
			return true
		} else {
			return result
		}
	}

}