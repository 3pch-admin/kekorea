<%@page import="e3ps.common.util.CommonUtils"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="e3ps.epm.numberRule.NumberRule"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
String oid = "e3ps.admin.commonCode.CommonCode:182753";
CommonCode c = (CommonCode) CommonUtils.getObject(oid);
System.out.println(c);
NumberRule numberRule = NumberRule.newNumberRule();
numberRule.setDepartment(c);
numberRule.setBusinessSector(c);
numberRule.setDocument(c);
numberRule.setDrawingCompany(c);
numberRule.setName("123");
numberRule.setNumber("KEDA00002");
numberRule.setVersion(1);
PersistenceHelper.manager.save(numberRule);
System.out.println("종룟,.");
%>