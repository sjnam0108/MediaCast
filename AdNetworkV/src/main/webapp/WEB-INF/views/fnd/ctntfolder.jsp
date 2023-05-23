<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/fnd/ctntfolder/create" var="createUrl" />
<c:url value="/fnd/ctntfolder/read" var="readUrl" />
<c:url value="/fnd/ctntfolder/update" var="updateUrl" />
<c:url value="/fnd/ctntfolder/destroy" var="destroyUrl" />

<c:url value="/fnd/ctntfolder/defaultvalue" var="defaultValueUrl" />


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
    			<button id="default-btn" type="button" class="btn btn-default d-none d-sm-inline">기본 이용 설정</button>
    			<button id="delete-btn" type="button" class="btn btn-danger">삭제</button>
    		</div>
    	</div>
	</kendo:grid-toolbarTemplate>
	<kendo:grid-filterable extra="false" />
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="수정" width="50" filterable="false" sortable="false" template="<%= editTemplate %>" />
		<kendo:grid-column title="폴더명" field="name" width="120" />
		<kendo:grid-column title="웹 접근 경로" field="webPath" width="300" />
		<kendo:grid-column title="로컬 네트워크 접근 경로" field="localPath" width="300" />
		<kendo:grid-column title="기본 이용" field="logout" width="150"
				template="#=curr ? \"<span class='fa-light fa-check'>\" : \"\"#" />
		<kendo:grid-column title="소재 파일수" field="creatFileCount" width="150" filterable="false" sortable="false" />
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
			<kendo:dataSource-schema-model id="id" />
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

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
		var delNotAllowed = false;
		
		rows.each(function(index, row) {
			var selectedItem = grid.dataItem(row);
			delRows.push(selectedItem.id);
			
			if (selectedItem.curr) {
				delNotAllowed = true;
			}
		});
		
		if (delNotAllowed) {
			showAlertModal("danger", "대상 자료는 시스템에서 현재 기본 이용중이며, 삭제할 수 없습니다.");
			return;
		}
		
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
			}, true, delRows.length);
		}
	});
	// / Delete
	
	// Default value
	$("#default-btn").click(function(e) {
		e.preventDefault();
		
		var grid = $("#grid").data("kendoGrid");
		var rows = grid.select();

		var opRows = [];
		rows.each(function(index, row) {
			var selectedItem = grid.dataItem(row);
			opRows.push(selectedItem.id);
		});
		
		if (opRows.length > 0) {
			$.ajax({
				type: "POST",
				contentType: "application/json",
				dataType: "json",
				url: "${defaultValueUrl}",
				data: JSON.stringify({ items: opRows }),
				success: function (data, status, xhr) {
					showOperationSuccessMsg();
					grid.dataSource.read();
				},
				error: ajaxOperationError
			});
		}
	});
	// / Default value
	
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
						폴더명
					</label>
					<div class="input-group">
						<div class="input-group-prepend">
							<div class="input-group-text">
								<label class="custom-control custom-checkbox px-2 m-0">
									<input name="nameChecked" type="checkbox" class="custom-control-input">
									<span class="custom-control-label"></span>
								</label>
							</div>
						</div>
						<input name="name" type="text" placeholder="[자동 생성]" maxlength="20" class="form-control">
					</div>
				</div>
				<div class="form-group col">
					<label class="form-label">
						웹 접근 경로
						<span class="text-danger">*</span>
					</label>
					<input name="webPath" type="text" maxlength="100" class="form-control required">
				</div>
				<div class="form-group col">
					<label class="form-label">
						로컬 네트워크 접근 경로
						<span class="text-danger">*</span>
					</label>
					<input name="localPath" type="text" maxlength="100" class="form-control required">
				</div>
			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button type="button" class="btn btn-primary" onclick='saveForm1()'>저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>

<!--  / Forms -->


<!--  Scripts -->

<script>

function initForm1(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-1").html()));

	$("#form-1 span[name='subtitle']").text(subtitle ? subtitle : "추가");
	
	$("#form-1 input[name='name']").prop("disabled", true);
	$("#form-1 input[name='nameChecked']").click(function (e) {
		if($(this).is(':checked')){
			$("#form-1 input[name='name']").prop("disabled", false);
			$("#form-1 input[name='name']").focus();
			$("#form-1 input[name='name']").attr("placeholder", "[직접 입력]");
		} else {
			$("#form-1 input[name='name']").prop("disabled", true);
			$("#form-1 input[name='name']").attr("placeholder", "[자동 생성]");
		}
	});

	$("#form-1").validate({
		rules: {
			name: {
				alphanumeric: true,
			},
		}
	});
}


function saveForm1() {

	// disabled 상태에서는 validation 진행 안하기 때문
	var currStatus = $("#form-1 input[name='name']").prop("disabled");
	$("#form-1 input[name='name']").prop("disabled", false);
	
	if ($("#form-1").valid()) {
    	var data = {
    		id: Number($("#form-1").attr("rowid")),
    		name: $.trim($("#form-1 input[name='name']").val()),
    		webPath: $.trim($("#form-1 input[name='webPath']").val()),
    		localPath: $.trim($("#form-1 input[name='localPath']").val()),
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
	
	$("#form-1 input[name='name']").prop("disabled", currStatus);
}


function edit(id) {
	
	initForm1("변경");

	var dataItem = $("#grid").data("kendoGrid").dataSource.get(id);
	
	$("#form-1").attr("rowid", dataItem.id);
	$("#form-1").attr("url", "${updateUrl}");
	
	$("#form-1 input[name='name']").val(dataItem.name);
	$("#form-1 input[name='webPath']").val(dataItem.webPath);
	$("#form-1 input[name='localPath']").val(dataItem.localPath);

	
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
