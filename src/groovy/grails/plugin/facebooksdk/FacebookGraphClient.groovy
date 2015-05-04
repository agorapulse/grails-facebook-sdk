package grails.plugin.facebooksdk

import com.restfb.BinaryAttachment
import com.restfb.Connection
import com.restfb.Parameter
import com.restfb.Version
import com.restfb.batch.BatchRequest
import com.restfb.batch.BatchRequest.BatchRequestBuilder
import com.restfb.json.JsonObject
import grails.converters.JSON
import grails.util.Holders
import org.springframework.context.ApplicationContext

class FacebookGraphClient extends DefaultFacebookGraphClient {

    static final int DEFAULT_READ_TIMEOUT_IN_MS = 180000
    static final String DEFAULT_API_VERSION = 'v2.3'

	/**
	 *
	 * @param accessToken
	 * @param apiVersion
	 * @param timeout
	 * @param proxyHost
	 * @param proxyPort
	 */
    FacebookGraphClient(String accessToken = '',
						String apiVersion = null,
						Integer timeout = DEFAULT_READ_TIMEOUT_IN_MS,
						String proxyHost = null,
						Integer proxyPort = null) {
        super(accessToken, timeout, proxyHost, proxyPort, buildVersionFromString(apiVersion))
    }

	/**
	 *
	 * @param object
	 * @return
	 */
    boolean deleteObject(String object) {
		return super.deleteObject(object)
	}

	/**
	 *
	 * @param connection
	 * @param parameters
	 * @return
	 */
	List fetchConnection(String connection,
						 Map parameters = [:]) {
		Connection result = super.fetchConnection(connection, JsonObject, buildVariableArgs(parameters))
		return (result && result.data) ? JSON.parse(result.data.toString()) as List : []
	}

	/**
	 *
	 * @param object
	 * @param parameters
	 * @return
	 */
	def fetchObject(String object,
					Map parameters = [:]) {
		String result = makeRequest(object, buildVariableArgs(parameters))
		return parseResult(result)
	}

	/**
	 *
	 * @param ids
	 * @param parameters
	 * @param batchSize
	 * @return
	 */
	Map fetchObjects(List ids,
					 Map parameters = [:],
					 int batchSize = 20) {
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

	/**
	 *
	 * @param connection
	 * @param parameters
	 * @param fileName
	 * @param inputStream
	 * @return
	 */
    def publish(String connection,
				Map parameters = [:],
				String fileName,
				InputStream inputStream) {
        if (fileName && inputStream) {
            return super.publish(connection, JsonObject, BinaryAttachment.with(fileName, inputStream), buildVariableArgs(parameters))
        } else {
            return super.publish(connection, JsonObject, buildVariableArgs(parameters))
        }
    }

	/**
	 *
	 * @param connection
	 * @param parameters
	 * @param filePath
	 * @return
	 */
	def publish(String connection,
				Map parameters = [:],
				String filePath = '') {
		if (filePath) {
			File file = new File(filePath)
			return super.publish(connection, JsonObject, BinaryAttachment.with(file.name, new FileInputStream(file)), buildVariableArgs(parameters))
		} else {
			return super.publish(connection, JsonObject, buildVariableArgs(parameters))	
		}
	}

	/**
	 *
	 * @param requests
	 * @param batchSize
	 * @return
	 */
	List executeBatch(List requests,
					  int batchSize = 20) {
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

	/**
	 *
	 * @param queries
	 * @param parameters
	 * @param batchSize
	 * @return
	 */
	Map executeQueries(Map queries,
					   Map parameters = [:],
					   int batchSize = 20) {
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

	/**
	 *
	 * @param query
	 * @param parameters
	 * @return
	 */
	List executeQuery(String query,
					  Map parameters = [:]) {
		parameters['q'] = query
		def result = makeRequest('fql', buildVariableArgs(parameters))
		result = parseResult(result)
		return (result && result.data) ? result.data : []
	}

	/**
	 *
	 * @param endPoint
	 * @param parameters
	 * @return
	 */
	def makeRequest(String endPoint,
					Map parameters = [:]) {
		def result = super.makeRequest(endPoint, false, false, null, buildVariableArgs(parameters))
		return parseResult(result)
	}

	/**
	 *
	 * @param endPoint
	 * @param parameters
	 * @return
	 */
	def makePostRequest(String endPoint,
						Map parameters = [:]) {
		def result = super.makeRequest(endPoint, true, false, null, buildVariableArgs(parameters))
		return parseResult(result)
	}

	/**
	 *
	 * @param endPoint
	 * @param parameters
	 * @return
	 */
	def makeDeleteRequest(String endPoint,
						  Map parameters = [:]) {
		def result = super.makeRequest(endPoint, false, true, null, buildVariableArgs(parameters))
		return parseResult(result)
	}

	// PRIVATE

    static private Version buildVersionFromString(String apiVersion) {
        if (!apiVersion) {
            apiVersion = config.apiVersion ?: DEFAULT_API_VERSION
        }
        Version version = Version.UNVERSIONED
        switch (apiVersion) {
            case 'v1.0':
                version = Version.VERSION_1_0
                break
            case 'v2.0':
                version = Version.VERSION_2_0
                break
            case 'v2.1':
                version = Version.VERSION_2_1
                break
			case 'v2.2':
				version = Version.VERSION_2_2
				break
			case 'v2.3':
				version = Version.VERSION_2_3
				break
        }
        version
    }

	static private Parameter[] buildVariableArgs(Map parameters) {
		Parameter[] variableArgs = new Parameter[parameters.size()]
		parameters.eachWithIndex { key, value, index ->
			variableArgs[index-1] = Parameter.with(key as String, value)
		}
		return variableArgs
	}

    static private def getConfig() {
        Holders.config.grails.plugin.facebooksdk
    }
	
	static private def parseResult(String result) {
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