<%@page import="wt.fc.Persistable"%>
<%@page import="wt.fc.PersistenceServerHelper"%>
<%@page import="e3ps.project.task.Task"%>
<%@page import="wt.query.QuerySpec"%>
<%@page import="wt.query.ClassAttribute"%>
<%@page import="wt.query.SQLFunction"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="e3ps.epm.numberRule.NumberRule"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
QuerySpec query = new QuerySpec();
int idx = query.appendClassList(Task.class, false);
query.setAdvancedQueryEnabled(true);

ClassAttribute ca = new ClassAttribute(Task.class, Task.SORT);
SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.MAXIMUM, ca);
query.appendSelect(function, false);
System.out.println(query);
QueryResult result = PersistenceServerHelper.manager.query(query);
if (result.hasMoreElements()) {
	Object[] obj = (Object[]) result.nextElement();
	BigDecimal next = (BigDecimal)obj[0];
	System.out.println(next);
}


%>