<%@ tag pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
    
	<link rel="stylesheet" href="/resources/vendor/lib/perfect-scrollbar/perfect-scrollbar.css">
	<link rel="stylesheet" href="/resources/vendor/lib/toastr/toastr.css">
	<link rel="stylesheet" href="/resources/vendor/lib/bootstrap-select/bootstrap-select.css">
	<link rel="stylesheet" href="/resources/vendor/lib/spinkit/spinkit.css">
	<link rel="stylesheet" href="/resources/vendor/lib/bootstrap-slider/bootstrap-slider.css">
	<link rel="stylesheet" href="/resources/vendor/lib/nouislider/nouislider.css">

    <link rel="stylesheet" href="/resources/base/base.css">

    <script src="/resources/vendor/js/layout-helpers.js"></script>
    <script src="/resources/vendor/js/pace.js"></script>

  	<script src="<c:url value='/resources/vendor/js/fa63.all.min.js' />"></script>
  	
	
	<script src="<c:url value='/resources/js/jquery.min.js' />"></script>
	<script src="<c:url value='/resources/js/jszip.min.js' />"></script>
	<script src="<c:url value='/resources/js/kendo.all.min.js' />"></script>
	
	<script src="<c:url value='/resources/vendor/lib/jquery-ui/jquery-ui.min.js' />"></script>

	<script src="<c:url value='/resources/shared/js/prettify.js'/>"></script>
	<script src="<c:url value='/resources/shared/js/sockjs.min.js'/>"></script>
	<script src="<c:url value='/resources/shared/js/jsbn.js' />"></script>
	<script src="<c:url value='/resources/shared/js/prng4.js' />"></script>
	<script src="<c:url value='/resources/shared/js/rng.js' />"></script>
	<script src="<c:url value='/resources/shared/js/rsa.js' />"></script>

	<script src="<c:url value='/resources/js/cultures/kendo.culture.${kendoLangCountryCode}.min.js' />"></script>
	<script src="<c:url value='/resources/js/messages/kendo.messages.${kendoLangCountryCode}.min.js' />"></script>
	<script> kendo.culture("${kendoLangCountryCode}"); </script>
	
</head>
