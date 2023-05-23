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

<c:url value="/adc/creative/invtargets/read" var="readUrl" />
<c:url value="/adc/creative/invtargets/readOrdered" var="readOrderedUrl" />
<c:url value="/adc/creative/invtargets/reorder" var="reorderUrl" />
<c:url value="/adc/creative/invtargets/calcScrCount" var="calcScrCountUrl" />
<c:url value="/adc/creative/invtargets/updateFilterType" var="updateFilterTypeUrl" />
<c:url value="/adc/creative/invtargets/destroy" var="destroyUrl" />

<c:url value="/adc/creative/invtargets/readACRegion" var="readRegionUrl" />
<c:url value="/adc/creative/invtargets/readACScreen" var="readScreenUrl" />
<c:url value="/adc/creative/invtargets/readACState" var="readStateUrl" />
<c:url value="/adc/creative/invtargets/readACSite" var="readSiteUrl" />
<c:url value="/adc/creative/invtargets/readACSiteCond" var="readSiteCondUrl" />

<c:url value="/adc/creative/invtargets/saveRegion" var="saveRegionUrl" />
<c:url value="/adc/creative/invtargets/saveScreen" var="saveScreenUrl" />
<c:url value="/adc/creative/invtargets/saveState" var="saveStateUrl" />
<c:url value="/adc/creative/invtargets/saveSite" var="saveSiteUrl" />
<c:url value="/adc/creative/invtargets/saveSiteCond" var="saveSiteCondUrl" />


<!-- Opening tags -->

<common:mediumPageOpeningNaverMap />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 fa-light fa-bullseye-arrow fa-fw"></span>
	${pageTitle}
	<span class="font-weight-light pl-1">인벤토리 타겟팅</span>
</h4>

<hr class="border-light container-m--x mt-0 mb-4">





<!-- Page body -->


<!--  Overview header -->

<c:choose>
<c:when test="${empty Creative}">

<adc:advertiser />

</c:when>
<c:otherwise>

<adc:advertiser-creative />

</c:otherwise>
</c:choose>

<!--  / Overview header -->


<!--  Tab -->
<ul class="nav nav-tabs tabs-alt mb-4 mt-3">
	<li class="nav-item">
		<a class="nav-link" href="/adc/creative/creatives/${Advertiser.id}">
			<i class="mr-1 fa-light fa-clapperboard-play"></i>
			소재 목록
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" href="/adc/creative/detail/${Advertiser.id}">
			<i class="mr-1 fa-light fa-microscope"></i>
			소재 상세
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" href="/adc/creative/files/${Advertiser.id}">
			<i class="mr-1 fa-light fa-photo-film-music"></i>
			소재 파일
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link active" href="/adc/creative/invtargets/${Advertiser.id}">
			<i class="mr-1 fa-light fa-bullseye-arrow"></i>
			<span id="inven-target-tab-title">인벤토리 타겟팅</span>
		</a>
	</li>
	<li class="nav-item mr-auto">
		<a class="nav-link" href="/adc/creative/timetarget/${Advertiser.id}">
			<i class="mr-1 fa-light fa-alarm-clock"></i>
			<span id="time-target-tab-title">시간 타겟팅</span>
		</a>
	</li>

<c:if test="${fn:length(currCreatives) > 0}" >

	<div class="mt-2 mr-1">
		<span class="fa-stack fa-xs" title="광고 소재" style="vertical-align: top; color: #a0a0a0;">
			<span class="fa-solid fa-circle fa-stack-2x"></span>
			<span class="fa-solid fa-clapperboard-play fa-stack-1x fa-inverse fa-lg"></span>
		</span>
	</div>
	<select name="nav-item-creat-select" class="selectpicker bg-white mb-1" data-style="btn-default" data-none-selected-text="" data-width="200px" >

<c:forEach var="item" items="${currCreatives}">

		<option value="${item.value}">${item.text}</option>

</c:forEach>

	</select>

<script>
$(document).ready(function() {

	$("select[name='nav-item-creat-select']").selectpicker('render');

	$("select[name='nav-item-creat-select']").on("change.bs.select", function(e){
		location.href = "/adc/creative/invtargets/${Advertiser.id}/" + $("select[name='nav-item-creat-select']").val();
	});
	
	bootstrapSelectVal($("select[name='nav-item-creat-select']"), "${currCreatId}");
	
});	
</script>

</c:if>
	
