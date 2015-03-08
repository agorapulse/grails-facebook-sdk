package grails.plugin.facebooksdk

import spock.lang.Specification

class FacebookContextAppSpec extends Specification {

    static APP_ID = 123456789
    static APP_SECRET = 'abcdefghhijkl'

    FacebookContext context

    void setup() {
        FacebookGraphClient.metaClass.fetchObject = { String object, Map parameters ->
            switch(object) {
                case 'oauth/access_token':
                    return [access_token:'new-access-token', expires:1000]
                    break
            }
        }

        // Mock context
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

    void "Get token"() {
        when:
        FacebookContextApp facebookContextApp = new FacebookContextApp(
                id: APP_ID,
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
    }

}