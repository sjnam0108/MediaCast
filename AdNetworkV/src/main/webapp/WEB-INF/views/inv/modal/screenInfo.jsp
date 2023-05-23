<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/adn/common/readScreenInfo" var="readScreenInfoUrl" />
<c:url value="/adn/common/readScreenPlayHist" var="readScreenPlayHistUrl" />





<!-- Page body -->


<!--  Forms -->

<ul class="nav nav-tabs tabs-alt mb-3 pt-0" id="scr-summary-tabs">
	<li class="nav-item">
		<a class="nav-link active" data-toggle="tab" href="#status-line-chart">
			<i class="d-none d-sm-inline mr-1 fa-light fa-chart-pie"></i>
			상태
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#api-request">
			<i class="d-none d-sm-inline mr-1 fa-light fa-plug"></i>
			API
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#info-screen">
			<span class="d-none d-sm-inline mr-1 fa-light fa-screen-users"></span>
			화면
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#info-site">
			<i class="d-none d-sm-inline mr-1 fa-light fa-map-pin"></i>
			사이트
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#map-loc" id="summary-map-loc">
			<i class="d-none d-sm-inline mr-1 fa-light fa-map"></i>
			위치
		</a>
	</li>
</ul>
<div class="tab-content">
	<div class="tab-pane active" id="status-line-chart">
		<div>
			<div class="d-flex mb-2">
				<div class="chart-container mr-auto">
					<div id="summary-chart-control" style="width: 250px; height: 250px; top: 25px; left: 25px;"></div>
					<div style="width: 1px; height: 50px"></div>
				</div>
				<div class="d-none d-sm-block pl-2">
					<div class="" style="position: relative; height: 300px; min-width: 200px;">

					<div class=" ">
						<div class="mb-2">
							<div class="d-flex justify-content-between">
								<div>
									<button type="button" class="btn btn-sm btn-outline-secondary" onclick="goPrevDate()">
										<span class="fa-regular fa-angle-left fa-lg"></span>
									</button>		
								</div>
								<div class="d-flex align-items-center">
									<span id="curr-date-day"><span id="curr-date-day-str"></span></span>
								</div>
								<div>
	    							<button type="button" class="btn btn-sm btn-outline-secondary" onclick="goNextDate()">
										<span class="fa-regular fa-angle-right fa-lg"></span>
									</button>		
								</div>
							</div>
						</div>
						<input id="pie-svcdate" type="text" class="form-control">
					</div>
					<div class="pb-2" style="position: absolute; bottom: 0; width: 100%;">
						<hr class="border-light pb-1">
						<div class="d-flex align-items-center mb-4 mt-2">
							<span class="flag-34 sts-34-6" id="pie-st6i"></span>
							<span class="ml-1">송출 및 보고</span>
							<span class="numbers ml-auto"><span id="pie-st6-count"></span></span><span class="text-muted">분</span>
						</div>
						<div class="d-flex align-items-center mt-3 mb-2">
							<span class="flag-34 sts-34-2" id="pie-st2i"></span>
							<span class="ml-1">요청 없음</span>
							<span class="numbers ml-auto"><span id="pie-st2-count"></span></span><span class="text-muted">분</span>
						</div>
					</div>


					</div>
				</div>
			</div>
		</div>
		
		<div class="row mb-2">
			<div class="col-12">
				<div class="form-group col mb-1 text-center">
					<button type="button" class="btn btn-xs btn-outline-secondary collapsed" data-toggle="collapse" data-target="#pie-op-time">
						<span name="op-time-show"><span class="fas fa-caret-up"></span></span>
						<span name="op-time-hide"><span class="fas fa-caret-down"></span></span>
						<span class="pl-1">화면 운영시간</span>
					</button>
				</div>
			</div>
		</div>
		<div class="row collapse mb-2" id="pie-op-time">
			<div class="col-12">

				<table id="pie-time-table">
					<tr>
						<th></th>
						<th><pre class="my-0">AM</pre></th>
						<th><pre class="my-0">1</pre></th>
						<th><pre class="my-0">2</pre></th>
						<th><pre class="my-0">3</pre></th>
						<th><pre class="my-0">4</pre></th>
						<th><pre class="my-0">5</pre></th>
						<th><pre class="my-0">6</pre></th>
						<th><pre class="my-0">7</pre></th>
						<th><pre class="my-0">8</pre></th>
						<th><pre class="my-0">9</pre></th>
						<th><pre class="my-0">10</pre></th>
						<th><pre class="my-0">11</pre></th>
						<th><pre class="my-0">PM</pre></th>
						<th><pre class="my-0">1</pre></th>
						<th><pre class="my-0">2</pre></th>
						<th><pre class="my-0">3</pre></th>
						<th><pre class="my-0">4</pre></th>
						<th><pre class="my-0">5</pre></th>
						<th><pre class="my-0">6</pre></th>
						<th><pre class="my-0">7</pre></th>
						<th><pre class="my-0">8</pre></th>
						<th><pre class="my-0">9</pre></th>
						<th><pre class="my-0">10</pre></th>
						<th><pre class="my-0">11</pre></th>
					</tr>
					<tr>
						<th class="pr-2"><small>월</small></th>
						<td id="ptt0"></td>
						<td id="ptt1"></td>
						<td id="ptt2"></td>
						<td id="ptt3"></td>
						<td id="ptt4"></td>
						<td id="ptt5"></td>
						<td id="ptt6"></td>
						<td id="ptt7"></td>
						<td id="ptt8"></td>
						<td id="ptt9"></td>
						<td id="ptt10"></td>
						<td id="ptt11"></td>
						<td id="ptt12"></td>
						<td id="ptt13"></td>
						<td id="ptt14"></td>
						<td id="ptt15"></td>
						<td id="ptt16"></td>
						<td id="ptt17"></td>
						<td id="ptt18"></td>
						<td id="ptt19"></td>
						<td id="ptt20"></td>
						<td id="ptt21"></td>
						<td id="ptt22"></td>
						<td id="ptt23"></td>
					</tr>
					<tr>
						<th><small>화</small></th>
						<td id="ptt24"></td>
						<td id="ptt25"></td>
						<td id="ptt26"></td>
						<td id="ptt27"></td>
						<td id="ptt28"></td>
						<td id="ptt29"></td>
						<td id="ptt30"></td>
						<td id="ptt31"></td>
						<td id="ptt32"></td>
						<td id="ptt33"></td>
						<td id="ptt34"></td>
						<td id="ptt35"></td>
						<td id="ptt36"></td>
						<td id="ptt37"></td>
						<td id="ptt38"></td>
						<td id="ptt39"></td>
						<td id="ptt40"></td>
						<td id="ptt41"></td>
						<td id="ptt42"></td>
						<td id="ptt43"></td>
						<td id="ptt44"></td>
						<td id="ptt45"></td>
						<td id="ptt46"></td>
						<td id="ptt47"></td>
					</tr>
					<tr>
						<th><small>수</small></th>
						<td id="ptt48"></td>
						<td id="ptt49"></td>
						<td id="ptt50"></td>
						<td id="ptt51"></td>
						<td id="ptt52"></td>
						<td id="ptt53"></td>
						<td id="ptt54"></td>
						<td id="ptt55"></td>
						<td id="ptt56"></td>
						<td id="ptt57"></td>
						<td id="ptt58"></td>
						<td id="ptt59"></td>
						<td id="ptt60"></td>
						<td id="ptt61"></td>
						<td id="ptt62"></td>
						<td id="ptt63"></td>
						<td id="ptt64"></td>
						<td id="ptt65"></td>
						<td id="ptt66"></td>
						<td id="ptt67"></td>
						<td id="ptt68"></td>
						<td id="ptt69"></td>
						<td id="ptt70"></td>
						<td id="ptt71"></td>
					</tr>
					<tr>
						<th><small>목</small></th>
						<td id="ptt72"></td>
						<td id="ptt73"></td>
						<td id="ptt74"></td>
						<td id="ptt75"></td>
						<td id="ptt76"></td>
						<td id="ptt77"></td>
						<td id="ptt78"></td>
						<td id="ptt79"></td>
						<td id="ptt80"></td>
						<td id="ptt81"></td>
						<td id="ptt82"></td>
						<td id="ptt83"></td>
						<td id="ptt84"></td>
						<td id="ptt85"></td>
						<td id="ptt86"></td>
						<td id="ptt87"></td>
						<td id="ptt88"></td>
						<td id="ptt89"></td>
						<td id="ptt90"></td>
						<td id="ptt91"></td>
						<td id="ptt92"></td>
						<td id="ptt93"></td>
						<td id="ptt94"></td>
						<td id="ptt95"></td>
					</tr>
					<tr>
						<th><small>금</small></th>
						<td id="ptt96"></td>
						<td id="ptt97"></td>
						<td id="ptt98"></td>
						<td id="ptt99"></td>
						<td id="ptt100"></td>
						<td id="ptt101"></td>
						<td id="ptt102"></td>
						<td id="ptt103"></td>
						<td id="ptt104"></td>
						<td id="ptt105"></td>
						<td id="ptt106"></td>
						<td id="ptt107"></td>
						<td id="ptt108"></td>
						<td id="ptt109"></td>
						<td id="ptt110"></td>
						<td id="ptt111"></td>
						<td id="ptt112"></td>
						<td id="ptt113"></td>
						<td id="ptt114"></td>
						<td id="ptt115"></td>
						<td id="ptt116"></td>
						<td id="ptt117"></td>
						<td id="ptt118"></td>
						<td id="ptt119"></td>
					</tr>
					<tr>
						<th><small>토</small></th>
						<td id="ptt120"></td>
						<td id="ptt121"></td>
						<td id="ptt122"></td>
						<td id="ptt123"></td>
						<td id="ptt124"></td>
						<td id="ptt125"></td>
						<td id="ptt126"></td>
						<td id="ptt127"></td>
						<td id="ptt128"></td>
						<td id="ptt129"></td>
						<td id="ptt130"></td>
						<td id="ptt131"></td>
						<td id="ptt132"></td>
						<td id="ptt133"></td>
						<td id="ptt134"></td>
						<td id="ptt135"></td>
						<td id="ptt136"></td>
						<td id="ptt137"></td>
						<td id="ptt138"></td>
						<td id="ptt139"></td>
						<td id="ptt140"></td>
						<td id="ptt141"></td>
						<td id="ptt142"></td>
						<td id="ptt143"></td>
					</tr>
					<tr>
						<th><small>일</small></th>
						<td id="ptt144"></td>
						<td id="ptt145"></td>
						<td id="ptt146"></td>
						<td id="ptt147"></td>
						<td id="ptt148"></td>
						<td id="ptt149"></td>
						<td id="ptt150"></td>
						<td id="ptt151"></td>
						<td id="ptt152"></td>
						<td id="ptt153"></td>
						<td id="ptt154"></td>
						<td id="ptt155"></td>
						<td id="ptt156"></td>
						<td id="ptt157"></td>
						<td id="ptt158"></td>
						<td id="ptt159"></td>
						<td id="ptt160"></td>
						<td id="ptt161"></td>
						<td id="ptt162"></td>
						<td id="ptt163"></td>
						<td id="ptt164"></td>
						<td id="ptt165"></td>
						<td id="ptt166"></td>
						<td id="ptt167"></td>
					</tr>
				</table>
					
			</div>
		</div>
	</div>
	<div class="tab-pane p-2" id="api-request">
		<div class="row mb-3">
			<div class="col-sm-4 text-muted"><span class="fa-light fa-check mr-2"></span>최근 파일정보 요청</div>
			<div class="col-sm-8">
				<span id="api-last-file-found" style="display: none;">
					<span id="api-last-file"></span>
					<span class="pl-3"></span>
					<small>
						<span class="fa-regular fa-timer text-muted"></span><span class="pl-1 text-muted" style="font-weight: 300;"><span id="api-last-file-long"></span> 경과</span>
					</small>
				</span>
				<span id="api-last-file-not-found" style="display: none;">
					<small>
						<span class="fa-regular fa-question text-muted"></span><span class="pl-1 text-muted" style="font-weight: 300;">요청 없음</span>
					</small>
				</span>
			</div>
		</div>
		<div class="row mb-3">
			<div class="col-sm-4 text-muted"><span class="fa-light fa-check mr-2"></span>최근 현재광고 요청</div>
			<div class="col-sm-8">
				<span id="api-last-ad-found" style="display: none;">
					<span id="api-last-ad"></span>
					<span class="pl-3"></span>
					<small>
						<span class="fa-regular fa-timer text-muted"></span><span class="pl-1 text-muted" style="font-weight: 300;"><span id="api-last-ad-long"></span> 경과</span>
					</small>
				</span>
				<span id="api-last-ad-not-found" style="display: none;">
					<small>
						<span class="fa-regular fa-question text-muted"></span><span class="pl-1 text-muted" style="font-weight: 300;">요청 없음</span>
					</small>
				</span>
			</div>
		</div>
		<div class="row mb-3">
			<div class="col-sm-4 text-muted"><span class="fa-light fa-check mr-2"></span>최근 방송완료 보고</div>
			<div class="col-sm-8">
				<span id="api-last-report-found" style="display: none;">
					<span id="api-last-report"></span>
					<span class="pl-3"></span>
					<small>
						<span class="fa-regular fa-timer text-muted"></span><span class="pl-1 text-muted" style="font-weight: 300;"><span id="api-last-report-long"></span> 경과</span>
					</small>
				</span>
				<span id="api-last-report-not-found" style="display: none;">
					<small>
						<span class="fa-regular fa-question text-muted"></span><span class="pl-1 text-muted" style="font-weight: 300;">보고 없음</span>
					</small>
				</span>
			</div>
		</div>
		<div class="row mb-3">
			<div class="col-sm-4 text-muted"><span class="fa-light fa-check mr-2"></span>최근 누락 완료보고</div>
			<div class="col-sm-8">
				<span id="api-last-retry-report-found" style="display: none;">
					<span id="api-last-retry-report"></span>
					<span class="pl-3"></span>
					<small>
						<span class="fa-regular fa-timer text-muted"></span><span class="pl-1 text-muted" style="font-weight: 300;"><span id="api-last-retry-report-long"></span> 경과</span>
					</small>
				</span>
				<span id="api-last-retry-report-not-found" style="display: none;">
					<small>
						<span class="fa-regular fa-question text-muted"></span><span class="pl-1 text-muted" style="font-weight: 300;">보고 없음</span>
					</small>
				</span>
			</div>
		</div>
		<div class="row mb-1">
			<div class="col-sm-4 text-muted"><span class="fa-light fa-check mr-2"></span>최근 로그</div>
			<div class="col-sm-8">
				<div class="float-right">
					<button id="log-detail-btn" type="button" class="btn btn-default btn-sm icon-btn">
						<span class='fa-regular fa-arrow-down-right'></span>
					</button>
					<button id="refresh-btn" type="button" class="btn btn-default btn-sm icon-btn">
						<span class='fa-regular fa-arrow-rotate-right'></span>
					</button>
				</div>
			</div>
		</div>
		<div class="card mb-3">
			<div class="table-responsive">
				<table class="table card-table">
					<tbody id="api-request-rows">
                        <tr>
                          <td>
							<div class='d-flex align-items-center justify-content-center py-4 text-muted'>로딩 중... 잠시 기다려 주세요.</div>
                          </td>
                        </tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>
	<div class="tab-pane p-2" id="info-screen">
		<div class="row mb-3">
			<div class="col-sm-4 text-muted"><span class="fa-light fa-check mr-2"></span>화면ID</div>
			<div class="col-sm-8"><span id="scr-screenID"></span></div>
		</div>
		<div class="row mb-3">
			<div class="col-sm-4 text-muted"><span class="fa-light fa-wave-pulse mr-2"></span>유효 기간</div>
			<div class="col-sm-8"><span id="scr-eff-period"></span></div>
		</div>
		<div class="row mb-3">
			<div class="col-sm-4 text-muted"><span class="fa-light fa-wave-pulse mr-2"></span>서비스</div>
			<div class="col-sm-8">
				<span id="scr-active-status" style="display: none;">
					<span class="fa-solid fa-check text-primary"></span>
				</span>
			</div>
		</div>
		<div class="row mb-3">
			<div class="col-sm-4 text-muted"><span class="fa-light fa-wave-pulse mr-2"></span>광고 서버에 이용</div>
			<div class="col-sm-8">
				<span id="scr-ad-server-available" style="display: none;">
					<span class="fa-solid fa-check text-primary"></span>
				</span>
			</div>
		</div>
		<div class="row mb-3">
			<div class="col-sm-4 text-muted"><span class="fa-light fa-check mr-2"></span>화면 해상도</div>
			<div class="col-sm-8"><span id="scr-reso"></span></div>
		</div>
		<div class="row mb-3">
			<div class="col-sm-4 text-muted"><span class="fa-light fa-check mr-2"></span>광고 소재 유형</div>
			<div class="col-sm-8">
				<span id="scr-video-allowed" style="display: none;">
					<span class="fa-solid fa-film text-primary"></span><span class="pl-1 pr-3">동영상</span>
				</span>
				<span id="scr-image-allowed" style="display: none;">
					<span class="fa-solid fa-image text-primary"></span><span class="pl-1 pr-3">이미지</span>
				</span>
			</div>
		</div>
		<div class="row mb-3">
			<div class="col-sm-4 text-muted"><span class="fa-light fa-check mr-2"></span>기본 재생<small>(초)</small></div>
			<div class="col-sm-8">
				<span id="scr-default-dur"></span>
				<span class="scr-medium-dur-applied">
					<span class="pl-3"></span>
					<small>
						<span class="fa-solid fa-earth-asia text-muted"></span><span class="pl-1 text-muted" style="font-weight: 300;">매체 설정값 적용</span>
					</small>
				</span>
			</div>
		</div>
		<div class="row mb-3">
			<div class="col-sm-4 text-muted"><span class="fa-light fa-check mr-2"></span>범위 재생<small>(초)</small></div>
			<div class="col-sm-8">
				<span id="scr-range-durs"></span>
				<span class="scr-medium-dur-applied">
					<span class="pl-3"></span>
					<small>
						<span class="fa-solid fa-earth-asia text-muted"></span><span class="pl-1 text-muted" style="font-weight: 300;">매체 설정값 적용</span>
					</small>
				</span>
			</div>
		</div>
		<div class="row mb-3">
			<div class="col-sm-4 text-muted"><span class="fa-light fa-check mr-2"></span>메모</div>
			<div class="col-sm-8">
				<span id="scr-memo"></span>
				<span id="scr-memo-not-found" style="display: none;">
					<small>
						<span class="fa-regular fa-file text-muted"></span><span class="pl-1 text-muted" style="font-weight: 300;">등록 안됨</span>
					</small>
				</span>
			</div>
		</div>
	</div>
	<div class="tab-pane p-2" id="info-site">
		<div class="row mb-3">
			<div class="col-sm-4 text-muted"><span class="fa-light fa-check mr-2"></span>사이트명</div>
			<div class="col-sm-8"><span id="st-name"></span></div>
		</div>
		<div class="row mb-3">
			<div class="col-sm-4 text-muted"><span class="fa-light fa-check mr-2"></span>사이트ID</div>
			<div class="col-sm-8"><span id="st-siteID"></span></div>
		</div>
		<div class="row mb-3">
			<div class="col-sm-4 text-muted"><span class="fa-light fa-check mr-2"></span>유효 기간</div>
			<div class="col-sm-8"><span id="st-eff-period"></span></div>
		</div>
		<div class="row mb-3">
			<div class="col-sm-4 text-muted"><span class="fa-light fa-check mr-2"></span>입지 유형</div>
			<div class="col-sm-8"><span id="st-site-cond-name"></span></div>
		</div>
		<div class="row mb-3">
			<div class="col-sm-4 text-muted"><span class="fa-light fa-check mr-2"></span>장소 유형</div>
			<div class="col-sm-8">
				<span id="st-venue-type-CVS" style="display: none;">
					<span class="fa-regular fa-store fa-fw text-green"></span><span class="pl-1 pr-3">편의점</span>
				</span>
				<span id="st-venue-type-BUSSH" style="display: none;">
					<span class="fa-regular fa-people-roof fa-fw text-orange"></span><span class="pl-1 pr-3">버스 쉘터</span>
				</span>
				<span id="st-venue-type-BLDG" style="display: none;">
					<span class="fa-regular fa-billboard fa-fw text-orange"></span><span class="pl-1 pr-3">빌딩 전광판</span>
				</span>
				<span id="st-venue-type-HOSP" style="display: none;">
					<span class="fa-regular fa-hospital fa-fw text-blue"></span><span class="pl-1 pr-3">병/의원</span>
				</span>
			</div>
		</div>
		<div class="row mb-3">
			<div class="col-sm-4 text-muted"><span class="fa-light fa-check mr-2"></span>시/군/구</div>
			<div class="col-sm-8"><span id="st-region-name"></span></div>
		</div>
		<div class="row mb-3">
			<div class="col-sm-4 text-muted"><span class="fa-light fa-check mr-2"></span>주소</div>
			<div class="col-sm-8">
				<span id="st-address"></span>
				<span id="st-address-not-found" style="display: none;">
					<small>
						<span class="fa-regular fa-file text-muted"></span><span class="pl-1 text-muted" style="font-weight: 300;">등록 안됨</span>
					</small>
				</span>
			</div>
		</div>
		<div class="row mb-3">
			<div class="col-sm-4 text-muted"><span class="fa-light fa-check mr-2"></span>메모</div>
			<div class="col-sm-8">
				<span id="st-memo"></span>
				<span id="st-memo-not-found" style="display: none;">
					<small>
						<span class="fa-regular fa-file text-muted"></span><span class="pl-1 text-muted" style="font-weight: 300;">등록 안됨</span>
					</small>
				</span>
			</div>
		</div>
	</div>
	<div class="tab-pane p-0 mb-3" id="map-loc">
		<div id="map" style="border: solid 1px #e4e4e4; background-color: white; height: 500px; width: 100%;">
		</div>
	</div>
