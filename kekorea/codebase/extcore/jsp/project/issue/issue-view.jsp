<%@page import="wt.log4j.SystemOutFacade"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="e3ps.project.issue.beans.IssueDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
IssueDTO dto = (IssueDTO) request.getAttribute("dto");
%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>    
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				특이사항 정보
			</div>
		</td>
		<td class="right">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>
<div id="tabs">
	<ul>
		<li>
			<a href="#tabs-1">기본정보</a>
		</li>
	</ul>
	<div id="tabs-1">
		<table class="view-table">
			<colgroup>
				<col width="10%">
				<col width="300">
				<col width="130">
				<col width="300">
			</colgroup>
			<tr>
				<th class="lb">특이사항 제목</th>
				<td class="indent5" colspan="3"><%=dto.getName()%></td>
			</tr>
			<tr>
				<th class="lb">작성자</th>
				<td class="indent5"><%=dto.getCreator()%></td>
				<th class="lb">작성일</th>
				<td class="indent5"><%=dto.getCreatedDate().toString().substring(0, 10)%></td>
			</tr>
			<tr>
				<th class="lb">설명</th>
				<td colspan="3" class="indent5">
					<textarea name="descriptionNotice" id="descriptionNotice" rows="12" cols="" readonly="readonly"><%=dto.getContent()%></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">KEK 작번</th>
				<td colspan="3" class="indent5">
				<jsp:include page="/extcore/include/project-include.jsp">
				<jsp:param value="<%=dto.getOid()%>" name="oid" />
				<jsp:param value="view" name="mode" />
				<jsp:param value="false" name="multi" />
				<jsp:param value="issue" name="obj" />
				<jsp:param value="180" name="height" />
			</jsp:include>
		</table>
	</div>
</div>
<script type="text/javascript">
	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs();
	})
</script>