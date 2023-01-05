<%@page import="wt.representation.Representation"%>
<%@page import="java.util.Locale"%>
<%@page import="com.ptc.wvs.server.ui.UIHelper"%>
<%@page import="com.ptc.wvs.server.util.Util"%>
<%@page import="wt.content.ContentRoleType"%>
<%@page import="com.ptc.wvs.server.util.FileHelper"%>
<%@page import="com.ptc.wvs.server.util.WVSContentHelper"%>
<%@page import="wt.content.FormatContentHolder"%>
<%@page import="wt.content.ContentHelper"%>
<%@page import="com.ptc.wvs.server.util.PublishUtils"%>
<%@page import="e3ps.common.util.ThumnailUtils"%>
<%@page import="wt.epm.EPMDocument"%>
<%@page import="wt.content.ContentHolder"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="wt.part.WTPart"%>
<%@page import="wt.viewmarkup.Viewable"%>
<%@page import="java.net.URL"%>
<%@page import="wt.viewmarkup.ViewMarkUpHelper"%>
<%@page import="com.ptc.wvs.server.ui.MarkupHelper"%>
<%@page import="wt.content.ApplicationData"%>
<%@page import="wt.content.ContentItem"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@page import="wt.viewmarkup.WTMarkUp"%>
<%@page import="java.util.Vector"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	Vector<WTMarkUp> list = (Vector<WTMarkUp>) request.getAttribute("list");
	String popup = (String) request.getParameter("popup");
	boolean isPopup = Boolean.parseBoolean(popup);
	String oid = (String) request.getParameter("oid");
	ReferenceFactory rf = new ReferenceFactory();
	WTPart part = (WTPart)rf.getReference(oid).getObject();
	// data
	
	// isAdmin
	boolean isAdmin = CommonUtils.isAdmin();
%>

<td valign="top">
	<script type="text/javascript">
	$(document).ready(function() {
	})
	</script>
	<input type="hidden" name="popup" id="popup" value="<%=isPopup %>">

	<div class="header_title">
		<i class="axi axi-subtitles"></i><span>주석 세트 정보</span>
	</div>
	<%
		if(isPopup) {
	%>
	<div class="right">
		<input type="button" value="닫기" id="closeDocBtn" title="닫기" class="redBtn">
	</div>
	<%
		}
	%>

	<table class="in_list_table left-border">
		<colgroup>
			<col width="150">
			<col width="200">
			<col width="200">
			<col width="200">
			<col width="200">
		</colgroup>
		<thead>
			<tr>
				<th>이름</th>
				<th>축소판</th>
				<th>설명</th>
				<th>작성자</th>
				<th>작성일</th>
			</tr>						
		</thead>
		<tbody id="grid_list">
		<%
			for(WTMarkUp markup : list) {
				String moid = markup.getPersistInfo().getObjectIdentifier().getStringValue();
				String markUpImg = ThumnailUtils.getMarkUp(markup)[0];
				String markupUrl = ThumnailUtils.getMarkUpCreoViewUrl(oid, markup);
		%>
		<tr>
			<td><%=markup.getName() %></td>
			<td><img data-url="<%=markupUrl %>" src="<%=markUpImg %>" class="pos3 markupStyle markupCreo"></td>
			<td><%=markup.getDescription() != null ? markup.getDescription() : ""%></td>
			<td><%=markup.getOwnership().getOwner().getFullName() %></td>
			<td><%=markup.getCreateTimestamp().toString().substring(0, 16) %></td>
		</tr>
		<%
			}
		%>
		</tbody>
	</table>
</td>