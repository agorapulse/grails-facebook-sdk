import agorapulse.plugins.facebooksdk.FacebookContext
import grails.plugins.*
import org.springframework.aop.scope.ScopedProxyFactoryBean

class FacebookSdkGrailsPlugin extends Plugin {

   // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.0.0.M2 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

   def title = "Facebook SDK Plugin"
    def author = "Benoit Hediard"
    def authorEmail = "ben@agorapulse.com"
    def description = '''The Facebook SDK Plugin allows your Grails application to use the Facebook Platform and develop Facebook apps on Facebook.com or on web sites (with Facebook Connect).
It is a port of the official Facebook PHP SDK to Grails 3.0.
''' 

    def profiles = ['web']

    def documentation = "http://agorapulse.github.io/grails-facebook-sdk/guide/"
    def license = "APACHE"
    def organization = [ name: "AgoraPulse", url: "http://www.agorapulse.com/" ]
    def issueManagement = [ system: "github", url: "https://github.com/agorapulse/grails-facebook-sdk/issues" ]
    def scm = [  url: "https://github.com/agorapulse/grails-facebook-sdk" ]

    Closure doWithSpring() { {->
            // It looks like proxy is automatically generated when request scope bean are injected in grails singleton bean (services)
            facebookContextProxy(ScopedProxyFactoryBean) { bean ->
                targetBeanName = 'facebookContext'
                proxyTargetClass = true
            }
            facebookContext(FacebookContext) { bean ->
                bean.scope = 'request'
                grailsLinkGenerator = ref('grailsLinkGenerator')
            }
        } 
    }

}
