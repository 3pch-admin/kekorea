<%@page import="java.util.Map"%>
<%@page import="java.util.Vector"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
// 주 첨부 파일은 무조건 있어야 한다.
String oid = request.getParameter("oid");
Map<String, Object> primary = ContentUtils.getPrimary(oid);
if (primary != null) {
%>
<div>
	<a href="<%=primary.get("url")%>">
		<span style="position: relative; bottom: 2px;"><%=primary.get("name")%></span>
		<img src="<%=primary.get("fileIcon")%>" style="position: relative; top: 1px;">
	</a>
</div>
<%
} else {
%>
<font color="red">
	<b>등록된 주 첨부파일이 없습니다.</b>
</font>
<%
}
%>