</ul>
<!--  / Tab -->


<!--  Root form container -->
<div id="formRoot"></div>


<!--  Page details -->

<c:choose>
<c:when test="${fn:length(currCreatives) == 0}" >

	<div class="card">
		<div class="card-body">
			<div class="form-row">
				<div class='container text-center my-4'>
					<div class='d-flex justify-content-center align-self-center'>
						<span class='fa-thin fa-diamond-exclamation fa-3x'></span>
						<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>현재 선택된 광고 소재 없음</span>
					</div>
				</div>
			</div>
		</div>
	</div>

</c:when>
<c:otherwise>


<!-- Page scripts  -->


<link href="/resources/vendor/lib/bootstrap-toggle/bootstrap-toggle.min.css" rel="stylesheet">
<link rel="stylesheet" href="/resources/vendor/lib/dragula/dragula.css">

<script src="/resources/vendor/lib/bootstrap-toggle/bootstrap-toggle.min.js"></script>
<script src="/resources/vendor/lib/dragula/dragula.js"></script>
<script src="/resources/vendor/lib/sortablejs/sortable.js"></script>


<!-- Java(optional)  -->

<%
	String noRecordsTemplate =
			"<div class='container text-center my-4'>" +
				"<div class='d-flex justify-content-center align-self-center'>" +
					"<span class='fa-thin fa-hexagon-exclamation fa-3x'></span>" +
					"<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>해당 자료 없음</span>" +
				"</div>" +
			"</div>";
			
			
	String invenTypeTemplate =
			"# if (invenType == 'RG') { #" +
				"<span class='fa-regular fa-mountain-city fa-fw'></span><span class='pl-1'>시/군/구</span>" +
			"# } else if (invenType == 'CT') { #" +
				"<span class='fa-regular fa-city fa-fw'></span><span class='pl-1'>광역시/도</span>" +
			"# } else if (invenType == 'SC') { #" +
				"<span class='fa-regular fa-screen-users fa-fw'></span><span class='pl-1'>매체 화면</span>" +
			"# } else if (invenType == 'ST') { #" +
				"<span class='fa-regular fa-map-pin fa-fw'></span><span class='pl-1'>사이트</span>" +
			"# } else if (invenType == 'CD') { #" +
				"<span class='fa-regular fa-location-crosshairs fa-fw'></span><span class='pl-1'>입지 유형</span>" +
			"# } else { #" +
				"<span>-</span>" +
			"# } #";

	String andOrTemplate =
			"<div class='toggle-container'>" +
				"<input class='toggle-btn' type='checkbox' data-toggle='toggle' data-on='And' data-off='Or' " +
						"data-onstyle='info' data-offstyle='success' rowid='#= id #' #= filterType == 'A' ? 'checked' : '' #>" +
			"</div>";

	String tgtDispTemplate =
			"<span class='pr-1'>#= tgtDisplay #</span>" +
			"<button type='button' onclick='edit(#= id #)' class='btn icon-btn btn-sm btn-outline-success borderless'>" + 
			"<span class='fas fa-pencil-alt'></span></button>";
			
	String geoLocTemplate =
			"<a href='javascript:showGeoLoc(\"CREATINVEN\", #= id #)' class='btn btn-default btn-xs icon-btn'>" +
				"<span class='fa-regular fa-location-dot text-info'></span>" +
			"</a>";
%>



