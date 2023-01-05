<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="e3ps.common.util.CommonUtils"%>
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
			<col width="300">
			<col width="300">
			<col width="*">
			<col width="100">
			<col width="100">
			<col width="100">
			<col width="130">
		</colgroup>
		<thead>
			<tr>
				<th>파일이름</th>
				<th>품명</th>
				<th>규격</th>
				<th>상태</th>
				<th>버전</th>	
				<th>등록자</th>
				<th>등록일</th>			
			</tr>
		</thead>
		<%
			while(result.hasMoreElements()) {
				Persistable per = (Persistable)result.nextElement();

				boolean isLatest = CommonUtils.isLatestVersion(per);
				if(!isLatest) {
					per = (Persistable)CommonUtils.getLatestVersion(per);
					per = (Persistable)PersistenceHelper.manager.refresh(per);
				}
				
				String poid = per.getPersistInfo().getObjectIdentifier().getStringValue();
				int idx = 1;
				String[] ss = ApprovalHelper.manager.getContractEpmData(per);
				
				String iconPath = ContentUtils.getStandardIcon(poid);
		%>
		<tr>
			<td class="infoPer left" data-oid="<%=poid %>"><img src="<%=iconPath %>" class="pos3">&nbsp;<%=ss[idx++] %></td>
			<td class="infoPer left" data-oid="<%=poid %>"><img src="<%=iconPath %>" class="pos3">&nbsp;<%=ss[idx++] %></td>
			<td><%=ss[idx++] %></td>
			<td><%=ss[idx++] %></td>
			<td><%=ss[idx++] %></td>
			<td><%=StringUtils.replaceToValue(ss[idx++]) %></td>
			<td><%=ss[idx++] %></td>
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
%>