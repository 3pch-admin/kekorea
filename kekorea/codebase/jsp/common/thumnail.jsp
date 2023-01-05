<%@page import="com.ptc.wvs.common.ui.VisualizationHelper"%>
<%@page import="e3ps.common.util.ThumnailUtils"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="wt.content.ContentRoleType"%>
<%@page import="java.util.Locale"%>
<%@page import="com.ptc.wvs.server.util.PublishUtils"%>
<%@page import="com.ptc.wvs.server.util.FileHelper"%>
<%@page import="com.ptc.wvs.server.ui.UIHelper"%>
<%@page import="wt.content.ContentHolder"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String oid = request.getParameter("oid");
	String url = request.getParameter("url");
	String[] thumbs = ThumnailUtils.getThumnail(oid);
	ReferenceFactory rf = new ReferenceFactory();
	ContentHolder holder = (ContentHolder)rf.getReference(oid).getObject();
	String ua = request.getHeader("User-Agent");
	
	boolean isChrome = true;
	if(ua.contains("Chrome")) {
		isChrome = true;
	}
	
	if (thumbs[0] == null) {
		thumbs[0] = "/Windchill/wt/clients/images/wvs/productview_publish_288.png";
	}
	
	String thumb = "";
	thumb = FileHelper.getViewContentURLForType(PublishUtils.findRepresentable(holder), ContentRoleType.THUMBNAIL3D);
// 	out.println(thumb);
%>
<%
	if(isChrome) {
%>
<img src="<%=thumbs[0]%>" data-url="<%=url %>" class="creoView" title="CreoView 열기">
<%
	} else if(thumb != null) {
%>
<script type="text/javascript">
ProductView("", "<%=thumb %>");
</script>
<%
	}
%>