<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid" pageable="true" filterable="false" sortable="false" scrollable="true" reorderable="true" resizable="true" selectable="${value_gridSelectable}" >
	<kendo:grid-pageable refresh="true" previousNext="false" numeric="false" pageSize="10000" info="false" />
	<kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button type="button" class="btn btn-outline-success dropdown-toggle" data-toggle="dropdown">타겟팅 추가</button>
				<div class="dropdown-menu">
					<a class="dropdown-item" href="javascript:void(0)" id="inv-state-btn">
						<i class="fa-light fa-city fa-fw"></i><span class="pl-2">광역시/도</span>
					</a>
					<a class="dropdown-item" href="javascript:void(0)" id="inv-region-btn">
						<i class="fa-light fa-mountain-city fa-fw"></i><span class="pl-2">시/군/구</span>
					</a>
					<div class="dropdown-divider"></div>
					<a class="dropdown-item" href="javascript:void(0)" id="inv-screen-btn">
						<i class="fa-light fa-screen-users fa-fw"></i><span class="pl-2">매체 화면</span>
					</a>
					<a class="dropdown-item" href="javascript:void(0)" id="inv-site-btn">
						<i class="fa-light fa-map-pin fa-fw"></i><span class="pl-2">사이트</span>
					</a>
					<div class="dropdown-divider"></div>
					<a class="dropdown-item" href="javascript:void(0)" id="inv-site-cond-btn">
						<i class="fa-light fa-location-crosshairs fa-fw"></i><span class="pl-2">입지 유형</span>
					</a>
				</div>
   				<button id="reorder-btn" type="button" class="btn btn-default">순서 변경</button>
    			<button id="calc-target-btn" type="button" class="btn btn-default">대상 화면수 확인</button>
    			<button id="geo-loc-btn" type="button" class="btn btn-default">
    				<span class='fa-regular fa-location-dot text-info'></span>
    				<span class="pl-1">
    					지리 위치
    				</span>
    			</button>
    		</div>
    		<div class="float-right">
    			<button id="delete-inv-target-btn" type="button" class="btn btn-danger">삭제</button>
    		</div>
    	</div>
	</kendo:grid-toolbarTemplate>
	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column width="100" template="<%= andOrTemplate %>" />
		<kendo:grid-column title="타겟 유형" field="invenType" width="100" template="<%= invenTypeTemplate %>" />
		<kendo:grid-column title="대상" field="tgtDisplay" width="500" template="<%= tgtDispTemplate %>" />
		<kendo:grid-column title="대상 수" field="tgtCount" width="100" />
		<kendo:grid-column title="화면 수" field="tgtScrCount" width="100" />
		<kendo:grid-column title="지리 위치" width="100" template="<%= geoLocTemplate %>" />
	</kendo:grid-columns>
	<kendo:grid-dataBound>
		<script>
			function grid_dataBound(e) {
				$('.toggle-btn').bootstrapToggle();
				$('.toggle-btn').change(function() {
					$.ajax({
						type: "POST",
						contentType: "application/json",
						dataType: "json",
						url: "${updateFilterTypeUrl}",
						data: JSON.stringify({ id: Number($(this).attr("rowid")), filterType: $(this).is(":checked") ? "A" : "O" }),
						success: function (form) {
        					showOperationSuccessMsg();
        					$("#grid").data("kendoGrid").dataSource.read();
						},
						error: ajaxOperationError
					});
					
			    });
				
				var rows = this.dataSource.view();
				if (rows.length > 0) {
					$(this.tbody.find("tr[data-uid='" + rows[0].uid + "'] .toggle-container")).hide();
				}
				
				if (rows.length > 0) {
					calcTargetScreenCnt();
				}
        	}
		</script>
	</kendo:grid-dataBound>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="siblingSeq" dir="asc"/>
		</kendo:dataSource-sort>
       	<kendo:dataSource-filter>
      		<kendo:dataSource-filterItem field="creative.id" operator="eq" logic="and" value="${Creative.id}" >
      		</kendo:dataSource-filterItem>
  	    </kendo:dataSource-filter>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readUrl}" dataType="json" type="POST" contentType="application/json">
				<kendo:dataSource-transport-read-data>
					<script>
						function additionalData(e) {
							return { reqIntValue1:  ${Creative.id} };
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


/* 순서 변경의 항목 보기 좋게 */
.reorder-custom {
	font-size:.9rem; font-weight: 300; margin: .2rem;
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

	// Inventory Add
	$("#inv-region-btn").click(function(e) {
		e.preventDefault();
		
		initForm11();

		$('#form-modal-11 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-11").modal();
	});

	$("#inv-state-btn").click(function(e) {
		e.preventDefault();
		
		initForm13();

		$('#form-modal-13 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-13").modal();
	});

	$("#inv-screen-btn").click(function(e) {
		e.preventDefault();
		
		initForm12();

		$('#form-modal-12 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-12").modal();
	});

	$("#inv-site-btn").click(function(e) {
		e.preventDefault();
		
		initForm14();

		$('#form-modal-14 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-14").modal();
	});

	$("#inv-site-cond-btn").click(function(e) {
		e.preventDefault();
		
		initForm15();

		$('#form-modal-15 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-15").modal();
	});
	// / Inventory Add
	
	// Delete
	$("#delete-inv-target-btn").click(function(e) {
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
							
							calcTargetScreenCnt();
						},
						error: ajaxDeleteError
					});
				}
			}, true, delRows.length);
		}
	});
	// / Delete
	
	// Reorder
	$("#reorder-btn").click(function(e) {
		e.preventDefault();
		
		initForm16();

		
		$('#form-modal-16 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-16").modal();
	});
	// / Reorder
	
	// Calc
	$("#calc-target-btn").click(function(e) {
		e.preventDefault();
			
		calcTargetScreenCnt();
	});
	// / Calc
	
	// Geo Loc
	$("#geo-loc-btn").click(function(e) {
		e.preventDefault();
			
		showGeoLoc("CREAT", ${Creative.id});
	});
	// / Geo Loc
	
});	
</script>

