<%@ tag pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!-- Geo Loc Modal -->

<div class="modal fade modal-level-plus-1" id="geoLocModal" tabindex="-1" role="dialog">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header move-cursor">
                <h5 class="modal-title" rowid="-1">
                	<span id="geoLocModalTitle">지리 위치</span>
                	<span id="geoLocModalTitleTag" class="font-weight-light pl-1"></span>
                </h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">×</span>
                </button>
            </div>
            <div class="modal-body py-1">
				<div class='d-flex align-items-center justify-content-center py-4'>잠시 기다려 주십시오</div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" id="btn-screen-info-ok" data-dismiss="modal">확인</button>
            </div>
        </div>
    </div>
</div>

<!-- / Geo Loc Modal -->


<!--  Scripts -->

<script>

function showGeoLoc(type, id) {
	
	if (type) {
		$("#geoLocModalTitle").attr("rowid", id);
		$("#geoLocModalTitle").attr("code", type);

		if (type == "PL") {
			$("#geoLocModalTitleTag").text("재생 목록");
		} else if (type == "PLINVEN") {
			$("#geoLocModalTitleTag").text("재생 목록의 인벤 타겟팅");
		} else if (type == "CREATINVEN") {
			$("#geoLocModalTitleTag").text("광고 소재의 인벤 타겟팅");
		}
		
		
	    $("#geoLocModal .modal-body").html("<div class='d-flex align-items-center justify-content-center py-4'>잠시 기다려 주십시오</div>");
	    

		$('#geoLocModal .modal-dialog').draggable({ handle: '.modal-header' });
		$("#geoLocModal").modal();
	}
}


$(document).ready(function() {
	$("#geoLocModal").on('show.bs.modal', function (e) {

		setTimeout(function(){
			$('.modal-backdrop:last-child').addClass('modal-level-plus-1');
		});
		
		$("#geoLocModal .modal-body").load("/adn/common/geoLoc?type=" +
				$("#geoLocModalTitle").attr("code") + "&id=" + 
				$("#geoLocModalTitle").attr("rowid"));
	});
});

</script>

<!--  / Scripts -->
