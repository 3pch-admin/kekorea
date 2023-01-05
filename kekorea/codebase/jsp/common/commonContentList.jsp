<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="wt.content.ContentRoleType"%>
<%@page import="wt.content.ApplicationData"%>
<%@page import="wt.content.ContentItem"%>
<%@page import="wt.content.ContentHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.content.ContentHolder"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String oid = request.getParameter("oid");
	if (!StringUtils.isNull(oid)) {
		ReferenceFactory rf = new ReferenceFactory();
		ContentHolder holder = (ContentHolder) rf.getReference(oid).getObject();

		String keys = "";
		QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.PRIMARY);
		if (result.hasMoreElements()) {
			keys += "{ primaryFile : ";
			ContentItem item = (ContentItem) result.nextElement();
			ApplicationData data = (ApplicationData) item;
			String savePath = data.getUploadedFromPath();
			String delocId = item.getPersistInfo().getObjectIdentifier().getStringValue();
			String fileName = data.getFileName();
			long fileSize = data.getFileSize();
			String ext = fileName.split(".").length > 1 ? fileName.split(".")[1] : "";
			String roleType = data.getRole().toString().toLowerCase();

			keys += "{";
			keys += "id : 'AXUpload_Single', ";
			keys += "name : '" + fileName + "', ";
			keys += "type : '" + ext + "', ";
			keys += "saveName : '" + fileName + "', ";
			keys += "fileSize : '" + fileSize + "', ";
			keys += "uploadedPath : '', ";
			keys += "thumbUrl : '', ";
			keys += "roleType : '" + roleType + "', ";
			keys += "saveLoc : '" + savePath + "', ";
			keys += "delocId : '" + delocId + "', ";
			keys += "}";
		}

		if (result.size() > 0) {
			keys += ", secondaryFile : [";
		} else {
			keys = "{ secondaryFile : [";
		}

		result.reset();

		int cnt = 0;
		result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
		while (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			ApplicationData data = (ApplicationData) item;

			String savePath = data.getUploadedFromPath();
			String delocId = item.getPersistInfo().getObjectIdentifier().getStringValue();
			String fileName = data.getFileName();
			long fileSize = data.getFileSize();
			String ext = fileName.split(".").length > 1 ? fileName.split(".")[1] : "";
			String roleType = data.getRole().toString().toLowerCase();

			keys += "{";
			keys += "id : 'AXUpload_AX_" + cnt + "', ";
			keys += "name : '" + fileName + "', ";
			keys += "type : '" + ext + "', ";
			keys += "saveName : '" + fileName + "', ";
			keys += "fileSize : '" + fileSize + "', ";
			keys += "uploadedPath : '', ";
			keys += "thumbUrl : '', ";
			keys += "roleType : '" + roleType + "', ";
			keys += "saveLoc : '" + savePath + "', ";
			keys += "delocId : '" + delocId + "', ";
			keys += "}";

			cnt++;
			if (result.hasMoreElements()) {
				keys += ",";
			}
		}

		keys += "]}";
		out.println(keys);
	}
%>