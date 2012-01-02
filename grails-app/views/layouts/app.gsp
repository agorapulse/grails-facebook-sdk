<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://www.facebook.com/2008/fbml">
<head>
	<title><g:layoutTitle default="Grails SDK Facebook App Example" /></title>
	<g:layoutHead />
	<r:require modules="bootstrap"/>
	<r:layoutResources />
	<!-- Le HTML5 shim, for IE6-8 support of HTML elements -->
	<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
	<![endif]-->

	<style type="text/css">
		body {
			padding-top: 60px;
		}
	</style>
</head>

<body>

	<div class="topbar">
		<div class="topbar-inner">
			<div class="container-fluid">
				<a class="brand" href="#">Facebook Grails SDK</a>
			</div>
		</div>
	</div>

	<div class="container-fluid">
		<div class="sidebar">
			<g:render template="/website/links" />
		</div>
		<div class="content">
			<g:layoutBody />
			<g:render template="/website/footer" />
		</div>
	</div>

</body>
</html>