<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ taglib prefix="adc" tagdir="/WEB-INF/tags/adc"%>


<!-- URL -->

<c:url value="/adc/campaign/hrlyplays/read" var="readUrl" />


<!-- Opening tags -->

<common:mediumPageOpeningNaverMap />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 fa-light fa-magnifying-glass-chart fa-fw"></span>
	${pageTitle}
	<span class="font-weight-light pl-1">시간별 통계</span>
</h4>

<hr class="border-light container-m--x mt-0 mb-4">





<!-- Page body -->


<!--  Overview header -->

<c:choose>
<c:when test="${empty Ad}">

<adc:campaign />

</c:when>
<c:otherwise>

<adc:campaign-ad />

</c:otherwise>
</c:choose>

<!--  / Overview header -->


<!--  Tab -->

<ul class="nav nav-tabs tabs-alt mb-4 mt-3 mr-auto d-flex">
	<li class="nav-item">
		<a class="nav-link" href="/adc/campaign/ads/${Campaign.id}">
			<i class="mr-1 fa-light fa-audio-description"></i>
			광고 목록
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" href="/adc/campaign/detail/${Campaign.id}">
			<i class="mr-1 fa-light fa-microscope"></i>
			광고 상세
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" href="/adc/campaign/creatives/${Campaign.id}">
			<i class="mr-1 fa-light fa-clapperboard-play"></i>
			광고 소재
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" href="/adc/campaign/invtargets/${Campaign.id}">
			<i class="mr-1 fa-light fa-bullseye-arrow"></i>
			<span id="inven-target-tab-title">인벤토리 타겟팅</span>
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" href="/adc/campaign/timetarget/${Campaign.id}">
			<i class="mr-1 fa-light fa-alarm-clock"></i>
			<span id="time-target-tab-title">시간 타겟팅</span>
		</a>
	</li>
	<li class="nav-item mr-auto">
		<a class="nav-link active" href="/adc/campaign/hrlyplays/${Campaign.id}">
			<i class="mr-1 fa-light fa-magnifying-glass-chart"></i>
			시간별 통계
		</a>
	</li>

<c:if test="${fn:length(currAds) > 0}" >

	<div class="mt-2 mr-1">
		<span class="fa-stack fa-xs" title="광고" style="vertical-align: top; color: #a0a0a0;">
			<span class="fa-solid fa-circle fa-stack-2x"></span>
			<span class="fa-solid fa-audio-description fa-stack-1x fa-inverse"></span>
		</span>
	</div>
	<select name="nav-item-ad-select" class="selectpicker bg-white mb-1" data-style="btn-default" data-none-selected-text="" data-width="200px" >

<c:forEach var="item" items="${currAds}">

		<option value="${item.value}">${item.text}</option>

</c:forEach>

	</select>

<script>
$(document).ready(function() {

	$("select[name='nav-item-ad-select']").selectpicker('render');

	$("select[name='nav-item-ad-select']").on("change.bs.select", function(e){
		location.href = "/adc/campaign/hrlyplays/${Campaign.id}/" + $("select[name='nav-item-ad-select']").val();
	});
	
	bootstrapSelectVal($("select[name='nav-item-ad-select']"), "${currAdId}");
	
});	
</script>

</c:if>
	
</ul>

<!--  / Tab -->


<!--  Root form container -->
<div id="formRoot"></div>


<!--  Page details -->

<c:choose>
<c:when test="${fn:length(currAds) == 0}" >

	<div class="card">
		<div class="card-body">
			<div class="form-row">
				<div class='container text-center my-4'>
					<div class='d-flex justify-content-center align-self-center'>
						<span class='fa-thin fa-diamond-exclamation fa-3x'></span>
						<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>현재 선택된 광고 없음</span>
					</div>
				</div>
			</div>
		</div>
	</div>

</c:when>
<c:otherwise>


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
			
	String playDateTemplate = net.doohad.utils.Util.getSmartDate("playDate", false, false);

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
<kendo:grid name="grid-hourly" pageable="true" filterable="true" sortable="true" scrollable="true" reorderable="true" resizable="true" selectable="single" >
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
		<kendo:grid-column title="날짜" field="playDate" width="120" template="<%= playDateTemplate %>" />
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
	</kendo:grid-columns>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="playDate" dir="desc"/>
		</kendo:dataSource-sort>
       	<kendo:dataSource-filter>
      		<kendo:dataSource-filterItem field="ad.id" operator="eq" logic="and" value="${Ad.id}" >
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
					<kendo:dataSource-schema-model-field name="playDate" type="date" />
					<kendo:dataSource-schema-model-field name="dateTotal" type="number" />
					<kendo:dataSource-schema-model-field name="succTotal" type="number" />
					<kendo:dataSource-schema-model-field name="failTotal" type="number" />
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


/* 텍스트 크기 표준보다는 작게 */
.text-large {
    font-size: 130% !important;
}


/* 그리드 자료 새로고침 버튼을 우측 정렬  */
div.k-pager-wrap.k-grid-pager.k-widget.k-floatwrap {
	display: flex!important;
	justify-content: flex-end!important;
}

