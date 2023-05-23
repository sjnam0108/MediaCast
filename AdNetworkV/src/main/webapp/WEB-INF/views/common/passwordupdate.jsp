<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/passwordupdate" var="pwdUpdateUrl" />


<!-- Opening tags -->

<common:pageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 fas fa-key"></span>
	${pageTitle}
</h4>

<hr class="border-light container-m--x mt-0 mb-4">





<!-- Page body -->

	
<c:if test="${updateRequired}">
	
	<div class="alert alert-primary alert-dismissible fade show">
		<button type="button" class="close" data-dismiss="alert">×</button>
		${msg_updateRequired}
	</div>
	
</c:if>
	

<!--  Forms -->

<div class="card mb-4">
	<div class="card-body">
		<form id="form">
			<div class="form-group">
				<label class="form-label">
					${label_current}
					<span class="text-danger">*</span>
				</label>
				<input name="currentPwd" type="password" class="form-control col-md-8 required">
			</div>
			<div class="form-group">
				<label class="form-label">
					${label_new}
					<span class="text-danger">*</span>
				</label>
				<input name="newPwd" type="password" class="form-control col-md-8 required">
			</div>
			<div class="form-group" col-md-6>
				<label class="form-label">
					${label_confirm}
					<span class="text-danger">*</span>
				</label>
				<input name="confirmPwd" type="password" class="form-control col-md-8 required">
			</div>
			<button id="save-btn" type="button" class="btn btn-primary">${btn_save}</button>
		</form>
	</div>
</div>

<!--  / Forms -->


<!--  Scripts -->

<script type="text/javascript">

$(document).ready(function() {
	
	$("#form").validate({
		errorPlacement: function errorPlacement(error, element) {
			$(element).parents('.form-group').append(error.addClass('invalid-feedback small d-block')) },
		highlight: function(element) { $(element).addClass('is-invalid'); },
		unhighlight: function(element) { $(element).removeClass('is-invalid'); },
		rules: {
			"confirmPwd": {
				equalTo: 'input[name="newPwd"]'
			}
		}
	});

	$("#save-btn").click(function(e) {
		
		if ($("#form").valid()) {
			
			var passwordLevel = "${val_pwdLevel}";
			var password = $.trim($("input[name='newPwd']").val());
			
			if (passwordLevel == "2" && password != null && password != "") {
				var reg = new RegExp(/^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[$@!?#%^&*])\S{9,}$/g);
				if (!reg.test(password)) {
					showAlertModal("danger", "${msg_wrongLevel2Pwd}");
					return;
				}
			}
			
			
        	var data = {
            	currentPassword: $.trim($("input[name='currentPwd']").val()),
            	newPassword: $.trim($("input[name='newPwd']").val()),
            	confirmPassword: $.trim($("input[name='confirmPwd']").val()),
            };

			var rsa = new RSAKey();
    		var keyMod = "${RSAKeyMod}";
    		var keyExp = "${RSAKeyExp}";
    			
    		var rsaApplied = keyMod.length > 0 && keyExp.length > 0;
    		if (rsaApplied) {
    			rsa.setPublic(keyMod, keyExp);
    	       	data = {
               		currentPassword: rsa.encrypt(data.currentPassword),
               		newPassword: rsa.encrypt(data.newPassword),
               		confirmPassword: rsa.encrypt(data.confirmPassword),
    	        };
    		}
        	
			$.ajax({
				type: "POST",
				contentType: "application/json",
				dataType: "json",
				url: "${pwdUpdateUrl}",
				data: JSON.stringify(data),
				success: function (form) {
					showAlertModal("success", "${msg_updateComplete}");
				},
				error: ajaxSaveError
			});
		}
	});
});

</script>

<!--  / Scripts -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
