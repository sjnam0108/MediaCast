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

<c:url value="/adc/ad/update" var="updateUrl" />
<c:url value="/adc/ad/approve" var="approveUrl" />
<c:url value="/adc/ad/reject" var="rejectUrl" />
<c:url value="/adc/ad/archive" var="archiveUrl" />
<c:url value="/adc/ad/unarchive" var="unarchiveUrl" />
<c:url value="/adc/ad/pause" var="pauseUrl" />
<c:url value="/adc/ad/resume" var="resumeUrl" />
<c:url value="/adc/ad/destroy" var="destroyUrl" />


<!-- Opening tags -->

<common:mediumPageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 fa-light fa-microscope fa-fw"></span>
	${pageTitle}
	<span class="font-weight-light pl-1">광고 상세</span>
</h4>

<hr class="border-light container-m--x mt-0 mb-4">





<!-- Page body -->


<!-- Page scripts  -->

<link rel="stylesheet" href="/resources/vendor/lib/bootstrap-slider/bootstrap-slider.css">

<script>$.fn.slider = null</script>
<script type="text/javascript" src="/resources/vendor/lib/bootstrap-slider/bootstrap-slider.js"></script>


<!--  Overview header -->

<adc:campaign />

<!--  / Overview header -->


<!--  Tab -->

<ul class="nav nav-tabs tabs-alt mb-4 mt-3">
	<li class="nav-item">
		<a class="nav-link" href="/adc/campaign/ads/${Campaign.id}">
			<i class="mr-1 fa-light fa-audio-description"></i>
			광고 목록
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link active" href="/adc/campaign/detail/${Campaign.id}">
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
		<a class="nav-link" href="/adc/campaign/hrlyplays/${Campaign.id}">
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
		location.href = "/adc/campaign/detail/${Campaign.id}/" + $("select[name='nav-item-ad-select']").val();
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


<div class="card">
	<h6 class="card-header with-elements pl-2">
		<span class="lead">
			<span class="fa-stack fa-xs" title="광고" style="vertical-align: top; color: #a0a0a0;">
				<span class="fa-solid fa-circle fa-stack-2x"></span>
				<span class="fa-solid fa-audio-description fa-stack-1x fa-inverse"></span>
			</span>
			<span id="ad-name"></span>
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
					<a class="dropdown-item" href="javascript:void(0)" id="approve-btn">
						<i class="fa-light fa-thumbs-up fa-fw"></i><span class="pl-2">승인</span>
					</a>
					<a class="dropdown-item" href="javascript:void(0)" id="reject-btn">
						<i class="fa-light fa-thumbs-down fa-fw"></i><span class="pl-2">거절</span>
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
		<div class="col-sm-6 col-md-4">
			<div class="d-flex align-items-center container-p-x py-4">
			
<c:choose>
<c:when test="${Ad.status == 'D'}">

				<span class="fa-thin fa-asterisk fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">광고 상태</div>
					<div class="text-large">준비</div>
				</div>
				
</c:when>
<c:when test="${Ad.status == 'P'}">

				<span class="fa-thin fa-quare-question fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">광고 상태</div>
					<div class="text-large">승인대기</div>
				</div>
				
</c:when>
<c:when test="${Ad.status == 'J'}">

				<span class="fa-thin fa-do-not-enter fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">광고 상태</div>
					<div class="text-large">거절</div>
				</div>
				
</c:when>
<c:when test="${Ad.status == 'A'}">

				<span class="fa-thin fa-alarm-clock fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">광고 상태</div>
					<div class="text-large">예약</div>
				</div>
				
</c:when>
<c:when test="${Ad.status == 'R'}">

				<span class="fa-thin fa-bolt-lightning fa-3x text-orange fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">광고 상태</div>
					<div class="text-large">진행</div>
				</div>
				
</c:when>
<c:when test="${Ad.status == 'C'}">

				<span class="fa-thin fa-flag-checkered fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">광고 상태</div>
					<div class="text-large">완료</div>
				</div>
				