<!-- / Grid button actions  -->


<!--  Forms -->

<script id="template-11" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-11">
	<div class="modal-dialog">
		<form class="modal-content" id="form-11" rowid="-1" url="${saveRegionUrl}">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					시/군/구
					<span class="font-weight-light pl-1"><span name="subtitle"></span></span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
				<div class="form-group col mb-1">
					<label class="form-label">
						검색 및 선택
					</label>
					<select name="regions" class="form-control border-none"></select>
				</div>
			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button type="button" class="btn btn-primary disabled" name="save-btn" onclick='saveForm11()'>저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>


<script id="template-12" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-12">
	<div class="modal-dialog">
		<form class="modal-content" id="form-12" rowid="-1" url="${saveScreenUrl}">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					매체 화면
					<span class="font-weight-light pl-1"><span name="subtitle"></span></span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
				<div class="form-group col mb-1">
					<label class="form-label">
						검색 및 선택
					</label>
					<select name="screens" class="form-control border-none"></select>
				</div>
			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button type="button" class="btn btn-primary disabled" name="save-btn" onclick='saveForm12()'>저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>


<script id="template-13" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-13">
	<div class="modal-dialog">
		<form class="modal-content" id="form-13" rowid="-1" url="${saveStateUrl}">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					광역시/도
					<span class="font-weight-light pl-1"><span name="subtitle"></span></span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
				<div class="form-group col mb-1">
					<label class="form-label">
						검색 및 선택
					</label>
					<select name="states" class="form-control border-none"></select>
				</div>
			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button type="button" class="btn btn-primary disabled" name="save-btn" onclick='saveForm13()'>저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>


<script id="template-14" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-14">
	<div class="modal-dialog">
		<form class="modal-content" id="form-14" rowid="-1" url="${saveSiteUrl}">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					사이트
					<span class="font-weight-light pl-1"><span name="subtitle"></span></span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
				<div class="form-group col mb-1">
					<label class="form-label">
						검색 및 선택
					</label>
					<select name="sites" class="form-control border-none"></select>
				</div>
			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button type="button" class="btn btn-primary disabled" name="save-btn" onclick='saveForm14()'>저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>


<script id="template-15" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-15">
	<div class="modal-dialog">
		<form class="modal-content" id="form-15" rowid="-1" url="${saveSiteCondUrl}">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					입지 유형
					<span class="font-weight-light pl-1"><span name="subtitle"></span></span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
				<div class="form-group col mb-1">
					<label class="form-label">
						검색 및 선택
					</label>
					<select name="siteConds" class="form-control border-none"></select>
				</div>
			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button type="button" class="btn btn-primary disabled" name="save-btn" onclick='saveForm15()'>저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>


<script id="template-16" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-16">
	<div class="modal-dialog">
		<form class="modal-content" id="form-16" rowid="-1" url="">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					인벤토리 타겟팅
					<span class="font-weight-light pl-1"><span>순서 변경</span></span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">

				<div id="reorder-root-div"></div>

			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button type="button" class="btn btn-primary" onclick='saveForm16()'>저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>


<script id="template-reorder-item" type="text/x-kendo-template">

# if (invenType == "ST") { #
	<span name="data" class="badge badge-pill badge-secondary reorder-custom" data-id="#: id #">
		<span class="font-weight-bold pr-1">#: seq #</span>
		<span class='fa-regular fa-map-pin opacity-50'></span><span class='pl-1'>사이트</span>
		<span class='px-1'>-</span>
		#: tgtDisplayShort #
	</span>
# } else if (invenType == "RG") { #
	<span name="data" class="badge badge-pill badge-secondary reorder-custom" data-id="#: id #">
		<span class="font-weight-bold pr-1">#: seq #</span>
		<span class='fa-regular fa-mountain-city opacity-50'></span><span class='pl-1'>시/군/구</span>
		<span class='px-1'>-</span>
		#: tgtDisplayShort #
	</span>
