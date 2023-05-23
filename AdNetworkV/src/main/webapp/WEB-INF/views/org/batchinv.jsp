<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/org/batchinv/readSite" var="readSiteUrl" />
<c:url value="/org/batchinv/readScreen" var="readScreenUrl" />
<c:url value="/org/batchinv/destroy" var="destroyUrl" />

<c:url value="/org/batchinv/readbatchoverview" var="readBatchOverviewUrl" />
<c:url value="/org/batchinv/replacetempdata" var="replaceTempDataUrl" />

<c:url value="/org/batchinv/startsitebatch" var="startSiteBatchUrl" />
<c:url value="/org/batchinv/startscreenbatch" var="startScreenBatchUrl" />

<c:url value="/org/batchinv/readResults" var="readResultUrl" />


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

<link rel="stylesheet" href="/resources/vendor/lib/smartwizard/smartwizard.css">

<script src="/resources/vendor/lib/smartwizard/smartwizard.js"></script>


<!-- Java(optional)  -->

<%
	String resultTemplate = "<span class='fa-regular " + 
			"# if (result == 'I') { #" + "fa-asterisk fa-fw text-muted" + 
			"# } else if (result == 'S') { #" + "fa-flag-checkered fa-fw" + 
			"# } else if (result == 'F') { #" + "fa-circle-stop fa-fw text-red" + 
			"# } else if (result == 'P') { #" + "fa-circle-exclamation fa-fw text-yellow" + 
			"# } #" +
			"'></span>";

	String noRecordsTemplate =
			"<div class='container text-center my-4'>" +
				"<div class='d-flex justify-content-center align-self-center'>" +
					"<span class='fa-thin fa-hexagon-exclamation fa-3x'></span>" +
					"<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>해당 자료 없음</span>" +
				"</div>" +
			"</div>";
%>


<div class="text-right">
	<button id="upload-btn" type="button" class="btn btn-primary">자료 엑셀 파일 업로드</button>
</div>


<!--  Tab -->
<ul class="nav nav-tabs tabs-alt container-p-x container-m--x mb-4">
	<li class="nav-item">
		<a class="nav-link active" data-toggle="tab" href="#download">
			<i class="mr-1 fa-light fa-download"></i>
			템플릿 파일 다운로드
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#site">
			<i class="mr-1 fa-light fa-map-pin"></i>
			사이트
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#screen">
			<i class="mr-1 fa-light fa-screen-users"></i>
			매체 화면
		</a>
	</li>
</ul>

