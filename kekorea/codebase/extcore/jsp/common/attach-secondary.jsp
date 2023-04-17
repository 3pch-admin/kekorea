<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
%>
<div class="AXUpload5" id="secondary_layer"></div>
<div class="AXUpload5QueueBox_list" id="uploadQueueBox" style=""></div>
<script type="text/javascript">
	const secondary = new AXUpload5();
	function load() {
		secondary.setConfig({
			isSingleUpload : false,
			targetID : "secondary_layer",
			uploadFileName : "secondary",
			buttonTxt : "파일 선택",
			uploadMaxFileSize : (1024 * 1024 * 1024),
			uploadUrl : getCallUrl("/content/upload"),
			dropBoxID : "uploadQueueBox",
			queueBoxID : "uploadQueueBox",
			uploadPars : {
				roleType : "secondary"
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
					let secondaryTag = document.createElement("input");
					secondaryTag.type = "hidden";
					secondaryTag.name = "secondarys";
					secondaryTag.value = this[i].cacheId;
					secondaryTag.id = this[i].tagId;
					form.appendChild(secondaryTag);
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
			pars : "oid=<%=oid%>&roleType=secondary",
			onsucc : function(res) {
				if (!res.e) {
					const form = document.querySelector("form");
					const data = res.secondaryFile;
					const len = data.length;
					for (let i = 0; i < len; i++) {
						const secondaryTag = document.createElement("input");
						secondaryTag.type = "hidden";
						secondaryTag.id = data[i].tagId;
						secondaryTag.name = "secondarys";
						secondaryTag.value = data[i].cacheId;
						form.appendChild(secondaryTag);
					}
					secondary.setUploadedList(data);
				}
			}
		});
	}
	load();
</script>