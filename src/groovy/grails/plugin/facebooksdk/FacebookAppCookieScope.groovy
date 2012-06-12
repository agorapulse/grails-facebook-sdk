package grails.plugin.facebooksdk

import javax.servlet.http.Cookie

/**
* Signed request cookie (set by Facebook Javascript SDK)
*/
class FacebookAppCookieScope extends FacebookAppScope {

	void deleteCookie() {
		Cookie cookie = getCookie()
		if (cookie) {
			cookie.setMaxAge(0)
			request.currentResponse.addCookie(cookie)
		}
	}
	
	Cookie getCookie() {
		return request.currentRequest.cookies.find { Cookie it ->
			return it.name == "fbsr_${facebookApp.id}"
		}
	}
	
	String getValue() {
		Cookie cookie = getCookie()
		if (cookie) {
			return cookie.value
		} else {
			return ""
		}
	}
	
	Boolean hasCookie() {
		getCookie() ? true : false
	}
	
}