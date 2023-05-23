<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/org/currmedium/read" var="readUrl" />
<c:url value="/org/currmedium/update" var="updateUrl" />
<c:url value="/org/currmedium/updateTime" var="updateTimeUrl" />


<!-- Opening tags -->

<common:mediumPageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}
	<span class='small text-muted pl-3'>${subtitle}</span>
</h4>





<!-- Page body -->


<!--  Forms -->

<ul class="nav nav-tabs tabs-alt container-p-x container-m--x mb-4">
	<li class="nav-item">
		<a class="nav-link active" data-toggle="tab" href="#basic-info">
			<i class="mr-1 fa-light fa-icons"></i>
			일반
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#ad-selection">
			<i class="mr-1 fa-light fa-list-ol"></i>
			광고 선택
		</a>
	</li>
</ul>

<div class="tab-content">
	<div class="tab-pane active" id="basic-info">
		<div class="card">
			<div class="card-body">
				<div class="pb-2">
			    	<div class="clearfix">
			    		<div class="float-left">
							기본 정보 및 운영 시간
							<span class="small text-muted pl-3">등록된 매체의 기본 정보를 확인할 수 있으며, 매체의 운영 시간을 확인 및 변경할 수 있습니다.</span>
			    		</div>
			    		<div class="float-right">
							<button type='button' onclick='editBizTime()' class='btn icon-btn btn-sm btn-outline-success'> 
								<span class='fas fa-pencil-alt'></span>
							</button>
			    		</div>
			    	</div>
				</div>
				<div class="form-row">
					<div class="col-sm-5">
						<div class="form-group col mb-0">
							<label class="form-label">
								매체명
							</label>
							<input value="${name}" type="text" class="form-control" readonly />
						</div>
					</div>
					<div class="col-sm-4">
						<div class="form-group col mb-0">
							<label class="form-label">
								매체의 단축명
							</label>
							<input value="${shortName}" type="text" class="form-control" readonly />
						</div>
					</div>
					<div class="col-sm-3">
						<div class="form-group col mb-0">
							<label class="form-label">
								1주일 총 운영시간
							</label>
							<div class="input-group">
								<input type="text" class="form-control" value="${bizHours}" readonly>
								<div class="input-group-append">
									<span class="input-group-text">시간</span>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="form-row">
					<div class="mt-3 ml-4">

						<table id="base-time-table">
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
								<th class="pr-2">월<small>요일</small></th>
								<td id="btt0"></td>
								<td id="btt1"></td>
								<td id="btt2"></td>
								<td id="btt3"></td>
								<td id="btt4"></td>
								<td id="btt5"></td>
								<td id="btt6"></td>
								<td id="btt7"></td>
								<td id="btt8"></td>
								<td id="btt9"></td>
								<td id="btt10"></td>
								<td id="btt11"></td>
								<td id="btt12"></td>
								<td id="btt13"></td>
								<td id="btt14"></td>
								<td id="btt15"></td>
								<td id="btt16"></td>
								<td id="btt17"></td>
								<td id="btt18"></td>
								<td id="btt19"></td>
								<td id="btt20"></td>
								<td id="btt21"></td>
								<td id="btt22"></td>
								<td id="btt23"></td>
							</tr>
							<tr>
								<th>화<small>요일</small></th>
								<td id="btt24"></td>
								<td id="btt25"></td>
								<td id="btt26"></td>
								<td id="btt27"></td>
								<td id="btt28"></td>
								<td id="btt29"></td>
								<td id="btt30"></td>
								<td id="btt31"></td>
								<td id="btt32"></td>
								<td id="btt33"></td>
								<td id="btt34"></td>
								<td id="btt35"></td>
								<td id="btt36"></td>
								<td id="btt37"></td>
								<td id="btt38"></td>
								<td id="btt39"></td>
								<td id="btt40"></td>
								<td id="btt41"></td>
								<td id="btt42"></td>
								<td id="btt43"></td>
								<td id="btt44"></td>
								<td id="btt45"></td>
								<td id="btt46"></td>
								<td id="btt47"></td>
							</tr>
							<tr>
								<th>수<small>요일</small></th>
								<td id="btt48"></td>
								<td id="btt49"></td>
								<td id="btt50"></td>
								<td id="btt51"></td>
								<td id="btt52"></td>
								<td id="btt53"></td>
								<td id="btt54"></td>
								<td id="btt55"></td>
								<td id="btt56"></td>
								<td id="btt57"></td>
								<td id="btt58"></td>
								<td id="btt59"></td>
								<td id="btt60"></td>
								<td id="btt61"></td>
								<td id="btt62"></td>
								<td id="btt63"></td>
								<td id="btt64"></td>
								<td id="btt65"></td>
								<td id="btt66"></td>
								<td id="btt67"></td>
								<td id="btt68"></td>
								<td id="btt69"></td>
								<td id="btt70"></td>
								<td id="btt71"></td>
							</tr>
							<tr>
								<th>목<small>요일</small></th>
								<td id="btt72"></td>
								<td id="btt73"></td>
								<td id="btt74"></td>
								<td id="btt75"></td>
								<td id="btt76"></td>
								<td id="btt77"></td>
								<td id="btt78"></td>
								<td id="btt79"></td>
								<td id="btt80"></td>
								<td id="btt81"></td>
								<td id="btt82"></td>
								<td id="btt83"></td>
								<td id="btt84"></td>
								<td id="btt85"></td>
								<td id="btt86"></td>
								<td id="btt87"></td>
								<td id="btt88"></td>
								<td id="btt89"></td>
								<td id="btt90"></td>
								<td id="btt91"></td>
								<td id="btt92"></td>
								<td id="btt93"></td>
								<td id="btt94"></td>
								<td id="btt95"></td>
							</tr>
							<tr>
								<th>금<small>요일</small></th>
								<td id="btt96"></td>
								<td id="btt97"></td>
								<td id="btt98"></td>
								<td id="btt99"></td>
								<td id="btt100"></td>
								<td id="btt101"></td>
								<td id="btt102"></td>
								<td id="btt103"></td>
								<td id="btt104"></td>
								<td id="btt105"></td>
								<td id="btt106"></td>
								<td id="btt107"></td>
								<td id="btt108"></td>
								<td id="btt109"></td>
								<td id="btt110"></td>
								<td id="btt111"></td>
								<td id="btt112"></td>
								<td id="btt113"></td>
								<td id="btt114"></td>
								<td id="btt115"></td>
								<td id="btt116"></td>
								<td id="btt117"></td>
								<td id="btt118"></td>
								<td id="btt119"></td>
							</tr>
							<tr>
								<th>토<small>요일</small></th>
								<td id="btt120"></td>
								<td id="btt121"></td>
								<td id="btt122"></td>
								<td id="btt123"></td>
								<td id="btt124"></td>
								<td id="btt125"></td>
								<td id="btt126"></td>
								<td id="btt127"></td>
								<td id="btt128"></td>
								<td id="btt129"></td>
								<td id="btt130"></td>
								<td id="btt131"></td>
								<td id="btt132"></td>
								<td id="btt133"></td>
								<td id="btt134"></td>
								<td id="btt135"></td>
								<td id="btt136"></td>
								<td id="btt137"></td>
								<td id="btt138"></td>
								<td id="btt139"></td>
								<td id="btt140"></td>
								<td id="btt141"></td>
								<td id="btt142"></td>
								<td id="btt143"></td>
							</tr>
							<tr>
								<th>일<small>요일</small></th>
								<td id="btt144"></td>
								<td id="btt145"></td>
								<td id="btt146"></td>
								<td id="btt147"></td>
								<td id="btt148"></td>
								<td id="btt149"></td>
								<td id="btt150"></td>
								<td id="btt151"></td>
								<td id="btt152"></td>
								<td id="btt153"></td>
								<td id="btt154"></td>
								<td id="btt155"></td>
								<td id="btt156"></td>
								<td id="btt157"></td>
								<td id="btt158"></td>
								<td id="btt159"></td>
								<td id="btt160"></td>
								<td id="btt161"></td>
								<td id="btt162"></td>
								<td id="btt163"></td>
								<td id="btt164"></td>
								<td id="btt165"></td>
								<td id="btt166"></td>
								<td id="btt167"></td>
							</tr>
						</table>
					
					</div>
				</div>
			</div>
			<hr class="m-0" />
			<div class="card-body">
				<div class="pb-2">
					API 키
					<span class="small text-muted pl-3">API 요청 시 이 매체를 식별할 수 있도록 부여받은 문자열입니다.</span>
				</div>
				<input value="${apiKey}" type="text" class="form-control col-sm-6" readonly>
			</div>
			<hr class="m-0" />
			<div class="card-body">
				<div class="pb-3">
		    		<div class="float-left">
						인벤 자료 등록 요청 시 매체의 기본값
						<span class="small text-muted pl-3">매체 인벤 자료 등록(인벤 일괄 업로드, 인벤 API 요청)할 때, 따로 명시하지 않아도 이 매체에 대해 시스템에서 인지하는 기본 값입니다. 문자열은 json 형식이며, 정확한 항목명은 우측의 도움말 링크를 참조하십시오.</span>
		    		</div>
		    		<div class="float-right">
						<button type='button' onclick='downloadGuideDoc()' class='btn icon-btn btn-sm btn-outline-secondary'> 
							<span class='fas fa-download'></span>
						</button>
		    		</div>
				</div>
				<div class="pt-3">
					<input name="invenValues" type="text" class="form-control col-10">
				</div>
			</div>
			<hr class="m-0" />
			<div class="card-body">
				<div class="pb-3">
		    		<div class="float-left">
						매체 화면 지도 마커
						<span class="small text-muted pl-3">지도에서 현재 매체 화면을 표시할 때의 마커 이미지입니다.</span>
		    		</div>
		    		<div class="float-right">
		    			<img src="${markerUrl}">
		    		</div>
				</div>
			</div>
		</div>
	</div>
	<div class="tab-pane" id="ad-selection">
		<div class="card">
			<div class="card-body">
				<div class="pb-2">
					광고 선택 방식
					<span class="small text-muted pl-3">재생 목록을 미리 정해두거나, 재생되기 직전에 기존 노출 통계를 바탕으로 이번에 재생될 광고를 판단하게 할 수 있습니다.</span>
				</div>
				
				<div class="row no-gutters row-bordered text-center">

					<div class="d-flex col-md flex-column pt-4 pb-0">
						<h5 class="m-0"><span class="lead">재생 목록 기반</span></h5>
						<div class="display-1 text-primary my-4"><i class="fa-thin fa-ballot-check fa-lg"></i></div>
						<div class="flex-grow-1">
							<div class="pb-4">
								<p class="ui-company-text mb-2">
									미리 설정된 재생 목록에 따라 광고 재생<br>
									<u>혼합 방식에 포함</u>되어 재생 목록 방식 설정 가능
								</p>
							</div>
						</div>
						<div class="px-md-3 px-lg-5">
						</div>
					</div>
					<div class="d-flex col-md flex-column pt-4 pb-0">
						<h5 class="m-0"><span class="lead">혼합(재생 목록 + 실시간 판단)</span></h5>
						<div class="display-1 text-primary my-4"><i class="fa-thin fa-shuffle fa-lg"></i></div>
						<div class="flex-grow-1">
							<div class="pb-4">
								<p class="ui-company-text mb-2 px-3">재생 목록에 따라 진행 중에 특정 항목에 대해 실시간 판단 진행</p>
							</div>
						</div>
						<div class="px-md-3 px-lg-5">
							<a href="javascript:setSelAdType('L')" class="btn btn-outline-primary btn-lg" id="sel-ad-type-playlist-a">
								<span id="sel-ad-type-playlist-icon" class="pr-2">
									<span class="fa-regular fa-check"></span>
								</span>
								<span id="sel-ad-type-playlist-text" class="pl-2"></span>
							</a>
						</div>
					</div>
					<div class="d-flex col-md flex-column pt-4 pb-0">
						<h5 class="m-0"><span class="lead">실시간 판단 기반</span></h5>
						<div class="display-1 text-primary my-4"><i class="fa-thin fa-robot fa-lg"></i></div>
						<div class="flex-grow-1">
							<div class="pb-4">
								<p class="ui-company-text mb-2 px-3">재생 횟수와 요구를 파악하여 다음 광고 선택</p>
							</div>
						</div>
						<div class="px-md-3 px-lg-5">
							<a href="javascript:setSelAdType('R')" class="btn btn-outline-primary btn-lg" id="sel-ad-type-realtime-a">
								<span id="sel-ad-type-realtime-icon" class="pr-2">
									<span class="fa-regular fa-check"></span>
								</span>
								<span id="sel-ad-type-realtime-text"></span>
							</a>
						</div>
					</div>

				</div>
			</div>
		</div>
	</div>
