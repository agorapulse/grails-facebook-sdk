package grails.plugin.facebooksdk

import grails.test.mixin.TestFor
import org.codehaus.groovy.grails.plugins.testing.GrailsMockHttpServletRequest
import org.codehaus.groovy.grails.plugins.testing.GrailsMockHttpSession
import org.junit.Before
import org.junit.Ignore

@TestFor(FacebookContext)
class FacebookContextTests {

    static APP_ID = 123456789
    static APP_SECRET = 'abcdefghhijkl'

    GrailsMockHttpServletRequest mockRequest
    GrailsMockHttpSession mockSession
    def cookieScopeControl
    def requestScopeControl
    def sessionScopeControl

    @Before
    public void setUp() {
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

    // PRIVATE

    @Before
    public void mockCookieScope() {
        cookieScopeControl = mockFor(FacebookAppCookieScope)
        cookieScopeControl.demand.hasCookie(0..1) { -> true }
        cookieScopeControl.demand.deleteCookie(0..1) { -> }
        service.facebookAppCookieScope = cookieScopeControl.createMock()
    }

    @Before
    public void mockSessionScope() {
        mockSession = new GrailsMockHttpSession()
        sessionScopeControl = mockFor(FacebookAppSessionScope)
        sessionScopeControl.demand.isEnabled(0..1) { -> true }
        sessionScopeControl.demand.deleteAllData(0..1) { -> mockSession.attributeNames.each { mockSession[it] = null } }
        service.facebookAppPersistentScope = sessionScopeControl.createMock()
    }

}