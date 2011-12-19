package grails.plugins.facebooksdk.scope

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.web.context.request.RequestContextHolder

class FacebookAppScope {
	
	def appId = 0
	
	GrailsWebRequest getRequest() {
		return RequestContextHolder.getRequestAttributes()
	}
	
	private String getKeyVariableName(String key) {
		if (!this.appId) {
			throw new Exception('AppId must be defined')
		}
		return "fb_${this.appId}_${key}"
	}
	
}
