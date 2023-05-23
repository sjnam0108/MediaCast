<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<!-- URL -->

<c:url value="/adc/creative/create" var="createUrl" />
<c:url value="/adc/creative/read" var="readUrl" />
<c:url value="/adc/creative/update" var="updateUrl" />
<c:url value="/adc/creative/destroy" var="destroyUrl" />

<c:url value="/adc/creative/submit" var="submitUrl" />
<c:url value="/adc/creative/recall" var="recallUrl" />
<c:url value="/adc/creative/reject" var="rejectUrl" />
<c:url value="/adc/creative/archive" var="archiveUrl" />
<c:url value="/adc/creative/unarchive" var="unarchiveUrl" />
<c:url value="/adc/creative/pause" var="pauseUrl" />
<c:url value="/adc/creative/resume" var="resumeUrl" />
<c:url value="/adc/creative/copy" var="copyUrl" />

<c:url value="/adc/creative/readTypes" var="readTypeUrl" />
<c:url value="/adc/creative/readStatuses" var="readStatusUrl" />


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
	String editTemplate = 
			"<button type='button' onclick='edit(#= id #)' class='btn icon-btn btn-sm btn-outline-success borderless'>" + 
			"<span class='fas fa-pencil-alt'></span></button>";
			
	String typeTemplate =
			"# if (type == 'C') { #" +
				"<span title='일반 광고'><span class='fa-regular fa-audio-description fa-fw'></span></span>" +
			"# } else if (type == 'F') { #" +
				"<span title='대체 광고'><span class='fa-regular fa-house text-orange fa-fw'></span></span>" +
			"# } #";
	String advertiserTemplate = "<a href='javascript:navToAdv(#= advertiser.id #)'><span class='text-link'>#= advertiser.name #</span></a>";
	String durPolicyTemplate =
			"# if (type == 'F') { #" +
				"# if (durPolicyOverriden) { #" +
					"<span class='fa-light fa-check'></span>" +
				"# } else { #" +
					"<span></span>" +
				"# } #" +
			"# } else { #" +
				"<span>-</span>" +
			"# } #";
	String fbWeightTemplate =
			"# if (type == 'F') { #" +
				"<span>#= fbWeight #</span>" +
			"# } else { #" +
				"<span>-</span>" +
			"# } #";
	String statusTemplate =
			"# if (status == 'D') { #" +
				"<span class='fa-regular fa-asterisk fa-fw'></span><span class='pl-1'>준비</span>" +
			"# } else if (status == 'P') { #" +
				"<span class='fa-regular fa-square-question fa-fw'></span><span class='pl-1'>승인대기</span>" +
			"# } else if (status == 'J') { #" +
				"<span class='fa-regular fa-do-not-enter fa-fw'></span><span class='pl-1'>거절</span>" +
			"# } else if (status == 'A') { #" +
				"<span class='fa-regular fa-square-check text-blue fa-fw'></span><span class='pl-1'>승인</span>" +
			"# } else if (status == 'V') { #" +
				"<span class='fa-regular fa-box-archive fa-fw'></span><span class='pl-1'>보관</span>" +
			"# } else { #" +
				"<span>-</span>" +
			"# } #";
			
	String regDateTemplate = net.doohad.utils.Util.getSmartDate("whoCreationDate");
	String lastPlayDateTemplate = net.doohad.utils.Util.getSmartDate("lastPlayDate");
	
	String nameTemplate =
			"<div>" + 
				"# if (paused == true) { #" +
					"<span title='잠시 멈춤'><span class='fa-regular fa-circle-pause text-danger'></span></span><span class='pr-1'></span>" +
				"# } #" +
				"<a href='javascript:navToCreat(#= advertiser.id #, #= id #)'><span class='text-link'>#= name #</span></a>" +
				"# if (invenTargeted == true) { #" +
					"<span class='pl-1'></span><span title='인벤토리 타겟팅'><span class='fa-regular fa-bullseye-arrow text-blue'></span></span>" +
				"# } #" +
				"# if (timeTargeted == true) { #" +
					"<span class='pl-1'></span><span title='시간 타겟팅'><span class='fa-regular fa-alarm-clock text-green'></span></span>" +
				"# } #" +
			"</div>";
	
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
    			<button id="add-btn" type="button" class="btn btn-outline-success">추가</button>
    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">엑셀</button>
    		</div>
    		<div class="float-right">

<c:if test="${fn:length(otherMedia) > 0}" >   		
    			<div class="btn-group">
					<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
						<span class="fa-light fa-lg fa-wand-sparkles"></span>
						<span class="pl-1">다른 매체로 복사</span>
					</button>
					<div class="dropdown-menu">
				
	<c:forEach var="item" items="${otherMedia}">
				
						<a class="dropdown-item" href="javascript:copyToMedium('${item.shortName}')">
							<span class="fa-light fa-earth-asia"></span>
							<span class="pl-1">${item.shortName}</span>
							<span class='small pl-3'>${item.name}</span>
						</a>
					
	</c:forEach>
	
					</div>    	

