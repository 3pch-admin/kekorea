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
	<tr>
		<th>첨부파일</th>
		<td class="indent5">
			<div class="AXUpload5" id="primary_layer"></div>
		</td>
	</tr>
</table>
<script type="text/javascript">
const primary = new AXUpload5();
	let data;
	function primaryUploader() {
		primary.setConfig({
			isSingleUpload : true,
			targetID : "primary_layer",
			uploadFileName : "primary",
			buttonTxt : "파일 선택",
			uploadMaxFileSize : (1024 * 1024 * 1024),
			uploadUrl : getCallUrl("/aui/upload"),
			uploadPars : {
				roleType : "primary"
			},
			deleteUrl : getCallUrl("/content/delete"),
			fileKeys : {},
			onComplete : function() {
				data = this[0];
			},
		})
	}

	primaryUploader();

	function save() {
		opener.<%=method%>(data);
		self.close();
	}
</script>