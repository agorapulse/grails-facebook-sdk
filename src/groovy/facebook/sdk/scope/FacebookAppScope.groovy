package facebook.sdk.scope

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.web.context.request.RequestContextHolder

class FacebookAppScope {
	
	long appId = 0
	
	GrailsWebRequest getRequest() {
		return RequestContextHolder.getRequestAttributes()
	}
	
	FacebookAppScope(long appId) {
		this.appId = appId
	}
	
	private String getKeyVariableName(String key) {
		if (!this.appId) {
			throw new Exception('AppId must be defined')
		}
		return "fb_${this.appId}_${key}"
	}
	
}
