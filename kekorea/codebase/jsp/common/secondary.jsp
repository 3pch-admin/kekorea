<%@page import="java.util.Vector"%>
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
<table class="in_list_table left-border2" id="secondary_table">
	<colgroup>
<!-- 		<col width="*"> -->
		<col width="250">
		<col width="50">
	</colgroup>
	<tr>
		<th>파일명</th>
<!-- 		<th>파일타입</th> -->
		<th>파일크기</th>	
	</tr>
	<%
		Vector<String[]> secondarys = ContentUtils.getSecondary(holder);
		for(String[] secondary : secondarys) {
	%>
	<tr>
		<td class="left"><a href="<%=secondary[5] %>"><img src="<%=secondary[4] %>" class="pos2">&nbsp;<%=secondary[2] %></a></td>
<%-- 		<td><%=secondary[7] %></td> --%>
		<td><%=secondary[3] %></td>
	</tr>
	<%
		}
		if(secondarys.size() == 0) {
	%>
	<tr>
		<td class="nodata" colspan="2">등록된 첨부파일이 없습니다.</td>
	</tr>
	<%
		}
	%>
</table>