</div>


<div class="text-right mt-3">
	<button id="save-btn" type="button" class="btn btn-primary">저장</button>
</div>


<!--  Root form container -->
<div id="formRoot"></div>


<script id="template-2" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-2">
	<div class="modal-dialog modal-lg">
		<form class="modal-content" id="form-2" rowid="-1" url="${createUrl}">
      
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
					<div class="form-group col-5">
						<label class="form-label">
							매체명
						</label>
						<input name="name" type="text" maxlength="100" class="form-control" readonly>
					</div>
					<div class="form-group col-4">
						<label class="form-label">
							매체의 단축명
						</label>
						<input name="shortName" type="text" maxlength="50" class="form-control" readonly>
					</div>
					<div class="form-group col-3">
						<label class="form-label">
							1주일 총 운영시간
						</label>
						<div class="input-group">
							<input name="bizHrs" type="text" class="form-control" value="0" readonly>
							<div class="input-group-append">
								<span class="input-group-text">시간</span>
							</div>
						</div>
					</div>
				</div>
				<div class="form-row">
					<div class="mx-auto">

<table id="time-table">
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
		<th class="pr-2">월<small>요일</small></th>
		<td id="tt0"></td>
		<td id="tt1"></td>
		<td id="tt2"></td>
		<td id="tt3"></td>
		<td id="tt4"></td>
		<td id="tt5"></td>
		<td id="tt6"></td>
		<td id="tt7"></td>
		<td id="tt8"></td>
		<td id="tt9"></td>
		<td id="tt10"></td>
		<td id="tt11"></td>
		<td id="tt12"></td>
		<td id="tt13"></td>
		<td id="tt14"></td>
		<td id="tt15"></td>
		<td id="tt16"></td>
		<td id="tt17"></td>
		<td id="tt18"></td>
		<td id="tt19"></td>
		<td id="tt20"></td>
		<td id="tt21"></td>
		<td id="tt22"></td>
		<td id="tt23"></td>
	</tr>
	<tr>
		<th>화<small>요일</small></th>
		<td id="tt24"></td>
		<td id="tt25"></td>
		<td id="tt26"></td>
		<td id="tt27"></td>
		<td id="tt28"></td>
		<td id="tt29"></td>
		<td id="tt30"></td>
		<td id="tt31"></td>
		<td id="tt32"></td>
		<td id="tt33"></td>
		<td id="tt34"></td>
		<td id="tt35"></td>
		<td id="tt36"></td>
		<td id="tt37"></td>
		<td id="tt38"></td>
		<td id="tt39"></td>
		<td id="tt40"></td>
		<td id="tt41"></td>
		<td id="tt42"></td>
		<td id="tt43"></td>
		<td id="tt44"></td>
		<td id="tt45"></td>
		<td id="tt46"></td>
		<td id="tt47"></td>
	</tr>
	<tr>
		<th>수<small>요일</small></th>
		<td id="tt48"></td>
		<td id="tt49"></td>
		<td id="tt50"></td>
		<td id="tt51"></td>
		<td id="tt52"></td>
		<td id="tt53"></td>
		<td id="tt54"></td>
		<td id="tt55"></td>
		<td id="tt56"></td>
		<td id="tt57"></td>
		<td id="tt58"></td>
		<td id="tt59"></td>
		<td id="tt60"></td>
		<td id="tt61"></td>
		<td id="tt62"></td>
		<td id="tt63"></td>
		<td id="tt64"></td>
		<td id="tt65"></td>
		<td id="tt66"></td>
		<td id="tt67"></td>
		<td id="tt68"></td>
		<td id="tt69"></td>
		<td id="tt70"></td>
		<td id="tt71"></td>
	</tr>
	<tr>
		<th>목<small>요일</small></th>
		<td id="tt72"></td>
		<td id="tt73"></td>
		<td id="tt74"></td>
		<td id="tt75"></td>
		<td id="tt76"></td>
		<td id="tt77"></td>
		<td id="tt78"></td>
		<td id="tt79"></td>
		<td id="tt80"></td>
		<td id="tt81"></td>
		<td id="tt82"></td>
		<td id="tt83"></td>
		<td id="tt84"></td>
		<td id="tt85"></td>
		<td id="tt86"></td>
		<td id="tt87"></td>
		<td id="tt88"></td>
		<td id="tt89"></td>
		<td id="tt90"></td>
		<td id="tt91"></td>
		<td id="tt92"></td>
		<td id="tt93"></td>
		<td id="tt94"></td>
		<td id="tt95"></td>
	</tr>
	<tr>
		<th>금<small>요일</small></th>
		<td id="tt96"></td>
		<td id="tt97"></td>
		<td id="tt98"></td>
		<td id="tt99"></td>
		<td id="tt100"></td>
		<td id="tt101"></td>
		<td id="tt102"></td>
		<td id="tt103"></td>
		<td id="tt104"></td>
		<td id="tt105"></td>
		<td id="tt106"></td>
		<td id="tt107"></td>
		<td id="tt108"></td>
		<td id="tt109"></td>
		<td id="tt110"></td>
		<td id="tt111"></td>
		<td id="tt112"></td>
		<td id="tt113"></td>
		<td id="tt114"></td>
		<td id="tt115"></td>
		<td id="tt116"></td>
		<td id="tt117"></td>
		<td id="tt118"></td>
		<td id="tt119"></td>
	</tr>
	<tr>
		<th>토<small>요일</small></th>
		<td id="tt120"></td>
		<td id="tt121"></td>
		<td id="tt122"></td>
		<td id="tt123"></td>
		<td id="tt124"></td>
		<td id="tt125"></td>
		<td id="tt126"></td>
		<td id="tt127"></td>
		<td id="tt128"></td>
		<td id="tt129"></td>
		<td id="tt130"></td>
		<td id="tt131"></td>
		<td id="tt132"></td>
		<td id="tt133"></td>
		<td id="tt134"></td>
		<td id="tt135"></td>
		<td id="tt136"></td>
		<td id="tt137"></td>
		<td id="tt138"></td>
		<td id="tt139"></td>
		<td id="tt140"></td>
		<td id="tt141"></td>
		<td id="tt142"></td>
		<td id="tt143"></td>
	</tr>
	<tr>
		<th>일<small>요일</small></th>
		<td id="tt144"></td>
		<td id="tt145"></td>
		<td id="tt146"></td>
		<td id="tt147"></td>
		<td id="tt148"></td>
		<td id="tt149"></td>
		<td id="tt150"></td>
		<td id="tt151"></td>
		<td id="tt152"></td>
		<td id="tt153"></td>
		<td id="tt154"></td>
		<td id="tt155"></td>
		<td id="tt156"></td>
		<td id="tt157"></td>
		<td id="tt158"></td>
		<td id="tt159"></td>
		<td id="tt160"></td>
		<td id="tt161"></td>
		<td id="tt162"></td>
		<td id="tt163"></td>
		<td id="tt164"></td>
		<td id="tt165"></td>
		<td id="tt166"></td>
		<td id="tt167"></td>
	</tr>
