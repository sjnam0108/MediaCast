<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>





<!-- Page body -->


<!--  Forms -->

<div class="card mt-2">
	<div class="row no-gutters row-bordered row-border-light">
		<div class="col-2-5">
			<div class="d-flex align-items-center justify-content-center p-2">
			
<c:choose>
<c:when test="${type == 'CT'}">

				<span class="fa-thin fa-city fa-2x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">유형</div>
					<div>광역시/도</div>
				</div>
				
</c:when>
<c:when test="${type == 'RG'}">

				<span class="fa-thin fa-mountain-city fa-2x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">유형</div>
					<div>시/군/구</div>
				</div>
				
</c:when>
<c:when test="${type == 'SC'}">

				<span class="fa-thin fa-screen-users fa-2x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">유형</div>
					<div>매체 화면</div>
				</div>
				
</c:when>
<c:when test="${type == 'ST'}">

				<span class="fa-thin fa-map-pin fa-2x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">유형</div>
					<div>사이트</div>
				</div>
				
</c:when>
<c:when test="${type == 'CD'}">

				<span class="fa-thin fa-location-crosshairs fa-2x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">유형</div>
					<div>입지 유형</div>
				</div>
				
</c:when>
<c:when test="${type == 'PL'}">

				<span class="fa-thin fa-list-ol fa-2x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">유형</div>
					<div>재생 목록</div>
				</div>
				
</c:when>
<c:when test="${type == 'AD'}">

				<span class="fa-thin fa-audio-description fa-2x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">유형</div>
					<div>광고</div>
				</div>
				
</c:when>
<c:when test="${type == 'CR'}">

				<span class="fa-thin fa-photo-film fa-2x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">유형</div>
					<div>광고 소재</div>
				</div>
				
</c:when>
</c:choose>
			
			</div>
		</div>
		<div class="col-5-5 d-flex align-items-center justify-content-center">
			<div class="p-2">
				${display}
			</div>
		</div>
		<div class="col-2">
			<div class="d-flex align-items-center justify-content-center p-2">
				<span class="fa-thin fa-screen-users fa-2x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">화면수</div>
					<div>${scrCnt}</div>
				</div>
			</div>
		</div>
		<div class="col-2">
			<div class="d-flex align-items-center justify-content-center p-2">
				<span class="fa-thin fa-map-pin fa-2x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">사이트수</div>
					<div>${siteCnt}</div>
				</div>
			</div>
		</div>
	</div>
</div>

<div class="mt-1 mb-2">
	<div id="geo-loc-map" style="border: solid 1px #e4e4e4; background-color: white; height: 600px; width: 100%;">
	</div>
</div>

<style>

.col-2-5 {
  position: relative;
  width: 100%;
  padding-right: 15px;
  padding-left: 15px;
  flex: 0 0 20.833333325%;
  max-width: 20.833333325%;
}

.col-5-5 {
  position: relative;
  width: 100%;
  padding-right: 15px;
  padding-left: 15px;
  flex: 0 0 45.833333315%;
  max-width: 45.833333315%;
}

</style>

<!--  / Forms -->


<!--  Scripts -->

<script>


var geoInfoWin = null;

$(document).ready(function() {

	drawMap();
	
});


function drawMap() {
	
	var map = new naver.maps.Map('geo-loc-map', {
		center: new naver.maps.LatLng(37.5512164, 126.98824864606178),
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
	
	var bounds = [];
	
	for (var i = 0; i < titles.length; i++) {
		var marker = new naver.maps.Marker({
			position: new naver.maps.LatLng(lats[i], lngs[i]),
			icon: "${markerUrl}",
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
		
		bounds.push(marker.position);
	}
	
	map.fitBounds(bounds);
	
	// 자료 수가 하나일때의 처리
	if (titles.length == 1) {
		map.setCenter(new naver.maps.LatLng(lats[0], lngs[0]));
	}
	
	
    function getClickHandler(seq) {
		
		return function(e) {  // 마커를 클릭하는 부분
			var marker = markers[seq], // 클릭한 마커의 시퀀스로 찾는다.
				infoWindow = infoWins[seq]; // 클릭한 마커의 시퀀스로 찾는다

			if (infoWindow.getMap()) {
				infoWindow.close();
				geoInfoWin = null;
			} else {
				geoInfoWin = infoWindow;
				infoWindow.open(map, marker);
			}
    	}
    };

	for(var i = 0; i < markers.length; i++){
		naver.maps.Event.addListener(markers[i], "click", getClickHandler(i));
	}
	
	naver.maps.Event.addListener(map, "click", function(e) {
		if (geoInfoWin) {
			geoInfoWin.close();
			geoInfoWin = null;
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
	
	setTimeout(function(){
		// Naver Map 버그 해결
		window.dispatchEvent(new Event('resize'));
	}, 500);
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
