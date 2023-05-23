<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/inv/site/create" var="createUrl" />
<c:url value="/inv/site/read" var="readUrl" />
<c:url value="/inv/site/update" var="updateUrl" />
<c:url value="/inv/site/destroy" var="destroyUrl" />

<c:url value="/inv/site/readVenueTypes" var="readVenueTypeUrl" />


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
			"<button type='button' onclick='edit(#= id #)' class='btn icon-btn btn-sm btn-outline-success borderless'>" + 
			"<span class='fas fa-pencil-alt'></span></button>";
	
	String nameTemplate =
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
			"<span class='pl-1'>#= name #</span>";
			
	String venueTypeTemplate =
			"# if (venueType == 'CVS') { #" +
				"<span class='fa-regular fa-store fa-fw text-green'></span><span class='pl-2'>편의점</span>" +
			"# } else if (venueType == 'BUSSH') { #" +
				"<span class='fa-regular fa-people-roof fa-fw text-orange'></span><span class='pl-2'>버스 쉘터</span>" +
			"# } else if (venueType == 'BLDG') { #" +
				"<span class='fa-regular fa-billboard fa-fw text-orange'></span><span class='pl-2'>빌딩 전광판</span>" +
			"# } else if (venueType == 'HOSP') { #" +
				"<span class='fa-regular fa-hospital fa-fw text-blue'></span><span class='pl-2'>병/의원</span>" +
			"# } #";
			
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
		<kendo:grid-column title="수정" width="50" filterable="false" sortable="false" template="<%= editTemplate %>" />
		<kendo:grid-column title="사이트명" field="name" width="200" template="<%= nameTemplate %>" />
		<kendo:grid-column title="사이트ID" field="shortName" width="150" />
		<kendo:grid-column title="서비스중" field="activeStatus" width="120"
				template="#=activeStatus ? \"<span class='fa-light fa-check'>\" : \"\"#" />
		<kendo:grid-column title="화면 수" field="screenCount" width="120" 
				template="<div><span>#= screenCount #</span><a href='javascript:navToScreens(#= id #)' class='btn btn-default btn-xs icon-btn ml-1'><span class='fa-regular fa-arrow-down-right'></span></a></div>" />
		<kendo:grid-column title="유효시작일" field="effectiveStartDate" template="<%= effStartDateTemplate %>" width="150"/>
		<kendo:grid-column title="유효종료일" field="effectiveEndDate" template="<%= effEndDateTemplate %>" width="150"/>
		<kendo:grid-column title="입지유형" field="siteCond.name" width="150" />
		<kendo:grid-column title="시/군/구" field="regionName" width="150" />
		<kendo:grid-column title="주소" field="address" width="500" />
		<kendo:grid-column title="장소유형" field="venueType" width="150"
				template="<%= venueTypeTemplate %>" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readVenueTypeUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
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
    	    		<kendo:dataSource-filterItem field="id" operator="eq" logic="and" value="<%= request.getAttribute(\"siteId\") %>">
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
					<kendo:dataSource-schema-model-field name="activeStatus" type="boolean" />
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
				<div class="form-row">
					<div class="form-group col">
						<label class="form-label">
							사이트명
							<span class="text-danger">*</span>
						</label>
						<input name="name" type="text" maxlength="100" class="form-control required">
					</div>
					<div class="form-group col">
						<label class="form-label">
							사이트ID
							<span class="text-danger">*</span>
						</label>
						<input name="shortName" type="text" maxlength="50" class="form-control required">
					</div>
				</div>
				<div class="form-row">
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
				</div>
				<div class="form-row">
					<div class="form-group col">
						<label class="form-label">
							위도
							<span class="text-danger">*</span>
						</label>
						<input name="latitude" type="text" class="form-control required">
					</div>
					<div class="form-group col">
						<label class="form-label">
							경도
							<span class="text-danger">*</span>
						</label>
						<input name="longitude" type="text" class="form-control required">
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col">
						<label class="form-label">
							입지 유형
						</label>
						<select name="siteCondType" class="selectpicker bg-white required" data-style="btn-default" data-none-selected-text="">
<c:forEach var="item" items="${SiteConds}">
							<option value="${item.value}">${item.text}</option>
</c:forEach>
						</select>
					</div>
					<div class="form-group col">
						<label class="form-label">
							장소 유형
							<span class="text-danger">*</span>
						</label>
						<select name="venueType" class="selectpicker bg-white required" data-style="btn-default" data-none-selected-text="">
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
				</div>
				<div class="form-row">
					<div class="form-group col-4">
						<label class="form-label">
							시/군/구
						</label>
						<select name="regionCode" class="selectpicker bg-white required" data-style="btn-default" data-none-selected-text=""
								data-none-results-text="조건에 맞는 자료는 없습니다." data-size="10"
								data-live-search="true">
<c:forEach var="item" items="${Regions}">
							<option value="${item.value}">${item.text}</option>
</c:forEach>
						</select>
					</div>
					<div class="form-group col-8">
						<label class="form-label">
							주소
						</label>
						<input name="address" type="text" class="form-control">
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
			<div class="modal-footer d-flex">
				<button type="button" class="btn btn-round btn-outline-secondary mr-auto" onClick="openMap()">
                	<span class="fa-light fa-map"></span>
                	<span class="ml-1">지도</span>
				</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button type="button" class="btn btn-primary" onclick='saveForm1()'>저장</button>
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


