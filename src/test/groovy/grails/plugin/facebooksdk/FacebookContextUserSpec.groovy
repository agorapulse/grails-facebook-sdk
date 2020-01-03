package grails.plugin.facebooksdk

import org.grails.plugins.testing.GrailsMockHttpServletRequest
import spock.lang.Specification

class FacebookContextUserSpec extends Specification {

    static APP_ID = 123456789
    static APP_SECRET = 'abcdefghhijkl'

    FacebookContext context

    void setup() {
        FacebookGraphClient.metaClass.fetchObject = { String object, Map parameters ->
            switch (object) {
                case 'oauth/access_token':
                    if (parameters['code']) return [access_token: 'new-token-from-code', expires: 1000]
                    if (parameters['fb_exchange_token']) return [access_token: 'new-exchanged-token', expires: 1000]
                    break
            }
        }
        FacebookContextUser.metaClass.getRequest = { ->
            def requestMock = new GrailsMockHttpServletRequest()
            requestMock.params = [:]
            requestMock
        }

        // Mock context
        context = new FacebookContext()
        context.metaClass.getApp = {
            new FacebookContextApp(context: context, id: APP_ID, secret: APP_SECRET)
        }

        def scope = new FacebookSessionScope()
        scope.metaClass.setData = { key, value -> }
        scope.metaClass.getData = { key, defaultValue -> defaultValue }
        scope.metaClass.getRequest = { ->
            def request = new Object()
            request.metaClass.getSession = {
                def session = new Object()
                session.metaClass.setAttribute = { k, v -> }
                session.metaClass.getAttribute = { k -> k }
                session
            }
            request
        }
        context.metaClass.getRequest = { -> [params: [:]] }
        context.metaClass.getSession { ->
            scope
        }
    }

    void "Exchange token"() {
        given:
        FacebookContextUser user = new FacebookContextUser(
                context: context
        )

        when:
        user._token = 'old-token'
        user.exchangeToken()

        then:
        user._token == 'new-exchanged-token'
    }

    void "Get empty token"() {
        given:
        FacebookContextUser user = new FacebookContextUser(
                context: context
        )

        def scope = new FacebookSessionScope()

        scope.metaClass.getRequest = { ->
            def request = new Object()
            request.metaClass.getSession = {
                def session = new Object()
                session.metaClass.setAttribute = { k, v -> }
                session.metaClass.getAttribute = { k -> '' }
                session
            }
            request
        }

        context.metaClass.getSession { ->
            scope
        }

        when:
        String token = user.token

        then:
        assert token == ''
    }

    void "Get token from signed request"() {
        given:
        FacebookContextUser user = new FacebookContextUser(
                context: context
        )

        context.signedRequest = new FacebookSignedRequest(accessToken: 'access-token')

        when:
        user.context.signedRequest.accessToken = 'access-token'
        String token = user.token

        then:
        token == 'access-token'
    }

    void "Get token from signed request code"() {
        given:
        FacebookContextUser user = new FacebookContextUser(
                context: context
        )
        def scope = new FacebookSessionScope()
        scope.metaClass.setData = { key, value -> }
        scope.metaClass.getData = { key, defaultValue -> defaultValue }
        scope.metaClass.getRequest = { ->
            def request = new Object()
            request.metaClass.getSession = {
                def session = new Object()
                session.metaClass.setAttribute = { k, v -> }
                session.metaClass.getAttribute = { k -> k }
                session
            }
            request
        }
        context.metaClass.getSession = {
            scope
        }
        user.context.signedRequest = new FacebookSignedRequest(code: 'some-code')

        when:
        String token = user.token

        then:
        token == 'new-token-from-code'
    }

    /*void testGetTokenFromParamsCode() {
        // Setup
        def contextMock = mockFor(FacebookContext)
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
            def requestMock = new defHttpServletRequest()
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


}