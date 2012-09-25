package grails.plugin.facebooksdk

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.web.context.request.RequestContextHolder

abstract class FacebookScope {
	
	long appId = 0

	GrailsWebRequest getRequest() {
		RequestContextHolder.getRequestAttributes() as GrailsWebRequest
	}
	
	protected String getKeyVariableName(String key) {
		"fb_${appId}_${key}"
	}
	
}