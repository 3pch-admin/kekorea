<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="AXUpload5" id="secondary_layer"></div>
<div class="AXUpload5QueueBox_list" id="uploadQueueBox"></div>
<script type="text/javascript">
	let secondary = new AXUpload5();
	function load() {
		secondary.setConfig({
			isSingleUpload : false,
			targetID : "secondary_layer",
			uploadFileName : "secondary",
			buttonTxt : "파일 선택",
			uploadMaxFileSize : (1024 * 1024 * 1024),
			uploadUrl : "/Windchill/plm/content/aui/auiUpload",
			dropBoxID : "uploadQueueBox",
			queueBoxID : "uploadQueueBox",
			uploadPars : {
				roleType : "secondary"
			},
			uploadMaxFileCount : 100,
			deleteUrl : "/Windchill/plm/content/delete",
			fileKeys : {},
			onStart : function() {
				openLayer();
			},
			onComplete : function() {
				console.log(this);
				closeLayer();
			},
		})
	}
	load();
</script>

