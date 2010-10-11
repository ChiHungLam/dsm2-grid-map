<%@ page
	import="com.google.appengine.api.blobstore.BlobstoreServiceFactory"%>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService"%>

<%
	BlobstoreService blobstoreService = BlobstoreServiceFactory
			.getBlobstoreService();
%>
<html>
<body>
<form action="<%=blobstoreService.createUploadUrl("/upload_file")%>"
	enctype="multipart/form-data" method="post">
<p>Enter name of file<br>
<input type="text" name="name" size="30"></p>
<p>Specify the file to upload <br>
<input type="file" name="file" size="40"></p>
<div><input type="submit" value="Send"></div>
</form>
</body>
</html>