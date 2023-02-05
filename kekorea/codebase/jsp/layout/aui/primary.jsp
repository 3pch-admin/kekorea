<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
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
			<div class="AXUpload5" id="primary_layer"></div>
		</td>
	</tr>
</table>
<script type="text/javascript">
	let primary = new AXUpload5();
	let preView;
	let preViewPath;
	function load() {
		primary.setConfig({
			isSingleUpload : true,
			targetID : "primary_layer",
			uploadFileName : "primary",
			buttonTxt : "파일 선택",
			uploadMaxFileSize : (1024 * 1024 * 1024),
			uploadUrl : "/Windchill/plm/content/aui/upload",
			uploadPars : {
				roleType : "primary"
			},
			deleteUrl : "/Windchill/plm/content/delete",
			fileKeys : {},
			// 			file_types : "image/*",
			onComplete : function() {
				console.log(this);
				preView = this[0].base64;
				preViewPath = this[0].fullPath;
			},
		})
	}
	load();

	$(function() {

		$("#saveBtn").click(function() {
			opener.setPreView(preView, preViewPath);
		})
	})
</script>