</div>

<style>

.si-icon-col {
	width: 80px;
	text-align: center;
}
.si-text-col {
	font-weight: 300;
}

/* 화면의 하루 운행 상태 파이 차트의 배경에 시간이 포함된 배경 이미지 출력  */
.chart-container {
	background:url(/resources/shared/styles/bg_time.png) left top no-repeat; 
	width:300px; 
	height:300px; 
	text-align:center;
}


/* 화면 운행 시간(분) 문자열 데코 */
span.numbers {
	font-size: 16px;
	font-weight:500; 
	color:#000; 
	padding-left:5px;
	padding-right: 10px;
}


/* 상태별 아이콘 */
.flag-34 {
	background-image: url('/resources/shared/images/color_flags.png');
	background-color: transparent;
	opacity: 1;
	display: inline-block;
	width: 34px;
	height: 34px;
	overflow: hidden;
	background-repeat: no-repeat;
}
.sts-34-0 { background-position: 0px 0px; }
.sts-34-2 { background-position: -34px 0px; }
.sts-34-3 { background-position: -68px 0px; }
.sts-34-4 { background-position: -102px 0px; }
.sts-34-5 { background-position: -136px 0px; }
.sts-34-6 { background-position: -170px 0px; }


.inner-content {
	position: absolute;
	top: 56%;
	left: 0%;
	width: 300px;
	height: 300px;
	vertical-align: middle;
	text-align: center;
	font-size: 16px;
}

