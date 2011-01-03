<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ page import="java.net.URLEncoder"%>

<%@page import="gov.ca.bdo.modeling.dsm2.map.server.data.UserProfile"%>
<%@page import="gov.ca.bdo.modeling.dsm2.map.server.UserProfileServiceImpl"%><html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="chrome=1">
<link type="text/css" rel="stylesheet" href="dsm2_grid_map/cwhf.css">
<title>DSM2 Grid Map</title>
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

.c {
	width: 4;
	height: 4;
}

a:link {
	color: #00c;
}

a:visited {
	color: #551a8b;
}

a:active {
	color: #f00;
}

.form-noindent {
	background-color: #fff;
	border: 1px solid #c3d9ff;
}
-->
</style>
<style type="text/css">
<!--
.gaia.le.lbl {
	font-family: Arial, Helvetica, sans-serif;
	font-size: smaller;
}

.gaia.le.fpwd {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 70%;
}

.gaia.le.chusr {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 70%;
}

.gaia.le.val {
	font-family: Arial, Helvetica, sans-serif;
	font-size: smaller;
}

.gaia.le.button {
	font-family: Arial, Helvetica, sans-serif;
	font-size: smaller;
}

.gaia.le.rem {
	font-family: Arial, Helvetica, sans-serif;
	font-size: smaller;
}

.gaia.captchahtml.desc {
	font-family: arial, sans-serif;
	font-size: smaller;
}

.gaia.captchahtml.cmt {
	font-family: arial, sans-serif;
	font-size: smaller;
	font-style: italic;
}
-->
</style>
<style type="text/css">
<!--
body {
	font-family: arial, sans-serif;
	margin: 0;
	padding: 13px 15px 15px;
}

.body {
	margin: 0;
}

div.errorbox-good {
	
}

div.errorbox-bad {
	
}

div.errormsg {
	color: red;
	font-size: smaller;
	font-family: arial, sans-serif;
}

font.errormsg {
	color: red;
	font-size: smaller;
	font-family: arial, sans-serif;
}

div.pagemsg {
	font-size: smaller;
	font-weight: bold;
	text-align: center;
}

div.pagemsg span {
	padding: 5px;
	background: #ff9;
}

div.topbar {
	font-size: smaller;
	margin-right: -5px;
	text-align: right;
	white-space: nowrap;
}

div.header {
	margin-bottom: 9px;
	margin-left: -2px;
	position: relative;
	zoom: 1
}

div.header img.logo {
	border: 0;
	float: left;
}

div.header div.headercontent {
	float: right;
	margin-top: 17px;
}

div.header:after {
	content: ".";
	display: block;
	height: 0;
	clear: both;
	visibility: hidden;
}

div.pagetitle {
	font-weight: bold;
}

.footer {
	color: #666;
	font-size: smaller;
	margin-top: 40px;
	text-align: center;
}

table#signupform {
	left: -5px;
	top: -7px;
	position: relative;
}

table#signupform td {
	padding: 7px 5px;
}

table#signupform td table td {
	padding: 1px;
}