<div class="modal fade modal-level-plus-1" id="modal-site-map" tabindex="-1" role="dialog">
	<div class="modal-dialog modal-dialog-centered modal-lg" role="document">
		<div class="modal-content">
      
			<!-- Modal body -->
			<div class="modal-body mb-0 p-0">
				<div id="map" style="height:300px;"></div>
			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer d-flex">
				<button type="button" class="btn btn-round btn-outline-secondary mr-auto" onClick="moveToAddressPoint()">
                	<span class="fa-light fa-map-marked-alt"></span>
                	<span class="ml-1">주소로 이동</span>
				</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button type="button" class="btn btn-primary" onclick='setLatLng()'>현재 위치 설정</button>
			</div>
			
		</div>
	</div>
</div>

<!--  / Forms -->


<!--  Scripts -->

<script>

function initForm1(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-1").html()));
	
	$("#form-1 select[name='siteCondType']").selectpicker('render');
	$("#form-1 select[name='venueType']").selectpicker('render');
	$("#form-1 select[name='regionCode']").selectpicker('render');
	
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

	
	$("#form-1 span[name='subtitle']").text(subtitle ? subtitle : "추가");
	
	$("#form-1").validate({
		rules: {
			shortName: {
				minlength: 2, alphanumeric: true,
			},
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
	
	if ($("#form-1").valid()) {
    	var data = {
    		id: Number($("#form-1").attr("rowid")),
    		shortName: $.trim($("#form-1 input[name='shortName']").val()),
    		name: $.trim($("#form-1 input[name='name']").val()),
    		effectiveStartDate: $("#form-1 input[name='effectiveStartDate']").data("kendoDatePicker").value(),
    		effectiveEndDate: $("#form-1 input[name='effectiveEndDate']").data("kendoDatePicker").value(),
    		latitude: $.trim($("#form-1 input[name='latitude']").val()),
    		longitude: $.trim($("#form-1 input[name='longitude']").val()),
    		siteCondType: $("#form-1 select[name='siteCondType']").val(),
    		venueType: $("#form-1 select[name='venueType']").val(),
    		regionCode: $("#form-1 select[name='regionCode']").val(),
    		address: $.trim($("#form-1 input[name='address']").val()),
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
	
	$("#form-1 input[name='shortName']").val(dataItem.shortName);
	$("#form-1 input[name='name']").val(dataItem.name);
	$("#form-1 input[name='latitude']").val(dataItem.latitude);
	$("#form-1 input[name='longitude']").val(dataItem.longitude);
	$("#form-1 input[name='address']").val(dataItem.address);

	bootstrapSelectVal($("#form-1 select[name='siteCondType']"), dataItem.siteCond.code);
	bootstrapSelectVal($("#form-1 select[name='venueType']"), dataItem.venueType);
	bootstrapSelectVal($("#form-1 select[name='regionCode']"), dataItem.regionCode);

	$("#form-1 input[name='effectiveStartDate']").data("kendoDatePicker").value(dataItem.effectiveStartDate);
	$("#form-1 input[name='effectiveEndDate']").data("kendoDatePicker").value(dataItem.effectiveEndDate);

	$("#form-1 textarea[name='memo']").text(dataItem.memo);

	
	$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-1").modal();
}


function navToScreens(id) {
	var path = "/inv/screen?siteid=" + id;
	location.href = path;
}



var marker;
var map;


function openMap() {
	
	var latitude = $.trim($("#form-1 input[name='latitude']").val());
	var longitude = $.trim($("#form-1 input[name='longitude']").val());
	
	var lat = 37.5512164;
	var lng = 126.98824864606178;
	
	if (latitude && $.isNumeric(latitude)) {
		lat = Number(latitude);
	}

	if (longitude && $.isNumeric(longitude)) {
		lng = Number(longitude);
	}
	
	
	map = new naver.maps.Map('map', {
		center: new naver.maps.LatLng(lat, lng),
		zoom: 15
	}); 

	marker = new naver.maps.Marker({
		position: new naver.maps.LatLng(lat, lng),
		draggable: true,
		icon: "${markerUrl}",
		map: map
	});
	
	
	$('#modal-site-map').on('shown.bs.modal', function (e) {

		// Naver Map 버그 해결
		window.dispatchEvent(new Event('resize'));
	});
	
	$("#modal-site-map").modal();
}


function moveToAddressPoint() {
	
	var address = $.trim($("#form-1 input[name='address']").val());
	
	if (address) {
	    naver.maps.Service.geocode({
	    	query: address
	    }, function (status, response) {
	    	var item = response.v2.addresses[0];
	    	var point = new naver.maps.Point(item.x, item.y);
	    	
	    	map.setCenter(point);
	    	marker.setPosition(new naver.maps.LatLng(point, point));
	    });
	}
}


function setLatLng() {
	
	if (marker) {
		$("#form-1 input[name='latitude']").val(marker.position.lat());
		$("#form-1 input[name='longitude']").val(marker.position.lng());
		
		$("#modal-site-map").modal("hide");
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
