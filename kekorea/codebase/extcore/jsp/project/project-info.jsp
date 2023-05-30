<%@page import="net.sf.json.JSONArray"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
JSONArray list = (JSONArray) request.getAttribute("list");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
<input type="hidden" name="isAdmin" id="isAdmin" value="<%=isAdmin%>">
<input type="hidden" name="sessionName" id="sessionName" value="<%=sessionUser.getFullName()%>">
<input type="hidden" name="sessionId" id="sessionId" value="<%=sessionUser.getName()%>">
<input type="hidden" name="oid" id="oid" value="<%=oid%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				프로젝트 정보
			</div>
		</td>
		<td class="right">
			<input type="button" value="수정" title="수정" onclick="modify();">
			<%
			if (isAdmin) {
			%>
			<input type="button" value="삭제" title="삭제" class="red" onclick="_delete();">
			<%
			}
			%>
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>

<table>
	<colgroup>
		<col width="450">
		<col width="*">
	</colgroup>
	<tr>
		<td valign="top">
			<div id="grid_wrap" style="height: 890px; border-top: 1px solid #3180c3;"></div>
			<script type="text/javascript">
				let myGridID;
				const list =
			<%=list%>
				const columns = [ {
					dataField : "name",
					headerText : "태스크명",
					dataType : "string",
				}, {
					dataField : "duration",
					headerText : "기간",
					dataType : "numeric",
					formatString : "###0",
					postfix : "일",
					width : 60,
					editRenderer : {
						type : "InputEditRenderer",
						onlyNumeric : true,
						maxlength : 3
					},
				}, {
					dataField : "taskType",
					headerText : "태스크 타입",
					dataType : "string",
					width : 80,
					renderer : {
						type : "IconRenderer",
						iconWidth : 16,
						iconHeight : 16,
						iconPosition : "aisleRight",
						iconTableRef : {
							"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png"
						},
						onClick : function(event) {
							AUIGrid.openInputer(event.pid);
						}
					},
					editRenderer : {
						type : "ComboBoxRenderer",
						autoCompleteMode : true,
						autoEasyMode : true,
						matchFromFirst : false,
						showEditorBtnOver : false,
						list : list,
						keyField : "key",
						valueField : "value",
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							for (let i = 0, len = list.length; i < len; i++) {
								if (list[i]["value"] == newValue) {
									isValid = true;
									break;
								}
							}
							return {
								"validate" : isValid,
								"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
							};
						}
					},
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
						let retStr = "";
						for (let i = 0, len = list.length; i < len; i++) {
							if (list[i]["key"] == value) {
								retStr = list[i]["value"];
								break;
							}
						}
						return retStr == "" ? value : retStr;
					},
				}, {
					dataField : "allocate",
					headerText : "할당율",
					dataType : "numeric",
					formatString : "###0",
					postfix : "%",
					width : 80,
					editRenderer : {
						type : "InputEditRenderer",
						onlyNumeric : true,
					},
				} ]

				function createAUIGrid(columnLayout) {
					const props = {
						headerHeight : 30,
						showRowNumColumn : true,
						rowNumHeaderText : "번호",
						editable : true,
						enableSorting : false,
						selectionMode : "multipleCells",
						displayTreeOpen : true,
						forceTreeView : true,
						useContextMenu : true,
						enableRightDownFocus : true,
						fixedColumnCount : 1,
						editableOnFixedCell : true,
						treeLevelIndent : 24,
						contextMenuItems : [ {
							label : "선택된 행 이전 추가",
							callback : contextItemHandler
						}, {
							label : "선택된 행 이후 추가",
							callback : contextItemHandler
						}, {
							label : "선택된 자식 추가",
							callback : contextItemHandler
						}, {
							label : "선택된 행 삭제",
							callback : contextItemHandler
						}, {
							label : "_$line"
						}, {
							label : "저장",
							callback : contextItemHandler
						} ],
						treeIconFunction : function(rowIndex, isBranch, isOpen, depth, item) {
							return item.treeIcon;
						}
					}
					myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
					readyHandler();
					AUIGrid.bind(myGridID, "selectionChange", auiGridSelectionChangeHandler);
					AUIGrid.bind(myGridID, "cellEditBegin", auiCellEditBegin);
				}

				function auiCellEditBegin(event) {
					const item = event.item;
					const type = item.type;
					const taskType = item.taskType
					if (type === "project") {
						return false;
					}

// 					if (taskType === "NORMAL" || taskType === "T-BOM") {
// 						return false;
// 					}

					return true;
				}

				function contextItemHandler(event) {
					const newItem = new Object();
					const item = event.item;
					const _$depth = item._$depth;
					switch (event.contextIndex) {
					case 0:
						if (_$depth === 1) {
							alert("프로젝트와 같은 레벨에 행 추가는 불가능합니다.");
							return false;
						}
						addRow(item, "selectionUp");
						break;
					case 1:
						if (_$depth === 1) {
							alert("프로젝트와 같은 레벨에 행 추가는 불가능합니다.");
							return false;
						}
						addRow(item, "selectionDown");
						break;
					case 2:
						addTreeRow(item);
						break;
					case 3:
						const selectedItems = AUIGrid.getSelectedItems(myGridID);
						if (_$depth === 1) {
							alert("프로젝트는 삭제가 불가능합니다.");
							return false;
						}
						for (let i = selectedItems.length - 1; i >= 0; i--) {
							const rowIndex = selectedItems[i].rowIndex;
							AUIGrid.removeRow(myGridID, rowIndex);
						}
						break;
					case 5:
						treeSave();
						break;
					}
				}

				function treeSave() {
					if (!confirm("저장 하시겠습니까?")) {
						return false;
					}
					const data = AUIGrid.getTreeGridData(myGridID);
					const json = btoa(unescape(encodeURIComponent(JSON.stringify(data))));
					const removeRows = AUIGrid.getRemovedItems(myGridID);
					const params = new Object();
					const url = getCallUrl("/project/treeSave");
					params.json = json;
					params.removeRows = removeRows;
					openLayer();
					call(url, params, function(data) {
						alert(data.msg);
						if (data.result) {
							readyHandler();
						} else {
							closeLayer();
						}
					})
				}

				function addRow(item, selection) {
					const parentItem = AUIGrid.getParentItemByRowId(myGridID, item._$uid);
					const parentRowId = parentItem._$uid;
					const newItem = new Object();
					newItem.parentRowId = parentRowId;
					newItem.name = "새 태스크";
					newItem.isNew = true;
					newItem.allocate = 0;
					newItem.taskType = "NORMAL";
					AUIGrid.addTreeRow(myGridID, newItem, parentRowId, selection);
				}

				function readyHandler() {
					const oid = document.getElementById("oid").value;
					const url = getCallUrl("/project/load?oid=" + oid);
					openLayer();
					call(url, null, function(data) {
						if (data.result) {
							AUIGrid.setGridData(myGridID, data.list);
							closeLayer();
						}
					}, "GET");
				}

				function addTreeRow(item) {
					const parentRowId = item._$uid;
					const newItem = new Object();
					newItem.parentRowId = parentRowId;
					newItem.name = "새 태스크";
					newItem.isNew = true;
					newItem.allocate = 0;
					newItem.taskType = "NORMAL";
					AUIGrid.addTreeRow(myGridID, newItem, parentRowId, "selectionDown");
				}
			</script>
		</td>
		<td valign="top">
			<iframe src="/Windchill/plm/project/view?oid=<%=oid%>" style="height: 900px;" id="view"></iframe>
		</td>
	</tr>
</table>

<script type="text/javascript">
	function modify() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/project/modify?oid=" + oid);
		popup(url, 1200, 460);
	}

	function auiGridSelectionChangeHandler(event) {
		const dataField = event.primeCell.dataField;
		const item = event.selectedItems[0].item;
		const name = item.name;
		const oid = document.getElementById("oid").value;
		const iframe = document.getElementById("view");
		const taskType = item.taskType;
		if (dataField === "name" && !item.isNew) {
			openLayer();
			if (item.type == "project") {
				iframe.src = "/Windchill/plm/project/view?oid=" + oid;
			} else if (item.type == "task") {
				iframe.src = "/Windchill/plm/project/task?oid=" + oid + "&toid=" + item.oid + "&name=" + name;
			}
		}
	}

	function _delete() {
		if (!confirm("삭제 하시겠습니까?")) {
			return false;
		}
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/project/delete?oid=" + oid);
		openLayer();
		call(url, null, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				closeLayer();
			}
		}, "GET");
	}
	
	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columns);
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>