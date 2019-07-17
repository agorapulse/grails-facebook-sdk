package grails.plugin.facebooksdk

import com.restfb.FacebookClient
import groovy.util.logging.Slf4j

@Slf4j
class FacebookContextApp {

    FacebookContext context

    long id = 0
    String data = ''
    List permissions = []
    String secret = ''

    private _token = null

    /*
     * @description Get application OAuth accessToken
     * @hint
     */
    String getToken(boolean oauthEnabled = false) {
        if (oauthEnabled) {
            if (_token == null) {
                FacebookClient.AccessToken result =  context.facebookGraphClientService.newClient(token).obtainAppAccessToken(id as String, secret)
                if (result?.accessToken) _token = result.accessToken
            }
            return _token
        } else {
            return "$id|$secret"
        }
    }

    String toString() {
        "FacebookApp(id: $id, permissions: $permissions, data: $data)"
    }

}