.prev-date-button {
	position: absolute;
	top: 0;
	left: 0;
	width: 300px;
	height: 300px;
	text-align: left;
	margin-left: 3px;
	margin-top: 3px;
}

.next-date-button {
	position: absolute;
	top: 0;
	left: 0;
	width: 300px;
	height: 300px;
	text-align: right;
	margin-right: 3px;
	margin-top: 3px;
}


/* 시간 설정 테이블 */
table#pie-time-table {
	border-spacing: 0px;
}
table#pie-time-table th {
	font-weight: 400;
}
table#pie-time-table td {
	border: 1px solid #fff;
	width: 22px;
	height: 22px;
	margin: 10px;
	background-color: rgba(24,28,33,0.06) !important;
	cursor: default;
	padding: 0px;
}
table#pie-time-table td.selected {
	background-color: #02a96b !important;
}
table#pie-time-table td.rselected {
	background-color: #02BC77 !important;
}
table#pie-time-table td.nzselected {
	background-color: #f00 !important;
}

</style>

<!--  / Forms -->


<!--  Scripts -->

<script>

var pieChartData = [];

var piePrevGroup = "";
var pieCurrExplode = false;

var piePrevDateStr = "", pieNextDateStr = "";
var pieMaxDate = new Date();

var tableMsg = "<tr><td><div class='d-flex align-items-center justify-content-center py-4 text-muted'>${1}</div></td></tr>";
var refreshWorking = false;

