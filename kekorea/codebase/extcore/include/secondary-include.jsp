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
%>
<div class="AXUpload5" id="secondary_layer"></div>
<div class="AXUpload5QueueBox_list" id="uploadQueueBox"></div>
<script type="text/javascript">
	let secondary = new AXUpload5();
	function load() {
		secondary.setConfig({
			isSingleUpload : false,
			targetID : "secondary_layer",
			uploadFileName : "secondary",
			buttonTxt : "파일 선택",
			uploadMaxFileSize : (1024 * 1024 * 1024),
			dropBoxID : "uploadQueueBox",
			uploadUrl : getCallUrl("/content/upload"),
			queueBoxID : "uploadQueueBox",
			uploadPars : {
				roleType : "secondary"
			},
			uploadMaxFileCount : 100,
			deleteUrl : getCallUrl("/content/delete"),
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