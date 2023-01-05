<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="wt.content.ContentHolder"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String oid = (String)request.getParameter("oid");
	ReferenceFactory rf = new ReferenceFactory();
	ContentHolder holder = (ContentHolder)rf.getReference(oid).getObject();
%>
<table class="in_list_table left-border2" id="primary_table">
	<colgroup>
<!-- 		<col width="*"> -->
		<col width="250">
		<col width="50">
	</colgroup>
	<tr>
		<th>파일명</th>
		<th>파일크기</th>	
	</tr>
	<%
		String[] primarys = ContentUtils.getPrimary(holder);
		if(primarys[0] != null){
	%>
	<tr>
		<td class="left"><a href="<%=primarys[5] %>"><img class="download" data-url="<%=primarys[5] %>" src="<%=primarys[4] %>" class="pos2">&nbsp;<%=primarys[2] %></a></td>
		<td><%=primarys[3] %></td>
	</tr>
	<% } else { %>
	<tr>
		<td class="nodata" colspan="2">등록된 주 첨부파일이 없습니다.</td>
	</tr>
	<%} %>
</table>