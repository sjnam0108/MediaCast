<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/inv/screen/create" var="createUrl" />
<c:url value="/inv/screen/read" var="readUrl" />
<c:url value="/inv/screen/update" var="updateUrl" />
<c:url value="/inv/screen/destroy" var="destroyUrl" />

<c:url value="/inv/screen/readACSiteName" var="readACSiteNameUrl" />
<c:url value="/inv/screen/readACSiteShortName" var="readACSiteShortNameUrl" />

<c:url value="/inv/screen/readResolutions" var="readResolutionUrl" />

<c:url value="/inv/screen/updateTime" var="updateTimeUrl" />


<!-- Opening tags -->

<common:mediumPageOpeningNaverMap />


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
			"<div class='text-nowrap'>" +
				"<button type='button' onclick='edit(#= id #)' class='btn icon-btn btn-sm btn-outline-success borderless'>" + 
				"<span class='fas fa-pencil-alt'></span></button>" +
				"<span class='d-none d-sm-inline pl-1'>" +
					"<button type='button' onclick='editTime(#= id #)' " +
						"# if (bizHour && bizHour.length == 168) { #" +
							"class='btn icon-btn btn-sm btn-outline-success borderless'>" + 
						"# } else { #" +
							"class='btn icon-btn btn-sm btn-outline-secondary borderless'>" + 
						"# } #" +
					"<span class='fa-regular fa-clock'></span></button>" +
				"</span>" +
			"</div>";
			
	String nameTemplate =
			"<div class='d-flex align-items-center'>" +
				"# if (lastStatus == '6') { #" +
					"<span title='10분내 요청'><span class='fa-solid fa-flag-swallowtail fa-fw text-blue'></span></span>" +
				"# } else if (lastStatus == '5') { #" +
					"<span title='1시간내 요청'><span class='fa-solid fa-flag-swallowtail fa-fw text-green'></span></span>" +
				"# } else if (lastStatus == '4') { #" +
					"<span title='6시간내 요청'><span class='fa-solid fa-flag-swallowtail fa-fw text-yellow'></span></span>" +
				"# } else if (lastStatus == '3') { #" +
					"<span title='24시간내 요청'><span class='fa-solid fa-flag-swallowtail fa-fw text-orange'></span></span>" +
				"# } else if (lastStatus == '1') { #" +
					"<span title='24시간내 없음'><span class='fa-solid fa-flag-pennant fa-fw text-danger'></span></span>" +
				"# } else if (lastStatus == '0') { #" +
					"<span title='기록 없음'><span class='fa-solid fa-flag-pennant fa-fw text-secondary'></span></span>" +
				"# } #" +
				//"<span class='pl-1'><a href='javascript:void(0)' class='stb-status-popover' tabindex='0'>#= name #</a></span>" +
				"<span class='pl-1'>#= name #</span>" +
				"<a href='javascript:showScreen(#= id #,\"#= name #\")' class='btn btn-default btn-xs icon-btn ml-1'><span class='fas fa-search text-info'></span></a>" +
			"</div>";
	
	String effStartDateTemplate = net.doohad.utils.Util.getSmartDate("effectiveStartDate", false, false);
	String effEndDateTemplate = net.doohad.utils.Util.getSmartDate("effectiveEndDate", false, false);
	String apiSyncDateTemplate = net.doohad.utils.Util.getSmartDate("apiSyncDate", false, true);

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
<kendo:grid name="grid" pageable="true" filterable="true" sortable="true" scrollable="true" reorderable="true" resizable="true" selectable="multiple" >
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
		<kendo:grid-column title="수정..." width="80" filterable="false" sortable="false" template="<%= editTemplate %>" />
		<kendo:grid-column title="화면명" field="name" width="250" template="<%= nameTemplate %>" />
		<kendo:grid-column title="화면ID" field="shortName" width="150" />
		<kendo:grid-column title="사이트명" field="site.name" width="200" />
		<kendo:grid-column title="사이트ID" field="site.shortName" width="150"
				template="<div><span>#= site.shortName #</span><a href='javascript:navToSite(#= site.id #)' class='btn btn-default btn-xs icon-btn ml-1'><span class='fa-regular fa-arrow-up-left'></span></a></div>" />
		<kendo:grid-column title="서비스중" field="activeStatus" width="100"
				template="#=activeStatus ? \"<span class='fa-light fa-check'>\" : \"\"#" />
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
		<kendo:grid-column title="최저CPM" field="floorCpm" width="150" format="{0:n0}" />
		<kendo:grid-column title="동영상 허용" field="videoAllowed" width="150"
				template="#=videoAllowed ? \"<span class='fa-light fa-check'>\" : \"\"#" />
		<kendo:grid-column title="이미지 허용" field="imageAllowed" width="150"
				template="#=imageAllowed ? \"<span class='fa-light fa-check'>\" : \"\"#" />
		<kendo:grid-column title="광고서버에 이용" field="adServerAvailable" width="180"
				template="#=adServerAvailable ? \"<span class='fa-light fa-check'>\" : \"\"#" />
		<kendo:grid-column title="유효시작일" field="effectiveStartDate" width="150" template="<%= effStartDateTemplate %>" />
		<kendo:grid-column title="유효종료일" field="effectiveEndDate" width="150" template="<%= effEndDateTemplate %>" />
		<kendo:grid-column title="최근 동기화" field="apiSyncDate" width="150" template="<%= apiSyncDateTemplate %>" />
		<kendo:grid-column title="시/군/구" field="site.regionName" width="150" />
		<kendo:grid-column title="재생시간(초)" field="defaultDurSecs" width="150" />
		<kendo:grid-column title="범위 재생시간 허용" field="rangeDurAllowed" width="180"
				template="#=rangeDurAllowed ? \"<span class='fa-light fa-check'>\" : \"\"#" />
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="name" dir="asc"/>
		</kendo:dataSource-sort>

   		<c:choose>
   		<c:when test="${requestScope['initFilterApplied']}">
        	<kendo:dataSource-filter>
   	    		<kendo:dataSource-filterItem field="medium.id" operator="eq" logic="and" value="${sessionScope['currMediumId']}" >
    	    		<kendo:dataSource-filterItem field="site.id" operator="eq" logic="and" value="<%= request.getAttribute(\"siteId\") %>">
	    	    	</kendo:dataSource-filterItem>
       			</kendo:dataSource-filterItem>
       		</kendo:dataSource-filter>
   		</c:when>
   		<c:otherwise>
        	<kendo:dataSource-filter>
       			<kendo:dataSource-filterItem field="medium.id" operator="eq" logic="and" value="${sessionScope['currMediumId']}" >
       			</kendo:dataSource-filterItem>
   	    	</kendo:dataSource-filter>
   		</c:otherwise>
   		</c:choose>
		
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
					<kendo:dataSource-schema-model-field name="floorCpm" type="number" />
					<kendo:dataSource-schema-model-field name="effectiveStartDate" type="date" />
					<kendo:dataSource-schema-model-field name="effectiveEndDate" type="date" />
					<kendo:dataSource-schema-model-field name="apiSyncDate" type="date" />
					<kendo:dataSource-schema-model-field name="activeStatus" type="boolean" />
					<kendo:dataSource-schema-model-field name="defaultDurSecs" type="number" />
					<kendo:dataSource-schema-model-field name="rangeDurAllowed" type="boolean" />
					<kendo:dataSource-schema-model-field name="videoAllowed" type="boolean" />
					<kendo:dataSource-schema-model-field name="imageAllowed" type="boolean" />
					<kendo:dataSource-schema-model-field name="adServerAvailable" type="boolean" />
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
	<div class="modal-dialog modal-lg">
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
				<div class="form-row">
					<div class="form-group col-6">
						<label class="form-label">
							사이트명
							<span class="text-danger">*</span>
						</label>
						<div name="siteName-con">
							<input name="siteName" type="text" maxlength="100" class="form-control min-height--2px">
						</div>
						<label name="siteName-feedback" for="siteName" class="small error invalid-feedback"></label>
					</div>
					<div class="form-group col-4">
						<label class="form-label">
							사이트ID
							<span class="text-danger">*</span>
						</label>
						<div name="siteShortName-con">
							<input name="siteShortName" type="text" maxlength="50" class="form-control min-height--2px">
						</div>
						<label name="siteShortName-feedback" for="siteShortName" class="small error invalid-feedback"></label>
					</div>
					<div class="form-group col-2">
						<label class="form-label">
							서비스
						</label>
						<div class="pt-1">
							<label class="switcher switcher-lg">
								<input type="checkbox" class="switcher-input" name="activeStatus">
								<span class="switcher-indicator">
									<span class="switcher-yes">
										<span class="fa-solid fa-check"></span>
									</span>
									<span class="switcher-no">
										<span class="fa-solid fa-xmark"></span>
									</span>
								</span>
							</label>
						</div>
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col-3">
						<select name="venueType" class="selectpicker bg-custom-gray" data-style="btn-default" data-none-selected-text="" disabled>
							<optgroup label="리테일">
								<option value="CVS">편의점</option>
							</optgroup>
							<optgroup label="옥외">
								<option value="BUSSH">버스 쉘터</option>
								<option value="BLDG">빌딩 전광판</option>
							</optgroup>
							<optgroup label="현장 진료(POC)">
								<option value="HOSP">병/의원</option>
							</optgroup>
						</select>
					</div>
					<div class="form-group col-3">
						<input name="regionName" type="text" class="form-control" placeholder="지역명" disabled>
					</div>
					<div class="form-group col-6">
						<input name="address" type="text" class="form-control" placeholder="주소" disabled>
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col-6">
						<label class="form-label">
							화면명
							<span class="text-danger">*</span>
						</label>
						<input name="name" type="text" maxlength="100" class="form-control required">
					</div>
					<div class="form-group col-6">
						<label class="form-label">
							화면ID
							<span class="text-danger">*</span>
						</label>
						<input name="shortName" type="text" maxlength="50" class="form-control required">
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col-3">
						<label class="form-label">
							화면 해상도
							<span class="text-danger">*</span>
						</label>
						<select name="resolution" class="selectpicker bg-white required" data-style="btn-default" data-none-selected-text="">