</c:when>
<c:when test="${Ad.status == 'V'}">

				<span class="fa-thin fa-box-archive  fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">광고 상태</div>
					<div class="text-large">보관</div>
				</div>
				
</c:when>
</c:choose>
			
			</div>
		</div>
		<div class="col-sm-6 col-md-4">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-calendar-range fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">광고 기간</div>
					<div class="text-large">
		
	<c:choose>
	<c:when test="${Ad.dispPeriod eq Campaign.dispPeriod}">
			
						캠페인과 동일
						
	</c:when>
	<c:otherwise>
	
						${Ad.dispPeriod}
						
	</c:otherwise>
	</c:choose>
		
					</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4">
			<div class="d-flex align-items-center container-p-x py-4">
			
	<c:choose>
	<c:when test="${Ad.purchType == 'G'}">

				<span class="fa-thin fa-hexagon-check fa-3x text-blue fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">구매 유형</div>
					<div class="text-large">목표 보장.${Ad.priority}</div>
				</div>
				
	</c:when>
	<c:when test="${Ad.purchType == 'N'}">

				<span class="fa-thin fa-hexagon-exclamation fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">구매 유형</div>
					<div class="text-large">목표 비보장.${Ad.priority}</div>
				</div>
				
	</c:when>
	<c:when test="${Ad.purchType == 'H'}">

				<span class="fa-thin fa-house fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">구매 유형</div>
					<div class="text-large">하우스 광고</div>
				</div>
				
	</c:when>
	</c:choose>
			
			</div>
		</div>


<c:if test="${Ad.purchType ne 'H'}">
		
		<div class="col-sm-6 col-md-4">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-sack-dollar fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">광고 예산</div>
					
		<c:choose>
		<c:when test="${Ad.budget == 0}">
					
					<div class="text-large">설정 없음</div>

		</c:when>
		<c:otherwise>
					
					<div class="text-large"><span id="ad-budget"></span> 원</div>

		</c:otherwise>
		</c:choose>
					
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-hexagon-check fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">보장 노출횟수</div>
					
		<c:choose>
		<c:when test="${Ad.goalValue == 0}">
					
					<div class="text-large">설정 없음</div>

		</c:when>
		<c:otherwise>
					
					<div class="text-large"><span id="ad-goal-value"></span> 회</div>

		</c:otherwise>
		</c:choose>
					
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-golf-flag-hole fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">목표 노출횟수</div>
					
		<c:choose>
		<c:when test="${Ad.sysValue == 0}">
					
					<div class="text-large">설정 없음</div>

		</c:when>
		<c:otherwise>
					
					<div class="text-large"><span id="ad-sys-value"></span> 회</div>

		</c:otherwise>
		</c:choose>
					
				</div>
			</div>
		</div>
		

		<div class="col-sm-6 col-md-4">
			<div class="d-flex align-items-center container-p-x py-4">
			
	<c:choose>
	<c:when test="${Ad.goalType == 'A'}">

				<span class="fa-thin fa-sack-dollar fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">집행 방법</div>
					<div class="text-large">광고 예산</div>
				</div>

	</c:when>
	<c:when test="${Ad.goalType == 'I'}">

				<span class="fa-thin fa-eye fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">집행 방법</div>
					<div class="text-large">노출횟수</div>
				</div>

	</c:when>
	<c:when test="${Ad.goalType == 'J'}">

				<span class="fa-thin fa-eye fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">집행 방법</div>
					<div class="text-large">화면당 1일 노출</div>
				</div>
				
	</c:when>
	<c:when test="${Ad.goalType == 'U'}">

				<span class="fa-thin fa-infinity fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">집행 방법</div>
					<div class="text-large">무제한 노출</div>
				</div>

	</c:when>
	</c:choose>
					
			</div>
		</div>
		
