<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/rev/pendappr/readPAs" var="readPAUrl" />
<c:url value="/rev/pendappr/readCreatFiles" var="readCreatFileUrl" />
<c:url value="/rev/pendappr/readCreatDecns" var="readCreatDecnUrl" />

<c:url value="/rev/pendappr/approve" var="approveUrl" />
<c:url value="/rev/pendappr/reject" var="rejectUrl" />

<c:url value="/rev/pendappr/readStatuses" var="readStatusUrl" />
<c:url value="/rev/pendappr/readActStatuses" var="readActStatusUrl" />


<!-- Opening tags -->

<common:mediumPageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}
</h4>

<hr class="border-light container-m--x mt-0 mb-4">





<!-- Page body -->


<!-- Page scripts  -->


<!-- Java(optional)  -->

<%
	String nameTemplate =
			"# if (type == 'C') { #" +
				"<span title='일반 광고'><span class='fa-regular fa-audio-description fa-fw'></span></span>" +
			"# } else if (type == 'F') { #" +
				"<span title='대체 광고'><span class='fa-regular fa-house text-orange fa-fw'></span></span>" +
			"# } #" +
			"<span class='pl-1'>#= name #</span>";
	String advertiserTemplate = "<span>#= advertiser.name #</span>";
	
	String submitDateTemplate = net.doohad.utils.Util.getSmartDate("submitDate", false);
	
	String noRecordsTemplate =
			"<div class='container text-center my-4'>" +
				"<div class='d-flex justify-content-center align-self-center'>" +
					"<span class='fa-thin fa-hexagon-exclamation fa-3x'></span>" +
					"<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>해당 자료 없음</span>" +
				"</div>" +
			"</div>";
			
	java.util.HashMap<String, Object> data = new java.util.HashMap<String, Object>();
	data.put("reqIntValue1", "#=id#");

	String thumbTemplate = "<img class='detailImg' width='128' height='128'>";
	String fileTemplate = "<span class='pr-2'><span class='detailFileSpan'></span></span>" +
			"<a target='_blank' class='detailBlankImg'><span class='fa-light fa-arrow-up-right-from-square'></span></a>";
	String resTemplate = "<span class='badge badge-outline-secondary'><span class='detailResSpan'></span></span>";
	String durTemplate = "<div class='dur-container'><span data-toggle='tooltip' data-placement='top' class='detailDurTipSpan'><span class='detailDurSpan'></span></span></div>";
	String lengthTemplate = "<div class='len-container'><span data-toggle='tooltip' data-placement='top' class='detailLenTipSpan'><span class='detailLenSpan'></span></span></div>";
	
	String nameTemplate2 =
			"# if (creative.type == 'C') { #" +
				"<span title='일반 광고'><span class='fa-regular fa-audio-description fa-fw'></span></span>" +
			"# } else if (creative.type == 'F') { #" +
				"<span title='대체 광고'><span class='fa-regular fa-house text-orange fa-fw'></span></span>" +
			"# } #" +
			"<span class='pl-1'>#= creative.name #</span>";
	String advertiserTemplate2 = "<span>#= creative.advertiser.name #</span>";
	String actDateTemplate = net.doohad.utils.Util.getSmartDate("actDate", false);
	String actStatusTemplate =
			"# if (status == 'A') { #" +
				"<span class='fa-regular fa-square-check text-blue fa-fw'></span><span class='pl-1'>승인</span>" +
			"# } else if (status == 'J') { #" +
				"<span class='fa-regular fa-do-not-enter fa-fw'></span><span class='pl-1'>거절</span>" +
				"<span class='pl-2 tip-container'></span>" +
				"<span data-toggle='tooltip' data-placement='right' title='#= reason #'>" +
				"<span id='tip-#= id#'><span class='fa-regular fa-circle-info text-info'></span></span></span>" +
			"# } else { #" +
				"<span>-</span>" +
			"# } #";
	String statusTemplate =
			"# if (creative.status == 'D') { #" +
				"<span class='fa-regular fa-asterisk fa-fw'></span><span class='pl-1'>준비</span>" +
			"# } else if (creative.status == 'P') { #" +
				"<span class='fa-regular fa-square-question fa-fw'></span><span class='pl-1'>승인대기</span>" +
			"# } else if (creative.status == 'J') { #" +
				"<span class='fa-regular fa-do-not-enter fa-fw'></span><span class='pl-1'>거절</span>" +
			"# } else if (creative.status == 'A') { #" +
				"<span class='fa-regular fa-square-check text-blue fa-fw'></span><span class='pl-1'>승인</span>" +
			"# } else if (creative.status == 'V') { #" +
				"<span class='fa-regular fa-box-archive fa-fw'></span><span class='pl-1'>보관</span>" +
			"# } else { #" +
				"<span>-</span>" +
			"# } #";
			