# } else if (invenType == "CT") { #
	<span name="data" class="badge badge-pill badge-secondary reorder-custom" data-id="#: id #">
		<span class="font-weight-bold pr-1">#: seq #</span>
		<span class='fa-regular fa-city opacity-50'></span><span class='pl-1'>광역시/도</span>
		<span class='px-1'>-</span>
		#: tgtDisplayShort #
	</span>
# } else if (invenType == "SC") { #
	<span name="data" class="badge badge-pill badge-secondary reorder-custom" data-id="#: id #">
		<span class="font-weight-bold pr-1">#: seq #</span>
		<span class='fa-regular fa-screen-users opacity-50'></span><span class='pl-1'>매체 화면</span>
		<span class='px-1'>-</span>
		#: tgtDisplayShort #
	</span>
# } else if (invenType == "CD") { #
	<span name="data" class="badge badge-pill badge-secondary reorder-custom" data-id="#: id #">
		<span class="font-weight-bold pr-1">#: seq #</span>
		<span class='fa-regular fa-location-crosshairs opacity-50'></span><span class='pl-1'>입지 유형</span>
		<span class='px-1'>-</span>
		#: tgtDisplayShort #
	</span>
# } else { #
	<span class="badge badge-pill badge-secondary reorder-custom" data-id="#: id #">#: tgtDisplayShort #</span>
# } #

</script>

<!--  / Forms -->


<!--  Scripts -->

<script>

function calcTargetScreenCnt() {
	
	$("#inven-target-tab-title").text("인벤토리 타겟팅(계산중)");
	
	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${calcScrCountUrl}",
		data: JSON.stringify({ creativeId: ${Creative.id} }),
		success: function (data) {
			
			$("#inven-target-tab-title").text("인벤토리 타겟팅(" + data + ")");
		},
		error: ajaxOperationError
	});
}


