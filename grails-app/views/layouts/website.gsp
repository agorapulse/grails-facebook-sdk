<!DOCTYPE html>
<html xmlns:fb="http://ogp.me/ns/fb#">
<head>
	<title><g:layoutTitle default="Facebook Grails SDK Website Example" /></title>
	<g:layoutHead />
	<r:require module="website"/>
	<r:layoutResources />
	<!-- Le HTML5 shim, for IE6-8 support of HTML elements -->
	<!--[if lt IE 9]>
	  <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
	<![endif]-->
  </head>

<body>
	<div class="topbar">
		<div class="fill">
			<div class="container">
				<a class="brand" href="#">Facebook Grails SDK</a>
			</div>
		</div>
	</div>

	<div class="container">
		<div class="content">
			<g:layoutBody />
		</div>
		<g:render template="/website/footer" />
	</div>
	<r:layoutResources />
</body>
</html>