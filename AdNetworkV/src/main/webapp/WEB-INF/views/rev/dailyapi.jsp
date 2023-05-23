<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/rev/dailyapi/readScrTot" var="readScrTotUrl" />
<c:url value="/rev/dailyapi/readSitTot" var="readSitTotUrl" />
<c:url value="/rev/dailyapi/readScrFailTot" var="readScrFailTotUrl" />
<c:url value="/rev/dailyapi/readScrNoAdTot" var="readScrNoAdTotUrl" />
<c:url value="/rev/dailyapi/readScrFbTot" var="readScrFbTotUrl" />

<c:url value="/rev/monitoring/readScr" var="readUrl" />

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

<hr class="border-light container-m--x mt-0 mb-3">





<!-- Page body -->


<!--  HTML tags -->

<div class="clearfix">
	<div class="float-left ml-3 lead header_title">${currDateTitle}</div>
	<div class="float-right mt-2">
		<div class="btn-group mr-2">
			<button id="prev-btn" type="button" class="btn btn-secondary">
				<span class="fa-regular fa-angle-left fa-lg"></span>
			</button>
			<button id="calendar-btn" type="button" class="btn btn-secondary">
				<span class="fa-light fa-calendar-days fa-lg"></span>
			</button>
			<button id="next-btn" type="button" class="btn btn-secondary">
				<span class="fa-regular fa-angle-right fa-lg"></span>
			</button>
		</div>
		<div class="btn-group mr-2">
			<button id="today-btn" type="button" class="btn btn-secondary">
				오늘
			</button>
		</div>
	</div>
</div>

<style>

.header_title {
	font-size: 35px;
	font-weight: 300;
}
.header_title small {
	font-size: 20px;
	font-weight: 300;
}

</style>

<!--  / HTML tags -->


<!--  Scripts -->

<script>

function navigateToDate(date) {
	
	showWaitModal();
	location.href = "/rev/dailyapi?date=" + date
}


$(document).ready(function() {

	$('[data-toggle="tooltip"]').tooltip();

	// Prev
	$("#prev-btn").click(function(e) {
		e.preventDefault();
		
		navigateToDate("${prevDate}");
	});
	// / Prev

	
	// Next
	$("#next-btn").click(function(e) {
		e.preventDefault();
		
		navigateToDate("${nextDate}");
	});
	// / Next

	
	// Today
	$("#today-btn").click(function(e) {
		e.preventDefault();
		
		navigateToDate("${today}");
	});
	// / Today

	
	// Calendar
	$("#calendar-btn").click(function(e) {
		e.preventDefault();
		
		initForm1();

		
		$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-1").modal();
	});
	// / Calendar
	
});	
</script>

<!--  / Scripts -->

<div class="card">
	<div class="row no-gutters row-bordered row-border-light">
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-plug fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">광고 요청 합계</div>
					<div class="text-large">${stat.sumRequest}</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-flag-checkered fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">성공 합계</div>
					<div class="text-large">${stat.sumSucc}</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-cloud-question fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">실패 합계</div>
					<div class="text-large">${stat.sumFail}</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="my-2">
					<span class='badge badge-outline-secondary' style='font-weight: 300;'>대체</span>
				</span>
				<div class="ml-3">
					<div class="text-muted small">대체광고 합계</div>
					<div class="text-large">${stat.sumFb}</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-empty-set fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">광고없음 합계</div>
					<div class="text-large">${stat.sumNoAd}</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-screen-users fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">화면 수</div>
					<div class="text-large">${stat.cntScr}</div>
				</div>
			</div>
		</div>
	</div>
	<hr class="m-0">
	<div class="row no-gutters row-bordered row-border-light">
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-plug fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">화면당 광고 요청</div>
					<div class="text-large">${stat.avgRequest}</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-flag-checkered fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">화면당 성공</div>
					<div class="text-large">${stat.avgSucc}</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-cloud-question fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">화면당 실패</div>
					<div class="text-large">${stat.avgFail}</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="my-2">
					<span class='badge badge-outline-secondary' style='font-weight: 300;'>대체</span>
				</span>
				<div class="ml-3">
					<div class="text-muted small">화면당 대체광고</div>
					<div class="text-large">${stat.avgFb}</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-empty-set fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">화면당 광고없음</div>
					<div class="text-large">${stat.avgNoAd}</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-map-pin fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">사이트 수</div>
					<div class="text-large">${stat.cntSit}</div>
				</div>
			</div>
		</div>
	</div>
	<hr class="m-0">
	<div class="row no-gutters row-bordered row-border-light">
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-ruler-combined fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">표준 편차</div>
					<div class="text-large">${stat.stdRequest}</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-percent fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">성공 비율</div>
					<div class="text-large">${stat.pctSucc}</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-percent fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">실패 비율</div>
					<div class="text-large">${stat.pctFail}</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-percent fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">대체광고 비율</div>
					<div class="text-large">${stat.pctFb}</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-percent fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">광고없음 비율</div>
					<div class="text-large">${stat.pctNoAd}</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
			</div>
		</div>
	</div>
