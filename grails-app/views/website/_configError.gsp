<div class="alert-message block-message  error">
	<h2>Incorrect Facebook Application configuration</h2>
	<p>
		Your application is not yet configured, you must create an application on <a href="https://developers.facebook.com/apps">Facebook Developers</a>, in order to get your own app ID and a secret key.
	</p> 
	<p>
		Add your setting in <i>Config.groovy</i>:<br />
		<ul>
			<li>facebook.sdk.app.id = {APP_ID}</li>
			<li>facebook.sdk.app.permissions = {APP_PERMISSIONS}</li>
			<li>facebook.sdk.app.secret = {APP_SECRET}</li>
		</ul>
	</p>
	<p>
		For more info, see <a href="http://github.com/affinitiz/facebook-grails-sdk/wiki/Usage">SDK Wiki</a> on GitHub.
	</p>
</div>