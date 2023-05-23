<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/knl/account/create" var="createUrl" />
<c:url value="/knl/account/read" var="readUrl" />
<c:url value="/knl/account/update" var="updateUrl" />
<c:url value="/knl/account/destroy" var="destroyUrl" />

<c:url value="/knl/account/readACMedia" var="readACMediumUrl" />



<!-- Opening tags -->

<common:pageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}
</h4>

<hr class="border-light container-m--x mt-0 mb-4">





<!-- Page body -->


<!-- Java(optional)  -->

<%
	String editTemplate = 
			"<button type='button' onclick='edit(#= id #)' class='btn icon-btn btn-sm btn-outline-success borderless'>" + 
			"<span class='fas fa-pencil-alt'></span></button>";
			
	String effStartDateTemplate = net.doohad.utils.Util.getSmartDate("effectiveStartDate", false, false);
	String effEndDateTemplate = net.doohad.utils.Util.getSmartDate("effectiveEndDate", false, false);
			
	String noRecordsTemplate =
			"<div class='container text-center my-4'>" +
				"<div class='d-flex justify-content-center align-self-center'>" +
					"<span class='fa-thin fa-hexagon-exclamation fa-3x'></span>" +
					"<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>해당 자료 없음</span>" +
				"</div>" +
			"</div>";
%>


<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid" pageable="true" filterable="true" sortable="true" scrollable="true" reorderable="true" resizable="true" selectable="single" >
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
    <kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button id="add-btn" type="button" class="btn btn-outline-success">추가</button>
    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">엑셀</button>
    		</div>
    		<div class="float-right">
    			<button id="delete-btn" type="button" class="btn btn-danger">삭제</button>
    		</div>
    	</div>
   	</kendo:grid-toolbarTemplate>
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="수정" width="50" filterable="false" sortable="false" template="<%= editTemplate %>" />
		<kendo:grid-column title="계정명" field="name" width="150" />
		<kendo:grid-column title="커널 관리" field="scopeKernel" width="150"
				template="#=scopeKernel ? \"<span class='fa-light fa-check'>\" : \"\"#" />
		<kendo:grid-column title="매체 관리" field="scopeMedium" width="150"
				template="#=scopeMedium ? \"<span class='fa-light fa-check'>\" : \"\"#" />
		<kendo:grid-column title="광고 제공" field="scopeAd" width="150"
				template="#=scopeAd ? \"<span class='fa-light fa-check'>\" : \"\"#" />
		<kendo:grid-column title="대상 매체" field="destMedia" width="300" template="#= dispBadgeValues(destMedia) #"  />
		<kendo:grid-column title="유효시작일" field="effectiveStartDate" template="<%= effStartDateTemplate %>" width="150" />
		<kendo:grid-column title="유효종료일" field="effectiveEndDate" template="<%= effEndDateTemplate %>" width="150" />
	</kendo:grid-columns>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="name" dir="asc"/>
		</kendo:dataSource-sort>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readUrl}" dataType="json" type="POST" contentType="application/json"/>
			<kendo:dataSource-transport-parameterMap>
				<script>
					function parameterMap(options,type) {
						return JSON.stringify(options);	
					}
				</script>
			</kendo:dataSource-transport-parameterMap>
		</kendo:dataSource-transport>
		<kendo:dataSource-schema data="data" total="total" groups="data">
			<kendo:dataSource-schema-model id="id">
				<kendo:dataSource-schema-model-fields>
					<kendo:dataSource-schema-model-field name="scopeKernel" type="boolean" />
					<kendo:dataSource-schema-model-field name="scopeMedium" type="boolean" />
					<kendo:dataSource-schema-model-field name="scopeAd" type="boolean" />
					<kendo:dataSource-schema-model-field name="effectiveStartDate" type="date" />
					<kendo:dataSource-schema-model-field name="effectiveEndDate" type="date" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

<!-- / Kendo grid  -->


<!-- Grid button actions  -->

<script>
$(document).ready(function() {

	// Add
	$("#add-btn").click(function(e) {
		e.preventDefault();
		
		initForm1();

		
		$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-1").modal();
	});
	// / Add
	
	// Delete
	$("#delete-btn").click(function(e) {
		e.preventDefault();
			
		var grid = $("#grid").data("kendoGrid");
		var rows = grid.select();
	
		var delRows = [];
		rows.each(function(index, row) {
			var selectedItem = grid.dataItem(row);
			delRows.push(selectedItem.id);
		});
			
		if (delRows.length > 0) {
			showDelConfirmModal(function(result) {
				if (result) {
					$.ajax({
						type: "POST",
						contentType: "application/json",
						dataType: "json",
						url: "${destroyUrl}",
						data: JSON.stringify({ items: delRows }),
						success: function (form) {
        					showDeleteSuccessMsg();
							grid.dataSource.read();
						},
						error: ajaxDeleteError
					});
				}
			});
		}
	});
	// / Delete
	
});	
</script>

