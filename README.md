Facebook SDK Grails Plugin
==========================

[![Build Status](https://travis-ci.org/agorapulse/grails-facebook-sdk.svg)](https://travis-ci.org/agorapulse/grails-facebook-sdk)

# Introduction

The [Facebook Platform](http://developers.facebook.com/) is a set of APIs that make your application more social. Read more about [integrating Facebook with your web site](http://developers.facebook.com/docs/guides/web) on the Facebook developer site.

This project contains the open source **Grails Facebook SDK Plugin** that allows you to integrate the [Facebook Platform](http://developers.facebook.com/) on a website/app powered by [Grails](http://grails.org).

This plugin is a port of the official [Facebook PHP SDK](http://github.com/facebook/facebook-php-sdk) to [Grails 3.0](http://grails.org).

It supports the latest *OAuth2.0 authentication* (required since October 1st 2011).

**Grails Facebook SDK Plugin** provides the following Grails artefacts:

* **FacebookContext** - A Spring bean to get current Facebook context in controllers, when running [apps on Facebook.com](http://developers.facebook.com/docs/guides/canvas/) or [websites with the Facebook Platform](http://developers.facebook.com/docs/guides/web).
* **FacebookGraphClient** - A client to call [Facebook Graph API](http://developers.facebook.com/docs/reference/api/), which is a wrapper around the rock solid [RestFB java library](http://restfb.com/) version 1.32.0 (released October 13, 2016).
* **FacebookJSTagLib** - A collection of tags to easily integrate [Facebook JS SDK](http://developers.facebook.com/docs/reference/javascript/) in your GSPs.

**WARNING**: Facebook API v3.0 is now used by default.

# Installation

Declare the plugin dependency in the _build.gradle_ file, as shown here:

```groovy
dependencies {
    ...
    compile "org.grails.plugins:facebook-sdk:4:0.0"
    ...
}
```


# Config

Create a Facebook app on [Facebook Developers](https://developers.facebook.com/apps), in order to get your own app ID and app secret.

Add your Facebook app parameters to your _grails-app/conf/application.yml_:

```yml
grails:
    plugin:
        facebooksdk:
            app:
                id: {APP_ID}
                permissions: {APP_PERMISSIONS} // Ex. ['email','user_photos']
                secret: {APP_SECRET}
```

By default, latest Graph API v3.3 will be used.
You can override default settings with `apiVersion` setting:

```yml
grails:
    plugin:
        facebooksdk:
            apiVersion: v2.3
```

Since FacebookContext should be instantiated at each request, you must use `prototype` scope for your Controllers (since Grails 2.3, `singleton` is the default scope).

```yml
grails:
    controllers:
        defaultScope: prototype
```

Default jQuery selector is `$`, if you require another one, you can define it globally in your _grails-app/conf/application.groovy_:

```yml
grails:
    plugin:
        facebooksdk:
            customSelector: jQuery
```

# Bugs

To report any bug, please use the project [Issues](http://github.com/agorapulse/grails-facebook-sdk/issues) section on GitHub.

# Feedback

The **Grails Facebook SDK** is not an official Facebook SDK such as [Javascript](http://developers.facebook.com/docs/reference/javascript/), [PHP](http://github.com/facebook/facebook-php-sdk), [iOS](http://github.com/facebook/facebook-ios-sdk/) and [Android SDKs](http://github.com/facebook/facebook-android-sdk).

It is developed by [AgoraPulse](http://www.agorapulse.com).

The **Grails Facebook SDK** is licensed under the [Apache Licence, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

# Breaking Changes
## 4.0.1

 * Default return type of `fetchObject` from `FacebookExtensions` is back to `JsonObject` instead of `Map`

## 4.0.0

 * restfb upgrade - see https://github.com/restfb/restfb/blob/master/migrations.md
   * removed `FacebookRestClient` and `DefaultFacebookRestClient`
   * avoid using raw `JsonObject` as the class has completely changed i.g. `JsonObject` from restfb 2.x implements own method `asBoolean()` which fails if the value is not `boolean` so you need to do regular `foo != null` check for testing the presense of the returned `JsonObject` value
 * `FacebookGraphClient` is now deprecated, use `FacebookGraphClientService` to create instances of `FacebookClient` with similar capabilities
 * `FacebookExtensions` the most useful methods from `FacebookGraphClient`are now injected using extensions methods to `FacebookClient` interface directly 
