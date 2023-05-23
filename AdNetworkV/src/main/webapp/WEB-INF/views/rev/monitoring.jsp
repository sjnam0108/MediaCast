<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/rev/monitoring/readScr" var="readScrUrl" />
<c:url value="/rev/monitoring/readApiLog" var="readApiLogUrl" />

<c:url value="/rev/monitoring/readResolutions" var="readResolutionUrl" />
<c:url value="/rev/monitoring/readStatuses" var="readStatusUrl" />
<c:url value="/rev/monitoring/recalcScr" var="recalcScrUrl" />


<!-- Opening tags -->

<common:mediumPageOpeningNaverMap />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}
</h4>





<!-- Page body -->


<!-- Java(optional)  -->

<%
	String statusTemplate =
			"# if (lastStatus == '6') { #" +
				"<span title='10분내 요청'><span class='fa-solid fa-flag-swallowtail text-blue'></span></span>" +
			"# } else if (lastStatus == '5') { #" +
				"<span title='1시간내 요청'><span class='fa-solid fa-flag-swallowtail text-green'></span></span>" +
			"# } else if (lastStatus == '4') { #" +
				"<span title='6시간내 요청'><span class='fa-solid fa-flag-swallowtail text-yellow'></span></span>" +
			"# } else if (lastStatus == '3') { #" +
				"<span title='24시간내 요청'><span class='fa-solid fa-flag-swallowtail text-orange'></span></span>" +
			"# } else if (lastStatus == '1') { #" +
				"<span title='24시간내 없음'><span class='fa-solid fa-flag-pennant text-danger'></span></span>" +
			"# } else if (lastStatus == '0') { #" +
				"<span title='기록 없음'><span class='fa-solid fa-flag-pennant text-secondary'></span></span>" +
			"# } #";

	String nameTemplate =
			"<div class='d-flex align-items-center'>" +
				//"<span class='pl-1'><a href='javascript:void(0)' class='stb-status-popover' tabindex='0'>#= name #</a></span>" +
				"<span class='pl-1'>#= name #</span>" +
				"<a href='javascript:showScreen(#= id #,\"#= name #\")' class='btn btn-default btn-xs icon-btn ml-1'><span class='fas fa-search text-info'></span></a>" +
				"<a href='javascript:goSelLogTab(\"#= shortName #\")' class='btn btn-default btn-xs icon-btn ml-1'><span class='fa-regular fa-arrow-right'></span></a>" +
			"</div>";
			
	String apiTemplate = 
			"<a href='" + request.getAttribute("apiTestServer") + "/v1/files/#= shortName #?apikey=#= medium.apiKey #&test=y' class='badge badge-pill badge-warning mr-1' target='_blank'><span class='pr-1'>file</span><span class='fa-light fa-arrow-up-right-from-square'></span></a>" +
			"<a href='" + request.getAttribute("apiTestServer") + "/v1/ad/#= shortName #?apikey=#= medium.apiKey #&test=y' class='badge badge-pill badge-warning mr-1' target='_blank'><span class='pr-1'>ad</span><span class='fa-light fa-arrow-up-right-from-square'></span></a>";
	
	String fileApiDateTemplate = net.doohad.utils.Util.getSmartDate("lastFileApiDate");
	String adRequestDateTemplate = net.doohad.utils.Util.getSmartDate("lastAdRequestDate");
	String adReportDateTemplate = net.doohad.utils.Util.getSmartDate("lastAdReportDate");
	String retryReportDateTemplate = net.doohad.utils.Util.getSmartDate("lastRetryReportDate");
	
	String noRecordsTemplate =
			"<div class='container text-center my-4'>" +
				"<div class='d-flex justify-content-center align-self-center'>" +
					"<span class='fa-thin fa-hexagon-exclamation fa-3x'></span>" +
					"<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>해당 자료 없음</span>" +
				"</div>" +
			"</div>";
			

	String selectAdDateTemplate = net.doohad.utils.Util.getSmartDate("selectDate");
	String playBeginDateTemplate = net.doohad.utils.Util.getSmartDate("beginDate");
	String playEndDateTemplate = net.doohad.utils.Util.getSmartDate("endDate");
	String reportDateTemplate = net.doohad.utils.Util.getSmartDate("reportDate");
	
	String resultTemplate =
			"# if (result && retryReported) { #" +
				"<span class='fa-light fa-flag-checkered text-danger'></span>" +
			"# } else if (result || adName == null) { #" +
				"<span class='fa-light fa-flag-checkered'></span>" +
			"# } else { #" +
				"<span></span>" +
			"# } #";
	
	String adNameTemplate = 
			"# if (adName) { #" + 
				"<div>" + 
					"# if (purchType == 'G') { #" +
						"<span title='목표 보장' class='pr-1'><span class='fa-regular fa-hexagon-check text-blue fa-fw'></span></span>" +
					"# } else if (purchType == 'N') { #" +
						"<span title='목표 비보장' class='pr-1'><span class='fa-regular fa-hexagon-exclamation fa-fw'></span></span>" +
					"# } else if (purchType == 'H') { #" +
						"<span title='하우스 광고' class='pr-1'><span class='fa-regular fa-house fa-fw'></span></span>" +
					"# } #" +
					"<span>#= adName #</span>" + 
				"</div>" +
			"# } else { #" +
				"<span class='badge badge-outline-secondary' style='font-weight: 300;'>대체</span><span class='pl-1'></span>" +
				"<span>#= creativeName #</span>" + 
			"# } #";
