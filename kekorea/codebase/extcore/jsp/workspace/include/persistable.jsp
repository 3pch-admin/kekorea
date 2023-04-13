<%@page import="e3ps.workspace.ApprovalContract"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@page import="wt.fc.Persistable"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
Persistable per = (Persistable) CommonUtils.getObject(oid);
%>


<!-- 일괄결재 문서 & 도면 -->
<%
	if(per instanceof ApprovalContract) {
%>


<%
	}
%>