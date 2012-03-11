package grails.plugins.facebooksdk

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.web.context.request.RequestContextHolder

abstract class FacebookAppScope {
	
	FacebookApp facebookApp
	
	GrailsWebRequest getRequest() {
		RequestContextHolder.getRequestAttributes()
	}
	
	private String getKeyVariableName(String key) {
		"fb_${facebookApp.id}_${key}"
	}
	
}