<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="e3ps.admin.FunctionControl"%>
<%
FunctionControl fc = FunctionControl.newFunctionControl();

PersistenceHelper.manager.save(fc);
%>