function edit(id) {
	
	var dataItem = $("#grid").data("kendoGrid").dataSource.get(id);

	if (dataItem.invenType == "RG") {
		initForm11("타겟팅 변경");

		$("#form-11").attr("rowid", dataItem.id);

		var regions = dataItem.tgtValue.replace("[", "[\"").replace("]", "\"]").replaceAll(", ", "\", \"");
		$("#form-11 select[name='regions']").data("kendoMultiSelect").value(eval(regions));
		
		validateRegionValue($("#form-11 select[name='regions']").data("kendoMultiSelect").value());
		
		
		$('#form-modal-11 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-11").modal();
		
	} else if (dataItem.invenType == "SC") {
		initForm12("타겟팅 변경");

		$("#form-12").attr("rowid", dataItem.id);

		var screens = dataItem.tgtValue.replace("[", "[\"").replace("]", "\"]").replaceAll(", ", "\", \"");
		$("#form-12 select[name='screens']").data("kendoMultiSelect").value(eval(screens));
		
		validateScreenValue($("#form-12 select[name='screens']").data("kendoMultiSelect").value());
		
		
		$('#form-modal-12 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-12").modal();
		
	} else if (dataItem.invenType == "CT") {
		initForm13("타겟팅 변경");

		$("#form-13").attr("rowid", dataItem.id);

		var states = dataItem.tgtValue.replace("[", "[\"").replace("]", "\"]").replaceAll(", ", "\", \"");
		$("#form-13 select[name='states']").data("kendoMultiSelect").value(eval(states));
		
		validateStateValue($("#form-13 select[name='states']").data("kendoMultiSelect").value());
		
		
		$('#form-modal-13 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-13").modal();
		
	} else if (dataItem.invenType == "ST") {
		initForm14("타겟팅 변경");

		$("#form-14").attr("rowid", dataItem.id);

		var sites = dataItem.tgtValue.replace("[", "[\"").replace("]", "\"]").replaceAll(", ", "\", \"");
		$("#form-14 select[name='sites']").data("kendoMultiSelect").value(eval(sites));
		
		validateSiteValue($("#form-14 select[name='sites']").data("kendoMultiSelect").value());
		
		
		$('#form-modal-14 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-14").modal();
		
	} else if (dataItem.invenType == "CD") {
		initForm15("타겟팅 변경");

		$("#form-15").attr("rowid", dataItem.id);

		var siteConds = dataItem.tgtValue.replace("[", "[\"").replace("]", "\"]").replaceAll(", ", "\", \"");
		$("#form-15 select[name='siteConds']").data("kendoMultiSelect").value(eval(siteConds));
		
		validateSiteValue($("#form-15 select[name='siteConds']").data("kendoMultiSelect").value());
		
		
		$('#form-modal-15 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-15").modal();
		
	}
}


function validateRegionValue(values) {

	if (values.length == 0) {
		$("#form-11 button[name='save-btn']").addClass("disabled");
	} else {
		$("#form-11 button[name='save-btn']").removeClass("disabled");
	}
}


function validateScreenValue(values) {

	if (values.length == 0) {
		$("#form-12 button[name='save-btn']").addClass("disabled");
	} else {
		$("#form-12 button[name='save-btn']").removeClass("disabled");
	}
}


function validateStateValue(values) {

	if (values.length == 0) {
		$("#form-13 button[name='save-btn']").addClass("disabled");
	} else {
		$("#form-13 button[name='save-btn']").removeClass("disabled");
	}
}


function validateSiteValue(values) {

	if (values.length == 0) {
		$("#form-14 button[name='save-btn']").addClass("disabled");
	} else {
		$("#form-14 button[name='save-btn']").removeClass("disabled");
	}
}


function validateSiteCondValue(values) {

	if (values.length == 0) {
		$("#form-15 button[name='save-btn']").addClass("disabled");
	} else {
		$("#form-15 button[name='save-btn']").removeClass("disabled");
	}
}


function initForm11(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-11").html()));
	
    $("#form-11 select[name='regions']").kendoMultiSelect({
        dataTextField: "text",
        dataValueField: "value",
        tagTemplate: "<span class='fa-regular fa-mountain-city text-gray'></span>" + 
        			 "<span class='pl-2'>#:data.text#</span>",
        itemTemplate: "<span class='fa-regular fa-mountain-city text-gray'></span>" +
        		      "<span class='pl-2'>#:data.text#</span>",
        dataSource: {
		    //serverFiltering: true,
            transport: {
                read: {
                    dataType: "json",
                    url: "${readRegionUrl}",
                    type: "POST",
                    contentType: "application/json",
					data: JSON.stringify({}),
                },
                parameterMap: function (options) {
            		return JSON.stringify(options);	
                }
            },
			error: kendoReadError
        },
        change: function(e) {
        	validateRegionValue(this.value());
        },
        height: 400,
        filter: "contains",
        noDataTemplate: "표시할 자료가 없습니다.",
    });
	
	
	$("#form-11 span[name='subtitle']").text(subtitle ? subtitle : "타겟팅 추가");
}


function saveForm11() {

	var regionCodes = $("#form-11 select[name='regions']").data("kendoMultiSelect").value();
	
	if (regionCodes.length > 0) {
		
		var dataItems = $("#form-11 select[name='regions']").data("kendoMultiSelect").dataItems();
		var valueTexts = [];
		
		for(var i = 0; i < dataItems.length; i += 1) {
			valueTexts.push(dataItems[i].text);
		}
		
		var data = {
			id: Number($("#form-11").attr("rowid")),
			creative: ${Creative.id},
			regionCodes: regionCodes,
			regionTexts: valueTexts,
		};
        	
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: $("#form-11").attr("url"),
			data: JSON.stringify(data),
			success: function (data, status, xhr) {
				showSaveSuccessMsg();
				$("#form-modal-11").modal("hide");
				$("#grid").data("kendoGrid").dataSource.read();
			},
			error: ajaxSaveError
		});
	}
}


function initForm12(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-12").html()));
	
    $("#form-12 select[name='screens']").kendoMultiSelect({
        dataTextField: "text",
        dataValueField: "value",
        tagTemplate: "<span class='fa-regular fa-screen-users text-gray'></span>" + 
        			 "<span class='pl-2'>#:data.text#</span>",
        itemTemplate: "<span class='fa-regular fa-screen-users text-gray'></span>" +
        		      "<span class='pl-2'>#:data.text#</span>",
        dataSource: {
		    //serverFiltering: true,
            transport: {
                read: {
                    dataType: "json",
                    url: "${readScreenUrl}",
                    type: "POST",
                    contentType: "application/json",
					data: JSON.stringify({}),
                },
                parameterMap: function (options) {
            		return JSON.stringify(options);	
                }
            },
			error: kendoReadError
        },
        change: function(e) {
        	validateScreenValue(this.value());
        },
        height: 400,
        filter: "contains",
        noDataTemplate: "표시할 자료가 없습니다.",
    });
	
	
	$("#form-12 span[name='subtitle']").text(subtitle ? subtitle : "타겟팅 추가");
}


function saveForm12() {

	var screenIds = $("#form-12 select[name='screens']").data("kendoMultiSelect").value();
	
	if (screenIds.length > 0) {
		
		var dataItems = $("#form-12 select[name='screens']").data("kendoMultiSelect").dataItems();
		var valueTexts = [];
		
		for(var i = 0; i < dataItems.length; i += 1) {
			valueTexts.push(dataItems[i].text);
		}
		
		var data = {
			id: Number($("#form-12").attr("rowid")),
			creative: ${Creative.id},
			screenIds: screenIds,
			screenTexts: valueTexts,
		};

		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: $("#form-12").attr("url"),
			data: JSON.stringify(data),
			success: function (data, status, xhr) {
				showSaveSuccessMsg();
				$("#form-modal-12").modal("hide");
				$("#grid").data("kendoGrid").dataSource.read();
			},
			error: ajaxSaveError
		});
	}
}


function initForm13(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-13").html()));
	
    $("#form-13 select[name='states']").kendoMultiSelect({
        dataTextField: "text",
        dataValueField: "value",
        tagTemplate: "<span class='fa-regular fa-city text-gray'></span>" + 
        			 "<span class='pl-2'>#:data.text#</span>",
        itemTemplate: "<span class='fa-regular fa-city text-gray'></span>" +
        		      "<span class='pl-2'>#:data.text#</span>",
        dataSource: {
		    //serverFiltering: true,
            transport: {
                read: {
                    dataType: "json",
                    url: "${readStateUrl}",
                    type: "POST",
                    contentType: "application/json",
					data: JSON.stringify({}),
                },
                parameterMap: function (options) {
            		return JSON.stringify(options);	
                }
            },
			error: kendoReadError
        },
        change: function(e) {
        	validateStateValue(this.value());
        },
        height: 400,
        filter: "contains",
        noDataTemplate: "표시할 자료가 없습니다.",
    });
	
	
	$("#form-13 span[name='subtitle']").text(subtitle ? subtitle : "타겟팅 추가");
}


function saveForm13() {

	var stateCodes = $("#form-13 select[name='states']").data("kendoMultiSelect").value();
	
	if (stateCodes.length > 0) {
		
		var dataItems = $("#form-13 select[name='states']").data("kendoMultiSelect").dataItems();
		var valueTexts = [];
		
		for(var i = 0; i < dataItems.length; i += 1) {
			valueTexts.push(dataItems[i].text);
		}
		
		var data = {
			id: Number($("#form-13").attr("rowid")),
			creative: ${Creative.id},
			stateCodes: stateCodes,
			stateTexts: valueTexts,
		};
        	
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: $("#form-13").attr("url"),
			data: JSON.stringify(data),
			success: function (data, status, xhr) {
				showSaveSuccessMsg();
				$("#form-modal-13").modal("hide");
				$("#grid").data("kendoGrid").dataSource.read();
			},
			error: ajaxSaveError
		});
	}
}


