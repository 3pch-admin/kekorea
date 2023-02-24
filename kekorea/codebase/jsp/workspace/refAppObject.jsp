<%@page import="e3ps.common.util.StringUtils"%>
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
	QueryResult result = data.result;
	// 일괄 결재
// 	out.println(result);
	if(result != null) {
		int size = result.size();
		String prefix = ApprovalHelper.manager.getPrefix(data.per);
	if(size >= 6) {
%>
<div class="clear5"></div>
<div class="refAppObject_container" id="refAppObject_container">
<%
	}
%>
	<table class="in_list_table left-border2" id="refAppObject_table">
		<colgroup>
			<col width="350">
			<col width="*">
			<col width="100">
			<col width="100">
			<col width="100">
			<col width="130">
			<col width="60">
		</colgroup>
		<thead>
			<tr>
				<th><%=prefix %>번호</th>
				<th><%=prefix %>제목</th>
				<th>상태</th>
				<th>버전</th>	
				<th>수정자</th>
				<th>수정일</th>
				<th>첨부파일</th>			
			</tr>
		</thead>
		<%
			while(result.hasMoreElements()) {
				Persistable per = (Persistable)result.nextElement();
				String poid = per.getPersistInfo().getObjectIdentifier().getStringValue();
				int idx = 1;
				String[] ss = ApprovalHelper.manager.getContractObjData(per);
				String iconPath = ContentUtils.getStandardIcon(poid);
				String[] primarys = ContentUtils.getPrimary(poid);
		%>
		<tr>
			<td class="infoPer left" data-oid="<%=poid %>"><img src="<%=iconPath %>" class="pos3">&nbsp;<%=ss[idx++] %></td>
			<td class="infoPer left" data-oid="<%=poid %>"><img src="<%=iconPath %>" class="pos3">&nbsp;<%=ss[idx++] %></td>
			<td><%=ss[idx++] %></td>
			<td><%=ss[idx++] %></td>
			<td><%=StringUtils.replaceToValue(ss[idx++]) %></td>
			<td><%=ss[idx++] %></td>
			<%
				if(primarys[5] != null) {
			%>
			<td><a href="<%=primarys[5] %>"><img src="<%=primarys[4] %>" class="pos2"></a></td>
			<%
				} else {
			%>
			<td>&nbsp;</td>
			<%
				}
			%>
		</tr>
		<%
			}
		%>
	</table>
	<%
		if(size >= 6) {
	%>
</div>
<%
		}
	}
	// 단일 결재
	if(result == null) {
		String prefix = ApprovalHelper.manager.getPrefix(data.per);
%>
<table class="in_list_table left-border2" id="refAppObject_table">
	<colgroup>
		<col width="350">
		<col width="*">
		<col width="100">
		<col width="100">
		<col width="100">
		<col width="130">
		<col width="60">
	</colgroup>
	<tr>
		<th><%=prefix %>번호</th>
		<th><%=prefix %>제목</th>
		<th>상태</th>
		<th>버전</th>	
		<th>수정자</th>
		<th>수정일</th>
		<th>첨부파일</th>			
	</tr>
	<%
		String[] ss = ApprovalHelper.manager.getContractObjData(data.per);
		int idx = 1;
		String poid = data.per.getPersistInfo().getObjectIdentifier().getStringValue();
		String iconPath = ContentUtils.getStandardIcon(poid);
		String[] primarys = ContentUtils.getPrimary(poid);
	%>
	<tr>
		<td class="infoPer left" data-oid="<%=poid %>"><img src="<%=iconPath %>" class="pos3">&nbsp;<%=ss[idx++] %></td>
		<td class="infoPer left" data-oid="<%=poid %>"><img src="<%=iconPath %>" class="pos3">&nbsp;<%=ss[idx++] %></td>
		<td><%=ss[idx++] %></td>
		<td><%=ss[idx++] %></td>
		<td><%=StringUtils.replaceToValue(ss[idx++]) %></td>
		<td><%=ss[idx++] %></td>
		<td><a href="<%=primarys[5] %>"><img src="<%=primarys[4] %>" class="pos2"></a></td>
	</tr>
</table>
<%
	}
%>