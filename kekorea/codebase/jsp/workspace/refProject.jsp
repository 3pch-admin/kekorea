<%@page import="e3ps.project.Project"%>
<%@page import="e3ps.partlist.PartListMasterProjectLink"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="e3ps.partlist.PartListMaster"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="e3ps.approval.service.ApprovalHelper"%>
<%@page import="wt.fc.Persistable"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="e3ps.approval.beans.ApprovalLineViewData"%>
<%@page import="e3ps.workspace.ApprovalLine"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
	ReferenceFactory rf = new ReferenceFactory();
	ApprovalLine line = (ApprovalLine)rf.getReference(oid).getObject();
	ApprovalLineViewData data = new ApprovalLineViewData(line);
	
	PartListMaster m = (PartListMaster)data.per;
	
	QueryResult result = PersistenceHelper.manager.navigate(m, "project", PartListMasterProjectLink.class);
	
	// 일괄 결재
// 	out.println(result);
	if(result != null) {
		int size = result.size();
		String prefix = WorkspaceHelper.manager.getPrefix(data.per);
	if(size >= 6) {
%>
<div class="clear5"></div>
<!-- <div class="refAppObject_container" id="refAppObject_container"> -->
<%
}
%>
	<table class="in_list_table left-border2" id="refAppObject_table">
		<colgroup>
			<col width="40">
			<col width="80">
			<col width="100">
			<col width="100">
			<col width="100">
			<col width="100">
			<col width="300">
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
		<%
		int cnt = 1;
			while(result.hasMoreElements()) {
				Project project = (Project) result.nextElement();
				String poid = project.getPersistInfo().getObjectIdentifier().getStringValue();
		%>
			<tr>
				<td class="center"><%=cnt++%></td>
				<td class="center"><%=project.getPType()%></td>
				<td class="center infoProject" data-oid="<%=poid%>"><%=project.getKekNumber()%></td>
				<td class="center infoProject" data-oid="<%=poid%>"><%=project.getKeNumber()%></td>
				<td class="center"><%=project.getCustomer()%></td>
				<td class="center"><%=project.getMak()%></td>
				<td class="left indent10"><%=project.getDescription()%></td>
			</tr>
		<%
		}
		%>
	</table>
	<%
	if(size >= 6) {
	%>
<!-- </div> -->
<%
}
	}
	// 단일 결재
	if(result == null) {
		String prefix = WorkspaceHelper.manager.getPrefix(data.per);
%>
<table class="in_list_table left-border2" id="refAppObject_table">
	<colgroup>
		<col width="350">
		<col width="*">
		<col width="100">
		<col width="100">
		<col width="100">
		<col width="130">
	</colgroup>
	<tr>
		<th><%=prefix%>번호</th>
		<th><%=prefix%>제목</th>
		<th>상태</th>
		<th>버전</th>	
		<th>수정자</th>
		<th>수정일</th>			
	</tr>
	<%
	String[] ss = WorkspaceHelper.manager.getContractObjData(data.per);
			int idx = 1;
			String poid = data.per.getPersistInfo().getObjectIdentifier().getStringValue();
			String iconPath = ContentUtils.getStandardIcon(poid);
	%>
	<tr>
		<td class="infoPer left" data-oid="<%=poid %>"><img src="<%=iconPath %>" class="pos3">&nbsp;<%=ss[idx++] %></td>
		<td class="infoPer left" data-oid="<%=poid %>"><img src="<%=iconPath %>" class="pos3">&nbsp;<%=ss[idx++] %></td>
		<td><%=ss[idx++] %></td>
		<td><%=ss[idx++] %></td>
		<td><%=ss[idx++] %></td>
		<td><%=ss[idx++] %></td>
	</tr>
</table>
<%
	}
%>