%>


<!--  Forms -->

<ul class="nav nav-tabs tabs-alt container-p-x container-m--x mb-4">
	<li class="nav-item">
		<a class="nav-link active" data-toggle="tab" href="#active-screen">
			<i class="mr-1 fa-light fa-flag-swallowtail"></i>
			활성 화면
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#ad-sel-log">
			<i class="mr-1 fa-light fa-table-list"></i>
			광고 선택/보고 로그
		</a>
	</li>
</ul>

<div class="tab-content">
	<div class="tab-pane active" id="active-screen">
	
	
	
	

<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid-screen" pageable="true" filterable="true" sortable="true" scrollable="true" reorderable="true" resizable="true" selectable="multiple" >
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
    <kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button id="recalc-scr-btn" type="button" class="btn btn-default">
    				상태 재계산
    				<span class="pl-0"></span>
					<span data-toggle="tooltip" data-placement="top" title="최근 상태에 대한 재계산을 수행하면 보다 정확한 값을 확인할 수 있습니다.">
						<span class="fa-regular fa-circle-info text-info"></span>
					</span>
    			</button>
    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">엑셀</button>
    		</div>
    		<div class="float-right">
    		</div>
    	</div>
   	</kendo:grid-toolbarTemplate>
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="상태" field="lastStatus" width="80" template="<%= statusTemplate %>" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readStatusUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="화면명" field="name" width="250" template="<%= nameTemplate %>" />
		<kendo:grid-column title="화면ID" field="shortName" width="150" />
		<kendo:grid-column title="해상도" field="resolution" width="150" 
				template="#= resolution.replace('x', ' x ') #" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcTextOnly">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readResolutionUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="API 테스트" width="150" template="<%= apiTemplate %>" />
		<kendo:grid-column title="최근 파일정보 요청" filterable="false" sortable="false" field="lastFileApiDate" width="180" template="<%= fileApiDateTemplate %>" />
		<kendo:grid-column title="최근 현재광고 요청" filterable="false" sortable="false" field="lastAdRequestDate" width="180" template="<%= adRequestDateTemplate %>" />
		<kendo:grid-column title="최근 방송완료 보고" filterable="false" sortable="false" field="lastAdReportDate" width="180" template="<%= adReportDateTemplate %>" />
		<kendo:grid-column title="최근 누락 완료보고" filterable="false" sortable="false" field="lastRetryReportDate" width="180" template="<%= retryReportDateTemplate %>" />
		<kendo:grid-column title="시/군/구" field="site.regionName" width="150" />
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="lastStatus" dir="desc"/>
			<kendo:dataSource-sortItem field="name" dir="asc"/>
		</kendo:dataSource-sort>
       	<kendo:dataSource-filter>
   			<kendo:dataSource-filterItem field="medium.id" operator="eq" logic="and" value="${sessionScope['currMediumId']}" >
   			</kendo:dataSource-filterItem>
    	</kendo:dataSource-filter>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readScrUrl}" dataType="json" type="POST" contentType="application/json"/>
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
					<kendo:dataSource-schema-model-field name="activeStatus" type="boolean" />
					<kendo:dataSource-schema-model-field name="defaultDurSecs" type="number" />
					<kendo:dataSource-schema-model-field name="rangeDurAllowed" type="boolean" />
					<kendo:dataSource-schema-model-field name="videoAllowed" type="boolean" />
					<kendo:dataSource-schema-model-field name="imageAllowed" type="boolean" />
					<kendo:dataSource-schema-model-field name="adServerAvailable" type="boolean" />
					<kendo:dataSource-schema-model-field name="lastFileApiDate" type="date" />
					<kendo:dataSource-schema-model-field name="lastAdRequestDate" type="date" />
					<kendo:dataSource-schema-model-field name="lastAdReportDate" type="date" />
					<kendo:dataSource-schema-model-field name="lastRetryReportDate" type="date" />
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

	// Recalc
	$("#recalc-scr-btn").click(function(e) {
		e.preventDefault();
		
		var msg = "자료 수가 많을 경우 다소 많은 시간이 소요될 예정입니다. 계속 진행하시겠습니까?";

		showConfirmModal(msg, function(result) {
			if (result) {
				showWaitModal();
		    	
				$.ajax({
					type: "POST",
					contentType: "application/json",
					dataType: "json",
					url: "${recalcScrUrl}",
					data: JSON.stringify({ }),
					success: function (data, status, xhr) {
						showOperationSuccessMsg();
						hideWaitModal();
						
						var grid = $("#grid-screen").data("kendoGrid");
						grid.dataSource.read();
					},
					error: function(e) {
						hideWaitModal();
						ajaxOperationError(e);
					}
				});
			}
		});
	});
	// / Recalc
	
});	
</script>

