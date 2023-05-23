<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/fnd/userpriv/create" var="createUrl" />
<c:url value="/fnd/userpriv/read" var="readUrl" />
<c:url value="/fnd/userpriv/destroy" var="destroyUrl" />

<c:url value="/fnd/userpriv/readUsers" var="readUserUrl" />
<c:url value="/fnd/userpriv/readPrivs" var="readPrivUrl" />


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
<kendo:grid name="grid" pageable="true" filterable="true" sortable="true" scrollable="false" reorderable="true" resizable="true" selectable="single" >
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
	<kendo:grid-filterable extra="false" />
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="사용자ID" field="user.shortName" />
		<kendo:grid-column title="사용자명" field="user.name" minScreenWidth="800" />
		<kendo:grid-column title="권한" field="priv.ukid" />
		<kendo:grid-column title="권한명" field="priv.name" minScreenWidth="800" />
	</kendo:grid-columns>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="priv.ukid" dir="asc"/>
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
	<div class="modal-dialog">
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
						사용자
						<span class="text-danger">*</span>
					</label>
					<div name="users-con">
						<select name="users" class="form-control border-none"></select>
					</div>
					<label name="users-feedback" for="users" class="error invalid-feedback"></label>
				</div>
				<div class="form-group col">
					<label class="form-label">
						권한
					</label>
					<div name="privs-con">
						<select name="privs" class="form-control border-none"></select>
					</div>
					<label name="privs-feedback" for="privs" class="error invalid-feedback"></label>
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

//Form validator
var validator = null;

//Form error obj
var errors = {};


function validateMSValue(name) {
	
	var selector = null;
	
	if (name == "users") {
		selector = $("#form-1 select[name='users']");
	} else {
		return;
	}
	
	var ids = selector.data("kendoMultiSelect").value();

	if (ids.length == 0) {
		errors[name] = "이 항목의 값을 선택하십시오.";
	} else {
		delete errors[name];
	}
}


function checkMSMessage(name) {
	
	var selector = null;
	var container = null;
	var feedback = null;
	
	if (name == "users") {
		selector = $("#form-1 select[name='users']");
		container = $("#form-1 div[name='users-con']");
		feedback = $("#form-1 label[name='users-feedback']");
	} else {
		return;
	}
	
	var ids = selector.data("kendoMultiSelect").value();

	if (ids.length == 0) {
		container.addClass("is-invalid");
		feedback.addClass("small d-block").css("display", "block");
	} else {
		container.removeClass("is-invalid");
		feedback.removeClass("small d-block").css("display", "none");
	}
}


function initForm1(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-1").html()));
	

    $("#form-1 select[name='users']").kendoMultiSelect({
        dataTextField: "name",
        dataValueField: "id",
        tagTemplate: "<span class='fas fa-user text-gray'></span>" + 
        			 "<span class='pl-2' title='#:data.shortName#'>#:data.name#</span>",
        itemTemplate: "<span class='fas fa-user text-gray'></span>" +
        		      "<span class='pl-2' title='#:data.shortName#'>#:data.name#</span>",
        dataSource: {
            transport: {
                read: {
                    dataType: "json",
                    url: "${readUserUrl}",
                    type: "POST",
                    contentType: "application/json"
                },
                parameterMap: function (options) {
            		return JSON.stringify(options);	
                }
            },
            schema: {
            	data: "data",
            	total: "total"
            },
			error: kendoReadError
        },
	    change: function(e) {
	    	checkMSMessage("users");
	    },
        noDataTemplate: "표시할 자료가 없습니다.",
    });

    $("#form-1 select[name='privs']").kendoMultiSelect({
        dataTextField: "ukid",
        dataValueField: "id",
        tagTemplate: "<span class='fas fa-cog text-gray'></span>" + 
        			 "<span class='pl-2' title='#:data.name#'>#:data.ukid#</span>",
        itemTemplate: "<span class='fas fa-cog text-gray'></span>" +
        		      "<span class='pl-2'>#:data.name#</span>",
        dataSource: {
            transport: {
                read: {
                    dataType: "json",
                    url: "${readPrivUrl}",
                    type: "POST",
                    contentType: "application/json"
                },
                parameterMap: function (options) {
            		return JSON.stringify(options);	
                }
            },
            schema: {
            	data: "data",
            	total: "total"
            },
			error: kendoReadError
        },
	    change: function(e) {
	    	checkMSMessage("privs");
	    },
        noDataTemplate: "표시할 자료가 없습니다.",
    });

	$("#form-1 span[name='subtitle']").text(subtitle ? subtitle : "추가");
	
	validator = $("#form-1").validate();
}


function saveForm1() {

	errors = {};
	
	validateMSValue("users");
	
	if (Object.keys(errors).length == 0) {
		var userIds = $("#form-1 select[name='users']").data("kendoMultiSelect").value();
		var privIds = $("#form-1 select[name='privs']").data("kendoMultiSelect").value();

		if (privIds.length > 0 && userIds.length > 0) {
			var data = {
				userIds: userIds,
				privIds: privIds,
			};
        	
			$.ajax({
				type: "POST",
				contentType: "application/json",
				dataType: "json",
				url: "${createUrl}",
				data: JSON.stringify(data),
				success: function (data, status, xhr) {
					showAlertModal("success", JSON.parse(xhr.responseText));
					$("#form-modal-1").modal("hide");
					$("#grid").data("kendoGrid").dataSource.read();
				},
				error: ajaxSaveError
			});
		}
	} else {
		validator.showErrors(errors);
		
		checkMSMessage("users");
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
