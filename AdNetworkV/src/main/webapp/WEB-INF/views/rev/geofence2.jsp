<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/rev/dailyapi/readScrTot" var="readScrTotUrl" />
<c:url value="/rev/dailyapi/readSitTot" var="readSitTotUrl" />

<c:url value="/rev/monitoring/readScr" var="readUrl" />

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


<div id="map" style="border: solid 1px #e4e4e4; background-color: white; height: 800px; width: 100%;">
</div>


<!--  Scripts -->

<script>
$(document).ready(function() {

	drawMap();
	
});	
</script>


<script>

function drawMap(data) {
	
	var map = new naver.maps.Map('map', {
		center: new naver.maps.LatLng(37.5512164, 126.98824864606178),
		zoom: 5
	}); 

	var titles = [];
	var lats = [];
	var lngs = [];
	
	var titles2 = [];
	var lats2 = [];
	var lngs2 = [];
	
<c:forEach var="item" items="${cuMarkerList}">
	titles.push("${item.title}");
	lats.push(${item.lat});
	lngs.push(${item.lng});
</c:forEach>

<c:forEach var="item" items="${helloMarkerList}">
	titles2.push("${item.title}");
	lats2.push(${item.lat});
	lngs2.push(${item.lng});
</c:forEach>

	var markers = [];
	var infoWins = [];
	
	for (var i = 0; i < titles.length; i++) {
		var marker = new naver.maps.Marker({
			position: new naver.maps.LatLng(lats[i], lngs[i]),
			icon: "${cuMarkerUrl}",
			map: map
		});
		
		markers.push(marker);
		
		var content = [
			'<div class="rounded-marker rounded-marker-white">',
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
	
	for (var i = 0; i < titles2.length; i++) {
		var marker = new naver.maps.Marker({
			position: new naver.maps.LatLng(lats2[i], lngs2[i]),
			icon: "${helloMarkerUrl}",
			map: map
		});
		
		markers.push(marker);
		
		var content = [
			'<div class="rounded-marker rounded-marker-white">',
	        	'<span class="rounded-marker-title">' + titles2[i] + '</span>',
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
	
    var htmlMarker1 = {
		content: '<div style="cursor:pointer;width:32px;height:32px;line-height:32px;font-size:10px;color:#2791FA;text-align:center;font-weight:500;background:url(/resources/shared/images/cluster-marker.png);background-size:contain;"></div>',
		size: N.Size(32, 32),
		anchor: N.Point(16, 16)
    }, htmlMarker2 = {
		content: '<div style="cursor:pointer;width:44px;height:44px;line-height:44px;font-size:12px;color:#2791FA;text-align:center;font-weight:500;background:url(/resources/shared/images/cluster-marker.png);background-size:contain;"></div>',
		size: N.Size(44, 44),
		anchor: N.Point(22, 22)
	}, htmlMarker3 = {
		content: '<div style="cursor:pointer;width:56px;height:56px;line-height:56px;font-size:14px;color:#2791FA;text-align:center;font-weight:500;background:url(/resources/shared/images/cluster-marker.png);background-size:contain;"></div>',
		size: N.Size(56, 56),
		anchor: N.Point(28, 28)
	}, htmlMarker4 = {
		content: '<div style="cursor:pointer;width:64px;height:64px;line-height:64px;font-size:16px;color:#2791FA;text-align:center;font-weight:500;background:url(/resources/shared/images/cluster-marker.png);background-size:contain;"></div>',
		size: N.Size(64, 64),
		anchor: N.Point(32, 32)
	}, htmlMarker5 = {
		content: '<div style="cursor:pointer;width:80px;height:80px;line-height:80px;font-size:18px;color:#2791FA;text-align:center;font-weight:500;background:url(/resources/shared/images/cluster-marker.png);background-size:contain;"></div>',
		size: N.Size(80, 80),
		anchor: N.Point(40, 40)
	};	
	
	var markerClustering = new MarkerClustering({
		minClusterSize: 2, // 최소 클러스트링 단위
		maxZoom: 13,
		map: map,
		markers: markers, // 마커설정
		disableClickZoom: false,
		gridSize: 120,
		icons: [htmlMarker1, htmlMarker2, htmlMarker3, htmlMarker4, htmlMarker5],
		indexGenerator: [10, 100, 200, 500, 1000],
		stylingFunction: function(clusterMarker, count) {    
			$(clusterMarker.getElement()).find('div:first-child').text(count);
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





<!-- Functional tags -->



<!-- Closing tags -->

<common:base />
<common:pageClosing />