</table>

						<div class="text-center mt-2">
							이용 방법
							<span data-toggle="tooltip" data-placement="top" title="위의 요일별 시간 테이블의 칸을 클릭 혹은 마우스 드래그를 하여 운영 시간을 지정하세요.">
								<span class="fa-regular fa-circle-info text-info"></span>
							</span>
							<span class="px-3"><span class="fa-regular fa-slash-forward fa-2xs text-muted"></span></span>
							주의 사항
							<span data-toggle="tooltip" data-placement="top" title="운영 시간은 반드시 지정되어야 합니다.">
								<span class="fa-regular fa-triangle-exclamation text-yellow"></span>
							</span>
						</div>
					</div>
				</div>

			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button name="save-btn" type="button" class="btn btn-primary" onclick='saveForm2()'>저장</button>
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


<style>

/* 시간 설정 테이블 */
table#base-time-table {
	border-spacing: 0px;
}
table#base-time-table th {
	font-weight: 400;
}
table#base-time-table td {
	border: 1px solid #fff;
	width: 28px;
	height: 30px;
	margin: 10px;
	background-color: rgba(24,28,33,0.06) !important;
	cursor: default;
	padding: 0px;
}
table#base-time-table td.selected {
	background-color: #02a96b !important;
}
table#base-time-table td.rselected {
	background-color: #02BC77 !important;
}
table#base-time-table td.nzselected {
	background-color: #f00 !important;
}