$(document).ready(function() {

	// Refresh
	$("#refresh-btn").click(function(e) {
		e.preventDefault();
		
		if (!refreshWorking) {
			$("#api-request-rows").html(tableMsg.replace("${1}", "로딩 중... 잠시 기다려 주세요."));

			readPlayHist();
		}
	});
	// / Refresh

	// Log Detail
	$("#log-detail-btn").click(function(e) {
		e.preventDefault();
		
		var path = "/rev/monitoring?screen=" + ($("#scr-screenID").text());
		location.href = path;
	});
	// / Log Detail
	
	$("#pie-svcdate").kendoDatePicker({
		dates: ${dates},
		format: "yyyy-MM-dd",
		parseFormats: [
			"yyyy-MM-dd", "yyyyMMdd",
		],
		max: pieMaxDate,
		month: {
            content: '# if ($.inArray(+data.date, ${dates}) != -1) { #' +
            		 "<span style=\"position: relative;\"><span style=\"position:absolute;right:0px;margin-top:-11px;\">&bull;</span>#= data.value #</span>" +
         			 '# } else { #' +
         			 '#= data.value #' +
         			 '# } #',
		},
		change: function(e) {
			var url = "/adn/common/screenInfo?id=${value_id}&date=" + 
					kendo.toString($("#pie-svcdate").data("kendoDatePicker").value(), "yyyy'-'MM'-'dd");
		    $("#screenInfoModal .modal-body").load(url);
		}
	});
	
	$("#pie-st6i").click(function() {
		clickGroup("6");
	});

	$("#pie-st5i").click(function() {
		clickGroup("5");
	});

	$("#pie-st2i").click(function() {
		clickGroup("2");
	});

	
	$('#scr-summary-tabs a').on('shown.bs.tab', function(e){
		var id = $(e.target).attr("id");
		
		if (parent) {
			if (id == "summary-map-loc") {
				// Naver Map 버그 해결
				window.dispatchEvent(new Event('resize'));
			}
		}
	});
	
	
	$("#status-line-chart span[name='op-time-hide']").hide();
	
	$("#pie-op-time").on('show.bs.collapse', function(){
		console.log("1");
		$("#status-line-chart span[name='op-time-show']").hide();
		$("#status-line-chart span[name='op-time-hide']").show();
	});
	
	$("#pie-op-time").on('hide.bs.collapse', function(){
		console.log("2");
		$("#status-line-chart span[name='op-time-show']").show();
		$("#status-line-chart span[name='op-time-hide']").hide();
	});

	
	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${readScreenInfoUrl}",
		data: JSON.stringify({ id: ${value_id}, date: "${value_date}" }),
		success: function (data, status) {
			refreshData(data, status);
			readPlayHist();
		},
		error: function(e) {
			if (parent) {
				parent.showReadErrorMsg();
			}
		}
	});
	
});


