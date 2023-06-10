<%@page import="wt.content.ApplicationData"%>
<%@page import="wt.content.ContentRoleType"%>
<%@page import="wt.content.ContentHelper"%>
<%@page import="wt.epm.build.EPMBuildHistory"%>
<%@page import="wt.epm.build.EPMBuildRule"%>
<%@page import="wt.vc.VersionControlHelper"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="wt.query.ClassAttribute"%>
<%@page import="wt.query.OrderBy"%>
<%@page import="wt.vc.baseline.Baseline"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="wt.part.QuantityUnit"%>
<%@page import="wt.part.Quantity"%>
<%@page import="wt.fc.PersistenceServerHelper"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.part.WTPartUsageLink"%>
<%@page import="wt.vc.views.View"%>
<%@page import="wt.part.WTPartStandardConfigSpec"%>
<%@page import="wt.part.WTPartAsMaturedConfigSpec"%>
<%@page import="wt.part.WTPartConfigSpec"%>
<%@page import="wt.part.WTPart"%>
<%@page import="java.util.HashMap"%>
<%@page import="wt.part.WTPartHelper"%>
<%@page import="wt.epm.util.EPMHelper"%>
<%@page import="wt.epm.structure.EPMMemberLink"%>
<%@page import="wt.query.QuerySpec"%>
<%@page import="wt.epm.structure.EPMStructureHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="com.ptc.windchill.collector.api.cad.CadCollector.GatherFamilyMembers"%>
<%@page import="com.ptc.windchill.collector.api.cad.CadCollector.GatherFamilyGenerics"%>
<%@page import="wt.fc.Persistable"%>
<%@page import="com.ptc.windchill.collector.api.cad.CadCollector.GatherDependents"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Collection"%>
<%@page import="com.ptc.windchill.collector.api.cad.CadCollectedResult"%>
<%@page import="com.ptc.windchill.collector.api.cad.CadCollector"%>
<%@page import="wt.vc.config.ConfigSpec"%>
<%@page import="java.util.Arrays"%>
<%@page import="wt.filter.NavigationCriteria"%>
<%@page import="wt.fc.collections.WTArrayList"%>
<%@page import="wt.epm.workspaces.EPMAsStoredConfigSpec"%>
<%@page import="wt.epm.EPMDocConfigSpec"%>
<%@page import="wt.fc.collections.WTCollection"%>
<%@page import="wt.epm.EPMDocument"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String eoid = "wt.epm.EPMDocument:1560012";

ReferenceFactory rf = new ReferenceFactory();
EPMDocument top = (EPMDocument) rf.getReference(eoid).getObject();
QueryResult qr = ContentHelper.service.getContentsByRole(top, ContentRoleType.SECONDARY);
while (qr.hasMoreElements()) {
	ApplicationData data = (ApplicationData) qr.nextElement();
	out.println(data.getFileName());
}
%>