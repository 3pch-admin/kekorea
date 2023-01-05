var agent = navigator.userAgent.toLowerCase();

var isExp = (navigator.appName == "Netscape" && navigator.userAgent.search("Trident") != -1) || (agent.indexOf("misie") != -1);

var uploadURL = "/Windchill/plm/content/uploadContent";
var deleteURL = "/Windchill/jsp/common/commonContentDelete.jsp";
var listURL = "/Windchill/jsp/common/commonContentList.jsp";

var primary = new AXUpload5();
var secondary = new AXUpload5();
var allUpload = new AXUpload5();
var listParam = null;

var upload = {
	pageStart : function(oid, openMode, type) {
		if (oid) {
			listParam = "oid=" + oid;
		}
		if (type == "s" || type == "secondary") {
			upload.secondaryUpload.init(openMode);
		}

		if (type == "p" || type == "primary") {
			upload.primaryUpload.init(openMode);
		}

		if (type == "a" || type == "all") {
			upload.allUpload.init(openMode);
		}
	},

	allUpload : {
		init : function(openMode) {
			secondary.setConfig({
				isSingleUpload : false,
				targetID : "allUpload_layer",
				uploadFileName : "allContent",
				crossDomain : true,
				dropBoxID : "uploadQueueBox",
				queueBoxID : "uploadQueueBox",
				openMode : openMode,
				buttonTxt : "파일 선택",

				onClickUploadedItem : function() {
					var link = "/Windchill/jsp/common/FileDownload.jsp?fileName=" + this.saveName.dec() + "&originFileName=" + this.name.dec();
					if (this.delocId.dec() != "") {
						// link = this.uploadedPath.dec();
					}

					if (isExp) {
						window.open(link, "_blank", "width=300, height=300");
					}
				},

				uploadMaxFileSize : (1024 * 1024 * 1024),
				uploadMaxFileCount : 100,
				uploadUrl : uploadURL,
				uploadPars : {
					roleType : "allContent"
				},
				deleteUrl : deleteURL,
				fileKeys : {
					name : "name",
					type : "type",
					saveName : "saveName",
					fileSize : "fileSize",
					uploadedPath : "uploadedPath",
					thumbPath : "thumbUrl",
					roleType : "roleType",
					saveLoc : "saveLoc",
					delocId : "delocId"
				},
				onStart : function() {
					$(document).onLayer();

				},
				onComplete : function() {

					$con = $("input[name*=allContent] ");
					$.each($con, function(k) {
						$con.eq(k).remove();
					})

					var len = this.length;
					for (var i = 0; i < len; i++) {
						$("form:eq(0)").append("<input id=\"" + this[i]._id_ + "\" type=\"hidden\" name=\"" + this[i].roleType + "_" + i + "\" value=\"" + this[i].saveLoc + "&" + this[i].name + "\">");
					}
					$(document).offLayer();
					$("#fileCount").text("(" + len + "개)");
				},
				onDelete : function() {
					$("form:eq(0)").find("input[id=" + this.file._id_ + "]").remove();
					// $("#fileCount").text("(" + len + "개)");
				},
				onbeforeFileSelect : function() {
					return true;
				},

				onError : function(errorType, data) {

				}
			});

			new AXReq(listURL, {
				pars : listParam,
				onsucc : function(res) {
					if (!res.error) {
						var s = res.secondaryFile;
						secondary.setUploadedList(s);
						$len = s.length;
						for (var i = 0; i < $len; i++) {
							$("form:eq(0)").append("<input id=\"" + s[i]._id_ + "\" type=\"hidden\" name=\"" + s[i].roleType + "_" + i + "\" value=\"" + s[i].saveLoc + "&" + s[i].name + "\">");
						}
					} else {
						alert(res.msg.dec());
					}
				}
			});
		}
	},

	primaryUpload : {
		init : function(openMode) {
			primary.setConfig({
				isSingleUpload : true,
				targetID : "primary_layer",
				uploadFileName : "primary",
				openMode : openMode,
				buttonTxt : "파일 선택",

				uploadMaxFileSize : (1024 * 1024 * 1024),
				// uploadMaxFileCount : 10,
				uploadUrl : uploadURL,
				uploadPars : {
					roleType : "primary"
				},
				deleteUrl : deleteURL,
				fileKeys : {
					name : "name",
					type : "type",
					saveName : "saveName",
					fileSize : "fileSize",
					uploadedPath : "uploadedPath",
					thumbPath : "thumbUrl",
					roleType : "roleType",
					saveLoc : "saveLoc",
					delocId : "delocId"
				},
				onStart : function() {
					$(document).onLayer();

				},
				onComplete : function() {
					$("form:eq(0)").append("<input id=\"" + this[0]._id_ + "\" type=\"hidden\" name=\"" + this[0].roleType + "\" value=\"" + this[0].saveLoc + "&" + this[0].name + "\">");
					$(document).offLayer();
				},
				onDelete : function() {
					$("form:eq(0)").find("#" + this.file._id_).remove();
				},
				onClickUploadedItem : function() {
					var link = "/Windchill/jsp/common/FileDownload.jsp?fileName=" + this.saveName.dec() + "&originFileName=" + this.name.dec();
					if (this.delocId.dec() != "") {
						// link = this.uploadedPath.dec();
					}

					if (isExp) {
						window.open(link, "_blank", "width=300, height=300");
					}
				},

				onError : function(errorType, data) {

				}
			});
			new AXReq(listURL, {
				pars : listParam,
				onsucc : function(res) {
					if (!res.error) {
						var p = res.primaryFile;
						primary.setUploadedList(p);
						$("form:eq(0)").append("<input id=\"" + p._id_ + "\" type=\"hidden\" name=\"" + p.roleType + "\" value=\"" + p.saveLoc + "&" + p.name + "\">");
					} else {
						alert(res.msg.dec());
					}
				}
			});
		}
	},

	secondaryUpload : {
		init : function(openMode) {
			secondary.setConfig({
				isSingleUpload : false,
				targetID : "secondary_layer",
				uploadFileName : "secondary",
				crossDomain : true,
				dropBoxID : "uploadQueueBox",
				queueBoxID : "uploadQueueBox",
				openMode : openMode,
				buttonTxt : "파일 선택",

				onClickUploadedItem : function() {
					var link = "/Windchill/jsp/common/FileDownload.jsp?fileName=" + this.saveName.dec() + "&originFileName=" + this.name.dec();
					if (this.delocId.dec() != "") {
						// link = this.uploadedPath.dec();
					}

					if (isExp) {
						window.open(link, "_blank", "width=300, height=300");
					}
				},

				uploadMaxFileSize : (1024 * 1024 * 1024),
				uploadMaxFileCount : 100,
				uploadUrl : uploadURL,
				uploadPars : {
					roleType : "secondary"
				},
				deleteUrl : deleteURL,
				fileKeys : {
					name : "name",
					type : "type",
					saveName : "saveName",
					fileSize : "fileSize",
					uploadedPath : "uploadedPath",
					thumbPath : "thumbUrl",
					roleType : "roleType",
					saveLoc : "saveLoc",
					delocId : "delocId"
				},
				onStart : function() {
					$(document).onLayer();

				},
				onComplete : function() {
					var len = this.length;
					for (var i = 0; i < len; i++) {
						$("form:eq(0)").append("<input id=\"" + this[i]._id_ + "\" type=\"hidden\" name=\"" + this[i].roleType + "_" + i + "\" value=\"" + this[i].saveLoc + "&" + this[i].name + "\">");
					}
					$(document).offLayer();
					$("#fileCount").text("(" + len + "개)");
				},
				onDelete : function() {
					$("form:eq(0)").find("input[id=" + this.file._id_ + "]").remove();
					// $("#fileCount").text("(" + len + "개)");
				},
				onbeforeFileSelect : function() {
					return true;
				},

				onError : function(errorType, data) {

				}
			});

			new AXReq(listURL, {
				pars : listParam,
				onsucc : function(res) {
					if (!res.error) {
						var s = res.secondaryFile;
						secondary.setUploadedList(s);
						$len = s.length;
						for (var i = 0; i < $len; i++) {
							$("form:eq(0)").append("<input id=\"" + s[i]._id_ + "\" type=\"hidden\" name=\"" + s[i].roleType + "_" + i + "\" value=\"" + s[i].saveLoc + "&" + s[i].name + "\">");
						}
					} else {
						alert(res.msg.dec());
					}
				}
			});
		}
	}
}

function deleteAllFiles() {
	var k = $("form:eq(0)").find("input[name*=secondary_]");
	$.each(k, function(idx) {
		k.eq(idx).remove();
	})

	var l = $("form:eq(0)").find("div.readyselect");
	$.each(l, function(idx) {
		var fid = l.eq(idx).attr("id");
		secondary.removeUploadedList(fid);
		l.eq(idx).hide();
	})
	$("#fileCount").text("");
}