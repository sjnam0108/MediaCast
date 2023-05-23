<%@ tag pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!--  HTML tags -->

<div class="card">
	<h6 class="card-header with-elements pl-2">
		<span class="lead">
			<span class="fa-stack fa-xs" title="캠페인" style="vertical-align: top; color: #a0a0a0;">
				<span class="fa-solid fa-circle fa-stack-2x"></span>
				<span class="fa-solid fa-briefcase fa-stack-1x fa-inverse fa-lg"></span>
			</span>
			<span id="campaign-name"></span>
		</span>
		<div class="card-header-elements ml-auto">
		</div>
	</h6>
	<div class="row no-gutters row-bordered row-border-light">
		<div class="col-sm-6 col-md-4">
			<div class="d-flex align-items-center container-p-x py-4">
			
<c:choose>
<c:when test="${Campaign.status == 'U'}">

				<span class="fa-thin fa-alarm-clock fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">캠페인 상태</div>
					<div class="text-large">예약</div>
				</div>
				
</c:when>
<c:when test="${Campaign.status == 'R'}">

				<span class="fa-thin fa-bolt-lightning fa-3x text-orange fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">캠페인 상태</div>
					<div class="text-large">진행</div>
				</div>
				
</c:when>
<c:when test="${Campaign.status == 'C'}">

				<span class="fa-thin fa-flag-checkered fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">캠페인 상태</div>
					<div class="text-large">완료</div>
				</div>
				
</c:when>
<c:when test="${Campaign.status == 'V'}">

				<span class="fa-thin fa-box-archive  fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">캠페인 상태</div>
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
					<div class="text-muted small">캠페인 기간</div>
					<div class="text-large">
						${Campaign.dispPeriod}
					</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-user-tie-hair fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">광고주</div>
					<div class="text-large">
						${Campaign.advertiser.name}
						<a href='javascript:navToAdv(${Campaign.advertiser.id})' class='btn btn-default btn-xs icon-btn ml-1'>
						<span class='fa-regular fa-arrow-right'></span>
						</a>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

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
		
<c:if test="${Ad.purchType eq 'H'}">
		
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
		
</c:if>

	</div>
</div>

<!-- Form button actions  -->

<script>
$(document).ready(function() {

	var campTitle = "<span>${Campaign.name}</span>";
	if ('${Campaign.statusCard}' == 'Y') {
		campTitle = campTitle + "<span class='pl-1'></span><span title='오늘 진행되는 광고 소재 없음'><span class='fa-solid fa-rectangle-vertical text-yellow'></span></span>";
	} else if ('${Campaign.statusCard}' == 'R') {
		campTitle = campTitle + "<span class='pl-1'></span><span title='활성중인 광고 소재 없음'><span class='fa-solid fa-rectangle-vertical text-danger'></span></span>";
	}
	
	$("#campaign-name").html(campTitle);
	

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
	
});


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


function navToAdv(advId) {
	var path = "/adc/creative/creatives/" + advId;
	location.href = path;
}

</script>

<!-- / Form button actions  -->

<!--  / HTML tags -->

