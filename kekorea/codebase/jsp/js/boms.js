/**
 * BOM 관련 Javscript
 */

var CLIPBOARD = null;

var boms = {

	deleteBomPartUrl : "/Windchill/plm/part/deleteBomPartAction",

	setDndUrlAction : "/Windchill/plm/part/setDndUrlAction",

	setIndentUrlAction : "/Windchill/plm/part/setIndentUrlAction",

	setOutdentUrlAction : "/Windchill/plm/part/setOutdentUrlAction",

	bomStart : function(gridKey, oid) {
		boms.bomGrid.init(gridKey, oid);
	},

	bomGrid : {
		init : function(gridKey, oid) {

			$("#" + gridKey).fancytree(
					{
						checkbox : true,
						checkboxAutoHide : false,
						dblclick : function(e, data) {
						},

						dnd5 : {
							preventVoidMoves : true, // Prevent moving nodes
							preventRecursion : true, // Prevent dropping
							preventSameParent : true, // Prevent dropping
							autoExpandMS : 1000,
							multiSource : true, // drag all selected nodes (plus
							dragStart : function(node, data) {
								data.effectAllowed = "all";
								data.dropEffect = data.dropEffectSuggested; // "link";
								return true;
							},
							dragEnter : function(node, data) {
								if (node.parent.key == "root_1") {
									return false;
								}
								return true;
							},
							dragOver : function(node, data) {
								data.dropEffect = data.dropEffectSuggested; // "link";
								return true;
							},
							dragEnd : function(node, data) {
								// data.node.info("dragEnd", data);
							},
							dragDrop : function(node, data) {
								var tree = $("#bomView").fancytree("getTree");
								var rootNode = tree.getRootNode();
								var sourceNodes = data.otherNodeList;
								var copyMode = data.dropEffect !== "move";
								var otherNode = data.otherNode;
								var org = otherNode.getParent();

								if (data.hitMode === "after") {
									sourceNodes.reverse();
								}

								if (false) {
									$.each(sourceNodes, function(i, o) {
										o.info("copy to " + node + ": " + data.hitMode);
										o.copyTo(node, data.hitMode, function(n) {
											delete n.key;
											n.selected = false;
											n.title = "Copy of " + n.title;
										});
									});
								} else {
									$.each(sourceNodes, function(i, o) {
										o.info("move to " + node + ": " + data.hitMode);
										o.moveTo(node, data.hitMode);
									});
								}
								// node.setExpanded();
								node.render();
								var arr = rootNode.getChildrensId(true, new Array());
								boms.setDnd(org, sourceNodes, arr);
							}
						},

						init : function(e, data) {
							$("span.fancytree-checkbox").addClass("sed");
						},

						collapse : function(e, data) {
						},

						expand : function(e, data) {
						},
						click : function(e, data) {
						},
						gridnav : {
							autofocusInput : false,
							handleCursorKeys : true
						},
						extensions : [ "table", "edit", "dnd5", "multi", "childcounter" ],
						persist : {

						},
						childcounter : {
							deep : true,
							hideZeros : true,
							hideExpanded : true
						},
						loadChildren : function(event, data) {
							var children = data.node.getChildren();

							for (var i = 0; i < children.length; i++) {
								// if (!children[i].isFolder()) {
								children[i].data.icon = "/Windchill/jsp/images/edit.png";
								// children[i].renderTitle();
								children[i].render();
								// }
							}
							data.node.updateCounters();
						},
						// icon : function(e, data) {
						// if (data.node.isFolder()) {
						// // return "my-part-icon-class";
						// }
						// },
						table : {
							nodeColumnIdx : 2,
							checkboxColumnIdx : 0
						},
						select : function(event, data) {
							// Get a list of all selected nodes, and convert to
							// a key
							// array:
							var selKeys = $.map(data.tree.getSelectedNodes(), function(node) {
								return node.data.id;
							});
							$("#items").val(selKeys.join(", "));
						},

						renderColumns : function(event, data) {
							var node = data.node;
							$tdList = $(node.tr).find(">td");

							$tdList.eq(1).text(node.getIndexHier());

							if (node.data.thumnail == "") {
								$tdList.eq(3).html("");
							} else {
								$tdList.eq(3).html(
										"<img data-url=\"/Windchill/plm/common/doPublisher?oid=" + node.data.id + "\" class=\"doPublisher\" title=\"뷰어 파일 생성\" src=\"" + node.data.thumnail + "\">");
							}

							if (node.data.minus) {
								$tdList.eq(4).text("-" + node.data.qty);
							} else {
								$tdList.eq(4).text(node.data.qty);
							}

							// $tdList.eq(4).text(node.data.minus);

							$tdList.eq(5).text(node.data.version);
							$("span.fancytree-checkbox").addClass("sed");
						},
						source : $.ajax({
							url : "/Windchill/plm/part/getBomData?oid=" + oid,
							type : "POST"
						})
					}).on("nodeCommand", function(event, data) {
				var refNode, moveMode;
				var tree = $(this).fancytree("getTree");
				var node = tree.getActiveNode();
				var rootNode = tree.getRootNode();
				switch (data.cmd) {
				case "checkin":
					var oid = node.data.id;
					var root = $("input[name=oid]").val();
					boms.checkinBomPart(root, oid);
					break;
				case "checkout":
					var oid = node.data.id;
					var root = $("input[name=oid]").val();
					boms.checkoutBomPart(root, oid);
					tree.clear();
					tree.reload();
					break;
				case "undocheckout":
					var oid = node.data.id;
					var root = $("input[name=oid]").val();
					boms.undocheckoutBomPart(root, oid);
					break;
				case "expands":
					tree.expandAll();
					break;
				case "collapse":
					tree.expandAll(false);
					break;
				case "moveUp":
					refNode = node.getPrevSibling();
					if (refNode) {
						node.moveTo(refNode, "before");
						node.setActive();
					}
					break;
				case "moveDown":
					refNode = node.getNextSibling();

					if (refNode == null) {
					}

					if (refNode) {
						node.moveTo(refNode, "after");
						node.setActive();
					}
					break;
				case "outdent":
					refNode = node.getPrevSibling();
					if (refNode) {
						node.moveTo(refNode, "child");
						refNode.setExpanded();
						node.setActive();

						var rootNode = tree.getRootNode();
						var arr = rootNode.getChildrensId(true, new Array());

						boms.setIndent(node, refNode, arr);
					}
					break;
				case "indent":
					if (!node.isTopLevel()) {
						var orgParent = node.getParent();
						node.moveTo(node.getParent(), "after");
						node.setActive();
						var rootNode = tree.getRootNode();
						var arr = rootNode.getChildrensId(true, new Array());
						boms.setOutdent(node, orgParent, arr);
					}
					break;
				case "cut":
					CLIPBOARD = {
						mode : data.cmd,
						data : node
					};
					// node.remove();
					break;
				case "copy":
					CLIPBOARD = {
						mode : data.cmd,
						data : node.toDict(function(n) {
							delete n.key;
						}),
					};
					break;
				case "paste":
					if (CLIPBOARD.mode === "cut") {
						CLIPBOARD.data.moveTo(node, "child");
						CLIPBOARD.data.setActive();
					} else if (CLIPBOARD.mode === "copy") {
						node.addChildren(CLIPBOARD.data).setActive();
					}
					break;
				default:
					alert("Unhandled command: " + data.cmd);
					return;
				}
			});
		}
	},

	setOutdent : function(org, orgParent, treeList) {
		// 원본
		var url = this.setOutdentUrlAction;
		mask.open();
		$("#loading_layer").show();

		// 새롭게 붙어야할 부모
		var poid = org.parent.data.id;

		var params = new Object();
		params.root = $("input[name=oid]").val();
		// 원본
		params.org = org.data.id;
		params.poid = poid;
		params.orgParent = orgParent.data.id;
		params.treeList = treeList;
		$(document).ajaxCallServer(url, params, function(data) {
			mask.close();
			$("#loading_layer").hide();
		}, false);
	},

	setIndent : function(org, refNode, treeList) {
		// 참조 되는 부품의 부모..
		var poid = refNode.parent.data.id; // poid
		// org 원본
		// refNode 부모?
		var url = this.setIndentUrlAction;
		mask.open();
		$("#loading_layer").show();

		var params = new Object();
		params.root = $("input[name=oid]").val();
		params.org = org.data.id;
		params.poid = poid;
		params.refId = refNode.data.id;
		params.treeList = treeList;
		$(document).ajaxCallServer(url, params, function(data) {
			mask.close();
			$("#loading_layer").hide();
		}, false);
	},

	setDnd : function(org, sourceNodes, treeList) {

		var url = this.setDndUrlAction;
		mask.open();
		$("#loading_layer").show();

		var arr = new Array();
		for (var i = 0; i < sourceNodes.length; i++) {
			var oid = sourceNodes[i].data.id;
			var poid = sourceNodes[i].parent.data.id;
			arr.push(oid + "&" + poid);
		}

		var params = new Object();
		params.root = $("input[name=oid]").val();
		params.org = org.data.id;
		params.list = arr;
		params.treeList = treeList;
		$(document).ajaxCallServer(url, params, function(data) {
			mask.close();
			$("#loading_layer").hide();
		}, false);
	},

	deleteParts : function(root, poid, oid) {
		var url = this.deleteBomPartUrl;

		mask.open();
		$("#loading_layer").show();

		var params = new Object();
		params.poid = poid;
		params.oid = oid;
		params.root = root;

		$(document).ajaxCallServer(url, params, function(data) {
			document.location.href = data.url;
			mask.close();
			$("#loading_layer").hide();
		}, false);
	},

	openAddPopup : function(poid) {
		var dbl = $(this).data("dbl");
		if (dbl == undefined) {
			dbl = "true";
		}

		var state = $(this).data("state");
		if (state == undefined) {
			state = "INWORK";
		}

		var fun = $(this).data("fun");
		if (fun == undefined) {
			fun = "insertOldParts";
		}

		var context = $(this).data("context");
		if (context == undefined) {
			context = "product";
		}

		var url = "/Windchill/plm/part/addPart?context=" + context + "&dbl=" + dbl + "&fun=" + fun + "&state=" + state + "&poid=" + poid;
		$(document).openURLViewOpt(url, 1200, 600, "");
	},

	addBomsAction : function(poid) {
		var dialogs = $(document).setOpen();
		$len = $("input[name=oid]").length;
		if ($len == 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "추가할 부품을 먼저 검색하세요."
			})
			return false;
		}

		var isSelect = $(document).isSelect();
		if (isSelect == false) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "추가할 부품을 선택하세요"
			})
			return false;
		}

		var url = "/Windchill/plm/part/insertBomPartAction";
		var params = $(document).getListParams();
		params.poid = poid;
		params.root = $("input[name=oid]", opener.document).val();
		mask.open();
		$("#loading_layer").show();

		$(document).ajaxCallServer(url, params, function(data) {
			opener.document.location.href = data.url;
			mask.close();
			$("#loading_layer").hide();
		}, false);
	},

	insertOldParts : function(obj, value, poid) {

		$all = $("input[name=all]");
		if ($all.prop("checked") == true) {
			$all.prop("checked", false);
			$all.next().removeClass("sed");
		}

		$oid = $("input[name=oid]");
		$.each($oid, function(idx) {
			$oid.eq(idx).parent().parent().css("background-color", "white");
			$oid.eq(idx).prop("checked", false);
			$oid.eq(idx).next().removeClass("sed");
		})

		if ($(obj).find("input[name=oid]").prop("checked") == false) {
			$(obj).css("background-color", "#fbfed1");
			$(obj).find("input[name=oid]").prop("checked", true);
			$(obj).find("div").addClass("sed");
		}

		var url = "/Windchill/plm/part/insertBomPartAction";
		var params = $(document).getDblFromData(value);
		params.poid = poid;
		params.root = $("input[name=oid]", opener.document).val();
		mask.open();
		$("#loading_layer").show();

		$(document).ajaxCallServer(url, params, function(data) {
			opener.document.location.href = data.url;
			mask.close();
			$("#loading_layer").hide();
		}, false);
	},

	checkinBomPart : function(root, oid) {
		var url = "/Windchill/plm/part/checkinBomPartAction";
		var params = new Object();
		params.oid = oid;
		params.root = root;
		mask.open();
		$("#loading_layer").show();

		$(document).ajaxCallServer(url, params, function(data) {
			document.location.href = data.url;
		}, false);
	},

	checkoutBomPart : function(root, oid) {
		var url = "/Windchill/plm/part/checkoutBomPartAction";
		var params = new Object();
		params.oid = oid;
		params.root = root;
		mask.open();
		$("#loading_layer").show();

		$(document).ajaxCallServer(url, params, function(data) {
			// document.location.href = data.url;
			mask.close();
		}, true);
	},

	undocheckoutBomPart : function(root, oid) {
		var url = "/Windchill/plm/part/undocheckoutBomPartAction";
		var params = new Object();
		params.oid = oid;
		params.root = root;
		mask.open();
		$("#loading_layer").show();

		$(document).ajaxCallServer(url, params, function(data) {
			document.location.href = data.url;
		}, false);
	}
}

