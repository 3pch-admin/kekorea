<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String codeType = (String) request.getParameter("codeType");
%>
<td valign="top" class="tree_td"><script type="text/javascript">
	$(document).ready(function() {
		$("#codeView").fancytree({
			dblclick : function(e, data) {
				$(document).getColumn();
			},
			init : function(e, data) {

			},
			edit : {
				triggerStart: ["f2"],
				close : function(e, data) {
					
					if(data.save && !data.isNew) {
						var dialogs = $(document).setOpen();
						var node = data.node;
						var orgTitle = data.orgTitle;
						if (node.data.codeType == "ROOT") {
							dialogs.alert({
								theme : "alert",
								title : "경고",
								msg : "최상위 코드명은 수정이 불가능합니다.",
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
						var url = "/Windchill/plm/admin/renameCodeAction";
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
						var dialogs = $(document).setOpen();
						var newNode = data.node;
						var poid = data.node.parent.data.id;
						var depths = data.node.parent.data.depth + 1;
						var url = "/Windchill/plm/admin/createCodeAction";
						var params = new Object();
						params.poid = poid;
						params.codeType = "<%=codeType%>";
						params.text = data.node.title;
						params.depths = depths;
						$(document).ajaxCallServer(url, params, function(data) {
							if(data.result == "FAIL") {
								if(data.reload) {
									document.location.href = data.url;									
								} else {
									dialogs.alert({
										theme : "alert",
										title : "경고",
										msg : data.msg,
										width : 380
									}, function() {
										if(this.key == "ok") {
											newNode.remove();
										}
									})
								}
							} else if(data.result == "SUCCESS") {
								newNode.data.id = data.coid;
								mask.close();
							}
						}, false);
					}
				}
			},
			
			click : function(e, data) {
				var oid = data.node.data.id;
				var name = data.node.parent.title;
				$("#codeName").text(name + " [" + data.node.data.codes + "]");
				$("input[name=name]").val(name);
				$("input[name=code]").val(data.node.data.codes);
				$("input[name=codeOid]").val(oid);
			},
			extensions : [ "edit" ],
			persist : {
				store : "local"
			},
 			focusOnSelect : false,
			icon : function(e, data) {
				if(data.node.isFolder()) {
				}
			},
			source : $.ajax({
				url : "/Windchill/plm/admin/getCodeTree?codeType=<%=codeType%>",
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
				$(document).deleteCode(node);
				pnode.setSelected(false);
				pnode.setActive();
			} else if (key == "alt+w") {
				$(document).createCodeSlib(node);
			} else if (key == "alt+s") {
				$(document).createCodeAfter(node);
			}
		})
		var h = $("#container_td").height();
		$("ul.fancytree-container").css("height", h - 9);
		$.contextMenu({
			selector : "#codeView span.fancytree-title",
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
					$(document).renameCode(node);
				} else if (itemKey == "delete") {
					$(document).deleteCode(node);
				} else if (itemKey == "createAfter") {
					$(document).createCodeAfter(node);
				} else if (itemKey == "createSlib") {
					$(document).createCodeSlib(node);
				}
			}
		});

		$.fn.renameCode = function(node) {
			node.editStart();
		}

		$.fn.createCodeSlib = function(node) {

			if (node == null) {
				return;
			}

			var dialogs = $(document).setOpen();
			if (node.data.codeType == "<%=codeType%>") {
				dialogs.alert({
					theme : "alert",
					title : "경고",
					msg : "최상위 코드랑 같은 레벨에 생성 할 수 없습니다.",
					width : 400
				}, function() {
					if (this.key == "ok") {
						$(".context-menu-list").hide();
					}
				})
				return false;
			}

			node.editCreateNode("after", {
				title : "새 코드",
				folder : true
			})
		}

		// 하위 레벨
		$.fn.createCodeAfter = function(node) {
			node.editCreateNode("child", {
				title : "새 코드",
				folder : true
			})
		}

		$.fn.deleteCode = function(node) {
			var dialogs = $(document).setOpen();
			var box = $(document).setNonOpen();
			var type = node.data.codeType;
			var children = node.children;

			if (children != null) {
				dialogs.alert({
					theme : "alert",
					title : "경고",
					msg : "하위 코드 값이 존재 합니다."
				})
				return false;
			}

			if (type == "ROOT") {
				dialogs.alert({
					theme : "alert",
					title : "경고",
					msg : "최상위 코드는 삭제 할 수 없습니다."
				})
				return false;
			}

			var url = "/Windchill/plm/admin/deleteCodeAction";
			var params = new Object();
			var arr = new Array();
			arr.push(node.data.id);
			params.list = arr;
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
	<div id="codeView"></div></td>