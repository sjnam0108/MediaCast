<%@ tag pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!-- Layout -->

<script>

//Collapse menu
(function() {
  if ($('#layout-sidenav').hasClass('sidenav-horizontal') || window.layoutHelpers.isSmallScreen()) {
    return;
  }

  try {
    window.layoutHelpers.setCollapsed(
      localStorage.getItem('layoutCollapsed') === 'true',
      false
    );
  } catch (e) {}
})();

$(function() {
  // Initialize sidenav
  $('#layout-sidenav').each(function() {
    new SideNav(this, {
      orientation: $(this).hasClass('sidenav-horizontal') ? 'horizontal' : 'vertical'
    });
  });

  // Initialize sidenav togglers
  $('body').on('click', '.layout-sidenav-toggle', function(e) {
    e.preventDefault();
    window.layoutHelpers.toggleCollapsed();
    if (!window.layoutHelpers.isSmallScreen()) {
      try { localStorage.setItem('layoutCollapsed', String(window.layoutHelpers.isCollapsed())); } catch (e) {}
    }
  });

  if ($('html').attr('dir') === 'rtl') {
    $('#layout-navbar .dropdown-menu').toggleClass('dropdown-menu-right');
  }
});

</script>

<script>

$(document).ready(function() {
	$("#logout-btn").click(function() {
		location.href = "/logout";
	});
	
	$("#pwd-update-btn").click(function() {
		location.href = "/common/passwordupdate";
	});
});

</script>

<!-- / Layout -->



<!-- Notification -->

<script>

function showToastNotification(notifType, msg) {
    toastr.options = {
        showMethod: 'show',
        timeOut: 3000,
        positionClass: 'toast-top-right-custom',
    };
    
    if (notifType) {
    	if (notifType == "success") {
    		toastr.success(msg);
    	} else if (notifType == "info") {
    		toastr.info(msg);
    	} else if (notifType == "warning") {
    		toastr.warning(msg);
    	} else if (notifType == "error") {
    		toastr.error(msg);
    	}
    }
}

function showSaveSuccessMsg() {
	showToastNotification("success", "${msg_saveSuccess}");
}

function showOperationSuccessMsg() {
	showToastNotification("success", "${msg_operationSuccess}");
}

function showDeleteSuccessMsg() {
	showToastNotification("success", "${msg_deleteSuccess}");
}

function showSaveErrorMsg() {
	showToastNotification("error", "${msg_saveFailure}");
}

function showOperationErrorMsg() {
	showToastNotification("error", "${msg_operationFailure}");
}

function showDeleteErrorMsg() {
	showToastNotification("error", "${msg_deleteFailure}");
}

function showReadErrorMsg() {
	showToastNotification("error", "${msg_readFailure}");
}

</script>

<!-- / Notification -->



<!-- Alert modal -->

<script>

function showAlertModal(notifType, msg) {
	var className = "";
	if (notifType) {
		className += "bg-" + notifType + " text-white";
	}
	
	var box = bootbox.alert({
		size: "small",
		title: "${alert_title}",
		message: msg,
		backdrop: true,
		buttons: {
			ok: {
				label: '${alert_ok}',
			}
		},
		animate: false,
		show: true,
		className: "modal-level-top",
	}).init(function() {
		setTimeout(function(){
			$('.modal-backdrop:last-child').addClass('modal-level-top');
		});
	});
	
	box.find('.modal-level-top .modal-header').addClass(className);
	box.find('.modal-level-top .close').addClass("text-white");
	box.find('.modal-level-top .modal-dialog').addClass("modal-dialog-vertical-center");
	box.find('.modal-level-top .modal-content').addClass("modal-content-border-1");

<c:if test="${not isMobileMode}">

	box.find('.modal-level-top .modal-header').addClass("move-cursor");
	box.find('.modal-level-top .modal-dialog').draggable({ handle: '.modal-header' });

</c:if>

}


function showDelConfirmModal(callbackFunc, multiRow, rowCount) {
	
	var msg = "${msg_delCurrentConfirm}";
	
	if (multiRow && rowCount > 1) {

<c:if test="${not isMobileMode}">

		msg = "${msg_delConfirm}".replace("{0}", "<strong>" + rowCount + "</strong>");

</c:if>

	}
	
	showConfirmModal(msg, callbackFunc);
}