<div class="tab-content mb-4">

	<div class="tab-pane active" id="download">
		<div class="card">
			<div class="card-body py-3">
				<h6 class="text-muted small py-3 mb-0">엑셀 템플릿 파일</h6>
				<p class="lead">
					1. 우측 하단에 있는 
					<button type="button" class="btn btn-outline-secondary btn-round btn-sm">엑셀 템플릿 파일 다운로드</button>
					버튼을 눌러 템플릿 파일을 다운로드 받습니다.
				</p>
				<p class="lead">
					2. 템플릿 파일의 제목행 메모 설명을 참고하여 자료를 채웁니다. 1행의 제목행은 그냥 두고, 2행부터 채우기 시작합니다.
				</p>
				<p class="lead">
					3. 템플릿 파일의 항목 중 기본값이 설정되어 있으면 생략될 수 있습니다.
					<button type="button" class="btn btn-outline-secondary btn-round btn-sm">인벤토리 항목 값 설정 가이드 다운로드</button>
					버튼을 눌러 관련 가이드 문서를 참고하기 바랍니다.
				</p>
				<p class="lead">
					4. 파일명의 마지막을 <mark>.v4.xlsx</mark> 로 변경 후 저장합니다. (예: "1차 파일 업로드.2023.04.13<mark>.v4.xlsx</mark>")
				</p>
				<p class="lead">
					5. 상단 우측에 있는 
					<button type="button" class="btn btn-primary btn-sm">자료 엑셀 파일 업로드</button>
					버튼을 눌러 저장한 자료 파일을 업로드합니다.
				</p>
				
				<div class="text-right mt-2">
					<button type="button" id="download-btn-2" class="btn btn-outline-secondary btn-round">인벤토리 항목 값 설정 가이드 다운로드</button>
					<button type="button" id="download-btn-1" class="btn btn-outline-secondary btn-round">엑셀 템플릿 파일 다운로드</button>
				</div>
			</div>
			<hr class="m-0" />
			<div class="card-body py-3">
				<h6 class="text-muted small font-weight-bold py-3 mb-0">v4</h6>
				<p>
					<span class="fa-regular fa-check text-info"></span>
					<span class="pl-1">사이트</span>
					<span class="pl-4" style="font-weight:300;">사이트ID, 사이트명, 위도, 경도, 시/도, 시/군/구, 주소, 입지 유형, 장소 유형, 액션 - 총 10개 항목</span>
				</p>
				<p>
					<span class="fa-regular fa-check text-info"></span>
					<span class="pl-1">매체 화면</span>
					<span class="pl-4" style="font-weight:300;">
						화면ID, 화면명, 사이트ID, 해상도(가로x세로), 동영상 허용, 이미지 허용, 
						활성화 여부, 광고 서버에서 이용, 액션 - 총 9개 항목</span>
				</p>
			</div>
			<hr class="m-0" />
			<div class="card-body py-3">
				<h6 class="text-muted small font-weight-bold py-3 mb-0">v3</h6>
				<p>
					<span class="fa-regular fa-check text-info"></span>
					<span class="pl-1">사이트</span>
					<span class="pl-4" style="font-weight:300;">사이트ID, 사이트명, 위도, 경도, 시/도, 시/군/구, 주소, 입지 유형, <mark>장소 유형</mark>, 액션 - 총 10개 항목</span>
				</p>
				<p>
					<span class="fa-regular fa-check text-info"></span>
					<span class="pl-1">매체 화면</span>
					<span class="pl-4" style="font-weight:300;">
						화면ID, 화면명, 사이트ID, 해상도(가로x세로), 동영상 허용, 이미지 허용, 
						활성화 여부, 광고 서버에서 이용, 액션 - 총 9개 항목<mark>(장소 유형 제외)</mark></span>
				</p>
			</div>
			<hr class="m-0" />
			<div class="card-body py-3">
				<h6 class="text-muted small font-weight-bold py-3 mb-0">v2</h6>
				<p>
					<span class="fa-regular fa-check text-info"></span>
					<span class="pl-1">사이트</span>
					<span class="pl-4" style="font-weight:300;">사이트ID, 사이트명, 위도, 경도, 시/도, 시/군/구, 주소, 입지 유형, <mark>액션</mark> - 총 9개 항목</span>
				</p>
				<p>
					<span class="fa-regular fa-check text-info"></span>
					<span class="pl-1">매체 화면</span>
					<span class="pl-4" style="font-weight:300;">
						화면ID, 화면명, 사이트ID, <mark>해상도(가로x세로)</mark>, <mark>장소 유형</mark>, <mark>동영상 허용</mark>, <mark>이미지 허용</mark>, 
						활성화 여부, <mark>광고 서버에서 이용</mark>, <mark>액션</mark> - 총 10개 항목</span>
				</p>
			</div>
			<hr class="m-0" />
			<div class="card-body py-3">
				<h6 class="text-muted small font-weight-bold py-3 mb-0">v1</h6>
				<p>
					<span class="fa-regular fa-check text-info"></span>
					<span class="pl-1">사이트</span>
					<span class="pl-4" style="font-weight:300;">사이트ID, 사이트명, 위도, 경도, 시/도, 시/군/구, 주소, 입지 유형 - 총 8개 항목</span>
				</p>
				<p>
					<span class="fa-regular fa-check text-info"></span>
					<span class="pl-1">매체 화면</span>
					<span class="pl-4" style="font-weight:300;">화면ID, 화면명, 사이트ID, 해상도 가로 픽셀, 해상도 세로 픽셀, 활성화 여부 - 총 6개 항목</span>
				</p>
			</div>
		</div>	
	</div>

	<div class="tab-pane" id="site">