function initForm14(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-14").html()));
	
    $("#form-14 select[name='sites']").kendoMultiSelect({
        dataTextField: "text",
        dataValueField: "value",
        tagTemplate: "<span class='fa-regular fa-map-pin text-gray'></span>" + 
        			 "<span class='pl-2'>#:data.text#</span>",
        itemTemplate: "<span class='fa-regular fa-map-pin text-gray'></span>" +
        		      "<span class='pl-2'>#:data.text#</span>",
        dataSource: {
		    //serverFiltering: true,
            transport: {
                read: {
                    dataType: "json",
                    url: "${readSiteUrl}",
                    type: "POST",
                    contentType: "application/json",
					data: JSON.stringify({}),
                },
                parameterMap: function (options) {
            		return JSON.stringify(options);	
                }
            },
			error: kendoReadError
        },
        change: function(e) {
        	validateSiteValue(this.value());
        },
        height: 400,
        filter: "contains",
        noDataTemplate: "표시할 자료가 없습니다.",
    });
	
	
	$("#form-14 span[name='subtitle']").text(subtitle ? subtitle : "타겟팅 추가");
}


function saveForm14() {

	var siteIds = $("#form-14 select[name='sites']").data("kendoMultiSelect").value();
	
	if (siteIds.length > 0) {
		
		var dataItems = $("#form-14 select[name='sites']").data("kendoMultiSelect").dataItems();
		var valueTexts = [];
		
		for(var i = 0; i < dataItems.length; i += 1) {
			valueTexts.push(dataItems[i].text);
		}
		
		var data = {
			id: Number($("#form-14").attr("rowid")),
			creative: ${Creative.id},
			siteIds: siteIds,
			siteTexts: valueTexts,
		};

		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: $("#form-14").attr("url"),
			data: JSON.stringify(data),
			success: function (data, status, xhr) {
				showSaveSuccessMsg();
				$("#form-modal-14").modal("hide");
				$("#grid").data("kendoGrid").dataSource.read();
			},
			error: ajaxSaveError
		});
	}
}


