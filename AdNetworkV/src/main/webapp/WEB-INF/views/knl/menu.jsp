<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/knl/menu/create" var="createUrl" />
<c:url value="/knl/menu/destroy" var="destroyUrl" />
<c:url value="/knl/menu/dragdrop" var="dragDropUrl" />
<c:url value="/knl/menu/readMenus" var="readMenuUrl" />


<!-- Opening tags -->

<common:pageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}
</h4>

<hr class="border-light container-m--x mt-0 mb-4">





<!-- Page body -->


<!-- Main form  -->

<div class="card mb-4">
	<div class="card-body">
		<div class="btn-group">
			<button type="button" class="btn btn-success" id="add-btn"><span class="fas fa-plus-circle fa-lg"></span><span class="ml-2">새 메뉴</span></button>
			<button type="button" class="btn btn-default" id="refresh-btn"><span class="fas fa-sync"></span><span class="ml-2">새로고침</span></button>
		</div>

		<div id="menuTree" class="mt-2 mb-0 py-1 px-3" style="font-size: 1.15rem;"></div>
	</div>
	<hr class="m-0">
	<div class="card-body">
		<form id="propForm">
			<input type="hidden" name="id" id="id" value="">
			<input type="hidden" name="oper" id="oper" value="">
			
			<div class="form-row">
				<div class="col-sm-6">
					<div class="form-group col">
						<label class="form-label">
							식별자
							<span class="text-danger">*</span>
						</label>
						<input name="ukid" type="text" class="form-control required">
					</div>
				</div>
				<div class="col-sm-6">
					<div class="form-group col">
						<label class="form-label">
							URL
						</label>
						<input name="url" type="text" class="form-control">
					</div>
				</div>
			</div>
			<div class="form-row">
				<div class="col-sm-6">
					<div class="form-group col">
						<label class="form-label">
							<span class="pr-1">아이콘</span>
							<a href="javascript:void(0)" class="d-none d-xl-inline" title="FA 아이콘" data-toggle="popover" data-trigger="focus" data-content="<a href='https://fontawesome.com/search?o=r&s=light' target='_blank'>Font Awesome 사이트</a>에서 사용을 원하는, 하나의 아이콘 이름(예: ad, address-book)을 이곳에 입력하십시오." tabindex="0">
								<span class="fa-regular fa-circle-info text-info"></span>
							</a>
						</label>
						<input name="icon" type="text" class="form-control">
					</div>
				</div>
				<div class="col-sm-6">
					<div class="form-group col">
						<label class="form-label d-block pb-2">
							<span class="pr-1">다음 영역에서 이 메뉴 이용 가능</span>
							<span class="tip-container">
								<span data-toggle="tooltip" data-placement="top" title="URL이 등록되지 않은 항목은 자동으로 체크 해제됩니다.">
									<span class="fa-regular fa-circle-info text-info"></span>
								</span>
							</span>
						</label>
						<label class="custom-control custom-checkbox custom-control-inline">
							<input type="checkbox" class="custom-control-input" name="scopeKernel">
							<span class="custom-control-label">커널 관리</span>
						</label>
						<label class="custom-control custom-checkbox custom-control-inline">
							<input type="checkbox" class="custom-control-input" name="scopeMedium" >
							<span class="custom-control-label">매체 관리</span>
						</label>
						<label class="custom-control custom-checkbox custom-control-inline">
							<input type="checkbox" class="custom-control-input" name="scopeAd" >
							<span class="custom-control-label">광고 제공</span>
						</label>
					</div>
				</div>
			</div>
			
			<hr/>

			<button type="button" class="btn btn-primary mr-1" id="save-btn">저장</button>
			<button type="button" class="btn btn-danger mr-1" id="del-btn">삭제</button>
			<button type="button" class="btn btn-secondary mr-1" id="unlock-btn">잠금해제</button>
			<button type="button" class="btn btn-default" id="cancel-btn">취소</button>
		</form>
	</div>
</div>


<style>

/* 메뉴 선택 시 선택 항목의 배경 가장자리에 약간의 곡선을 추가 */
div#menuTree span.k-in.k-state-selected {
	border-radius: 4px;
}

</style>


<!-- / Main form  -->


<!--  Scripts -->

<script>

