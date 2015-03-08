package grails.plugin.facebooksdk

//import org.grails.plugins.testing.GrailsMockHttpServletRequest
//import org.grails.plugins.testing.GrailsMockHttpSession
import spock.lang.Specification

class FacebookContextSpec extends Specification {

    static APP_ID = 123456789
    static APP_SECRET = 'abcdefghhijkl'

    /*GrailsMockHttpServletRequest mockRequest
    GrailsMockHttpSession mockSession
    def cookieScopeControl
    def sessionScopeControl

    void setup() {
        // Mock cookie scope
        cookieScopeControl = mockFor(FacebookCookieScope, true)
        //cookieScopeControl.demand.value() { -> true }
        cookieScopeControl.demand.deleteCookie() { -> }

        // Mock session scope
        mockSession = new GrailsMockHttpSession()
        sessionScopeControl = mockFor(FacebookSessionScope, true)
        sessionScopeControl.demand.deleteAllData() { -> mockSession.attributeNames.each { mockSession[it] = null } }
    }

    void testGetLoginStatusURL() {
        // To implement
    }

    void testGetLoginURL() {
        // To implement
    }

    void testGetLogoutURL() {
        // To implement
    }*/

}