<%@page import="e3ps.approval.service.ApprovalHelper"%>
<%@page import="e3ps.approval.beans.ApprovalMasterViewData"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.approval.beans.ApprovalLineViewData"%>
<%@page import="e3ps.workspace.ApprovalLine"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String oid = request.getParameter("oid");
	ReferenceFactory rf = new ReferenceFactory();
	ApprovalLine line = (ApprovalLine)rf.getReference(oid).getObject();
	ApprovalMasterViewData mdata = new ApprovalMasterViewData(line.getMaster());
	int size = mdata.size;
	if(size >= 6) {
%>
<div class="clear5"></div>
<div class="refAppLine_container" id="refAppLine_container">
<%
	}
%>
	<table class="in_list_table left-border2" id="refAppLine_table">
		<colgroup>
			<col width="70">
			<col width="70">
			<col width="250">
			<col width="100">
			<col width="100">
			<col width="130">
			<col width="130">
			<col width="*">
		</colgroup>
		<thead>
			<tr>
				<th>구분</th>
				<th>역할</th>
				<th>결재제목</th>
				<th>상태</th>
				<th>담당자</th>
				<th>수신일</th>
				<th>완료일</th>
				<th>결재의견</th>
			</tr>
		</thead>
		<%
			ArrayList<ApprovalLine> subMit = new ApprovalMasterViewData(line.getMaster()).appLines;
			for(ApprovalLine appLine : subMit) {
				ApprovalLineViewData datas = new ApprovalLineViewData(appLine);
				if(!datas.role.equals(ApprovalHelper.WORKING_SUBMIT)) {
					continue;
				}
		%>
		<tr>
			<input type="hidden" id="hckim_appline" value="<%=appLine%>"/>
			<td><font color="blue"><%=datas.type %></font></td>
			<td><%=datas.role %></td>
			<td><%=datas.name %></td>
			<td><%=datas.state %></td>
			<td><%=datas.creator %></td>
			<td><%=datas.startTime %></td>
			<td><%=datas.completeTime %></td>
			<td class="left indent5 ellipsis250" title="<%=datas.description %>"><%=datas.description %></td>
		</tr>		
		<%
			}
			ArrayList<ApprovalLine> agreeLines = new ApprovalMasterViewData(line.getMaster()).agreeLines;
			for(ApprovalLine agreeLine : agreeLines) {
				ApprovalLineViewData datas = new ApprovalLineViewData(agreeLine);
		%>
		<tr>
			<input type="hidden" id="hckim_agreeline" value="<%=agreeLine%>"/>
			<td><font color="green"><%=datas.type %></font></td>
			<td><%=datas.role %></td>
			<td><%=datas.name %></td>
			<td><%=datas.state %></td>
			<td><%=datas.creator %></td>
			<td><%=datas.startTime %></td>
			<td><%=datas.completeTime %></td>
			<td class="left indent5 ellipsis250" title="<%=datas.description %>"><%=datas.description %></td>
		</tr>		
		<%
			}
			ArrayList<ApprovalLine> appLines = new ApprovalMasterViewData(line.getMaster()).appLines;
			for(ApprovalLine appLine : appLines) {
				ApprovalLineViewData datas = new ApprovalLineViewData(appLine);
				if(datas.role.equals(ApprovalHelper.WORKING_SUBMIT)) {
					continue;
				}
		%>
		<tr>
			<input type="hidden" id="hckim_appline2" value="<%=appLine%>"/>
			<td><font color="blue"><%=datas.type %></font></td>
			<td><%=datas.role %></td>
			<td><%=datas.name %></td>
			<td><%=datas.state %></td>
			<td><%=datas.creator %></td>
			<td><%=datas.startTime %></td>
			<td><%=datas.completeTime %></td>
			<td class="left indent5 ellipsis250" title="<%=datas.description %>"><%=datas.description %></td>
		</tr>
		<%
			}
			ArrayList<ApprovalLine> receiveLines = new ApprovalMasterViewData(line.getMaster()).receiveLines;
			for(ApprovalLine receiveLine : receiveLines) {
				ApprovalLineViewData datas = new ApprovalLineViewData(receiveLine);
		%>
		<tr>
			<input type="hidden" id="hckim_receiveLine" value="<%=receiveLine%>"/>
			<td><font color="red"><%=datas.type %></font></td>
			<td><%=datas.role %></td>
			<td><%=datas.name %></td>
			<td><%=datas.state %></td>
			<td><%=datas.creator %></td>
			<td><%=datas.startTime %></td>
			<td><%=datas.completeTime %></td>
			<td class="left indent5 ellipsis250" title="<%=datas.description %>"><%=datas.description %></td>
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
%>