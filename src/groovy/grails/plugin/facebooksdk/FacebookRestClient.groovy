package grails.plugin.facebooksdk

import com.restfb.Parameter
import grails.converters.JSON

class FacebookRestClient extends DefaultFacebookRestClient {

    static final int DEFAULT_READ_TIMEOUT_IN_MS = 180000

    FacebookRestClient(String accessToken = '', Integer timeout = DEFAULT_READ_TIMEOUT_IN_MS, String proxyHost = null, Integer proxyPort = null) {
        super(accessToken, timeout, proxyHost, proxyPort)
    }
	
	def execute(String method, Map parameters = [:]) {
        def result = super.execute(method, String, buildVariableArgs(parameters))
		return parseResult(result)
	}

    List executeQuery(String query, Map parameters = [:]) {
        List result = []
        parameters['query'] = query
        List resultList = super.executeForList('fql.query', String, buildVariableArgs(parameters))
        resultList.each {
            result << parseResult(it)
        }
        return result
    }

    Map executeQueries(Map queries, Map parameters = [:], int batchSize = 20) {
        Map batchQueries = [:]
        Map results = [:]
        List queryNames = queries.keySet() as String[]
        queryNames.each { String name ->
            batchQueries[name] = queries[name]
            if (batchQueries.size() == batchSize || name == queryNames[-1]) {
                parameters['queries'] = (batchQueries as JSON).toString()
                List resultList = super.executeForList('fql.multiquery', String, buildVariableArgs(parameters))
                resultList.each {
                    def result = parseResult(it)
                    results[result['name']] = result['fql_result_set']
                }
                batchQueries.clear()
            }
        }
        return results
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