<!-- / Grid button actions  -->
	



	
	</div>
	<div class="tab-pane" id="ad-sel-log">
	
	
	
<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid-api" pageable="true" filterable="true" sortable="true" scrollable="true" reorderable="true" resizable="true" selectable="single" >
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
    <kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">엑셀</button>
    		</div>
    		<div class="float-right  d-flex align-items-center">
    			<div id="screen-div" style="display:none;">
    				<span id="screen-name">화면명</span>
					<a href='javascript:showThisScreen()' class='btn btn-default btn-xs icon-btn ml-1'><span class='fas fa-search text-info'></span></a>
    				<span class="px-2">
	    				<span class="fa-solid fa-pipe text-muted"></span>
    				</span>
    			</div>
    			<input id="api-screen-ID" type="text" maxlength="100" class="form-control mr-1" placeholder="화면ID" style="width: 150px;">
    			<button id="query-btn" type="button" class="btn btn-default">조회</button>
    		</div>
    	</div>
   	</kendo:grid-toolbarTemplate>
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="광고 선택" field="selectDate" width="130" template="<%= selectAdDateTemplate %>"/>
		<kendo:grid-column title="광고" field="adName" width="250" template="<%= adNameTemplate %>" />
		<kendo:grid-column title="결과" field="result" width="80" filterable="false" template="<%= resultTemplate %>" />
		<kendo:grid-column title="시작" field="beginDate" width="120" template="<%= playBeginDateTemplate %>"/>
		<kendo:grid-column title="종료" field="endDate" width="120" template="<%= playEndDateTemplate %>"/>
		<kendo:grid-column title="재생시간" field="duration" width="120" template="#= durDisp #" />
		<kendo:grid-column title="보고" field="reportDate" width="120" template="<%= reportDateTemplate %>"/>
		<kendo:grid-column title="광고 소재" field="creativeName" width="200" />
		<kendo:grid-column title="누락보고" field="retryReported" width="130"
				template="#=retryReported ? \"<span class='fa-light fa-check'>\" : \"\"#" />
		<kendo:grid-column title="UUID" field="uuid" width="300" />
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:grid-dataBound>
		<script>
			function grid_dataBound(e) {
				var grid = e.sender;
				var rows = grid.dataSource.view();

				if (rows.length > 0) {
					apiScrId = rows[0].screenId;
					apiScrName = rows[0].screenName;
					
					showScreenInfo();
				}
			}
		</script>
	</kendo:grid-dataBound>
	<kendo:dataSource serverPaging="false" serverSorting="false" serverFiltering="false" serverGrouping="false" error="kendoReadError">
		<kendo:dataSource-requestStart>
			<script>
				function dataSource_requestStart(e) {
					hideScreenInfo();
				}
			</script>
		</kendo:dataSource-requestStart>
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="selectDate" dir="desc"/>
		</kendo:dataSource-sort>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readApiLogUrl}" dataType="json" type="POST" contentType="application/json">
				<kendo:dataSource-transport-read-data>
					<script>
						function additionalData(e) {
							return { reqStrValue1: $("#api-screen-ID").val() };
						}
					</script>
				</kendo:dataSource-transport-read-data>
			</kendo:dataSource-transport-read>
			<kendo:dataSource-transport-parameterMap>
				<script>
					function parameterMap(options,type) {
						return JSON.stringify(options);	
					}
				</script>
			</kendo:dataSource-transport-parameterMap>
		</kendo:dataSource-transport>
		<kendo:dataSource-schema>
			<kendo:dataSource-schema-model id="id">
				<kendo:dataSource-schema-model-fields>
					<kendo:dataSource-schema-model-field name="selectDate" type="date" />
					<kendo:dataSource-schema-model-field name="beginDate" type="date" />
					<kendo:dataSource-schema-model-field name="endDate" type="date" />
					<kendo:dataSource-schema-model-field name="reportDate" type="date" />
					<kendo:dataSource-schema-model-field name="duration" type="number" />
					<kendo:dataSource-schema-model-field name="result" type="boolean" />
					<kendo:dataSource-schema-model-field name="retryReported" type="boolean" />
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

	// Query
	$("#query-btn").click(function(e) {
		e.preventDefault();

		var grid = $("#grid-api").data("kendoGrid");
		grid.dataSource.read();
	});
	// / Query


<c:if test="${not empty screenID}">

	$('a[href="#ad-sel-log"]').tab('show');
	$("#api-screen-ID").val("${screenID}");

	var grid = $("#grid-api").data("kendoGrid");
	grid.dataSource.read();

</c:if>
	
});


var apiScrId = -1;
var apiScrName = "";


function showScreenInfo() {

	$("#screen-name").text(apiScrName);
	$("#screen-div").show();
}


function hideScreenInfo() {

	apiScrId = -1;
	apiScrName = "";
	
	$("#screen-name").text(apiScrName);
	$("#screen-div").hide();
}


function showThisScreen() {
	
	if (apiScrId > 0 && apiScrName) {
		showScreen(apiScrId, apiScrName);
	}
}


function goSelLogTab(screenID) {
		
		var path = "/rev/monitoring?screen=" + screenID;
		location.href = path;
}

</script>

<!-- / Grid button actions  -->
	



	
	</div>
</div>


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


<!-- / Page body -->





<!-- Functional tags -->

<func:screenInfoModal />
<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