<!-- / Grid button actions  -->


<!--  Root form container -->
<div id="formRoot"></div>


<!--  Forms -->

<script id="template-1" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-1">
	<div class="modal-dialog modal-sm">
		<form class="modal-content" id="form-1" rowid="-1" url="${createUrl}">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					${pageTitle}
					<span class="font-weight-light pl-1"><span name="subtitle"></span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
				<div class="form-group col">
					<label class="form-label">
						계정명
						<span class="text-danger">*</span>
					</label>
					<input name="name" type="text" maxlength="100" class="form-control required">
				</div>
				<div class="form-group col">
					<label class="form-label">
						유효시작일
						<span class="text-danger">*</span>
					</label>
					<input name="effectiveStartDate" type="text" class="form-control required">
				</div>
				<div class="form-group col">
					<label class="form-label">
						유효종료일
					</label>
					<input name="effectiveEndDate" type="text" class="form-control">
				</div>
				<div class="form-group col">
					<label class="form-label">
						관리 범위
						<span data-toggle="tooltip" data-placement="right" title="한 항목 이상은 반드시 선택해 주세요.">
							<span class="fa-regular fa-circle-info text-info"></span>
						</span>
					</label>
					<div>
						<label class="switcher">
							<input type="checkbox" class="switcher-input check-switch-status" name="scopeKernel">
							<span class="switcher-indicator">
								<span class="switcher-yes"></span>
								<span class="switcher-no"></span>
							</span>
							<span class="switcher-label">커널</span>
						</label>
						<span class="pl-2"></span>
						<label class="switcher switcher-secondary">
							<input type="checkbox" class="switcher-input check-switch-status" name="scopeMedium">
							<span class="switcher-indicator">
								<span class="switcher-yes"></span>
								<span class="switcher-no"></span>
							</span>
							<span class="switcher-label">매체</span>
						</label>
						<span class="pl-2"></span>
						<label class="switcher switcher-secondary">
							<input type="checkbox" class="switcher-input check-switch-status" name="scopeAd">
							<span class="switcher-indicator">
								<span class="switcher-yes"></span>
								<span class="switcher-no"></span>
							</span>
							<span class="switcher-label">광고</span>
						</label>
					</div>
				</div>
				<div class="form-group col">
					<label class="form-label">
						대상 매체
					</label>
					<select name="destMedia" class="form-control border-none"></select>
				</div>
				<div class="form-group col">
					<label class="form-label">
						운영자 메모
					</label>
					<textarea name="memo" rows="3" maxlength="150" class="form-control"></textarea>
				</div>

			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button name="save-btn" type="button" class="btn btn-primary disabled" onclick='saveForm1()'>저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>

<style>

/* Vertical Scrollbar 삭제 */
.k-grid .k-grid-header
{
   padding: 0 !important;
}
.k-grid .k-grid-content
{
   overflow-y: visible;
   min-height: 90px;
}

</style>

<!--  / Forms -->


<!--  Scripts -->

<script>

function checkSaveValidation() {
	
	var ret = false;
	if ($("#form-1 input[name='scopeKernel']").is(':checked') ||
			$("#form-1 input[name='scopeMedium']").is(':checked') ||
			$("#form-1 input[name='scopeAd']").is(':checked')) {
		ret = true;
	}
	
	if (ret) {
		$("#form-1 button[name='save-btn']").removeClass("disabled");
	} else {
		$("#form-1 button[name='save-btn']").addClass("disabled");
	}
	
	return ret;
}


function dispBadgeValues(values) {
	
	var ret = "";
	var value = values.split("|");
	  
	for(var i = 0; i < value.length; i ++) {
		if (value[i]) {
			ret = ret + "<span class='badge badge-outline-secondary'>" +
					"<span class='fa-light fa-globe text-green'></span><span class='pl-1'></span>" + 
					value[i] + "</span><span class='pl-1'></span>";
		}
	}
	  
	return ret;
}


