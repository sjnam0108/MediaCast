<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/adc/campaign/create" var="createUrl" />
<c:url value="/adc/campaign/update" var="updateUrl" />
<c:url value="/adc/campaign/read" var="readUrl" />
<c:url value="/adc/campaign/destroy" var="destroyUrl" />

<c:url value="/adc/campaign/createAd" var="createAdUrl" />
<c:url value="/adc/campaign/readStatuses" var="readStatusUrl" />


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

<link rel="stylesheet" href="/resources/vendor/lib/bootstrap-slider/bootstrap-slider.css">

<script>$.fn.slider = null</script>
<script type="text/javascript" src="/resources/vendor/lib/bootstrap-slider/bootstrap-slider.js"></script>


<!-- Java(optional)  -->

<%
	String editTemplate = 
			"<button type='button' onclick='edit(#= id #)' class='btn icon-btn btn-sm btn-outline-success borderless'>" + 
			"<span class='fas fa-pencil-alt'></span></button>";
	String advertiserTemplate = "<a href='javascript:navToAdv(#= advertiser.id #)'><span class='text-link'>#= advertiser.name #</span></a>";
	String freqCapTemplate =
			"# if (freqCap > 0) { #" +
				"<span>#= freqCap #</span>" +
			"# } else { #" +
				"<span></span>" +
			"# } #";
	String statusTemplate =
			"# if (status == 'U') { #" +
				"<span class='fa-regular fa-alarm-clock'></span><span class='pl-2'>시작전</span>" +
			"# } else if (status == 'R') { #" +
				"<span class='fa-regular fa-bolt-lightning text-orange'></span><span class='pl-2'>진행</span>" +
			"# } else if (status == 'C') { #" +
				"<span class='fa-regular fa-flag-checkered'></span><span class='pl-2'>완료</span>" +
			"# } else { #" +
				"<span>-</span>" +
			"# } #";
			
	String startDateTemplate = net.doohad.utils.Util.getSmartDate("startDate", false, false);
	String endDateTemplate = net.doohad.utils.Util.getSmartDate("endDate", false, false);
	String regDateTemplate = net.doohad.utils.Util.getSmartDate("whoCreationDate", false);
	String lastUpdateDateTemplate = net.doohad.utils.Util.getSmartDate("whoLastUpdateDate", false);

	String noRecordsTemplate =
			"<div class='container text-center my-4'>" +
				"<div class='d-flex justify-content-center align-self-center'>" +
					"<span class='fa-thin fa-hexagon-exclamation fa-3x'></span>" +
					"<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>해당 자료 없음</span>" +
				"</div>" +
			"</div>";
			
	String nameTemplate =
			"<div>" +
				"<a href='javascript:navToCamp(#= id #)'><span class='text-link'>#= name #</span></a>" + 
				"# if (statusCard == 'Y') { #" +
					"<span class='pl-1'></span><span title='오늘 진행되는 광고 소재 없음'><span class='fa-solid fa-rectangle-vertical text-yellow'></span></span>" +
				"# } else if (statusCard == 'R') { #" +
					"<span class='pl-1'></span><span title='활성중인 광고 소재 없음'><span class='fa-solid fa-rectangle-vertical text-danger'></span></span>" +
				"# } #" +
			"</div>";

%>