</div>

<!--  / HTML tags -->

<!-- Page scripts  -->


<!-- Java(optional)  -->

<%
	String screenNameTemplate =
			"# if (screen.deleted == true) { #" +
				"<span title='삭제 처리'><span class='fa-regular fa-trash-can text-danger fa-fw'></span></span>" +
				"<span class='pl-1'>#= screen.name #</span>" +
			"# } else { #" +
				"<div class='d-flex align-items-center'>" +
					"# if (screen.lastStatus == '6') { #" +
						"<span title='10분내 요청'><span class='fa-solid fa-flag-swallowtail fa-fw text-blue'></span></span>" +
					"# } else if (screen.lastStatus == '5') { #" +
						"<span title='1시간내 요청'><span class='fa-solid fa-flag-swallowtail fa-fw text-green'></span></span>" +
					"# } else if (screen.lastStatus == '4') { #" +
						"<span title='6시간내 요청'><span class='fa-solid fa-flag-swallowtail fa-fw text-yellow'></span></span>" +
					"# } else if (screen.lastStatus == '3') { #" +
						"<span title='24시간내 요청'><span class='fa-solid fa-flag-swallowtail fa-fw text-orange'></span></span>" +
					"# } else if (screen.lastStatus == '1') { #" +
						"<span title='24시간내 없음'><span class='fa-solid fa-flag-pennant fa-fw text-danger'></span></span>" +
					"# } else if (screen.lastStatus == '0') { #" +
						"<span title='기록 없음'><span class='fa-solid fa-flag-pennant fa-fw text-secondary'></span></span>" +
					"# } #" +
					"<span class='pl-1'>#= screen.name #</span>" +
					"<a href='javascript:showScreen(#= screen.id #,\"#= screen.name #\")' class='btn btn-default btn-xs icon-btn ml-1'><span class='fas fa-search text-info'></span></a>" +
				"</div>" +
			"# } #";
	
	String noRecordsTemplate =
			"<div class='container text-center my-4'>" +
				"<div class='d-flex justify-content-center align-self-center'>" +
					"<span class='fa-thin fa-hexagon-exclamation fa-3x'></span>" +
					"<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>해당 자료 없음</span>" +
				"</div>" +
			"</div>";
			
%>


<!--  Tab -->
<ul class="nav nav-tabs tabs-alt container-p-x container-m--x mb-4 mt-3">
	<li class="nav-item">
		<a class="nav-link active" data-toggle="tab" href="#screen-data">
			<i class="mr-1 fa-light fa-flag-checkered"></i>
			성공
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#screen-fail-data">
			<i class="mr-1 fa-light fa-cloud-question"></i>
			실패
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#screen-fb-data">
			<small><span class='badge badge-outline-secondary' style='font-weight: 300;'>대체</span></small>
			대체광고
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#screen-no-ad-data">
			<i class="mr-1 fa-light fa-empty-set"></i>
			광고없음
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#site-data">
			<i class="mr-1 fa-light fa-map-pin"></i>
			사이트
		</a>
	</li>