<c:forEach var="item" items="${Resolutions}">
							<option value="${item.value}">${item.text}</option>
</c:forEach>
						</select>
					</div>
					<div class="form-group col-3">
						<label class="form-label">
							최저 CPM
							<span class="text-danger">*</span>
						</label>
						<div class="input-group">
							<input name="cpm" type="text" value="0" class="form-control required" >
							<div class="input-group-append">
								<span class="input-group-text">원</span>
							</div>
						</div>
					</div>
					<div class="form-group col-3">
						<label class="form-label">
							유효시작일
							<span class="text-danger">*</span>
						</label>
						<input name="effectiveStartDate" type="text" class="form-control required">
					</div>
					<div class="form-group col-3">
						<label class="form-label">
							유효종료일
						</label>
						<input name="effectiveEndDate" type="text" class="form-control">
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col-3">
						<label class="form-label">
							개별설정 적용
						</label>
						<span data-toggle="tooltip" data-placement="right" title="매체의 설정과 다른 값을 적용할 경우 활성화 하십시오.">
							<span class="fa-regular fa-circle-info text-info"></span>
						</span>
						<div class="pt-1">
							<label class="switcher switcher-lg">
								<input type="checkbox" class="switcher-input" name="durationOverridden">
								<span class="switcher-indicator">
									<span class="switcher-yes">
										<span class="fa-solid fa-lock-open"></span>
									</span>
									<span class="switcher-no">
										<span class="fa-solid fa-lock"></span>
									</span>
								</span>
							</label>
						</div>
					</div>
					<div class="form-group col-2">
						<label class="form-label">
							기본 재생시간
							<span class="text-danger">*</span>
						</label>
						<div class="input-group">
							<input name="defaultDurSecs" type="text" class="form-control required" disabled>
							<div class="input-group-append">
								<span class="input-group-text">초</span>
							</div>
						</div>
					</div>
					<div class="form-group col-3">
						<label class="form-label">
							범위 재생시간 허용
						</label>
						<div class="pt-1">
							<label class="switcher switcher-lg">
								<input type="checkbox" class="switcher-input" name="rangeDurAllowed" disabled>
								<span class="switcher-indicator">
									<span class="switcher-yes">
										<span class="fa-solid fa-lock-open"></span>
									</span>
									<span class="switcher-no">
										<span class="fa-solid fa-lock"></span>
									</span>
								</span>
							</label>
						</div>
					</div>
					<div class="form-group col-2">
						<label class="form-label">
							최저 재생시간
							<span class="text-danger">*</span>
						</label>
						<div class="input-group">
							<input name="minDurSecs" type="text" class="form-control required" disabled>
							<div class="input-group-append">
								<span class="input-group-text">초</span>
							</div>
						</div>
					</div>
					<div class="form-group col-2">
						<label class="form-label">
							최고 재생시간
							<span class="text-danger">*</span>
						</label>
						<div class="input-group">
							<input name="maxDurSecs" type="text" class="form-control required" disabled>
							<div class="input-group-append">
								<span class="input-group-text">초</span>
							</div>
						</div>
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col-4">
						<label class="form-label">
							화면 이용 범위
						</label>
						<div class="pt-1">
							<label class="switcher switcher-lg mr-0">
								<input type="checkbox" class="switcher-input" name="adServerAvailable" checked>
								<span class="switcher-indicator">
									<span class="switcher-yes">
										<span class="fa-solid fa-check"></span>
									</span>
									<span class="switcher-no">
										<span class="fa-solid fa-xmark"></span>
									</span>
								</span>
							</label>
							<span class="switcher-label">광고 서버</span>
						</div>
					</div>
					<div class="form-group col-4">
						<label class="form-label">
							광고 소재 유형
						</label>
						<div class="pt-1">
							<label class="switcher switcher-lg mr-0">
								<input type="checkbox" class="switcher-input" name="videoAllowed">
								<span class="switcher-indicator">
									<span class="switcher-yes">
										<span class="fa-solid fa-check"></span>
									</span>
									<span class="switcher-no">
										<span class="fa-solid fa-xmark"></span>
									</span>
								</span>
							</label>
							<span class="switcher-label">동영상 허용</span>
						</div>
					</div>
					<div class="form-group col-4">
						<label class="form-label">
							광고 소재 유형
						</label>
						<div class="pt-1">
							<label class="switcher switcher-lg mr-0">
								<input type="checkbox" class="switcher-input" name="imageAllowed">
								<span class="switcher-indicator">
									<span class="switcher-yes">
										<span class="fa-solid fa-check"></span>
									</span>
									<span class="switcher-no">
										<span class="fa-solid fa-xmark"></span>
									</span>
								</span>
							</label>
							<span class="switcher-label">이미지 허용</span>
						</div>
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col">
						<label class="form-label">
							운영자 메모
						</label>
						<textarea name="memo" rows="2" maxlength="150" class="form-control"></textarea>
					</div>
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