</style>

<!-- / Kendo grid  -->


<!--  Forms -->

<script id="template-1" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-1">
	<div class="modal-dialog modal-sm">
		<form class="modal-content" id="form-1" rowid="-1" url="${linkUrl}">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					광고
					<span class="font-weight-light pl-1"><span name="subtitle"></span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
				<div class="form-group col">
					<label class="form-label">
						광고 소재
					</label>
					<select name="creative" class="selectpicker bg-white required" data-style="btn-default" data-none-selected-text="" >
<c:forEach var="item" items="${Creatives}">
							<option value="${item.value}">${item.text}</option>
</c:forEach>
					</select>
				</div>
				<div class="form-group col">
					<label class="form-label">
						시작일
						<span class="text-danger">*</span>
					</label>
					<input name="startDate" type="text" class="form-control required">
				</div>
				<div class="form-group col">
					<label class="form-label">
						종료일
						<span class="text-danger">*</span>
					</label>
					<input name="endDate" type="text" class="form-control required">
				</div>
				<div class="form-group col">
					<label class="form-label">
						광고 소재간 가중치
					</label>
					<input name="weight" type="text" class="form-control required" value="1">
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


<!-- Grid button actions  -->

<script>
$(document).ready(function() {

	// Link
	$("#link-btn").click(function(e) {
		e.preventDefault();
		
		initForm1();

		
		$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-1").modal();
	});
	// / Link
	
	// Unlink
	$("#unlink-btn").click(function(e) {
		e.preventDefault();
			
		var grid = $("#grid").data("kendoGrid");
		var rows = grid.select();
	
		var opRows = [];
		
		rows.each(function(index, row) {
			var selectedItem = grid.dataItem(row);
			opRows.push(selectedItem.id);
		});
		
		if (opRows.length > 0) {
			showConfirmModal("선택된 자료에 대한 연결 해제 작업을 수행할 예정입니다. 계속 진행하시겠습니까?", function(result) {
				if (result) {
					$.ajax({
						type: "POST",
						contentType: "application/json",
						dataType: "json",
						url: "${unlinkUrl}",
						data: JSON.stringify({ items: opRows }),
						success: function (form) {
							showOperationSuccessMsg();
							grid.dataSource.read();
						},
						error: ajaxOperationError
					});
				}
			}, true, opRows.length);
		}
	});
	// / Unlink
	
});	
</script>

<!-- / Grid button actions  -->


<!--  Scripts -->

<script>

function initForm1(subtitle) {

	$("#formRoot").html(kendo.template($("#template-1").html()));
	
	$("#form-1 select[name='creative']").selectpicker('render');
	
	var min = new Date(${Ad.startDateLong});
	var max = new Date(${Ad.endDateLong});
	
	$("#form-1 input[name='startDate']").kendoDatePicker({
		format: "yyyy-MM-dd",
		parseFormats: [
			"yyyy-MM-dd",
		],
		value: new Date(min),
		min: min,
		max: max,
	});
	
	$("#form-1 input[name='endDate']").kendoDatePicker({
		format: "yyyy-MM-dd",
		parseFormats: [
			"yyyy-MM-dd",
		],
		value: new Date(max),
		min: min,
		max: max,
	});
	
	
	$("#form-1 span[name='subtitle']").text(subtitle ? subtitle : "소재와 연결");
	
	$("#form-1").validate({
		rules: {
			name: {
				minlength: 2,
			},
			weight: {
				digits: true,
				min: 1,
			},
		}
	});
}


function saveForm1() {

	// kendo datepicker validation
	validateKendoDateValue($("#form-1 input[name='startDate']"));
	validateKendoDateValue($("#form-1 input[name='endDate']"));

	
	var creative = $("#form-1 select[name='creative']").val();
	
	if ($("#form-1").valid() && creative != "-1") {
    	var data = {
    		id: Number($("#form-1").attr("rowid")),
    		ad: ${Ad.id},
    		creative: Number($("#form-1 select[name='creative']").val()),
    		startDate: $("#form-1 input[name='startDate']").data("kendoDatePicker").value(),
    		endDate: $("#form-1 input[name='endDate']").data("kendoDatePicker").value(),
    		weight: Number($("#form-1 input[name='weight']").val()),
    	};
    	
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: $("#form-1").attr("url"),
			data: JSON.stringify(data),
			success: function (data, status, xhr) {
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
	
	
	$("#form-1 input[name='weight']").val(dataItem.weight);

	bootstrapSelectVal($("#form-1 select[name='creative']"), dataItem.creative.id);
	
	$("#form-1 input[name='startDate']").data("kendoDatePicker").value(dataItem.startDate);
	$("#form-1 input[name='endDate']").data("kendoDatePicker").value(dataItem.endDate);
	
	bootstrapSelectDisabled($("#form-1 select[name='creative']"), true);

	
	$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-1").modal();
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



</c:otherwise>
</c:choose>

<!--  / Page details -->


<!-- / Page body -->





<!-- Functional tags -->

<func:screenInfoModal />
<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
