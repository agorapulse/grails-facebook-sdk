<div class="content">
	<div class="page-header">
		<h1>Facebook Grails SDK <small>Website with Facebook Platform integration</small></h1>
	</div>
	<div class="row">
		<div class="span10">
			<g:if test="${!appId}">
				<div style="color:red">
					<h2>Incorrect Facebook Application configuration</h2>
					Your application is not yet configured, you must create an application on <a href="https://developers.facebook.com/apps">Facebook Developers</a>, in order to get your own app ID and a secret key.<br /> 
					Replace <i>appId</i> and <i>secretKey</i> in <i>examples/website/index.cfm</i>.<br />
					For more info, see SDK <a href="http://github.com/affinitiz/facebook-grails-sdk/wiki/Usage">Usage</a> documentation.<br />
				</div>
				<br />
			</g:if>
			<g:else>
				<!--
				  We use the Facebook JavaScript SDK to provide a richer user experience. For more info,
				  look here: http://github.com/facebook/connect-js
				-->
				<facebook:connectJS appId="${appId}" />
				
				<h2>Authentication</h2>
				<g:if test="${user}">
					<div>
						Log out via Facebook JavaScript SDK: <facebook:logoutLink nextURL="${createLink(action:'logout')}">Logout</facebook:logoutLink>
					</div>
					<br />
					<div>
						Log out Facebook.com server side redirect:
						<a href="${logoutURL}">
							<img src="http://static.ak.fbcdn.net/rsrc.php/z2Y31/hash/cxrz4k7j.gif">
						</a>
					</div>
				</g:if>
				<g:else>
					<div>
						Log in via Facebook JavaScript SDK: <facebook:loginLink appPermissions="${appPermissions}">Login</facebook:loginLink><br />
						(<i>with Facebook Grails SDK handling authorization code from cookie on reload</i>)
					</div>
					<br />
					<div>
						Log in via Facebook.com server side redirect:
						<a href="${loginURL} }">
							<img src="http://static.ak.fbcdn.net/rsrc.php/zB6N8/hash/4li2k73z.gif">
						</a><br />
						(<i>with Facebook Grails SDK handling authorization code from url on return</i>)
					</div>
				</g:else>
				<hr />
				<h2>Your data</h2>
				<g:if test="${user}">
					<h3>Your profile pic + name</h3>
					<img src="https://graph.facebook.com/${user.id}/picture">
					${user.name}<br />
					<br />
					<h3>Your friends</h3>
					<g:each in="${userFriends}" var="friend">
						<img src="https://graph.facebook.com/${friend.id}/picture">
					</g:each><br />
					<br />
					<h3>Your info</h3>
				</g:if>
				<g:else>
					<strong><em>You are not Connected.</em></strong>
				</g:else>
			</g:else>
			<hr />
			<h2>Naitik data</h2>
			<h3>Profile pic + name</h3>
			<img src="https://graph.facebook.com/naitik/picture">
			${naitik?.name}
		</div>
		<div class="span4">
			<h3>Secondary content</h3>
		</div>
	</div>
</div>