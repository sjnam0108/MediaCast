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

<c:url value="/adc/creative/timetarget/save" var="saveUrl" />
<c:url value="/adc/creative/timetarget/destory" var="destoryUrl" />


<!-- Opening tags -->

<common:mediumPageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 fa-light fa-alarm-clock fa-fw"></span>
	${pageTitle}
	<span class="font-weight-light pl-1">시간 타겟팅</span>
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
		<a class="nav-link active" href="/adc/creative/timetarget/${Advertiser.id}">
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
		location.href = "/adc/creative/timetarget/${Advertiser.id}/" + $("select[name='nav-item-creat-select']").val();
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
	<div class="card-body">
		<div class="pb-2">
	    	<div class="clearfix">
	    		<div class="float-left">
					시간 타겟팅
					<span class="small text-muted pl-3">타겟팅이 등록되지 않을 경우에는 모든 요일, 모든 시간대에 광고가 노출됩니다.</span>
	    		</div>
	    		<div class="float-right">
	    			<div id="time-target-no-data-row-menu">
						<button type='button' onclick='addExpTime()' class='btn icon-btn btn-sm btn-outline-success'> 
							<span class='fas fa-plus-large'></span>
						</button>
	    			</div>
	    			<div id="time-target-data-row-menu">
						<button type='button' onclick='updateExpTime()' class='btn icon-btn btn-sm btn-outline-success'> 
							<span class='fas fa-pencil-alt'></span>
						</button>
						<button type='button' onclick='deleteExpTime()' class='btn icon-btn btn-sm btn-outline-danger'> 
							<span class='fas fa-trash-can'></span>
						</button>
	    			</div>
	    		</div>
	    	</div>
		</div>
		<div class="form-row" id="time-target-no-data-row">
			<div class='container text-center my-4'>
				<div class='d-flex justify-content-center align-self-center'>
					<span class='fa-thin fa-hexagon-exclamation fa-3x'></span>
					<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>해당 자료 없음</span>
				</div>
			</div>
		</div>
		<div class="form-row" id="time-target-data-row">
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
</div>


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


<!--  Forms -->

<script id="template-7" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-7">
	<div class="modal-dialog modal-lg">
		<form class="modal-content" id="form-7" rowid="-1" url="${createUrl}">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					광고 소재
					<span class="font-weight-light pl-1"><span name="subtitle"></span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
				<div class="form-row">
					<div class="form-group col-8">
						<label class="form-label">
							광고 소재명
						</label>
						<input name="name" type="text" maxlength="100" class="form-control" readonly>
					</div>
					<div class="form-group col-4">
						<label class="form-label">
							1주일 총 노출시간
						</label>
						<div class="input-group">
							<input name="expHrs" type="text" class="form-control" value="0" readonly>
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
							<span data-toggle="tooltip" data-placement="top" title="노출 시간은 반드시 지정되어야 합니다.">
								<span class="fa-regular fa-triangle-exclamation text-yellow"></span>
							</span>
						</div>
					</div>
				</div>

			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button name="save-btn" type="button" class="btn btn-primary" onclick='saveForm7()'>저장</button>
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


<script>
$(document).ready(function() {
	
	currExpTime = "${Creative.expHour}";
	
	if ("${Creative.expHour}".length == 168) {
		$("#time-target-no-data-row").hide();
		$("#time-target-data-row").show();
		$("#time-target-no-data-row-menu").hide();
		$("#time-target-data-row-menu").show();
	} else {
		$("#time-target-no-data-row").show();
		$("#time-target-data-row").hide();
		$("#time-target-no-data-row-menu").show();
		$("#time-target-data-row-menu").hide();
	}

	setExpHourStr();
});
</script>

<script>

var currExpTime = "";


function setExpHourStr() {

	$("#time-target-tab-title").text("시간 타겟팅");

	if (currExpTime.length == 168) {
		var cnt = 0;
		for(var i = 0; i < 168; i++) {
			$("#btt" + i).removeClass("selected rselected nselected");
			if (Number(currExpTime.substr(i, 1)) == 1) {
				$("#btt" + i).addClass("rselected");
				cnt ++;
			}
		}
		
		$("#time-target-tab-title").text("시간 타겟팅(" + cnt + "hrs)");
	}
}


function addExpTime() {

	editExpTime("111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
}


function updateExpTime() {

	editExpTime(currExpTime);
}


function mouseUpAct() {
	
	var cnt = 0;
	for(var i = 0; i < timeTableIds.length; i++) {
		if (timeTableIds[i] == 1) {
			cnt ++;
		}
	}
	
	if (Number($("#form-7 input[name='expHrs']").val()) != cnt) {
		$("#form-7 input[name='expHrs']").val(cnt);
	}
	validateExpHours();
}


function validateExpHours() {

	if (Number($("#form-7 input[name='expHrs']").val()) == 0) {
		$("#form-7 button[name='save-btn']").addClass("disabled");
	} else {
		$("#form-7 button[name='save-btn']").removeClass("disabled");
	}
}


function editExpTime(expHour) {
	
	$("#formRoot").html(kendo.template($("#template-7").html()));
	
	$('[data-toggle="tooltip"]').tooltip();

	
	$("#form-7 span[name='subtitle']").text("노출 시간");

	
	$("#form-7").attr("url", "${saveUrl}");
	
	$("#form-7 input[name='name']").val("${Creative.name}");


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

	// 노출 시간 표시
	ttSetValueStr(expHour, true);
	mouseUpAct();
	
	
	$('#form-modal-7 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-7").modal();
}


function saveForm7() {

	if (Number($("#form-7 input[name='expHrs']").val()) > 0) {
		var data = {
		   		id: ${Creative.id},
		   		expHour: ttGetValueStr(),
		   	};
		    	
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: $("#form-7").attr("url"),
			data: JSON.stringify(data),
			success: function (form) {
				showSaveSuccessMsg();
				$("#form-modal-7").modal("hide");

				$("#time-target-no-data-row").hide();
				$("#time-target-data-row").show();
				$("#time-target-no-data-row-menu").hide();
				$("#time-target-data-row-menu").show();

				currExpTime = ttGetValueStr();
				setExpHourStr();
			},
			error: ajaxSaveError
		});
	}
}


function deleteExpTime() {
	
	showConfirmModal("광고 소재에 설정된 시간 타겟팅 자료의 삭제 작업을 수행할 예정입니다. 계속 진행하시겠습니까?", function(result) {
		if (result) {
			$.ajax({
				type: "POST",
				contentType: "application/json",
				dataType: "json",
				url: "${destoryUrl}",
				data: JSON.stringify({ id: ${Creative.id} }),
				success: function (form) {
					showDeleteSuccessMsg();

					$("#time-target-no-data-row").show();
					$("#time-target-data-row").hide();
					$("#time-target-no-data-row-menu").show();
					$("#time-target-data-row-menu").hide();

					currExpTime = "";
					setExpHourStr();
				},
				error: ajaxDeleteError
			});
		}
	});
}

</script>


</c:otherwise>
</c:choose>

<!--  / Page details -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmTimeTable />
<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
