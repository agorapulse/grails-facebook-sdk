<div class="page-header">
	<h1>App Example <small>running on Facebook.com</small></h1>
</div>
<div class="row">
	<div class="span12">
		<g:if test="${!appId}">
			<g:render template="/website/configError" />
		</g:if>
		<g:else>
			<!--
			  We use the Facebook JavaScript SDK to provide a richer user experience. For more info,
			  look here: http://github.com/facebook/facebook-js-sdk
			-->
			<facebook:initJS appId="${appId}" />
			
			<g:if test="${!user}">
				<h2 class="tab">Authentication</h2>
				<p>
					Install app via Facebook JavaScript SDK: <facebook:loginLink appPermissions="${appPermissions}" elementClass="large primary btn">Login</facebook:loginLink>
				</p>
			</g:if>
			<g:else>
				<h2 class="tab">Your data</h2>
				<h3>Your profile pic + name</h3>
				<p>
					<img src="https://graph.facebook.com/${user.id}/picture">
					${user.name}
				</p>
				<h3>Your friends</h3>
				<p>
					<g:each in="${userFriends}" var="friend">
						<img src="https://graph.facebook.com/${friend.id}/picture">
					</g:each>
				</p>
			</g:else>
			<p>&nbsp;</p>
		</g:else>
		<h2 class="tab">Public data</h2>
		<h3>Profile pic + name</h3>
		<p>
			<img src="https://graph.facebook.com/benorama/picture">
			${benorama?.name}
		</p>
	</div>
</div>