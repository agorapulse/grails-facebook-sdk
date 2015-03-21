package grails.plugin.facebooksdk

class FacebookContextPage {

    boolean admin = false
    long id = 0
    boolean liked = false

    String toString() {
        "FacebookPage(id: $id, admin: $admin, liked: $liked)"
    }

}