</c:if> 		

				</div>
    			<div class="btn-group">
					<button type="button" class="btn btn-secondary dropdown-toggle" data-toggle="dropdown">
						<span class="fa-light fa-lg fa-signs-post"></span>
						<span class="pl-1">진행</span>
					</button>
					<div class="dropdown-menu">
						<a class="dropdown-item" href="javascript:void(0)" id="submit-btn">
							<i class="fa-light fa-paper-plane fa-fw"></i><span class="pl-2">승인 요청</span>
						</a>
						<a class="dropdown-item" href="javascript:void(0)" id="recall-btn">
							<i class="fa-light fa-arrow-rotate-left fa-fw"></i><span class="pl-2">승인 요청 회수</span>
						</a>
						<div class="dropdown-divider"></div>
						<a class="dropdown-item" href="javascript:void(0)" id="reject-btn">
							<i class="fa-light fa-thumbs-down fa-fw"></i><span class="pl-2">승인 소재 거절</span>
						</a>
						<div class="dropdown-divider"></div>
						<a class="dropdown-item" href="javascript:void(0)" id="archive-btn">
							<i class="fa-light fa-box-archive fa-fw"></i><span class="pl-2">보관</span>
						</a>
						<a class="dropdown-item" href="javascript:void(0)" id="unarchive-btn">
							<i class="fa-light fa-box-open fa-fw"></i><span class="pl-2">보관 해제</span>
						</a>
						<div class="dropdown-divider"></div>
						<a class="dropdown-item" href="javascript:void(0)" id="pause-btn">
							<i class="fa-regular fa-circle-pause text-danger fa-fw"></i><span class="pl-2">잠시 멈춤</span>
						</a>
						<a class="dropdown-item" href="javascript:void(0)" id="resume-btn">
							<i class="fa-light fa-play fa-fw"></i><span class="pl-2">재개</span>
						</a>
						<div class="dropdown-divider"></div>
						<a class="dropdown-item" href="javascript:void(0)" id="delete-btn">
							<i class="fa-regular fa-trash-can text-danger fa-fw"></i><span class="pl-2">삭제</span>
						</a>
					</div>    		
				</div>	
    		</div>
    	</div>
	</kendo:grid-toolbarTemplate>
	<kendo:grid-filterable extra="false" />
	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="수정" width="50" filterable="false" sortable="false" template="<%= editTemplate %>" />
		<kendo:grid-column title="유형" field="type" width="100" template="<%= typeTemplate %>" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readTypeUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="광고 소재" field="name" width="250" template="<%= nameTemplate %>" />
		<kendo:grid-column title="등록된 해상도" width="200" sortable="false" filterable="false"
				template="#= dispBadgeValues(fileResolutions) #" />
		<kendo:grid-column title="상태" field="status" width="120" template="<%= statusTemplate %>" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readStatusUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="광고주" field="advertiser.name" width="200" template="<%= advertiserTemplate %>" />
		<kendo:grid-column title="최근 방송" field="lastPlayDate" width="120" filterable="false" sortable="false" template="<%= lastPlayDateTemplate %>" />
		<kendo:grid-column title="잠시 멈춤" field="paused" width="120"
				template="#=paused ? \"<span class='fa-light fa-check'>\" : \"\"#" />
		<kendo:grid-column title="재생시간 정책 무시" field="durPolicyOverriden" width="180" template="<%= durPolicyTemplate %>" />
		<kendo:grid-column title="대체 광고 가중치" field="fbWeight" width="150" template="<%= fbWeightTemplate %>" />
		<kendo:grid-column title="등록" field="whoCreationDate" width="120" template="<%= regDateTemplate %>" />
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="whoCreationDate" dir="desc"/>
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
					<kendo:dataSource-schema-model-field name="paused" type="boolean" />
					<kendo:dataSource-schema-model-field name="durPolicyOverriden" type="boolean" />
					<kendo:dataSource-schema-model-field name="fbWeight" type="number" />
					<kendo:dataSource-schema-model-field name="lastPlayDate" type="date" />
					<kendo:dataSource-schema-model-field name="whoCreationDate" type="date" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

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


