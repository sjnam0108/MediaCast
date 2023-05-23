<%@ tag pageEncoding="UTF-8"%>

<script src="/resources/vendor/lib/validate/validate.${html_lang}.js"></script>

<script>

function validateKendoDateValue(obj) {
	// jQeury validator의 validation 시점에는 kendo Datepicker의 control은 아직 validation 전 상황임.
	// 따라서 jQuery validator의 validation 전에 kendo Datepicker의 validation이 먼저 이루어져야 하나
	// kendo date picker에는 validation을 위한 메소드가 준비되어 있지 않음.
	// 그나마, 흉내낼 수 있는 것은 datepicker의 값 설정(value()) 메소드에 현재의 텍스트 값을 설정하면,
	// 날짜 형식으로 올바르지 못한 값인 경우 null로 처리하게 됨.(화면에서는 빈값)
	obj.data("kendoDatePicker").value(obj.data("kendoDatePicker").value());
}

function validateKendoDateTimeValue(obj) {
	obj.data("kendoDateTimePicker").value(obj.data("kendoDateTimePicker").value());
}

function validateKendoTimeValue(obj) {
	obj.data("kendoTimePicker").value(obj.data("kendoTimePicker").value());
}

function bootstrapSelectVal(obj, value) {
	obj.val(value);
	obj.selectpicker("refresh");
}

function bootstrapSelectDisabled(obj, value) {
	obj.prop('disabled', value);
	obj.selectpicker("refresh");
}

$(document).ready(function() {
	$.validator.setDefaults({
		errorPlacement: function errorPlacement(error, element) {
			$(element).parents('.form-group').append(error.addClass('invalid-feedback small d-block')) },
		highlight: function(element) { $(element).addClass('is-invalid'); },
		unhighlight: function(element) { $(element).removeClass('is-invalid'); },
    });	
});

</script>