<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/org/playlist/create" var="createUrl" />
<c:url value="/org/playlist/update" var="updateUrl" />
<c:url value="/org/playlist/read" var="readUrl" />
<c:url value="/org/playlist/destroy" var="destroyUrl" />

<c:url value="/org/playlist/readAds" var="readAdUrl" />
<c:url value="/org/playlist/readOrdered" var="readOrderedUrl" />
<c:url value="/org/playlist/reorder" var="reorderUrl" />

<c:url value="/org/playlist/readPlTargets" var="readPlTargetUrl" />
<c:url value="/org/playlist/updateFilterType" var="updateFilterTypeUrl" />
<c:url value="/org/playlist/readOrderedTargets" var="readOrderedTargetUrl" />
<c:url value="/org/playlist/reorderTargets" var="reorderTargetUrl" />
<c:url value="/org/playlist/calcScrCount" var="calcScrCountUrl" />
<c:url value="/org/playlist/destroyTargets" var="destroyTargetUrl" />

<c:url value="/org/playlist/readACRegion" var="readRegionUrl" />
<c:url value="/org/playlist/readACScreen" var="readScreenUrl" />
<c:url value="/org/playlist/readACState" var="readStateUrl" />
<c:url value="/org/playlist/readACSite" var="readSiteUrl" />

<c:url value="/org/playlist/saveRegion" var="saveRegionUrl" />
<c:url value="/org/playlist/saveScreen" var="saveScreenUrl" />
<c:url value="/org/playlist/saveState" var="saveStateUrl" />
<c:url value="/org/playlist/saveSite" var="saveSiteUrl" />


<!-- Opening tags -->

<common:mediumPageOpeningNaverMap />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}
</h4>

<hr class="border-light container-m--x mt-0 mb-4">





<!-- Page body -->


<!-- Page scripts  -->


<link href="/resources/vendor/lib/bootstrap-toggle/bootstrap-toggle.min.css" rel="stylesheet">
<link rel="stylesheet" href="/resources/vendor/lib/dragula/dragula.css">

<script src="/resources/vendor/lib/bootstrap-toggle/bootstrap-toggle.min.js"></script>
<script src="/resources/vendor/lib/dragula/dragula.js"></script>
<script src="/resources/vendor/lib/sortablejs/sortable.js"></script>


<!-- Java(optional)  -->

<%
	String nameTemplate =
			"<span class='pr-1'>#= name #</span>" +
			"<button type='button' onclick='edit(#= id #)' class='btn icon-btn btn-sm btn-outline-success borderless'>" + 
			"<span class='fas fa-pencil-alt'></span></button>";

	String noRecordsTemplate =
			"<div class='container text-center my-4'>" +
				"<div class='d-flex justify-content-center align-self-center'>" +
					"<span class='fa-thin fa-hexagon-exclamation fa-3x'></span>" +
					"<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>해당 자료 없음</span>" +
				"</div>" +
			"</div>";
			
	String geoLocTemplate =
			"<a href='javascript:showGeoLoc(\"PL\", #= id #)' class='btn btn-default btn-xs icon-btn'>" +
				"<span class='fa-regular fa-location-dot text-info'></span>" +
			"</a>";
			
			
	java.util.HashMap<String, Object> data = new java.util.HashMap<String, Object>();
	data.put("reqIntValue1", "#=id#");
			
	String startDateTemplate = net.doohad.utils.Util.getSmartDate("startDate", false, false);
	String endDateTemplate = net.doohad.utils.Util.getSmartDate("endDate", false, false);
	String regDateTemplate = net.doohad.utils.Util.getSmartDate("whoCreationDate", false);
	String lastUpdateDateTemplate = net.doohad.utils.Util.getSmartDate("whoLastUpdateDate", false);
%>


