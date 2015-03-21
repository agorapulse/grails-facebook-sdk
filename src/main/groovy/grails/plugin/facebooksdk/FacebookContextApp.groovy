package grails.plugin.facebooksdk

import org.apache.log4j.Logger

class FacebookContextApp {

    FacebookContext context

    long id = 0
    String data = ''
    List permissions = []
    String secret = ''

    private _token = null
    private Logger log = Logger.getLogger(getClass())

    /*
     * @description Get application OAuth accessToken
     * @hint
     */
    String getToken(boolean oauthEnabled = false) {
        if (oauthEnabled) {
            if (_token == null) {
                def result = graphClient.fetchObject('oauth/access_token', [
                        client_id: id,
                        client_secret: secret,
                        grant_type: 'client_credentials'
                ])
                if (result['access_token']) _token = result['access_token']
            }
            return _token
        } else {
            return "$id|$secret"
        }
    }

    String toString() {
        "FacebookApp(id: $id, permissions: $permissions, data: $data)"
    }

    // PRIVATE

    protected FacebookGraphClient getGraphClient(String token = '') {
        new FacebookGraphClient(
                token,
                context?.config?.apiVersion ?: FacebookGraphClient.DEFAULT_API_VERSION,
                context?.config?.timeout ?: FacebookGraphClient.DEFAULT_READ_TIMEOUT_IN_MS,
                context?.config?.proxyHost ?: null,
                context?.config?.proxyPort ?: null
        )
    }

}