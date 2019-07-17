package grails.plugin.facebooksdk

import com.restfb.FacebookClient
import com.restfb.Parameter
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.servlet.mvc.GrailsWebRequest
import spock.lang.Specification

class FacebookContextUserSpec extends Specification {

    static APP_ID = '123456789'
    static APP_SECRET = 'abcdefghhijkl'

    FacebookClient client = Mock() {
        fetchObject(*_) >> { String object, Class objectType, Parameter... parameters ->
            if (object == 'oauth/access_token') {
                if (parameters.any { it.name == 'code' }) return [access_token: 'new-token-from-code', expires: 1000]
                if (parameters.any {
                    it.name == 'fb_exchange_token'
                }) return [access_token: 'new-token-from-code', expires: 1000]
            }
        }
    }

    FacebookGraphClientService factory = Mock() {
        newClient(*_) >> client
    }

    FacebookSessionScope scope = Mock()

    FacebookSignedRequest signedRequest = Mock()

    FacebookContextApp facebookContextApp = new FacebookContextApp(
            id: APP_ID as Long,
            secret: APP_SECRET
    )

    GrailsParameterMap grailsParameterMap = Mock()

    GrailsWebRequest grailsWebRequest = Mock() {
        getParams() >> grailsParameterMap
    }

    FacebookContext context = Mock() {
        getFacebookGraphClientService() >> factory
        getApp() >> facebookContextApp
        getSession() >> scope
        getSignedRequest() >> signedRequest
        getRequest() >> grailsWebRequest
    }

    void setup() {
        facebookContextApp.context = context
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

            1 * client.obtainExtendedAccessToken(
                    APP_ID,
                    APP_SECRET,
                    'old-token'
            ) >> new FacebookClient.AccessToken(
                    accessToken: 'new-exchanged-token',
                    expires: System.currentTimeMillis() + 1000
            )
    }

    void "Get empty token"() {
        given:
            FacebookContextUser user = new FacebookContextUser(
                    context: context
            )
        when:
            String token = user.token

        then:
            token == ''

            1 * scope.getData(*_) >> { String name, Object defaultValue -> defaultValue }
    }

    void "Get token from signed request"() {
        given:
            FacebookContextUser user = new FacebookContextUser(
                    context: context
            )
        when:
            String token = user.token

        then:
            token == 'access-token'

            _ * signedRequest.getAccessToken() >> 'access-token'
    }

    void "Get token from signed request code"() {
        given:
            FacebookContextUser user = new FacebookContextUser(
                    context: context
            )

        when:
            String token = user.token
        then:
            token == 'new-token-from-code'

            _ * signedRequest.getCode() >> 'some-code'
            1 * scope.getData(*_) >> { String name -> 'some-other-code' }

            1 * client.obtainUserAccessToken(
                    APP_ID,
                    APP_SECRET,
                    _,
                    'some-code'
            ) >> new FacebookClient.AccessToken(
                    accessToken: 'new-token-from-code',
                    expires: System.currentTimeMillis() + 1000
            )
    }


}