function clickGroup(group) {
	if (piePrevGroup != group) {
		pieCurrExplode = true;
	} else {
		pieCurrExplode = !pieCurrExplode;
	}
	
	for(var i in pieChartData) {
		if (pieChartData[i].group == group) {
			pieChartData[i].explode = pieCurrExplode;
		} else {
			pieChartData[i].explode = false;
		}
	}
	
	piePrevGroup = group;
	
	$("#summary-chart-control").data("kendoChart").refresh();
}


function refreshData(data, status) {
	
	$("#scr-screenID").text(data.scrScreenID);
	$("#scr-reso").text(data.scrReso);
	$("#scr-eff-period").html(data.scrEffPeriod);
	
	if (data.scrActiveStatus) {
		$("#scr-active-status").show();
	} else {
		$("#scr-active-status").hide();
	}
	if (data.scrAdServerAvailable) {
		$("#scr-ad-server-available").show();
	} else {
		$("#scr-ad-server-available").hide();
	}
	
	if (data.scrActive) {
		$("#screenInfoModalTitleTag").html("<small><span class='badge badge-pill badge-success' style='font-weight: 300;'>활성</span></small>");
	} else {
		$("#screenInfoModalTitleTag").html("<small><span class='badge badge-pill badge-secondary' style='font-weight: 300;'>비활성</span></small>");
	}
	
	if (data.scrVideoAllowed) {
		$("#scr-video-allowed").show();
	} else {
		$("#scr-video-allowed").hide();
	}
	if (data.scrImageAllowed) {
		$("#scr-image-allowed").show();
	} else {
		$("#scr-image-allowed").hide();
	}
	
	$("#scr-default-dur").text(data.scrDefaultDur);
	$("#scr-range-durs").text(data.scrRangeDurs);
	
	if (data.scrMediumDurApplied) {
		$(".scr-medium-dur-applied").show();
	} else {
		$(".scr-medium-dur-applied").hide();
	}
	
	$("#scr-memo").text(data.scrMemo);
	if (data.scrMemo) {
		$("#scr-memo-not-found").hide();
	} else {
		$("#scr-memo-not-found").show();
	}
	
	
	$("#st-name").text(data.stName);
	$("#st-siteID").text(data.stSiteID);
	$("#st-eff-period").html(data.stEffPeriod);
	
	$("#st-site-cond-name").text(data.stSiteCondName);
	$("#st-venue-type-" + data.stVenueType).show();

	$("#st-region-name").text(data.stRegionName);
	$("#st-address").text(data.stAddress);
	if (data.stAddress) {
		$("#st-address-not-found").hide();
	} else {
		$("#st-address-not-found").show();
	}
	
	$("#st-memo").text(data.stMemo);
	if (data.stMemo) {
		$("#st-memo-not-found").hide();
	} else {
		$("#st-memo-not-found").show();
	}
	
	
	if (data.apiLastFile && data.apiLastFile != '-') {
		$("#api-last-file").html(data.apiLastFile);
		$("#api-last-file-long").text(data.apiLastFileLong);
		$("#api-last-file-found").show();
		$("#api-last-file-not-found").hide();
	} else {
		$("#api-last-file-found").hide();
		$("#api-last-file").html("");
		$("#api-last-file-not-found").show();
	}
	if (data.apiLastAd && data.apiLastAd != '-') {
		$("#api-last-ad").html(data.apiLastAd);
		$("#api-last-ad-long").text(data.apiLastAdLong);
		$("#api-last-ad-found").show();
		$("#api-last-ad-not-found").hide();
	} else {
		$("#api-last-ad-found").hide();
		$("#api-last-ad").html("");
		$("#api-last-ad-not-found").show();
	}
	if (data.apiLastReport && data.apiLastReport != '-') {
		$("#api-last-report").html(data.apiLastReport);
		$("#api-last-report-long").text(data.apiLastReportLong);
		$("#api-last-report-found").show();
		$("#api-last-report-not-found").hide();
	} else {
		$("#api-last-report-found").hide();
		$("#api-last-report").html("");
		$("#api-last-report-not-found").show();
	}
	if (data.apiLastRetryReport && data.apiLastRetryReport != '-') {
		$("#api-last-retry-report").html(data.apiLastRetryReport);
		$("#api-last-retry-report-long").text(data.apiLastRetryReportLong);
		$("#api-last-retry-report-found").show();
		$("#api-last-retry-report-not-found").hide();
	} else {
		$("#api-last-retry-report-found").hide();
		$("#api-last-retry-report").html("");
		$("#api-last-retry-report-not-found").show();
	}
	
	
	$("#pie-svcdate").data("kendoDatePicker").value(data.piePlayDate);
	
	$("#pie-st6-count").text(data.piePlayingCount);
	$("#pie-st5-count").text(data.pieApiCount);
	$("#pie-st2-count").text(data.pieNothingCount);

	$("#pie-running-count").text(data.pieTotalCount);
	
	for(var i in data.pieRunningTimeItems) {
		var color = "#f3f3f4";
		if (data.pieRunningTimeItems[i].group == "6") {
			color = "#0276bd";
		} else if (data.pieRunningTimeItems[i].group == "5") {
			color = "#69bd44";
		} else if (data.pieRunningTimeItems[i].group == "2") {
			color = "#ef262e";
		}
		
		var item = {
			category: data.pieRunningTimeItems[i].title,
			value: data.pieRunningTimeItems[i].value,
			explode: false,
			group: data.pieRunningTimeItems[i].group,
			color: color,
		};
		
		pieChartData.push(item);
	}
	
	createPieChart(data.piePlayDate);
	
	showPieBizHours(data.pieBizHours);
	
	drawMap(data);
}


