<g:if test="${appPermissions instanceof List}">
    <g:set var="appPermissions" value="${appPermissions.join(',')}" />
</g:if>
<r:script disposition="footer">
	function FBGrailsSDK_login() {
        FB.login(function(response) {
            if (response.authResponse) {
				// user is logged
				<g:if test="${returnUrl}">
					window.location.href = "${returnUrl}";
				</g:if>
				<g:else>
					window.location.reload();
				</g:else>
			<g:if test="${cancelUrl}">
			} else {
				// user cancelled login
				window.location.href = "${cancelUrl}";
			</g:if>
			}
		}, {scope:"${appPermissions}"});
	}
</r:script>
<a <g:if test="${elementId}">id="${elementId}"</g:if> <g:if test="${elementClass}">class="${elementClass}"</g:if> href="#" onclick="FBGrailsSDK_login();">${body}</a>