/* 링크 텍스트 표시 */
.text-link {
	text-decoration: underline;
	text-decoration-color: #cccccc !important;
	text-underline-offset: 5px;
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
	
	// Submit
	$("#submit-btn").click(function(e) {
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
				url: "${submitUrl}",
				data: JSON.stringify({ items: opRows }),
				success: function (form) {
					showOperationSuccessMsg();
					grid.dataSource.read();
				},
				error: ajaxOperationError
			});
		}
	});
	// / Submit
	
	// Recall
	$("#recall-btn").click(function(e) {
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
				url: "${recallUrl}",
				data: JSON.stringify({ items: opRows }),
				success: function (form) {
					showOperationSuccessMsg();
					grid.dataSource.read();
				},
				error: ajaxOperationError
			});
		}
	});
	// / Recall
	
	// Reject
	$("#reject-btn").click(function(e) {
		e.preventDefault();
			
		var grid = $("#grid").data("kendoGrid");
		var rows = grid.select();
	
		var opRows = [];
		
		rows.each(function(index, row) {
			var selectedItem = grid.dataItem(row);
			opRows.push(selectedItem.id);
		});
		
		if (opRows.length > 0) {
			
			var box = bootbox.prompt({
				size: "small",
				title: "거절 이유",
				backdrop: "static",
				buttons: {
					cancel: {
						label: '취소',
						className: "btn-default",
					},
					confirm: {
						label: '확인',
						className: "btn-danger",
					}
				},
				animate: false,
				callback: function(result) {
					if (result) {
						var realRes = $.trim(result);
						if (realRes) {
							
							$.ajax({
								type: "POST",
								contentType: "application/json",
								dataType: "json",
								url: "${rejectUrl}",
								data: JSON.stringify({ items: opRows, reason: realRes }),
								success: function (form) {
									showOperationSuccessMsg();
									grid.dataSource.read();
									$("#grid").data("kendoGrid").dataSource.read();
								},
								error: ajaxOperationError
							});

						} else {
							showConfirmModal("거절 이유가 입력되지 않아, 자동으로 거절이 취소되었습니다.", function(result) { });
						}
					} else {
						showConfirmModal("거절 이유가 입력되지 않아, 자동으로 거절이 취소되었습니다.", function(result) { });
					}
				},
				className: "modal-level-top",
			}).init(function() {
				setTimeout(function(){
					$('.modal-backdrop:last-child').addClass('modal-level-top');
				});
			});
			
			box.find('.modal-level-top .modal-dialog').addClass("modal-dialog-vertical-center");
			box.find('.modal-level-top .modal-content').addClass("modal-content-border-1");

<c:if test="${not isMobileMode}">

			box.find('.modal-level-top .modal-header').addClass("move-cursor");
			box.find('.modal-level-top .modal-dialog').draggable({ handle: '.modal-header' });

</c:if>

		}
	});
	// / Reject
	
	// Archive
	$("#archive-btn").click(function(e) {
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
				url: "${archiveUrl}",
				data: JSON.stringify({ items: opRows }),
				success: function (form) {
					showOperationSuccessMsg();
					grid.dataSource.read();
				},
				error: ajaxOperationError
			});
		}
	});
	// / Archive
	
	// Unarchive
	$("#unarchive-btn").click(function(e) {
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
				url: "${unarchiveUrl}",
				data: JSON.stringify({ items: opRows }),
				success: function (form) {
					showOperationSuccessMsg();
					grid.dataSource.read();
				},
				error: ajaxOperationError
			});
		}
	});
	// / Unarchive
	
	// Pause
	$("#pause-btn").click(function(e) {
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
				url: "${pauseUrl}",
				data: JSON.stringify({ items: opRows }),
				success: function (form) {
					showOperationSuccessMsg();
					grid.dataSource.read();
				},
				error: ajaxOperationError
			});
		}
	});
	// / Pause
	
	// Resume
	$("#resume-btn").click(function(e) {
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
				url: "${resumeUrl}",
				data: JSON.stringify({ items: opRows }),
				success: function (form) {
					showOperationSuccessMsg();
					grid.dataSource.read();
				},
				error: ajaxOperationError
			});
		}
	});
	// / Resume
	
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
						광고 소재명
						<span class="text-danger">*</span>
					</label>
					<input name="name" type="text" maxlength="100" class="form-control required">
				</div>
				<div class="form-group col">
					<label class="form-label">
						광고주
					</label>
					<select name="advertiser" class="selectpicker bg-white" data-style="btn-default" data-none-selected-text="">
<c:forEach var="item" items="${Advertisers}">
						<option value="${item.value}">${item.text}</option>