<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid" pageable="true" filterable="false" sortable="false" scrollable="true" reorderable="true" resizable="true" 
		selectable="${value_gridSelectable}" detailTemplate="template" >
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" previousNext="false" numeric="false" pageSize="10000" info="false" />
	<kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button id="add-btn" type="button" class="btn btn-outline-success">추가</button>
   				<button id="reorder-btn" type="button" class="btn btn-default">순서 변경</button>
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
		<kendo:grid-column title="목록명" field="name" width="200" template="<%= nameTemplate %>" />
		<kendo:grid-column title="광고수" field="adCount" width="100" />
		<kendo:grid-column title="시작일" field="startDate" width="120" template="<%= startDateTemplate %>" />
		<kendo:grid-column title="종료일" field="endDate" width="120" template="<%= endDateTemplate %>" />
		<kendo:grid-column title="유효광고수" field="validAdCount" width="150" filterable="false" sortable="false" />
		<kendo:grid-column title="대상화면수" field="screenCount" width="150" filterable="false" sortable="false" />
		<kendo:grid-column title="지리 위치" width="100" template="<%= geoLocTemplate %>" />
		<kendo:grid-column title="최근 변경" field="whoLastUpdateDate" width="120" template="<%= lastUpdateDateTemplate %>" />
		<kendo:grid-column title="등록" field="whoCreationDate" width="120" template="<%= regDateTemplate %>" />
	</kendo:grid-columns>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="siblingSeq" dir="asc"/>
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
					<kendo:dataSource-schema-model-field name="adCount" type="number" />
					<kendo:dataSource-schema-model-field name="startDate" type="date" />
					<kendo:dataSource-schema-model-field name="endDate" type="date" />
					<kendo:dataSource-schema-model-field name="whoCreationDate" type="date" />
					<kendo:dataSource-schema-model-field name="whoLastUpdateDate" type="date" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
