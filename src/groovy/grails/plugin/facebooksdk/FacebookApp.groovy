package grails.plugin.facebooksdk

class FacebookApp {

    long id = 0
    List permissions = []
    String secret = ''

    long getToken() {
        "$id|$secret"
    }

    String toString() {
        "FacebookApp(id: $id, permissions: $permissions)"
    }

}