%>

<!-- Kendo grid  -->

<div class="mb-4">
<div class="card" style="border-bottom: transparent;">
	<h6 class="card-header with-elements">
		<div class="card-header-title py-1">
			<span class="fa-light fa-photo-film fa-lg"></span>
			<span class="ml-1 font-weight-light" style="font-size: 1.2rem;">광고 소재</span>
		</div>
	</h6>
</div>
<kendo:grid name="grid" pageable="true" filterable="true" sortable="true" scrollable="true" 
		reorderable="true" resizable="true" selectable="multiple" detailTemplate="template">
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
    <kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    		</div>
    		<div class="float-right">
    			<button id="approve-btn" type="button" class="btn btn-outline-success">
					<span class="fa-regular fa-thumbs-up"></span>
					<span class="pl-1">승인</span>
				</button>
    			<button id="reject-btn" type="button" class="btn btn-outline-danger">
					<span class="fa-regular fa-thumbs-down"></span>
					<span class="pl-1">거절</span>
    			</button>
    		</div>
    	</div>
   	</kendo:grid-toolbarTemplate>
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="소재명" field="name" width="250" template="<%= nameTemplate %>" />
		<kendo:grid-column title="등록된 해상도" width="200" sortable="false" filterable="false"
				template="#= dispBadgeValues(fileResolutions) #" />
		<kendo:grid-column title="광고주" field="advertiser.name" width="250" template="<%= advertiserTemplate %>" />
		<kendo:grid-column title="승인 요청" field="submitDate" width="120" template="<%= submitDateTemplate %>" />
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:grid-dataBound>
		<script>
			function grid_dataBound(e) {
				this.expandRow(this.tbody.find("tr.k-master-row").first());
        	}
		</script>
	</kendo:grid-dataBound>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="id" dir="desc"/>
		</kendo:dataSource-sort>
       	<kendo:dataSource-filter>
   			<kendo:dataSource-filterItem field="medium.id" operator="eq" logic="and" value="${sessionScope['currMediumId']}" >
   			</kendo:dataSource-filterItem>
    	</kendo:dataSource-filter>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readPAUrl}" dataType="json" type="POST" contentType="application/json"/>
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
					<kendo:dataSource-schema-model-field name="submitDate" type="date" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
<kendo:grid-detailTemplate id="template">
	<kendo:grid name="grid_#=id#" pageable="false" filterable="false" sortable="false" scrollable="false" dataBound="dataBound1">
		<kendo:grid-pageable refresh="false" previousNext="false" numeric="false" pageSize="10000" info="false" />
	   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
		<kendo:grid-columns>
			<kendo:grid-column title="썸네일" field="thumbFilename" template="<%= thumbTemplate %>" />
			<kendo:grid-column title="파일명" field="srcFilename" width="250" template="<%= fileTemplate %>" />
			<kendo:grid-column title="해상도" field="resolution" width="100" template="<%= resTemplate %>" />
			<kendo:grid-column title="파일형식" field="mimeType" width="100" />
			<kendo:grid-column title="재생시간" field="durSecs" width="120" template="<%= durTemplate %>" />
			<kendo:grid-column title="파일크기" field="fileLength" width="120" template="<%= lengthTemplate %>" />
		</kendo:grid-columns>
		<kendo:grid-dataBound>
			<script>
				function grid_dataBound(e) {
					var rows = this.dataSource.view();
	    			
					for (var i = 0; i < rows.length; i ++) {
						$(this.tbody.find("tr[data-uid='" + rows[i].uid + "'] .detailImg"))
								.attr("src", "/thumbs/" + rows[i].ctntFolderName + "/" + rows[i].thumbFilename);
						$(this.tbody.find("tr[data-uid='" + rows[i].uid + "'] .detailFileSpan"))
								.text(rows[i].srcFilename);
						$(this.tbody.find("tr[data-uid='" + rows[i].uid + "'] .detailBlankImg"))
								.attr("href", rows[i].ctntFolderWebPath + "/" + rows[i].ctntFolderName + "/" +
										rows[i].uuid + "/" + rows[i].filename);
						$(this.tbody.find("tr[data-uid='" + rows[i].uid + "'] .detailResSpan"))
								.text(rows[i].resolution);
						$(this.tbody.find("tr[data-uid='" + rows[i].uid + "'] .detailDurTipSpan"))
								.attr("title", rows[i].dispSrcDurSecs);
						$(this.tbody.find("tr[data-uid='" + rows[i].uid + "'] .detailDurSpan"))
								.text(rows[i].durSecs);
						$(this.tbody.find("tr[data-uid='" + rows[i].uid + "'] .detailLenTipSpan"))
								.attr("title", rows[i].dispFileLength);
						$(this.tbody.find("tr[data-uid='" + rows[i].uid + "'] .detailLenSpan"))
								.text(rows[i].smartLength);
					}

					$('[data-toggle="tooltip"]').tooltip({
						   container: '.dur-container'
					});
	        	}
			</script>
		</kendo:grid-dataBound>
		<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
			<kendo:dataSource-transport>
				<kendo:dataSource-transport-read url="${readCreatFileUrl}" data="<%= data %>" 
						type="POST" contentType="application/json" />
					<kendo:dataSource-transport-parameterMap>
					<script>
						function parameterMap(options) { 
							return JSON.stringify(options);
						}
					</script>
					</kendo:dataSource-transport-parameterMap>
			</kendo:dataSource-transport>
		</kendo:dataSource>
	</kendo:grid>
