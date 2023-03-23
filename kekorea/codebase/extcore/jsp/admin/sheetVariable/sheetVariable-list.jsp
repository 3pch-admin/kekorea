<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/include/css.jsp"%>
<%@include file="/extcore/include/script.jsp"%>
<%@include file="/extcore/include/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js"></script>
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

		<div id="grid_wrap" style="height: 705px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			const list = [ {
				"key" : "CATEGORY",
				"value" : "카테고리"
			}, {
				"key" : "CATEGORY_ITEM",
				"value" : "아이템"
			}, {
				"key" : "CATEGORY_SPEC",
				"value" : "사양"
			} ];

			const columns = [ {
				dataField : "name",
				headerText : "코드 명",
				dataType : "string",
				width : 350,
				style : "aui-left",
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "code",
				headerText : "코드",
				dataType : "string",
				width : 130,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "codeType",
				headerText : "코드타입",
				dataType : "string",
				width : 120,
				editable : false,
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
				width : 100,
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
				width : 100,
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
				filter : {
					showIcon : true,
					inline : true
				},
			} ]

			// AUIGrid 생성 함수
			function createAUIGrid(columnLayout) {
				const props = {
					rowIdField : "oid",
					headerHeight : 30,
					rowHeight : 30,
					showRowNumColumn : true,
					showRowCheckColumn : true,
					showStateColumn : true,
					rowNumHeaderText : "번호",
					noDataMessage : "검색 결과가 없습니다.",
					enableFilter : true,
					selectionMode : "multipleCells",
					showInlineFilter : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
					displayTreeOpen : true,
					editable : true
				};
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				loadGridData();
				AUIGrid.bind(myGridID, "addRowFinish", auiAddRowFinish);
				AUIGrid.bind(myGridID, "cellEditBegin", auiCellEditBegin);
				AUIGrid.bind(myGridID, "cellEditEndBefore", auiCellEditEndBefore);
// 				AUIGrid.bind(myGridID, "ready", auiReadyHandler);
			}
			
// 			function auiReadyHandler() {
// 				const data = AUIGrid.getTreeGridData(myGridID);
// 				for(let i=0; i<data.length; i++) {
// 					const item = data[i];
// 					console.log(item);
// 					if(item.codeType === "ITEM") {
// 						AUIGrid.expandItemByRowId(myGridID, item.oid, false);
// 					}
// 				}
// 			}

			function auiCellEditEndBefore(event) {
				const dataField = event.dataField;
				const value = event.value;
				if (dataField === "code") {
					const isUnique = AUIGrid.isUniqueValue(myGridID, "code", value);
					const editValue = AUIGrid.getInitCellValue(myGridID, event.item.oid, "code");
					if (!isUnique && value !== editValue) {
						alert("입력하신 코드는 이미 존재합니다.");
						return "";
					}
				}
				return value;
			}

			function save() {

				if (!confirm("저장 하시겠습니까?")) {
					return false;
				}

				const params = new Object();
				const url = getCallUrl("/sheetVariable/save");
				const addRows = AUIGrid.getAddedRowItems(myGridID);
				const removeRows = AUIGrid.getRemovedItems(myGridID);
				const editRows = AUIGrid.getEditedRowItems(myGridID);

				for (let i = 0; i < addRows.length; i++) {
					const item = addRows[i];
					const rowIndex = AUIGrid.rowIdToIndex(myGridID, item.oid);
					if (isNull(item.name)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 0, "코드 명 값은 공백을 입력 할 수 없습니다.");
						return false;
					}
					if (isNull(item.code)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 1, "코드 값은 공백을 입력 할 수 없습니다.");
						return false;
					}
				}

				for (let i = 0; i < editRows.length; i++) {
					const item = editRows[i];
					const rowIndex = AUIGrid.rowIdToIndex(myGridID, item.oid);
					if (isNull(item.name)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 0, "코드 명 값은 공백을 입력 할 수 없습니다.");
						return false;
					}
					if (isNull(item.code)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 1, "코드 값은 공백을 입력 할 수 없습니다.");
						return false;
					}
				}

				params.addRows = addRows;
				params.removeRows = removeRows;
				params.editRows = editRows;
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
						"codeType" : "CATEGORY"
					};
					AUIGrid.updateRow(myGridID, item, "selectedIndex");
				}

				if (depth == 3) {
					const item = {
						"codeType" : "CATEGORY_ITEM"
					};
					AUIGrid.updateRow(myGridID, item, "selectedIndex");
				}

				if (depth == 4) {
					const item = {
						"codeType" : "CATEGORY_SPEC"
					};
					AUIGrid.updateRow(myGridID, item, "selectedIndex");
				}

				if (depth > 4 || depth === undefined) {
					AUIGrid.removeRow(myGridID, "selectedIndex");
				}

				const selected = AUIGrid.getSelectedIndex(myGridID);
				if (selected.length <= 0) {
					return;
				}

				const rowIndex = selected[0];
				const colIndex = AUIGrid.getColumnIndexByDataField(myGridID, "name");
				AUIGrid.setSelectionByIndex(myGridID, rowIndex, colIndex);
				AUIGrid.openInputer(myGridID);
			}

			function loadGridData() {
				const params = new Object();
				const url = getCallUrl("/sheetVariable/list");
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
				newItem.parentRowId = parentRowId; // 부모의 rowId 값을 보관해 놓음...나중에 개발자가 유용하게 쓰기 위함...실제 그리드는 사용하지 않음.
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