</c:if>

		
		<div class="col-sm-6 col-md-4">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-arrow-right-to-line fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">하루 노출한도</div>
					
		<c:choose>
		<c:when test="${Ad.dailyCap == 0}">
					
					<div class="text-large">설정 없음</div>

		</c:when>
		<c:otherwise>
					
					<div class="text-large"><span id="ad-daily-cap"></span> 회</div>

		</c:otherwise>
		</c:choose>
					
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-arrow-right-to-line fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">화면당 하루 노출한도</div>
					
		<c:choose>
		<c:when test="${Ad.dailyScrCap == 0}">
					
					<div class="text-large">설정 없음</div>

		</c:when>
		<c:otherwise>
					
					<div class="text-large"><span id="ad-daily-scr-cap"></span> 회</div>

		</c:otherwise>
		</c:choose>
					
				</div>
			</div>
		</div>
		
		<div class="col-sm-6 col-md-4">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-hand fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">동일 광고 송출 금지</div>
					
		<c:choose>
		<c:when test="${Ad.freqCap == 0}">
					
					<div class="text-large">설정 없음</div>

		</c:when>
		<c:otherwise>
					
					<div class="text-large"><span id="ad-freq-cap"></span> 초</div>

		</c:otherwise>
		</c:choose>
					
				</div>
			</div>
		</div>
		
<c:if test="${Ad.purchType ne 'H'}">
		
		<div class="col-sm-6 col-md-4">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-coin fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">CPM</div>
					
		<c:choose>
		<c:when test="${Ad.cpm == 0}">
					
					<div class="text-large">화면 설정값</div>

		</c:when>
		<c:otherwise>
					
					<div class="text-large"><span id="ad-cpm"></span> 원</div>

		</c:otherwise>
		</c:choose>
					
				</div>
			</div>
		</div>
		
</c:if>

		<div class="col-sm-6 col-md-4">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-timer fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">재생 시간</div>
					
		<c:choose>
		<c:when test="${Ad.duration == 0}">
					
					<div class="text-large">화면 설정값</div>

		</c:when>
		<c:otherwise>
					
					<div class="text-large"><span id="ad-duration"></span> 초</div>

		</c:otherwise>
		</c:choose>
					
				</div>
			</div>
		</div>
		
	</div>
</div>


<!-- Form button actions  -->

<script>
$(document).ready(function() {

	var title = "";
	if (${Ad.paused} == true) {
		title = "<span title='잠시 멈춤'><span class='fa-light fa-circle-pause text-danger'></span></span><span class='pr-1'></span>";
	}
	title = title + "<span>${Ad.name}</span>";
	if (${Ad.invenTargeted} == true) {
		title = title + "<span class='pl-1'></span><span title='인벤토리 타겟팅'><span class='fa-light fa-bullseye-arrow text-blue'></span></span>";
	}
	if (${Ad.timeTargeted} == true) {
		title = title + "<span class='pl-1'></span><span title='시간 타겟팅'><span class='fa-light fa-alarm-clock text-green'></span></span>";
	}
	if ('${Ad.statusCard}' == 'Y') {
		title = title + "<span class='pl-1'></span><span title='오늘 진행되는 광고 소재 없음'><span class='fa-solid fa-rectangle-vertical text-yellow'></span></span>";
	} else if ('${Ad.statusCard}' == 'R') {
		title = title + "<span class='pl-1'></span><span title='활성중인 광고 소재 없음'><span class='fa-solid fa-rectangle-vertical text-danger'></span></span>";
	}
	
	var resolutions = "<span class='pl-3'>" + dispResoBadgeValues("${Ad.resolutions}") + "</span>";
	$("#ad-name").html(title + resolutions);
	
	
	var regexp = /\B(?=(\d{3})+(?!\d))/g;
	$("#ad-budget").html("${Ad.budget}".replace(regexp, ','));
	$("#ad-goal-value").html("${Ad.goalValue}".replace(regexp, ','));
	$("#ad-sys-value").html("${Ad.sysValue}".replace(regexp, ','));
	$("#ad-daily-cap").html("${Ad.dailyCap}".replace(regexp, ','));
	$("#ad-daily-scr-cap").html("${Ad.dailyScrCap}".replace(regexp, ','));
	$("#ad-freq-cap").html("${Ad.freqCap}".replace(regexp, ','));
	$("#ad-cpm").html("${Ad.cpm}".replace(regexp, ','));
	$("#ad-duration").html("${Ad.duration}".replace(regexp, ','));

	
	// Edit
	$("#edit-btn").click(function(e) {
		e.preventDefault();
		
		edit();
	});
	// / Edit
	
	// Approve
	$("#approve-btn").click(function(e) {
		e.preventDefault();

		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${approveUrl}",
			data: JSON.stringify({ items: [ ${Ad.id} ] }),
			success: function (form) {
				showOperationSuccessMsg();
				reload();
			},
			error: ajaxOperationError
		});
	});
	// / Approve
	
	// Reject
	$("#reject-btn").click(function(e) {
		e.preventDefault();
			
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${rejectUrl}",
			data: JSON.stringify({ items: [ ${Ad.id} ] }),
			success: function (form) {
				showOperationSuccessMsg();
				reload();
			},
			error: ajaxOperationError
		});
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
			data: JSON.stringify({ items: [ ${Ad.id} ] }),
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
			data: JSON.stringify({ items: [ ${Ad.id} ] }),
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
			data: JSON.stringify({ items: [ ${Ad.id} ] }),
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
			data: JSON.stringify({ items: [ ${Ad.id} ] }),
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
			data: JSON.stringify({ items: [ ${Ad.id} ] }),
			success: function (form) {
				showDeleteSuccessMsg();
				reload();
			},
			error: ajaxDeleteError
		});
	});
	// / Delete
	
});	
</script>