hr {
	border: 0;
	background-color: #DDDDDD;
	height: 1px;
	width: 100%;
	text-align: left;
	margin: 5px;
}
-->
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
		if (profile != null){
			response.sendRedirect("dsm2_grid_map.html");
		} else {
			response.sendRedirect("user_profile.jsp");
		}
	}else {
		String successRedirectURL=userService.createLoginURL(request.getRequestURL()
				.toString());
%>
<div id="maincontent">
<table width="100%" cellspacing="0" cellpadding="0" border="0">
	<tbody>
		<tr>
			<td width="75%" valign="top">
			<table width="95%" cellspacing="0" cellpadding="0" border="0">
				<tbody>
					<tr>
						<td width="155"><img height="111" width="145"
							src="images/dsm2_grid_snapshot.png"></td>
						<td>
						<h3>View DSM2 Model Grid on a Google Map</h3>
						<p><font size="-1"> Upload your DSM2 study files and
						view them on a Google Map. Adjust channel lengths, reservoir
						areas, along with bathymetry information for input to your model.</font></p>
						</td>
					</tr>
				</tbody>
			</table>
			<table width="90%" cellspacing="0" cellpadding="0" border="0"
				style="margin: 30px 0pt 0pt;">
				<tbody>
					<tr>
						<td class="feature-description"><font size="-1"> <b>Works
						right off existing model input files</b><br>
						Add in GIS information and you have the grid on the map. 
					</tr>
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td class="feature-description"><font size="-1"> <b>Edit
						model elements on the map</b><br>
						Edit by dragging and dropping elements or editing existing ones.
						Take advantage of GIS information and elevation information based
						on LIDAR and bathymetry surveys. </font></td>
					</tr>
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td class="feature-description"><font size="-1"> <b>View
						timeseries for input and output</b><br>
						Upload your input/output time series using a small script and view
						the ouput on the map. Send out the link with secure or public
						view. </font></td>
					</tr>
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td class="feature-description">
						<p><font size="-1"> To understand more about this site
						or to view the source code see this <a
							href="http://code.google.com/p/dsm2-grid-map/">GPL'ed project</a></font></p>
						</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
					</tr>
				</tbody>
			</table>
			</td>
			<td valign="top" align="center" style="padding-left: 10px;">
			<div id="rhs">
			<div id="rhs_login_signup_box">
			<style type="text/css">
&
lt  ;!-- .gaia.le.lbl {
	font-family: Arial, Helvetica, sans-serif;
	font-size: smaller;
}

.gaia.le.fpwd {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 70%;
}

.gaia.le.chusr {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 70%;
}

.gaia.le.val {
	font-family: Arial, Helvetica, sans-serif;
	font-size: smaller;
}

.gaia.le.button {
	font-family: Arial, Helvetica, sans-serif;
	font-size: smaller;
}

.gaia.le.rem {
	font-family: Arial, Helvetica, sans-serif;
	font-size: smaller;
}

.gaia.captchahtml.desc {
	font-family: arial, sans-serif;
	font-size: smaller;
}

.gaia.captchahtml.cmt {
	font-family: arial, sans-serif;
	font-size: smaller;
	font-style: italic;
}
--&
gt


;
</style>
			<div id="loginbox">
			<table width="100%" cellspacing="3" cellpadding="5" border="0"
				class="form-noindent">
				<tbody>
					<tr>
						<td nowrap="nowrap" valign="top" bgcolor="#e8eefa"
							style="text-align: center;"><input type="hidden" value="ae"
							name="ltmpl">
						<div class="loginBox">
						<table cellspacing="0" cellpadding="1" border="0" align="center"
							id="gaia_table">
							<tbody>
								<tr>
									<td align="center" colspan="2"><button onclick="location.href='<%=successRedirectURL%>'">Sign in</button> with your
									</td>
									<td> 
									<img alt="Google"
										src="https://www.google.com/accounts/google_transparent.gif">
	
									</td>
									<td valign="middle"><font size="+0"><b>Account</b></font>
									</td>
								</tr>
							</tbody>
						</table>
						</div>
						</td>
					</tr>
				</tbody>
			</table>
			</div>
			<table width="100%" cellspacing="3" cellpadding="6" border="0"
				class="form-noindent">
				<tbody>
					<tr>
						<td bgcolor="#e8eefa" align="center" style="font-size: 83%;">
						<b>Don't have a Google Account? </b><br>
						<a
							href="https://www.google.com/accounts/NewAccount?continue=<%=successRedirectURL%>">
						<b>Create an account now</b> </a></td>
					</tr>
				</tbody>
			</table>
			</div>
			<br>
			</div>
			</td>
		</tr>
	</tbody>
</table>
</div>
<%
	}
%>
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