<!-- Kendo grid  -->

		<kendo:grid name="grid-site" pageable="true" filterable="true" sortable="true" scrollable="true" reorderable="true" resizable="true" selectable="multiple" >
			<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
			<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
		    <kendo:grid-toolbarTemplate>
		    	<div class="clearfix">
		    		<div class="float-left">
		    			<button id="start-site-batch-btn" type="button" class="btn btn-outline-success">사이트 일괄 작업 시작</button>
		    		</div>
		    		<div class="float-right">
		    			<button id="delete-site-btn" type="button" class="btn btn-danger">삭제</button>
		    		</div>
		    	</div>
		   	</kendo:grid-toolbarTemplate>
			<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
			<kendo:grid-columns>
				<kendo:grid-column title="결과" field="result" width="100" template="<%= resultTemplate %>" >
					<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
						<kendo:dataSource>
							<kendo:dataSource-transport>
								<kendo:dataSource-transport-read url="${readResultUrl}" dataType="json" type="POST" contentType="application/json" />
							</kendo:dataSource-transport>
						</kendo:dataSource>
					</kendo:grid-column-filterable>
				</kendo:grid-column>
				<kendo:grid-column title="사이트ID" field="colA" width="150" />
				<kendo:grid-column title="사이트명" field="colB" width="200" />
				<kendo:grid-column title="위도" field="colC" width="120" filterable="false" sortable="false" />
				<kendo:grid-column title="경도" field="colD" width="120" filterable="false" sortable="false" />
				<kendo:grid-column title="시/도" field="colE" width="100" />
				<kendo:grid-column title="시/군/구" field="colF" width="150" />
				<kendo:grid-column title="주소" field="colG" width="500" />
				<kendo:grid-column title="입지 유형" field="colH" width="120" />
				<kendo:grid-column title="장소 유형" field="colI" width="150" />
				<kendo:grid-column title="액션" field="colJ" width="100" />
				<kendo:grid-column title="번호" field="id" width="150" />
			</kendo:grid-columns>
			<kendo:grid-filterable>
				<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
			</kendo:grid-filterable>
			<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
				<kendo:dataSource-sort>
					<kendo:dataSource-sortItem field="id" dir="asc"/>
				</kendo:dataSource-sort>
				<kendo:dataSource-filter>
					<kendo:dataSource-filterItem field="medium.id" operator="eq" logic="and" value="${sessionScope['currMediumId']}">
					</kendo:dataSource-filterItem>
				</kendo:dataSource-filter>
				<kendo:dataSource-transport>
					<kendo:dataSource-transport-read url="${readSiteUrl}" dataType="json" type="POST" contentType="application/json"/>
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
						</kendo:dataSource-schema-model-fields>
					</kendo:dataSource-schema-model>
				</kendo:dataSource-schema>
			</kendo:dataSource>
		</kendo:grid>

<!-- / Kendo grid  -->

	</div>
	<div class="tab-pane" id="screen">

<!-- Kendo grid  -->

		<kendo:grid name="grid-screen" pageable="true" filterable="true" sortable="true" scrollable="true" reorderable="true" resizable="true" selectable="multiple" >
			<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
			<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
		    <kendo:grid-toolbarTemplate>
		    	<div class="clearfix">
		    		<div class="float-left">
		    			<button id="start-screen-batch-btn" type="button" class="btn btn-outline-success">매체 화면 일괄 작업 시작</button>
		    		</div>
		    		<div class="float-right">
		    			<button id="delete-screen-btn" type="button" class="btn btn-danger">삭제</button>
		    		</div>
		    	</div>
		   	</kendo:grid-toolbarTemplate>
			<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
			<kendo:grid-columns>
				<kendo:grid-column title="결과" field="result" width="100" template="<%= resultTemplate %>" >
					<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
						<kendo:dataSource>
							<kendo:dataSource-transport>
								<kendo:dataSource-transport-read url="${readResultUrl}" dataType="json" type="POST" contentType="application/json" />
							</kendo:dataSource-transport>
						</kendo:dataSource>
					</kendo:grid-column-filterable>
				</kendo:grid-column>
				<kendo:grid-column title="화면ID" field="colA" width="120" />
				<kendo:grid-column title="화면명" field="colB" width="200" />
				<kendo:grid-column title="사이트ID" field="colC" width="120" />
				<kendo:grid-column title="해상도" field="colD" width="150" />
				<kendo:grid-column title="동영상허용" field="colE" width="150" />
				<kendo:grid-column title="이미지허용" field="colF" width="150" />
				<kendo:grid-column title="활성화여부" field="colG" width="150" />
				<kendo:grid-column title="광고서버이용" field="colH" width="150" />
				<kendo:grid-column title="액션" field="colI" width="100" />
				<kendo:grid-column title="번호" field="id" width="150" />
			</kendo:grid-columns>
			<kendo:grid-filterable>
				<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
			</kendo:grid-filterable>
			<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
				<kendo:dataSource-sort>
					<kendo:dataSource-sortItem field="id" dir="asc"/>
				</kendo:dataSource-sort>
				<kendo:dataSource-filter>
					<kendo:dataSource-filterItem field="medium.id" operator="eq" logic="and" value="${sessionScope['currMediumId']}">
					</kendo:dataSource-filterItem>
				</kendo:dataSource-filter>
				<kendo:dataSource-transport>
					<kendo:dataSource-transport-read url="${readScreenUrl}" dataType="json" type="POST" contentType="application/json"/>
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
						</kendo:dataSource-schema-model-fields>
					</kendo:dataSource-schema-model>
				</kendo:dataSource-schema>
			</kendo:dataSource>
		</kendo:grid>

