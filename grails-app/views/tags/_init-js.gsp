<%@page expressionCodec="raw" %>
<div id="fb-root"></div>
<script type="text/javascript">
	window.fbAsyncInit = function() {
		FB.init({
			appId: "${appId}",
			<g:if test="${channelUrl}">channelUrl: "${channelUrl}",  // Custom channel URL</g:if>
			cookie: <g:if test="${cookie}">true</g:if><g:else>false</g:else>, // enable cookies to allow the server to access the session
			oauth: true, // enables OAuth 2.0
			status: <g:if test="${status}">true</g:if><g:else>false</g:else>, // check login status
			xfbml: <g:if test="${xfbml}">true</g:if><g:else>false</g:else>, // parse XFBML
			frictionlessRequests: <g:if test="${frictionlessRequests}">true</g:if><g:else>false</g:else> // to enable frictionless requests
		});

		<g:if test="${autoGrow}">
			FB.Canvas.setAutoGrow();
		</g:if>

		${body}
	};

	(function() {
		var e = document.createElement("script");
		e.src = document.location.protocol + "//connect.facebook.net/${locale}/all.js";
		e.async = true;
		document.getElementById("fb-root").appendChild(e);
	}());
</script>

