grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.dependency.resolution = {
	// inherit Grails' default dependencies
	inherits("global") {
		// uncomment to disable ehcache
		// excludes 'ehcache'
	}
	log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
	repositories {
		grailsCentral()
	}
	dependencies {
		compile "com.restfb:restfb:1.6.9"
	}
	plugins {
		compile ":resources:latest.integration"
		build(":tomcat:$grailsVersion",
			  ":release:latest.integration") {
			export = false
		}
		runtime ":fbootstrapp:0.1.1"
	}
}