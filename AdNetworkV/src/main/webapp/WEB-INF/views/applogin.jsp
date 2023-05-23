<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!DOCTYPE html>
<html>
	<head>
		<title>App Login</title>
		<script src="<c:url value='/resources/js/jquery.min.js' />"></script>
	</head>
<body>

<script>
$(document).ready(function() {

	if ("A" == "${appMode}") {
		
<c:choose>
<c:when test="${forcedLogout}">

		location.href="app://loginf";

</c:when>
<c:otherwise>

		location.href="app://login";

</c:otherwise>
</c:choose>
	
	} else if ("I" == "${appMode}") {
		
<c:choose>
<c:when test="${forcedLogout}">

		window.webkit.messageHandlers.app.postMessage("loginf");

</c:when>
<c:otherwise>

		window.webkit.messageHandlers.app.postMessage("login"); 

</c:otherwise>
</c:choose>
			
	}
	
});	
</script>

</body>
</html>