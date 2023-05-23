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

<c:url value="/adc/creative/update" var="updateUrl" />
<c:url value="/adc/creative/submit" var="submitUrl" />
<c:url value="/adc/creative/recall" var="recallUrl" />
<c:url value="/adc/creative/reject" var="rejectUrl" />
<c:url value="/adc/creative/archive" var="archiveUrl" />
<c:url value="/adc/creative/unarchive" var="unarchiveUrl" />
<c:url value="/adc/creative/pause" var="pauseUrl" />
<c:url value="/adc/creative/resume" var="resumeUrl" />
<c:url value="/adc/creative/destroy" var="destroyUrl" />

<c:url value="/adc/creative/copy" var="copyUrl" />


<!-- Opening tags -->

<common:mediumPageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 fa-light fa-microscope fa-fw"></span>
	${pageTitle}
	<span class="font-weight-light pl-1">소재 상세</span>
</h4>

<hr class="border-light container-m--x mt-0 mb-4">





<!-- Page body -->


<!--  Overview header -->

<adc:advertiser />

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
		<a class="nav-link active" href="/adc/creative/detail/${Advertiser.id}">
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
		location.href = "/adc/creative/detail/${Advertiser.id}/" + $("select[name='nav-item-creat-select']").val();
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


<div class="card">
	<h6 class="card-header with-elements pl-2">
		<span class="lead">
			<span class="fa-stack fa-xs" title="광고 소재" style="vertical-align: top; color: #a0a0a0;">
				<span class="fa-solid fa-circle fa-stack-2x"></span>
				<span class="fa-solid fa-clapperboard-play fa-stack-1x fa-inverse fa-lg"></span>
			</span>
			<span id="creative-name"></span>
		</span>
		<div class="card-header-elements ml-auto">
			<div class="btn-group pl-3">
				<button type="button" class="btn btn-sm btn-secondary dropdown-toggle" data-toggle="dropdown">
					<span class="fa-light fa-lg fa-signs-post"></span>
					<span class="pl-1">진행</span>
				</button>
				<div class="dropdown-menu">
					<a class="dropdown-item" href="javascript:void(0)" id="edit-btn">
						<i class="fa-regular fa-pencil-alt text-success fa-fw"></i><span class="pl-2">수정</span>
					</a>
					<div class="dropdown-divider"></div>
					<a class="dropdown-item" href="javascript:void(0)" id="submit-btn">
						<i class="fa-light fa-paper-plane fa-fw"></i><span class="pl-2">승인 요청</span>
					</a>
					<a class="dropdown-item" href="javascript:void(0)" id="recall-btn">
						<i class="fa-light fa-arrow-rotate-left fa-fw"></i><span class="pl-2">승인 요청 회수</span>
					</a>
					<div class="dropdown-divider"></div>
					<a class="dropdown-item" href="javascript:void(0)" id="reject-btn">
						<i class="fa-light fa-thumbs-down fa-fw"></i><span class="pl-2">승인 소재 거절</span>
					</a>
					<div class="dropdown-divider"></div>
					<a class="dropdown-item" href="javascript:void(0)" id="archive-btn">
						<i class="fa-light fa-box-archive fa-fw"></i><span class="pl-2">보관</span>
					</a>
					<a class="dropdown-item" href="javascript:void(0)" id="unarchive-btn">
						<i class="fa-light fa-box-open fa-fw"></i><span class="pl-2">보관 해제</span>
					</a>
					<div class="dropdown-divider"></div>
					<a class="dropdown-item" href="javascript:void(0)" id="pause-btn">
						<i class="fa-regular fa-circle-pause text-danger fa-fw"></i><span class="pl-2">잠시 멈춤</span>
					</a>
					<a class="dropdown-item" href="javascript:void(0)" id="resume-btn">
						<i class="fa-light fa-play fa-fw"></i><span class="pl-2">재개</span>
					</a>
					<div class="dropdown-divider"></div>
					<a class="dropdown-item" href="javascript:void(0)" id="delete-btn">
						<i class="fa-regular fa-trash-can text-danger fa-fw"></i><span class="pl-2">삭제</span>
					</a>
				</div>    			
			</div>			
		</div>
	</h6>
	<div class="row no-gutters row-bordered row-border-light">
		<div class="col">
			<div class="d-flex align-items-center container-p-x py-4">
			
