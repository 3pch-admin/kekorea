<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String root = (String) request.getParameter("root");
	String context = (String) request.getParameter("context");
	String type = (String) request.getParameter("type");
%>
<td valign="top" class="tree_td"><script type="text/javascript">
	$(document).ready(function() {
		var dialogs = $(document).setOpen();
		var pathname = document.location.pathname;
		var idx = pathname.lastIndexOf("/");
		var s = pathname.substring(idx + 1);
		var root = encodeURIComponent("<%=root%>");
		$("#tree").fancytree({
			collapse : function() {
				$(document).setHTML();
				if(s == "addEpm") {
					$("#epmTypes").bindSelectDisabled(true);
					var ss = $("#statesEpm").val();
					if (ss != "") {
						$("#statesEpm").bindSelectDisabled(true);
					}
				}
				
				if(s == "addPart" || s == "addEBOM" || s == "addECN") {
					var ss = $("#statesPart").val();
					if (ss != "") {
						$("#statesPart").bindSelectDisabled(true);
					}
				}
			},
			
			expand : function() {
				$(document).setHTML();
				if(s == "addEpm") {
					$("#epmTypes").bindSelectDisabled(true);
					var ss = $("#statesEpm").val();
					if (ss != "") {
						$("#statesEpm").bindSelectDisabled(true);
					}
				}
				
				if(s == "addPart" || s == "addEBOM" || s == "addECN") {
					var ss = $("#statesPart").val();
					if (ss != "") {
						$("#statesPart").bindSelectDisabled(true);
					}
				}
			},
			
			dblclick : function(e, data) {
				$(document).getColumn();
			},
			
			init : function(e, data) {
				$(document).setHTML();
				if(s == "addEpm") {
					$("#epmTypes").bindSelectDisabled(true);
					var ss = $("#statesEpm").val();
					if (ss != "") {
						$("#statesEpm").bindSelectDisabled(true);
					}
				}
				
				if(s == "addPart" || s == "addEBOM" || s == "addECN") {
					var ss = $("#statesPart").val();
					if (ss != "") {
						$("#statesPart").bindSelectDisabled(true);
					}
				}
			},
			
			edit : {
				triggerStart: ["f2"],
				close : function(e, data) {
					
					if(data.save && !data.isNew) {
						var node = data.node;
						var orgTitle = data.orgTitle;
						if (node.type == "root") {
							dialogs.alert({
								theme : "alert",
								title : "경고",
								msg : "최상위 폴더명은 수정이 불가능합니다.",
							}, function() {
								if (this.key == "ok") {
									$(".context-menu-list").hide();
									node.title = orgTitle;
									node.render(true);
								}
							})
							return false;
						}
						
						var oid = data.node.data.id;
						var poid = data.node.parent.data.id;
						var url = "/Windchill/plm/common/renameFolderAction";
						var params = new Object();
						params.oid = oid;
						params.poid = poid;
						params.text = data.node.title;
						$(document).ajaxCallServer(url, params, function(data) {
							if(data.result == "FAIL") {
								if(data.reload) {
									document.location.href = data.url;									
								} else {
									dialogs.alert({
										theme : "alert",
										title : "경고",
										msg : data.msg
									}, function() {
										if(this.key == "ok") {
											node.title = "";
										}
									})
								}
							} else if(data.result == "SUCCESS") {
// 								newNode.data.id = data.foid;
								mask.close();
							}
						}, false);
					}

					if(data.save && data.isNew) {
						var newNode = data.node;
						var poid = data.node.parent.data.id;
						var url = "/Windchill/plm/common/createFolderAction";
						var params = new Object();
						params.poid = poid;
						params.text = data.node.title;
						params.context = "<%=context%>";
						$(document).ajaxCallServer(url, params, function(data) {
							if(data.result == "FAIL") {
								if(data.reload) {
									document.location.href = data.url;									
								} else {
									dialogs.alert({
										theme : "alert",
										title : "경고",
										msg : data.msg
									}, function() {
										if(this.key == "ok") {
											newNode.remove();
										}
									})
								}
							} else if(data.result == "SUCCESS") {
								newNode.data.id = data.foid;
								mask.close();
							}
						}, false);
					}
				}
			},
			click : function(e, data) {
				var loc = data.node.data.location;
				$("#location").text(loc);
				$("input[name=location]").val(loc);
			},
			childcounter: {
		          deep: true,
		          hideZeros: true,
		          hideExpanded: true
	        },
	        loadChildren: function(event, data) {
	            // update node and parent counters after lazy loading
	            data.node.updateCounters();
            },					
			extensions : [ "edit"],
			persist : {
				store : "local"
			},
 			focusOnSelect : false,
			source : $.ajax({
				url : "/Windchill/plm/common/getFolder?root=" + root + "&type=<%=type%>&context=<%=context %>",
				type : "POST"
			})
		}).on("keydown", function(e, data) {
			var key = $.ui.fancytree.eventToString(e);
			var tree = $(this).fancytree("getTree");
			var node = tree.getActiveNode();
			var pnode = node.getPrevSibling();
			if (pnode == undefined) {
				pnode = node.getParent();
			}
			if (key == "del") {
				$(document).deleteFolder(node);
				pnode.setSelected(false);
				pnode.setActive();
			} else if (key == "alt+w") {
				$(document).createFolderSlib(node);
			} else if (key == "alt+s") {
				$(document).createFolderAfter(node);
			}
		})

		var h = $("#container_td").height();
		$("ul.fancytree-container").css("height", h - 9);

		$.contextMenu({
			selector : "#tree span.fancytree-title",
			items : {
				"rename" : {
					name : "편집 (F2)",
					icon : "edit"
				},
				"createSlib" : {
					name : "생성 (ALT+W 동일레벨)",
					icon : "paste",
					disabled : false
				},
				"createAfter" : {
					name : "생성 (ALT+S 하위레벨)",
					icon : "copy",
					disabled : false
				},
				"delete" : {
					name : "삭제 (DELETE)",
					icon : "delete",
					disabled : false
				}
			},
			callback : function(itemKey, opt) {
				var node = $.ui.fancytree.getNode(opt.$trigger);
				if (itemKey == "rename") {
					$(document).renameFolder(node);
				} else if (itemKey == "delete") {
					$(document).deleteFolder(node);
				} else if (itemKey == "createAfter") {
					$(document).createFolderAfter(node);
				} else if (itemKey == "createSlib") {
					$(document).createFolderSlib(node);
				}
			}
		});

		$.fn.renameFolder = function(node) {
			node.editStart();
		}

		$.fn.createFolderSlib = function(node) {

			if (node == null) {
				return;
			}

			if (node.type == "root") {
				dialogs.alert({
					theme : "alert",
					title : "경고",
					msg : "최상위 폴더랑 같은 레벨에 생성 할 수 없습니다.",
					width : 400
				}, function() {
					if (this.key == "ok") {
						$(".context-menu-list").hide();
					}
				})
				return false;
			}

			node.editCreateNode("after", {
				title : "새 폴더",
				folder : true
			})
		}

		// 하위 레벨
		$.fn.createFolderAfter = function(node) {
			node.editCreateNode("child", {
				title : "새 폴더",
				folder : true
			})
		}

		$.fn.deleteFolder = function(node) {
			var box = $(document).setNonOpen();
			var type = node.type;
			var children = node.children;

			if (children != null) {
				dialogs.alert({
					theme : "alert",
					title : "경고",
					msg : "하위 폴더가 존재 합니다."
				})
				return false;
			}

			if (type == "root") {
				dialogs.alert({
					theme : "alert",
					title : "경고",
					msg : "최상위 폴더는 삭제 할 수 없습니다."
				})
				return false;
			}

			var url = "/Windchill/plm/common/deleteFolderAction";
			var params = new Object();
			// 결재??
			params.oid = node.data.id;
			$(document).ajaxCallServer(url, params, function(data) {

				if (data.result == "SUCCESS") {
					node.remove();
					mask.close();
				} else if (data.result == "FAIL") {
					dialogs.alert({
						theme : "alert",
						title : "경고",
						msg : data.msg,
						width : 400
					})
				}
			}, false);
		}
	})
</script> 
	<div id="tree"></div>
</td>