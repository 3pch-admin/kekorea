<%@page import="com.lowagie.text.Meta"%>
<%@page import="e3ps.doc.meeting.dto.MeetingTemplateDTO"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
MeetingTemplateDTO dto = (MeetingTemplateDTO) request.getAttribute("dto");
%>
<!-- tinymce -->
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
				// 에디터가 초기화되면 실행되는 콜백 함수
				editor.on('init', function() {
					// 에디터의 내용 가져오기
					const content = editor.getContent();
					editor.setContent(content);
				});
			}
		});
	}

	document.addEventListener("DOMContentLoaded", function() {
		// DOM이 로드된 후 실행할 코드 작성
		loadTinymce();
	});

	document.addEventListener("keydown", function(event) {
		// 키보드 이벤트 객체에서 눌린 키의 코드 가져오기
		const keyCode = event.keyCode || event.which;
		// esc 키(코드 27)를 눌렀을 때
		if (keyCode === 27) {
			// 현재 창 닫기
			self.close();
		}
	})
</script>