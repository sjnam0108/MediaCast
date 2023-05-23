<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/login" var="loginUrl" />
<c:url value="/loginkey" var="loginKeyUrl" />


<!-- Opening tags -->

<!DOCTYPE html>
<html lang="${html_lang}" class="default-style">
	<common:head />
	<body>
		<div class="page-loader">
			<div class="bg-primary"></div>
		</div>
        

<!-- Content -->

<div class="authentication-wrapper authentication-1 px-4">
	<div class="authentication-inner py-5">

		<!-- Logo -->
		<div class="d-flex justify-content-center align-items-center">
			<img src="${logoPathFile}" alt/>
		</div>
		<!-- / Logo -->

		<!-- Form -->
		<form class="my-5">
			<div class="form-group">
				<input type="text" placeholder="${label_username}" id="ip_id" class="form-control" onfocus="onFocus()">
			</div>
			<div class="form-group">
				<input type="password" placeholder="${label_password}" id="ip_pwd" class="form-control" onfocus="onFocus()">
			</div>
			<div class="d-flex justify-content-between align-items-center m-0">
				<label class="custom-control custom-checkbox m-0">
					<input type="checkbox" class="custom-control-input" id="ip_remember">
					<span class="custom-control-label">${tip_remember}</span>
				</label>
				<button type="button" class="btn btn-primary" onclick="login()">${btn_login}</button>
			</div>
		</form>
		<!-- / Form -->

	</div>
</div>

<style>
.authentication-wrapper {
	display:-webkit-box; display:-ms-flexbox; display:flex; -ms-flex-preferred-size:100%; flex-basis:100%; min-height:100vh; width:100%
}
.authentication-wrapper .authentication-inner { width:100% }
.authentication-wrapper.authentication-1 {
	-webkit-box-align:center; -ms-flex-align:center; align-items:center; -webkit-box-pack:center; -ms-flex-pack:center; justify-content:center
}
.authentication-wrapper.authentication-1 .authentication-inner { max-width:300px }

@media all and (-ms-high-contrast: none), (-ms-high-contrast: active) {
	.authentication-wrapper:after {
		content:''; display:block; -webkit-box-flex:0; -ms-flex:0 0 0%; flex:0 0 0%; min-height:inherit; width:0; font-size:0
	}
}
</style>

<script>

var keyValidTime = new Date().getTime() + 1 * 60000;

$(document).ready(function() {
    
	$("#ip_id").keydown(function(e) {
		checkEnterKey(e, 1);
	});

	$("#ip_pwd").keydown(function(e) {
		checkEnterKey(e, 2);
	});
    
    if (getCookie()) {
    	$("#ip_id").val(getCookie());
    	$("#ip_remember").prop("checked", true);
    	$("#ip_pwd").focus();
    } else {
        $("#ip_id").focus();
    }
    
<c:if test="${forcedLogout}">
	showAlertModal("danger", "${msg_forcedLogout}");
</c:if>
});


function setCookie(value, expireDays) {
	var expiredDate = new Date();
	expiredDate.setDate(expiredDate.getDate() + expireDays);
	
	document.cookie = "username=" + escape(value) + "; path=/; expires=" + expiredDate.toGMTString() + ";";
}

function getCookie() {
	if (document.cookie.length > 0) {
		var search = "username=";
		var offset = document.cookie.indexOf(search);
		if (offset != -1) {
			offset += search.length;
			var end = document.cookie.indexOf(";", offset);
			if (end == -1) { end = document.cookie.length; }
			
			return unescape(document.cookie.substring(offset, end));
		}
	}
}

function login() {
	var ip_id = $('#ip_id').val();
	var ip_pwd = $('#ip_pwd').val();
	
	if (ip_id.length > 0 && ip_pwd.length > 0) {
    	var data = {
			shortName: ip_id,
			password: ip_pwd,
		};

		var rsa = new RSAKey();
		var keyMod = "${RSAKeyMod}";
		var keyExp = "${RSAKeyExp}";
			
		var rsaApplied = keyMod.length > 0 && keyExp.length > 0;
		if (rsaApplied) {
			rsa.setPublic(keyMod, keyExp);
			data = {
				shortName: rsa.encrypt($("#ip_id").val()),
				password: rsa.encrypt($("#ip_pwd").val()),
			};
		}
		
		showWaitModal();
		
		if ($("#ip_remember").is(":checked") == true) {
			setCookie(ip_id, 365);
		} else {
			setCookie(ip_id, 0);
		}
		
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${loginUrl}",
			data: JSON.stringify(data),
			success: function (form) {
				var dstUrl = "/userhome";
				if ("${prevUri}" != "") {
					dstUrl = dstUrl + "?dst=${prevUri}";
					if ("${prevQuery}" != "") {
						dstUrl = dstUrl + "?" + "${prevQuery}";
					}
				}
				
				location.href = dstUrl;
			},
			error: function(e) {
				hideWaitModal();
				
				if (e.responseText) {
					showAlertModal("danger", JSON.parse(e.responseText).error);
				}
				
				$("#ip_id").focus();
				$("#ip_id").select();
			}
		});
	}
}

function checkEnterKey(e, idx) {
	if (e.keyCode == 13 && idx == 1) {
		$('#ip_pwd').focus();
		$('#ip_pwd').select();
	} else if (e.keyCode == 13 && idx == 2) {
		login();
	}
}

function onFocus() {
	
	var keyMod = "${RSAKeyMod}";
	var keyExp = "${RSAKeyExp}";
		
	var rsaApplied = keyMod.length > 0 && keyExp.length > 0;
	if (rsaApplied) {
		if (keyValidTime < new Date()) {
			$.ajax({
				type: "POST",
				contentType: "application/json",
				dataType: "json",
				url: "${loginKeyUrl}",
				data: JSON.stringify({ key: "${RSAKeyMod}" }),
				success: function (data, status) {
					if (data == "Y") {
						keyValidTime = new Date().getTime() + 1 * 60000;
					} else if (data == "N") {
						location.reload();
					}
				},
				error: ajaxReadError
			});
		}
	}
}

</script>

<!-- / Content -->


<!-- Base modules -->
<common:base />


		<!-- Core scripts -->
		<script src="/resources/vendor/lib/popper/popper.js"></script>
		<script src="/resources/vendor/js/bootstrap.js"></script>
		<script src="/resources/vendor/js/sidenav.js"></script>

		<!-- Libs -->
		<script src="/resources/vendor/lib/perfect-scrollbar/perfect-scrollbar.js"></script>
		<script src="/resources/vendor/lib/toastr/toastr.js"></script>
		<script src="/resources/vendor/lib/bootbox/bootbox.js"></script>
		<script src="/resources/vendor/lib/bootstrap-select/bootstrap-select.js"></script>
	</body>
</html>