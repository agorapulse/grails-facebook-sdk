package grails.plugin.facebooksdk

import grails.test.GrailsUnitTestCase
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.junit.Before

class FacebookContextAppTests extends GrailsUnitTestCase {

    static APP_ID = 123456789
    static APP_SECRET = 'abcdefghhijkl'

    FacebookContext context

    @Before
    void setUp() {
        FacebookGraphClient.metaClass.fetchObject = { String object, Map parameters ->
            switch(object) {
                case 'oauth/access_token':
                    return [access_token:'new-access-token', expires:1000]
                    break
            }
        }
    }

    @Before
    void mockContext() {
        def config = mockConfig('''
            grails {
                plugin {
                    facebooksdk {}
                }
            }
            ''')
        context = mockFor(FacebookContext, true).createMock()
        context.grailsApplication = [config: config]
    }

    void testGetToken() {
        // When
        FacebookContextApp facebookContextApp = new FacebookContextApp(
                id: APP_ID,
                context: context,
                secret: APP_SECRET
        )
        String token = facebookContextApp.token
        // Then
        assert token == "$APP_ID|$APP_SECRET"

        // When
        token = facebookContextApp.getToken(true)
        // Then
        assert token == 'new-access-token'
    }



}