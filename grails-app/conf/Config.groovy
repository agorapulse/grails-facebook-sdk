// configuration for plugin testing - will not be included in the plugin zip

log4j = {
		appenders {
				console name: "stdout",
								layout: pattern(conversionPattern: "%d{ISO8601} %p %c{1} - %m%n")
				environments {
						production {
								rollingFile name: "myAppender", maxFileSize: 1024,
														file: "/var/tmp/logs/grails-facebook-examples.log"
						}
				}
		}

		error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
					 'org.codehaus.groovy.grails.web.pages', //  GSP
					 'org.codehaus.groovy.grails.web.sitemesh', //  layouts
					 'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
					 'org.codehaus.groovy.grails.web.mapping', // URL mapping
					 'org.codehaus.groovy.grails.commons', // core / classloading
					 'org.codehaus.groovy.grails.plugins', // plugins
					 'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
					 'org.springframework',
					 'org.hibernate',
					 'net.sf.ehcache.hibernate'

		warn   'org.mortbay.log'

		environments {
			development {
				debug "grails.app.controllers.grails.plugins.facebooksdk"
				debug "grails.app.services.grails.plugins.facebooksdk"
			}
		}
}
grails.views.default.codec="none" // none, html, base64
grails.views.gsp.encoding="UTF-8"

// Twitter bootstrap plugin
grails.plugins.twitterbootstrap.fixtaglib = true

// Facebook sdk plugin 
facebook.sdk.app.id = 242034339191134
facebook.sdk.app.permissions = "publish_stream"
facebook.sdk.app.secret = "fa2cbd14c910dfefeb96793f70f51578"