<script id="template-2" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-2">
	<div class="modal-dialog modal-lg">
		<form class="modal-content" id="form-2" rowid="-1" url="${createUrl}">
      
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
				<div class="form-row">
					<div class="form-group col-4">
						<label class="form-label">
							화면ID
						</label>
						<input name="shortName" type="text" maxlength="50" class="form-control" readonly>
					</div>
					<div class="form-group col-5">
						<label class="form-label">
							화면명
						</label>
						<input name="name" type="text" maxlength="100" class="form-control" readonly>
					</div>
					<div class="form-group col-3">
						<label class="form-label">
							1주일 총 운영시간
						</label>
						<div class="input-group">
							<input name="bizHrs" type="text" class="form-control" value="0" readonly>
							<div class="input-group-append">
								<span class="input-group-text">시간</span>
							</div>
						</div>
					</div>
				</div>
				<div class="form-row">
					<div class="mx-auto">

<table id="time-table">
	<tr>
		<th></th>
		<th><pre class="my-0">AM</pre></th>
		<th><pre class="my-0">1</pre></th>
		<th><pre class="my-0">2</pre></th>
		<th><pre class="my-0">3</pre></th>
		<th><pre class="my-0">4</pre></th>
		<th><pre class="my-0">5</pre></th>
		<th><pre class="my-0">6</pre></th>
		<th><pre class="my-0">7</pre></th>
		<th><pre class="my-0">8</pre></th>
		<th><pre class="my-0">9</pre></th>
		<th><pre class="my-0">10</pre></th>
		<th><pre class="my-0">11</pre></th>
		<th><pre class="my-0">PM</pre></th>
		<th><pre class="my-0">1</pre></th>
		<th><pre class="my-0">2</pre></th>
		<th><pre class="my-0">3</pre></th>
		<th><pre class="my-0">4</pre></th>
		<th><pre class="my-0">5</pre></th>
		<th><pre class="my-0">6</pre></th>
		<th><pre class="my-0">7</pre></th>
		<th><pre class="my-0">8</pre></th>
		<th><pre class="my-0">9</pre></th>
		<th><pre class="my-0">10</pre></th>
		<th><pre class="my-0">11</pre></th>
	</tr>
	<tr>
		<th class="pr-2">월<small>요일</small></th>
		<td id="tt0"></td>
		<td id="tt1"></td>
		<td id="tt2"></td>
		<td id="tt3"></td>
		<td id="tt4"></td>
		<td id="tt5"></td>
		<td id="tt6"></td>
		<td id="tt7"></td>
		<td id="tt8"></td>
		<td id="tt9"></td>
		<td id="tt10"></td>
		<td id="tt11"></td>
		<td id="tt12"></td>
		<td id="tt13"></td>
		<td id="tt14"></td>
		<td id="tt15"></td>
		<td id="tt16"></td>
		<td id="tt17"></td>
		<td id="tt18"></td>
		<td id="tt19"></td>
		<td id="tt20"></td>
		<td id="tt21"></td>
		<td id="tt22"></td>
		<td id="tt23"></td>
	</tr>
	<tr>
		<th>화<small>요일</small></th>
		<td id="tt24"></td>
		<td id="tt25"></td>
		<td id="tt26"></td>
		<td id="tt27"></td>
		<td id="tt28"></td>
		<td id="tt29"></td>
		<td id="tt30"></td>
		<td id="tt31"></td>
		<td id="tt32"></td>
		<td id="tt33"></td>
		<td id="tt34"></td>
		<td id="tt35"></td>
		<td id="tt36"></td>
		<td id="tt37"></td>
		<td id="tt38"></td>
		<td id="tt39"></td>
		<td id="tt40"></td>
		<td id="tt41"></td>
		<td id="tt42"></td>
		<td id="tt43"></td>
		<td id="tt44"></td>
		<td id="tt45"></td>
		<td id="tt46"></td>
		<td id="tt47"></td>
	</tr>
	<tr>
		<th>수<small>요일</small></th>
		<td id="tt48"></td>
		<td id="tt49"></td>
		<td id="tt50"></td>
		<td id="tt51"></td>
		<td id="tt52"></td>
		<td id="tt53"></td>
		<td id="tt54"></td>
		<td id="tt55"></td>
		<td id="tt56"></td>
		<td id="tt57"></td>
		<td id="tt58"></td>
		<td id="tt59"></td>
		<td id="tt60"></td>
		<td id="tt61"></td>
		<td id="tt62"></td>
		<td id="tt63"></td>
		<td id="tt64"></td>
		<td id="tt65"></td>
		<td id="tt66"></td>
		<td id="tt67"></td>
		<td id="tt68"></td>
		<td id="tt69"></td>
		<td id="tt70"></td>
		<td id="tt71"></td>
	</tr>
	<tr>
		<th>목<small>요일</small></th>
		<td id="tt72"></td>
		<td id="tt73"></td>
		<td id="tt74"></td>
		<td id="tt75"></td>
		<td id="tt76"></td>
		<td id="tt77"></td>
		<td id="tt78"></td>
		<td id="tt79"></td>
		<td id="tt80"></td>
		<td id="tt81"></td>
		<td id="tt82"></td>
		<td id="tt83"></td>
		<td id="tt84"></td>
		<td id="tt85"></td>
		<td id="tt86"></td>
		<td id="tt87"></td>
		<td id="tt88"></td>
		<td id="tt89"></td>
		<td id="tt90"></td>
		<td id="tt91"></td>
		<td id="tt92"></td>
		<td id="tt93"></td>
		<td id="tt94"></td>
		<td id="tt95"></td>
	</tr>
	<tr>
		<th>금<small>요일</small></th>
		<td id="tt96"></td>
		<td id="tt97"></td>
		<td id="tt98"></td>
		<td id="tt99"></td>
		<td id="tt100"></td>
		<td id="tt101"></td>
		<td id="tt102"></td>
		<td id="tt103"></td>
		<td id="tt104"></td>
		<td id="tt105"></td>
		<td id="tt106"></td>
		<td id="tt107"></td>
		<td id="tt108"></td>
		<td id="tt109"></td>
		<td id="tt110"></td>
		<td id="tt111"></td>
		<td id="tt112"></td>
		<td id="tt113"></td>
		<td id="tt114"></td>
		<td id="tt115"></td>
		<td id="tt116"></td>
		<td id="tt117"></td>
		<td id="tt118"></td>
		<td id="tt119"></td>
	</tr>
	<tr>
		<th>토<small>요일</small></th>
		<td id="tt120"></td>
		<td id="tt121"></td>
		<td id="tt122"></td>
		<td id="tt123"></td>
		<td id="tt124"></td>
		<td id="tt125"></td>
		<td id="tt126"></td>
		<td id="tt127"></td>
		<td id="tt128"></td>
		<td id="tt129"></td>
		<td id="tt130"></td>
		<td id="tt131"></td>
		<td id="tt132"></td>
		<td id="tt133"></td>
		<td id="tt134"></td>
		<td id="tt135"></td>
		<td id="tt136"></td>
		<td id="tt137"></td>
		<td id="tt138"></td>
		<td id="tt139"></td>
		<td id="tt140"></td>
		<td id="tt141"></td>
		<td id="tt142"></td>
		<td id="tt143"></td>
	</tr>
	<tr>
		<th>일<small>요일</small></th>
		<td id="tt144"></td>
		<td id="tt145"></td>
		<td id="tt146"></td>
		<td id="tt147"></td>
		<td id="tt148"></td>
		<td id="tt149"></td>
		<td id="tt150"></td>
		<td id="tt151"></td>
		<td id="tt152"></td>
		<td id="tt153"></td>
		<td id="tt154"></td>
		<td id="tt155"></td>
		<td id="tt156"></td>
		<td id="tt157"></td>
		<td id="tt158"></td>
		<td id="tt159"></td>
		<td id="tt160"></td>
		<td id="tt161"></td>
		<td id="tt162"></td>
		<td id="tt163"></td>
		<td id="tt164"></td>
		<td id="tt165"></td>
		<td id="tt166"></td>
		<td id="tt167"></td>
	</tr>