$(document).ready(function() {
   
	$('[data-toggle="popover"]').popover({html:true});
	$('[data-toggle="tooltip"]').tooltip({
		   container: '.tip-container'
	});
	
    var homogeneous = new kendo.data.HierarchicalDataSource({
    	transport: {
            read: {
                url: "${readMenuUrl}",
                dataType: "json",
                type: "POST",
                contentType: "application/json",
            },
            parameterMap: function(options, type) {
            	return JSON.stringify(options);
            },
        },
        schema: {
            model: {}
        }
    });
    
    var treeView = $("#menuTree").kendoTreeView({
    	dataSource: homogeneous,
    	dragAndDrop: true,
    	messages: {
    		retry: "재시도",
    		requestFailed: "요청 실패.",
    		loading: "로드중...",
    	},
    	select: onSelect,
    	drop: onDrop,
    });


	//Tree event: select
	function onSelect(e) {
		selectNode(e.node);
	}
	
	//Tree event: drop
	function onDrop(e) {
		if (e.valid) {
			var destinationNode = treeView.data("kendoTreeView").dataItem(e.destinationNode);
			var dropPosition = e.dropPosition;

			if (dropPosition == "over" && destinationNode["expanded"] == false 
					&& destinationNode["items"] != null && destinationNode["items"].length == 0) {
				e.preventDefault();
				alert("대상 노드를 먼저 펼친 후, 이 작업을 다시 수행해 주십시오.");
				return;
			}

			var sourceNodeId = treeView.data("kendoTreeView").dataItem(e.sourceNode).get("id");
			var destinationNodeId = destinationNode.get("id");
			
        	var data = {
        			sourceId: sourceNodeId,
        			destId: destinationNodeId,
        			position: dropPosition,
        	};

			draggedNode = e.sourceNode;
        	
        	$.ajax({
					type: "POST",
					contentType: "application/json",
					dataType: "json",
					url: "${dragDropUrl}",
					data: JSON.stringify(data),
					async: false,
					success: function (data, status, xhr) {
						showOperationSuccessMsg();
					setTimeout(function() {
						treeView.data("kendoTreeView").select(e.sourceNode);
						selectNode(e.sourceNode);
					}, 50);
					},
					error: ajaxOperationError
				});
		}
	}
	// / Tree event: drop
	
	add();

    
	// Button Group: Add
	$("#add-btn").click(function(e) {
		e.preventDefault();
		
		add();
	});
	// / Button Group: Add

	// Button Group: Refresh
	$("#refresh-btn").click(function(e) {
		e.preventDefault();
		
		treeView.data("kendoTreeView").dataSource.read();
		add();
	});
	// / Button Group: Refresh

	
	// Save
	$("#save-btn").click(function(e) {
		if ($("#propForm").valid()) {
        	var treeView = $("#menuTree").data("kendoTreeView");
        	var selectedNode = treeView.select();
        	
        	var targetId = "0";
        	if (selectedNode.length > 0) {
        		var dataItem = treeView.dataItem(selectedNode);
        		targetId = dataItem["id"];
        	}

        	if (targetId == "0" && $("#oper").val() == "Update") {
        		return;
        	}
        	
        	var url = $.trim($("#propForm input[name='url']").val());
        	if (!url) {
        		$("#propForm input[name='scopeKernel']").prop("checked", false);
        		$("#propForm input[name='scopeMedium']").prop("checked", false);
        		$("#propForm input[name='scopeAd']").prop("checked", false);
        	}
        	
        	var data = {
        			id: targetId,
        			ukid: $("#propForm input[name='ukid']").val(),
        			url: url,
        			icon: $("#propForm input[name='icon']").val(),
        			scopeKernel: $("#propForm input[name='scopeKernel']").is(":checked"),
        			scopeMedium: $("#propForm input[name='scopeMedium']").is(":checked"),
        			scopeAd: $("#propForm input[name='scopeAd']").is(":checked"),
        			oper: $("#oper").val(),
        	};
        	
			$.ajax({
				type: "POST",
				contentType: "application/json",
				dataType: "json",
				url: "${createUrl}",
				data: JSON.stringify(data),
				success: function (data, status, xhr) {
					var dataItem = treeView.dataItem(selectedNode);
						
					if ($("#oper").val() == "Update") {
						dataItem.set("text", data.text);
						dataItem.set("id", data.id);
						dataItem.set("custom1", data.custom1);
						dataItem.set("custom2", data.custom2);
						dataItem.set("custom3", data.custom3);
						dataItem.set("custom4", data.custom4);
						dataItem.set("custom5", data.custom5);
						dataItem.set("custom6", data.custom6);
						dataItem.set("custom7", data.custom7);
							
						selectNode(selectedNode);
					} else {
						if (selectedNode.length == 0) {
							selectedNode = null;
						}

						treeView.append({
							childrenCount: 0,
							custom1: data.custom1,
							custom2: data.custom2,
							custom3: data.custom3,
							custom4: data.custom4,
							custom5: data.custom5,
							custom6: data.custom6,
							custom7: data.custom7,
							expanded: false,
							hasChildren: false,
							id: data.id,
							items: null,
							siblingSeq: data.siblingSeq,
							spriteCssClass: data.spriteCssClass,
							text: data.text,
						}, selectedNode);
							
						var newNodeDataItem = treeView.dataSource.get(data.id);
						var newNode = treeView.findByUid(newNodeDataItem.uid);
							
						treeView.select(newNode);
							
						selectNode(newNode);
					}
					showSaveSuccessMsg();
				},
				error: ajaxSaveError
			});
        }
	});
	// / Save
	
	// Delete
	$("#del-btn").click(function(e) {
    	var treeView = $("#menuTree").data("kendoTreeView");
    	var selectedNode = treeView.select();

    	if (selectedNode.length == 0) {
    		return;
    	}
    	
   		var dataItem = treeView.dataItem(selectedNode);
   		
		showDelConfirmModal(function(result) {
			if (result) {
				$.ajax({
					type: "POST",
					contentType: "application/json",
					dataType: "json",
					url: "${destroyUrl}",
					data: JSON.stringify({id: dataItem["id"]}),
					success: function (form) {
						treeView.remove(selectedNode);
						showDeleteSuccessMsg();
						add();
					},
					error: ajaxDeleteError
				});
			}
		});
	});
	// / Delete
	
	// Unlock
	$("#unlock-btn").click(function(e) {
		$("#propForm input[name='ukid']").removeAttr('readonly');
		$("#propForm input[name='url']").removeAttr('readonly');
		$("#propForm input[name='icon']").removeAttr('readonly');
		
		$("#propForm input[name='scopeKernel']").prop('disabled', false);
		$("#propForm input[name='scopeMedium']").prop('disabled', false);
		$("#propForm input[name='scopeAd']").prop('disabled', false);
			
		$("#unlock-btn").hide();
		$("#save-btn").show();
		$("#del-btn").hide();
		$("#cancel-btn").show();
			
		$("#ukid").focus().select();
	});
	// / Unlock
	
	// Cancel
	$("#cancel-btn").click(function(e) {
    	$("#menuTree").data("kendoTreeView").select($());
    	add();
	});
	// / Cancel
});	


