<%@page import="e3ps.doc.meeting.dto.MeetingDTO"%>
<%@page import="com.lowagie.text.Meta"%>
<%@page import="e3ps.doc.meeting.dto.MeetingTemplateDTO"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
MeetingDTO dto = (MeetingDTO) request.getAttribute("dto");
%>
<!-- tinymce -->
<%@include file="/extcore/include/tinymce.jsp"%>
<!-- AUIGrid -->
<%@include file="/extcore/include/auigrid.jsp"%>
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
		<col width="800">
		<col width="130">
		<col width="800">
	</colgroup>
	<tr>
		<th>회의록 제목</th>
		<td class="indent5"><%=dto.getName()%></td>
		<th>회의록 템플릿</th>
		<td class="indent5"><%=dto.getName()%></td>
	</tr>
	<tr>
		<th>KEK 작번</th>
		<td class="indent5" colspan="3">
			<jsp:include page="/extcore/include/project-include.jsp">
				<jsp:param value="<%=dto.getOid()%>" name="oid" />
				<jsp:param value="view" name="mode" />
				<jsp:param value="meeting" name="obj"/>
			</jsp:include>
		</td>
	</tr>
	<tr>
		<th>내용</th>
		<td class="indent5" colspan="3">
			<textarea name="description" id="description" rows="8" readonly="readonly"><%=dto.getContent()%></textarea>
		</td>
	</tr>
	<tr>
		<th>첨부파일</th>
		<td class="indent5" colspan="3">
			<jsp:include page="/extcore/include/secondary-include.jsp">
				<jsp:param value="<%=dto.getOid()%>" name="oid" />
				<jsp:param value="view" name="mode" />
			</jsp:include>
		</td>
	</tr>
</table>
<script type="text/javascript">
	function loadTinymce() {
		tinymce.init({
			selector : 'textarea',
			height : 500,
			menubar : false,
			statusbar : false,
			language : 'ko_KR',
			toolbar : false,
			readonly : true,
			setup : function(editor) {
				// 에디터가 초기화되면 실행되는 콜백 함수
				editor.on('init', function() {
					// 에디터의 내용 가져오기
					let content = editor.getContent();
					editor.setContent(content);
				});
			}
		});
	}

	document.addEventListener("DOMContentLoaded", function() {
		// DOM이 로드된 후 실행할 코드 작성
		loadTinymce();
		_createAUIGrid(_columns);
	});

	document.addEventListener("keydown", function(event) {
		// 키보드 이벤트 객체에서 눌린 키의 코드 가져오기
		let keyCode = event.keyCode || event.which;
		// esc 키(코드 27)를 눌렀을 때
		if (keyCode === 27) {
			// 현재 창 닫기
			self.close();
		}
	})
</script>