</style>

<!--  / Forms -->

<!--  Scripts -->

<script>

var selAdType = "";


function setSelAdType(type) {
	
	if (type == "L") {
		$("#sel-ad-type-playlist-a").addClass("btn-primary").removeClass("btn-outline-primary");
		$("#sel-ad-type-realtime-a").addClass("btn-outline-primary").removeClass("btn-primary");
		
		$("#sel-ad-type-playlist-icon").show();
		$("#sel-ad-type-realtime-icon").hide();
		
		$("#sel-ad-type-playlist-text").text("선택됨");
		$("#sel-ad-type-realtime-text").text("선택");
	} else {
		$("#sel-ad-type-playlist-a").addClass("btn-outline-primary").removeClass("btn-primary");
		$("#sel-ad-type-realtime-a").addClass("btn-primary").removeClass("btn-outline-primary");
		
		$("#sel-ad-type-playlist-icon").hide();
		$("#sel-ad-type-realtime-icon").show();
		
		$("#sel-ad-type-playlist-text").text("선택");
		$("#sel-ad-type-realtime-text").text("선택됨");
	}
	
	selAdType = type;
}


function setBizHourStr() {

	var val = "${bizHour}";
	if (val.length == 168) {
		for(var i = 0; i < 168; i++) {
			$("#btt" + i).removeClass("selected rselected nselected");
			if (Number(val.substr(i, 1)) == 1) {
				$("#btt" + i).addClass("rselected");
			}
		}
	}
}


