<%@page import="wt.content.ApplicationData"%>
<%@page import="wt.content.ContentRoleType"%>
<%@page import="wt.content.ContentHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@page import="wt.content.ContentHolder"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
String mode = StringUtils.getParameter(request.getParameter("mode"), "create");
String height = StringUtils.getParameter(request.getParameter("height"), "150");
%>

<%
// 등록 모드
if ("create".equals(mode)) {
%>
<div class="AXUpload5" id="primary_layer"></div>
<div class="AXUpload5QueueBox_list" id="uploadQueueBox" style="height: <%=height%>px;"></div>
<script type="text/javascript">
	let primary = new AXUpload5();
	function load() {
		primary.setConfig({
			isSingleUpload : false,
			targetID : "primary_layer",
			uploadFileName : "primary",
			buttonTxt : "파일 선택",
			uploadMaxFileSize : (1024 * 1024 * 1024),
			uploadUrl : getCallUrl("/aui/upload"),
			dropBoxID : "uploadQueueBox",
			queueBoxID : "uploadQueueBox",
			uploadPars : {
				roleType : "primary"
			},
			uploadMaxFileCount : 100,
			deleteUrl : getCallUrl("/aui/delete"),
			fileKeys : {},
			onComplete : function() {
				let form = document.querySelector("form");
				for (let i = 0; i < this.length; i++) {
					let primaryTag = document.createElement("input");
					primaryTag.type = "hidden";
					primaryTag.name = "primarys";
					primaryTag.value = this[i].fullPath;
					form.appendChild(primaryTag);
				}
			},
		})
	}
	load();
</script>
<%
// 뷰 모드
} else if ("view".equals(mode)) {
ContentHolder holder = (ContentHolder) CommonUtils.getObject(oid);
QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
while (result.hasMoreElements()) {
	ApplicationData data = (ApplicationData) result.nextElement();
%>
<p><a href=""><%=data.getFileName()%></a></p>
<%
}
}
%>