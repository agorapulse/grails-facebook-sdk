<script type="text/javascript">
	function FacebookSDK_login() {
		FB.login(function(response) {
			if (response.authResponse) {
				// user is logged
				<g:if test="${returnURL}">
					window.location.href = "${returnURL}";
				</g:if>
				<g:else>
					window.location.reload();
				</g:else>
			<g:if test="${cancelURL}">
			} else {
				// user cancelled login
				window.location.href = "${cancelURL}";
			</g:if>
			}
		}, {scope:"${appPermissions}"});
	}
</script>
<a <g:if test="${elementId}">id="${elementId}"</g:if> <g:if test="${elementClass}">class="${elementClass}"</g:if> href="#" onclick="FacebookSDK_login();">${body}</a>