</ul>
<!--  / Tab -->


<!--  Root form container -->
<div id="formRoot"></div>


<div class="tab-content">
	<div class="tab-pane active" id="screen-data">
	
<!-- screen data[S] -->



<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="screen-grid" pageable="true" filterable="true" sortable="true" scrollable="true" reorderable="true" resizable="true" selectable="multiple" >
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
    <kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">엑셀</button>
    		</div>
    		<div class="float-right">
    		</div>
    	</div>
   	</kendo:grid-toolbarTemplate>
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="화면" field="screen.name" width="250" template="<%= screenNameTemplate %>" />
		<kendo:grid-column title="성공" field="succTotal" width="100" />
		<kendo:grid-column title="00" field="cnt00" width="80" filterable="false" />
		<kendo:grid-column title="01" field="cnt01" width="80" filterable="false" />
		<kendo:grid-column title="02" field="cnt02" width="80" filterable="false" />
		<kendo:grid-column title="03" field="cnt03" width="80" filterable="false" />
		<kendo:grid-column title="04" field="cnt04" width="80" filterable="false" />
		<kendo:grid-column title="05" field="cnt05" width="80" filterable="false" />
		<kendo:grid-column title="06" field="cnt06" width="80" filterable="false" />
		<kendo:grid-column title="07" field="cnt07" width="80" filterable="false" />
		<kendo:grid-column title="08" field="cnt08" width="80" filterable="false" />
		<kendo:grid-column title="09" field="cnt09" width="80" filterable="false" />
		<kendo:grid-column title="10" field="cnt10" width="80" filterable="false" />
		<kendo:grid-column title="11" field="cnt11" width="80" filterable="false" />
		<kendo:grid-column title="12" field="cnt12" width="80" filterable="false" />
		<kendo:grid-column title="13" field="cnt13" width="80" filterable="false" />
		<kendo:grid-column title="14" field="cnt14" width="80" filterable="false" />
		<kendo:grid-column title="15" field="cnt15" width="80" filterable="false" />
		<kendo:grid-column title="16" field="cnt16" width="80" filterable="false" />
		<kendo:grid-column title="17" field="cnt17" width="80" filterable="false" />
		<kendo:grid-column title="18" field="cnt18" width="80" filterable="false" />
		<kendo:grid-column title="19" field="cnt19" width="80" filterable="false" />
		<kendo:grid-column title="20" field="cnt20" width="80" filterable="false" />
		<kendo:grid-column title="21" field="cnt21" width="80" filterable="false" />
		<kendo:grid-column title="22" field="cnt22" width="80" filterable="false" />
		<kendo:grid-column title="23" field="cnt23" width="80" filterable="false" />
		<kendo:grid-column title="광고수" field="adCount" width="120" filterable="false" />
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="succTotal" dir="desc"/>
		</kendo:dataSource-sort>
       	<kendo:dataSource-filter>
   			<kendo:dataSource-filterItem field="medium.id" operator="eq" logic="and" value="${sessionScope['currMediumId']}" >
   			</kendo:dataSource-filterItem>
    	</kendo:dataSource-filter>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readScrTotUrl}" dataType="json" type="POST" contentType="application/json">
				<kendo:dataSource-transport-read-data>
					<script>
						function additionalData(e) {
							return { reqStrValue1:  "${currDate}" };
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
		<kendo:dataSource-schema data="data" total="total" groups="data">
			<kendo:dataSource-schema-model id="id">
				<kendo:dataSource-schema-model-fields>
					<kendo:dataSource-schema-model-field name="dateTotal" type="number" />
					<kendo:dataSource-schema-model-field name="succTotal" type="number" />
					<kendo:dataSource-schema-model-field name="failTotal" type="number" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

<!-- / Kendo grid  -->



<!-- screen data[E] -->

	</div>
	<div class="tab-pane" id="site-data">
	
<!-- site date[S] -->



<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="site-grid" pageable="true" filterable="true" sortable="true" scrollable="true" reorderable="true" resizable="true" selectable="multiple" >
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
    <kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">엑셀</button>
    		</div>
    		<div class="float-right">
    		</div>
    	</div>
   	</kendo:grid-toolbarTemplate>
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="사이트" field="site.name" width="200" />
		<kendo:grid-column title="합계" field="dateTotal" width="100" />
		<kendo:grid-column title="성공" field="succTotal" width="100" />
		<kendo:grid-column title="00" field="cnt00" width="80" filterable="false" />
		<kendo:grid-column title="01" field="cnt01" width="80" filterable="false" />
		<kendo:grid-column title="02" field="cnt02" width="80" filterable="false" />
		<kendo:grid-column title="03" field="cnt03" width="80" filterable="false" />
		<kendo:grid-column title="04" field="cnt04" width="80" filterable="false" />
		<kendo:grid-column title="05" field="cnt05" width="80" filterable="false" />
		<kendo:grid-column title="06" field="cnt06" width="80" filterable="false" />
		<kendo:grid-column title="07" field="cnt07" width="80" filterable="false" />
		<kendo:grid-column title="08" field="cnt08" width="80" filterable="false" />
		<kendo:grid-column title="09" field="cnt09" width="80" filterable="false" />
		<kendo:grid-column title="10" field="cnt10" width="80" filterable="false" />
		<kendo:grid-column title="11" field="cnt11" width="80" filterable="false" />
		<kendo:grid-column title="12" field="cnt12" width="80" filterable="false" />
		<kendo:grid-column title="13" field="cnt13" width="80" filterable="false" />
		<kendo:grid-column title="14" field="cnt14" width="80" filterable="false" />
		<kendo:grid-column title="15" field="cnt15" width="80" filterable="false" />
		<kendo:grid-column title="16" field="cnt16" width="80" filterable="false" />
		<kendo:grid-column title="17" field="cnt17" width="80" filterable="false" />
		<kendo:grid-column title="18" field="cnt18" width="80" filterable="false" />
		<kendo:grid-column title="19" field="cnt19" width="80" filterable="false" />
		<kendo:grid-column title="20" field="cnt20" width="80" filterable="false" />
		<kendo:grid-column title="21" field="cnt21" width="80" filterable="false" />
		<kendo:grid-column title="22" field="cnt22" width="80" filterable="false" />
		<kendo:grid-column title="23" field="cnt23" width="80" filterable="false" />
		<kendo:grid-column title="실패" field="failTotal" width="100" />
		<kendo:grid-column title="광고수" field="adCount" width="120" filterable="false" />
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="dateTotal" dir="desc"/>
		</kendo:dataSource-sort>
       	<kendo:dataSource-filter>
   			<kendo:dataSource-filterItem field="medium.id" operator="eq" logic="and" value="${sessionScope['currMediumId']}" >
   			</kendo:dataSource-filterItem>
    	</kendo:dataSource-filter>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readSitTotUrl}" dataType="json" type="POST" contentType="application/json">
				<kendo:dataSource-transport-read-data>
					<script>
						function additionalData(e) {
							return { reqStrValue1:  "${currDate}" };
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
		<kendo:dataSource-schema data="data" total="total" groups="data">
			<kendo:dataSource-schema-model id="id">
				<kendo:dataSource-schema-model-fields>
					<kendo:dataSource-schema-model-field name="dateTotal" type="number" />
					<kendo:dataSource-schema-model-field name="succTotal" type="number" />
					<kendo:dataSource-schema-model-field name="failTotal" type="number" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

<!-- / Kendo grid  -->



<!-- site data[E] -->

	</div>
	<div class="tab-pane" id="screen-fail-data">
	
<!-- screen fail date[S] -->



<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="screen-fail-grid" pageable="true" filterable="true" sortable="true" scrollable="true" reorderable="true" resizable="true" selectable="multiple" >
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
    <kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">엑셀</button>
    		</div>
    		<div class="float-right">
    		</div>
    	</div>
   	</kendo:grid-toolbarTemplate>
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="화면" field="screen.name" width="250" template="<%= screenNameTemplate %>" />
		<kendo:grid-column title="합계" field="dateTotal" width="100" />
		<kendo:grid-column title="00" field="cnt00" width="80" filterable="false" />
		<kendo:grid-column title="01" field="cnt01" width="80" filterable="false" />
		<kendo:grid-column title="02" field="cnt02" width="80" filterable="false" />
		<kendo:grid-column title="03" field="cnt03" width="80" filterable="false" />
		<kendo:grid-column title="04" field="cnt04" width="80" filterable="false" />
		<kendo:grid-column title="05" field="cnt05" width="80" filterable="false" />
		<kendo:grid-column title="06" field="cnt06" width="80" filterable="false" />
		<kendo:grid-column title="07" field="cnt07" width="80" filterable="false" />
		<kendo:grid-column title="08" field="cnt08" width="80" filterable="false" />
		<kendo:grid-column title="09" field="cnt09" width="80" filterable="false" />
		<kendo:grid-column title="10" field="cnt10" width="80" filterable="false" />
		<kendo:grid-column title="11" field="cnt11" width="80" filterable="false" />
		<kendo:grid-column title="12" field="cnt12" width="80" filterable="false" />
		<kendo:grid-column title="13" field="cnt13" width="80" filterable="false" />
		<kendo:grid-column title="14" field="cnt14" width="80" filterable="false" />
		<kendo:grid-column title="15" field="cnt15" width="80" filterable="false" />
		<kendo:grid-column title="16" field="cnt16" width="80" filterable="false" />
		<kendo:grid-column title="17" field="cnt17" width="80" filterable="false" />
		<kendo:grid-column title="18" field="cnt18" width="80" filterable="false" />
		<kendo:grid-column title="19" field="cnt19" width="80" filterable="false" />
		<kendo:grid-column title="20" field="cnt20" width="80" filterable="false" />
		<kendo:grid-column title="21" field="cnt21" width="80" filterable="false" />
		<kendo:grid-column title="22" field="cnt22" width="80" filterable="false" />
		<kendo:grid-column title="23" field="cnt23" width="80" filterable="false" />
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="dateTotal" dir="desc"/>
		</kendo:dataSource-sort>
       	<kendo:dataSource-filter>
   			<kendo:dataSource-filterItem field="medium.id" operator="eq" logic="and" value="${sessionScope['currMediumId']}" >
   			</kendo:dataSource-filterItem>
    	</kendo:dataSource-filter>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readScrFailTotUrl}" dataType="json" type="POST" contentType="application/json">
				<kendo:dataSource-transport-read-data>
					<script>
						function additionalData(e) {
							return { reqStrValue1:  "${currDate}" };
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
		<kendo:dataSource-schema data="data" total="total" groups="data">
			<kendo:dataSource-schema-model id="id">
				<kendo:dataSource-schema-model-fields>
					<kendo:dataSource-schema-model-field name="dateTotal" type="number" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

<!-- / Kendo grid  -->



<!-- screen fail data[E] -->

	</div>
	<div class="tab-pane" id="screen-no-ad-data">
	
<!-- screen no ad date[S] -->



<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="screen-no-ad-grid" pageable="true" filterable="true" sortable="true" scrollable="true" reorderable="true" resizable="true" selectable="multiple" >
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
    <kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">엑셀</button>
    		</div>
    		<div class="float-right">
    		</div>
    	</div>
   	</kendo:grid-toolbarTemplate>
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="화면" field="screen.name" width="250" template="<%= screenNameTemplate %>" />
		<kendo:grid-column title="합계" field="dateTotal" width="100" />
		<kendo:grid-column title="00" field="cnt00" width="80" filterable="false" />
		<kendo:grid-column title="01" field="cnt01" width="80" filterable="false" />
		<kendo:grid-column title="02" field="cnt02" width="80" filterable="false" />
		<kendo:grid-column title="03" field="cnt03" width="80" filterable="false" />
		<kendo:grid-column title="04" field="cnt04" width="80" filterable="false" />
		<kendo:grid-column title="05" field="cnt05" width="80" filterable="false" />
		<kendo:grid-column title="06" field="cnt06" width="80" filterable="false" />
		<kendo:grid-column title="07" field="cnt07" width="80" filterable="false" />
		<kendo:grid-column title="08" field="cnt08" width="80" filterable="false" />
		<kendo:grid-column title="09" field="cnt09" width="80" filterable="false" />
		<kendo:grid-column title="10" field="cnt10" width="80" filterable="false" />
		<kendo:grid-column title="11" field="cnt11" width="80" filterable="false" />
		<kendo:grid-column title="12" field="cnt12" width="80" filterable="false" />
		<kendo:grid-column title="13" field="cnt13" width="80" filterable="false" />
		<kendo:grid-column title="14" field="cnt14" width="80" filterable="false" />
		<kendo:grid-column title="15" field="cnt15" width="80" filterable="false" />
		<kendo:grid-column title="16" field="cnt16" width="80" filterable="false" />
		<kendo:grid-column title="17" field="cnt17" width="80" filterable="false" />
		<kendo:grid-column title="18" field="cnt18" width="80" filterable="false" />
		<kendo:grid-column title="19" field="cnt19" width="80" filterable="false" />
		<kendo:grid-column title="20" field="cnt20" width="80" filterable="false" />
		<kendo:grid-column title="21" field="cnt21" width="80" filterable="false" />
		<kendo:grid-column title="22" field="cnt22" width="80" filterable="false" />
		<kendo:grid-column title="23" field="cnt23" width="80" filterable="false" />
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="dateTotal" dir="desc"/>
		</kendo:dataSource-sort>
       	<kendo:dataSource-filter>
   			<kendo:dataSource-filterItem field="medium.id" operator="eq" logic="and" value="${sessionScope['currMediumId']}" >
   			</kendo:dataSource-filterItem>
    	</kendo:dataSource-filter>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readScrNoAdTotUrl}" dataType="json" type="POST" contentType="application/json">
				<kendo:dataSource-transport-read-data>
					<script>
						function additionalData(e) {
							return { reqStrValue1:  "${currDate}" };
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
		<kendo:dataSource-schema data="data" total="total" groups="data">
			<kendo:dataSource-schema-model id="id">
				<kendo:dataSource-schema-model-fields>
					<kendo:dataSource-schema-model-field name="dateTotal" type="number" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

<!-- / Kendo grid  -->



<!-- screen fail data[E] -->

	</div>
	<div class="tab-pane" id="screen-fb-data">
	
<!-- screen fallback date[S] -->



<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="screen-fb-grid" pageable="true" filterable="true" sortable="true" scrollable="true" reorderable="true" resizable="true" selectable="multiple" >
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
    <kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">엑셀</button>
    		</div>
    		<div class="float-right">
    		</div>
    	</div>
   	</kendo:grid-toolbarTemplate>
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="화면" field="screen.name" width="250" template="<%= screenNameTemplate %>" />
		<kendo:grid-column title="합계" field="dateTotal" width="100" />
		<kendo:grid-column title="00" field="cnt00" width="80" filterable="false" />
		<kendo:grid-column title="01" field="cnt01" width="80" filterable="false" />
		<kendo:grid-column title="02" field="cnt02" width="80" filterable="false" />
		<kendo:grid-column title="03" field="cnt03" width="80" filterable="false" />
		<kendo:grid-column title="04" field="cnt04" width="80" filterable="false" />
		<kendo:grid-column title="05" field="cnt05" width="80" filterable="false" />
		<kendo:grid-column title="06" field="cnt06" width="80" filterable="false" />
		<kendo:grid-column title="07" field="cnt07" width="80" filterable="false" />
		<kendo:grid-column title="08" field="cnt08" width="80" filterable="false" />
		<kendo:grid-column title="09" field="cnt09" width="80" filterable="false" />
		<kendo:grid-column title="10" field="cnt10" width="80" filterable="false" />
		<kendo:grid-column title="11" field="cnt11" width="80" filterable="false" />
		<kendo:grid-column title="12" field="cnt12" width="80" filterable="false" />
		<kendo:grid-column title="13" field="cnt13" width="80" filterable="false" />
		<kendo:grid-column title="14" field="cnt14" width="80" filterable="false" />
		<kendo:grid-column title="15" field="cnt15" width="80" filterable="false" />
		<kendo:grid-column title="16" field="cnt16" width="80" filterable="false" />
		<kendo:grid-column title="17" field="cnt17" width="80" filterable="false" />
		<kendo:grid-column title="18" field="cnt18" width="80" filterable="false" />
		<kendo:grid-column title="19" field="cnt19" width="80" filterable="false" />
		<kendo:grid-column title="20" field="cnt20" width="80" filterable="false" />
		<kendo:grid-column title="21" field="cnt21" width="80" filterable="false" />
		<kendo:grid-column title="22" field="cnt22" width="80" filterable="false" />
		<kendo:grid-column title="23" field="cnt23" width="80" filterable="false" />
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="dateTotal" dir="desc"/>
		</kendo:dataSource-sort>
       	<kendo:dataSource-filter>
   			<kendo:dataSource-filterItem field="medium.id" operator="eq" logic="and" value="${sessionScope['currMediumId']}" >
   			</kendo:dataSource-filterItem>
    	</kendo:dataSource-filter>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readScrFbTotUrl}" dataType="json" type="POST" contentType="application/json">
				<kendo:dataSource-transport-read-data>
					<script>
						function additionalData(e) {
							return { reqStrValue1:  "${currDate}" };
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
		<kendo:dataSource-schema data="data" total="total" groups="data">
			<kendo:dataSource-schema-model id="id">
				<kendo:dataSource-schema-model-fields>
					<kendo:dataSource-schema-model-field name="dateTotal" type="number" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

<!-- / Kendo grid  -->



<!-- screen fallback data[E] -->

	</div>
</div>



<!--  Forms -->

<script id="template-1" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-1">
	<div class="modal-dialog modal-sm">
		<form class="modal-content" id="form-1" rowid="-1" url="${createUrl}">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					대상일
					<span class="font-weight-light pl-1">선택</span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
				<div class="form-group col">
					<input name="dstDate" type="text" class="form-control required">
				</div>
			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button type="button" class="btn btn-primary" onclick='selectDate()'>선택</button>
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


/* 그리드 행의 높이 지정 */
.k-grid tbody tr, .k-grid tbody tr td
{
    height: 40px;
}

</style>


<!--  / Forms -->


<!--  Scripts -->

<script>

function to_date(dateStr) {
	
    var yyyyMMdd = String(dateStr);
    
    var sYear = yyyyMMdd.substring(0, 4);
    var sMonth = yyyyMMdd.substring(5, 7);
    var sDate = yyyyMMdd.substring(8, 10);

    return new Date(Number(sYear), Number(sMonth)-1, Number(sDate));
}


function initForm1() {
	
	$("#formRoot").html(kendo.template($("#template-1").html()));
	
	var today = new Date();
	today.setHours(0, 0, 0, 0);

	$("#form-1 input[name='dstDate']").kendoDatePicker({
		format: "yyyy-MM-dd",
		parseFormats: [
			"yyyy-MM-dd",
		],
		value: to_date("${currDate}"),
		max: today,
	});


	$("#form-1").validate({
		rules: {
			dstDate: { date: true },
		}
	});
}


function selectDate() {

	// kendo datepicker validation
	validateKendoDateValue($("#form-1 input[name='dstDate']"));
	
	if ($("#form-1").valid()) {
		var date = $("#form-1 input[name='dstDate']").data("kendoDatePicker").value();
		
		if (date != null) {
			navigateToDate(kendo.toString(date, "yyyy-MM-dd"));
		}
	}
}


</script>

<!--  / Scripts -->


<!-- / Page body -->





<!-- Functional tags -->

<func:screenInfoModal />
<func:cmmValidate />



<!-- Closing tags -->

<common:base />
<common:pageClosing />
