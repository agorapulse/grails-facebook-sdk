package grails.plugin.facebooksdk

import grails.test.mixin.TestFor
import org.codehaus.groovy.grails.plugins.testing.GrailsMockHttpServletRequest
import org.codehaus.groovy.grails.plugins.testing.GrailsMockHttpSession
import org.junit.Before
import org.junit.Ignore

@TestFor(FacebookAppService)
class FacebookAppServiceTests {

    static APP_ID = 123456789
    static APP_SECRET = 'abcdefghhijkl'

    GrailsMockHttpServletRequest mockRequest
    GrailsMockHttpSession mockSession
    def cookieScopeControl
    def requestScopeControl
    def sessionScopeControl

    @Before
    public void setUp() {
        FacebookGraphClient.metaClass.fetchObject = { String object, Map parameters ->
            switch(object) {
                case 'oauth/access_token':
                    return [access_token:'new-access-token', expires:1000]
                    break
            }
            // For fetch, saveComment
            //new JsonSlurper().parseText(new File("test/resources/data/comment/${facebookId}.json").text)
        }
        service.facebookApp = new FacebookApp(id: APP_ID, secret: APP_SECRET)
    }

    void testExchangeAccessToken() {
        // When
        Map result = service.exchangeAccessToken('access-token')
        // Then
        assert result
        assert result['access_token'] == 'new-access-token'
    }

    void testInvalidateUser() {
        // Setup
        mockRequest["fb_${APP_ID}_accessToken"] = 'access-token'
        mockRequest["fb_${APP_ID}_userId"] = 123456789
        mockSession["fb_${APP_ID}_accessToken"] = 'access-token'
        mockSession["fb_${APP_ID}_userId"] = 123456789
        // When
        service.invalidateUser()
        // Then
        assert !mockRequest["fb_${APP_ID}_accessToken"]
        assert !mockRequest["fb_${APP_ID}_userId"]
        assert !mockSession["fb_${APP_ID}_accessToken"]
        assert !mockSession["fb_${APP_ID}_userId"]
    }

    void testGetAccessToken() {
        // When
        //String accessToken = service.getAccessToken()
        // Then
        //assert accessToken

    }

    void testGetApplicationAccessToken() {
        // When
        String accessToken = service.getApplicationAccessToken()
        // Then
        assert accessToken == "$APP_ID|$APP_SECRET"

        // When
        accessToken = service.getApplicationAccessToken(true)
        // Then
        assert accessToken == 'new-access-token'
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

    void testGetUserAccessTokenFromRequestScope() {
        // Setup
        mockRequest["fb_${APP_ID}_accessToken"] = 'access-token'
        mockRequest["fb_${APP_ID}_userId"] = 123456789
        service.facebookAppRequestScope = requestScopeControl.createMock()
        // When
        String accessToken = service.getUserAccessToken()
        // Then
        assert accessToken == 'access-token'
        requestScopeControl.verify()
    }

    @Ignore
    void testGetUserAccessTokenFromSignedRequest() {
        // Setup
        mockRequest["signedRequest"] = 'signed-request'
        // When
        String accessToken = service.getUserAccessToken()
        // Then
        assert accessToken == 'access-token'
    }

    void testGetUserId() {
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
    public void mockRequestScope() {
        mockRequest = new GrailsMockHttpServletRequest()
        requestScopeControl = mockFor(FacebookAppRequestScope)
        requestScopeControl.demand.deleteData(0..2) { String key -> mockRequest["fb_${APP_ID}_${key}"] = null }
        requestScopeControl.demand.hasData(0..2) { String key -> if (mockRequest["fb_${APP_ID}_${key}"]) true else false }
        requestScopeControl.demand.getData(0..1) { String key, String defaultValue -> mockRequest["fb_${APP_ID}_${key}"] }
        service.facebookAppRequestScope = requestScopeControl.createMock()
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