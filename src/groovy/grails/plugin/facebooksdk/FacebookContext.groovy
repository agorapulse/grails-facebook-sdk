package grails.plugin.facebooksdk

class FacebookContext {

    FacebookApp app
    FacebookAppService facebookAppService
    boolean authenticated
    FacebookSignedRequest signedRequest
    FacebookUser user

    boolean isAuthenticated() {
        user.id ? true : false
    }

    String toString() {
        "FacebookContext(app: $app, authenticated: $authenticated, signedRequest: $signedRequest, user: $user)"
    }

}
