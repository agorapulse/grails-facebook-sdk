grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {
	inherits 'global'
	log 'warn'
	repositories {
		grailsCentral()
		mavenCentral()
	}
	dependencies {
		compile 'com.restfb:restfb:1.6.9'
	}
	plugins {
		build(':release:latest.integration') {
			export = false
		}
	}
}