</c:forEach>
					</select>
				</div>
				<div class="form-group col">
					<label class="form-label">
						소재 유형
					</label>
					<select name="type" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
						<option value="C" data-icon="fa-regular fa-audio-description fa-fw mr-1">일반 광고</option>
						<option value="F" data-icon="fa-regular fa-house fa-fw text-orange mr-1">대체 광고</option>
					</select>
				</div>
				<div class="form-group col">
					<label class="form-label">
						화면의 재생시간 정책
					</label>
					<div>
						<label class="switcher">
							<input type="checkbox" class="switcher-input check-switch-status" name="durPolicyOverriden">
							<span class="switcher-indicator">
								<span class="switcher-yes"></span>
								<span class="switcher-no"></span>
							</span>
							<span class="switcher-label">매체 화면의 재생시간 정책 무시 허용</span>
						</label>
					</div>
				</div>
				<div class="form-group col">
					<label class="form-label">
						대체 광고간 가중치
					</label>
					<input name="fbWeight" type="text" class="form-control required" value="1">
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

function initFallbackAdAttribute() {
	
	$("#form-1 input[name='durPolicyOverriden']").prop("disabled", true);
	$("#form-1 input[name='fbWeight']").prop("disabled", true);
	
	$("#form-1 input[name='durPolicyOverriden']").prop("checked", false);
	$("#form-1 input[name='fbWeight']").val("1");
}


function initForm1(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-1").html()));
	
	$("#form-1 select[name='advertiser']").selectpicker('render');
	$("#form-1 select[name='type']").selectpicker('render');
	
	$("#form-1 select[name='type']").on("changed.bs.select", 
		function (e, clickedIndex, isSelected, previousValue) {
			var val = $("#form-1 select[name='type']").val();
			
			if (val == "C") {
				// 일반 광고
				initFallbackAdAttribute();
			} else if (val == "F") {
				// 대체 광고
				$("#form-1 input[name='durPolicyOverriden']").prop("disabled", false);
				$("#form-1 input[name='fbWeight']").prop("disabled", false);
			}
	});

	initFallbackAdAttribute();
	
	
	$("#form-1 span[name='subtitle']").text(subtitle ? subtitle : "추가");

	$("#form-1").validate({
		rules: {
			name: {
				minlength: 2,
			},
			fbWeight: {
				digits: true,
				min: 1,
			},
		}
	});
}


function saveForm1() {

	var advertiser = $("#form-1 select[name='advertiser']").val();
	
	if ($("#form-1").valid() && advertiser != "-1") {
    	var data = {
    		id: Number($("#form-1").attr("rowid")),
    		name: $.trim($("#form-1 input[name='name']").val()),
    		advertiser: Number($("#form-1 select[name='advertiser']").val()),
    		type: $("#form-1 select[name='type']").val(),
    		durPolicyOverriden: $("#form-1 input[name='durPolicyOverriden']").is(':checked'),
    		fbWeight: Number($.trim($("#form-1 input[name='fbWeight']").val())),
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
	$("#form-1 input[name='fbWeight']").val(dataItem.fbWeight);

	bootstrapSelectVal($("#form-1 select[name='advertiser']"), dataItem.advertiser.id);
	bootstrapSelectVal($("#form-1 select[name='type']"), dataItem.type);
	
	$("#form-1 input[name='durPolicyOverriden']").prop("checked", dataItem.durPolicyOverriden);
	
	if (dataItem.type == "F") {
		$("#form-1 input[name='durPolicyOverriden']").prop("disabled", false);
		$("#form-1 input[name='fbWeight']").prop("disabled", false);
	} else {
		$("#form-1 input[name='durPolicyOverriden']").prop("disabled", true);
		$("#form-1 input[name='fbWeight']").prop("disabled", true);
	}
	
	bootstrapSelectDisabled($("#form-1 select[name='advertiser']"), true);

	
	$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-1").modal();
}


function navToCreat(advId, id) {
	var path = "/adc/creative/files/" + advId + "/" + id;
	location.href = path;
}


function navToAdv(advId) {
	var path = "/adc/creative/creatives/" + advId;
	location.href = path;
}


function copyToMedium(shortName) {
	
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
			url: "${copyUrl}",
			data: JSON.stringify({ items: opRows, mediumID: shortName }),
			success: function (form) {
				showOperationSuccessMsg();
				hideWaitModal();
			},
			error: function(e) {
				hideWaitModal();
				ajaxOperationError(e);
			}
		});
	}
}


function dispBadgeValues(values) {
	
	var ret = "";
	var value = values.split("|");
	  
	for(var i = 0; i < value.length; i ++) {
		if (value[i]) {
			var item = value[i].split(":");
			if (item.length == 2) {
				if (Number(item[0]) == 1) {
					ret = ret + "<span class='badge badge-outline-success'>";
				} else if (Number(item[0]) == 0) {
					ret = ret + "<span class='badge badge-outline-secondary'>";
				} else {
					ret = ret + "<span class='badge badge-outline-danger'>";
				}
				
				ret = ret + item[1] + "</span><span class='pl-1'></span>";
			}
		}
	}
	  
	return ret;
}

</script>

<!--  / Scripts -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
