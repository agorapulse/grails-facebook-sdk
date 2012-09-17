package grails.plugin.facebooksdk

import javax.servlet.http.Cookie

/**
* Signed request cookie (set by Facebook Javascript SDK)
*/
class FacebookAppCookieScope extends FacebookAppScope {

	void delete() {
		if (cookie) {
			cookie.setMaxAge(0)
			request.currentResponse.addCookie(cookie)
		}
	}

    String getValue() {
        cookie?.value ?: ''
    }
	
	Cookie getCookie() {
		return request.currentRequest.cookies.find { Cookie it ->
			return it.name == "fbsr_${appId}"
		}
	}
	
}