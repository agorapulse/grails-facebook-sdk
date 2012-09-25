package grails.plugin.facebooksdk

import javax.servlet.http.Cookie

/**
* Signed request cookie (set by Facebook Javascript SDK)
*/
class FacebookCookieScope extends FacebookScope {

	void delete() {
		Cookie currentCookie = getCookie()
        if (currentCookie) {
            if (request.currentRequest.serverName == 'localhost') currentCookie.domain = '.localhost' // For local dev
            currentCookie.maxAge = 0
            currentCookie.path = '/'
			request.currentResponse.addCookie(currentCookie)
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