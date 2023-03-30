<%@page import="com.lowagie.text.Meta"%>
<%@page import="e3ps.doc.meeting.dto.MeetingTemplateDTO"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
MeetingTemplateDTO dto = (MeetingTemplateDTO) request.getAttribute("dto");
%>
<%@include file="/extcore/include/tinymce.jsp"%>
<table class="button-table">
	<tr>
		<td class="right">
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
		<th>회의록 템플릿 제목</th>
		<td class="indent5"><%=dto.getName()%></td>
	</tr>
	<tr>
		<th>회의록 양식</th>
		<td class="indent5">
			<textarea name="description" id="description" rows="8" readonly="readonly"><%=dto.getContent()%></textarea>
		</td>
	</tr>
</table>
<script type="text/javascript">
	function loadTinymce() {
		tinymce.init({
			selector : 'textarea',
			height : 800,
			menubar : false,
			statusbar : false,
			language : 'ko_KR',
			toolbar : false,
			readonly : true,
			setup : function(editor) {
				editor.on('init', function() {
					const content = editor.getContent();
					editor.setContent(content);
				});
			}
		});
	}

	document.addEventListener("DOMContentLoaded", function() {
		loadTinymce();
	});

	document.addEventListener("keydown", function(event) {
		const keyCode = event.keyCode || event.which;
		if (keyCode === 27) {
			self.close();
		}
	})
</script>