function initForm2(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-2").html()));
	
	$('[data-toggle="tooltip"]').tooltip();

	
	$("#form-2 span[name='subtitle']").text(subtitle ? subtitle : "추가");
}


function editBizTime() {
	
	initForm2("운영 시간");

	$("#form-2").attr("url", "${updateTimeUrl}");
	
	$("#form-2 input[name='shortName']").val("${shortName}");
	$("#form-2 input[name='name']").val("${name}");


	// Time table 제어 코드
	timeTable = $("#time-table");
	timeTable.find("td").mousedown(function (e) {

		if($(this).hasClass("rselected")){
			isTimeMouseDown = true;
		} else {
			isTimeMouseDown2 = true;		  
		}
		  
		var cell = $(this);

		if (e.shiftKey) {
			ttSelectTo(cell);                
		} else {
			if(cell.hasClass("rselected")){
				cell.removeClass("rselected")
				timeTableIds.splice(cell.attr('id').substr(2),1,0);
			} else {
				cell.addClass("selected")
				timeTableIds.splice(cell.attr('id').substr(2),1,1);
			}

			timeStartCellIndex = cell.index() - 1;
			timeStartRowIndex = cell.parent().index();
		}
	      
		return false; // prevent text selection
		
	}).mouseover(function () {

		if (isTimeMouseDown){
			timeTable.find(".nselected").addClass("rselected");
			ttSelectTo2($(this));
		}
		if(isTimeMouseDown2) {
			timeTable.find(".selected").removeClass("selected");
			ttSelectTo($(this));
		}
		
	}).bind("selectstart", function () {

		return false;
	});

	// 운영 시간 표시
	ttSetValueStr("${bizHour}", true);
	mouseUpAct();
	
	
	$('#form-modal-2 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-2").modal();
	validateBizHours();
}


function mouseUpAct() {
	
	var cnt = 0;
	for(var i = 0; i < timeTableIds.length; i++) {
		if (timeTableIds[i] == 1) {
			cnt ++;
		}
	}
	
	if (Number($("#form-2 input[name='bizHrs']").val()) != cnt) {
		$("#form-2 input[name='bizHrs']").val(cnt);
	}
	validateBizHours();
}


function validateBizHours() {

	if (Number($("#form-2 input[name='bizHrs']").val()) == 0) {
		$("#form-2 button[name='save-btn']").addClass("disabled");
	} else {
		$("#form-2 button[name='save-btn']").removeClass("disabled");
	}
}


function saveForm2() {

	if (Number($("#form-2 input[name='bizHrs']").val()) > 0) {
		var data = {
		   		id: Number($("#form-2").attr("rowid")),
		   		bizHour: ttGetValueStr(),
		   	};
		    	
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: $("#form-2").attr("url"),
			data: JSON.stringify(data),
			success: function (form) {
				showSaveSuccessMsg();
				$("#form-modal-2").modal("hide");

				setTimeout(function(){
					location.reload();
				}, 1000);
			},
			error: ajaxSaveError
		});
	}
}


