<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ page import="java.net.URLEncoder"%>

<%@page import="gov.ca.bdo.modeling.dsm2.map.server.data.UserProfile"%>
<%@page
	import="gov.ca.bdo.modeling.dsm2.map.server.UserProfileServiceImpl"%><html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="chrome=1">
<title>Create a profile</title>
<style type="text/css">
<!--
body {
	font-family: arial, sans-serif;
	background-color: #fff;
	margin-top: 2;
}

td {
	font-family: arial, sans-serif;
}

#maincontent{
	align: center;
	margin-left: 50px;
}
</style>
</head>
<body>
<div id="main">
<div class="header"><img alt="" src="images/dsm2_icon.png"></div>
<%
	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();
	if (user != null && userService.isUserLoggedIn()) {
		UserProfileServiceImpl profileService = new UserProfileServiceImpl();
		String email = user.getEmail();
		UserProfile profile = profileService.getUserProfile(email);
		if (profile == null) {
			profileService.createUserProfile(email);
			profile = profileService.getUserProfile(email);
		}
	} else {
		String successRedirectURL = userService.createLoginURL(request
				.getRequestURL().toString());
		return;
	}
%>
<div id="maincontent">
<h3>Your information:</h3>
<table>
	<tbody>
		<tr>
			<td>Email:</td>
			<td><%=user.getEmail()%></td>
		</tr>
	</tbody>
</table>
Click <button onclick="location.href='/update_user_profile'">Accept</button> to get started.
</div>
<style type="text/css">
<!--
.footer {
	padding-right: 5px;
	padding-left: 5px;
	padding-bottom: 5px;
	padding-top: 5px;
	font-size: 83%;
	border-top: #ffffff 1px solid;
	border-bottom: #ffffff 1px solid;
	background: #e5ecf9;
	text-align: center;
	font-family: arial, sans-serif;
}
-->
</style>
<div align="center" class="footer"><font color="#666666">
&copy;2010 California Department of Water Resources (DWR) </font> - <a
	href="http://water.ca.gov/">DWR</a> - <a
	href="http://www.water.ca.gov/canav/conditions.cfm">Conditions of
Use</a> - <a href="http://water.ca.gov/privacy.html">Privacy Policy</a> - <a
	href="http://code.google.com/p/dsm2-grid-map/wiki">Help</a></div>
</div>
</body>
</html>