<c:choose>
<c:when test="${Creative.type == 'C'}">

				<span class="fa-thin fa-audio-description fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">유형</div>
					<div class="text-large">일반 광고</div>
				</div>
				
</c:when>
<c:when test="${Creative.type == 'F'}">

				<span class="fa-thin fa-house fa-3x text-orange"></span>
				<div class="ml-3">
					<div class="text-muted small">유형</div>
					<div class="text-large">대체 광고</div>
				</div>
				
</c:when>
</c:choose>
			
			</div>
		</div>
		<div class="col">
			<div class="d-flex align-items-center container-p-x py-4">
			
<c:choose>
<c:when test="${Creative.status == 'D'}">

				<span class="fa-thin fa-asterisk fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">상태</div>
					<div class="text-large">준비</div>
				</div>
				
</c:when>
<c:when test="${Creative.status == 'P'}">

				<span class="fa-thin fa-square-question fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">상태</div>
					<div class="text-large">승인대기</div>
				</div>
				
</c:when>
<c:when test="${Creative.status == 'A'}">

				<span class="fa-thin fa-square-check fa-3x text-blue"></span>
				<div class="ml-3">
					<div class="text-muted small">상태</div>
					<div class="text-large">승인</div>
				</div>
				
</c:when>
<c:when test="${Creative.status == 'J'}">

				<span class="fa-thin fa-do-not-enter fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">상태</div>
					<div class="text-large">거절</div>
				</div>
				
</c:when>
<c:when test="${Creative.status == 'V'}">

				<span class="fa-thin fa-box-archive fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">상태</div>
					<div class="text-large">보관</div>
				</div>
				
</c:when>
</c:choose>
			
			</div>
		</div>
		
<c:if test="${Creative.type eq 'F'}">
		
		<div class="col">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-weight-scale fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">대체 광고간 가중치</div>
					<div class="text-large">${Creative.fbWeight}</div>
				</div>
			</div>
		</div>
		
</c:if>		
		
	</div>
</div>


<!-- Form button actions  -->

