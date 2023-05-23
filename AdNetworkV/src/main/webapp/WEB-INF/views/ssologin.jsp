<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/ssologin" var="loginUrl" />


<!-- Opening tags -->

<!DOCTYPE html>
<html lang="${html_lang}" class="default-style">
	<common:head />
	<body>
        

<!-- Content -->

<script>

$(document).ready(function() {
	
	showWaitModal();
	
	var data = {
			reqStrValue1: "${user}",
			reqStrValue2: "${referer}",
		};

	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${loginUrl}",
		data: JSON.stringify(data),
		success: function (form) {
			location.href = "/dsg/mondashboard";
		},
		error: function(e) {
			location.href = "/";
		}
	});

});

</script>

<!-- / Content -->


<!-- Base modules -->
<common:base />


		<!-- Core scripts -->
		<script src="/resources/vendor/lib/popper/popper.js"></script>
		<script src="/resources/vendor/js/bootstrap.js"></script>
	</body>
</html>