<!-- / Kendo grid  -->

	</div>
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

</style>


<!-- Smart wizard -->

<script id="sw-template" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="wizard-modal">
    <div class="modal-dialog">
        <form class="modal-content" id="wizard-form" file="">
            <div class="modal-header move-cursor"">
				<h5 class="modal-title">
					<span name="form-title">사이트 및 매체 화면</span>
					<span class="font-weight-light pl-1">일괄 추가</span>
				</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">×</span>
                </button>
            </div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">

				<div name="smartWizard">
					<ul>
						<li name="tab-0">
							<a href="\\\#sw-step-1" class="mb-3">
								<span class="sw-done-icon"><span class="fa-regular fa-check"></span></span>
								<span class="sw-icon"><span class="fa-regular fa-cloud-upload-alt"></span></span>
								업로드
								<div class="text-muted small">정해진 양식의 엑셀 파일</div>
							</a>
						</li>
						<li name="tab-1">
							<a href="\\\#sw-step-2" class="mb-3">
								<span class="sw-done-icon"><span class="fa-regular fa-check"></span></span>
								<span class="sw-icon"><span class="fa-regular fa-eye"></span></span>
								파일 내용 확인
								<div class="text-muted small">파일명, 버전, 자료 건수</div>
							</a>
						</li>
					</ul>

					<div class="mb-3">
						<div id="sw-step-1" class="card animated fadeIn">
							<div class="card-body">
								<div class="drop-zone upload-root-div">
									<input name="files" type="file" />
								</div>
							</div>
						</div>
						<div id="sw-step-2" class="card animated fadeIn">
							<div class="card-body">
								<div class="form-row">
									<div class="col-sm-8">
										<div class="form-group col">
											<label class="form-label">
												파일명
											</label>
											<input name="filename" type="text" class="form-control" readonly="readonly">
										</div>
									</div>
									<div class="col-sm-4">
										<div class="form-group col">
											<label class="form-label">
												버전
											</label>
											<input name="version" type="text" class="form-control" readonly="readonly">
										</div>
									</div>
								</div>
								<div class="form-row">
									<div class="col-sm-6">
										<div class="form-group col">
											<label class="form-label">
												사이트 자료 건수
											</label>
											<input name="siteCount" type="text" class="form-control" readonly="readonly">
										</div>
									</div>
									<div class="col-sm-6">
										<div class="form-group col">
											<label class="form-label">
												매체 화면 자료 건수
											</label>
											<input name="screenCount" type="text" class="form-control" readonly="readonly">
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>

			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer d-flex">
				<div name="error-msg-div">
					<span class="fas fa-minus-circle text-danger"></span>
					<span class="pl-1" name="error-msg-span"></span>
				</div>
				<button type="button" class="btn btn-default ml-auto" data-dismiss="modal">${form_cancel}</button>
				<button name="proc-btn" type="button" class="btn btn-primary" onclick='processBatchJob()'>진행</button>
			</div>
        </form>
    </div>
</div>

</script>

<style>

.upload-root-div {
	height: 200px;
}

.k-upload-files {
	height: 100px;
	overflow-x: hidden;
	overflow-y: hidden;
}

