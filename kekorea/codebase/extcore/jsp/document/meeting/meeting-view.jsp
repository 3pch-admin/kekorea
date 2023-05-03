<%@page import="e3ps.doc.meeting.dto.MeetingDTO"%>
<%@page import="com.lowagie.text.Meta"%>
<%@page import="e3ps.doc.meeting.dto.MeetingTemplateDTO"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
MeetingDTO dto = (MeetingDTO) request.getAttribute("dto");
%>
<%@include file="/extcore/jsp/common/tinymce.jsp"%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>    
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<input type="hidden" name="poid" id="poid" value="<%=dto.getPoid()%>">
<input type="hidden" name="loid" id="loid" value="<%=dto.getLoid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				회의록 정보
			</div>
		</td>
		<td class="right">
			<input type="button" value="수정" title="수정" class="green" onclick="modify();">
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
		<th class="lb">회의록 제목</th>
		<td class="indent5"><%=dto.getName()%></td>
		<th>회의록 템플릿</th>
		<td class="indent5"><%=dto.getTname() != null ? dto.getTname() : ""%></td>
	</tr>
	<tr>
		<th class="lb">KEK 작번</th>
		<td class="indent5" colspan="3">
			<jsp:include page="/extcore/jsp/common/project-include.jsp">
				<jsp:param value="<%=dto.getOid()%>" name="oid" />
				<jsp:param value="view" name="mode" />
			</jsp:include>
		</td>
	</tr>
	<tr>
		<th class="lb">내용</th>
		<td class="indent5" colspan="3">
			<textarea rows="5" readonly="readonly"><%=dto.getContent()%></textarea>
		</td>
	</tr>
	<tr>
		<th class="lb">첨부파일</th>
		<td class="indent5" colspan="3">
			<jsp:include page="/extcore/jsp/common/secondary-view.jsp">
				<jsp:param value="<%=dto.getOid()%>" name="oid" />
			</jsp:include>
		</td>
	</tr>
</table>
<script type="text/javascript">
	function loadTinymce() {
		tinymce.init({
			selector : 'textarea',
			height : 400,
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
		createAUIGrid9(columns9);
		AUIGrid.resize(myGridID9);
		loadTinymce();
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID9);
	});

	function modify() {
		const loid = document.getElementById("loid").value;
		const url = getCallUrl("/meeting/update?oid=" + loid);
		openLayer();
		document.location.href = url;
	}
</script>