function showConfirmModal(message, callbackFunc) {
	
	var box = bootbox.confirm({
		size: "small",
		title: "확인",
		message: message,
		backdrop: "static",
		buttons: {
			cancel: {
				label: '취소',
				className: "btn-default",
			},
			confirm: {
				label: '확인',
				className: "btn-danger",
			}
		},
		animate: false,
		callback: callbackFunc,
		className: "modal-level-top",
	}).init(function() {
		setTimeout(function(){
			$('.modal-backdrop:last-child').addClass('modal-level-top');
		});
	});
	
	box.find('.modal-level-top .modal-dialog').addClass("modal-dialog-vertical-center");
	box.find('.modal-level-top .modal-content').addClass("modal-content-border-1");

<c:if test="${not isMobileMode}">

	box.find('.modal-level-top .modal-header').addClass("move-cursor");
	box.find('.modal-level-top .modal-dialog').draggable({ handle: '.modal-header' });

</c:if>

}

</script>

<!-- / Alert modal -->



<!-- Wait modal -->

<div class="modal modal-level-top" id="waitModal" data-backdrop="static">
	<div class="modal-dialog modal-sm wait-modal-sm">
		<div class="modal-content">
			<div class="modal-body wait-modal-body">
				<div class="sk-folding-cube sk-primary wait-cube">
					<div class="sk-cube1 sk-cube"></div>
					<div class="sk-cube2 sk-cube"></div>
					<div class="sk-cube4 sk-cube"></div>
					<div class="sk-cube3 sk-cube"></div>
				</div>
			</div>
			<div class="mx-auto mb-3">
				${wait_plaseWait}
			</div>
		</div>
	</div>
</div>

<script>

$(document).ready(function() {
	$("#waitModal").on('show.bs.modal', function (e) {
		setTimeout(function(){
			$('.modal-backdrop:last-child').addClass('modal-level-top');
		});
	});
});

function showWaitModal() {
	$("#waitModal").modal();
}

function hideWaitModal() {
	$("#waitModal").modal("hide");
}

</script>

<!-- / Wait modal -->



<!-- Remote modal -->

<script>

var recentTaskTimer = null;

</script>

<!-- / Remote modal -->



<!-- Recent task modal -->

<div class="modal fade" id="recentTaskModal" tabindex="-1" role="dialog">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header move-cursor"">
                <h5 class="modal-title">${window_recentTask}</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">×</span>
                </button>
            </div>
            <div class="modal-body modal-bg-color">
                <div class='d-flex align-items-center justify-content-center py-4'>${wait_plaseWait}</div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" data-dismiss="modal">${confirm_ok}</button>
            </div>
        </div>
    </div>
</div>

<script>

$(document).ready(function() {
	$('#recentTaskModal').on('show.bs.modal', function (e) {

	    $(this).find('.modal-body').load("/dsg/common/recenttask");

	});

	$('#recentTaskModal').on('hidden.bs.modal', function (e) {
		
		clearTimeout(recentTaskTimer);

	});
	
	$("#recent-task-btn").click(function() {
	    $("#recentTaskModal .modal-body").html("<div class='d-flex align-items-center justify-content-center py-4'>${wait_plaseWait}</div>");
	    
		$('#recentTaskModal .modal-dialog').draggable({ handle: '.modal-header' });
		$("#recentTaskModal").modal();
	});
});

</script>

<!-- / Recent task modal -->



<!-- Error handling -->

<script>

function ajaxReadError(e) {
	commonErrorHandling(e.responseText, showReadErrorMsg);
}

function ajaxDeleteError(e) {
	commonErrorHandling(e.responseText, showDeleteErrorMsg);
}

function ajaxSaveError(e) {
	commonErrorHandling(e.responseText, showSaveErrorMsg);
}

function ajaxOperationError(e) {
	commonErrorHandling(e.responseText, showOperationErrorMsg);
}

function kendoReadError(e) {
	commonErrorHandling(e.xhr.responseText, showReadErrorMsg);
}

function commonErrorHandling(text, showMsg) {
	if (text == "") {
		showMsg();
	} else {
		try {
			if (JSON.parse(text).error == "ToLoginPage" || JSON.parse(text).error == "ToLoginPage2") {
				showAlertModal("danger", "${msg_sessionExpired}");
				setTimeout(function(){
					location.href = "${url_login}";
				}, 1500);
			} else if (JSON.parse(text).error != "ReadError" && 
					JSON.parse(text).error != "SaveError" && 
					JSON.parse(text).error != "DeleteError" && 
					JSON.parse(text).error != "OperationError") {
				showAlertModal("danger", JSON.parse(text).error);
			} else {
				showMsg();
			}
		} catch (err) {
			console.log(err);
			showAlertModal("danger", "${msg_sessionExpired}");
			setTimeout(function(){
				location.href = "${url_login}";
			}, 11500);
		}
	}
}

</script>

<!-- / Error handling -->



<!-- Kendo grid column - checkbox filter -->

<script>