function createPieChart(currDate) {
	
    $("#summary-chart-control").kendoChart({
        legend: {
            visible: false,
        },
        chartArea: {
            background: ""
        },
        seriesDefaults: {
            type: "donut",
            holeSize: 40,
            startAngle: 90,
            explodeField: "explode",
            tooltip: {
            	font: "'Roboto', 'Noto Sans', 'Noto Sans CJK KR', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue'",
            }
        },
        series: [{
            name: "Service Time",
            data: pieChartData,
            size: 65,
        }],
        tooltip: {
            visible: true,
            template: "#= category #"
        },
    });

    
    // 선택 날짜의 요일 표시
	$("#curr-date-day-str").text(kendo.toString($("#pie-svcdate").data("kendoDatePicker").value(), "dddd"));
    

    // 하루 전, 다음 날 처리를 위한 계산
    const [y, m, d] = currDate.split("-");
    
    var nextDate = new Date(Number(y), Number(m) - 1, Number(d) + 1);
    var prevDate = new Date(Number(y), Number(m) - 1, Number(d) - 1);
    
    piePrevDateStr = toStringByFormatting(prevDate, "-");
    
    if (nextDate.getTime() > pieMaxDate.getTime()) {
        pieNextDateStr = toStringByFormatting(pieMaxDate, "-");
    } else {
        pieNextDateStr = toStringByFormatting(nextDate, "-");
    }
}


