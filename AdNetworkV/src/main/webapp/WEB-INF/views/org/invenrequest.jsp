<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/org/invenrequest/read" var="readUrl" />
<c:url value="/org/invenrequest/destroy" var="destroyUrl" />
<c:url value="/org/invenrequest/execute" var="executeUrl" />

<c:url value="/org/invenrequest/readResults" var="readResultUrl" />
<c:url value="/org/invenrequest/readTypes" var="readTypeUrl" />



<!-- Opening tags -->

<common:mediumPageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}
</h4>

<hr class="border-light container-m--x mt-0 mb-4">





<!-- Page body -->


<!-- Java(optional)  -->

<%
	String creationDateTemplate = net.doohad.utils.Util.getSmartDate("whoCreationDate", true, true);
	
	String resultTemplate = "<span class='fa-regular " + 
			"# if (result == 'I') { #" + "fa-asterisk fa-fw text-muted" + 
			"# } else if (result == 'S') { #" + "fa-flag-checkered fa-fw" + 
			"# } else if (result == 'F') { #" + "fa-circle-stop fa-fw text-red" + 
			"# } else if (result == 'P') { #" + "fa-circle-exclamation fa-fw text-yellow" + 
			"# } #" +
			"'></span>";
			
	String typeTemplate =
			"# if (type == 'U') { #" + "<span class='fa-regular fa-asterisk fa-fw text-success'></span>" + 
			"# } else if (type == 'D') { #" + "<span class='fa-regular fa-trash-can text-danger fa-fw'></span>" + 
			"# } else { #" + "<span></span>" + 
			"# } #";

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
<kendo:grid name="grid" pageable="true" filterable="true" sortable="true" scrollable="true" reorderable="true" resizable="true" selectable="${value_gridSelectable}" >
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
    <kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">엑셀</button>
    		</div>
    		<div class="float-right">
    			<button id="execute-btn" type="button" class="btn btn-outline-success">
					<span class="fa-regular fa-bolt-lightning"></span>
					<span class="pl-1">실행</span>
				</button>
   				<span class="px-2">
    				<span class="fa-solid fa-pipe text-muted"></span>
   				</span>
    			<button id="delete-btn" type="button" class="btn btn-danger">삭제</button>
    		</div>
    	</div>
   	</kendo:grid-toolbarTemplate>
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="#" field="id" width="100" />
		<kendo:grid-column title="결과" field="result" width="100" template="<%= resultTemplate %>" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readResultUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="화면" field="screenName" width="200" />
		<kendo:grid-column title="화면ID" field="screenShortName" width="150" />
		<kendo:grid-column title="사이트" field="siteName" width="200" />
		<kendo:grid-column title="사이트ID" field="siteShortName" width="150" />
		<kendo:grid-column title="유형" field="type" width="100" template="<%= typeTemplate %>" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readTypeUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="내용" field="request" width="500" template="<small>#= request #</small>" />
		<kendo:grid-column title="결과코드" field="resultCode" width="150" />
		<kendo:grid-column title="등록" field="whoCreationDate" template="<%= creationDateTemplate %>" width="180" />
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="id" dir="asc"/>
		</kendo:dataSource-sort>
       	<kendo:dataSource-filter>
      		<kendo:dataSource-filterItem field="medium.id" operator="eq" logic="and" value="${sessionScope['currMediumId']}" >
      		</kendo:dataSource-filterItem>
  	    </kendo:dataSource-filter>
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
					<kendo:dataSource-schema-model-field name="id" type="number" />
					<kendo:dataSource-schema-model-field name="whoCreationDate" type="date" />
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

	// Execute
	$("#execute-btn").click(function(e) {
		e.preventDefault();
		
		var grid = $("#grid").data("kendoGrid");
		var rows = grid.select();
	
		var opRows = [];
		
		rows.each(function(index, row) {
			var selectedItem = grid.dataItem(row);
			opRows.push(selectedItem.id);
		});
		
		if (opRows.length > 0) {
			
			showWaitModal();

			$.ajax({
				type: "POST",
				contentType: "application/json",
				dataType: "json",
				url: "${executeUrl}",
				data: JSON.stringify({ items: opRows }),
				success: function (form) {
					showOperationSuccessMsg();
					hideWaitModal();
					
					grid.dataSource.read();
				},
				error: function(e) {
					hideWaitModal();
					ajaxOperationError(e);
				}
			});
		}
	});
	// / Execute
	
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
			}, true, delRows.length);
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
						사용자ID
						<span class="text-danger">*</span>
					</label>
					<input name="shortName" type="text" maxlength="50" class="form-control required">
				</div>
				<div class="form-group col">
					<label class="form-label">
						사용자명
						<span class="text-danger">*</span>
					</label>
					<input name="name" type="text" maxlength="100" class="form-control required">
				</div>
				<div class="form-group col">
					<label class="form-label">
						패스워드
						<span data-toggle="tooltip" data-placement="right" title="제시된 패스워드를 이용해서 로그인 후, 패스워드를 변경해 주세요.">
							<span class="fa-regular fa-circle-info text-info"></span>
						</span>
					</label>
					<input name="password" type="text" class="form-control" readonly value="${defaultPwd}">
				</div>
				<div class="form-group col">
					<label class="form-label">
						역할
					</label>
					<select name="role" class="selectpicker bg-white required" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
						<option value="M2" data-icon="fa-regular fa-user-gear fa-fw mr-1">관리자</option>
						<option value="AA" data-icon="fa-regular fa-signs-post fa-fw mr-1">광고 승인자</option>
					</select>
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