<kendo:grid-detailTemplate id="template">
	<div class="k-grid-toolbar solid-border-color" style="border-bottom: transparent;">
    	<div class="float-left">
    		<div class="btn-group">
				<button type="button" class="btn btn-sm btn-outline-success dropdown-toggle" data-display="static"
						data-toggle="dropdown">타겟팅 추가</button>
				<div class="dropdown-menu">
					<a class="dropdown-item" href="javascript:addTgtState(#=id#)">
						<i class="fa-light fa-city fa-fw"></i><span class="pl-2">광역시/도</span>
					</a>
					<a class="dropdown-item" href="javascript:addTgtRegion(#=id#)">
						<i class="fa-light fa-mountain-city fa-fw"></i><span class="pl-2">시/군/구</span>
					</a>
					<div class="dropdown-divider"></div>
					<a class="dropdown-item" href="javascript:addTgtScreen(#=id#)">
						<i class="fa-light fa-screen-users fa-fw"></i><span class="pl-2">매체 화면</span>
					</a>
					<a class="dropdown-item" href="javascript:addTgtSite(#=id#)">
						<i class="fa-light fa-map-pin fa-fw"></i><span class="pl-2">사이트</span>
					</a>
				</div>
    		</div>
  			<a href="javascript:reorderTargets(#=id#)" class="btn btn-sm btn-default">순서 변경</a>
  			<a href="javascript:calcTargetScreenCnt(#=id#)" class="btn btn-sm btn-default"><span id="calc-scr-count-span-#=id#">대상 화면수 확인</span></a>
   		</div>
   		<div class="float-right">
   			<a href="javascript:deleteTargets(#=id#)" class="btn btn-sm btn-danger text-white">삭제</a>
   		</div>
	</div>
	<kendo:grid name="grid-#=id#" pageable="false" filterable="false" sortable="false" scrollable="true" resizable="true" selectable="multiple" >
		<kendo:grid-pageable refresh="true" previousNext="false" numeric="false" pageSize="10000" info="false" />
	   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
		<kendo:grid-columns>
			<kendo:grid-column width="100" template="<span class='detailAndOr'></span>" />
			<kendo:grid-column title="타겟 유형" field="invenType" width="100" template="<span class='detailInvenType'></span>" />
			<kendo:grid-column title="대상" field="tgtDisplay" width="500" template="<span class='detailDisplay'></span>" />
			<kendo:grid-column title="대상 수" field="tgtCount" width="100" template="<span class='detailCount'></span>" />
			<kendo:grid-column title="화면 수" field="tgtScrCount" width="100" template="<span class='detailScrCount'></span>" />
			<kendo:grid-column title="지리 위치" width="100" template="<span class='detailGeoLoc'></span>" />
		</kendo:grid-columns>
		<kendo:grid-dataBound>
			<script>
				function grid_dataBound(e) {
					var rows = this.dataSource.view();
	    			
					for (var i = 0; i < rows.length; i ++) {
						if (rows[i].invenType == 'RG') {
							$(this.tbody.find("tr[data-uid='" + rows[i].uid + "'] .detailInvenType"))
									.html("<span class='fa-regular fa-mountain-city fa-fw'></span><span class='pl-1'>시/군/구</span>");
						} else if (rows[i].invenType == 'CT') {
							$(this.tbody.find("tr[data-uid='" + rows[i].uid + "'] .detailInvenType"))
									.html("<span class='fa-regular fa-city fa-fw'></span><span class='pl-1'>광역시/도</span>");
						} else if (rows[i].invenType == 'SC') {
							$(this.tbody.find("tr[data-uid='" + rows[i].uid + "'] .detailInvenType"))
									.html("<span class='fa-regular fa-screen-users fa-fw'></span><span class='pl-1'>매체 화면</span>");
						} else if (rows[i].invenType == 'ST') {
							$(this.tbody.find("tr[data-uid='" + rows[i].uid + "'] .detailInvenType"))
									.html("<span class='fa-regular fa-map-pin fa-fw'></span><span class='pl-1'>사이트</span>");
						} else {
							$(this.tbody.find("tr[data-uid='" + rows[i].uid + "'] .detailInvenType"))
									.text("-");
						}
						
						$(this.tbody.find("tr[data-uid='" + rows[i].uid + "'] .detailCount")).text(rows[i].tgtCount);
						$(this.tbody.find("tr[data-uid='" + rows[i].uid + "'] .detailScrCount")).text(rows[i].tgtScrCount);
						$(this.tbody.find("tr[data-uid='" + rows[i].uid + "'] .detailGeoLoc")).html(
								"<a href='javascript:showGeoLoc(\"PLINVEN\", " + rows[i].id + ")' class='btn btn-default btn-xs icon-btn'><span class='fa-regular fa-location-dot text-info'></span></a>");
						
						
						var tgtDisp = "<span class='pr-1'>" + rows[i].tgtDisplay + "</span>" +
								"<button type='button' onclick='editTarget(" + rows[i].plId + "," + rows[i].id + ")' class='btn icon-btn btn-sm btn-outline-success borderless'>" +
								"<span class='fas fa-pencil-alt'></span></button>";
						
						$(this.tbody.find("tr[data-uid='" + rows[i].uid + "'] .detailDisplay")).html(tgtDisp);
						
						
						var andOr = "<div class='toggle-container'>" +
									"<input class='toggle-btn-#= id #' type='checkbox' data-toggle='toggle' data-on='And' data-off='Or' " +
											"data-onstyle='info' data-offstyle='success' rowid='" + rows[i].id + "' plid='" + rows[i].plId + "' ";
						if (rows[i].filterType == 'A') {
							andOr += "checked";
						}
						andOr += "></div>";

						$(this.tbody.find("tr[data-uid='" + rows[i].uid + "'] .detailAndOr")).html(andOr);
					}
					
					$('.toggle-btn-#= id #').bootstrapToggle();
					$('.toggle-btn-#= id #').change(function() {
						updateFilterType(Number($(this).attr("plid")), Number($(this).attr("rowid")), $(this).is(":checked") ? "A" : "O");
				    });
					
					if (rows.length > 0) {
						$(this.tbody.find("tr[data-uid='" + rows[0].uid + "'] .toggle-container")).hide();
					}
	        	}
			</script>
		</kendo:grid-dataBound>
		<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
			<kendo:dataSource-transport>
				<kendo:dataSource-transport-read url="${readPlTargetUrl}" data="<%= data %>" 
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

<style>

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

<!-- / Kendo grid  -->


<!-- Grid button actions  -->

