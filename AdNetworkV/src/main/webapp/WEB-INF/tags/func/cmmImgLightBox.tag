<%@ tag pageEncoding="UTF-8"%>


<!-- Page scripts  -->

<link rel="stylesheet" href="/resources/vendor/lib/blueimp-gallery/gallery.css">
<link rel="stylesheet" href="/resources/vendor/lib/blueimp-gallery/gallery-indicator.css">

<script src="/resources/vendor/lib/blueimp-gallery/gallery.js"></script>
<script src="/resources/vendor/lib/blueimp-gallery/gallery-fullscreen.js"></script>
<script src="/resources/vendor/lib/blueimp-gallery/gallery-indicator.js"></script>


<!--  Gallery lightbox template -->

<div id="imgLlightbox" class="blueimp-gallery blueimp-gallery-controls" style="z-index: 2200 !important;">
	<div class="slides"></div>
	<h3 class="title"></h3>
	<a class="prev">‹</a>
	<a class="next">›</a>
	<a class="close">×</a>
	<a class="play-pause"></a>
	<ol class="indicator"></ol>
</div>

<!--  / Gallery lightbox template -->


<!--  Scripts -->

<script>

function viewUnivImage(url) {
	
	blueimpGallery(
		[{
			data: {urls: [url]}
		}], {
    	container: '#imgLlightbox',
        urlProperty: 'data.urls[0]'
	});
}


function viewUnivImages(links, urlProperty, index) {
	
	blueimpGallery(links, {
		container: '#imgLlightbox',
		carousel: false,
		index: index,
		urlProperty: urlProperty,
	});
}

</script>

<!--  / Scripts -->