<style>

/* 선택 체크박스를 포함하는 필터 패널을 보기 좋게 */
.k-filter-selected-items {
	font-weight: 500;
	margin: 0.5em 0;
}
.k-filter-menu .k-button {
	width: 47%;
	margin: 0.5em 1% 0.25em;
}


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

function initForm1(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-1").html()));
	
	$("#form-1 select[name='role']").selectpicker('render');
	
	$('[data-toggle="tooltip"]').tooltip();

	
	$("#form-1 span[name='subtitle']").text(subtitle ? subtitle : "추가");
	
	$("#form-1").validate({
		rules: {
			shortName: {
				minlength: 2, maxlength: 50, alphanumeric: true,
			},
			name: {
				minlength: 2, maxlength: 100,
			},
		}
	});
}


function saveForm1() {
	
	if ($("#form-1").valid()) {
    	var data = {
    		id: Number($("#form-1").attr("rowid")),
    		shortName: $.trim($("#form-1 input[name='shortName']").val()),
    		name: $.trim($("#form-1 input[name='name']").val()),
    		password: $.trim($("#form-1 input[name='password']").val()),
    		role: $("#form-1 select[name='role']").val(),
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


function initForm2(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-2").html()));
	
	$("#form-2 select[name='role']").selectpicker('render');

	
	$("#form-2 span[name='subtitle']").text(subtitle ? subtitle : "추가");
	
	$("#form-2").validate({
		rules: {
			shortName: {
				minlength: 2, maxlength: 50, alphanumeric: true,
			},
			name: {
				minlength: 2, maxlength: 100,
			},
		}
	});
}


function edit(id) {
	
	initForm2("변경");

	var dataItem = $("#grid").data("kendoGrid").dataSource.get(id);
	
	$("#form-2").attr("rowid", dataItem.id);
	
	$("#form-2 input[name='shortName']").val(dataItem.shortName);
	$("#form-2 input[name='name']").val(dataItem.name);

	bootstrapSelectVal($("#form-2 select[name='role']"), dataItem.role);
	
	
	$('#form-modal-2 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-2").modal();
}


function saveForm2() {
	
	if ($("#form-2").valid()) {
    	var data = {
    		id: Number($("#form-2").attr("rowid")),
    		shortName: $.trim($("#form-2 input[name='shortName']").val()),
    		name: $.trim($("#form-2 input[name='name']").val()),
    		role: $("#form-2 select[name='role']").val(),
    	};
    	
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: $("#form-2").attr("url"),
			data: JSON.stringify(data),
			success: function (form) {
				showSaveSuccessMsg();
				$("#form-modal-2").modal("hide");
				$("#grid").data("kendoGrid").dataSource.read();
			},
			error: ajaxSaveError
		});
	}
}

</script>

<!--  / Scripts -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