function initForm1(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-1").html()));
	
	$("#form-1 input[name='effectiveStartDate']").kendoDatePicker({
		format: "yyyy-MM-dd",
		parseFormats: [
			"yyyy-MM-dd",
		],
		value: new Date(),
	});
	
	$("#form-1 input[name='effectiveEndDate']").kendoDatePicker({
		format: "yyyy-MM-dd",
		parseFormats: [
			"yyyy-MM-dd",
		],
	});

	$("#form-1 textarea[name='memo']").keypress(function (e) {
		if (e.keyCode != 13) {
			return;
		}
		
		$(this).text().replace(/\n/g, "");
		
		return false;
	});
	
	$("#form-1 .check-switch-status").click(function() {
		checkSaveValidation();
	});
	
	$('[data-toggle="tooltip"]').tooltip();

    $("#form-1 select[name='destMedia']").kendoMultiSelect({
        dataTextField: "shortName",
        dataValueField: "shortName",
        tagTemplate: "<span class='fa-light fa-globe'></span><span class='pl-2' title='#:data.name#'>#:data.shortName#</span>",
        itemTemplate: "<span class='fa-light fa-globe'></span><span class='pl-2' title='#:data.name#'>#:data.shortName#</span>",
        dataSource: {
            transport: {
                read: {
                    dataType: "json",
                    url: "${readACMediumUrl}",
                    type: "POST",
                    contentType: "application/json",
					data: JSON.stringify({}),
                },
                parameterMap: function (options) {
            		return JSON.stringify(options);	
                }
            },
			error: kendoReadError
        },
        height: 400,
        filter: "contains",
        noDataTemplate: "표시할 자료가 없습니다.",
    });

	
	$("#form-1 span[name='subtitle']").text(subtitle ? subtitle : "추가");
	
	$("#form-1").validate({
		rules: {
			name: {
				minlength: 2, maxlength: 100,
			},
			effectiveStartDate: { date: true },
			effectiveEndDate: { date: true },
		}
	});
}


function saveForm1() {

	// kendo datepicker validation
	validateKendoDateValue($("#form-1 input[name='effectiveStartDate']"));
	validateKendoDateValue($("#form-1 input[name='effectiveEndDate']"));
	
	if (checkSaveValidation() && $("#form-1").valid()) {
    	var data = {
    		id: Number($("#form-1").attr("rowid")),
    		name: $.trim($("#form-1 input[name='name']").val()),
    		effectiveStartDate: $("#form-1 input[name='effectiveStartDate']").data("kendoDatePicker").value(),
    		effectiveEndDate: $("#form-1 input[name='effectiveEndDate']").data("kendoDatePicker").value(),
    		scopeKernel: $("#form-1 input[name='scopeKernel']").is(':checked'),
    		scopeMedium: $("#form-1 input[name='scopeMedium']").is(':checked'),
    		scopeAd: $("#form-1 input[name='scopeAd']").is(':checked'),
    		destMedia: $("#form-1 select[name='destMedia']").data("kendoMultiSelect").value(),
    		memo: $.trim($("#form-1 textarea[name='memo']").val()),
    	};
    	
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: $("#form-1").attr("url"),
			data: JSON.stringify(data),
			success: function (form) {
				showSaveSuccessMsg();
				$("#form-modal-1").modal("hide");
				$("#grid").data("kendoGrid").dataSource.read();
			},
			error: ajaxSaveError
		});
	}
}


function edit(id) {
	
	initForm1("변경");

	var dataItem = $("#grid").data("kendoGrid").dataSource.get(id);
	
	$("#form-1").attr("rowid", dataItem.id);
	$("#form-1").attr("url", "${updateUrl}");
	
	$("#form-1 input[name='name']").val(dataItem.name);
	
	$("#form-1 input[name='effectiveStartDate']").data("kendoDatePicker").value(dataItem.effectiveStartDate);
	$("#form-1 input[name='effectiveEndDate']").data("kendoDatePicker").value(dataItem.effectiveEndDate);

	$("#form-1 textarea[name='memo']").text(dataItem.memo);
	
	
	$("#form-1 input[name='scopeKernel']").prop("checked", dataItem.scopeKernel);
	$("#form-1 input[name='scopeMedium']").prop("checked", dataItem.scopeMedium);
	$("#form-1 input[name='scopeAd']").prop("checked", dataItem.scopeAd);
	
	$("#form-1 select[name='destMedia']").data("kendoMultiSelect").value(dataItem.destMedia.split("|"));
	
	checkSaveValidation();

	
	$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-1").modal();
}


</script>

<!--  / Scripts -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
