<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>    
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=12"></script>
</head>
<body>
	<form>
		<table class="button-table">
			<tr>
				<td class="left">
					<input type="button" value="저장" title="저장" onclick="save();">
					<input type="button" value="자식 추가" title="자식 추가" class="orange" onclick="addTreeRow();">
					<input type="button" value="행 추가" title="행 추가" class="blue" onclick="addRow();">
					<input type="button" value="행 삭제" title="행 삭제" class="red" onclick="deleteRow();">
				</td>
				<td class="right">
					<input type="button" value="조회" title="조회" onclick="loadGridData();">
				</td>
			</tr>
		</table>

		<div id="grid_wrap" style="height: 790px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			const list = [ {
				key : "SPEC",
				value : "사양"
			}, {
				key : "OPTION",
				value : "옵션"
			} ]

			const columns = [ {
				dataField : "name",
				headerText : "코드 명",
				dataType : "string",
				width : 500,
				style : "aui-left",
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "code",
				headerText : "코드",
				dataType : "string",
				width : 120,
				editRenderer : {
					type : "InputEditRenderer",
					regExp : "^[a-zA-Z0-9-]+$",
					autoUpperCase : true
				},
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "codeType",
				headerText : "코드타입",
				dataType : "string",
				width : 120,
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

						if (fromClipboard) {
							for (let i = 0, len = list.length; i < len; i++) {
								if (list[i]["key"] == newValue) {
									isValid = true;
									break;
								}
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
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "sort",
				headerText : "정렬",
				dataType : "numeric",
				width : 80,
				formatString : "###0",
				editRenderer : {
					type : "InputEditRenderer",
					onlyNumeric : true,
				},
				filter : {
					showIcon : false,
					inline : false
				},
			}, {
				dataField : "enable",
				headerText : "사용여부",
				dataType : "boolean",
				width : 120,
				renderer : {
					type : "CheckBoxEditRenderer",
					editable : true,
					disabledFunction : function(rowIndex, columnIndex, value, isChecked, item, dataField) {
						if (rowIndex != 0) {
							return false;
						}
						return true;
					},
				},
				filter : {
					showIcon : false,
					inline : false
				},
			}, {
				dataField : "description",
				headerText : "설명",
				dataType : "string",
				style : "aui-left",
				filter : {
					showIcon : true,
					inline : true
				},
			} ]

			function createAUIGrid(columnLayout) {
				const props = {
					rowIdField : "oid",
					headerHeight : 30,
					showRowNumColumn : true,
					showRowCheckColumn : true,
					showStateColumn : true,
					rowNumHeaderText : "번호",
					showAutoNoDataMessage : false,
					enableFilter : true,
					selectionMode : "multipleCells",
					showInlineFilter : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
					displayTreeOpen : true,
					editable : true,
					useContextMenu : true,
					forceTreeView : true,
					enableRightDownFocus : true,
					contextMenuItems : [ {
						label : "선택된 행 이전 추가",
						callback : contextItemHandler
					}, {
						label : "선택된 행 이후 추가",
						callback : contextItemHandler
					}, {
						label : "선택된 행 자식 추가",
						callback : contextItemHandler
					}, {
						label : "_$line"
					}, {
						label : "선택된 행 삭제",
						callback : contextItemHandler
					} ],
				};
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				loadGridData();
				AUIGrid.bind(myGridID, "addRowFinish", auiAddRowFinish);
				AUIGrid.bind(myGridID, "cellEditBegin", auiCellEditBegin);
				AUIGrid.bind(myGridID, "ready", auiReadyHandler);
			}

			function auiReadyHandler() {
				AUIGrid.showItemsOnDepth(myGridID, 2);
			}

			function contextItemHandler(event) {
				const _$depth = event.item._$depth;
				const item = {}
				switch (event.contextIndex) {
				case 0:
					if (_$depth === 1) {
						alert("최상위랑 같은 레벨에 행을 추가 할 수 없습니다.");
						return false;
					}
					AUIGrid.addRow(myGridID, item, "selectionUp");
					break;
				case 1:
					if (_$depth === 1) {
						alert("최상위랑 같은 레벨에 행을 추가 할 수 없습니다.");
						return false;
					}
					AUIGrid.addRow(myGridID, item, "selectionDown");
					break;
				case 2:
					const parentRowId = event.item.oid;
					const newItem = new Object();
					newItem.parentRowId = parentRowId;
					newItem.enable = true;
					newItem.sort = 1;
					AUIGrid.addTreeRow(myGridID, newItem, parentRowId, "selectionDown");
					break;
				case 4:
					const selectedItems = AUIGrid.getSelectedItems(myGridID);
					const rows = AUIGrid.getRowCount(myGridID);

					if (_$depth === 1) {
						alert("최상위 행은 삭제를 할 수 없습니다.");
						return false;
					}

					if (rows === 1) {
						alert("최 소 하나의 행이 존재해야합니다.");
						return false;
					}
					for (let i = selectedItems.length - 1; i >= 0; i--) {
						const rowIndex = selectedItems[i].rowIndex;
						AUIGrid.removeRow(myGridID, rowIndex);
					}
					break;
				}
			}

			function save() {
				const url = getCallUrl("/specCode/save");
				const params = new Object();
				const addRows = AUIGrid.getAddedRowItems(myGridID);
				const removeRows = AUIGrid.getRemovedItems(myGridID);
				const editRows = AUIGrid.getEditedRowItems(myGridID);

				if (addRows.length === 0 && removeRows.length === 0 && editRows.length === 0) {
					alert("변경된 내용이 없습니다.");
					return false;
				}

				for (let i = 0; i < addRows.length; i++) {
					const item = addRows[i];
					const rowIndex = AUIGrid.rowIdToIndex(myGridID, item.oid);
					const depth = item._$depth;

					if (depth === 1) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 0, "최상위와 같은 레벨의 데이터 생성은 불가능 합니다.");
						return false;
					}

					if (depth === 2 && item.codeType !== "SPEC") {
						AUIGrid.showToastMessage(myGridID, rowIndex, 2, "사양 코드타입을 선택하세요.");
						return false;
					}

					if (depth === 3 && item.codeType !== "OPTION") {
						AUIGrid.showToastMessage(myGridID, rowIndex, 2, "옵션 코드타입을 선택하세요.");
						return false;
					}

					if (isNull(item.name)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 0, "코드 명 값은 공백을 입력 할 수 없습니다.");
						return false;
					}

					if (isNull(item.code)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 1, "코드 값은 공백을 입력 할 수 없습니다.");
						return false;
					}

					// 					const isUnique = AUIGrid.isUniqueValue(myGridID, "code", item.code);
					// 					if (!isUnique) {
					// 						AUIGrid.showToastMessage(myGridID, rowIndex, 1, "입력하신 코드는 이미 존재합니다.");
					// 						return false;
					// 					}
				}

				for (let i = 0; i < editRows.length; i++) {
					const item = editRows[i];
					const rowIndex = AUIGrid.rowIdToIndex(myGridID, item.oid);
					const depth = item._$depth;

					if (depth === 1) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 0, "최상위와 같은 레벨의 데이터 생성은 불가능 합니다.");
						return false;
					}

					if (depth === 2 && item.codeType !== "SPEC") {
						AUIGrid.showToastMessage(myGridID, rowIndex, 2, "사양 코드타입을 선택하세요.");
						return false;
					}

					if (depth === 3 && item.codeType !== "OPTION") {
						AUIGrid.showToastMessage(myGridID, rowIndex, 2, "옵션 코드타입을 선택하세요.");
						return false;
					}

					if (isNull(item.name)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 0, "코드 명 값은 공백을 입력 할 수 없습니다.");
						return false;
					}
					if (isNull(item.code)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 1, "코드 값은 공백을 입력 할 수 없습니다.");
						return false;
					}

					// 					const isUnique = AUIGrid.isUniqueValue(myGridID, "code", item.code);
					// 					if (!isUnique) {
					// 						AUIGrid.showToastMessage(myGridID, rowIndex, 1, "입력하신 코드는 이미 존재합니다.");
					// 						return false;
					// 					}
				}

				if (!confirm("저장 하시겠습니까?")) {
					return false;
				}
				params.addRows = addRows;
				params.removeRows = removeRows;
				params.editRows = editRows;
				console.log(params);
				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						loadGridData();
					}
				})
			}

			function auiCellEditBegin(event) {
				const dataField = event.dataField;
				const rowIndex = event.rowIndex;
				if (rowIndex == 0) {
					return false;
				}
				return true;
			}

			function auiAddRowFinish(event) {
				const item = event.items[0];
				const depth = item._$depth;
				if (depth == 2) {
					const item = {
						codeType : "SPEC",
					};
					AUIGrid.updateRow(myGridID, item, "selectedIndex");
				}

				if (depth == 3) {
					const item = {
						codeType : "OPTION",
					};
					AUIGrid.updateRow(myGridID, item, "selectedIndex");
				}

				if (depth > 3 || depth === undefined) {
					AUIGrid.removeRow(myGridID, "selectedIndex");
				}

				const selected = AUIGrid.getSelectedIndex(myGridID);
				if (selected.length <= 0) {
					return false;
				}

				const rowIndex = selected[0];
				const colIndex = AUIGrid.getColumnIndexByDataField(myGridID, "name");
				AUIGrid.setSelectionByIndex(myGridID, rowIndex, colIndex);
				AUIGrid.openInputer(myGridID);
			}

			function loadGridData() {
				const params = new Object();
				const url = getCallUrl("/specCode/list");
				AUIGrid.showAjaxLoader(myGridID);
				parent.openLayer();
				call(url, params, function(data) {
					AUIGrid.removeAjaxLoader(myGridID);
					AUIGrid.setGridData(myGridID, data.list);
					parent.closeLayer();
				});
			}

			function addRow() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				if (checkedItems.length <= 0) {
					alert("행을 추가할 행을 선택하세요.");
					return false;
				}

				if (checkedItems.length > 1) {
					alert("하나의 행을 선택하세요.");
					return false;
				}

				const selItem = checkedItems[0].item;
				const parentItem = AUIGrid.getParentItemByRowId(myGridID, selItem.oid);
				const parentRowId = parentItem.oid;

				const newItem = new Object();
				newItem.parentRowId = parentRowId;
				newItem.enable = true;
				newItem.sort = 0;
				AUIGrid.addTreeRow(myGridID, newItem, parentRowId, "last");
			}

			function addTreeRow() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				if (checkedItems.length <= 0) {
					alert("자식행을 추가할 행을 선택하세요.");
					return false;
				}

				if (checkedItems.length > 1) {
					alert("하나의 행을 선택하세요.");
					return false;
				}

				const selItem = checkedItems[0].item;
				const parentRowId = selItem.oid;
				const newItem = new Object();
				newItem.parentRowId = parentRowId;
				newItem.enable = true;
				newItem.sort = 0;
				AUIGrid.addTreeRow(myGridID, newItem, parentRowId, "selectionDown");
			}

			function deleteRow() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				for (let i = checkedItems.length - 1; i >= 0; i--) {
					const rowIndex = checkedItems[i].rowIndex;
					AUIGrid.removeRow(myGridID, rowIndex);
				}
			}

			document.addEventListener("DOMContentLoaded", function() {
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
			});

			document.addEventListener("keydown", function(event) {
				const keyCode = event.keyCode || event.which;
				if (keyCode === 13) {
					loadGridData();
				}
			})

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID);
			});
		</script>
	</form>
</body>
</html>