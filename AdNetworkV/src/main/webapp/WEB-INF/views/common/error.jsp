<%@ page language="java" contentType="text/html; charset=UTF-8" isErrorPage="true"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- Opening tags -->

<!DOCTYPE html>
<html lang="${html_lang}" class="default-style">
	<head>
		<title>${pageTitle}</title>
	    <meta charset="utf-8">
    	<meta http-equiv="x-ua-compatible" content="IE=edge,chrome=1">
	    <meta name="description" content="">
    	<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0">

	    <link href="/favicon.ico" rel="icon" type="image/x-icon">

		<link href="<c:url value='/resources/css/kendo.bootstrap.cotton.all.css'/>" rel="stylesheet" />

	    <link rel="stylesheet" href="/resources/vendor/css/bootstrap.css">
    	<link rel="stylesheet" href="/resources/vendor/css/appwork.css">
	    <link rel="stylesheet" href="/resources/vendor/css/theme-comp-cotton-red.css">
    	<link rel="stylesheet" href="/resources/vendor/css/colors.css">
	    <link rel="stylesheet" href="/resources/vendor/css/uikit.css">
    
	    <link rel="stylesheet" href="/resources/base/base.css">

    	<script src="/resources/vendor/js/layout-helpers.js"></script>
	    <script src="/resources/vendor/js/pace.js"></script>

		<script src="<c:url value='/resources/vendor/js/fa63.all.min.js' />"></script>
	
		<script src="<c:url value='/resources/js/jquery.min.js' />"></script>
		<script src="<c:url value='/resources/js/jszip.min.js' />"></script>
		<script src="<c:url value='/resources/js/kendo.all.min.js' />"></script>

		<script src="<c:url value='/resources/shared/js/prettify.js'/>"></script>
		<script src="<c:url value='/resources/shared/js/sockjs.min.js'/>"></script>
		<script src="<c:url value='/resources/shared/js/jsbn.js' />"></script>
		<script src="<c:url value='/resources/shared/js/prng4.js' />"></script>
		<script src="<c:url value='/resources/shared/js/rng.js' />"></script>
		<script src="<c:url value='/resources/shared/js/rsa.js' />"></script>
	</head>
	
	<body>
		<div class="page-loader">
			<div class="bg-primary"></div>
		</div>
        

<!-- Content -->

		<div id="error_area">
			<h1><img src="/resources/shared/images/logo/logo_error.png"></h1>
			<div class="msg_box">
				<c:if test="${not empty code}">
					<h3>${code}</h3>
				</c:if>
				<c:if test="${not empty title}">
					<h4>${title}</h4>
				</c:if>
				<p>${desc}</p>
			</div>
		</div>
		
		<div id="error_foot">
			<a href="javascript:window.history.back();"><span class="fas fa-arrow-left fa-2x"></span></a>
			<a href="/"><span class="fas fa-home fa-2x"></span></a>
		</div>

<style>

#error_area { width:100%; text-align:center;}
.msg_box { width:600px; height:400px; position:absolute; left:50%; top:160px; margin-left:-300px; }
#error_area h1 { text-align:center; padding:20px 0; margin:0; border-bottom: solid 1px #d0d0d0;}
#error_area h3 { font-size:120px; color:#fb2b66; letter-spacing:-0.5px; margin-top:50px; margin-bottom:10px; font-weight:700;}
#error_area h4 { font-size:24px; color:#000; }
#error_area p { font-size:14px; color:#666; padding-top:20px;}
 
#error_foot { background:#b7bbbe; position:fixed; left:0; bottom:0; height: 80px; width:100%; text-align:center; padding-top:25px;}
#error_foot a { margin:0 25px;}

</style>

<!-- / Content -->


<!-- Base modules -->
<common:base />


		<!-- Core scripts -->
		<script src="/resources/vendor/lib/popper/popper.js"></script>
		<script src="/resources/vendor/js/bootstrap.js"></script>
	</body>
</html>
