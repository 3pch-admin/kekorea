<%@page import="e3ps.epm.workOrder.dto.WorkOrderDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
WorkOrderDTO dto = (WorkOrderDTO) request.getAttribute("dto");
%>
<!-- AUIGrid -->
<%@include file="/extcore/include/auigrid.jsp"%>
<table class="view-table">
	<colgroup>
		<col width="150">
		<col width="700">
		<col width="150">
		<col width="700">
	</colgroup>
	<tr>
		<th class="lb">도면 일람표 제목</th>
		<td class="indent5"><%=dto.getName()%></td>
		<th>상태</th>
		<td class="indent5"><%=dto.getState()%></td>
	</tr>
	<tr>
		<th class="lb">작성자</th>
		<td class="indent5"><%=dto.getCreator()%></td>
		<th>작성일</th>
		<td class="indent5"><%=dto.getCreatedDate_txt()%></td>
	</tr>
	<tr>
		<th class="lb">KEK 작번</th>
		<td class="indent5" colspan="3">
			<jsp:include page="/extcore/include/project-include.jsp">
				<jsp:param value="<%=dto.getOid()%>" name="oid" />
				<jsp:param value="view" name="mode" />
				<jsp:param value="false" name="multi" />
				<jsp:param value="project" name="obj" />
				<jsp:param value="400" name="height" />
			</jsp:include>
		</td>
	</tr>
	<tr>
		<th class="lb">내용</th>
		<td class="indent5" colspan="3">
			<textarea rows="7" cols="" readonly="readonly"><%=dto.getContent() != null ? dto.getContent() : ""%></textarea>
		</td>
	</tr>
	<tr>
		<th class="lb">첨부파일</th>
		<td class="indent5" colspan="3">
			<jsp:include page="/extcore/include/attachment-view.jsp">
				<jsp:param value="<%=dto.getOid()%>" name="oid" />
				<jsp:param value="secondary" name="mode" />
			</jsp:include>
		</td>
	</tr>
</table>