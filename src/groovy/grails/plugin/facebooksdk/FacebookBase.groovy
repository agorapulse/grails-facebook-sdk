package grails.plugin.facebooksdk

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.web.context.request.RequestContextHolder

class FacebookBase {

    def config = [:]

    protected FacebookGraphClient getGraphClient(String token = '') {
        new FacebookGraphClient(
                token,
                config.timeout ?: FacebookGraphClient.DEFAULT_READ_TIMEOUT_IN_MS,
                config.proxyHost ?: null,
                config.proxyPort ?: null
        )
    }

    protected GrailsWebRequest getRequest() {
        return RequestContextHolder.getRequestAttributes() as GrailsWebRequest
    }

}
