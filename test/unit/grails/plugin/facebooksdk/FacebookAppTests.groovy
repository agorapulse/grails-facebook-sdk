package grails.plugin.facebooksdk

import org.junit.Before

class FacebookAppTests {

    static APP_ID = 123456789
    static APP_SECRET = 'abcdefghhijkl'

    @Before
    public void setUp() {
        FacebookGraphClient.metaClass.fetchObject = { String object, Map parameters ->
            switch(object) {
                case 'oauth/access_token':
                    return [access_token:'new-access-token', expires:1000]
                    break
            }
        }
    }

    void testGetToken() {
        // When
        FacebookApp facebookApp = new FacebookApp(
                id: APP_ID,
                secret: APP_SECRET
        )
        String token = facebookApp.token
        // Then
        assert token == "$APP_ID|$APP_SECRET"

        // When
        token = facebookApp.getToken(true)
        // Then
        assert token == 'new-access-token'
    }

}