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
});	


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

