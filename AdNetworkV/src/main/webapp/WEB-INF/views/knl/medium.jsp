<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/knl/medium/create" var="createUrl" />
<c:url value="/knl/medium/read" var="readUrl" />
<c:url value="/knl/medium/update" var="updateUrl" />
<c:url value="/knl/medium/updateTime" var="updateTimeUrl" />
<c:url value="/knl/medium/destroy" var="destroyUrl" />


<!-- Opening tags -->

<common:pageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}
</h4>

<hr class="border-light container-m--x mt-0 mb-4">





<!-- Page body -->


<!-- Java(optional)  -->

<%
	String editTemplate =
			"<div class='text-nowrap'>" +
				"<button type='button' onclick='edit(#= id #)' class='btn icon-btn btn-sm btn-outline-success borderless'>" + 
				"<span class='fas fa-pencil-alt'></span></button>" +
				"<span class='d-none d-sm-inline pl-1'>" +
					"<button type='button' onclick='editTime(#= id #)' class='btn icon-btn btn-sm btn-outline-secondary borderless'>" + 
					"<span class='fa-regular fa-clock'></span></button>" +
				"</span>" +
			"</div>";

	String effStartDateTemplate = net.doohad.utils.Util.getSmartDate("effectiveStartDate", false, false);
	String effEndDateTemplate = net.doohad.utils.Util.getSmartDate("effectiveEndDate", false, false);
	
	String defaultDurSecTemplate =
			"<span>#= defaultDurSecs #</span>" +
			"# if (rangeDurAllowed) { #" +
				" <span>(#= minDurSecs # ~ #= maxDurSecs #)</span>" +
			"# } #";
	
					
	String noRecordsTemplate =
			"<div class='container text-center my-4'>" +
				"<div class='d-flex justify-content-center align-self-center'>" +
					"<span class='fa-thin fa-hexagon-exclamation fa-3x'></span>" +
					"<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>해당 자료 없음</span>" +
				"</div>" +
			"</div>";
%>


<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid" pageable="true" filterable="true" sortable="true" scrollable="true" reorderable="true" resizable="true" selectable="single" >
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
    <kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button id="add-btn" type="button" class="btn btn-outline-success">추가</button>
    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">엑셀</button>
    		</div>
    		<div class="float-right">
    			<button id="delete-btn" type="button" class="btn btn-danger">삭제</button>
    		</div>
    	</div>
   	</kendo:grid-toolbarTemplate>
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="수정..." width="80" filterable="false" sortable="false" template="<%= editTemplate %>" />
		<kendo:grid-column title="매체ID" field="shortName" width="150" />
		<kendo:grid-column title="매체" field="name" width="150" />
		<kendo:grid-column title="화면해상도" field="resolutions" width="250" template="#= dispBadgeValues(resolutions) #" />
		<kendo:grid-column title="1주 운영시간" field="bizHours" width="120" filterable="false" sortable="false" />
		<kendo:grid-column title="재생시간(초)" field="defaultDurSecs" width="150" template="<%= defaultDurSecTemplate %>" />
		<kendo:grid-column title="유효시작일" field="effectiveStartDate" template="<%= effStartDateTemplate %>" width="150" />
		<kendo:grid-column title="유효종료일" field="effectiveEndDate" template="<%= effEndDateTemplate %>" width="150" />
	</kendo:grid-columns>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="shortName" dir="asc"/>
		</kendo:dataSource-sort>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readUrl}" dataType="json" type="POST" contentType="application/json"/>
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
					<kendo:dataSource-schema-model-field name="effectiveStartDate" type="date" />
					<kendo:dataSource-schema-model-field name="effectiveEndDate" type="date" />
					<kendo:dataSource-schema-model-field name="defaultDurSecs" type="number" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

<!-- / Kendo grid  -->


<!-- Grid button actions  -->

