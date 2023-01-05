<%@page import="e3ps.doc.service.DocumentHelper"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.project.ProjectOutputLink"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="e3ps.project.Project"%>
<%@page import="e3ps.partlist.PartListMasterProjectLink"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="e3ps.partlist.PartListMaster"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="e3ps.approval.service.ApprovalHelper"%>
<%@page import="wt.fc.Persistable"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="e3ps.approval.beans.ApprovalLineViewData"%>
<%@page import="e3ps.approval.ApprovalLine"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String oid = request.getParameter("oid");
	ReferenceFactory rf = new ReferenceFactory();
	ApprovalLine line = (ApprovalLine)rf.getReference(oid).getObject();
	ApprovalLineViewData data = new ApprovalLineViewData(line);
	
	WTDocument m = (WTDocument)data.per;
	
	ArrayList<ProjectOutputLink> projectList = DocumentHelper.manager.getProjectOutputLink(m);
	
	String prefix = ApprovalHelper.manager.getPrefix(data.per);
%>
<table class="in_list_table left-border2" id="refAppObject_table">
	<colgroup>
		<col width="60">
		<col width="100">
		<col width="100">
		<col width="100">
		<col width="100">
		<col width="100">
		<col width="530">
	</colgroup>
	<thead>
		<tr>
			<th>NO</th>
			<th>작번유형</th>
			<th>KEK 작번</th>
			<th>KE 작번</th>
			<th>고객사</th>
			<th>막종</th>
			<th>작업내용</th>
		</tr>
	</thead>
	<tbody id="addOutputsBody">
	<%
		int cnt = 1;
		for(ProjectOutputLink projectLink : projectList) {
			Project project = projectLink.getProject();
			String ooid = project.getPersistInfo().getObjectIdentifier().getStringValue();
	%>
	<tr>
		<td class="center"><%=cnt++ %></td>
		<td class="center"><%=project.getPType() %></td>
		<td class="center viewProject" data-oid="<%=ooid%>"><input type="hidden" name="projectOid" value="<%=ooid %>"><%=project.getKekNumber() %></td>
		<td class="center viewProject" data-oid="<%=ooid%>"><%=project.getKeNumber() %></td>
		<td class="center"><%=project.getCustomer() %></td>
		<td class="center"><%=project.getMak() %></td>
		<td class="left indent10"><%=project.getDescription() %></td>
	</tr>
	<%
		}
	%>
</table>