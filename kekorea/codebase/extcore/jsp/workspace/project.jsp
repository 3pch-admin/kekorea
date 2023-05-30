<%@page import="e3ps.project.Project"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.workspace.service.WorkspaceHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String oid = request.getParameter("oid");
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				관련 작번
			</div>
		</td>
	</tr>
</table>

<table class="view-table">
	<colgroup>
		<col width="100">
		<col width="100">
		<col width="80">
		<col width="120">
		<col width="120">
		<col width="120">
		<col width="120">
		<col width="*">
	</colgroup>
	<tr>
		<th class="lb">KEK 작번</th>
		<th class="lb">KE 작번</th>
		<th class="lb">작번유형</th>
		<th class="lb">거래처</th>
		<th class="lb">설치장소</th>
		<th class="lb">막종</th>
		<th class="lb">막종상세</th>
		<th class="lb">작업내용</th>
	</tr>
	<%
	ArrayList<Project> list = WorkspaceHelper.manager.getProjects(oid);
	for (Project project : list) {
		String poid = project.getPersistInfo().getObjectIdentifier().getStringValue();
	%>
	<tr>
		<td class="center">
			<a href="javascript:_detail('<%=poid%>');"><%=project.getKekNumber()%></a>
		</td>
		<td class="center">
			<a href="javascript:_detail('<%=poid%>');"><%=project.getKeNumber()%></a>
		</td>
		<td class="center"><%=project.getProjectType().getName()%></td>
		<td class="center"><%=project.getCustomer() != null ? project.getCustomer().getName() : ""%></td>
		<td class="center"><%=project.getInstall() != null ? project.getInstall().getName() : ""%></td>
		<td class="center"><%=project.getMak() != null ? project.getMak().getName() : ""%></td>
		<td class="center"><%=project.getDetail() != null ? project.getDetail().getName() : ""%></td>
		<td class="indent5"><%=project.getDescription()%></td>
	</tr>
	<%
	}
	%>
</table>