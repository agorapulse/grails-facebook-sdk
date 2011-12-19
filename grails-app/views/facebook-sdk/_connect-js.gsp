<div id="fb-root"></div>
<script type="text/javascript">
	window.fbAsyncInit = function() {
		FB.init({
			appId   : "${appId}",
			<g:if test="${channelUrl}">channelUrl  : "${channelUrl}",  // Custom channel URL</g:if>
			cookie  : <g:if test="${cookieEnabled}">true</g:if><g:else>false</g:else>, // enable cookies to allow the server to access the session
			oauth : true, // enables OAuth 2.0
			status  : <g:if test="${statusEnabled}">true</g:if><g:else>false</g:else>, // check login status
			xfbml   : <g:if test="${xfbmlEnabled}">true</g:if><g:else>false</g:else> // parse XFBML
		});
		
		<g:if test="${autoGrowthEnabled}">
			FB.Canvas.setAutoGrowth();
		</g:if>
		<g:elseif test="${sizeEnabled}">
			FB.Canvas.setSize();
		</g:elseif>
		
		${body}
	};

	(function() {
		var e = document.createElement("script");
		e.src = document.location.protocol + "//connect.facebook.net/${localeCode}/all.js";
		e.async = true;
		document.getElementById("fb-root").appendChild(e);
	}());
</script>