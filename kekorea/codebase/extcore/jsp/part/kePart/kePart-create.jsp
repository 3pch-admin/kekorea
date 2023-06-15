<%@page import="java.util.HashMap"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=1010"></script>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				KE 부품 등록
			</div>
		</td>
		<td class="right">
			<input type="button" value="저장" title="저장" onclick="save();">
			<input type="button" value="닫기" title="닫기" class="orange" onclick="self.close();">
			<input type="button" value="행 추가" title="행 추가" class="blue" onclick="addRow();">
			<input type="button" value="행 삭제" title="행 삭제" class="red" onclick="deleteRow();">
		</td>
	</tr>
</table>

<div id="grid_wrap" style="height: 590px; border-top: 1px solid #3180c3;"></div>
<%@include file="/extcore/jsp/common/aui/aui-context.jsp"%>
<script type="text/javascript">
	let myGridID;
	const list = [ "사용", "폐기" ];
	function _layout() {
		return [ {
			dataField : "lotNo",
			headerText : "LOT",
			dataType : "numeric",
			width : 80,
			formatString : "###0",
			editRenderer : {
				type : "InputEditRenderer",
				onlyNumeric : true,
				maxlength : 4,
			},
			filter : {
				showIcon : true,
				inline : true,
				displayFormatValues : true
			},
		}, {
			dataField : "code",
			headerText : "중간코드",
			dataType : "string",
			width : 100,
			editRenderer : {
				type : "InputEditRenderer",
				regExp : "^[a-zA-Z0-9]+$",
				autoUpperCase : true,
				maxlength : 10,
			},
			filter : {
				showIcon : true,
				inline : true,
			},
		}, {
			dataField : "keNumber",
			headerText : "부품번호",
			dataType : "string",
			width : 100,
			editRenderer : {
				type : "InputEditRenderer",
				regExp : "^[a-zA-Z0-9]+$",
				autoUpperCase : true,
				maxlength : 10,
			},
			filter : {
				showIcon : true,
				inline : true,
			},
		}, {
			dataField : "name",
			headerText : "부품명",
			dataType : "string",
			width : 200,
			editRenderer : {
				type : "InputEditRenderer",
				autoUpperCase : true
			},
			filter : {
				showIcon : true,
				inline : true,
			},
		}, {
			dataField : "model",
			headerText : "KokusaiModel",
			dataType : "string",
			width : 300,
			editRenderer : {
				type : "InputEditRenderer",
				autoUpperCase : true
			},
			filter : {
				showIcon : true,
				inline : true,
			},
		}, {
			dataField : "version",
			headerText : "버전",
			dataType : "numeric",
			formatString : "###0",
			width : 80,
			editable : false,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "latest",
			headerText : "최신버전",
			dataType : "string",
			width : 80,
			renderer : {
				type : "CheckBoxEditRenderer",
				edtiable : false,
			},
			filter : {
				showIcon : false,
				inline : false
			},
		}, {
			dataField : "state",
			headerText : "상태",
			dataType : "string",
			width : 100,
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
				validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
					let isValid = false;
					for (let i = 0, len = list.length; i < len; i++) {
						if (list[i] == newValue) {
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
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "creator",
			headerText : "등록자",
			dataType : "string",
			width : 100,
			editable : false,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "createdDate",
			headerText : "등록일",
			dataType : "date",
			formatString : "yyyy-mm-dd",
			width : 100,
			editable : false,
			filter : {
				showIcon : true,
				inline : true,
				displayFormatValues : true
			},
		}, {
			dataField : "modifier",
			headerText : "수정자",
			dataType : "string",
			width : 100,
			editable : false,
			filter : {
				showIcon : true
			}
		}, {
			dataField : "modifiedDate",
			headerText : "수정일",
			dataType : "date",
			formatString : "yyyy-mm-dd",
			width : 100,
			editable : false,
			filter : {
				showIcon : true,
				inline : true,
				displayFormatValues : true
			},
		}, {
			dataField : "primary",
			headerText : "첨부파일",
			dataType : "string",
			width : 100,
			renderer : {
				type : "TemplateRenderer",
			},
			filter : {
				showIcon : false,
				inline : false
			},
		}, {
			dataField : "button",
			headerText : "",
			width : 100,
			editable : false,
			renderer : {
				type : "ButtonRenderer",
				labelText : "파일선택",
				onclick : function(rowIndex, columnIndex, value, item) {
					recentGridItem = item;
					const _$uid = item._$uid;
					const url = getCallUrl("/aui/primary?oid=" + _$uid + "&method=attach");
					popup(url, 1000, 300);
				}
			},
			filter : {
				showIcon : false,
				inline : false
			},
		}, {
			dataField : "isNew",
			dataType : "boolean",
			visible : false
		} ]
	}

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			showRowCheckColumn : true,
			showStateColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableFilter : true,
			selectionMode : "multipleCells",
			enableMovingColumn : true,
			showInlineFilter : true,
			useContextMenu : true,
			enableRightDownFocus : true,
			filterLayerWidth : 320,
			filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
			editable : true
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		AUIGrid.bind(myGridID, "pasteEnd", auiPasteEnd);
		AUIGrid.bind(myGridID, "keyDown", auiKeyDownHandler);
		auiReadyHandler();
	}

	// enter 키 행 추가
	function auiKeyDownHandler(event) {
		if (event.keyCode == 13) { // 엔터 키
			var selectedItems = AUIGrid.getSelectedItems(event.pid);
			var rowIndex = selectedItems[0].rowIndex;
			if (rowIndex === AUIGrid.getRowCount(event.pid) - 1) { // 마지막 행인지 여부 
				const item = {
					latest : true,
					state : "사용",
					version : 1
				};
				AUIGrid.addRow(event.pid, item); // 행 추가
				return false; // 엔터 키의 기본 행위 안함.
			}
		}
		return true; // 기본 행위 유지
	}

	function auiReadyHandler() {
		const item = {
			latest : true,
			state : "사용",
			version : 1
		};
		AUIGrid.addRow(myGridID, item, "first");
	}

	function addRow() {
		const item = {
			latest : true,
			state : "사용",
			version : 1
		};
		AUIGrid.addRow(myGridID, item, "first");
	}

	function deleteRow() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
		const sessionId = document.getElementById("sessionId").value;
		for (let i = checkedItems.length - 1; i >= 0; i--) {
			const item = checkedItems[i].item;
			const rowIndex = checkedItems[i].rowIndex;
			if ((!isNull(item.creatorId) && !checker(sessionId, item.creatorId))) {
				// 					if ((!isNull(item.creatorId) && !checker(sessionId, item.creatorId)) || (!isNull(item.modifierId) && !checker(sessionId, item.modifierId))) {
				alert(rowIndex + "행 데이터의 작성자 혹은 수정자가 아닙니다.");
				return false;
			}
			AUIGrid.removeRow(myGridID, rowIndex);
		}
	}

	function attach(data) {
		const template = "<img src='" + data.icon + "' style='position: relative; top: 2px;'>";
		AUIGrid.updateRowsById(myGridID, {
			_$uid : recentGridItem._$uid,
			primary : template,
			cacheId : data.cacheId
		});
	}

	function save() {
		const url = getCallUrl("/kePart/save");
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
			const rowIndex = AUIGrid.rowIdToIndex(myGridID, item._$uid);
			if (isNull(item.lotNo) || item.lotNo === 0) {
				AUIGrid.showToastMessage(myGridID, rowIndex, 0, "LOT NO의 값은 0혹은 공백을 입력 할 수 없습니다.");
				return false;
			}

			if (isNull(item.code)) {
				AUIGrid.showToastMessage(myGridID, rowIndex, 1, "중간코드의 값은 공백을 입력 할 수 없습니다.");
				return false;
			}

			if (isNull(item.keNumber)) {
				AUIGrid.showToastMessage(myGridID, i, 2, "부품번호의 값은 공백을 입력 할 수 없습니다.");
				return false;
			}

			if (isNull(item.name)) {
				AUIGrid.showToastMessage(myGridID, rowIndex, 3, "부품명 값은 공백을 입력 할 수 없습니다.");
				return false;
			}

			if (isNull(item.model)) {
				AUIGrid.showToastMessage(myGridID, rowIndex, 4, "KokusaiModel 값은 공백을 입력 할 수 없습니다.");
				return false;
			}
		}

		for (let i = 0; i < editRows.length; i++) {
			const item = editRows[i];
			const rowIndex = AUIGrid.rowIdToIndex(myGridID, item._$uid);
			if (isNull(item.lotNo) || item.lotNo === 0) {
				AUIGrid.showToastMessage(myGridID, rowIndex, 0, "LOT NO의 값은 0혹은 공백을 입력 할 수 없습니다.");
				return false;
			}

			if (isNull(item.code)) {
				AUIGrid.showToastMessage(myGridID, rowIndex, 1, "중간코드의 값은 공백을 입력 할 수 없습니다.");
				return false;
			}

			if (isNull(item.keNumber)) {
				AUIGrid.showToastMessage(myGridID, rowIndex, 2, "부품번호의 값은 공백을 입력 할 수 없습니다.");
				return false;
			}

			if (isNull(item.name)) {
				AUIGrid.showToastMessage(myGridID, rowIndex, 3, "부품명 값은 공백을 입력 할 수 없습니다.");
				return false;
			}

			if (isNull(item.model)) {
				AUIGrid.showToastMessage(myGridID, rowIndex, 4, "KokusaiModel 값은 공백을 입력 할 수 없습니다.");
				return false;
			}
		}

		if (!confirm("저장 하시겠습니까?")) {
			return false;
		}

		params.addRows = addRows;
		params.removeRows = removeRows;
		params.editRows = editRows;
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				closeLayer();
			}
		});
	}

	function auiPasteEnd(event) {
		const clipboardData = event.clipboardData;
		for (let i = 0; i < clipboardData.length; i++) {
			AUIGrid.setCellValue(myGridID, i, "latest", true);
			AUIGrid.setCellValue(myGridID, i, "state", "사용");
			AUIGrid.setCellValue(myGridID, i, "version", 1);
			AUIGrid.setCellValue(myGridID, i, "isNew", true);
		}
	}

	document.addEventListener("DOMContentLoaded", function() {
		const columns = loadColumnLayout("kePart-create");
		const contenxtHeader = genColumnHtml(columns);
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>