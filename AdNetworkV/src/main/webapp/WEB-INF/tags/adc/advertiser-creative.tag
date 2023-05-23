<%@ tag pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!--  HTML tags -->

<div class="card">
	<h6 class="card-header with-elements pl-2 py-1">
		<span class="lead">
			<span class="fa-stack fa-xs" title="광고주" style="vertical-align: top; color: #a0a0a0;">
				<span class="fa-solid fa-circle fa-stack-2x"></span>
				<span class="fa-solid fa-user-tie-hair fa-stack-1x fa-inverse fa-lg" data-fa-transform="up-1"></span>
			</span>
			<span id="advertiser-name">${Advertiser.name}</span>
		</span>
		<div class="card-header-elements ml-auto">
			<span class="lead">${Advertiser.domainName}</span>
		</div>
	</h6>
	<div class="row no-gutters row-bordered row-border-light">
		<div class="d-none d-md-inline" style="width: 220px">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-photo-film fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">총 소재 / 파일 수</div>
					<div>
						<span id="adv-creat-count" class="text-large"></span>
						<span class="text-muted">/</span>
						<span id="adv-creat-file-count" class="text-large"></span>
					</div>
				</div>
			</div>
		</div>
		<div class="col">
			
			<div class="table-responsive">
				<table class="table card-table m-0 k-grid">
					<tbody>
						<tr>
							<td class="align-middle w-100">
								<span id="table-adv-0-name"></span>
							</td>
							<td class="text-nowrap align-middle text-center" style="min-width: 100px;">
								<span id="table-adv-0-status"></span>
							</td>
							<td class="text-nowrap align-middle text-center" style="min-width: 180px;">
								<span id="table-adv-0-period"></span>
							</td>
						</tr>
						<tr>
							<td class="align-middle">
								<span id="table-adv-1-name"></span>
							</td>
							<td class="text-nowrap align-middle text-center" style="min-width: 100px;">
								<span id="table-adv-1-status"></span>
							</td>
							<td class="text-nowrap align-middle text-center" style="min-width: 180px;">
								<span id="table-adv-1-period"></span>
							</td>
						</tr>
						<tr>
							<td class="align-middle">
								<span id="table-adv-2-name"></span>
							</td>
							<td class="text-nowrap align-middle text-center" style="min-width: 100px;">
								<span id="table-adv-2-status"></span>
							</td>
							<td class="text-nowrap align-middle text-center" style="min-width: 180px;">
								<span id="table-adv-2-period"></span>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
			
		</div>
	</div>
</div>

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

	var regexp = /\B(?=(\d{3})+(?!\d))/g;
	$("#adv-creat-count").html("${CreatCount}".replace(regexp, ','));
	$("#adv-creat-file-count").html("${CreatFileCount}".replace(regexp, ','));

	
	var item00 = ${Camp01};
	var item01 = ${Camp02};
	var item02 = ${Camp03};
	
	$("#table-adv-0-name").html(getAdvCampaignName(item00));
	$("#table-adv-0-status").html(getAdvCampaignStatus(item00));
	$("#table-adv-0-period").html(getAdvCampaignPeriod(item00));

	$("#table-adv-1-name").html(getAdvCampaignName(item01));
	$("#table-adv-1-status").html(getAdvCampaignStatus(item01));
	$("#table-adv-1-period").html(getAdvCampaignPeriod(item01));

	$("#table-adv-2-name").html(getAdvCampaignName(item02));
	$("#table-adv-2-status").html(getAdvCampaignStatus(item02));
	$("#table-adv-2-period").html(getAdvCampaignPeriod(item02));

	
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


function getAdvCampaignName(item) {
	
	if (item == null) {
		return "";
	}
	
	var ret = "";
	ret = ret + "<div>";
	ret = ret +   "<span class='fa-regular fa-briefcase' title='캠페인'></span>";
	ret = ret +   "<span class='pr-1'></span>";
	ret = ret +   "<a href='javascript:navToCampaign(" + item.id + ")'><span class='text-link'>" + item.name + "</span></a>";
	
	if (item.statusCard == 'Y') {
		ret = ret + "<span class='pl-1'></span><span title='오늘 진행되는 광고 소재 없음'><span class='fa-solid fa-rectangle-vertical text-yellow'></span></span>";
	} else if (item.statusCard == 'R') {
		ret = ret + "<span class='pl-1'></span><span title='활성중인 광고 소재 없음'><span class='fa-solid fa-rectangle-vertical text-danger'></span></span>";
	}
	ret = ret + "</div>";
	
	return ret;
}


function getAdvCampaignStatus(item) {
	
	if (item == null) {
		return "";
	}
	
	var ret = "";
	
	if (item.status == "U") {
		ret = "<span class='fa-regular fa-alarm-clock'></span><span class='pl-2'>시작전</span>";
	} else if (item.status == "R") {
		ret = "<span class='fa-regular fa-bolt-lightning text-orange'></span><span class='pl-2'>진행</span>";
	} else if (item.status == "C") {
		ret = "<span class='fa-regular fa-flag-checkered'></span><span class='pl-2'>완료</span>";
	} else {
		ret = "<span>-</span>";
	}
	
	return ret;	
}


function getAdvCampaignPeriod(item) {
	
	if (item == null) {
		return "";
	}
	
	return item.dispPeriod;
}


function navToCampaign(id) {
	var path = "/adc/campaign/" + id;
	location.href = path;
}

</script>



<style>

/* 그리드 행의 높이 지정 */
.card-table tbody tr, .k-grid tbody tr td
{
    height: 40px;
}

</style>

<!-- / Form button actions  -->


<!--  / HTML tags -->