</kendo:grid-detailTemplate>
</div>

<!-- / Kendo grid  -->


<!-- Kendo grid  -->

<div class="mb-4">
<div class="card" style="border-bottom: transparent;">
	<h6 class="card-header with-elements">
		<div class="card-header-title py-1">
			<span class="fa-light fa-clock-rotate-left fa-lg"></span>
			<span class="ml-1 font-weight-light" style="font-size: 1.2rem;">승인/거절 이력</span>
		</div>
	</h6>
</div>
<kendo:grid name="grid-decn" pageable="true" filterable="true" sortable="true" scrollable="true" 
		reorderable="true" resizable="true" selectable="single">
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="소재명" field="creative.name" width="250" template="<%= nameTemplate2 %>" />
		<kendo:grid-column title="광고주" field="creative.advertiser.name" sortable="false" filterable="false" width="200" template="<%= advertiserTemplate2 %>" />
		<kendo:grid-column title="현재 상태" field="creative.status" width="120" template="<%= statusTemplate %>" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readStatusUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="어떻게?" field="status" width="120" template="<%= actStatusTemplate %>" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readActStatusUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="누가?" field="actedBy" width="120" filterable="false" template="#= actedByShortName #" />
		<kendo:grid-column title="언제?" field="actDate" width="120" template="<%= actDateTemplate %>" />
		<kendo:grid-column title="승인 요청" field="submitDate" width="120" template="<%= submitDateTemplate %>" />
	</kendo:grid-columns>
		<kendo:grid-dataBound>
			<script>
				function grid_dataBound(e) {
					var rows = this.dataSource.view();
	    			
					for (var i = 0; i < rows.length; i ++) {
						$(this.tbody.find("tr[data-uid='" + rows[i].uid + "'] [data-toggle='tooltip']")).tooltip({
							container: '#tip-' + rows[i].id
						});
					}
	        	}
			</script>
		</kendo:grid-dataBound>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="id" dir="desc"/>
		</kendo:dataSource-sort>
       	<kendo:dataSource-filter>
   			<kendo:dataSource-filterItem field="medium.id" operator="eq" logic="and" value="${sessionScope['currMediumId']}" >
   			</kendo:dataSource-filterItem>
    	</kendo:dataSource-filter>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readCreatDecnUrl}" dataType="json" type="POST" contentType="application/json"/>
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
					<kendo:dataSource-schema-model-field name="actDate" type="date" />
					<kendo:dataSource-schema-model-field name="submitDate" type="date" />
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

	$('[data-toggle="tooltip"]').tooltip();

	// Approve
	$("#approve-btn").click(function(e) {
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
				url: "${approveUrl}",
				data: JSON.stringify({ items: opRows }),
				success: function (form) {
					showOperationSuccessMsg();
					grid.dataSource.read();
					$("#grid-decn").data("kendoGrid").dataSource.read();
				},
				error: ajaxOperationError
			});
		}
	});
	// / Approve
	
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
									$("#grid-decn").data("kendoGrid").dataSource.read();
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
	
});	
</script>

<!-- / Grid button actions  -->


<!--  Forms -->


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


/* 그리드 행의 높이 지정 */
.k-grid tbody tr, .k-grid tbody tr td
{
    height: 40px;
}

</style>


<!--  / Forms -->


<!--  Scripts -->

<script>

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


<!-- Closing tags -->

<common:base />
<common:pageClosing />
