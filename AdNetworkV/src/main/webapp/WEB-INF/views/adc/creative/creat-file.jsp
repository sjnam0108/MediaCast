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

<c:url value="/adc/creative/files/read" var="readUrl" />
<c:url value="/adc/creative/files/destroy" var="destroyUrl" />


<!-- Opening tags -->

<common:mediumPageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 fa-light fa-photo-film-music fa-fw"></span>
	${pageTitle}
	<span class="font-weight-light pl-1">소재 파일</span>
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
		<a class="nav-link active" href="/adc/creative/files/${Advertiser.id}">
			<i class="mr-1 fa-light fa-photo-film-music"></i>
			소재 파일
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" href="/adc/creative/invtargets/${Advertiser.id}">
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
		location.href = "/adc/creative/files/${Advertiser.id}/" + $("select[name='nav-item-creat-select']").val();
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


<!-- Java(optional)  -->

<%
	String thumbTemplate = "<Img src='/thumbs/#= ctntFolder.name #/#= thumbFilename #' width='128' height='128'>";
	String fileTemplate = "<span class='pr-2'>#= srcFilename #</span><a href='" +
			"#= ctntFolder.webPath #/#= ctntFolder.name #/#= uuid #/#= filename #' target='_blank'>" +
			"<span class='fa-light fa-arrow-up-right-from-square'></span></a>";
	String resolTemplate =
			"# if (fitnessOfResRatio == 1) { #" +
				"<span class='badge badge-outline-success'>#= resolution #</span>" +
			"# } else if (fitnessOfResRatio == 0) { #" +
				"<span class='badge badge-outline-secondary'>#= resolution #</span>" +
			"# } else if (fitnessOfResRatio == -1) { #" +
				"<span class='badge badge-outline-danger'>#= resolution #</span>" +
			"# } #";
	String durTemplate =
			"# if (mediaType == 'V') { #" +
				"<div class='dur-container'><span data-toggle='tooltip' data-placement='top' title='#= dispSrcDurSecs #'>#= durSecs #s</span></div>" +
			"# } else { #" +
				"<span>-</span>" +
			"# } #";
	String lengthTemplate = "<div class='len-container'><span data-toggle='tooltip' data-placement='top' title='#= dispFileLength #'>#= smartLength #</span></div>";
	String regDateTemplate = net.doohad.utils.Util.getSmartDate("whoCreationDate");
	
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
<kendo:grid name="grid" pageable="true" filterable="true" sortable="true" scrollable="true" reorderable="true" resizable="true" selectable="single" >
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" previousNext="false" numeric="false" pageSize="10000" info="false" />
	<kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button id="upload-btn" type="button" class="btn btn-outline-success">업로드</button>
    		</div>
    		<div class="float-right">
    			<button id="delete-btn" type="button" class="btn btn-danger">삭제</button>
    		</div>
    	</div>
	</kendo:grid-toolbarTemplate>
	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="썸네일" field="thumbFilename" width="160" filterable="false" template="<%= thumbTemplate %>" />
		<kendo:grid-column title="파일명" field="srcFilename" width="250" filterable="false" template="<%= fileTemplate %>" />
		<kendo:grid-column title="해상도" field="resolution" width="100" filterable="false" template="<%= resolTemplate %>" />
		<kendo:grid-column title="파일형식" field="mimeType" width="100" filterable="false" />
		<kendo:grid-column title="재생시간" field="durSecs" width="80" filterable="false" template="<%= durTemplate %>" />
		<kendo:grid-column title="파일크기" field="fileLength" width="100" filterable="false" template="<%= lengthTemplate %>" />
		<kendo:grid-column title="컨텐츠 폴더" field="ctntFolder.name" width="120" filterable="false" />
		<kendo:grid-column title="UUID" field="uuid" width="300" filterable="false" />
		<kendo:grid-column title="등록" field="whoCreationDate" width="120" filterable="false" template="<%= regDateTemplate %>" />
	</kendo:grid-columns>
	<kendo:grid-dataBound>
		<script>
			function grid_dataBound(e) {
				$('[data-toggle="tooltip"]').tooltip({
					   container: '.dur-container'
				});
        	}
		</script>
	</kendo:grid-dataBound>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="filename" dir="asc"/>
		</kendo:dataSource-sort>
       	<kendo:dataSource-filter>
      		<kendo:dataSource-filterItem field="creative.id" operator="eq" logic="and" value="${Creative.id}" >
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
					<kendo:dataSource-schema-model-field name="whoCreationDate" type="date" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
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


/* 그리드 자료 새로고침 버튼을 우측 정렬  */
div.k-pager-wrap.k-grid-pager.k-widget.k-floatwrap {
	display: flex!important;
	justify-content: flex-end!important;
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

	// Upload
	$("#upload-btn").click(function(e) {
		e.preventDefault();
		
		openUploadModal("MEDIA", "광고 소재 파일", "CREATFILE", "${Creative.id}");
	});
	// / Upload
	
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


<!--  Scripts -->

<script>


function uploadModalClosed() {
	
	$("#grid").data("kendoGrid").dataSource.read();
}


function navToPrev() {
	history.back();
}


function closeAutoUploadModal() {
	
	setTimeout(function() {
		$("#commonUploadModal").modal("hide");
	}, 1500);
}

</script>

<!--  / Scripts -->



</c:otherwise>
</c:choose>

<!--  / Page details -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmValidate />
<func:creatdetailUpload />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