<!-- / Form button actions  -->


<!--  Forms -->

<script id="template-1" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-1">
	<div class="modal-dialog modal-lg">
		<form class="modal-content" id="form-1" rowid="-1" url="${createUrl}">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					광고
					<span class="font-weight-light pl-1"><span name="subtitle"></span></span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
				<div class="form-row">
					<div class="form-group col">
						<label class="form-label">
							광고명
							<span class="text-danger">*</span>
						</label>
						<input name="name" type="text" maxlength="100" class="form-control required">
					</div>
					<div class="form-group col">
						<label class="form-label">
							캠페인
							<span class="text-danger">*</span>
						</label>
						<input type="text" class="form-control" value="${Campaign.name}" readonly="readonly">
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


function validatePurchType() {

	if ($("#form-1 select[name='purchType']").val() == "H") {
		$("#form-1 div[name='purch-type-depending-div']").hide();
	} else {
		$("#form-1 div[name='purch-type-depending-div']").show();
	}
}


function validateDurationType() {
	
	if ($("#form-1 select[name='durationType']").val() == "0") {
		$("#form-1 div[name='duration-value-div']").hide();
		$("#form-1 input[name='durSecs']").attr('readonly', 'readonly');
	} else {
		$("#form-1 input[name='durSecs']").removeAttr('readonly');
		$("#form-1 div[name='duration-value-div']").show();
		$("#form-1 input[name='durSecs']").select();
		$("#form-1 input[name='durSecs']").focus();
	}
}


function validateCpmType() {
	
	if ($("#form-1 select[name='cpmType']").val() == "0") {
		$("#form-1 div[name='cpm-value-div']").hide();
		$("#form-1 input[name='cpm']").attr('readonly', 'readonly');
	} else {
		$("#form-1 input[name='cpm']").removeAttr('readonly');
		$("#form-1 div[name='cpm-value-div']").show();
		$("#form-1 input[name='cpm']").select();
		$("#form-1 input[name='cpm']").focus();
	}
}


