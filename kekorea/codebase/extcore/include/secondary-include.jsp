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
String height = StringUtils.replaceToValue(request.getParameter("height"), "150");
%>

<%
// 등록 모드
if ("create".equals(mode)) {
%>
<div class="AXUpload5" id="secondary_layer"></div>
<div class="AXUpload5QueueBox_list" id="uploadQueueBox" style="<%=height%>px;"></div>
<script type="text/javascript">
	let secondary = new AXUpload5();
	function load() {
		secondary.setConfig({
			isSingleUpload : false,
			targetID : "secondary_layer",
			uploadFileName : "secondary",
			buttonTxt : "파일 선택",
			uploadMaxFileSize : (1024 * 1024 * 1024),
			uploadUrl : getCallUrl("/aui/upload"),
			dropBoxID : "uploadQueueBox",
			queueBoxID : "uploadQueueBox",
			uploadPars : {
				roleType : "secondary"
			},
			uploadMaxFileCount : 100,
			deleteUrl : getCallUrl("/aui/delete"),
			fileKeys : {},
			onComplete : function() {
				let form = document.querySelector("form");
				for (let i = 0; i < this.length; i++) {
					let secondaryTag = document.createElement("input");
					secondaryTag.type = "hidden";
					secondaryTag.name = "secondarys";
					secondaryTag.value = this[i].fullPath;
					form.appendChild(secondaryTag);
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