</table>

						<div class="text-center mt-2">
							이용 방법
							<span data-toggle="tooltip" data-placement="top" title="위의 요일별 시간 테이블의 칸을 클릭 혹은 마우스 드래그를 하여 운영 시간을 지정하세요.">
								<span class="fa-regular fa-circle-info text-info"></span>
							</span>
							<span class="px-3"><span class="fa-regular fa-slash-forward fa-2xs text-muted"></span></span>
							주의 사항
							<span data-toggle="tooltip" data-placement="top" title="운영 시간은 반드시 지정되어야 합니다.">
								<span class="fa-regular fa-triangle-exclamation text-yellow"></span>
							</span>
						</div>
					</div>
				</div>

			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<label class="switcher switcher-lg mr-auto">
					<input type="checkbox" class="switcher-input" name="bizHourOverridden">
					<span class="switcher-indicator">
						<span class="switcher-yes">
							<span class="fa-solid fa-lock-open"></span>
						</span>
						<span class="switcher-no">
							<span class="fa-solid fa-lock"></span>
						</span>
					</span>
				</label>

				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button name="save-btn" type="button" class="btn btn-primary" onclick='saveForm2()'>저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>

<style>

/* 폼에서 열 사이의 간격을 원래대로 넓게 */
.form-row>.col, .form-row>[class*="col-"] {
	padding-right: .75rem;
	padding-left: .75rem;
}


