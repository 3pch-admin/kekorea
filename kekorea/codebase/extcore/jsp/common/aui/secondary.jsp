<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
String method = (String)request.getAttribute("method");
%>
<!-- button table -->
<table class="btn_table">
	<tr>
		<td class="right">
			<input type="button" value="저장" class="blueBtn" id="saveBtn" title="저장">
			<input type="button" value="닫기" class="orangeBtn" id="closeBtn" title="닫기">
		</td>
	</tr>
</table>
<table class="create_table">
	<colgroup>
		<col width="130">
		<col width="*">
	</colgroup>
	<tr>
		<th>첨부파일</th>
		<td>
			<div class="AXUpload5" id="secondary_layer"></div>
			<div class="AXUpload5QueueBox_list" id="uploadQueueBox" style="height: 290px;"></div>
		</td>
	</tr>
</table>
<script type="text/javascript">
	let secondary = new AXUpload5();
	let data;
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
			onComplete : function() {
				data = this;
			},
		})
	}
	load();

	$(function() {

		$("#saveBtn").click(function() {
			opener.<%=method%>(data);
			self.close();
		})
	})
</script>