package grails.plugin.facebooksdk

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.web.context.request.RequestContextHolder

abstract class FacebookAppScope {

    protected FacebookApp getFacebookApp() {
        // Get facebook app from request (created by FacebookSdkFilters)
        Map facebook = request.currentRequest.getAttribute("facebook") as Map
        if (facebook) return facebook['app']
    }

    protected GrailsWebRequest getRequest(){
        RequestContextHolder.getRequestAttributes() as GrailsWebRequest
    }

    protected String getKeyVariableName(String key) {
		"fb_${facebookApp.id}_${key}"
	}
	
}