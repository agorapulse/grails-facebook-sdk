Facebook Grails SDK Plugin
================================

# Introduction

The [Facebook Platform](http://developers.facebook.com/) is a set of APIs that make your application more social. Read more about [integrating Facebook with your web site](http://developers.facebook.com/docs/guides/web) on the Facebook developer site. 

This project contains the open source **Facebook Grails SDK Plugin** that allows you to integrate the [Facebook Platform](http://developers.facebook.com/) on a website/app powered by [Grails](http://grails.org).

This plugin is a port to [Grails 2.0](http://grails.org) of the official [Facebook PHP SDK V3.1.1](http://github.com/facebook/facebook-php-sdk).
It supports the latest *OAuth2.0 authentication* (required since October 1st 2011).

**Facebook Grails SDK Plugin** provides 2 artefacts :

* **FacebookAppService** - A service to build [apps on Facebook.com](http://developers.facebook.com/docs/guides/canvas/) and [websites with the Facebook Platform](http://developers.facebook.com/docs/guides/web).
* **FacebookJSTagLib** - A collection of tags to easily integrate [Facebook JS SDK](http://developers.facebook.com/docs/reference/javascript/) in your GSPs.

Under the cover, it uses the excellent [RestDB java library](http://restfb.com/) version 1.6.9 (released October 21 2011) for all the _Facebook Graph API_ calls.


# Getting started

If you want to quickly run the SDK.

**1- Download or clone the [plugin GitHub project](https://github.com/benorama/facebook-grails-sdk).**

```groovy
git clone https://benorama@github.com/benorama/facebook-grails-sdk.git
```

**2- Create a Facebook app on [Facebook Developers](https://developers.facebook.com/apps), in order to get your own app ID and app secret.**

Configure your Facebook app as below:

* *App name space* = my-app-name-space
* *Website URL* = http://localhost:8080/facebook-sdk/website/
* *App on Facebook* = http://localhost:8080/facebook-sdk/app/
* *sandbox mode* = enabled (in Advanced setting, to be able to use the app on Facebook without SSL certificate)

**3- Add your Facebook app parameters to _grails-app/conf/Config.groovy_.**

```groovy
facebook.sdk.app.id = {APP_ID}
facebook.sdk.app.permissions = {APP_PERMISSIONS}
facebook.sdk.app.secret = {APP_SECRET}
```

**4-Run the app from the project root.** 

```groovy
grails run-app
```

Browse to :

* <http://localhost:8080/facebook-sdk/website/> for the website example
* <http://apps.facebook.com/my-app-name-space> for the app example on Facebook


# Plugin Installation

Run the install-plugin script from your project root.

```groovy
grails install-plugin facebook-sdk
```

**WARNING: since, this plugin is not yet released on Grails.org, you have to manually package and install the plugin...**


# Plugin Config 

Create a Facebook app on [Facebook Developers](https://developers.facebook.com/apps), in order to get your own app ID and app secret.

Add your Facebook app parameters to your _grails-app/conf/Config.groovy_:

```groovy
facebook.sdk.app.id = {APP_ID}
facebook.sdk.app.permissions = {APP_PERMISSIONS}
facebook.sdk.app.secret = {APP_SECRET}
```


# Facebook App Service Usage

Reference *facebookAppService* from any of your grails artefacts (controllers, domain, services...) to automatically inject it.

```groovy
def facebookAppService
```

## User Id

To check if current user has authorized your app and is authenticated, get `userId` from *facebookAppService*.
It will return `0` if user is not authenticated (or if he has not authorized your app).

```groovy
def userId = facebookAppService.getUserId()
if (userId) {
  println "User authenticated (id=${userId})"
} else {
  println "User not authenticated"
}
```

## User Access Token

If current user is authenticated, you can get his private `accessToken` from *facebookAppService*.

If the app is running on a website, the access token will be automatically fetched in exchange for the authorization code returned by [Facebook JS SDK](http://developers.facebook.com/docs/reference/javascript/) signed request cookie. It is recommended to wrap this code in a try/catch block since external HTTP calls to _Facebook Graph API_ might be executed.

If the app is running on Facebook.com, the access token will be extracted from the signed request params (passed through the canvas iFrame).

```groovy
try {
  String userAccessToken = facebookAppService.getUserAccessToken()
} catch (Exception exception) {
  // Something went wrong...
}
```

By default, after initial request, access token is stored in session scope for better performance: another reason to surround all your _Facebook Graph API_ calls in try/catch in order to catch expired/invalid access tokens.

## Controller integration

An easy way to integrate the Facebook SDK is to use a `beforeInterceptor` in your _controller_.

```groovy
def beforeInterceptor =  {
  request.appId = facebookAppService.appId
  request.appPermissions = facebookAppService.appPermissions
  request.userId = facebookAppService.userId
}
```

You might also use a global _Filter_.

## Graph API

Since *RestFB* java libray comes bundled with the plugin, you can use it to make _Facebook Graph API_ calls.

```groovy
import com.restfb.Connection
import com.restfb.DefaultFacebookClient
import com.restfb.Parameter
import com.restfb.types.User

def userId = facebookAppService.userId
if (userId) {
  try {
    DefaultFacebookClient facebookClient = new DefaultFacebookClient(facebookAppService.userAccessToken)
    User user = facebookClient.fetchObject(userId.toString(), User)
    Connection userFriendsConnection = facebookClient.fetchConnection("${userId}/friends", User, Parameter.with("limit", 10))
    List userFriends = userFriendsConnection ? userFriendsConnection.data : []
  } catch (Exception exception) {
    // Something went wrong...
  }
}
```

For more information, see [RestFB documentation](http://restfb.com).


# Facebook JS Taglib usage

Current tag lib has 3 tags (but other are coming soon...).

## facebook:init

To initialize [Facebook JS SDK](http://developers.facebook.com/docs/reference/javascript/) in your GSP views, simply insert `initJS` tag, after HTML body tag.
The only required attribute is `appId`.

```html
<facebook:initJS appId="${appId}" />
```

Optional attributes are : 

* `autoGrowth` Call FB.setAutoGroth() after page rendering (default to `false`)
* `channelUrl` Channel File
* `cookie` Enable cookies to allow the server to access the session (default to `true`)
* `locale` Define JS SDK locale (default to server locale)
* `status` Check login status (default to `false`)
* `xfbml` Parse XFBML (default to `false`)

## facebook:loginLink

For the user to connect/install your app, use `loginLink` tag.

```html
<facebook:loginLink appPermissions="${appPermissions}">Login</facebook:loginLink>
```

Optional attributes are :

* `appPermissions` Facebook app permissions/scope
* `cancelURL` Cancel URL for redirect if login is canceled (if not defined, nothing happens)
* `elementClass` HTML element 'class' attribute value
* `elementId` HTML element 'id' attribute value
* `returnURL` Return URL for redirect after login (if not defined page will be reloaded)

You might also use Facebook JS SDK [Login button](http://developers.facebook.com/docs/reference/plugins/login/) (but do not forget to set `xfbml` attributes to true in `facebook:init` tag).

```html
<html xmlns:fb="http://ogp.me/ns/fb#">
...
<fb:login-button scope="${appPermissions}"></fb:login-button>
```

## facebook:logoutLink

For the user to logout from your app and Facebook.com, use `logoutLink` tag.

```html
<facebook:logoutLink>Logout</facebook:loginLink>
```

Optional attributes are :

* `elementClass` HTML element 'class' attribute value
* `elementId` HTML element 'id' attribute value
* `nextURL` Next URL for redirect after logout (if not defined page will be reloaded)

# Bugs

To report any bug, please use the project [Issues](http://github.com/benorama/facebook-grails-sdk/issues) section on GitHub.

# Alpha status

This is an **alpha release**.
The underlying APIs are generally stable, however we may make changes to the library in response to developer feedback.

# Feedback

The **Facebook Grails SDK Plugin** is not an official Facebook SDK such as [Javascript](http://developers.facebook.com/docs/reference/javascript/), [PHP](http://github.com/facebook/facebook-php-sdk), [iOS](http://github.com/facebook/facebook-ios-sdk/) and [Android SDKs](http://github.com/facebook/facebook-android-sdk).

It is developped by [Affinitiz](http://poweredby.affinitiz.com), a french social media web agency.

The **Facebook Grails SDK** is licensed under the [Apache Licence, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).