strong.k-upload-status.k-upload-status-total { font-weight: 500; color: #2e2e2e; }

div.k-dropzone.k-dropzone-hovered em, div.k-dropzone em { color: #2e2e2e; }

.k-upload .k-upload-files ~ .k-button {
	width: 48%;
	margin: 3px 3px 0.25em 1%;
}

.k-upload .k-button {
	height: 38px;
	border-radius: .25rem;
}

.k-upload .k-upload-button {
	border-color: transparent;
	background: #8897AA;
	color: #fff;
}

.k-upload .k-upload-button:hover {
	background: #818fa2;
}

.k-upload .k-upload-files ~ .k-upload-selected {
	border-color: transparent;
	background: #e84c64;
	color: #fff;
}

.k-upload .k-upload-files ~ .k-upload-selected:hover {
	background: #dc485f;
}

.k-upload .k-upload-files ~ .k-clear-selected {
	background: transparent;
	color: #4E5155;
	border: 1px solid rgba(24,28,33,0.1);
}

.k-upload .k-upload-files ~ .k-clear-selected:hover {
	background: rgba(24,28,33,0.06);
}

</style>

<!-- / Smart wizard -->


<!--  Scripts -->

<script>

$(document).ready(function() {
	
	// Upload
	$("#upload-btn").click(function(e) {
		e.preventDefault();
		
		initSmartWizard();
		
		$('#wizard-modal .modal-dialog').draggable({ handle: '.modal-header' });
		$("#wizard-modal").modal();
	});
	// / Upload
	
	
	// Download
	$("#download-btn-1").click(function(e) {
		e.preventDefault();

		location.href = "/adn/common/download?type=XlsTemplate&file=Inventory_Data_Upload_Excel_Template.v4.xlsx";
	});
	// / Download
	
	
	// Download
	$("#download-btn-2").click(function(e) {
		e.preventDefault();

		location.href = "/adn/common/download?type=XlsTemplate&file=Inventory_Data_Setting_Guide.pdf";
	});
	// / Download
    
});

</script>

<!--  / Scripts -->


<!--  Scripts for smart wizard -->

<script>

var goAhead = false;

function initSmartWizard() {
	
	$("#formRoot").html(kendo.template($("#sw-template").html()));
	
	$("#wizard-form div[name='smartWizard']").smartWizard({
		showStepURLhash: false,
		toolbarSettings: {
			showNextButton: false,
			showPreviousButton: false,
		}
	});

	$("#wizard-form input[name='files']").kendoUpload({
		multiple: false,
		async: {
			saveUrl: "${uploadModel.saveUrl}",
			autoUpload: false
		},
		localization: {
			cancel: "취소",
			dropFilesHere: "업로드 대상 파일을 여기에 끌어 놓기",
			headerStatusUploaded: "완료",
			headerStatusUploading: "업로드중...",
			remove: "삭제",
			retry: "재시도",
			select: "파일 선택...",
			uploadSelectedFiles: "업로드 시작",
			clearSelectedFiles: "목록 지우기",
			invalidFileExtension: "허용되지 않는 파일 유형입니다.",
		},
		dropZone: ".drop-zone",
		upload: function(e) {
			e.data = {
				type: "${uploadModel.type}",
			};
		},
		success: function(e) {
			$("#wizard-form div[name='smartWizard']").smartWizard("next");
			$.ajax({
				type: "POST",
				contentType: "application/json",
				dataType: "json",
				url: "${readBatchOverviewUrl}",
				data: JSON.stringify({ file: e.files[0].name }),
				success: function (data) {
					
					$("#wizard-form input[name='filename']").val(data.filename);
					$("#wizard-form input[name='version']").val(data.version);
					$("#wizard-form input[name='siteCount']").val(data.siteCount);
					$("#wizard-form input[name='screenCount']").val(data.screenCount);
					
					$("#wizard-form").attr("file", e.files[0].name);
					
					if (data.errorMsg) {
						$("#wizard-form div[name='error-msg-div']").show();
						$("#wizard-form span[name='error-msg-span']").html(data.errorMsg);
						goAhead = false;
					} else {
						$("#wizard-form span[name='error-msg-span']").html("");
						$("#wizard-form div[name='error-msg-div']").hide();
						$("#wizard-form button[name='proc-btn']").removeClass("disabled");
						goAhead = true;
					}
				},
				error: ajaxReadError
			});
		},
		validation: {
			allowedExtensions: ${uploadModel.allowedExtensions}
		},
	});
	
	$("#wizard-form div[name='error-msg-div']").hide();
	$("#wizard-form button[name='proc-btn']").addClass("disabled");
	goAhread = false;
}


function processBatchJob() {

	if (goAhead) {
		var msg = "자료 검증을 위해 임시 저장 공간에 일괄 등록될 예정이며, 이전에 등록된 자료는 모두 삭제됩니다. " +
				"자료 수가 많을 경우 다소 많은 시간이 소요될 예정입니다. 계속 진행하시겠습니까?";
		
		showConfirmModal(msg, function(result) {
			if (result) {
				$("#wizard-form button[name='proc-btn']").addClass("disabled");
				goAhead = false;
				
				showWaitModal();
		    	
				$.ajax({
					type: "POST",
					contentType: "application/json",
					dataType: "json",
					url: "${replaceTempDataUrl}",
					data: JSON.stringify({ file: $("#wizard-form").attr("file") }),
					success: function (data) {
						showOperationSuccessMsg();
						hideWaitModal();
						
						$("#wizard-modal").modal("hide");
						
						$("#grid-site").data("kendoGrid").dataSource.read();
						$("#grid-screen").data("kendoGrid").dataSource.read();
					},
					error: function(e) {
						hideWaitModal();
						ajaxOperationError(e);
					}
				});
			}
		});
	}
}

</script>

<!--  / Scripts for smart wizard -->


<!-- Grid button actions  -->

<script>
$(document).ready(function() {

	// Start site batch
	$("#start-site-batch-btn").click(function(e) {
		e.preventDefault();
			
		var grid = $("#grid-site").data("kendoGrid");
		
		if (grid.dataSource.total() > 0) {
			
			var msg = "사이트 총 {0} 건에 대한 추가/변경/삭제 작업을 진행할 예정입니다. 자료 수가 많을 경우 다소 많은 시간이 소요될 예정입니다. 계속 진행하시겠습니까?".replace("{0}", "<strong>" + grid.dataSource.total() + "</strong>");
			
			showConfirmModal(msg, function(result) {
				if (result) {
					showWaitModal();
					
					$.ajax({
						type: "POST",
						contentType: "application/json",
						dataType: "json",
						url: "${startSiteBatchUrl}",
						data: JSON.stringify({}),
						success: function (data, status, xhr) {
							hideWaitModal();
	  						showOperationSuccessMsg();
							
							$("#grid-site").data("kendoGrid").dataSource.read();
						},
						error: function(e) {
							hideWaitModal();
							
							var msg = JSON.parse(e.responseText).error;
							if (msg == "OperationError") {
								showOperationErrorMsg();
							} else {
								showAlertModal("danger", msg);
							}
						}
					});
				}
			});
		}
	});
	// / Start site batch

	
	// Start screen batch
	$("#start-screen-batch-btn").click(function(e) {
		e.preventDefault();
			
		var grid = $("#grid-screen").data("kendoGrid");
		
		if (grid.dataSource.total() > 0) {
			
			var msg = "매체 화면 총 {0} 건에 대한 추가/변경/삭제 작업을 진행할 예정입니다. 자료 수가 많을 경우 다소 많은 시간이 소요될 예정입니다. 계속 진행하시겠습니까?".replace("{0}", "<strong>" + grid.dataSource.total() + "</strong>");
			
			showConfirmModal(msg, function(result) {
				if (result) {
					showWaitModal();
					
					$.ajax({
						type: "POST",
						contentType: "application/json",
						dataType: "json",
						url: "${startScreenBatchUrl}",
						data: JSON.stringify({}),
						success: function (data, status, xhr) {
							hideWaitModal();
	  						showOperationSuccessMsg();
							
							$("#grid-screen").data("kendoGrid").dataSource.read();
						},
						error: function(e) {
							hideWaitModal();
							
							var msg = JSON.parse(e.responseText).error;
							if (msg == "OperationError") {
								showOperationErrorMsg();
							} else {
								showAlertModal("danger", msg);
							}
						}
					});
				}
			});
		}
	});
	// / Start screen batch

	
	// Delete(site)
	$("#delete-site-btn").click(function(e) {
		e.preventDefault();
			
		var grid = $("#grid-site").data("kendoGrid");
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

	
	// Delete(screen)
	$("#delete-screen-btn").click(function(e) {
		e.preventDefault();
			
		var grid = $("#grid-screen").data("kendoGrid");
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


<!-- / Page body -->





<!-- Functional tags -->


<!-- Closing tags -->

<common:base />
<common:pageClosing />