<script>
$(document).ready(function() {

	// Add
	$("#add-btn").click(function(e) {
		e.preventDefault();
		
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${readAdUrl}",
			data: JSON.stringify({ }),
			success: function (data, status) {
				
				initForm1();
			
				var template = kendo.template($("#itemTemplate").html());
				
				for(var i in data) {
					$('#dragula-left').append(template(data[i]));
				}
				
				displaySelCount();
				
				// '유효 광고 복사'를 위한
				adData = data;
				

				$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
				$("#form-modal-1").modal();
				
			},
			error: ajaxReadError
		});
	});
	// / Add
	
	// Reorder
	$("#reorder-btn").click(function(e) {
		e.preventDefault();
		
		initForm2();

		
		$('#form-modal-2 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-2").modal();
	});
	// / Reorder
	
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
							재생 목록명
							<span class="text-danger">*</span>
						</label>
						<input name="name" type="text" maxlength="100" class="form-control required">
					</div>
					<div class="form-group col-3">
						<label class="form-label">
							시작일
						</label>
						<input name="startDate" type="text" class="form-control">
					</div>
					<div class="form-group col-3">
						<label class="form-label">
							종료일
						</label>
						<input name="endDate" type="text" class="form-control">
					</div>
				</div>
				
				
				<div class="form-row">
					<div class="card col px-0 mx-1">
						<h6 class="card-header with-elements bg-secondary text-white py-1">
							<div class="card-header-title">가능한 광고</div>
					<div class="card-header-elements ml-auto small">
						<button type="button" class="btn btn-sm btn-outline-default" onClick="copyToRight()">
							<span class="fas fa-chevron-double-right text-white"></span>
						</button>
					</div>
						</h6>
						<div id="dragula-left" class="card-body px-2 pb-2 pt-3 pl-container">
						</div>
					</div>
					
					<div class="card col px-0 mx-1">
						<h6 class="card-header with-elements bg-success text-white py-1">
							<div class="card-header-title">재생 목록</div>
							<div class="card-header-elements ml-auto small opacity-75">
								<span id="sel-pl-count">0</span>
								<span>항목</span>
							</div>
						</h6>
						<div id="dragula-right" class="card-body px-2 pb-2 pt-3 pl-container">
						</div>
					</div>
				</div>

			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button name="save-btn" type="button" class="btn btn-primary" onclick='saveForm1()'>저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>


<script id="itemTemplate" type="text/x-kendo-template">

<div class="pl-item card card-condenced pc-opt" ukid="#: id #">
	<div class="card-body media align-items-center">
		<span class="d-none d-sm-block fa-light #: icon # fa-fw fa-2x mr-3"></span>
		<div class="media-body">
			<span class="text-dark font-weight-semibold mb-2">
				#: creatName #
				# if (valid) { #
					<span class="pl-1"></span>
					<span class="badge badge-dot badge-success indicator"></span>
				# } #
			</span>
			<div class="text-muted small text-ellipsis">
				<span class="fa-regular fa-calendar-range"></span>
				#: period #
				<span class="pr-2"></span>
				<span class="fa-regular fa-audio-description"></span>
				#: adName #
			</div>
		</div>
		<div><span class="fas fa-arrows-alt fa-lg text-secondary mobile-opt"></span></div>
	</div>
</div>

</script>


<script id="template-2" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-2">
	<div class="modal-dialog">
		<form class="modal-content" id="form-1" rowid="-1" url="">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					재생 목록
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
				<button type="button" class="btn btn-primary" onclick='saveForm2()'>저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>


<script id="template-reorder-item" type="text/x-kendo-template">

<span name="data" class="badge badge-pill badge-secondary reorder-custom" data-id="#: id #">
	<span class="font-weight-bold pr-1">#: seq #</span>
	<span class='fa-regular fa-list-ol opacity-50'></span>
	#: name #
</span>

</script>


<script id="template-9" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-9">
	<div class="modal-dialog">
		<form class="modal-content" id="form-1" rowid="-1" url="">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					재생 목록 타겟팅
					<span class="font-weight-light pl-1"><span>순서 변경</span></span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">

				<div id="reorder-target-div"></div>

			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button type="button" class="btn btn-primary" onclick='saveForm9()'>저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>


<script id="template-reorder-target-item" type="text/x-kendo-template">

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
# } else { #
	<span class="badge badge-pill badge-secondary reorder-custom" data-id="#: id #">#: tgtDisplayShort #</span>
# } #

</script>


<script id="template-11" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-11">
	<div class="modal-dialog">
		<form class="modal-content" id="form-11" rowid="-1" url="${saveStateUrl}">
      
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
				<button type="button" class="btn btn-primary disabled" name="save-btn" onclick='saveForm11()'>저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>


<script id="template-12" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-12">
	<div class="modal-dialog">
		<form class="modal-content" id="form-12" rowid="-1" url="${saveRegionUrl}">
      
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
				<button type="button" class="btn btn-primary disabled" name="save-btn" onclick='saveForm12()'>저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>


<script id="template-13" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-13">
	<div class="modal-dialog">
		<form class="modal-content" id="form-13" rowid="-1" url="${saveScreenUrl}">
      
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


<style>

/* 메뉴들을 포함하는 컨테이너 박스가 많은 메뉴일 경우 스크롤 되도록 */
.pl-container {
	overflow-y: auto; height: 500px;
}


/* 메뉴 항목과 항목간 간격 조정 */
.pl-item {
	margin-bottom: 0.4rem;
}


/* 메뉴 오버 마우스 모습이 포인트에서 이동십자 포인트로 표시(PC 환경) */
.pc-opt {

<c:if test="${not isMobileMode}">
	cursor: move;
</c:if>

}


/* 그리드 자료 새로고침 버튼을 우측 정렬  */
div.k-pager-wrap.k-grid-pager.k-widget.k-floatwrap {
	display: flex!important;
	justify-content: flex-end!important;
}


/* 끌기 가능한 아이콘 버튼 표시(모바일 환경) */
.mobile-opt {

<c:if test="${not isMobileMode}">
	display: none;
</c:if>

}


/* 순서 변경의 항목 보기 좋게 */
.reorder-custom {
	font-size:.9rem; font-weight: 300; margin: .2rem;
}


/* 그리드 상단 추가 영역을 그리드와 유사하게 */
.solid-border-color {
	border: solid 1px #e4e4e4;
	background-color: white;
}

</style>

<!--  / Forms -->


<!--  Scripts -->

<script>

var adData = [];


function initForm1(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-1").html()));
	
	$("#form-1 input[name='startDate']").kendoDatePicker({
		format: "yyyy-MM-dd",
		parseFormats: [
			"yyyy-MM-dd",
		],
		value: new Date(),
	});
	
	$("#form-1 input[name='endDate']").kendoDatePicker({
		format: "yyyy-MM-dd",
		parseFormats: [
			"yyyy-MM-dd",
		],
	});

	$("#form-1 span[name='subtitle']").text(subtitle ? subtitle : "추가");
	

	$("#form-1").validate({
		rules: {
			name: {
				minlength: 2,
			},
			startDate: { date: true },
			endDate: { date: true },
		}
	});

	dragula([$('#dragula-left')[0], $('#dragula-right')[0]], {
		revertOnSpill: true,
		copy: function (el, source) {
			return source === $('#dragula-left')[0];
		},
		accepts: function (el, target) {
			return target !== $('#dragula-left')[0];
		},		
		removeOnSpill: function (el, source) {
			return source === $('#dragula-right')[0];
		},
		moves: function (el, container, handle) {

<c:if test="${isMobileMode}">
			var iconHandler = handle;
			if (handle.tagName == "path") {
				iconHandler = handle.parentElement;
			}

			return iconHandler.classList.contains('fa-arrows-alt');
</c:if>

<c:if test="${not isMobileMode}">
			return true;
</c:if>

		},
	}).on('dragend', function (el) {
		displaySelCount();
	});
}


function saveForm1() {

	// kendo datepicker validation
	validateKendoDateValue($("#form-1 input[name='startDate']"));
	validateKendoDateValue($("#form-1 input[name='endDate']"));
	
	var ids = "";
	$("#dragula-right .pl-item").each(function(index) {
		if (ids) {
			ids = ids + "|" + $(this).attr("ukid");
		} else {
			ids = $(this).attr("ukid");
		}
	});

	if (ids && $("#form-1").valid()) {
    	var data = {
    		id: Number($("#form-1").attr("rowid")),
    		name: $.trim($("#form-1 input[name='name']").val()),
    		startDate: $("#form-1 input[name='startDate']").data("kendoDatePicker").value(),
    		endDate: $("#form-1 input[name='endDate']").data("kendoDatePicker").value(),
    		ids: ids,
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
	
	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${readAdUrl}",
		data: JSON.stringify({ }),
		success: function (data, status) {
			
			initForm1("변경");
		
			var template = kendo.template($("#itemTemplate").html());
			
			for(var i in data) {
				$('#dragula-left').append(template(data[i]));
			}
			
			// '유효 광고 복사'를 위한
			adData = data;
			

			var dataItem = $("#grid").data("kendoGrid").dataSource.get(id);
			
			$("#form-1").attr("rowid", dataItem.id);
			$("#form-1").attr("url", "${updateUrl}");
			
			$("#form-1 input[name='name']").val(dataItem.name);
			
			$("#form-1 input[name='startDate']").data("kendoDatePicker").value(dataItem.startDate);
			$("#form-1 input[name='endDate']").data("kendoDatePicker").value(dataItem.endDate);


			var value = dataItem.adValue.split("|");
			for(var i = 0; i < value.length; i ++) {
				if (value[i]) {
					for(var j = 0; j < data.length; j ++) {
						if (value[i] == data[j].id) {
							$('#dragula-right').append(template(data[j]));
							break;
						}
					}
				}
			}
			
			displaySelCount();

			
			$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
			$("#form-modal-1").modal();
			
		},
		error: ajaxReadError
	});
}


function displaySelCount() {
	
	var length = $("#dragula-right .pl-item").length;
	$("#sel-pl-count").text(length);
	
	if (length > 0) {
		$("#form-1 button[name='save-btn']").removeClass("disabled");
	} else {
		$("#form-1 button[name='save-btn']").addClass("disabled");
	}
}


function copyToRight() {
	
	var template = kendo.template($("#itemTemplate").html());
	
	for(var i in adData) {
		if (adData[i].valid) {
			$('#dragula-right').append(template(adData[i]));
		}
	}
	
	displaySelCount();
}


function initForm2() {
	
	$("#formRoot").html(kendo.template($("#template-2").html()));
	
	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${readOrderedUrl}",
		data: JSON.stringify({ }),
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


function saveForm2() {

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
				$("#form-modal-2").modal("hide");
				$("#grid").data("kendoGrid").dataSource.read();
			},
			error: ajaxSaveError
		});
	} else {
		$("#form-modal-2").modal("hide");
		$("#grid").data("kendoGrid").dataSource.read();
	}
}

