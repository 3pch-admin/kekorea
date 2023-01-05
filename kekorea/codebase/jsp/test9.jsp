<%@page import="wt.clients.folder.FolderTaskLogic"%>
<%@page import="wt.folder.Foldered"%>
<%@page import="wt.folder.Folder"%>
<%@page import="wt.enterprise.CabinetManaged"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@page import="wt.folder.CabinetBased"%>
<%@page import="wt.folder.FolderHelper"%>
<%@page import="wt.lifecycle.StandardLifeCycleService"%>
<%@page import="wt.folder.Cabinet"%>
<%@page import="wt.epm.EPMDocument"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="e3ps.part.UnitBom"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="e3ps.common.db.DBCPManager"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.Connection"%>
<%

String oid = "wt.epm.EPMDocument:107323599";
ReferenceFactory rf = new ReferenceFactory();
EPMDocument epm = (EPMDocument)rf.getReference(oid).getObject();

out.println(epm.getFolderPath());

String path = "/Default/도면/기계/03_PRODUCT/14_DB-Hitek";
Folder folder = FolderTaskLogic.getFolder(path, CommonUtils.getContainer());

epm = (EPMDocument) CommonUtils.getLatestVersion(epm);
Cabinet master = (Cabinet) epm.getCabinet().getObject();

FolderHelper.service.changeFolder((Foldered)epm, folder);

out.println(FolderHelper.inPersonalCabinet((CabinetBased)epm));
out.println(epm.getLocation());

%>