// Method: add
function add() {
	$("#propForm input[name='ukid']").val("");
	$("#propForm input[name='url']").val("");
	$("#propForm input[name='icon']").val("");
	$("#propForm input[name='scopeKernel']").prop("checked", false);
	$("#propForm input[name='scopeMedium']").prop("checked", false);
	$("#propForm input[name='scopeAd']").prop("checked", false);

	$("#oper").val("Add");
		
	$("#propForm input[name='ukid']").removeAttr('readonly');
	$("#propForm input[name='url']").removeAttr('readonly');
	$("#propForm input[name='icon']").removeAttr('readonly');
	
	$("#propForm input[name='scopeKernel']").prop('disabled', false);
	$("#propForm input[name='scopeMedium']").prop('disabled', false);
	$("#propForm input[name='scopeAd']").prop('disabled', false);

	$("#unlock-btn").hide();
	$("#save-btn").show();
	$("#del-btn").hide();
	$("#cancel-btn").show();
		
	var selectedNode = $("#menuTree").data("kendoTreeView").select();
	if (selectedNode.length != 0) {
		$("#menuTree").data("kendoTreeView").expand(selectedNode);
	}
}
// / Method: add

// Method: selectNode
function selectNode(node) {
	var dataItem = $("#menuTree").data("kendoTreeView").dataItem(node);

	$("#propForm input[name='ukid']").attr('readonly', 'readonly');
	$("#propForm input[name='url']").attr('readonly', 'readonly');
	$("#propForm input[name='icon']").attr('readonly', 'readonly');

	$("#propForm input[name='scopeKernel']").prop('disabled', true);
	$("#propForm input[name='scopeMedium']").prop('disabled', true);
	$("#propForm input[name='scopeAd']").prop('disabled', true);
	
	$("#propForm input[name='ukid']").val(dataItem["custom1"]);
	$("#propForm input[name='url']").val(dataItem["custom2"]);
	$("#propForm input[name='icon']").val(dataItem["custom3"]);
	$("#propForm input[name='scopeKernel']").prop("checked", dataItem["custom5"] == "Y");
	$("#propForm input[name='scopeMedium']").prop("checked", dataItem["custom6"] == "Y");
	$("#propForm input[name='scopeAd']").prop("checked", dataItem["custom7"] == "Y");
	
	$("#id").val(dataItem["id"]);
	$("#oper").val("Update");

	$("#unlock-btn").show();
	$("#save-btn").hide();
	$("#del-btn").show();
	$("#cancel-btn").show();
}
// / Method: selectNode

</script>

<!--  Scripts -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmValidate />


<!-- One-time page validation  -->

<script>

$(document).ready(function() {
 	// set validation
	$("#propForm").validate({
		rules: {
			ukid: {
				minlength: 2, maxlength: 20, alphanumeric: true,
			},
		}
	});
	
});	

</script>

<!-- / One-time page validation  -->


<!-- Closing tags -->

<common:base />
<common:pageClosing />
