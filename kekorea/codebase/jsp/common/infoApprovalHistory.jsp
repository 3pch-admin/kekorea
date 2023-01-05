<%@page import="e3ps.approval.service.ApprovalHelper"%>
<%@page import="e3ps.approval.beans.ApprovalLineViewData"%>
<%@page import="e3ps.approval.ApprovalLine"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.approval.beans.ApprovalMasterViewData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	ApprovalMasterViewData data = (ApprovalMasterViewData) request.getAttribute("data");
%>
<td valign="top">

<!-- script -->
	<script type="text/javascript">
	$(document).ready(function() {
		$("#closeAppBtn").click(function() {
			self.close();
		})
	})
	</script>
	<input type="hidden" name="oid" value="<%=data != null ? data.oid : "" %>">
	<table class="btn_table top_margin0">
		<tr>
			<td class="left">
				<div class="header_title pos5">
					<i class="axi axi-subtitles"></i><span>결재 이력</span>
				</div>
			</td>
			<td class="right">
				<input type="button" id="closeAppBtn" title="닫기" value="닫기">
			</td>
		</tr>
	</table>
	
	<table class="in_list_table left-border2">
		<colgroup>
			<col width="60">
			<col width="80">
			<col width="180">
			<col width="80">
			<col width="100">
			<col width="130">
			<col width="130">
			<col width="*">
		</colgroup>
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
		<%
			if(data != null) {
				
				ArrayList<ApprovalLine> subMit = data.appLines;
				for(ApprovalLine appLine : subMit) {
					ApprovalLineViewData datas = new ApprovalLineViewData(appLine);
					if(!datas.role.equals(ApprovalHelper.WORKING_SUBMIT)) {
						continue;
					}
			%>
			<tr>
				<td><font color="blue"><%=datas.type %></font></td>
				<td><%=datas.role %></td>
				<td class="ellipsis250" title="<%=datas.name %>"><%=datas.name%></td>
				<td><%=datas.state %></td>
				<td><%=datas.creator %></td>
				<td><%=datas.startTime %></td>
				<td><%=datas.completeTime %></td>
				<td class="left ellipsis250" title="<%=datas.description %>"><%=datas.description%></td>
			</tr>					
		<%			
				}
				ArrayList<ApprovalLine> appLines = data.agreeLines;
				for(ApprovalLine appLine : appLines) {
					ApprovalLineViewData datas = new ApprovalLineViewData(appLine);
		%>
		<tr>
			<td><font color="blue"><%=datas.type%></font></td>
			<td><%=datas.role%></td>
			<td class="ellipsis250" title="<%=datas.name %>"><%=datas.name%></td>
			<td><%=datas.state%></td>
			<td><%=datas.creator%></td>
			<td><%=datas.startTime%></td>
			<td><%=datas.completeTime%></td>
			<td class="left ellipsis250" title="<%=datas.description %>"><%=datas.description%></td>
		</tr>		
		<%
				}
				ArrayList<ApprovalLine> agreeLines = data.appLines;
				for(ApprovalLine agreeLine : agreeLines) {
					ApprovalLineViewData datas = new ApprovalLineViewData(agreeLine);
					if(datas.role.equals(ApprovalHelper.WORKING_SUBMIT)) {
						continue;
					}					
					System.out.println(datas.master.getPersistInfo().getObjectIdentifier().getStringValue());
		%>
		<tr>
			<td><font color="green"><%=datas.type%></font></td>
			<td><%=datas.role%></td>
			<td class="ellipsis250" title="<%=datas.name %>"><%=datas.name%></td>
			<td><%=datas.state%></td>
			<td><%=datas.creator%></td>
			<td><%=datas.startTime%></td>
			<td><%=datas.completeTime%></td>
			<td class="left ellipsis250" title="<%=datas.description %>"><%=datas.description%></td>
		</tr>			
		<%
				}
				ArrayList<ApprovalLine> receiveLines = data.receiveLines;
				for(ApprovalLine receiveLine : receiveLines) {
					ApprovalLineViewData datas = new ApprovalLineViewData(receiveLine);
		%>
		<tr>
			<td><font color="red"><%=datas.type %></font></td>
			<td><%=datas.role %></td>
			<td class="ellipsis250" title="<%=datas.name %>"><%=datas.name %></td>
			<td><%=datas.state %></td>
			<td><%=datas.creator %></td>
			<td><%=datas.startTime %></td>
			<td><%=datas.completeTime %></td>
			<td class="left ellipsis250" title="<%=datas.description %>"><%=datas.description %></td>
		</tr>			
		<% 
				}
			}
			if(data == null) {
		%>
		<tr>
			<td class="nodata" colspan="8">결재 이력이 없습니다.</td>
		</tr>
		<%
			}
		%>
	</table>
</td>