<script>
$(document).ready(function() {

	// Add
	$("#add-btn").click(function(e) {
		e.preventDefault();
		
		initForm1();

		
		$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-1").modal();
	});
	// / Add
	
	// Delete
	$("#delete-btn").click(function(e) {
		e.preventDefault();
			
		var grid = $("#grid").data("kendoGrid");
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


<!--  Forms -->

<script id="template-1" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-1">
	<div class="modal-dialog">
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
				<div class="form-row">
					<div class="form-group col">
						<label class="form-label">
							매체ID
							<span class="text-danger">*</span>
						</label>
						<input name="shortName" type="text" maxlength="50" class="form-control required">
					</div>
					<div class="form-group col">
						<label class="form-label">
							매체명
							<span class="text-danger">*</span>
						</label>
						<input name="name" type="text" maxlength="100" class="form-control required">
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col">
						<label class="form-label">
							유효시작일
							<span class="text-danger">*</span>
						</label>
						<input name="effectiveStartDate" type="text" class="form-control required">
					</div>
					<div class="form-group col">
						<label class="form-label">
							유효종료일
						</label>
						<input name="effectiveEndDate" type="text" class="form-control">
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col">
						<label class="form-label">
							API 키
						</label>
						<div class="input-group">
							<div class="input-group-prepend">
								<div class="input-group-text">
									<label class="custom-control custom-checkbox px-2 m-0">
										<input name="apiChecked" type="checkbox" class="custom-control-input">
										<span class="custom-control-label"></span>
									</label>
								</div>
							</div>
							<input name="apiKey" type="text" placeholder="[자동 생성]" maxlength="22" class="form-control">
						</div>
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col">
						<label class="form-label">
							화면 해상도
							<span class="text-danger">*</span>
						</label>
						<select name="resolutions" class="selectpicker bg-white required" data-style="btn-default" data-none-selected-text="" data-size="7" multiple>
							<optgroup label="가로형">
								<option value="1024x768">1024 x 768</option>
								<option value="1360x768">1360 x 768</option>
								<option value="1366x768">1366 x 768</option>
								<option value="1920x1080">1920 x 1080</option>
								<option value="3840x1080">3840 x 1080</option>
								<option value="3840x2160">3840 x 2160</option>
							</optgroup>
							<optgroup label="세로형">
								<option value="768x1024">768 x 1024</option>
								<option value="768x1360">768 x 1360</option>
								<option value="768x1366">768 x 1366</option>
								<option value="1080x1920">1080 x 1920</option>
								<option value="1080x3840">1080 x 3840</option>
								<option value="2160x3840">2160 x 3840</option>
							</optgroup>
							<optgroup label="커스텀">
								<option value="800x1728">800 x 1728</option>
							</optgroup>
						</select>
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col">
						<label class="form-label">
							기본 재생시간
							<span class="text-danger">*</span>
						</label>
						<div class="input-group">
							<input name="defaultDurSecs" type="text" class="form-control required">
							<div class="input-group-append">
								<span class="input-group-text">초</span>
							</div>
						</div>
					</div>
					<div class="form-group col">
						<label class="form-label">
							재생시간 범위 내 다운로드 허용
						</label>
						<div class="pt-1">
							<label class="switcher switcher-lg">
								<input type="checkbox" class="switcher-input" name="rangeDurAllowed">
								<span class="switcher-indicator">
									<span class="switcher-yes">
										<span class="fa-solid fa-lock-open"></span>
									</span>
									<span class="switcher-no">
										<span class="fa-solid fa-lock"></span>
									</span>
								</span>
							</label>
						</div>
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col">
						<label class="form-label">
							최저 재생시간
							<span class="text-danger">*</span>
						</label>
						<div class="input-group">
							<input name="minDurSecs" type="text" class="form-control required">
							<div class="input-group-append">
								<span class="input-group-text">초</span>
							</div>
						</div>
					</div>
					<div class="form-group col">
						<label class="form-label">
							최고 재생시간
							<span class="text-danger">*</span>
						</label>
						<div class="input-group">
							<input name="maxDurSecs" type="text" class="form-control required">
							<div class="input-group-append">
								<span class="input-group-text">초</span>
							</div>
						</div>
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col">
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
					<div class="form-group col-4">
						<label class="form-label">
							매체ID
						</label>
						<input name="shortName" type="text" maxlength="50" class="form-control" readonly>
					</div>
					<div class="form-group col-5">
						<label class="form-label">
							매체명
						</label>
						<input name="name" type="text" maxlength="100" class="form-control" readonly>
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

<!--  / Forms -->


<!--  Scripts -->

<script>

function dispBadgeValues(values) {
	
	var ret = "";
	var value = values.split("|");
	  
	for(var i = 0; i < value.length; i ++) {
		if (value[i]) {
			ret = ret + "<span class='badge badge-outline-secondary'>" +
					value[i] + "</span><span class='pl-1'></span>";
		}
	}
	  
	return ret;
}


function initForm1(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-1").html()));
	
	$("#form-1 input[name='effectiveStartDate']").kendoDatePicker({
		format: "yyyy-MM-dd",
		parseFormats: [
			"yyyy-MM-dd",
		],
		value: new Date(),
	});
	
	$("#form-1 input[name='effectiveEndDate']").kendoDatePicker({
		format: "yyyy-MM-dd",
		parseFormats: [
			"yyyy-MM-dd",
		],
	});

	$("#form-1 textarea[name='memo']").keypress(function (e) {
		if (e.keyCode != 13) {
			return;
		}
		
		$(this).text().replace(/\n/g, "");
		
		return false;
	});
	
	$("#form-1 input[name='apiKey']").prop("disabled", true);
	$("#form-1 input[name='apiChecked']").click(function (e) {
		if($(this).is(':checked')){
			$("#form-1 input[name='apiKey']").prop("disabled", false);
			$("#form-1 input[name='apiKey']").focus();
			$("#form-1 input[name='apiKey']").attr("placeholder", "[직접 입력]");
		} else {
			$("#form-1 input[name='apiKey']").prop("disabled", true);
			$("#form-1 input[name='apiKey']").attr("placeholder", "[자동 생성]");
		}
	});

	
	$("#form-1 select[name='resolutions']").selectpicker('render');
	bootstrapSelectVal($("#form-1 select[name='resolutions']"), "");

	$("#form-1 input[name='minDurSecs']").prop("disabled", true);
	$("#form-1 input[name='maxDurSecs']").prop("disabled", true);

	
	// 재생시간 항목들의 기본값 설정
	$("#form-1 input[name='defaultDurSecs']").val(15);
	$("#form-1 input[name='minDurSecs']").val(10);
	$("#form-1 input[name='maxDurSecs']").val(20);
	
	$("#form-1 input[name='rangeDurAllowed']").change(function() {
		$("#form-1 input[name='minDurSecs']").prop("disabled", !$(this).is(":checked"));
		$("#form-1 input[name='maxDurSecs']").prop("disabled", !$(this).is(":checked"));
	});

	
	$("#form-1 span[name='subtitle']").text(subtitle ? subtitle : "추가");
	
	$("#form-1").validate({
		rules: {
			shortName: {
				minlength: 2, alphanumeric: true,
			},
			name: {
				minlength: 2
			},
			effectiveStartDate: { date: true },
			effectiveEndDate: { date: true },
			apiKey: {
				minlength: 22, maxlength: 22, alphanumeric: true,
			},
			defaultDurSecs: { digits: true, min: 5 },
			minDurSecs: { digits: true, min: 5 },
			maxDurSecs: { digits: true, min: 5 },
		}
	});
}


function saveForm1() {

	// kendo datepicker validation
	validateKendoDateValue($("#form-1 input[name='effectiveStartDate']"));
	validateKendoDateValue($("#form-1 input[name='effectiveEndDate']"));
	
	
	// disabled 상태에서는 validation 진행 안하기 때문
	var currStatus = $("#form-1 input[name='apiKey']").prop("disabled");
	$("#form-1 input[name='apiKey']").prop("disabled", false);
	
	if ($("#form-1").valid()) {
    	var data = {
    		id: Number($("#form-1").attr("rowid")),
    		shortName: $.trim($("#form-1 input[name='shortName']").val()),
    		name: $.trim($("#form-1 input[name='name']").val()),
    		apiKey: $.trim($("#form-1 input[name='apiKey']").val()),
    		effectiveStartDate: $("#form-1 input[name='effectiveStartDate']").data("kendoDatePicker").value(),
    		effectiveEndDate: $("#form-1 input[name='effectiveEndDate']").data("kendoDatePicker").value(),
    		resolutions: $("#form-1 select[name='resolutions']").val(),
    		defaultDurSecs: Number($("#form-1 input[name='defaultDurSecs']").val()),
    		rangeDurAllowed: $("#form-1 input[name='rangeDurAllowed']").is(':checked'),
    		minDurSecs: Number($("#form-1 input[name='minDurSecs']").val()),
    		maxDurSecs: Number($("#form-1 input[name='maxDurSecs']").val()),
    		memo: $.trim($("#form-1 textarea[name='memo']").val()),
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
				$("#grid").data("kendoGrid").dataSource.read();
			},
			error: ajaxSaveError
		});
	}
	
	$("#form-1 input[name='apiKey']").prop("disabled", currStatus);
}


function edit(id) {
	
	initForm1("변경");

	var dataItem = $("#grid").data("kendoGrid").dataSource.get(id);
	
	$("#form-1").attr("rowid", dataItem.id);
	$("#form-1").attr("url", "${updateUrl}");
	
	$("#form-1 input[name='shortName']").val(dataItem.shortName);
	$("#form-1 input[name='name']").val(dataItem.name);
	$("#form-1 input[name='apiKey']").val(dataItem.apiKey);
	
	$("#form-1 input[name='effectiveStartDate']").data("kendoDatePicker").value(dataItem.effectiveStartDate);
	$("#form-1 input[name='effectiveEndDate']").data("kendoDatePicker").value(dataItem.effectiveEndDate);

	// 해상도 값에 대해 select 에 맞추어 변경
	var resolutions = "[ \"" + dataItem.resolutions.replaceAll("|", "\", \"") + "\" ]";
	bootstrapSelectVal($("#form-1 select[name='resolutions']"), eval(resolutions));

	$("#form-1 input[name='defaultDurSecs']").val(dataItem.defaultDurSecs);
	$("#form-1 input[name='minDurSecs']").val(dataItem.minDurSecs);
	$("#form-1 input[name='maxDurSecs']").val(dataItem.maxDurSecs);
	
	$("#form-1 input[name='minDurSecs']").prop("disabled", !dataItem.rangeDurAllowed);
	$("#form-1 input[name='maxDurSecs']").prop("disabled", !dataItem.rangeDurAllowed);

	$("#form-1 input[name='rangeDurAllowed']").prop("checked", dataItem.rangeDurAllowed);
	
	$("#form-1 textarea[name='memo']").text(dataItem.memo);

	
	$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-1").modal();
}


function initForm2(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-2").html()));
	
	$('[data-toggle="tooltip"]').tooltip();

	
	$("#form-2 span[name='subtitle']").text(subtitle ? subtitle : "추가");
}


function editTime(id) {
	
	initForm2("운영 시간");

	var dataItem = $("#grid").data("kendoGrid").dataSource.get(id);
	
	$("#form-2").attr("rowid", dataItem.id);
	$("#form-2").attr("url", "${updateTimeUrl}");
	
	$("#form-2 input[name='shortName']").val(dataItem.shortName);
	$("#form-2 input[name='name']").val(dataItem.name);


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
	ttSetValueStr(dataItem.bizHour, true);
	mouseUpAct();
	
	
	$('#form-modal-2 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-2").modal();
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
				$("#grid").data("kendoGrid").dataSource.read();
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

</script>

<!--  / Scripts -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmTimeTable />
<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