$(document).ready(function() {
	// }
	// },
	// callback : function(itemKey, opt) {
	// var node = $.ui.fancytree.getNode(opt.$trigger);
	// var root = $("input[name=oid]").val();
	// if (itemKey == "deleteBomParts") {
	// var poid = node.parent.data.id;
	// if (poid == undefined) {
	// return;
	// }
	// var oid = node.data.id;
	// boms.deleteParts(root, poid, oid);
	// } else if (itemKey == "insertOldPart") {
	// var poid = node.data.id;
	// boms.openAddPopup(poid);
	// } else if (itemKey == "checkin") {
	// var oid = node.data.id;
	// boms.checkinBomPart(root, oid);
	// } else if (itemKey == "checkout") {
	// var oid = node.data.id;
	// boms.checkoutBomPart(root, oid);
	// } else if (itemKey == "undocheckout") {
	// var oid = node.data.id;
	// boms.undocheckoutBomPart(root, oid);
	// }
	// }
	// });

	$("#bomView").contextmenu({
		delegate : "span.fancytree-node",
		menu : [ {
			title : "전체확장",
			cmd : "expands",
			uiIcon : "ui-icon-expand",
		}, {
			title : "전체축소",
			cmd : "collapse",
			uiIcon : "ui-icon-collapse",
		}, {
			title : "----"
		}, {
			title : "삽입",
			uiIcon : "ui-icon-adds",
			children : [ {
				title : "신규부품추가",
				cmd : "sub1",
				uiIcon : "ui-icon-newpart",
			}, {
				title : "기존부품추가",
				cmd : "sub1",
				uiIcon : "ui-icon-oldpart",
			} ]
		}, {
			title : "구조변경",
			uiIcon : "ui-icon-structure",
			children : [ {
				title : "위로",
				cmd : "moveUp",
				uiIcon : "ui-icon-upPart",
			}, {
				title : "아래로",
				cmd : "moveDown",
				uiIcon : "ui-icon-downPart",
			}, {
				title : "오른쪽",
				cmd : "outdent",
				uiIcon : "ui-icon-outdent",
			}, {
				title : "왼쪽",
				cmd : "indent",
				uiIcon : "ui-icon-indent",
			} ]
		}, {
			title : "제거",
			cmd : "remove",
			uiIcon : "ui-icon-remove",
		}, {
			title : "----"
		}, {
			title : "편집",
			uiIcon : "ui-icon-edits",
			children : [ {
				title : "체크인",
				cmd : "checkin",
				uiIcon : "ui-icon-checkin",
			}, {
				title : "체크아웃",
				cmd : "checkout",
				uiIcon : "ui-icon-checkout",
			}, {
				title : "체크아웃취소",
				cmd : "undocheckout",
				uiIcon : "ui-icon-undocheckout",
			} ]
		}, {
			title : "----"
		}, {
			title : "잘라내기",
			cmd : "cut",
			uiIcon : "ui-icon-cut",
		}, {
			title : "복사하기",
			cmd : "copy",
			uiIcon : "ui-icon-copy",
		}, {
			title : "붙여넣기",
			cmd : "paste",
			uiIcon : "ui-icon-paste",
			disabled : true,
		}, {
			title : "----"
		}, {
			title : "되돌리기",
			cmd : "undo",
			uiIcon : "ui-icon-undo",
		}, {
			title : "이전으로",
			cmd : "redo",
			uiIcon : "ui-icon-redo",
		} ],
		beforeOpen : function(event, ui) {
			var node = $.ui.fancytree.getNode(ui.target);
			$("#bomView").contextmenu("enableEntry", "paste", !!CLIPBOARD);
			node.setActive();
		},
		select : function(event, ui) {
			var that = this;
			// delay the event, so the menu can close and the click event does
			// not interfere with the edit control
			setTimeout(function() {
				$(that).trigger("nodeCommand", {
					cmd : ui.cmd
				});
			}, 100);
		},
	});
})