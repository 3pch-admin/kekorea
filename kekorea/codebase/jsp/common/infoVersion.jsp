<%@page import="wt.vc.VersionControlHelper"%>
<%@page import="wt.enterprise.RevisionControlled"%>
<%@page import="wt.util.SortedEnumeration"%>
<%@page import="e3ps.approval.beans.ApprovalLineViewData"%>
<%@page import="e3ps.approval.ApprovalLine"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.approval.beans.ApprovalMasterViewData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	SortedEnumeration se = (SortedEnumeration) request.getAttribute("se");
	String sort = (String) request.getAttribute("sort");
%>
<td valign="top">

<!-- script -->
	<script type="text/javascript">
	$(document).ready(function() {
		$("#closeVerBtn").click(function() {
			self.close();
		})
		
		$(".up").click(function() {
			$(document).onLayer();
			$("input[name=sort]").val(1);
			$("form").submit();
		})
		
		$(".down").click(function() {
			$(document).onLayer();
			$("input[name=sort]").val(2);
			$("form").submit();
		})
	})
	</script>
	<input type="hidden" name="sort">
	<table class="btn_table top_margin0">
		<tr>
			<td class="left">
				<div class="header_title pos5">
					<i class="axi axi-subtitles"></i><span>버전 정보</span>
				</div>
			</td>
			<td class="right">
				<input type="button" id="closeVerBtn" title="닫기" value="닫기" class="redBtn">
			</td>
		</tr>
	</table>
	
	<table class="in_list_table left-border2">
		<colgroup>
			<col width="250">
			<col width="80">
			<col width="120">
			<col width="120">
			<col width="120">
			<col width="120">
<!-- 			<col width="*"> -->
		</colgroup>
		<tr>
			<th>이름</th>
			<th>버전
			<%
				if("1".equals(sort)) {
			%>
			<font class="down" title="내림차순">↓</font>
			<%
				} else {
			%>
			<font class="up" title="오름차순">↑</font>
			<%
				}
			%>
			</th>
			<th>작성자</th>
			<th>작성일</th>
			<th>수정자</th>
			<th>수정일</th>
<!-- 			<th>수정사유</th> -->
		</tr>
		<%
			while(se.hasMoreElements()) {
				RevisionControlled rc = (RevisionControlled) se.nextElement();
				String note = VersionControlHelper.getNote(rc);
				String oid = rc.getPersistInfo().getObjectIdentifier().getStringValue();
		%>
		<tr>
			<td class="name_column infoPer" data-oid="<%=oid %>"><%=rc.getName() %></td>
			<td class="version_column infoPer" data-oid="<%=oid %>">
				<%=rc.getVersionIdentifier().getSeries().getValue() %>.<%=rc.getIterationIdentifier().getSeries().getValue() %>
			</td>
			<td><%=rc.getCreatorFullName() %></td>
			<td><%=rc.getCreateTimestamp().toString().substring(0, 16) %></td>			
			<td><%=rc.getModifierFullName() %></td>
			<td><%=rc.getModifyTimestamp().toString().substring(0, 16) %></td>
<%-- 			<td class="left"><%=note != null ? note : "" %></td>		 --%>
		</tr>
		<%
			}
		%>
	</table>
</td>