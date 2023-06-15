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
				KE 도면 등록
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
	let recentGridItem = null;
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
			dataField : "name",
			headerText : "DRAWING TITLE",
			dataType : "string",
			style : "aui-left",
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "keNumber",
			headerText : "DWG NO",
			dataType : "string",
			width : 100,
			editable : false,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "version",
			headerText : "버전",
			dataType : "numeric",
			width : 80,
			editable : false,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "state",
			headerText : "상태",
			dataType : "string",
			width : 80,
			editable : true,
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
			dataField : "latest",
			headerText : "최신버전",
			dataType : "boolean",
			width : 80,
			renderer : {
				type : "CheckBoxEditRenderer"
			},
			editable : false,
			filter : {
				showIcon : false,
				inline : false
			},
		}, {
			dataField : "creator",
			headerText : "작성자",
			dataType : "string",
			width : 100,
			editable : false,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "createdDate_txt",
			headerText : "작성일",
			dataType : "string",
			width : 100,
			editable : false,
			filter : {
				showIcon : true,
				inline : true,
			},
		}, {
			dataField : "modifier",
			headerText : "수정자",
			dataType : "string",
			width : 100,
			editable : false,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "modifiedDate_txt",
			headerText : "수정일",
			dataType : "string",
			width : 100,
			editable : false,
			filter : {
				showIcon : true,
				inline : true,
			},
		}, {
			dataField : "preView",
			headerText : "미리보기",
			width : 80,
			editable : false,
			style : "preView",
			renderer : {
				type : "ImageRenderer",
				altField : null,
				imgHeight : 34,
			},
			filter : {
				showIcon : false,
				inline : false
			},
		}, {
			dataField : "primary",
			headerText : "도면파일",
			dataType : "string",
			width : 80,
			editable : false,
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
			width : 80,
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
			showStateColumn : true,
			showRowCheckColumn : true,
			showRowNumColumn : true,
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
			editable : true,
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		AUIGrid.bind(myGridID, "pasteEnd", auiPasteEnd);
		AUIGrid.bind(myGridID, "keyDown", auiKeyDownHandler);
		auiReadyHandler();
	}

	function auiReadyHandler() {
		const item = {
			latest : true,
			preView : null,
			state : "사용"
		};
		AUIGrid.addRow(myGridID, item, "first");
	}

	function addRow() {
		const item = {
			latest : true,
			preView : null,
			state : "사용"
		};
		AUIGrid.addRow(myGridID, item, "first");
	}

	function deleteRow() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
		const sessionId = document.getElementById("sessionId").value;
		for (let i = checkedItems.length - 1; i >= 0; i--) {
			const item = checkedItems[i].item;
			const rowIndex = checkedItems[i].rowIndex;
			// 					if ((!isNull(item.creatorId) && !checker(sessionId, item.creatorId)) || (!isNull(item.modifierId) && !checker(sessionId, item.modifierId))) {
			if ((!isNull(item.creatorId) && !checker(sessionId, item.creatorId))) {
				alert(rowIndex + "행 데이터의 작성자가 아닙니다.");
				return false;
			}
			if (!item.latest) {
				alert("최신버전의 도면이 아닙니다.");
				return false;
			}
			AUIGrid.removeRow(myGridID, rowIndex);
		}
	}

	function attach(data) {
		const name = data.name;
		// 				if (name.length !== 18) {
		// 					alert("도면파일 이름명을 체크하세요. \nDWG NO : 10자리, 버전 3자리의 양식을 맞춰주세요.");
		// 					return false;
		// 				}
		const start = name.indexOf("-");
		if (start <= -1) {
			alert("도면파일 이름의 양식이 맞지 않습니다.\nDWG NO-버전 형태의 파일명만 허용됩니다.");
			return false;
		}
		const end = name.lastIndexOf(".");
		if (end <= -1) {
			alert("도면파일 확장자를 체크해주세요.");
			return false;
		}
		const ext = name.substring(end + 1);
		if (ext.toLowerCase() !== "pdf") {
			alert("PDF 파일 형식의 도면파일만 허용됩니다.");
			return false;
		}
		const number = name.substring(0, start);
		// 				if (number.length !== 10) {
		// 					alert("도면파일의 DWG NO의 자리수를 확인해주세요. 등록가능한 도번의 자리수는 10자리여야 합니다.");
		// 					return false;
		// 				}
		const version = name.substring(start + 1, end);
		if (version.length !== 3) {
			alert("도면파일의 버전 자리수를 확인해주세요. 등록가능한 버전의 자리수는 3자리여야 합니다.");
			return false;
		}

		const template = "<img src='" + data.icon + "' style='position: relative; top: 2px;'>";
		AUIGrid.updateRowsById(myGridID, {
			_$uid : recentGridItem._$uid,
			keNumber : number,
			version : Number(version),
			file : name,
			primary : template,
			cacheId : data.cacheId
		});
	}

	function save() {
		const url = getCallUrl("/keDrawing/save");
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

			if (isNull(item.name)) {
				AUIGrid.showToastMessage(myGridID, rowIndex, 1, "DRAWING TITLE의 값은 공백을 입력 할 수 없습니다.");
				return false;
			}

			if (isNull(item.primary)) {
				AUIGrid.showToastMessage(myGridID, rowIndex, 9, "도면파일을 선택하세요.");
				return false;
			}
		}

		for (let i = 0; i < editRows.length; i++) {
			const item = editRows[i];
			const rowIndex = AUIGrid.rowIdToIndex(myGridID, item._$uid);
			if (isNull(item.lotNo) || item.lotNo === 0) {
				AUIGrid.showToastMessage(myGridID, rowIndex, 0, "LOT NO의 값은 0을 입력 할 수 없습니다.");
				return false;
			}

			if (isNull(item.name)) {
				AUIGrid.showToastMessage(myGridID, rowIndex, 1, "DRAWING TITLE의 값은 공백을 입력 할 수 없습니다.");
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

	function auiKeyDownHandler(event) {
		if (event.keyCode == 13) { // 엔터 키
			var selectedItems = AUIGrid.getSelectedItems(event.pid);
			var rowIndex = selectedItems[0].rowIndex;
			if (rowIndex === AUIGrid.getRowCount(event.pid) - 1) { // 마지막 행인지 여부 
				const item = {
					latest : true,
					preView : null,
					state : "사용"
				};
				AUIGrid.addRow(event.pid, item); // 행 추가
				return false; // 엔터 키의 기본 행위 안함.
			}
		}
		return true; // 기본 행위 유지
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
		const columns = loadColumnLayout("keDrawing-create");
		const contenxtHeader = genColumnHtml(columns);
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>