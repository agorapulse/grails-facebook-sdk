Facebook Grails SDK Plugin
================================

# Introduction

The [Facebook Platform](http://developers.facebook.com/) is a set of APIs that make your application more social. Read more about [integrating Facebook with your web site](http://developers.facebook.com/docs/guides/web) on the Facebook developer site. 

This project contains the open source **Facebook Grails SDK Plugin** that allows you to integrate the [Facebook Platform](http://developers.facebook.com/) on a website/app powered by [Grails](http://grails.org).

This plugin is a port to [Grails 2.0](http://grails.org) of the official [Facebook PHP SDK V3.1.1](http://github.com/facebook/facebook-php-sdk).
It supports the latest *OAuth2.0 authentication* (required since October 1st 2011).

**Facebook Grails SDK Plugin** provides :

* **FacebookAppService** - A service to build [apps on Facebook.com](http://developers.facebook.com/docs/guides/canvas/) and [websites with the Facebook Platform](http://developers.facebook.com/docs/guides/web).
* **FacebookJSTagLib** - A collection of tags to easily integrate [Facebook JS SDK](http://developers.facebook.com/docs/reference/javascript/) in your GSPs.
* **FacebookGraphClient** - A client to call [Facebook Graph API](http://developers.facebook.com/docs/reference/api/), which is a wrapper around the rock solid [RestFB java library](http://restfb.com/) version 1.6.9.


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
grails.plugins.facebooksdk.appId = {APP_ID}
grails.plugins.facebooksdk.appPermissions = {APP_PERMISSIONS}
grails.plugins.facebooksdk.appSecret = {APP_SECRET}
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

But since Grails 1.3.x, it is recommanded to declare the plugin dependency in the BuildConfig.groovvy file, as shown here:

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
        runtime ':facebook-sdk:0.1.1'
    }
}
```

**WARNING: since, this plugin is not yet released on Grails.org, you have to manually package and install the plugin...**


# Plugin Config 

Create a Facebook app on [Facebook Developers](https://developers.facebook.com/apps), in order to get your own app ID and app secret.

Add your Facebook app parameters to your _grails-app/conf/Config.groovy_:

```groovy
grails.plugins.facebooksdk.appId = {APP_ID}
grails.plugins.facebooksdk.appPermissions = {APP_PERMISSIONS}
grails.plugins.facebooksdk.appSecret = {APP_SECRET}
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

To perform Facebook Graph API call, use the _FacebookGraphClient_ without access token for public data or with an access token for private data.
_FacebookGraphClient_ is a thin groovy wrapper around the rock solid [RestFB java library](http://restfb.com/).
It will return JSON-based graph objects.

To play with the API, you might use the grails console from your project root.
```groovy
grails console
```

**Initialization**
```groovy
import grails.plugins.facebooksdk.FacebookGraphClient
// For public data
def facebookClient = new FacebookGraphClient()
// For private data (access token required)
def userAccessToken = facebookAppService.getUserAccessToken()
def facebookClient = new FacebookGraphClient(userAccessToken)
```

**Fetching Single Objects**
```groovy
def user = facebookClient.fetchObject("me") // Requires a user access token
def page = facebookClient.fetchObject("cocacola")
println "User name: " + user.name
println "Page likes: " + page.likes
```

**Fetching Multiple Objects in One Call**
```groovy
def fetchObjectsResults = facebookClient.fetchObjects(["me", "cocacola"])
println "User name: " + fetchObjectsResults["me"].name
println "Page likes: " + fetchObjectsResults["cocacola"].likes
```

**Fetching Connections**
```groovy
def myFriends = facebookClient.fetchConnection("me/friends")
def myFeed = facebookClient.fetchConnection("me/feed")
println "Count of my friends: " + myFriends.size()
println "First item in my feed: " + myFeed[0]
```

**Searching**
```groovy
// Searching is just a special case of fetching Connections -
// all you have to do is pass along a few extra parameters.
def publicSearch = facebookClient.fetchConnection("search", [q:"watermelon", type:"post"])
println "Public search: " + publicSearch[0].message
// Targeted search
def targetedSearch = facebookClient.fetchConnection("me/home", [q:"Mark", type:"user"])
println "Posts on my wall by friends named Mark: " + targetedSearch.size()
```

**Fetching Insights**
```groovy
// Fetching Insights data is as simple as fetching a Connection
def insights = facebookClient.fetchConnection("PAGE_ID/insights")
for (insight in insights) println insight.name
```

**Executing FQL Queries**
```groovy
String query = "SELECT uid, name FROM user WHERE uid=220439 or uid=7901103"
def users = facebookClient.executeQuery(query)
println "Users: " + users
```

**Executing Multiple FQL Queries in One Call**
```groovy
Map queries = [users:"SELECT uid, name FROM user WHERE uid=220439 OR uid=7901103", likers:"SELECT user_id FROM like WHERE object_id=122788341354"]
multiqueryResults = facebookClient.executeMultiquery(queries)
println "Users: " + multiqueryResults.users
println "People who liked: " + multiqueryResults.likers
```

**Metadata/Introspection**
```groovy
// You can specify metadata=1 for many calls, not just this one.
// See the Facebook Graph API documentation for more details. 
def userWithMetadata = facebookClient.fetchObject("me", [metadata:1])
println "User connections  " + userWithMetadata.metadata.connections
```

**Passing Parameters**
```groovy
// You can pass along any parameters you'd like to the Facebook endpoint.
Date oneWeekAgo = new Date() - 7
def filteredFeed = facebookClient.fetchConnection("me/feed", [limit:3, until:"yesterday", since:oneWeekAgo])
println "Filtered feed count: " + filteredFeed.size()
```

**Selecting Specific Fields**
```groovy
def user = facebookClient.fetchObject("me", [fields:"id, name"])
println "User name: " + user.name
```

**Publishing a Message and Event**
```groovy
// Publishing a simple message.
def publishMessageResponse = facebookClient.publish("me/feed", [message:"RestFB test"])
println "Published message ID: " + publishMessageResponse.id

// Publishing an event
Date tomorrow = new Date() + 1
Date twoDaysFromNow = new Date() + 2
def publishEventResponse = facebookClient.publish("me/events", [name:"Party", start_time:tomorrow, end_time:twoDaysFromNow])
println "Published event ID: " + publishEventResponse.id
```

**Publishing a Photo or a Video**
```groovy
// Publishing an image to a photo album is easy!
// Just specify the image you'd like to upload and RestFB will handle it from there.
def publishPhotoResponse = facebookClient.publishFile("me/photos", [message, "Test cat"], "/cat.png")
println "Published photo ID: " + publishPhotoResponse.id
// Publishing a video works the same way.
facebookClient.publish("me/videos", [message, "Test cat"], "/cat.mov")
```

**Deleting**
```groovy
Boolean deleted = facebookClient.deleteObject("some object ID")
out.println("Deleted object? " + deleted)
```

**Using the Batch Request API**
```groovy
List batchResponses = facebookClient.executeBatch(["me", "m83music/feed"]);
// Responses are ordered to match up with their corresponding requests.
println "Me object " + batchResponses[0]
println "M83 feed " + batchResponses[1]
```

**Error Handling**

All _FacebookClient_ methods may throw _com.restfb.exception.FacebookException_, which is an unchecked exception as of RestFB 1.6.

These are the _FacebookException_ subclasses that you may catch:

* _FacebookJsonMappingException_
* _FacebookNetworkException_ 
* _FacebookGraphException_ 
* _FacebookOAuthException_ 
* _FacebookQueryParseException_ 
* _FacebookResponseStatusException_ 

For more info, check [RestFB java library](http://restfb.com/) documentation.

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