<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/rev/dailyapi/readScrTot" var="readScrTotUrl" />
<c:url value="/rev/dailyapi/readSitTot" var="readSitTotUrl" />

<c:url value="/rev/geofence" var="readUrl" />

<c:url value="/rev/monitoring/readResolutions" var="readResolutionUrl" />
<c:url value="/rev/monitoring/readStatuses" var="readStatusUrl" />
<c:url value="/rev/monitoring/recalcScr" var="recalcScrUrl" />


<!-- Opening tags -->

<common:mediumPageOpeningNaverMap />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}
	
</h4>

<hr class="border-light container-m--x mt-0 mb-3">





<!-- Page body -->


<div id="map"
	style="border: solid 1px #e4e4e4; background-color: white; height: 90% !important; width: 100%;">
	<div  id="_panel" style="display: inline-block; border: solid 0px #e4e4e4; position: relative; z-index: 1; margin: 20px 0 0 20px; font-size: 14 px;">
		<form method="post" name="form-1" url="${readUrl} }">
			<div id="guide" style="display: inline-block; border: 1px solid #b4b4b4; border-radius: 4px; padding: 10px; background: #fff;">
				<div style=" border: solid 0px #fff; background: #fff;">
					<span class="fa-regular fa-store fa-fw text-green" id="cvs"></span> <b>편의점</b> 
	<!-- 				<input type="hidden" id="test_check2" name="test_check2" checked value="null"/>  -->
					<input type="checkbox" id="cvs_check" name="cvs_check" checked/> 
			
					<span class="fa-regular fa-hospital fa-fw text-blue"></span><b>병 원</b> 
					<input type="checkbox" id ="hosp_check"  name="hosp_check" checked/>
				</div>
			</div>
		</form>
	</div>
</div>

<!--  Scripts -->

<script>

	$(document).ready(function() {

		showWaitModal();

		setTimeout(function(){
			drawMap();
			hideWaitModal();
		}, 100);

	});
</script>