<script>
$(document).ready(function() {

	var title = "";
	
	if (${Creative.paused} == true) {
		title = "<span title='잠시 멈춤'><span class='fa-light fa-circle-pause text-danger'></span></span><span class='pr-1'></span>";
	}
	title = title + "<span>${Creative.name}</span>";
	if (${Creative.invenTargeted} == true) {
		title = title + "<span class='pl-1'></span><span title='인벤토리 타겟팅'><span class='fa-light fa-bullseye-arrow text-blue'></span></span>";
	}
	if (${Creative.timeTargeted} == true) {
		title = title + "<span class='pl-1'></span><span title='시간 타겟팅'><span class='fa-light fa-alarm-clock text-green'></span></span>";
	}
	
	var resolutions = "<span class='pl-3'>" + dispResoBadgeValues("${Creative.fileResolutions}") + "</span>";
	$("#creative-name").html(title + resolutions);
	
	
	// Edit
	$("#edit-btn").click(function(e) {
		e.preventDefault();
		
		edit();
	});
	// / Edit
	
	// Submit
	$("#submit-btn").click(function(e) {
		e.preventDefault();

		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${submitUrl}",
			data: JSON.stringify({ items: [ ${Creative.id} ] }),
			success: function (form) {
				showOperationSuccessMsg();
				reload();
			},
			error: ajaxOperationError
		});
	});
	// / Submit
	
	// Recall
	$("#recall-btn").click(function(e) {
		e.preventDefault();

		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${recallUrl}",
			data: JSON.stringify({ items: [ ${Creative.id} ] }),
			success: function (form) {
				showOperationSuccessMsg();
				reload();
			},
			error: ajaxOperationError
		});
	});
	// / Recall
	
	// Reject
	$("#reject-btn").click(function(e) {
		e.preventDefault();
		
		var box = bootbox.prompt({
			size: "small",
			title: "거절 이유",
			backdrop: "static",
			buttons: {
				cancel: {
					label: '취소',
					className: "btn-default",
				},
				confirm: {
					label: '확인',
					className: "btn-danger",
				}
			},
			animate: false,
			callback: function(result) {
				if (result) {
					var realRes = $.trim(result);
					if (realRes) {
						
						$.ajax({
							type: "POST",
							contentType: "application/json",
							dataType: "json",
							url: "${rejectUrl}",
							data: JSON.stringify({ items: [ ${Creative.id} ], reason: realRes }),
							success: function (form) {
								showOperationSuccessMsg();
								reload();
							},
							error: ajaxOperationError
						});

					} else {
						showConfirmModal("거절 이유가 입력되지 않아, 자동으로 거절이 취소되었습니다.", function(result) { });
					}
				} else {
					showConfirmModal("거절 이유가 입력되지 않아, 자동으로 거절이 취소되었습니다.", function(result) { });
				}
			},
			className: "modal-level-top",
		}).init(function() {
			setTimeout(function(){
				$('.modal-backdrop:last-child').addClass('modal-level-top');
			});
		});
		
		box.find('.modal-level-top .modal-dialog').addClass("modal-dialog-vertical-center");
		box.find('.modal-level-top .modal-content').addClass("modal-content-border-1");

<c:if test="${not isMobileMode}">

		box.find('.modal-level-top .modal-header').addClass("move-cursor");
		box.find('.modal-level-top .modal-dialog').draggable({ handle: '.modal-header' });

</c:if>

	});
	// / Reject
	
	// Archive
	$("#archive-btn").click(function(e) {
		e.preventDefault();

		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${archiveUrl}",
			data: JSON.stringify({ items: [ ${Creative.id} ] }),
			success: function (form) {
				showOperationSuccessMsg();
				reload();
			},
			error: ajaxOperationError
		});
	});
	// / Archive
	
	// Unarchive
	$("#unarchive-btn").click(function(e) {
		e.preventDefault();

		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${unarchiveUrl}",
			data: JSON.stringify({ items: [ ${Creative.id} ] }),
			success: function (form) {
				showOperationSuccessMsg();
				reload();
			},
			error: ajaxOperationError
		});
	});
	// / Unarchive
	
	// Pause
	$("#pause-btn").click(function(e) {
		e.preventDefault();

		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${pauseUrl}",
			data: JSON.stringify({ items: [ ${Creative.id} ] }),
			success: function (form) {
				showOperationSuccessMsg();
				reload();
			},
			error: ajaxOperationError
		});
	});
	// / Pause
	
	// Resume
	$("#resume-btn").click(function(e) {
		e.preventDefault();

		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${resumeUrl}",
			data: JSON.stringify({ items: [ ${Creative.id} ] }),
			success: function (form) {
				showOperationSuccessMsg();
				reload();
			},
			error: ajaxOperationError
		});
	});
	// / Resume
	
	// Delete
	$("#delete-btn").click(function(e) {
		e.preventDefault();

		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${destroyUrl}",
			data: JSON.stringify({ items: [ ${Creative.id} ] }),
			success: function (form) {
				showOperationSuccessMsg();
				reload();
			},
			error: ajaxOperationError
		});
	});
	// / Delete
	
});	
</script>

<!-- / Form button actions  -->


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
						광고 소재명
						<span class="text-danger">*</span>
					</label>
					<input name="name" type="text" maxlength="100" class="form-control required">
				</div>
				<div class="form-group col">
					<label class="form-label">
						광고주
					</label>
					<input type="text" class="form-control" value="${Advertiser.name}" readonly="readonly">
				</div>
				<div class="form-group col">
					<label class="form-label">
						소재 유형
					</label>
					<select name="type" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
						<option value="C" data-icon="fa-regular fa-audio-description fa-fw mr-1">일반 광고</option>
						<option value="F" data-icon="fa-regular fa-house fa-fw text-orange mr-1">대체 광고</option>
					</select>
				</div>
				<div class="form-group col">
					<label class="form-label">
						화면의 재생시간 정책
					</label>
					<div>
						<label class="switcher">
							<input type="checkbox" class="switcher-input check-switch-status" name="durPolicyOverriden">
							<span class="switcher-indicator">
								<span class="switcher-yes"></span>
								<span class="switcher-no"></span>
							</span>
							<span class="switcher-label">매체 화면의 재생시간 정책 무시 허용</span>
						</label>
					</div>
				</div>
				<div class="form-group col">
					<label class="form-label">
						대체 광고간 가중치
					</label>
					<input name="fbWeight" type="text" class="form-control required" value="1">
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

