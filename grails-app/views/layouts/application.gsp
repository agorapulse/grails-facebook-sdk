<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://www.facebook.com/2008/fbml">
<head>
	<title><g:layoutTitle default="Grails SDK Demo" /></title>
	<%-- <link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'website.css')}" /> --%>
	<g:layoutHead />
	<r:require modules="bootstrap"/>
	<r:layoutResources />
	<!-- Le HTML5 shim, for IE6-8 support of HTML elements -->
	<!--[if lt IE 9]>
	  <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
	<![endif]-->

	<!-- Le styles -->
	<style type="text/css">
	  /* Override some defaults */
	  html, body {
		background-color: #eee;
	  }
	  body {
		padding-top: 40px; /* 40px to make the container go all the way to the bottom of the topbar */
	  }
	  .container > footer p {
		text-align: center; /* center align it with the container */
	  }
	  .container {
		width: 820px; /* downsize our container to make the content feel a bit tighter and more cohesive. NOTE: this removes two full columns from the grid, meaning you only go to 14 columns and not 16. */
	  }

	  /* The white background content wrapper */
	  .container > .content {
		background-color: #fff;
		padding: 20px;
		margin: 0 -20px; /* negative indent the amount of the padding to maintain the grid system */
		-webkit-border-radius: 0 0 6px 6px;
		   -moz-border-radius: 0 0 6px 6px;
				border-radius: 0 0 6px 6px;
		-webkit-box-shadow: 0 1px 2px rgba(0,0,0,.15);
		   -moz-box-shadow: 0 1px 2px rgba(0,0,0,.15);
				box-shadow: 0 1px 2px rgba(0,0,0,.15);
	  }

	  /* Page header tweaks */
	  .page-header {
		background-color: #f5f5f5;
		padding: 20px 20px 10px;
		margin: -20px -20px 20px;
	  }

	  /* Styles you shouldn't keep as they are for displaying this base example only */
	  .content .span10,
	  .content .span4 {
		min-height: 500px;
	  }
	  /* Give a quick and non-cross-browser friendly divider */
	  .content .span4 {
		margin-left: 0;
		padding-left: 19px;
		border-left: 1px solid #eee;
	  }

	  .topbar .btn {
		border: 0;
	  }

	</style>
  </head>

<body>

	<div class="topbar">
		<div class="fill">
			<div class="container">
				<a class="brand" href="#">Grails Facebook SDK</a>
				<ul class="nav">
					<g:if test="${!controllerName}"><li class="active"></g:if><g:else><li></g:else><a href="${createLink(uri:'/')}">Home</a></li>
					<g:if test="${controllerName=='website'}"><li class="active"></g:if><g:else><li></g:else><a href="${createLink(uri:'/website')}">Website</a></li>
					<g:if test="${controllerName=='app'}"><li class="active"></g:if><g:else><li></g:else><a href="${createLink(uri:'/app')}">App</a></li>
		 		</ul>
			</div>
		</div>
	</div>

	<div class="container">
		<g:layoutBody />
		<footer>
			<a href="http://github.com/affinitiz/facebook-grails-sdk">Facebook Grails SDK</a> - Version <g:meta name="app.version" /> (Built with Grails <g:meta name="app.grails.version" />) - Open source project by <a href="http://affinitiz.com">Affinitiz</a> - 
				<a href="http://www.apache.org/licenses/LICENSE-2.0">Licensed under the Apache License, Version 2.0</a><br />
		</footer>

	</div> <!-- /container -->
	<r:layoutResources />
</body>
</html>