<script>
	function drawMap() {
		
		var rgnTitles = [];
		var rgnLats = [];
		var rgnLngs = [];
		
		var stateTitles = [];
		var stateLats = [];
		var stateLngs = [];
		
		var cvsTitles = [];
		var cvsLats = [];
		var cvsLngs = [];
		var cvsLatsLngs = [];
		
		var hospTitles = [];
		var hospLats = [];
		var hospLngs = [];
		var hospLatsLngs = [];
		
		var allatslngs = [];

		<c:forEach var="item" items="${stateList}">
		stateTitles.push("${item.title}");
		stateLats.push(${item.lat});
		stateLngs.push(${item.lng});
		</c:forEach>
		
		<c:forEach var="item" items="${regionList}">
		rgnTitles.push("${item.title}");
		rgnLats.push(${item.lat});
		rgnLngs.push(${item.lng});
		</c:forEach>
		
		<c:forEach var="item" items="${CmarkerList}">
		cvsTitles.push("${item.title}");
		cvsLats.push(${item.lat});
		cvsLngs.push(${item.lng});
		cvsLatsLngs.push(new naver.maps.LatLng(${item.lat},${item.lng}));
		allatslngs.push(new naver.maps.LatLng(${item.lat},${item.lng}));
		</c:forEach>
		
		<c:forEach var="item" items="${HmarkerList}">
		hospTitles.push("${item.title}");
		hospLats.push(${item.lat});
		hospLngs.push(${item.lng});
		hospLatsLngs.push(new naver.maps.LatLng(${item.lat},${item.lng}));
		allatslngs.push(new naver.maps.LatLng(${item.lat},${item.lng}));
		</c:forEach>

		function highlightMarker(marker) {
	        var icon = marker.getIcon();

	        marker.setZIndex(1000);
	    }

	    function unhighlightMarker(marker) {
	        var icon = marker.getIcon();

	        marker.setZIndex(100);
	    }
		
		var lat = 37.5512164;
		var lng = 126.98824864606178;
		
		var map = new naver.maps.Map('map', {
			center : new naver.maps.LatLng(lat, lng),
			zoom : 11,
			logoControl : false,
		});
		
	    var cvsHeatmap = new naver.maps.visualization.HeatMap({
	        data: cvsLatsLngs
	    });
	    var hospHeatmap = new naver.maps.visualization.HeatMap({
	        data: hospLatsLngs
	    });
	    var allHeatmap = new naver.maps.visualization.HeatMap({
	        data: allatslngs
	    });
	    
	var infoWin = null;
	var cvsInfoWins = [];
	var hospInfoWins = [];
	var rgnMarkers = [];
	var stateMarkers = [];
	var cvsMarkers = [];
	var cvsdtlMarkers = [];
	var hospMarkers = [];
	var hospdtlMarkers = [];
	
	var bounds = map.getBounds(),
    southWest = bounds.getSW(),
    northEast = bounds.getNE(),
    lngSpan = northEast.lng() - southWest.lng(),
    latSpan = northEast.lat() - southWest.lat();
	
	for (var i = 0; i < rgnTitles.length; i++) {
		var marker = new naver.maps.Marker({
			position: new naver.maps.LatLng(rgnLats[i], rgnLngs[i]),
		    icon: {
		        content: [
		        	'<div class="cs_mapbridge">',
		        	'<div class="rounded-marker rounded-marker-white">'
		        	+'<span class="rounded-marker-title">' + rgnTitles[i] + '</span>'
		        +'</div>' 
		        +'</div>',
		        
			    ].join(''),
		    }
			
		});
		
		rgnMarkers.push(marker);

	}
	
	for (var i = 0; i < stateTitles.length; i++) {
		var marker = new naver.maps.Marker({
			position: new naver.maps.LatLng(stateLats[i], stateLngs[i]),
			map: map,
		    icon: {
		        content: [
		        '<div class="rounded-marker rounded-marker-white">',
		        	'<div class="circle">',
			        '</div>',
			        	'<span class="rounded-marker-title">' + stateTitles[i] + '</span>',
		        '</div>',
			    ].join(''),
		    }
		});
		
		stateMarkers.push(marker);

	}
	
	for (var i = 0; i < cvsTitles.length; i++) {
		var marker = new naver.maps.Marker({
			position: new naver.maps.LatLng(cvsLats[i], cvsLngs[i]),
		    icon: {
		    	url : '/resources/shared/icons/tmp/circle-purple-cu.png',
		    },
		});
		cvsMarkers.push(marker);
		
		var	content=[
			'<div>',
			'<div class="bubble-textInfo">',
				'<img src="/resources/shared/icons/tmp/cu03.png" width="20" height="20" style="margin-left: 2px"/>',
				'<span class="bubble-text-span">' + cvsTitles[i] + '</span>',
			'</div>',
			'<div class="triangle-wrapInfo">',
				'<div class="triangle-blue"></div>',
				'<div class="triangle-white"></div>',
			'</div>',
		'</div>'
	].join('');
		
		cvsInfoWins.push(new naver.maps.InfoWindow({
		    content: content,
		    borderWidth: 0,
		    disableAnchor: true,
		    backgroundColor: 'transparent',
		    pixelOffset: new naver.maps.Point(0, 7),
		}));
	}
	
	for (var i = 0; i < cvsTitles.length; i++) {
		var marker = new naver.maps.Marker({
			position: new naver.maps.LatLng(cvsLats[i], cvsLngs[i]),
			icon:{
		    	content:[
					'<div>',
						'<div class="bubble-text">',
							'<img src="/resources/shared/icons/tmp/cu03.png" width="20" height="20" style="margin-left: 2px"/>',
							'<span class="bubble-text-span">' + cvsTitles[i] + '</span>',
						'</div>',
						'<div class="triangle-wrap">',
							'<div class="triangle-blue"></div>',
							'<div class="triangle-white"></div>',
						'</div>',
					'</div>'
				].join(''),
				anchor: new naver.maps.Point(19, 44)
			},
		});
        
        marker.addListener('mouseover', function(e) {
            highlightMarker(e.overlay);
        });
        marker.addListener('mouseout', function(e) {
            unhighlightMarker(e.overlay);
        });
		
		cvsdtlMarkers.push(marker);
		
	}
	
	for (var i = 0; i < hospTitles.length; i++) {
		var marker = new naver.maps.Marker({
			position: new naver.maps.LatLng(hospLats[i], hospLngs[i]),
			icon:{
		    	content:[
					'<div>',
						'<div class="bubble-text">',
							'<img src="/resources/shared/icons/tmp/hospital04.png" width="20" height="20" style="margin-left: 2px"/>',
							'<span class="bubble-text-span">' + hospTitles[i] + '</span>',
						'</div>',
						'<div class="triangle-wrap">',
							'<div class="triangle-blue"></div>',
							'<div class="triangle-white"></div>',
						'</div>',
					'</div>'
				].join(''),
				anchor: new naver.maps.Point(19, 44)
			}
		});
        
        marker.addListener('mouseover', function(e) {
            highlightMarker(e.overlay);
        });
        marker.addListener('mouseout', function(e) {
            unhighlightMarker(e.overlay);
        });
		
		hospdtlMarkers.push(marker);
	}

	for (var i = 0; i < hospTitles.length; i++) {
		var marker = new naver.maps.Marker({
			position: new naver.maps.LatLng(hospLats[i], hospLngs[i]),
		    icon: {
		    	url : '/resources/shared/icons/tmp/circle-blue-hosp.png',
		    },
		});
		
		hospMarkers.push(marker);

		var content=[
			'<div>',
			'<div class="bubble-textInfo">',
				'<img src="/resources/shared/icons/tmp/hospital04.png" width="20" height="20" style="margin-left: 2px"/>',
				'<span class="bubble-text-span">' + hospTitles[i] + '</span>',
			'</div>',
			'<div class="triangle-wrapInfo">',
				'<div class="triangle-blue"></div>',
				'<div class="triangle-white"></div>',
			'</div>',
		'</div>'
	].join('');
	
	hospInfoWins.push(new naver.maps.InfoWindow({
	    content: content,
	    borderWidth: 0,
	    disableAnchor: true,
	    backgroundColor: 'transparent',
	    pixelOffset: new naver.maps.Point(0, 7),
	}));
		
	}

	naver.maps.Event.addListener(map, 'idle', function() {
		var zoom = map.getZoom();
		if(zoom>=16 && $("#cvs_check").is(":checked")) {
			updateMarkers(map, cvsdtlMarkers);
			hideMarkers(map, cvsMarkers);
		}else if(zoom >= 14 && $("#cvs_check").is(":checked")){
		    hideMarkers(map, cvsdtlMarkers);
		    hideMarkers(map, rgnMarkers);
		    updateMarkers(map, cvsMarkers);
		    endHeatMap(cvsHeatmap)
		}else if(zoom >= 14){
		    hideMarkers(map, rgnMarkers);
		}else if(zoom >= 11){
		    updateMarkers(map, rgnMarkers);
		    hideMarkers(map, stateMarkers);
			hideMarkers(map, cvsMarkers);
			hideMarkers(map, hospMarkers);
		}else{
		    updateMarkers(map, stateMarkers);
		    hideMarkers(map, rgnMarkers);
		}
		if(13>= zoom && $("#cvs_check").is(":checked") && $("#hosp_check").is(":checked")) {
			startHeatMap(allHeatmap)
		}else if(13>= zoom && $("#cvs_check").is(":checked")) {
			startHeatMap(cvsHeatmap)
		}else if(13>= zoom && $("#hosp_check").is(":checked")) {
			startHeatMap(hospHeatmap)
		}else{
			endHeatMap(allHeatmap)
		}
		
		if(zoom>=16 && $("#hosp_check").is(":checked")) {
			updateMarkers(map, hospdtlMarkers);
			hideMarkers(map, hospMarkers);
		}else if(zoom >= 14 && $("#hosp_check").is(":checked")){
		    hideMarkers(map, hospdtlMarkers);
		    hideMarkers(map, rgnMarkers);
		    updateMarkers(map, hospMarkers);
		    endHeatMap(hospHeatmap)
		}
	});
	
	function updateMarkers(map, markers) {

	    var mapBounds = map.getBounds();
	    var marker, position;

		    for (var i = 0; i < markers.length; i++) {
	
		        marker = markers[i]
		        position = marker.getPosition();

		        if (mapBounds.hasLatLng(position)) {
		            showMarker(map, marker);
		        } else{
		            hideMarker(map, marker);
		        }
		    }
	}
	
	function hideMarkers(map, markers) {

	    var mapBounds = map.getBounds();
	    var marker, position;

		    for (var i = 0; i < markers.length; i++) {
	
		        marker = markers[i]

		        marker.setMap(null);

		    }
	}
	
	function showMarker(map, marker) {
	    
	    marker.setMap(map);	    	

	}

	function hideMarker(map, marker) {

	    if (!marker.getMap()) return;
	    marker.setMap(null);
	}
	
	  $("#cvs_check").change(function(){
		  var zoom = map.getZoom();
		  
		  if(zoom >= 16 && $("#cvs_check").is(":checked")) {
				updateMarkers(map, cvsdtlMarkers);
		  } else if(zoom >= 16 && $("#hosp_check").is(":checked")){
			    hideMarkers(map, cvsdtlMarkers);
		  } else if(zoom >= 14 && $("#cvs_check").is(":checked")){
			    updateMarkers(map, cvsMarkers);
		  } else if(zoom >= 14 && $("#hosp_check").is(":checked")){
			  hideMarkers(map, cvsMarkers);
		  }else if($("#cvs_check").is(":checked")&& $("#hosp_check").is(":checked")){
			  startHeatMap(allHeatmap)
		  } else if($("#cvs_check").is(":checked")){
			  startHeatMap(cvsHeatmap)
		  } else if($("#hosp_check").is(":checked")){
			  startHeatMap(hospHeatmap)
			  allHeatmap.setMap(null);
			  cvsHeatmap.setMap(null);
		  }else{
	        	allHeatmap.setMap(null);
	        	cvsHeatmap.setMap(null);
	        	hospHeatmap.setMap(null);
	        	hideMarkers(map, cvsMarkers);
	        	hideMarkers(map, cvsdtlMarkers);
	        }
	    });
	  
	    
	    
	  $("#hosp_check").change(function(){
		  var zoom = map.getZoom();
		  
		  if(zoom >= 16 && $("#hosp_check").is(":checked")) {
				updateMarkers(map, hospdtlMarkers);
				hideMarkers(map, hospMarkers);
		  }else if(zoom >= 16 && $("#cvs_check").is(":checked")){
			    hideMarkers(map, hospdtlMarkers);
		  } else if(zoom >= 14 && $("#hosp_check").is(":checked")){
			    updateMarkers(map, hospMarkers);
		  } else if(zoom >= 14 && $("#cvs_check").is(":checked")){
			  hideMarkers(map, hospMarkers);
		  } else if($("#cvs_check").is(":checked") && $("#hosp_check").is(":checked")){
			  startHeatMap(allHeatmap)
		  } else if($("#hosp_check").is(":checked")){
			  startHeatMap(hospHeatmap)
		  } else if($("#cvs_check").is(":checked")){
			  startHeatMap(cvsHeatmap)
			  allHeatmap.setMap(null);
			  hospHeatmap.setMap(null);
		  }else{
	        	allHeatmap.setMap(null);
	        	hospHeatmap.setMap(null);
	        	cvsHeatmap.setMap(null);
	        	hideMarkers(map, hospMarkers);
	        	hideMarkers(map, hospdtlMarkers);
	        }
	    });
	  
	    function getClickHandler(seq, markers,infoWins) {
			
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

		for(var i = 0; i < cvsMarkers.length; i++){
			naver.maps.Event.addListener(cvsMarkers[i], "click", getClickHandler(i,cvsMarkers,cvsInfoWins));
		}
		for(var i = 0; i < hospMarkers.length; i++){
			naver.maps.Event.addListener(hospMarkers[i], "click", getClickHandler(i,hospMarkers,hospInfoWins));
		}
		
		naver.maps.Event.addListener(map, "click", function(e) {
			if (infoWin) {
	    	    infoWin.close();
	    	    infoWin = null;
			}
		});
		
		naver.maps.Event.addListener(map, "zoom_changed", function(zoom) {
			
			if (infoWin && 13>= zoom) {
		    	    infoWin.close();
		    	    infoWin = null;	
			}else if (infoWin && zoom >=16) {
		    	    infoWin.close();
		    	    infoWin = null;	
			}

		});
		
		function startHeatMap(heatmap) {
		    heatmap.setMap(map);
		    };
		 function endHeatMap(heatmap) {
		    heatmap.setMap(null);
		    };
	
			setTimeout(function(){
				map.setZoom(10);
			}, 500);
			

}

	
</script>


<!--  / Scripts -->

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

.bubble-textInfo {
position: relative;
bottom: 4px;
	cursor: default;
    color: black;
    max-width: 300px;
    width: fit-content;
    border: 2px solid #A3A4A6;
    border-radius: 20px;
    background-color: white;
    padding: 2px;
}
.bubble-text {
	cursor: default;
    color: black;
    max-width: 300px;
    width: fit-content;
    display: flex;
    flex-direction: row;
    border: 2px solid #A3A4A6;
    border-radius: 20px;
    background-color: white;
    padding: 2px;
    align-items: center;
}
.bubble-text-span {
	cursor: default;
	margin: 0 10px 0 4px; 
	font-size: 0.9rem; 
	font-weight: 500;
	white-space: nowrap; 
	overflow: hidden; 
	text-overflow: ellipsis;
}
.triangle-wrap {
	cursor: default;
	position: relative;
	right: -10px;
	bottom: 1px;
}
.triangle-wrapInfo {
	cursor: default;
	position: relative;
	right: -46%;
	bottom: 5px;
}

.triangle-blue {
cursor: default;
	width: 0;
	height: 0;
	border-color: #A3A4A6 transparent transparent;
	border-style: solid;
	border-width: 8px 6px 0;
	pointer-events: none;
}
.triangle-white {
	cursor: default;
	border-color: white transparent transparent;
	border-style: solid;
	border-width: 8px 6px 0;
	pointer-events: none;
	position: absolute;
	top: -3px;
}

</style>

<!-- / Page body -->





<!-- Functional tags -->



<!-- Closing tags -->

<common:base />
<common:pageClosing />