/* Kendo autocomplete form control의 높이를 다른 컨트롤과 동일하게 하기 */
.min-height--2px {
	min-height: calc(2.25rem - 2px);
}


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


/* bootstrap-select의 비활성화 상태 배경 변경 */
.bg-custom-gray {
    background-color: #f1f1f2 !important;
}

</style>

<!--  / Forms -->


<!--  Scripts -->

<script>


//Form validator
var validator = null;

//Form error obj
var errors = {};

var shortNameSelected = false;
var nameSelected = false;

// mediumFormValues
var medium_defaultDurSecs = ${medium_defaultDurSecs};
var medium_rangeDurAllowed = ${medium_rangeDurAllowed};
var medium_minDurSecs = ${medium_minDurSecs};
var medium_maxDurSecs = ${medium_maxDurSecs};


function displaySiteInfo(dataItem) {

	if (dataItem == null) {
		$("#form-1 input[name='address']").val("");
		$("#form-1 input[name='regionName']").val("");
	} else {
    	$("#form-1 input[name='address']").val(dataItem.address);
    	$("#form-1 input[name='regionName']").val(dataItem.regionName);
    	
    	bootstrapSelectVal($("#form-1 select[name='venueType']"), dataItem.venueType);
	}
}


function initForm1(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-1").html()));
	
	$("#form-1 select[name='resolution']").selectpicker('render');
	$("#form-1 select[name='venueType']").selectpicker('render');
	
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
	
	$('[data-toggle="tooltip"]').tooltip();
	
	$("#form-1 input[name='siteShortName']").kendoAutoComplete({
        dataTextField: "shortName",
        dataValueField: "id",
        dataSource: {
		    serverFiltering: true,
			transport: {
				read: {
				    dataType: "json",
				    url: "${readACSiteShortNameUrl}",
				    type: "POST",
				    contentType: "application/json",
					data: JSON.stringify({}),
				},
				parameterMap: function (options) {
                	return JSON.stringify(options);
				},
			},
			error: function(e) {
      			showReadErrorMsg();
			}
        },
        change: function(e) {
        	checkSiteShortName();
        	
        	if (!shortNameSelected) {
        		$("#form-1 input[name='siteName']").val("");
        		displaySiteInfo(null);
        	}
        	
        	shortNameSelected = false;
        },
        select: function(e) {
        	var dataItem = this.dataItem(e.item.index());
        	
        	$("#form-1 input[name='siteName']").val(dataItem.name);
    		displaySiteInfo(dataItem);
        	
        	shortNameSelected = true;
        },
        height: 400,
        delay: 500,
        minLength: 1,
        filter: "contains",
        noDataTemplate: "표시할 자료가 없습니다.",
    });
	
	$("#form-1 input[name='siteName']").kendoAutoComplete({
        dataTextField: "name",
        dataValueField: "id",
        dataSource: {
		    serverFiltering: true,
			transport: {
				read: {
				    dataType: "json",
				    url: "${readACSiteNameUrl}",
				    type: "POST",
				    contentType: "application/json",
					data: JSON.stringify({}),
				},
				parameterMap: function (options) {
                	return JSON.stringify(options);
				},
			},
			error: function(e) {
      			showReadErrorMsg();
			}
        },
        change: function(e) {
        	checkSiteName();
        	
        	if (!nameSelected) {
        		$("#form-1 input[name='siteShortName']").val("");
        		displaySiteInfo(null);
        	}
        	
        	nameSelected = false;
        },
        select: function(e) {
        	var dataItem = this.dataItem(e.item.index());
        	
        	$("#form-1 input[name='siteShortName']").val(dataItem.shortName);
    		displaySiteInfo(dataItem);
    		
        	nameSelected = true;
        },
        height: 400,
        delay: 500,
        minLength: 1,
        filter: "contains",
        noDataTemplate: "표시할 자료가 없습니다.",
    });
	
	// 재생시간 초기값(매체 설정)
	$("#form-1 input[name='defaultDurSecs']").val(medium_defaultDurSecs);
	$("#form-1 input[name='minDurSecs']").val(medium_minDurSecs);
	$("#form-1 input[name='maxDurSecs']").val(medium_maxDurSecs);
	$("#form-1 input[name='rangeDurAllowed']").prop("checked", medium_rangeDurAllowed);
	
	$("#form-1 input[name='rangeDurAllowed']").change(function() {
		$("#form-1 input[name='minDurSecs']").prop("disabled", !$(this).is(":checked"));
		$("#form-1 input[name='maxDurSecs']").prop("disabled", !$(this).is(":checked"));
	});
	
	$("#form-1 input[name='durationOverridden']").change(function() {
		if ($(this).is(":checked")) {
			$("#form-1 input[name='defaultDurSecs']").prop("disabled", !$(this).is(":checked"));
			$("#form-1 input[name='rangeDurAllowed']").prop("disabled", !$(this).is(":checked"));
			
			if ($("#form-1 input[name='rangeDurAllowed']").is(':checked')) {
				$("#form-1 input[name='minDurSecs']").prop("disabled", !$(this).is(":checked"));
				$("#form-1 input[name='maxDurSecs']").prop("disabled", !$(this).is(":checked"));
			}
		} else {
			$("#form-1 input[name='defaultDurSecs']").val(medium_defaultDurSecs);
			$("#form-1 input[name='minDurSecs']").val(medium_minDurSecs);
			$("#form-1 input[name='maxDurSecs']").val(medium_maxDurSecs);
			$("#form-1 input[name='rangeDurAllowed']").prop("checked", medium_rangeDurAllowed);

			$("#form-1 input[name='defaultDurSecs']").prop("disabled", !$(this).is(":checked"));
			$("#form-1 input[name='minDurSecs']").prop("disabled", !$(this).is(":checked"));
			$("#form-1 input[name='maxDurSecs']").prop("disabled", !$(this).is(":checked"));
			$("#form-1 input[name='rangeDurAllowed']").prop("disabled", !$(this).is(":checked"));
		}
	});

	
	$("#form-1 span[name='subtitle']").text(subtitle ? subtitle : "추가");
	
	validator = $("#form-1").validate({
		rules: {
			shortName: {
				minlength: 2, alphanumeric: true,
			},
			name: {
				minlength: 2, maxlength: 100,
			},
			cpm: { digits: true, min: 0 },
			effectiveStartDate: { date: true },
			effectiveEndDate: { date: true },
			defaultDurSecs: { digits: true, min: 5 },
			minDurSecs: { digits: true, min: 5 },
			maxDurSecs: { digits: true, min: 5 },
		}
	});
}


