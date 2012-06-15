Grails Facebook SDK Plugin
==========================

# Introduction

The [Facebook Platform](http://developers.facebook.com/) is a set of APIs that make your application more social. Read more about [integrating Facebook with your web site](http://developers.facebook.com/docs/guides/web) on the Facebook developer site.

This project contains the open source **Grails Facebook SDK Plugin** that allows you to integrate the [Facebook Platform](http://developers.facebook.com/) on a website/app powered by [Grails](http://grails.org).

This plugin is a port of the official [Facebook PHP SDK V3.1.1](http://github.com/facebook/facebook-php-sdk) to [Grails 2.0](http://grails.org).

It supports the latest *OAuth2.0 authentication* (required since October 1st 2011).

**Grails Facebook SDK Plugin** provides the following Grails artefacts:

* **FacebookAppService** - A service to build [apps on Facebook.com](http://developers.facebook.com/docs/guides/canvas/) and [websites with the Facebook Platform](http://developers.facebook.com/docs/guides/web).
* **FacebookGraphClient** - A client to call [Facebook Graph API](http://developers.facebook.com/docs/reference/api/), which is a wrapper around the rock solid [RestFB java library](http://restfb.com/) version 1.6.9 (released October 21, 2011).
* **FacebookJSTagLib** - A collection of tags to easily integrate [Facebook JS SDK](http://developers.facebook.com/docs/reference/javascript/) in your GSPs.


# Installation

Declare the plugin dependency in the BuildConfig.groovvy file, as shown here:

```groovy
grails.project.dependency.resolution = {
		inherits("global") { }
		log "info"
		repositories {
				//your repositories
		}
		dependencies {
				//your regular dependencies
		}
		plugins {
				//here go your plugin dependencies
				runtime ':facebook-sdk:0.3.0'
		}
}
```


# Config

Create a Facebook app on [Facebook Developers](https://developers.facebook.com/apps), in order to get your own app ID and app secret.

Add your Facebook app parameters to your _grails-app/conf/Config.groovy_:

```groovy
grails.plugin.facebooksdk.appId = {APP_ID}
grails.plugin.facebooksdk.appPermissions = {APP_PERMISSIONS}
grails.plugin.facebooksdk.appSecret = {APP_SECRET}
```

# Getting started with a demo app

If you want to quickly run the SDK on a demo app, you can download [Facebook SDK Grails - Demo](https://github.com/benorama/facebook-sdk-grails-demo).


# Integration with Shiro Grails Plugin

To see an example of integration with Shiro Grails Plugin, you can download [Facebook SDK Grails - Shiro demo](https://github.com/benorama/facebook-sdk-grails-demo-shiro).


# Documentation

Project documentation is located here :

* [Reference Documentation (Page per chapter)](http://benorama.github.com/grails-facebook-sdk/guide)
* [Reference Documentation (Single page)](http://benorama.github.com/grails-facebook-sdk/guide/single.html)
* [Groovy API docs](http://benorama.github.com/grails-facebook-sdk/gapi/)

# Latest releases

* 2012-06-12 **V0.3.2** : bug fix in filter and plugin config
* 2012-06-12 **V0.3.1** : package _grails.plugins.facebooksdk_ renamed to _grails.plugin.facebooksdk_
* 2012-06-08 **V0.3.0** : new documentation based on GDoc, FacebookRestClient added + bug fixes


# Bugs

To report any bug, please use the project [Issues](http://github.com/benorama/grails-facebook-sdk/issues) section on GitHub.

# Alpha status

This is an **alpha release**.
The underlying APIs are generally stable, however we may make changes to the library in response to developer feedback.

# Feedback

The **Grails Facebook SDK** is not an official Facebook SDK such as [Javascript](http://developers.facebook.com/docs/reference/javascript/), [PHP](http://github.com/facebook/facebook-php-sdk), [iOS](http://github.com/facebook/facebook-ios-sdk/) and [Android SDKs](http://github.com/facebook/facebook-android-sdk).

It is developped by [AgoraPulse](http://www.agorapulse.com).

The **Grails Facebook SDK** is licensed under the [Apache Licence, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).