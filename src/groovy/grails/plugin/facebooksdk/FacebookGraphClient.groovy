package grails.plugin.facebooksdk

import com.restfb.BinaryAttachment
import com.restfb.Connection
import com.restfb.Parameter
import com.restfb.batch.BatchRequest
import com.restfb.batch.BatchRequest.BatchRequestBuilder
import com.restfb.json.JsonObject
import grails.converters.JSON

class FacebookGraphClient extends DefaultFacebookGraphClient {

    static final int DEFAULT_READ_TIMEOUT_IN_MS = 180000

    FacebookGraphClient(String accessToken = '', Integer timeout = DEFAULT_READ_TIMEOUT_IN_MS, String proxyHost = null, Integer proxyPort = null) {
        super(accessToken, timeout, proxyHost, proxyPort)
    }

	boolean deleteObject(String object) {
		return super.deleteObject(object)
	}

	List fetchConnection(String connection, Map parameters = [:]) {
		Connection result = super.fetchConnection(connection, JsonObject, buildVariableArgs(parameters))
		return (result && result.data) ? JSON.parse(result.data.toString()) as List : []
	}

	def fetchObject(String object, Map parameters = [:]) {
		String result = makeRequest(object, buildVariableArgs(parameters))
		return parseResult(result)
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
				batchIds.clear()
			}
		}
		return objects
	}

    def publish(String connection, Map parameters = [:], String fileName, InputStream inputStream) {
        if (fileName && inputStream) {
            return super.publish(connection, JsonObject, BinaryAttachment.with(fileName, inputStream), buildVariableArgs(parameters))
        } else {
            return super.publish(connection, JsonObject, buildVariableArgs(parameters))
        }
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
            if (it instanceof BatchRequest) {
                batchRequests << it
            } else {
                batchRequests << new BatchRequestBuilder(it as String).build()
            }
			if (batchRequests.size() == batchSize || it == requests[-1]) {
				def batchResponses = super.executeBatch(batchRequests, [])
                batchResponses.each { batchResponse ->
                    if (batchResponse) {
                        if (batchResponse.code == 200) {
                            responses << JSON.parse(batchResponse.body)
                        } else {
                            responses << [error: [code: batchResponse.code, message: batchResponse.body]]
                        }
                    } else {
                        responses << [:]
                    }
                }
				batchRequests.clear()
			}
		}
		return responses
	}

	Map executeQueries(Map queries, Map parameters = [:], int batchSize = 20) {
        Map batchQueries = [:]
        Map results = [:]
        List queryNames = queries.keySet() as String[]
        queryNames.each { String name ->
            batchQueries[name] = queries[name]
            if (batchQueries.size() == batchSize || name == queryNames[-1]) {
                parameters['q'] = batchQueries
                def result = makeRequest('fql', buildVariableArgs(parameters))
                result = parseResult(result)
                if (result && result.data) {
                    result.data.each {
                        results[it['name']] = it['fql_result_set']
                    }
                }
                batchQueries.clear()
            }
        }
        return results
	}

	List executeQuery(String query, Map parameters = [:]) {
		parameters['q'] = query
		def result = makeRequest('fql', buildVariableArgs(parameters))
		result = parseResult(result)
		return (result && result.data) ? result.data : []
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
			variableArgs[index-1] = Parameter.with(key as String, value)
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