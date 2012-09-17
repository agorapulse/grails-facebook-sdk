package grails.plugin.facebooksdk

import grails.test.GrailsMock
import grails.test.GrailsUnitTestCase
import org.codehaus.groovy.grails.plugins.testing.GrailsMockHttpServletRequest
import org.junit.Before

class FacebookUserTests extends GrailsUnitTestCase {

    static APP_ID = 123456789
    static APP_SECRET = 'abcdefghhijkl'

    @Before
    public void setUp() {
        FacebookGraphClient.metaClass.fetchObject = { String object, Map parameters ->
            switch(object) {
                case 'oauth/access_token':
                    if (parameters['code']) return [access_token:'new-token-from-code', expires:1000]
                    if (parameters['fb_exchange_token']) return [access_token:'new-exchanged-token', expires:1000]
                    break
            }
        }
        FacebookUser.metaClass.getRequest = { ->
            def requestMock = new GrailsMockHttpServletRequest()
            requestMock.params = [:]
            requestMock
        }
    }

    void testExchangeToken() {
        // Setup
        GrailsMock contextMock = mockFor(FacebookContext)
        contextMock.demand.getSession(2..2) { ->
            def sessionScopeMock = mockFor(FacebookAppSessionScope)
            sessionScopeMock.demand.setData(2..2) { key, value -> }
            sessionScopeMock.createMock()
        }
        FacebookUser user = new FacebookUser(
            context: contextMock.createMock()
        )
        user.context.app = mockFacebookApp()

        // When
        user._token = 'old-token'
        user.exchangeToken()

        // Then
        assert user._token == 'new-exchanged-token'
    }

    void testGetTokenEmpty() {
        // Setup
        GrailsMock contextMock = mockFor(FacebookContext)
        contextMock.demand.getSession(1..1) { ->
            def sessionScopeMock = mockFor(FacebookAppSessionScope)
            sessionScopeMock.demand.getData(1..1) { key, defaultValue -> defaultValue }
            sessionScopeMock.createMock()
        }
        FacebookUser user = new FacebookUser(
                context: contextMock.createMock()
        )
        user.context.app = mockFacebookApp()

        // When
        String token = user.token

        // Then
        assert token == ''
    }

    void testGetTokenFromSignedRequest() {
        // Setup
        GrailsMock contextMock = mockFor(FacebookContext)
        contextMock.demand.getSession(1..1) { ->
            def sessionScopeMock = mockFor(FacebookAppSessionScope)
            sessionScopeMock.demand.setData(1..1) { key, value -> }
            sessionScopeMock.createMock()
        }
        FacebookUser user = new FacebookUser(
                context: contextMock.createMock()
        )
        user.context.app = mockFacebookApp()
        user.context.signedRequest = mockFor(FacebookSignedRequest).createMock()

        // When
        user.context.signedRequest.accessToken = 'access-token'
        String token = user.token

        // Then
        assert token == 'access-token'
    }

    void testGetTokenFromSignedRequestCode() {
        // Setup
        GrailsMock contextMock = mockFor(FacebookContext)
        contextMock.demand.getSession(4..4) { ->
            def sessionScopeMock = mockFor(FacebookAppSessionScope)
            sessionScopeMock.demand.getData(0..1) { key -> 'some-other-code' }
            sessionScopeMock.demand.setData(3..3) { key, value -> }
            sessionScopeMock.createMock()
        }
        FacebookUser user = new FacebookUser(
                context: contextMock.createMock()
        )
        user.context.app = mockFacebookApp()
        user.context.signedRequest = mockFor(FacebookSignedRequest).createMock()

        // When
        user.context.signedRequest.code = 'some-code'
        String token = user.token

        // Then
        assert token == 'new-token-from-code'
    }

    /*void testGetTokenFromParamsCode() {
        // Setup
        GrailsMock contextMock = mockFor(FacebookContext)
        contextMock.demand.getSession(4..4) { ->
            def sessionScopeMock = mockFor(FacebookAppSessionScope)
            sessionScopeMock.demand.getData(1..1) { key ->
                if (key == 'state') 'some-state'
            }
            sessionScopeMock.demand.deleteData(1..1) { key -> }
            sessionScopeMock.demand.getData(1..1) { key ->
                if (key == 'code') 'some-code'
                else if (key == 'token') 'existing-token-from-session'
            }
            sessionScopeMock.demand.setData(3..3) { key, value -> }
            sessionScopeMock.createMock()
        }
        FacebookUser user = new FacebookUser(
                context: contextMock.createMock()
        )
        user.context.app = mockFacebookApp()
        FacebookUser.metaClass.getRequest = { ->
            def requestMock = new GrailsMockHttpServletRequest()
            requestMock.params = [
                    code: 'some-code',
                    state: 'some-state'
            ]
            requestMock
        }

        // When
        String token = user.token

        // Then
        assert token == 'existing-token-from-session'
    }*/

    /*void testGetUserId() {
        // To implement
    }*/

    // PRIVATE

    private FacebookApp mockFacebookApp() {
        FacebookApp app = mockFor(FacebookApp).createMock()
        app.id = APP_ID
        app.secret =  APP_SECRET
        return app
    }

}