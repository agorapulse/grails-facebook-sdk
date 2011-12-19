package grails.plugins.facebooksdk.scope

import javax.servlet.http.Cookie

/**
* Signed request cookie (set by Facebook Javascript SDK)
*/

class FacebookAppCookieScope extends FacebookAppScope {
	
	void deleteCookie() {
		Cookie cookie = getCookie()
		if (cookie) {
			cookie.setMaxAge(0)
			request.getCurrentResponse().addCookie(cookie)
		}
	}
	
	Cookie getCookie() {
		Cookie appCookie
		for (Cookie cookie in request.getCurrentRequest().getCookies()) {
			if (cookie.name == getAppCookieName()) {
				appCookie = cookie
				break
			}
		}
		return appCookie
	}
	
	String getData() {
		Cookie cookie = getCookie()
		if (cookie) {
			return cookie.value
		} else {
			return [:]
		}
	}
	
	Boolean hasCookie() {
		return getCookie() ? true : false
	}
	
	// PRIVATE

	private String getAppCookieName(String appId) {
		if (!this.appId) {
			throw new Exception("AppId must be defined")
		}
		return "fbsr_${this.appId}"
	}
	
}
