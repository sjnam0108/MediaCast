<%@ tag pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!-- URL -->

<c:url value="/dsg/noctask/createAct" var="createActUrl" />


<!-- Modals -->

<div class="modal fade modal-level-minus-2" data-backdrop="static" id="nocActionModal">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header move-cursor"">
                <h5 class="modal-title">
                	<span name="title">${noc_titleTaskView}</span>
					<span class="font-weight-light pl-1"><span name="subtitle"></span>
                </h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">×</span>
                </button>
            </div>
            <div class="modal-body modal-bg-color py-3">
				<div class='d-flex align-items-center justify-content-center py-4'>${wait_plaseWait}</div>
            </div>
            <div class="modal-footer d-flex">
                <button type="button" class="btn btn-round btn-outline-success mr-auto" onclick='openActionForm()'>
                	<span class="fas fa-plus-circle"></span>
                	<span class="ml-1">${cmd_add}</span>
                </button>
                <button type="button" class="btn btn-primary" data-dismiss="modal">${confirm_ok}</button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade modal-level-minus-1" data-backdrop="static" id="nocActionModal-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header move-cursor">
                <h5 class="modal-title">
                	<span name="title">${noc_titleTaskView}</span>
					<span class="font-weight-light pl-1"><span name="subtitle"></span>
                </h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">×</span>
                </button>
            </div>
            <div class="modal-body modal-bg-color py-3">
				<div class="card">
					<h6 class="card-header d-flex">
						<div class="d-none d-sm-inline pr-1">
							<span id="form-noctask-urgent" class="hidden">
								<span class="badge bg-danger text-white font-weight-normal small">${noc_urgent}</span>
							</span>
							<span class="hidden" id="form-noctask-taskType-F"><span class="fas fa-bolt"></span></span>
							<span class="hidden" id="form-noctask-taskType-S"><span class="fas fa-toolbox"></span></span>
							<span class="hidden" id="form-noctask-taskType-I"><span class="fas fa-truck"></span></span>
						</div>
						<span id="form-noctask-taskType"></span>
						<div class="ml-auto">
							<span class="badge text-white font-weight-normal small" id="form-noctask-status"></span>
						</div>
					</h6>
					<div class="card-body p-3">
						<div class="form-row">
							<div class="col-sm-6">
								<div class="form-group col">
									<label class="form-label">
										${noc_visitDate}
									</label>
									<input name="visitDate" type="text" class="form-control border-none">
								</div>
							</div>
							<div class="col-sm-6">
								<div class="form-group col">
									<label class="form-label">
										${noc_status}
									</label>
									<select name="status" class="selectpicker" data-style="btn-default" data-none-selected-text="">
										<optgroup label="${nStatus_groupNOC}">
											<option value="R">${nStatus_R}</option>
											<option value="P">${nStatus_P}</option>
											<option value="DS">${nStatus_DS}</option>
											<option value="DE">${nStatus_DE}</option>
											<option value="C">${nStatus_C}</option>
										</optgroup>
										<optgroup label="${nStatus_groupMA}">
											<option value="MW">${nStatus_MW}</option>
											<option value="MS">${nStatus_MS}</option>
											<option value="MV">${nStatus_MV}</option>
											<option value="MVNOC">${nStatus_MVNOC}</option>
											<option value="MVC">${nStatus_MVC}</option>
										</optgroup>
										<optgroup label="${nStatus_groupHW}">
											<option value="HW">${nStatus_HW}</option>
											<option value="HS">${nStatus_HS}</option>
											<option value="HV">${nStatus_HV}</option>
											<option value="HVNOC">${nStatus_HVNOC}</option>
											<option value="HVC">${nStatus_HVC}</option>
										</optgroup>
										<optgroup label="${nStatus_groupNW}">
											<option value="NW">${nStatus_NW}</option>
											<option value="NS">${nStatus_NS}</option>
											<option value="NV">${nStatus_NV}</option>
											<option value="NVNOC">${nStatus_NVNOC}</option>
											<option value="NVC">${nStatus_NVC}</option>
										</optgroup>
										<optgroup label="${nStatus_groupETC}">
											<option value="EW">${nStatus_EW}</option>
											<option value="ES">${nStatus_ES}</option>
											<option value="EV">${nStatus_EV}</option>
											<option value="EVNOC">${nStatus_EVNOC}</option>
											<option value="EVC">${nStatus_EVC}</option>
										</optgroup>
									</select>
									
								</div>
							</div>
						</div>
						<div class="form-row">
							<div class="col-sm-12">
								<div class="form-group col">
									<label class="form-label">
										${noc_desc}
									</label>
									<textarea name="desc" rows="3" class="form-control"></textarea>
								</div>
							</div>
						</div>
					</div>
				</div>
            </div>
            <div class="modal-footer d-flex">

<c:if test="${isAssetModuleApplied}">

                <button type="button" class="btn btn-round btn-outline-secondary mr-auto" onclick='openNocAssetHistory()'>
                	<span class="fas fa-history"></span>
                	<span class="ml-1">${noc_assetHistory}</span>
                </button>

</c:if>

				<button type="button" class="btn btn-default ml-auto" data-dismiss="modal">${form_cancel}</button>
				<button type="button" class="btn btn-primary" onclick='saveActionForm()'>${form_save}</button>
            </div>
        </div>
    </div>
</div>

<!-- / Modals -->


<!--  Scripts -->

<script>

var nocTaskId = null;
var nocStatus = null;

var nocAssetStbName = "";
var nocAssetStbId = -1;
var nocAssetPlayerID = "";
var nocAssetDesc = null;