<style>

/* 폼에서 열 사이의 간격을 원래대로 넓게 */
.form-row>.col, .form-row>[class*="col-"] {
	padding-right: .75rem;
	padding-left: .75rem;
}


/* 텍스트 크기 표준보다는 작게 */
.text-large {
    font-size: 130% !important;
}


/* 링크 텍스트 표시 */
.text-link {
	text-decoration: underline;
	text-decoration-color: #cccccc !important;
	text-underline-offset: 5px;
}

</style>

<!--  / Forms -->


<!--  Scripts -->

<script>

function dispResoBadgeValues(values) {
	
	var ret = "";
	var value = values.split("|");
	  
	for(var i = 0; i < value.length; i ++) {
		if (value[i]) {
			var item = value[i].split(":");
			if (item.length == 2) {
				if (Number(item[0]) == 1) {
					ret = ret + "<small><span class='badge badge-outline-success'>";
				} else if (Number(item[0]) == 0) {
					ret = ret + "<small><span class='badge badge-outline-secondary'>";
				} else {
					ret = ret + "<small><span class='badge badge-outline-danger'>";
				}
				
				ret = ret + "<span class='text-small'>" + item[1] + "</span></span></small><span class='pl-1'></span>";
			}
		}
	}
	  
	return ret;
}


function initFallbackAdAttribute() {
	
	$("#form-1 input[name='durPolicyOverriden']").prop("disabled", true);
	$("#form-1 input[name='fbWeight']").prop("disabled", true);
	
	$("#form-1 input[name='durPolicyOverriden']").prop("checked", false);
	$("#form-1 input[name='fbWeight']").val("1");
}


function initForm1(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-1").html()));
	
	$("#form-1 select[name='type']").selectpicker('render');
	
	$("#form-1 select[name='type']").on("changed.bs.select", 
		function (e, clickedIndex, isSelected, previousValue) {
			var val = $("#form-1 select[name='type']").val();
			
			if (val == "C") {
				// 일반 광고
				initFallbackAdAttribute();
			} else if (val == "F") {
				// 대체 광고
				$("#form-1 input[name='durPolicyOverriden']").prop("disabled", false);
				$("#form-1 input[name='fbWeight']").prop("disabled", false);
			}
	});

	initFallbackAdAttribute();
	
	
	$("#form-1 span[name='subtitle']").text(subtitle ? subtitle : "추가");

	$("#form-1").validate({
		rules: {
			name: {
				minlength: 2,
			},
			fbWeight: {
				digits: true,
				min: 1,
			},
		}
	});
}


function saveForm1() {

	if ($("#form-1").valid()) {
    	var data = {
    		id: Number($("#form-1").attr("rowid")),
    		name: $.trim($("#form-1 input[name='name']").val()),
    		advertiser: ${Advertiser.id},
    		type: $("#form-1 select[name='type']").val(),
    		durPolicyOverriden: $("#form-1 input[name='durPolicyOverriden']").is(':checked'),
    		fbWeight: Number($.trim($("#form-1 input[name='fbWeight']").val())),
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
				reload();
			},
			error: ajaxSaveError
		});
	}
}


function edit() {
	
	initForm1("변경");

	
	$("#form-1").attr("rowid", ${Creative.id});
	$("#form-1").attr("url", "${updateUrl}");
	
	$("#form-1 input[name='name']").val("${Creative.name}");
	$("#form-1 input[name='fbWeight']").val(${Creative.fbWeight});

	bootstrapSelectVal($("#form-1 select[name='type']"), "${Creative.type}");
	
	$("#form-1 input[name='durPolicyOverriden']").prop("checked", ${Creative.durPolicyOverriden});
	
	if ("${Creative.type}" == "F") {
		$("#form-1 input[name='durPolicyOverriden']").prop("disabled", false);
		$("#form-1 input[name='fbWeight']").prop("disabled", false);
	} else {
		$("#form-1 input[name='durPolicyOverriden']").prop("disabled", true);
		$("#form-1 input[name='fbWeight']").prop("disabled", true);
	}

	
	$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-1").modal();
}


function reload() {
	
	setTimeout(function(){
		location.reload();
	}, 1000);
}

</script>

<!--  / Scripts -->


</c:otherwise>
</c:choose>

<!--  / Page details -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
