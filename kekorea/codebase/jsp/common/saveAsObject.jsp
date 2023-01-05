<%@page import="wt.epm.EPMDocument"%>
<%@page import="wt.part.WTPart"%>
<%@page import="wt.vc.VersionControlHelper"%>
<%@page import="wt.enterprise.RevisionControlled"%>
<%@page import="wt.util.SortedEnumeration"%>
<%@page import="e3ps.approval.beans.ApprovalLineViewData"%>
<%@page import="e3ps.approval.ApprovalLine"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.approval.beans.ApprovalMasterViewData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	ArrayList<RevisionControlled> list = (ArrayList<RevisionControlled>) request.getAttribute("list");
%>
<td valign="top">

<!-- script -->
	<script type="text/javascript">
	$(document).ready(function() {
		$("#closeSaveAsBtn").click(function() {
			self.close();
		})

		$("input").checks();
		
	})
	</script>
	<input type="hidden" name="sort">
	<table class="btn_table top_margin0">
		<tr>
			<td class="left">
				<div class="header_title pos5">
					<i class="axi axi-subtitles"></i><span>파생품 생성</span>
				</div>
			</td>
			<td class="right">
				<input type="button" id="createSaveAsBtn" class="blueBtn" title="생성" value="생성">
				<input type="button" id="closeSaveAsBtn" title="닫기" value="닫기">
			</td>
		</tr>
	</table>
	
	<table class="in_list_table left-border">
		<colgroup>
			<!-- 1500 -->
			<col width="40">
			<col width="250">
			<col width="250">
			<col width="250">
			<col width="250">
			<col width="60">
			<col width="160">
			<col width="90">
			<col width="90">
		</colgroup>
		<tr>
			<th><input type="checkbox"></th>
			<th>기존 번호</th>
			<th>새 번호</th>
			<th>기존 이름</th>
			<th>새 이름</th>
			<th>버전</th>
			<th>위치</th>
			<th>등록일</th>
			<th>등록자</th>
		</tr>
		<%
			int idx = 0;
			for(RevisionControlled rc : list) {
				String oid = rc.getPersistInfo().getObjectIdentifier().getStringValue();
				String number = "";
				String name = "";
				String rNumber = "";
				String rName = "";
				if(rc instanceof WTPart) {
					WTPart part = (WTPart)rc;
					number = part.getNumber();
					name = part.getName();
				} else if(rc instanceof EPMDocument) {
					EPMDocument epm = (EPMDocument)rc;
					number = epm.getNumber();
					name = epm.getName();
				}
				
		%>
		<tr>
			<td><input type="checkbox" name="oid" value="<%=oid%>"></td>
			<td><%=number %></td>
			<td><input type="text" name="number_<%=idx %>" class="AXInput wid200" value="<%=rNumber %>"></td>
			<td><%=name %></td>
			<td><input type="text" name="name_<%=idx %>" class="AXInput wid200" value="<%=rName %>"></td>
			<td><%=rc.getVersionIdentifier().getSeries().getValue()%>.<%=rc.getIterationIdentifier().getSeries().getValue()%></td>
			<td><%=rc.getLocation()%></td>
			<td><%=rc.getCreateTimestamp().toString().substring(0, 10)%></td>
			<td><%=rc.getCreatorFullName()%></td>			
		</tr>
		<%
			idx++;
			}
		%>
	</table>
</td>