$(document).ready(function() {

	$('#nocActionModal').on('show.bs.modal', function (e) {

	    $(this).find('.modal-body').load("/dsg/common/noctaskview?id=" + nocTaskId);

		setTimeout(function(){
			$('.modal-backdrop:last-child').addClass('modal-level-minus-2');
		});

	});

	$("#nocActionModal-1 select[name='status']").selectpicker('render');
	
	$('#nocActionModal-1').on('show.bs.modal', function (e) {

		setTimeout(function(){
			$('.modal-backdrop:last-child').addClass('modal-level-minus-1');
		});

	});

	$("#nocActionModal-1 textarea[name='desc']").keypress(function (e) {
		if (e.keyCode != 13) {
			return;
		}
		
		$(this).text().replace(/\n/g, "");
		
		return false;
	});
	
	$("#nocActionModal-1 input[name='visitDate']").kendoDateTimePicker({
		format: "yyyy-MM-dd HH:mm", 
		parseFormats: [
			"yyyy-MM-dd HH:mm", "yyyy-MM-dd HHmm", "yyyy-MM-dd HH mm", "yyyy-MM-dd HH",
			"yyyyMMdd HH:mm", "yyyyMMdd HHmm", "yyyyMMdd HH mm", "yyyyMMdd HH",
			"yyyy-MM-dd", "yyyyMMdd", "MMdd HH mm", "MMdd HHmm", "MMdd HH", "MMdd"
		],
		change: onKendoPickerChange,
	});

<c:if test="${isAssetModuleApplied}">

	$("#nocActionModal-1 select[name='status']").on("change.bs.select", function(e){
		var newVal = $("#nocActionModal-1 select[name='status']").val();
		if (newVal && (newVal == 'C' || newVal == 'MVNOC' || newVal == 'HVNOC' || newVal == 'NVNOC' || newVal == 'EVNOC')) {
			openNocAssetHistory();
		}
	});

</c:if>

});


function showNocAction(id, name, taskStatusType) {
	
	if (id == null || taskStatusType == null || taskStatusType == "null") {
		return;
	}
	
	nocTaskId = id;
	nocAssetDesc = $("#nocActionModal-1 textarea[name='desc']");

	$("#nocActionModal span[name='subtitle']").text(name);
	$("#nocActionModal-1 span[name='subtitle']").text(name);
	
    $("#nocActionModal .modal-body").html("<div class='d-flex align-items-center justify-content-center py-4'>${wait_plaseWait}</div>");
    
	$('#nocActionModal .modal-dialog').draggable({ handle: '.modal-header' });
	$("#nocActionModal").modal();

}


function updateActionFormTaskData(data) {
	
	if (data.urgent && data.urgent == "Y") {
		$("#form-noctask-urgent").show();
	}
	
	$("#form-noctask-taskType-F").hide();
	$("#form-noctask-taskType-S").hide();
	$("#form-noctask-taskType-I").hide();
	
	if (data.taskType.indexOf('F') == 0) { $("#form-noctask-taskType-F").show(); }
	else if (data.taskType.indexOf('S') == 0) { $("#form-noctask-taskType-S").show(); }
	else if (data.taskType.indexOf('I') == 0) { $("#form-noctask-taskType-I").show(); }
	
	$("#form-noctask-taskType").text(data.taskTypeDisp);
	
	$("#form-noctask-status").text(data.statusDisp);
	
	if (data.status.indexOf("M") == 0 || data.status.indexOf("H") == 0 || data.status.indexOf("N") == 0 || data.status.indexOf("E") == 0) {
		$("#form-noctask-status").addClass("bg-secondary");
	} else if (data.status == "R" || data.status == "P") {
		$("#form-noctask-status").addClass("bg-secondary");
	} else if (data.status.indexOf("D") == 0) {
		$("#form-noctask-status").addClass("bg-danger");
	} else if (data.status == "C") {
		$("#form-noctask-status").addClass("bg-blue");
	}
	
	nocStatus = data.status;
	
	nocAssetStbId = data.stbId;
	nocAssetStbName = data.stbName;
	nocAssetPlayerID = data.playerCode;
}


function openActionForm() {
	
	if (nocTaskId && nocStatus) {
		bootstrapSelectVal($("#nocActionModal-1 select[name='status']"), nocStatus);
		
		$("#nocActionModal-1 input[name='visitDate']").data("kendoDateTimePicker").value(null);
		$("#nocActionModal-1 textarea[name='desc']").val("");
		
		$('#nocActionModal-1 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#nocActionModal-1").modal();
	}
}


function saveActionForm() {
	
	validateKendoDateTimeValue($("#nocActionModal-1 input[name='visitDate']"));
	
	var data = {
   		taskId: nocTaskId,
   		status: $("#nocActionModal-1 select[name='status']").val(),
   		visitDate: $("#nocActionModal-1 input[name='visitDate']").data("kendoDateTimePicker").value(),
   		desc: $.trim($("#nocActionModal-1 textarea[name='desc']").val()),
   	};
	
	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${createActUrl}",
		data: JSON.stringify(data),
		success: function (form) {
			showSaveSuccessMsg();
			$("#nocActionModal-1").modal("hide");
			
		    $("#nocActionModal").find('.modal-body').load("/dsg/common/noctaskview?id=" + nocTaskId);
		},
		error: ajaxSaveError
	});
}


function openNocAssetHistory() {
	
	if (nocAssetStbName) {
		openAHByStbModal(nocAssetStbName, nocAssetPlayerID, nocAssetStbId, (nocAssetDesc != null));
	}
}

</script>

<!--  / Scripts -->