function kfcTextOnly(e) {
	
	if (e.field == "all") {
		return "<div class='mt-3 mx-3' style='min-width:150px;'>" +
				"  <label class='custom-control custom-checkbox'>" +
				"    <input type='checkbox' class='custom-control-input'>" +
				"    <span class='custom-control-label'>#= all #</span>" +
				"  </label>" +
				"</div>";
	} else {
		return "<div class='mx-3'>" +
				"  <label class='custom-control custom-checkbox'>" +
				"    <input type='checkbox' class='custom-control-input' value='#= value #'>" +
				"    <span class='custom-control-label'>#= text #</span>" +
				"  </label>" +
				"</div>";
	}
}


function kfcIconText(e) {
	
	if (e.field == "all") {
		return "<div class='mt-3 mx-3' style='min-width:150px;'>" +
				"  <label class='custom-control custom-checkbox'>" +
				"    <input type='checkbox' class='custom-control-input'>" +
				"    <span class='custom-control-label'>#= all #</span>" +
				"  </label>" +
				"</div>";
	} else {
		return "<div class='mx-3'>" +
				"  <label class='custom-control custom-checkbox'>" +
				"    <input type='checkbox' class='custom-control-input' value='#= value #'>" +
				"    <span class='custom-control-label'><span class='#= icon #'></span> #= text #</span>" +
				"  </label>" +
				"</div>";
	}
}


function kfc2IconText(e) {
	
	if (e.field == "all") {
		return "<div class='mt-3 mx-3' style='min-width:150px;'>" +
				"  <label class='custom-control custom-checkbox'>" +
				"    <input type='checkbox' class='custom-control-input'>" +
				"    <span class='custom-control-label'>#= all #</span>" +
				"  </label>" +
				"</div>";
	} else {
		return "<div class='mx-3'>" +
				"  <label class='custom-control custom-checkbox'>" +
				"    <input type='checkbox' class='custom-control-input' value='#= value #'>" +
				"    <span class='custom-control-label'><span class='#= icon #'></span><span class='#= subIcon #'></span> #= text #</span>" +
				"  </label>" +
				"</div>";
	}
}

</script>

<!-- / Kendo grid column - checkbox filter -->



<!-- Kendo grid tab -->

<script>

function setTabItemChecked(obj, val) {
	
	if (val) {
		obj.removeClass("fa-blank").addClass("fa-check");
	} else {
		obj.removeClass("fa-check").addClass("fa-blank");
	}
}

function showColumns(var1, grid) {
	
	var grid1 = grid;
	if (grid1 == null) {
		grid1 = $("#grid").data("kendoGrid");
	}

	if (grid1 != null) {
		if (var1.constructor === Array) {
			var1.forEach(function (item) {
				grid1.showColumn(item);
			});
		} else if (!isNaN(var1)) {
			grid1.showColumn(var1);
		}
	}
}

function hideColumns(var1, grid) {
	
	var grid1 = grid;
	if (grid1 == null) {
		grid1 = $("#grid").data("kendoGrid");
	}

	if (grid1 != null) {
		if (var1.constructor === Array) {
			var1.forEach(function (item) {
				grid1.hideColumn(item);
			});
		} else if (!isNaN(var1)) {
			grid1.hideColumn(var1);
		}
	}
}

function showRangeColumns(var1, var2, grid) {
	
	var grid1 = grid;
	if (grid1 == null) {
		grid1 = $("#grid").data("kendoGrid");
	}
	
	if (grid1 != null && !isNaN(var1) && !isNaN(var2)) {
		for (var i = var1; i < var2 + 1; i ++) {
			grid1.showColumn(i);
		}
	}
}

function hideRangeColumns(var1, var2, grid) {
	
	var grid1 = grid;
	if (grid1 == null) {
		grid1 = $("#grid").data("kendoGrid");
	}
	
	if (grid1 != null && !isNaN(var1) && !isNaN(var2)) {
		for (var i = var1; i < var2 + 1; i ++) {
			grid1.hideColumn(i);
		}
	}
}

</script>

<!-- / Kendo grid tab -->



<!-- Kendo date / time / datetime picker change -->

<script>

function onKendoPickerChange(e) {
	
	var value = e.sender.value();
	
	if (value == null) {
		e.sender.value("");
	}
}

</script>

<!-- / Kendo date / time / datetime picker change -->



<!-- Common functions -->

<script>

function isToday(date) {
	
	if (date == null) { return false; }
	
	var today = new Date();
	
	return date.getDate() == today.getDate() && date.getMonth() == today.getMonth() && 
			date.getFullYear() == today.getFullYear();
}

function isThisYear(date) {
	
	if (date == null) { return false; }
	
	var today = new Date();
	
	return date.getFullYear() == today.getFullYear();
}

</script>

<!-- / Common functions -->
