package grails.plugin.facebooksdk

import grails.test.GrailsUnitTestCase
import org.codehaus.groovy.grails.plugins.testing.GrailsMockHttpServletRequest
import org.codehaus.groovy.grails.plugins.testing.GrailsMockHttpSession
import org.junit.Before

class FacebookContextTests extends GrailsUnitTestCase {

    static APP_ID = 123456789
    static APP_SECRET = 'abcdefghhijkl'

    GrailsMockHttpServletRequest mockRequest
    GrailsMockHttpSession mockSession
    def cookieScopeControl
    def sessionScopeControl

    @Before
    void setUp() {

    }

    @Before
    void mockCookieScope() {
        cookieScopeControl = mockFor(FacebookCookieScope, true)
        //cookieScopeControl.demand.value() { -> true }
        cookieScopeControl.demand.deleteCookie() { -> }
    }

    @Before
    void mockSessionScope() {
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
    }

}