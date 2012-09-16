package grails.plugin.facebooksdk

class FacebookUser {

    FacebookAppService facebookAppService

    long id = 0

    long getToken() {
        facebookAppService.userAccessToken
    }

    void invalidate() {
        // TODO invalidate user
    }

    String toString() {
        "FacebookUser(id: $id)"
    }

}
