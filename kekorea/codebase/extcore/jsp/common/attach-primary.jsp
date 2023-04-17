<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
%>
<div class="AXUpload5" id="primary_layer"></div>
<div class="AXUpload5QueueBox_list" id="uploadQueueBox" style="height: 150px;"></div>
<script type="text/javascript">
	const primary = new AXUpload5();
	function load() {
		primary.setConfig({
			isSingleUpload : false,
			targetID : "primary_layer",
			uploadFileName : "primary",
			buttonTxt : "파일 선택",
			uploadMaxFileSize : (1024 * 1024 * 1024),
			uploadUrl : getCallUrl("/content/upload"),
			dropBoxID : "uploadQueueBox",
			queueBoxID : "uploadQueueBox",
			uploadPars : {
				roleType : "primary"
			},
			uploadMaxFileCount : 100,
			deleteUrl : getCallUrl("/content/delete"),
			fileKeys : {
				name : "name",
				type : "type",
				saveName : "saveName",
				fileSize : "fileSize",
				uploadedPath : "uploadedPath",
				roleType : "roleType",
				cacheId : "cacheId",
			},
			onStart : function() {
				openLayer();
			},
			onComplete : function() {
				const form = document.querySelector("form");
				for (let i = 0; i < this.length; i++) {
					const primaryTag = document.createElement("input");
					primaryTag.type = "hidden";
					primaryTag.name = "primarys";
					primaryTag.value = this[i].cacheId;
					primaryTag.id = this[i].tagId;
					form.appendChild(primaryTag);
				}
				closeLayer();
			},
			onDelete : function() {
				const key = this.file.tagId;
				const el = document.getElementById(key);
				el.parentNode.removeChild(el);
			}
		})

		new AXReq("/Windchill/plm/content/list", {
			pars : "oid=<%=oid%>&roleType=primary",
			onsucc : function(res) {
				if (!res.e) {
					const form = document.querySelector("form");
					const data = res.primaryFile;
					const len = data.length;
					for (let i = 0; i < len; i++) {
						const primaryTag = document.createElement("input");
						primaryTag.type = "hidden";
						primaryTag.id = data[i].tagId;
						primaryTag.name = "primarys";
						primaryTag.value = data[i].cacheId;
						form.appendChild(primaryTag);
					}
					primary.setUploadedList(data);
				}
			}
		});
	}
	load();
</script>