function leftPad(value) {
    if (value >= 10) {
        return value;
    }

    return "0" + value;
}


function toStringByFormatting(source, delimiter = '-') {
    const year = source.getFullYear();
    const month = leftPad(source.getMonth() + 1);
    const day = leftPad(source.getDate());
    
    return [year, month, day].join(delimiter);
}


function goPrevDate() {
	$("#screenInfoModal .modal-body").load("/adn/common/screenInfo?id=${value_id}&date=" + piePrevDateStr);
}


function goNextDate() {
	$("#screenInfoModal .modal-body").load("/adn/common/screenInfo?id=${value_id}&date=" + pieNextDateStr);
}


function readPlayHist() {
	
	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${readScreenPlayHistUrl}",
		data: JSON.stringify({ id: ${value_id} }),
		success: function (data, status) {

			if (data && data.length > 0) {
				var html = "";
				for(var i in data) {
					if (data[i].completed) {
						html = html + "<tr>";
					} else {
						html = html + "<tr class='table-danger'>";
					}
					html = html + "  <td class='si-text-col'>" + data[i].time + "</td>";
					html = html + "  <td class='si-text-col'>" + data[i].adName + "</td>";

					if (data[i].completed && data[i].retryReported) {
						html = html + "<td class='si-icon-col'><span class='fa-light fa-flag-checkered text-danger'></span></td>";
					} else if (data[i].completed) {
						html = html + "<td class='si-icon-col'><span class='fa-light fa-flag-checkered'></span></td>";
					} else {
						html = html + "<td class='si-icon-col'></td>";
					}
					html = html + "</tr>";
				}
				
				$("#api-request-rows").html(html);
			} else {
				$("#api-request-rows").html(tableMsg.replace("${1}", "자료가 확인되지 않습니다."));
			}
			
			refreshWorking = false;
		},
		error: function(e) {
			if (parent) {
				parent.showReadErrorMsg();
			}
			refreshWorking = false;
		}
	});
}


