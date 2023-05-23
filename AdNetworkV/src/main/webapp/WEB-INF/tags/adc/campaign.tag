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
	
});


function navToAdv(advId) {
	var path = "/adc/creative/creatives/" + advId;
	location.href = path;
}

</script>

<!-- / Form button actions  -->

<!--  / HTML tags -->

