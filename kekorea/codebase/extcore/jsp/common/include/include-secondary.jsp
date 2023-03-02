<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="AXUpload5" id="secondary_layer" style="margin-top: 3px;"></div>
<div class="AXUpload5QueueBox_list" id="uploadQueueBox" style="margin: 3px 5px 3px 5px;"></div>
<script type="text/javascript">
	let secondary = new AXUpload5();
	function secondaryUploader() {
		secondary.setConfig({
			isSingleUpload : false,
			targetID : "secondary_layer",
			uploadFileName : "secondary",
			buttonTxt : "파일 선택",
			uploadMaxFileSize : (1024 * 1024 * 1024),
			uploadUrl : getCallUrl("/content/upload"),
			dropBoxID : "uploadQueueBox",
			queueBoxID : "uploadQueueBox",
			uploadPars : {
				roleType : "secondary"
			},
			uploadMaxFileCount : 100,
			deleteUrl : getCallUrl("/content/delete"),
			fileKeys : {},
			onStart : function() {
			},
			onComplete : function() {
				console.log(this);
			},
		})
	}
	secondaryUploader();
</script>

