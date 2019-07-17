package grails.plugin.facebooksdk

import com.restfb.FacebookClient
import spock.lang.Specification

class FacebookContextAppSpec extends Specification {

    private static String APP_ID = '123456789'
    private static String APP_SECRET = 'abcdefghhijkl'

    FacebookClient client = Mock()

    FacebookGraphClientService factory = Mock() {
        newClient(*_) >> client
    }

    FacebookContext context = Mock() {
        getFacebookGraphClientService() >> factory
    }

    void "Get token"() {
        when:
            FacebookContextApp facebookContextApp = new FacebookContextApp(
                    id: APP_ID as Long,
                    context: context,
                    secret: APP_SECRET
            )
            String token = facebookContextApp.token

        then:
            token == "$APP_ID|$APP_SECRET"

        when:
            token = facebookContextApp.getToken(true)

        then:
            token == 'new-access-token'

            1 * client.obtainAppAccessToken(APP_ID, APP_SECRET) >> new FacebookClient.AccessToken(
                    accessToken: 'new-access-token',
                    expires: System.currentTimeMillis() + 10000
            )
    }

}