package grails.plugin.facebooksdk

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.web.context.request.RequestContextHolder

abstract class FacebookAppScope {
	
	//FacebookApp facebookApp
    FacebookContextService facebookContextProxyService
	
	GrailsWebRequest getRequest() {
		RequestContextHolder.getRequestAttributes() as GrailsWebRequest
	}
	
	protected String getKeyVariableName(String key) {
		"fb_${facebookContextProxyService.app.id}_${key}"
	}
	
}