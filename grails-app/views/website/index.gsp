<div class="page-header">
	<h1>Website example <small>with Facebook Platform integration</small></h1>
</div>
<div class="row">
	<div class="span10">
		<g:if test="${!appId}">
			<g:render template="/website/configError" />
		</g:if>
		<g:else>
			<!--
			  We use the Facebook JavaScript SDK to provide a richer user experience. For more info,
			  look here: http://github.com/facebook/facebook-js-sdk
			-->
			<facebook:initJS appId="${appId}" />
			
			<h2>Authentication</h2>
			<g:if test="${user}">
				<p>
					Log out via Facebook JavaScript SDK: <facebook:logoutLink elementClass="btn" nextURL="${createLink(action:'logout')}">Logout</facebook:logoutLink>
				</p>
				<!-- <p>
					Log out Facebook.com server side redirect:
					<a href="${logoutURL}" class="btn">
						Logout
					</a>
				</p> -->
			</g:if>
			<g:else>
				<p>
					Log in via Facebook JavaScript SDK: <facebook:loginLink appPermissions="${appPermissions}" elementClass="large primary btn">Login</facebook:loginLink><br />
					(<i>with Facebook Grails SDK handling authorization code from cookie on reload</i>)
				</p>
				<!-- <p>
					Log in via Facebook.com server side redirect:
					<a href="${loginURL} }" class="large btn">Login</a><br />
					(<i>with Facebook Grails SDK handling authorization code from url on return</i>)
				</p> -->
			</g:else>
			<hr />
			<g:if test="${user}">
				<h2>Your data</h2>
				<h3>Your profile pic + name</h3>
				<p>
					<img src="https://graph.facebook.com/${user.id}/picture">
					${user.name}<br />
				</p>
				<h3>Your friends</h3>
				<p>
					<g:each in="${userFriends}" var="friend">
						<img src="https://graph.facebook.com/${friend.id}/picture">
					</g:each>
				</p>
			</g:if>
			<g:else>
				<strong><em>You are not Connected.</em></strong>
			</g:else>
		</g:else>
		<hr />
		<h2>Public data</h2>
		<h3>Profile pic + name</h3>
		<p>
			<img src="https://graph.facebook.com/benorama/picture">
			${benorama?.name}
		</p>
	</div>
	<div class="span4">
		<g:render template="links" />
	</div>
</div>