function initForm1(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-1").html()));
	
	$("#form-1 select[name='campaign']").selectpicker('render');
	$("#form-1 select[name='purchType']").selectpicker('render');
	$("#form-1 select[name='durationType']").selectpicker('render');
	$("#form-1 select[name='goalType']").selectpicker('render');
	$("#form-1 select[name='cpmType']").selectpicker('render');
	
	var today = new Date();
	today.setHours(0, 0, 0, 0);
	
	var start = new Date();
	var end = new Date();
	start.setDate(today.getDate() + 2);
	end.setDate(today.getDate() + 31);
	
	$("#form-1 input[name='startDate']").kendoDatePicker({
		format: "yyyy-MM-dd",
		parseFormats: [
			"yyyy-MM-dd",
		],
		value: start,
		//min: today,
	});
	
	$("#form-1 input[name='endDate']").kendoDatePicker({
		format: "yyyy-MM-dd",
		parseFormats: [
			"yyyy-MM-dd",
		],
		value: end,
		//min: today,
	});
	
	$("#form-1 select[name='purchType']").on("change.bs.select", function(e){
		validatePurchType();
	});
	
	$("#form-1 select[name='durationType']").on("change.bs.select", function(e){
		validateDurationType();
	});
	
	$("#form-1 select[name='cpmType']").on("change.bs.select", function(e){
		validateCpmType();
	});
	
	$(".input-change").change(function(){
		// 예산, 보장 노출횟수, 목표 노출횟수
		var budget = Number($.trim($("#form-1 input[name='budget']").val()));
		var goalValue = Number($.trim($("#form-1 input[name='goalValue']").val()));
		var sysValue = Number($.trim($("#form-1 input[name='sysValue']").val()));
		
	    if (goalValue > 0 || sysValue > 0) {
	    	bootstrapSelectVal($("#form-1 select[name='goalType']"), "I");
	    } else if (budget > 0) {
	    	bootstrapSelectVal($("#form-1 select[name='goalType']"), "A");
	    } else {
	    	bootstrapSelectVal($("#form-1 select[name='goalType']"), "U");
	    }
	});

	
	bootstrapSelectVal($("#form-1 select[name='campaign']"), "-1");
	bootstrapSelectVal($("#form-1 select[name='goalType']"), "U");
	
	$("#form-1 textarea[name='memo']").keypress(function (e) {
		if (e.keyCode != 13) {
			return;
		}
		
		$(this).text().replace(/\n/g, "");
		
		return false;
	});
	
	$('[data-toggle="tooltip"]').tooltip();

	
	if (subtitle == null) {		
		// ADD 모드
		$("#form-1 input[name='startDate']").data("kendoDatePicker").min(today);
		$("#form-1 input[name='endDate']").data("kendoDatePicker").min(today);
	}

	$("#form-1 input[name='priority']").slider();
	

	$("#form-1 span[name='subtitle']").text(subtitle ? subtitle : "추가");
	

	$("#form-1").validate({
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
}


function saveForm1() {

	// kendo datepicker validation
	validateKendoDateValue($("#form-1 input[name='startDate']"));
	validateKendoDateValue($("#form-1 input[name='endDate']"));

	if ($("#form-1").valid()) {
		
		var goAhead = true;
		
		var durationType = $("#form-1 select[name='durationType']").val();
		var durSecs = Number($.trim($("#form-1 input[name='durSecs']").val()));
		if (durationType == "0") {
			durSecs = 0;
		} else {
			goAhead = durSecs >= 5;
		}
		
		var cpmType = $("#form-1 select[name='cpmType']").val();
		var cpm = Number($.trim($("#form-1 input[name='cpm']").val()));
		if (cpmType == "0") {
			cpm = 0;
		} else {
			goAhead = goAhead && cpm >= 1;
		}
		
    	var data = {
    		id: Number($("#form-1").attr("rowid")),
    		name: $.trim($("#form-1 input[name='name']").val()),
    		campaign: ${Campaign.id},
    		purchType: $("#form-1 select[name='purchType']").val(),
    		priority: Number($.trim($("#form-1 input[name='priority']").val())),
    		startDate: $("#form-1 input[name='startDate']").data("kendoDatePicker").value(),
    		endDate: $("#form-1 input[name='endDate']").data("kendoDatePicker").value(),
    		freqCap: Number($("#form-1 input[name='freqCap']").val()),
    		cpm: Number($("#form-1 input[name='cpm']").val()),
    		durSecs: durSecs,
    		goalType: $("#form-1 select[name='goalType']").val(),
    		goalValue: Number($.trim($("#form-1 input[name='goalValue']").val())),
    		sysValue: Number($("#form-1 input[name='sysValue']").val()),
    		budget: Number($("#form-1 input[name='budget']").val()),
    		dailyCap: Number($("#form-1 input[name='dailyCap']").val()),
    		dailyScrCap: Number($("#form-1 input[name='dailyScrCap']").val()),
    		cpm: cpm,
    		memo: $.trim($("#form-1 textarea[name='memo']").val()),
    	};
    	
    	if (goAhead) {

    		$.ajax({
    			type: "POST",
    			contentType: "application/json",
    			dataType: "json",
    			url: $("#form-1").attr("url"),
    			data: JSON.stringify(data),
    			success: function (data, status, xhr) {
    				
    				showSaveSuccessMsg();
    				$("#form-modal-1").modal("hide");

    				reload();
    			},
    			error: ajaxSaveError
    		});
    	}
	}
}


function edit() {
	
	initForm1("변경");

	var startDate = new Date(${Ad.startDateLong});
	var endDate = new Date(${Ad.endDateLong});
	
	
	$("#form-1").attr("rowid", ${Ad.id});
	$("#form-1").attr("url", "${updateUrl}");
	
	$("#form-1 input[name='name']").val("${Ad.name}");
	$("#form-1 input[name='freqCap']").val("${Ad.freqCap}");
	$("#form-1 input[name='goalValue']").val("${Ad.goalValue}");
	$("#form-1 input[name='dailyCap']").val("${Ad.dailyCap}");

	$("#form-1 input[name='budget']").val(${Ad.budget});
	$("#form-1 input[name='sysValue']").val(${Ad.sysValue});
	$("#form-1 input[name='dailyScrCap']").val(${Ad.dailyScrCap});

	bootstrapSelectVal($("#form-1 select[name='purchType']"), "${Ad.purchType}");
	bootstrapSelectVal($("#form-1 select[name='goalType']"), "${Ad.goalType}");
	
	$("#form-1 input[name='startDate']").data("kendoDatePicker").value(startDate);
	$("#form-1 input[name='endDate']").data("kendoDatePicker").value(endDate);

	$("#form-1 textarea[name='memo']").text("${Ad.memo}");
	
	bootstrapSelectDisabled($("#form-1 select[name='campaign']"), true);
	

	// 시작일과 종료일에 대해 오늘 날짜 비교하여 더 작은 값을 min으로, 더 큰 값을 max로 처리
	var today = new Date();
	today.setHours(0, 0, 0, 0);
	
	if (startDate < today) {
		$("#form-1 input[name='startDate']").data("kendoDatePicker").min(startDate);
		$("#form-1 input[name='endDate']").data("kendoDatePicker").min(startDate);
	}

	$("#form-1 input[name='startDate']").data("kendoDatePicker").enable(${Ad.startDateEditable});
	
	$("#form-1 input[name='priority']").slider("setValue", [${Ad.priority}]);
	
	if (${Ad.duration} == 0) {
		bootstrapSelectVal($("#form-1 select[name='durationType']"), "0");
		$("#form-1 input[name='durSecs']").val("15");		// 초기값으로 15
	} else {
		bootstrapSelectVal($("#form-1 select[name='durationType']"), "5");
		$("#form-1 input[name='durSecs']").val("${Ad.duration}");
	}
	validateDurationType();
	
	if (${Ad.cpm} == 0) {
		bootstrapSelectVal($("#form-1 select[name='cpmType']"), "0");
		$("#form-1 input[name='cpm']").val("1000");		// 초기값으로 1000
	} else {
		bootstrapSelectVal($("#form-1 select[name='cpmType']"), "5");
		$("#form-1 input[name='cpm']").val("${Ad.cpm}");
	}
	validateCpmType();
	
	validatePurchType();

	
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
