package grails.plugin.facebooksdk

class FacebookApp {

	long id = 0
	String permissions = ""
	String secret = ""

    String toString() {
        "FacebookApp(id: $id, permissions: $permissions)"
    }

}