<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid" pageable="true" filterable="true" sortable="true" scrollable="true" reorderable="true" resizable="true" selectable="single" >
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
	<kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<div class="btn-group">
					<button type="button" class="btn btn-outline-success dropdown-toggle" data-toggle="dropdown">추가</button>
					<div class="dropdown-menu">
						<a class="dropdown-item" href="javascript:void(0)" id="add-btn">
							<i class="fa-light fa-briefcase fa-fw"></i><span class="pl-2">캠페인만</span>
						</a>
						<a class="dropdown-item" href="javascript:void(0)" id="add-together-btn">
							<i class="fa-light fa-audio-description fa-fw"></i><span class="pl-2">캠페인과 광고를 함께</span>
						</a>
					</div>    			
    			</div>
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
		<kendo:grid-column title="수정" width="50" filterable="false" sortable="false" template="<%= editTemplate %>" />
		<kendo:grid-column title="캠페인명" field="name" width="250" template="<%= nameTemplate %>" />
		<kendo:grid-column title="상태" field="status" width="120" template="<%= statusTemplate %>" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readStatusUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="광고 수" width="80" filterable="false" sortable="false"
				template="<span>#= adCount #</span>" />
		<kendo:grid-column title="광고주" field="advertiser.name" width="200" template="<%= advertiserTemplate %>" />
		<kendo:grid-column title="시작일" field="startDate" width="120" template="<%= startDateTemplate %>" />
		<kendo:grid-column title="종료일" field="endDate" width="120" template="<%= endDateTemplate %>" />
		<kendo:grid-column title="송출금지(초)" field="freqCap" width="150" template="<%= freqCapTemplate %>" />
		<kendo:grid-column title="최근 변경" field="whoLastUpdateDate" width="120" template="<%= lastUpdateDateTemplate %>" />
		<kendo:grid-column title="등록" field="whoCreationDate" width="120" template="<%= regDateTemplate %>" />
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="whoCreationDate" dir="desc"/>
		</kendo:dataSource-sort>

   		<c:choose>
   		<c:when test="${requestScope['initFilterApplied']}">
        	<kendo:dataSource-filter>
   	    		<kendo:dataSource-filterItem field="medium.id" operator="eq" logic="and" value="${sessionScope['currMediumId']}" >
    	    		<kendo:dataSource-filterItem field="id" operator="eq" logic="and" value="<%= request.getAttribute(\"campaignId\") %>">
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
					<kendo:dataSource-schema-model-field name="freqCap" type="number" />
					<kendo:dataSource-schema-model-field name="startDate" type="date" />
					<kendo:dataSource-schema-model-field name="endDate" type="date" />
					<kendo:dataSource-schema-model-field name="whoCreationDate" type="date" />
					<kendo:dataSource-schema-model-field name="whoLastUpdateDate" type="date" />
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

	// Add(캠페인)
	$("#add-btn").click(function(e) {
		e.preventDefault();
		
		
		initForm1();

		
		$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-1").modal();
	});
	// / Add
	
	// Add(캠페인+광고)
	$("#add-together-btn").click(function(e) {
		e.preventDefault();
		
		
		initForm2();

		
		$('#form-modal-2 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-2").modal();
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
						캠페인명
						<span class="text-danger">*</span>
					</label>
					<input name="name" type="text" maxlength="100" class="form-control required">
				</div>
				<div class="form-group col">
					<label class="form-label">
						광고주
					</label>
					<select name="advertiser" class="selectpicker bg-white required" data-style="btn-default" data-none-selected-text="">
<c:forEach var="item" items="${Advertisers}">
						<option value="${item.value}">${item.text}</option>
</c:forEach>
					</select>
				</div>
				<div class="form-group col">
					<label class="form-label">
						동일 광고주 광고 송출 금지 시간
						<span class="text-danger">*</span>
						<span data-toggle="tooltip" data-placement="right" title="이 캠페인에 속한 광고 후, 이 캠페인의 다음 광고가 송출되지 않도록 보장되는 시간입니다.">
							<span class="fa-regular fa-circle-info text-info"></span>
						</span>
					</label>
					<div class="input-group">
						<input name="freqCap" type="text" class="form-control required" value="0">
						<div class="input-group-append">
							<span class="input-group-text">초</span>
						</div>
					</div>
				</div>
				<div class="form-group col">
					<label class="form-label">
						운영자 메모
					</label>
					<textarea name="memo" rows="2" maxlength="150" class="form-control"></textarea>
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
		<form class="modal-content" id="form-2" rowid="-1" url="${createAdUrl}">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					캠페인 + 광고
					<span class="font-weight-light pl-1">추가</span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
				<div class="form-row">
					<div class="form-group col">
						<label class="form-label">
							캠페인/광고명
							<span class="text-danger">*</span>
						</label>
						<input name="name" type="text" maxlength="100" class="form-control required">
					</div>
					<div class="form-group col">
						<label class="form-label">
							광고주
						</label>
						<select name="advertiser" class="selectpicker bg-white required" data-style="btn-default" data-none-selected-text="">
<c:forEach var="item" items="${Advertisers}">
						<option value="${item.value}">${item.text}</option>
</c:forEach>
						</select>
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col-3">
						<label class="form-label">
							구매 유형
						</label>
						<select name="purchType" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
							<option value="G" data-icon="fa-regular fa-hexagon-check text-blue fa-fw mr-1">목표 보장</option>
							<option value="N" data-icon="fa-regular fa-hexagon-exclamation fa-fw mr-1">목표 비보장</option>
							<option value="H" data-icon="fa-regular fa-house fa-fw mr-1">하우스 광고</option>
						</select>
					</div>
					<div class="form-group col-3">
						<label class="form-label">
							동일 광고 송출 금지 시간
							<span class="text-danger">*</span>
							<span data-toggle="tooltip" data-placement="right" title="동일한 광고가 송출되지 않도록 보장되는 시간입니다.">
								<span class="fa-regular fa-circle-info text-info"></span>
							</span>
						</label>
						<div class="input-group">
							<input name="freqCap" type="text" class="form-control required" value="0">
							<div class="input-group-append">
								<span class="input-group-text">초</span>
							</div>
						</div>
					</div>
					<div class="form-group col-3">
						<label class="form-label">
							시작일
							<span class="text-danger">*</span>
						</label>
						<input name="startDate" type="text" class="form-control required">
					</div>
					<div class="form-group col-3">
						<label class="form-label">
							종료일
							<span class="text-danger">*</span>
						</label>
						<input name="endDate" type="text" class="form-control required">
					</div>
				</div>
				<div class="form-row" name="purch-type-depending-div">
					<div class="form-group col">
						<label class="form-label">
							우선 순위
							<span data-toggle="tooltip" data-placement="right" title="최고 1에서 최저 10 사이의 값을 선택해 주세요. 기본 선택값은 5입니다.">
								<span class="fa-regular fa-circle-info text-info"></span>
							</span>
						</label>
						<div class="slider-primary">
							<input name="priority" type="text" data-slider-min="1" data-slider-max="10" data-slider-step="1" data-slider-value="5">
						</div>
					</div>
                </div>
				<div class="form-row" name="purch-type-depending-div">
					<div class="form-group col">
						<label class="form-label">
							광고 예산
						</label>
						<div class="input-group">
							<input name="budget" type="text" value="0" class="form-control input-change required" >
							<div class="input-group-append">
								<span class="input-group-text">원</span>
							</div>
						</div>
					</div>
					<div class="form-group col">
						<label class="form-label">
							보장 노출횟수
						</label>
						<div class="input-group">
							<input name="goalValue" type="text" value="0" class="form-control input-change required" >
							<div class="input-group-append">
								<span class="input-group-text">회</span>
							</div>
						</div>
					</div>
					<div class="form-group col">
						<label class="form-label">
							목표 노출횟수
						</label>
						<div class="input-group">
							<input name="sysValue" type="text" value="0" class="form-control input-change required" >
							<div class="input-group-append">
								<span class="input-group-text">회</span>
							</div>
						</div>
					</div>
				</div>
				<div class="form-row" name="purch-type-depending-div">
					<div class="form-group col">
						<label class="form-label">
							집행 방법
							<span data-toggle="tooltip" data-placement="right" title="광고 예산과 보장/목표 노출횟수 값에 따라 집행 방법이 결정됩니다.">
								<span class="fa-regular fa-circle-info text-info"></span>
							</span>
						</label>
						<select name="goalType" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="" disabled>
							<option value="A" data-icon="fa-regular fa-sack-dollar fa-fw mr-1">광고 예산</option>
							<option value="I" data-icon="fa-regular fa-eye fa-fw mr-1">노출횟수</option>
							<option value="J" data-icon="fa-regular fa-eye fa-fw mr-1">화면당1일노출(삭제예정)</option>
							<option value="U" data-icon="fa-regular fa-infinity fa-fw mr-1">무제한 노출</option>
						</select>
					</div>
					<div class="form-group col">
						<label class="form-label">
							CPM
						</label>
						<select name="cpmType" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
							<option value="0" data-icon="fa-regular fa-screen-users fa-fw mr-1">화면 설정값 기준</option>
							<option value="5" data-icon="fa-regular fa-input-numeric fa-fw mr-1">값 지정</option>
						</select>
					</div>
					<div name="cpm-value-div" class="form-group col" style="display: none;">
						<label class="form-label">
							CPM 지정
						</label>
						<div class="input-group">
							<input name="cpm" type="text" value="1000" class="form-control required" >
							<div class="input-group-append">
								<span class="input-group-text">원</span>
							</div>
						</div>
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col">
						<label class="form-label">
							하루 노출한도
							<span data-toggle="tooltip" data-placement="right" title="하루 동안 매체 전체 화면에서의 총합이며, 0은 한도가 없음을 의미합니다.">
								<span class="fa-regular fa-circle-info text-info"></span>
							</span>
						</label>
						<div class="input-group">
							<input name="dailyCap" type="text" value="0" class="form-control required" >
							<div class="input-group-append">
								<span class="input-group-text">회</span>
							</div>
						</div>
					</div>
					<div class="form-group col">
						<label class="form-label">
							화면당 하루 노출한도
							<span data-toggle="tooltip" data-placement="right" title="0은 한도가 없음을 의미합니다.">
								<span class="fa-regular fa-circle-info text-info"></span>
							</span>
						</label>
						<div class="input-group">
							<input name="dailyScrCap" type="text" value="0" class="form-control required" >
							<div class="input-group-append">
								<span class="input-group-text">회</span>
							</div>
						</div>
					</div>
					<div class="form-group col">
						<label class="form-label">
							재생 시간
						</label>
						<select name="durationType" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
							<option value="0" data-icon="fa-regular fa-screen-users fa-fw mr-1">화면 설정값 기준</option>
							<option value="5" data-icon="fa-regular fa-input-numeric fa-fw mr-1">값 지정</option>
						</select>
					</div>
					<div class="form-group col" name="duration-value-div" style="display: none;">
						<label class="form-label">
							재생 시간 지정
						</label>
						<div class="input-group">
							<input name="durSecs" type="text" value="15" class="form-control required">
							<div class="input-group-append">
								<span class="input-group-text">초</span>
							</div>
						</div>
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col mb-0">
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
				<button type="button" class="btn btn-primary" onclick='saveForm2()'>저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>

<!--  / Forms -->


<!--  Scripts -->

<script>

function initForm1(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-1").html()));

	$("#form-1 select[name='advertiser']").selectpicker('render');
	
	$('[data-toggle="tooltip"]').tooltip();

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
			name: {
				minlength: 2,
			},
			freqCap: {
				digits: true,
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
    		freqCap: Number($("#form-1 input[name='freqCap']").val()),
    		memo: $.trim($("#form-1 textarea[name='memo']").val()),
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
	
	$("#form-1 input[name='name']").val(dataItem.name);
	$("#form-1 input[name='freqCap']").val(dataItem.freqCap);

	bootstrapSelectVal($("#form-1 select[name='advertiser']"), dataItem.advertiser.id);

	$("#form-1 textarea[name='memo']").text(dataItem.memo);
	
	bootstrapSelectDisabled($("#form-1 select[name='advertiser']"), true);

	
	$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-1").modal();
}


function validatePurchType() {

	if ($("#form-2 select[name='purchType']").val() == "H") {
		$("#form-2 div[name='purch-type-depending-div']").hide();
	} else {
		$("#form-2 div[name='purch-type-depending-div']").show();
	}
}


function validateDurationType() {
	
	if ($("#form-2 select[name='durationType']").val() == "0") {
		$("#form-2 div[name='duration-value-div']").hide();
		$("#form-2 input[name='durSecs']").attr('readonly', 'readonly');
	} else {
		$("#form-2 input[name='durSecs']").removeAttr('readonly');
		$("#form-2 div[name='duration-value-div']").show();
		$("#form-2 input[name='durSecs']").select();
		$("#form-2 input[name='durSecs']").focus();
	}
}


function validateCpmType() {
	
	if ($("#form-2 select[name='cpmType']").val() == "0") {
		$("#form-2 div[name='cpm-value-div']").hide();
		$("#form-2 input[name='cpm']").attr('readonly', 'readonly');
	} else {
		$("#form-2 input[name='cpm']").removeAttr('readonly');
		$("#form-2 div[name='cpm-value-div']").show();
		$("#form-2 input[name='cpm']").select();
		$("#form-2 input[name='cpm']").focus();
	}
}


function initForm2(subtitle) {
	
	//
	// 이 메소드의 코드는 ad 페이지의 initForm1()을 이 페이지에 맞게 최소한의 변경을 진행
	//
	//   - form name 변경(form-1을 모두 form-2로)
	//   - campaign의 "동일 광고주 광고 송출 금지 시간", "운영자 메모"는 생략(기본값으로 대체)
	//   - campaign SELECT 없음
	//
	
	$("#formRoot").html(kendo.template($("#template-2").html()));

	$("#form-2 select[name='advertiser']").selectpicker('render');
	
	// -- (S)
	//$("#form-1 select[name='campaign']").selectpicker('render');
	$("#form-2 select[name='purchType']").selectpicker('render');
	$("#form-2 select[name='durationType']").selectpicker('render');
	$("#form-2 select[name='goalType']").selectpicker('render');
	$("#form-2 select[name='cpmType']").selectpicker('render');
	
	var today = new Date();
	today.setHours(0, 0, 0, 0);
	
	var start = new Date();
	var end = new Date();
	start.setDate(today.getDate() + 2);
	end.setDate(today.getDate() + 31);
	
	$("#form-2 input[name='startDate']").kendoDatePicker({
		format: "yyyy-MM-dd",
		parseFormats: [
			"yyyy-MM-dd",
		],
		value: start,
		//min: today,
	});
	
	$("#form-2 input[name='endDate']").kendoDatePicker({
		format: "yyyy-MM-dd",
		parseFormats: [
			"yyyy-MM-dd",
		],
		value: end,
		//min: today,
	});
	
	$("#form-2 select[name='purchType']").on("change.bs.select", function(e){
		validatePurchType();
	});
	
	$("#form-2 select[name='durationType']").on("change.bs.select", function(e){
		validateDurationType();
	});
	
	$("#form-2 select[name='cpmType']").on("change.bs.select", function(e){
		validateCpmType();
	});
	
	$(".input-change").change(function(){
		// 예산, 보장 노출횟수, 목표 노출횟수
		var budget = Number($.trim($("#form-2 input[name='budget']").val()));
		var goalValue = Number($.trim($("#form-2 input[name='goalValue']").val()));
		var sysValue = Number($.trim($("#form-2 input[name='sysValue']").val()));
		
	    if (goalValue > 0 || sysValue > 0) {
	    	bootstrapSelectVal($("#form-2 select[name='goalType']"), "I");
	    } else if (budget > 0) {
	    	bootstrapSelectVal($("#form-2 select[name='goalType']"), "A");
	    } else {
	    	bootstrapSelectVal($("#form-2 select[name='goalType']"), "U");
	    }
	});

	
	//bootstrapSelectVal($("#form-2 select[name='campaign']"), "-1");
	bootstrapSelectVal($("#form-2 select[name='goalType']"), "U");
	
	$("#form-2 textarea[name='memo']").keypress(function (e) {
		if (e.keyCode != 13) {
			return;
		}
		
		$(this).text().replace(/\n/g, "");
		
		return false;
	});
	
	$('[data-toggle="tooltip"]').tooltip();

	
	if (subtitle == null) {		
		// ADD 모드
		$("#form-2 input[name='startDate']").data("kendoDatePicker").min(today);
		$("#form-2 input[name='endDate']").data("kendoDatePicker").min(today);
	}

	$("#form-2 input[name='priority']").slider();
	

	$("#form-2 span[name='subtitle']").text(subtitle ? subtitle : "추가");
	

	$("#form-2").validate({
		rules: {
			name: {
				minlength: 2,
			},
			startDate: { date: true },
			endDate: { date: true },
			budget: {
				digits: true,
			},
			sysValue: {
				digits: true,
			},
			cpm: {
				digits: true,
				min: 1,
			},
			durSecs: {
				digits: true,
				min: 5,
			},
			freqCap: {
				digits: true,
			},
			goalValue: {
				digits: true,
			},
			dailyCap: {
				digits: true,
			},
			dailyScrCap: {
				digits: true,
			},
		}
	});
	// -- (E)
}


function saveForm2() {

	// kendo datepicker validation
	validateKendoDateValue($("#form-2 input[name='startDate']"));
	validateKendoDateValue($("#form-2 input[name='endDate']"));

	var advertiser = Number($("#form-2 select[name='advertiser']").val());

	
	if ($("#form-2").valid() && advertiser != -1) {
		
		var goAhead = true;
		
		var durationType = $("#form-2 select[name='durationType']").val();
		var durSecs = Number($.trim($("#form-2 input[name='durSecs']").val()));
		if (durationType == "0") {
			durSecs = 0;
		} else {
			goAhead = durSecs >= 5;
		}
		
		var cpmType = $("#form-2 select[name='cpmType']").val();
		var cpm = Number($.trim($("#form-2 input[name='cpm']").val()));
		if (cpmType == "0") {
			cpm = 0;
		} else {
			goAhead = goAhead && cpm >= 1;
		}
		
    	var data = {
       		id: Number($("#form-2").attr("rowid")),
    		name: $.trim($("#form-2 input[name='name']").val()),
    		purchType: $("#form-2 select[name='purchType']").val(),
    		priority: Number($.trim($("#form-2 input[name='priority']").val())),
    		startDate: $("#form-2 input[name='startDate']").data("kendoDatePicker").value(),
    		endDate: $("#form-2 input[name='endDate']").data("kendoDatePicker").value(),
    		freqCap: Number($("#form-2 input[name='freqCap']").val()),
    		cpm: Number($("#form-2 input[name='cpm']").val()),
    		durSecs: durSecs,
    		goalType: $("#form-2 select[name='goalType']").val(),
    		goalValue: Number($.trim($("#form-2 input[name='goalValue']").val())),
       		sysValue: Number($("#form-2 input[name='sysValue']").val()),
       		budget: Number($("#form-2 input[name='budget']").val()),
    		dailyCap: Number($("#form-2 input[name='dailyCap']").val()),
       		dailyScrCap: Number($("#form-2 input[name='dailyScrCap']").val()),
    		cpm: cpm,
    		memo: $.trim($("#form-2 textarea[name='memo']").val()),
    		
    		advertiser: advertiser,
    	};
    	
    	if (goAhead) {
        	
    		$.ajax({
    			type: "POST",
    			contentType: "application/json",
    			dataType: "json",
    			url: $("#form-2").attr("url"),
    			data: JSON.stringify(data),
    			success: function (data, status, xhr) {
    				showSaveSuccessMsg();
    				$("#form-modal-2").modal("hide");
    				$("#grid").data("kendoGrid").dataSource.read();
    			},
    			error: ajaxSaveError
    		});
    	}
	}
}


function navToAdv(advId) {
	var path = "/adc/creative/creatives/" + advId;
	location.href = path;
}


function navToCamp(campId) {
	var path = "/adc/campaign/ads/" + campId;
	location.href = path;
}

</script>

<!--  / Scripts -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
