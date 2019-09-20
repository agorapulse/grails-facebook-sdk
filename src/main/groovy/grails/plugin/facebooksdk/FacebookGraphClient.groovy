package grails.plugin.facebooksdk

import com.restfb.BinaryAttachment
import com.restfb.Connection
import com.restfb.FacebookEndpoints
import com.restfb.Parameter
import com.restfb.Version
import com.restfb.batch.BatchRequest
import com.restfb.batch.BatchRequest.BatchRequestBuilder
import com.restfb.json.JsonObject
import grails.converters.JSON
import grails.util.Holders
import groovy.transform.PackageScope

class FacebookGraphClient extends DefaultFacebookGraphClient {

    static final int DEFAULT_READ_TIMEOUT_IN_MS = 180000
    static final String DEFAULT_API_VERSION = Version.LATEST.urlElement

	protected final String apiVersionString

	/**
	 *
	 * @param accessToken
	 * @param apiVersion
	 * @param timeout
	 * @param proxyHost
	 * @param proxyPort
     * @param
	 * @deprecated use FacebookGraphClientService instead
	 */
	@Deprecated
    @PackageScope
	FacebookGraphClient(String accessToken = '',
									  String apiVersion = null,
									  Integer timeout = DEFAULT_READ_TIMEOUT_IN_MS,
									  String proxyHost = null,
									  Integer proxyPort = null,
                                      String appSecret = null) {
        super(accessToken, getAppSecretOrFromConfig(appSecret), timeout, proxyHost, proxyPort, buildVersionFromString(apiVersion))
        this.apiVersionString = apiVersion ?: config.apiVersion ?: DEFAULT_API_VERSION
    }

	/**
	 *
	 * @param object
	 * @param parameters
	 * @return
	 */
	boolean deleteObject(String object, Map parameters = [:]) {
		return super.deleteObject(object, buildVariableArgs(parameters))
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
	Object fetchObject(String object,
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

	public <T> Map<String, T> fetchAll(List<String> ids, Class<T> objectType, Map<String, Object> parameters = [:], int batchSize = 20) {
		Map<String, T> results = [:]
		ids.collate(batchSize).each {
			JsonObject jsonObject = super.fetchObjects(it, JsonObject, buildVariableArgs(parameters))
			for (JsonObject.Member member in jsonObject) {
				results.put(member.name, jsonMapper.toJavaObject(member.value.toString(), objectType))
			}
		}
		return results
	}

	/**
	 *
	 * @param connection
	 * @param parameters
	 * @param fileName
	 * @param inputStream
	 * @return
	 */
    Object publish(String connection,
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
	Object publish(String connection,
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
				Object batchResponses = super.executeBatch(batchRequests, [])
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
                Object result = makeRequest('fql', buildVariableArgs(parameters))
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
		Object result = makeRequest('fql', buildVariableArgs(parameters))
		result = parseResult(result)
		return (result && result.data) ? result.data : []
	}

	/**
	 *
	 * @param endPoint
	 * @param parameters
	 * @return
	 */
	@Deprecated
	Object makeRequest(String endPoint,
					Map parameters = [:]) {
		Object result = super.makeRequest(endPoint, false, false, null, buildVariableArgs(parameters))
		return parseResult(result)
	}

	/**
	 *
	 * @param endPoint
	 * @param parameters
	 * @return
	 */
	@Deprecated
	Object makePostRequest(String endPoint,
						Map parameters = [:]) {
		Object result = super.makeRequest(endPoint, true, false, null, buildVariableArgs(parameters))
		return parseResult(result)
	}

	/**
	 *
	 * @param endPoint
	 * @param parameters
	 * @return
	 */
	@Deprecated
	Object makeDeleteRequest(String endPoint,
						  Map parameters = [:]) {
		Object result = super.makeRequest(endPoint, false, true, null, buildVariableArgs(parameters))
		return parseResult(result)
	}

	// PRIVATE

	private static String getAppSecretOrFromConfig(String appSecret) {
		appSecret ?: config.appSecret
	}
	
    private static Version buildVersionFromString(String apiVersion) {
        Version.getVersionFromString(apiVersion ?: config.apiVersion ?: DEFAULT_API_VERSION)
    }

	private static Parameter[] buildVariableArgs(Map parameters) {
		Parameter[] variableArgs = new Parameter[parameters.size()]
		parameters.eachWithIndex { key, value, index ->
			variableArgs[index-1] = Parameter.with(key as String, value)
		}
		return variableArgs
	}

    private static Object getConfig() {
        Holders.config?.grails?.plugin?.facebooksdk ?: new ConfigObject()
    }

	private static Object parseResult(String result) {
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