function initForm15(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-15").html()));
	
    $("#form-15 select[name='siteConds']").kendoMultiSelect({
        dataTextField: "text",
        dataValueField: "value",
        tagTemplate: "<span class='fa-regular fa-location-crosshairs text-gray'></span>" + 
        			 "<span class='pl-2'>#:data.text#</span>",
        itemTemplate: "<span class='fa-regular fa-location-crosshairs text-gray'></span>" +
        		      "<span class='pl-2'>#:data.text#</span>",
        dataSource: {
		    //serverFiltering: true,
            transport: {
                read: {
                    dataType: "json",
                    url: "${readSiteCondUrl}",
                    type: "POST",
                    contentType: "application/json",
					data: JSON.stringify({}),
                },
                parameterMap: function (options) {
            		return JSON.stringify(options);	
                }
            },
			error: kendoReadError
        },
        change: function(e) {
        	validateSiteCondValue(this.value());
        },
        height: 400,
        filter: "contains",
        noDataTemplate: "표시할 자료가 없습니다.",
    });
	
	
	$("#form-15 span[name='subtitle']").text(subtitle ? subtitle : "타겟팅 추가");
}


function saveForm15() {

	var siteCondCodes = $("#form-15 select[name='siteConds']").data("kendoMultiSelect").value();
	
	if (siteCondCodes.length > 0) {
		
		var dataItems = $("#form-15 select[name='siteConds']").data("kendoMultiSelect").dataItems();
		var valueTexts = [];
		
		for(var i = 0; i < dataItems.length; i += 1) {
			valueTexts.push(dataItems[i].text);
		}
		
		var data = {
			id: Number($("#form-15").attr("rowid")),
			creative: ${Creative.id},
			siteCondCodes: siteCondCodes,
			siteCondTexts: valueTexts,
		};

		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: $("#form-15").attr("url"),
			data: JSON.stringify(data),
			success: function (data, status, xhr) {
				showSaveSuccessMsg();
				$("#form-modal-15").modal("hide");
				$("#grid").data("kendoGrid").dataSource.read();
			},
			error: ajaxSaveError
		});
	}
}


function initForm16() {
	
	$("#formRoot").html(kendo.template($("#template-16").html()));
	
	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${readOrderedUrl}",
		data: JSON.stringify({ creativeId: ${Creative.id} }),
		success: function (data) {
			
	    	var itemTemplate = kendo.template($("#template-reorder-item").html());
	    	
	    	for(var i = 0; i < data.length; i++) {
	        	$("#reorder-root-div").append(itemTemplate(data[i]));
	    	}
	    	Sortable.create(document.getElementById("reorder-root-div"), { animation: 150 });
		},
		error: ajaxReadError
	});
}


function saveForm16() {

	var items = "";
	$("#reorder-root-div span[name='data']").each(function(i, obj) {
		if (items) {
			items = items + "|";
		}
		items = items + $(obj).attr("data-id");
	})
	
	if (items) {
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${reorderUrl}",
			data: JSON.stringify({ items: items }),
			success: function (form) {
				showSaveSuccessMsg();
				$("#form-modal-16").modal("hide");
				$("#grid").data("kendoGrid").dataSource.read();
			},
			error: ajaxSaveError
		});
	} else {
		$("#form-modal-16").modal("hide");
		$("#grid").data("kendoGrid").dataSource.read();
	}
}

</script>

<!--  / Scripts -->




</c:otherwise>
</c:choose>

<!--  / Page details -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmValidate />
<func:geoLocModal />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