function showPieBizHours(timeVal) {

	if (timeVal.length == 168) {
		for(var i = 0; i < 168; i++) {
			$("#ptt" + i).removeClass("selected rselected nselected");
			if (Number(timeVal.substr(i, 1)) == 1) {
				$("#ptt" + i).addClass("rselected");
			}
		}
	}
}


var infoWin = null;

function drawMap(data) {
	
	var map = new naver.maps.Map('map', {
		center: new naver.maps.LatLng(data.mapLat, data.mapLng),
		zoom: 15
	}); 

	var titles = [];
	var lats = [];
	var lngs = [];
	
<c:forEach var="item" items="${markerList}">
	titles.push("${item.title}");
	lats.push(${item.lat});
	lngs.push(${item.lng});
</c:forEach>

	var markers = [];
	var infoWins = [];
	
	for (var i = 0; i < titles.length; i++) {
		var marker = new naver.maps.Marker({
			position: new naver.maps.LatLng(lats[i], lngs[i]),
			icon: data.mapMarkerUrl,
			map: map
		});
		
		markers.push(marker);
		
		var content = [
			(i == 0) ? '<div class="rounded-marker rounded-marker-yellow">' : '<div class="rounded-marker rounded-marker-white">',
	        	'<span class="rounded-marker-title">' + titles[i] + '</span>',
	        '</div>'
	    ].join('');
		
		infoWins.push(new naver.maps.InfoWindow({
		    content: content,
		    borderWidth: 0,
		    disableAnchor: true,
		    backgroundColor: 'transparent',
		    pixelOffset: new naver.maps.Point(0, 7),
		}));
	}
	
	var circle = new naver.maps.Circle({
	    map: map,
	    center: new naver.maps.LatLng(lats[0], lngs[0]),
	    radius: 250,
	    fillColor: '#FFFF00',
	    fillOpacity: 0.1
	});
	
    function getClickHandler(seq) {
		
		return function(e) {  // 마커를 클릭하는 부분
			var marker = markers[seq], // 클릭한 마커의 시퀀스로 찾는다.
				infoWindow = infoWins[seq]; // 클릭한 마커의 시퀀스로 찾는다

			if (infoWindow.getMap()) {
				infoWindow.close();
				infoWin = null;
			} else {
				infoWin = infoWindow;
				infoWindow.open(map, marker);
			}
    	}
    };

	for(var i = 0; i < markers.length; i++){
		naver.maps.Event.addListener(markers[i], "click", getClickHandler(i));
	}
	
	naver.maps.Event.addListener(map, "click", function(e) {
		if (infoWin) {
    	    infoWin.close();
    	    infoWin = null;
		}
	});
};

</script>

<style>

.rounded-marker {
	border-radius: 10px; 
	background: #000;
	font-weight: 300;
}

.rounded-marker-white {
	color: white;
}

.rounded-marker-yellow {
	color: yellow;
	font-weight: 400;
}

.rounded-marker-title {
	padding-right: 10px; 
	padding-left: 10px;
}

</style>

<!--  / Scripts -->


<!-- / Page body -->
