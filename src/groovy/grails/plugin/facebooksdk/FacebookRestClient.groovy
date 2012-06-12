package grails.plugin.facebooksdk

import com.restfb.DefaultLegacyFacebookClient
import com.restfb.Parameter

class FacebookRestClient extends DefaultLegacyFacebookClient {

	FacebookRestClient(String accessToken = '') {
		super(accessToken)
	}
	
	def execute(String method, Map parameters = [:]) {
		def result = super.execute(method, String, buildVariableArgs(parameters))
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