</script>


<script>

var currPlaylistId = 0;


function reorderTargets(plId) {

	initForm9(plId);

	
	$('#form-modal-9 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-9").modal();
}


function calcTargetScreenCnt(plId) {
	
	if (!plId) {
		plId = currPlaylistId;
	}
	
	$("#calc-scr-count-span-" + plId).text("대상 화면수 확인(계산중)");
	
	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${calcScrCountUrl}",
		data: JSON.stringify({ playlistId: plId }),
		success: function (data) {
			
			$("#calc-scr-count-span-" + plId).text("대상 화면수 확인(" + data + ")");
		},
		error: ajaxOperationError
	});
}


function deleteTargets(plId) {

	currPlaylistId = plId;
	
	var grid = $("#grid-" + plId).data("kendoGrid");
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
					url: "${destroyTargetUrl}",
					data: JSON.stringify({ items: delRows }),
					success: function (form) {
    					showDeleteSuccessMsg();
						$("#grid-" + plId).data("kendoGrid").dataSource.read();
						
						calcTargetScreenCnt(currPlaylistId);
					},
					error: ajaxDeleteError
				});
			}
		}, true, delRows.length);
	}
}


function addTgtState(plId) {

	currPlaylistId = plId;
	
	initForm11();

	$('#form-modal-11 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-11").modal();
}


function addTgtRegion(plId) {

	currPlaylistId = plId;
	
	initForm12();

	$('#form-modal-12 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-12").modal();
}


function addTgtScreen(plId) {

	currPlaylistId = plId;
	
	initForm13();

	$('#form-modal-13 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-13").modal();
}


function addTgtSite(plId) {

	currPlaylistId = plId;
	
	initForm14();

	$('#form-modal-14 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-14").modal();
}


function editTarget(plId, id) {
	
	currPlaylistId = plId;
	
	var dataItem = $("#grid-" + plId).data("kendoGrid").dataSource.get(id);

	if (dataItem.invenType == "RG") {
		initForm12("타겟팅 변경");

		$("#form-12").attr("rowid", dataItem.id);

		var regions = dataItem.tgtValue.replace("[", "[\"").replace("]", "\"]").replaceAll(", ", "\", \"");
		$("#form-12 select[name='regions']").data("kendoMultiSelect").value(eval(regions));
		
		validateRegionValue($("#form-12 select[name='regions']").data("kendoMultiSelect").value());
		
		
		$('#form-modal-12 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-12").modal();
		
	} else if (dataItem.invenType == "SC") {
		initForm13("타겟팅 변경");

		$("#form-13").attr("rowid", dataItem.id);

		var screens = dataItem.tgtValue.replace("[", "[\"").replace("]", "\"]").replaceAll(", ", "\", \"");
		$("#form-13 select[name='screens']").data("kendoMultiSelect").value(eval(screens));
		
		validateScreenValue($("#form-13 select[name='screens']").data("kendoMultiSelect").value());
		
		
		$('#form-modal-13 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-13").modal();
		
	} else if (dataItem.invenType == "CT") {
		initForm11("타겟팅 변경");

		$("#form-11").attr("rowid", dataItem.id);

		var states = dataItem.tgtValue.replace("[", "[\"").replace("]", "\"]").replaceAll(", ", "\", \"");
		$("#form-11 select[name='states']").data("kendoMultiSelect").value(eval(states));
		
		validateStateValue($("#form-11 select[name='states']").data("kendoMultiSelect").value());
		
		
		$('#form-modal-11 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-11").modal();
		
	} else if (dataItem.invenType == "ST") {
		initForm14("타겟팅 변경");

		$("#form-14").attr("rowid", dataItem.id);

		var sites = dataItem.tgtValue.replace("[", "[\"").replace("]", "\"]").replaceAll(", ", "\", \"");
		$("#form-14 select[name='sites']").data("kendoMultiSelect").value(eval(sites));
		
		validateSiteValue($("#form-14 select[name='sites']").data("kendoMultiSelect").value());
		
		
		$('#form-modal-14 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-14").modal();
	}
}


function updateFilterType(plId, id, type) {
	
	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${updateFilterTypeUrl}",
		data: JSON.stringify({ id: id, filterType: type }),
		success: function (form) {
			showOperationSuccessMsg();
			$("#grid-" + plId).data("kendoGrid").dataSource.read();
			
			calcTargetScreenCnt(plId);
		},
		error: ajaxOperationError
	});
}


function initForm9(plId) {
	
	currPlaylistId = plId;
	
	$("#formRoot").html(kendo.template($("#template-9").html()));
	
	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${readOrderedTargetUrl}",
		data: JSON.stringify({ playlistId: plId }),
		success: function (data) {
			
	    	var itemTemplate = kendo.template($("#template-reorder-target-item").html());
	    	
	    	for(var i = 0; i < data.length; i++) {
	        	$("#reorder-target-div").append(itemTemplate(data[i]));
	    	}
	    	Sortable.create(document.getElementById("reorder-target-div"), { animation: 150 });
		},
		error: ajaxReadError
	});
}


function saveForm9() {

	var items = "";
	$("#reorder-target-div span[name='data']").each(function(i, obj) {
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
			url: "${reorderTargetUrl}",
			data: JSON.stringify({ items: items }),
			success: function (form) {
				showSaveSuccessMsg();
				$("#form-modal-9").modal("hide");
				$("#grid-" + currPlaylistId).data("kendoGrid").dataSource.read();
				
				calcTargetScreenCnt(currPlaylistId);
			},
			error: ajaxSaveError
		});
	} else {
		$("#form-modal-9").modal("hide");
		$("#grid-" + currPlaylistId).data("kendoGrid").dataSource.read();
	}
}


function initForm11(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-11").html()));
	
    $("#form-11 select[name='states']").kendoMultiSelect({
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
	
	
	$("#form-11 span[name='subtitle']").text(subtitle ? subtitle : "타겟팅 추가");
}


function saveForm11() {

	var stateCodes = $("#form-11 select[name='states']").data("kendoMultiSelect").value();
	
	if (stateCodes.length > 0) {
		
		var dataItems = $("#form-11 select[name='states']").data("kendoMultiSelect").dataItems();
		var valueTexts = [];
		
		for(var i = 0; i < dataItems.length; i += 1) {
			valueTexts.push(dataItems[i].text);
		}
		
		var data = {
			id: Number($("#form-11").attr("rowid")),
			playlist: currPlaylistId,
			stateCodes: stateCodes,
			stateTexts: valueTexts,
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
				$("#grid-" + currPlaylistId).data("kendoGrid").dataSource.read();
				
				calcTargetScreenCnt();
			},
			error: ajaxSaveError
		});
	}
}


function initForm12(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-12").html()));
	
    $("#form-12 select[name='regions']").kendoMultiSelect({
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
	
	
	$("#form-12 span[name='subtitle']").text(subtitle ? subtitle : "타겟팅 추가");
}


function saveForm12() {

	var regionCodes = $("#form-12 select[name='regions']").data("kendoMultiSelect").value();
	
	if (regionCodes.length > 0) {
		
		var dataItems = $("#form-12 select[name='regions']").data("kendoMultiSelect").dataItems();
		var valueTexts = [];
		
		for(var i = 0; i < dataItems.length; i += 1) {
			valueTexts.push(dataItems[i].text);
		}
		
		var data = {
			id: Number($("#form-12").attr("rowid")),
			playlist: currPlaylistId,
			regionCodes: regionCodes,
			regionTexts: valueTexts,
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
				$("#grid-" + currPlaylistId).data("kendoGrid").dataSource.read();
				
				calcTargetScreenCnt();
			},
			error: ajaxSaveError
		});
	}
}


function initForm13(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-13").html()));
	
    $("#form-13 select[name='screens']").kendoMultiSelect({
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
	
	
	$("#form-13 span[name='subtitle']").text(subtitle ? subtitle : "타겟팅 추가");
}


function saveForm13() {

	var screenIds = $("#form-13 select[name='screens']").data("kendoMultiSelect").value();
	
	if (screenIds.length > 0) {
		
		var dataItems = $("#form-13 select[name='screens']").data("kendoMultiSelect").dataItems();
		var valueTexts = [];
		
		for(var i = 0; i < dataItems.length; i += 1) {
			valueTexts.push(dataItems[i].text);
		}
		
		var data = {
			id: Number($("#form-13").attr("rowid")),
			playlist: currPlaylistId,
			screenIds: screenIds,
			screenTexts: valueTexts,
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
				$("#grid-" + currPlaylistId).data("kendoGrid").dataSource.read();
				
				calcTargetScreenCnt();
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
			playlist: currPlaylistId,
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
				$("#grid-" + currPlaylistId).data("kendoGrid").dataSource.read();
				
				calcTargetScreenCnt();
			},
			error: ajaxSaveError
		});
	}
}


function validateStateValue(values) {

	if (values.length == 0) {
		$("#form-11 button[name='save-btn']").addClass("disabled");
	} else {
		$("#form-11 button[name='save-btn']").removeClass("disabled");
	}
}


function validateRegionValue(values) {

	if (values.length == 0) {
		$("#form-12 button[name='save-btn']").addClass("disabled");
	} else {
		$("#form-12 button[name='save-btn']").removeClass("disabled");
	}
}


function validateScreenValue(values) {

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

</script>

<!--  / Scripts -->


<!-- / Page body -->





<!-- Functional tags -->

<func:geoLocModal />
<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
