package grails.plugin.facebooksdk

class FacebookUser {

    FacebookAppService facebookAppService

    long id = 0

    private String _token

    String getToken() {
        // Token is only retrieved if required
        if (!_token == null){
            _token = facebookAppService.userAccessToken
        }
        _token
    }

    void invalidate() {
        facebookAppService.invalidateUser()
    }

    String toString() {
        "FacebookUser(id: $id)"
    }

}
