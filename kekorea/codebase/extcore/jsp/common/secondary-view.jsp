<%@page import="java.util.Map"%>
<%@page import="java.util.Vector"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
Vector<Map<String, Object>> secondarys = ContentUtils.getSecondary(oid);
for (Map<String, Object> secondary : secondarys) {
%>
<div>
	<p>
		<a href="<%=secondary.get("url")%>">
			<span style="position: relative; bottom: 2px;"><%=secondary.get("name")%></span>
			<img src="<%=secondary.get("fileIcon")%>" style="position: relative; top: 1px;">
		</a>
	</p>
</div>
<%
}
if (secondarys.size() == 0) {
%>
<font color="red">
	<b>등록된 첨부파일이 없습니다.</b>
</font>
<%
}
%>