function mouseUpAct() {
	
	var cnt = 0;
	for(var i = 0; i < timeTableIds.length; i++) {
		if (timeTableIds[i] == 1) {
			cnt ++;
		}
	}
	
	if (Number($("#form-2 input[name='bizHrs']").val()) != cnt) {
		$("#form-2 input[name='bizHrs']").val(cnt);
	}
	validateBizHours();
}


function downloadGuideDoc() {
	
	location.href = "/adn/common/download?type=XlsTemplate&file=Inventory_Data_Setting_Guide.pdf";
}


$(document).ready(function() {
	
	//$('[data-toggle="tooltip"]').tooltip();


	$("#save-btn").click(function(e) {
		
    	var data = {
    		selAdType: selAdType,
			invenValues: $.trim($("input[name='invenValues']").val()),
		};

    	$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${updateUrl}",
			data: JSON.stringify(data),
			success: function (form) {
				showAlertModal("success", "설정이 변경되었습니다.");
			},
			error: ajaxSaveError
		});
	});
	

	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${readUrl}",
		data: JSON.stringify({ }),
		success: function (data, status) {
			console.log(data);
			for(var i in data) {
				if (data[i].code == "selAd.type") {
					setSelAdType(data[i].value);
				} else if (data[i].code == "inven.default") {
					$("input[name='invenValues']").val(data[i].value);
				}
			}
		},
		error: ajaxReadError
	});
	
	setBizHourStr();
});

</script>

<!--  / Scripts -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmTimeTable />
<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
