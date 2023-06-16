<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- 메뉴얼 동영상 모달 -->
<div id="modalLayer" class="video-modal">
	<div class="modal-content">
		<span class="close" onclick="_stop();">&times;</span>
		<video id="videoPlayer" controls>
			<source type="video/mp4">
		</video>
	</div>
</div>
<script type="text/javascript">
	const modalLayer = document.getElementById("modalLayer");
	var videoPlayer = document.getElementById("videoPlayer");

	function play(src) {
		modalLayer.style.display = "block";

		if (videoPlayer.requestFullscreen) {
			videoPlayer.requestFullscreen();
		} else if (videoPlayer.mozRequestFullScreen) { // Firefox
			videoPlayer.mozRequestFullScreen();
		} else if (videoPlayer.webkitRequestFullscreen) { // Chrome, Safari, Opera
			videoPlayer.webkitRequestFullscreen();
		} else if (videoPlayer.msRequestFullscreen) { // IE/Edge
			videoPlayer.msRequestFullscreen();
		}

		videoPlayer.style.width = 1500 + "px";
		videoPlayer.style.height = 700 + "px";
		videoPlayer.src = "/Windchill/extcore/video/" + src;
		videoPlayer.play();
	}

	function _stop() {
		modalLayer.style.display = "none";
		videoPlayer.pause();
		videoPlayer.currentTime = 0;
		AUIGrid.resize(myGridID);
	}
</script>