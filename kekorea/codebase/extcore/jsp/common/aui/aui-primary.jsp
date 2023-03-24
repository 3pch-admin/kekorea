<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
String method = (String) request.getAttribute("method");
%>
<table class="button-table">
	<tr>
		<td class="right">
			<input type="button" value="저장" title="저장" onclick="save();">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>
<table class="create-table">
	<colgroup>
		<col width="150">
		<col width="*">
	</colgroup>
	<tr>
		<th class="lb">첨부파일</th>
		<td class="indent5">
			<div class="AXUpload5" id="primary_layer"></div>
			<div class="AXUpload5QueueBox_list" id="uploadQueueBox" style="height: 200px;"></div>
		</td>
	</tr>
</table>
<script type="text/javascript">
	const primary = new AXUpload5();
	let data;
	function primaryUploader() {
		primary.setConfig({
			isSingleUpload : false,
			targetID : "primary_layer",
			uploadFileName : "primary",
			buttonTxt : "파일 선택",
			uploadMaxFileSize : (1024 * 1024 * 1024),
			uploadUrl : getCallUrl("/aui/upload"),
			dropBoxID : "uploadQueueBox",
			queueBoxID : "uploadQueueBox",
			uploadPars : {
				roleType : "primary"
			},
			uploadMaxFileCount : 1,
			deleteUrl : getCallUrl("/content/delete"),
			fileKeys : {},
			onComplete : function() {
				data = this[0];
			},
			onError : function() {
				alert("하나의 첨부파일만 업로드가 가능합니다.");
				document.location.reload();
			}
		})
	}

	primaryUploader();

	function save() {
		opener.
<%=method%>
	(data);
		self.close();
	}
</script>