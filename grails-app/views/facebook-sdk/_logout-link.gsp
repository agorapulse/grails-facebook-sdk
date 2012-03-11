<script type="text/javascript">
	function FBGrailsSDK_logout() {
		FB.getLoginStatus(function(response) {
			if (response.authResponse) {
		    	FB.logout(function(response) {
					<g:if test="${nextUrl}">
						window.location.href = "${nextUrl}";
					</g:if>
					<g:else>
						window.location.reload();
					</g:else>
				});
		  	} else {
		  		<g:if test="${nextUrl}">
					window.location.href = "${nextUrl}";
				</g:if>
				<g:else>
					window.location.reload();
				</g:else>
		  	}
		});
	}
</script>
<a <g:if test="${elementId}">id="${elementId}"</g:if> <g:if test="${elementClass}">class="${elementClass}"</g:if> href="#" onclick="FBGrailsSDK_logout();">${body}</a>