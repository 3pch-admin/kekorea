<%@page import="java.util.Vector"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
String mode = request.getParameter("mode");
%>
<%
// 주첨부
if ("primary".equals(mode)) {
	String[] primarys = ContentUtils.getPrimary(oid);
%>
<div><%=primarys[6]%></div>
<%
} else if ("secondary".equals(mode)) {
Vector<String[]> secondarys = ContentUtils.getSecondary(oid);
for (String[] secondary : secondarys) {
%>
<div>
	<p>
		<a href="<%=secondary[5]%>">
			<span style="position: relative; bottom: 2px;"><%=secondary[2]%></span>
			<img src="<%=secondary[4]%>" style="position: relative; top: 1px;">
		</a>
	</p>
</div>
<%
}
}
%>