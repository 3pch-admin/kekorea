<%@page import="e3ps.doc.meeting.dto.MeetingDTO"%>
<%@page import="com.lowagie.text.Meta"%>
<%@page import="e3ps.doc.meeting.dto.MeetingTemplateDTO"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
MeetingDTO dto = (MeetingDTO) request.getAttribute("dto");
%>
<%@include file="/extcore/include/tinymce.jsp"%>
<%@include file="/extcore/include/auigrid.jsp"%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				회의록 정보
			</div>
		</td>
		<td class="right">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>

<table class="view-table">
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
				<jsp:param value="false" name="multi" />
				<jsp:param value="meeting" name="obj" />
				<jsp:param value="180" name="height" />
			</jsp:include>
		</td>
	</tr>
	<tr>
		<th>내용</th>
		<td class="indent5" colspan="3">
			<textarea name="description" id="description"  readonly="readonly"><%=dto.getContent()%></textarea>
		</td>
	</tr>
	<tr>
		<th>첨부파일</th>
		<td class="indent5" colspan="3">
			<jsp:include page="/extcore/include/attachment-view.jsp">
				<jsp:param value="<%=dto.getOid()%>" name="oid" />
				<jsp:param value="secondary" name="mode" />
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
				editor.on('init', function() {
					const content = editor.getContent();
					editor.setContent(content);
				});
			}
		});
	}
	
	document.addEventListener("DOMContentLoaded", function() {
		_createAUIGrid(_columns);
		AUIGrid.resize(_myGridID);
		loadTinymce();
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(_myGridID);
	});
</script>