function isSiteShortNameValid() {
	
	var siteShortName = $.trim($("#form-1 input[name='siteShortName']").val());
	return (siteShortName && siteShortName.length > 0);
}


function checkSiteShortName() {
	
	var container = $("#form-1 div[name='siteShortName-con']");
	var feedback = $("#form-1 label[name='siteShortName-feedback']");
	
	if (isSiteShortNameValid()) {
		container.removeClass("is-invalid");
		feedback.removeClass("d-block").css("display", "none");
	} else {
		container.addClass("is-invalid");
		feedback.addClass("d-block").css("display", "block");
	}
}


function isSiteNameValid() {
	
	var siteName = $.trim($("#form-1 input[name='siteName']").val());
	return (siteName && siteName.length > 0);
}


function checkSiteName() {
	
	var container = $("#form-1 div[name='siteName-con']");
	var feedback = $("#form-1 label[name='siteName-feedback']");
	
	if (isSiteNameValid()) {
		container.removeClass("is-invalid");
		feedback.removeClass("d-block").css("display", "none");
	} else {
		container.addClass("is-invalid");
		feedback.addClass("d-block").css("display", "block");
	}
}


function saveForm1() {

	// kendo datepicker validation
	validateKendoDateValue($("#form-1 input[name='effectiveStartDate']"));
	validateKendoDateValue($("#form-1 input[name='effectiveEndDate']"));
	
	if (isSiteShortNameValid()) {
		delete errors["siteShortName"];
	} else {
		errors["siteShortName"] = "이 항목의 값을 입력하십시오.";
	}
	
	if (isSiteNameValid()) {
		delete errors["siteName"];
	} else {
		errors["siteName"] = "이 항목의 값을 입력하십시오.";
	}
	
	if ($("#form-1").valid() && Object.keys(errors).length == 0) {
    	var data = {
        		id: Number($("#form-1").attr("rowid")),
        		shortName: $.trim($("#form-1 input[name='shortName']").val()),
        		name: $.trim($("#form-1 input[name='name']").val()),
        		effectiveStartDate: $("#form-1 input[name='effectiveStartDate']").data("kendoDatePicker").value(),
        		effectiveEndDate: $("#form-1 input[name='effectiveEndDate']").data("kendoDatePicker").value(),
        		siteShortName: $.trim($("#form-1 input[name='siteShortName']").val()),
        		siteName: $.trim($("#form-1 input[name='siteName']").val()),
        		activeStatus: $("#form-1 input[name='activeStatus']").is(':checked'),
        		resolution: $("#form-1 select[name='resolution']").val(),
        		durationOverridden: $("#form-1 input[name='durationOverridden']").is(':checked'),
        		defaultDurSecs: Number($("#form-1 input[name='defaultDurSecs']").val()),
        		rangeDurAllowed: $("#form-1 input[name='rangeDurAllowed']").is(':checked'),
        		minDurSecs: Number($("#form-1 input[name='minDurSecs']").val()),
        		maxDurSecs: Number($("#form-1 input[name='maxDurSecs']").val()),
        		adServerAvailable: $("#form-1 input[name='adServerAvailable']").is(':checked'),
        		videoAllowed: $("#form-1 input[name='videoAllowed']").is(':checked'),
        		imageAllowed: $("#form-1 input[name='imageAllowed']").is(':checked'),
        		memo: $.trim($("#form-1 textarea[name='memo']").val()),
        		cpm: Number($("#form-1 input[name='cpm']").val()),
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
	} else {
		validator.showErrors(errors);
		
		checkSiteShortName();
		checkSiteName();
	}
}


function edit(id) {
	
	initForm1("변경");

	var dataItem = $("#grid").data("kendoGrid").dataSource.get(id);
	
	$("#form-1").attr("rowid", dataItem.id);
	$("#form-1").attr("url", "${updateUrl}");
	
	$("#form-1 input[name='siteShortName']").val(dataItem.site.shortName);
	$("#form-1 input[name='siteName']").val(dataItem.site.name);
	$("#form-1 input[name='regionName']").val(dataItem.site.regionName);
	$("#form-1 input[name='address']").val(dataItem.site.address);

	$("#form-1 input[name='cpm']").val(dataItem.floorCpm);
	
	bootstrapSelectVal($("#form-1 select[name='venueType']"), dataItem.site.venueType);

	$("#form-1 input[name='shortName']").val(dataItem.shortName);
	$("#form-1 input[name='name']").val(dataItem.name);
	
	$("#form-1 input[name='activeStatus']").prop("checked", dataItem.activeStatus);
	
	$("#form-1 input[name='adServerAvailable']").prop("checked", dataItem.adServerAvailable);
	
	$("#form-1 input[name='videoAllowed']").prop("checked", dataItem.videoAllowed);
	$("#form-1 input[name='imageAllowed']").prop("checked", dataItem.imageAllowed);


	bootstrapSelectVal($("#form-1 select[name='resolution']"), dataItem.resolution);

	$("#form-1 input[name='effectiveStartDate']").data("kendoDatePicker").value(dataItem.effectiveStartDate);
	$("#form-1 input[name='effectiveEndDate']").data("kendoDatePicker").value(dataItem.effectiveEndDate);

	$("#form-1 textarea[name='memo']").text(dataItem.memo);
	
	$("#form-1 input[name='durationOverridden']").prop("checked", dataItem.durationOverridden);
	if (dataItem.durationOverridden) {
		if (dataItem.defaultDurSecs != null) {
			$("#form-1 input[name='defaultDurSecs']").val(dataItem.defaultDurSecs);
		}
		if (dataItem.minDurSecs != null) {
			$("#form-1 input[name='minDurSecs']").val(dataItem.minDurSecs);
		}
		if (dataItem.maxDurSecs != null) {
			$("#form-1 input[name='maxDurSecs']").val(dataItem.maxDurSecs);
		}
		
		var rangeDurAllowed = dataItem.rangeDurAllowed != null && dataItem.rangeDurAllowed == true;
		$("#form-1 input[name='rangeDurAllowed']").prop("checked", rangeDurAllowed);
		
		$("#form-1 input[name='defaultDurSecs']").prop("disabled", false);
		$("#form-1 input[name='rangeDurAllowed']").prop("disabled", false);
		
		if (rangeDurAllowed) {
			$("#form-1 input[name='minDurSecs']").prop("disabled", false);
			$("#form-1 input[name='maxDurSecs']").prop("disabled", false);
		}
	}

	
	$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-1").modal();
}


function navToSite(id) {
	var path = "/inv/site?siteid=" + id;
	location.href = path;
}



var mediumBizHour = "${bizHour}";
var mediumBizHourUsed = true;


function displayBizHour(value) {
	
	// 운영 시간 표시
	ttSetValueStr(mediumBizHourUsed ? mediumBizHour : value, false);
	mouseUpAct();

	// 운영 시간 표시를 직접 진행
	for(var i = 0; i < timeTableIds.length; i++) {
		$("#tt" + i).removeClass("selected rselected nzselected");
		if (timeTableIds[i] == 1) {
			if (mediumBizHourUsed) {
				$("#tt" + i).addClass("nzselected");
			} else {
				$("#tt" + i).addClass("rselected");
			}
		}
	}
}


function initForm2(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-2").html()));
	
	$('[data-toggle="tooltip"]').tooltip();

	
	$("#form-2 span[name='subtitle']").text(subtitle ? subtitle : "추가");
}


function editTime(id) {
	
	initForm2("운영 시간");

	var dataItem = $("#grid").data("kendoGrid").dataSource.get(id);
	
	$("#form-2").attr("rowid", dataItem.id);
	$("#form-2").attr("url", "${updateTimeUrl}");
	
	$("#form-2 input[name='shortName']").val(dataItem.shortName);
	$("#form-2 input[name='name']").val(dataItem.name);


	// Time table 제어 코드
	timeTable = $("#time-table");
	timeTable.find("td").mousedown(function (e) {

		if (mediumBizHourUsed) {
			return false;
		}
		
		if($(this).hasClass("rselected")){
			isTimeMouseDown = true;
		} else {
			isTimeMouseDown2 = true;		  
		}
		  
		var cell = $(this);

		if (e.shiftKey) {
			ttSelectTo(cell);                
		} else {
			if(cell.hasClass("rselected")){
				cell.removeClass("rselected")
				timeTableIds.splice(cell.attr('id').substr(2),1,0);
			} else {
				cell.addClass("selected")
				timeTableIds.splice(cell.attr('id').substr(2),1,1);
			}

			timeStartCellIndex = cell.index() - 1;
			timeStartRowIndex = cell.parent().index();
		}
	      
		return false; // prevent text selection
		
	}).mouseover(function () {

		if (mediumBizHourUsed) {
			return;
		}

		if (isTimeMouseDown){
			timeTable.find(".nselected").addClass("rselected");
			ttSelectTo2($(this));
		}
		if(isTimeMouseDown2) {
			timeTable.find(".selected").removeClass("selected");
			ttSelectTo($(this));
		}
		
	}).bind("selectstart", function () {

		return false;
	});

	// 화면과 매체의 운영 시간을 비교하여 개별 설정
	mediumBizHourUsed = !(dataItem.bizHour && dataItem.bizHour.length == 168);

	
	$("#form-2 input[name='bizHourOverridden']").prop("checked", !mediumBizHourUsed);
	
	$("#form-2 input[name='bizHourOverridden']").change(function() {

		mediumBizHourUsed = !$(this).is(":checked");
		displayBizHour(mediumBizHour);
	});
	
	
	// 운영 시간 표시
	displayBizHour(dataItem.bizHour);
	
	
	$('#form-modal-2 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-2").modal();
	validateBizHours();
}


function validateBizHours() {

	if (Number($("#form-2 input[name='bizHrs']").val()) == 0) {
		$("#form-2 button[name='save-btn']").addClass("disabled");
	} else {
		$("#form-2 button[name='save-btn']").removeClass("disabled");
	}
}


function saveForm2() {

	if (Number($("#form-2 input[name='bizHrs']").val()) > 0) {
		var data = {
		   		id: Number($("#form-2").attr("rowid")),
		   		bizHour: ttGetValueStr(),
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


function mouseUpAct() {
	
	var cnt = 0;
	for(var i = 0; i < timeTableIds.length; i++) {
		if (timeTableIds[i] == 1) {
			cnt ++;
		}
	}
	
	if (Number($("#form-2 input[name='bizHrs']").val()) != cnt) {
		$("#form-2 input[name='bizHrs']").val(cnt);
	}
	validateBizHours();
}

</script>

<!--  / Scripts -->


<!-- / Page body -->





<!-- Functional tags -->

<func:screenInfoModal />
<func:cmmTimeTable />
<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
