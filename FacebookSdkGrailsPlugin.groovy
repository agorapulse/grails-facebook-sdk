import grails.plugin.facebooksdk.*
import org.springframework.aop.scope.ScopedProxyFactoryBean

class FacebookSdkGrailsPlugin {
	
	def version = "2.3.0"
	def grailsVersion = "2.0 > *"

    def author = "Benoit Hediard"
    def authorEmail = "ben@agorapulse.com"
    def title = "Facebook SDK Plugin"
    def description = '''The Facebook SDK Plugin allows your Grails application to use the Facebook Platform and develop Facebook apps on Facebook.com or on web sites (with Facebook Connect).
It is a port of the official Facebook PHP SDK to Grails 2.0.
''' 
    
    def documentation = "http://agorapulse.github.io/grails-facebook-sdk/guide/"
    def license = "APACHE"
    def organization = [ name: "AgoraPulse", url: "http://www.agorapulse.com/" ]
    def issueManagement = [ system: "github", url: "https://github.com/agorapulse/grails-facebook-sdk/issues" ]
    def scm = [  url: "https://github.com/agorapulse/grails-facebook-sdk" ]

    def doWithSpring = {
        // It looks like proxy is automatically generated when request scope bean are injected in grails singleton bean (services)
        facebookContextProxy(ScopedProxyFactoryBean) { bean ->
            targetBeanName = 'facebookContext'
            proxyTargetClass = true
        }
        facebookContext(FacebookContext) { bean ->
            bean.scope = 'request'
            grailsApplication = ref('grailsApplication')
            grailsLinkGenerator = ref('grailsLinkGenerator')
        }
    }

}
