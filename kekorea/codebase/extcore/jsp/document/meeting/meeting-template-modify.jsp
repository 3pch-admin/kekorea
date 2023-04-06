<%@page import="com.lowagie.text.Meta"%>
<%@page import="e3ps.doc.meeting.dto.MeetingTemplateDTO"%>
<%@page import="java.util.Map"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
MeetingTemplateDTO dto = (MeetingTemplateDTO) request.getAttribute("dto");
%>
<!-- tinymce -->
<%@include file="/extcore/include/tinymce.jsp"%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="right">
			<input type="button" value="수정" title="수정" onclick="modify();">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>

<table class="create-table">
	<colgroup>
		<col width="130">
		<col width="*">
	</colgroup>
	<tr>
		<th class="req lb">회의록 템플릿 제목</th>
		<td class="indent5">
			<input type="text" name="name" id="name" class="AXInput width-500" value="<%=dto.getName()%>">
		</td>
	</tr>
	<tr>
		<th class="req lb">회의록 양식</th>
		<td class="indent5">
			<textarea name="description" id="description" rows="8"><%=dto.getContent()%></textarea>
		</td>
	</tr>
</table>
<script type="text/javascript">

	function modify() {

		const params = new Object();
		const url = getCallUrl("/meeting/modify");
		const oid = document.getElementById("oid").value;
		const content = tinymce.activeEditor.getContent();
		params.oid = oid;
		params.name = document.getElementById("name").value;
		params.content = content;
		if(isNull(params.name)){
			alert("회의록 템플릿 제목은 공백을 입력할 수 없습니다.");
			document.getElementById("name").focus();
			return false;
		}
		if(isNull(params.content)){
			alert("회의록 양식은 공백을 입력할 수 없습니다.");
			tinymce.activeEditor.focus();
			return false;
		}
		if (!confirm("수정 하시겠습니까?")) {
			return false;
		}
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			}
		})
	}

	
	function loadTinymce() {
		tinymce.init({
			selector : 'textarea',
			height : 800,
			statusbar : false,
			language : 'ko_KR',
			plugins : 'anchor autolink charmap codesample emoticons image link lists media searchreplace table visualblocks wordcount',
			toolbar : 'undo redo | blocks fontfamily fontsize | bold italic underline strikethrough | link image media table | addcomment showcomments | spellcheckdialog a11ycheck typography | align lineheight | checklist numlist bullist indent outdent | emoticons charmap | removeformat',
		});
	}

	document.addEventListener("DOMContentLoaded", function() {
		loadTinymce();
	});
</script>