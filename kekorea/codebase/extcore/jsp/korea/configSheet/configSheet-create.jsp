<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="e3ps.epm.keDrawing.dto.KeDrawingDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
JSONArray categorys = (JSONArray) request.getAttribute("categorys");
JSONArray baseData = (JSONArray) request.getAttribute("baseData");
String oid = (String) request.getAttribute("oid");
%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
<style type="text/css">
.row1 {
	background-color: #99CCFF;
}

.row2 {
	background-color: #FFCCFF;
}

.row3 {
	background-color: #CCFFCC;
}

.row4 {
	background-color: #FFFFCC;
}

.row5 {
	background-color: #FFCC99;
}

.row6 {
	background-color: #CCCCFF;
}

.row7 {
	background-color: #99FF66;
}

.row8 {
	background-color: #CC99FF;
}

.row9 {
	background-color: #66CCFF;
}

.row10 {
	background-color: #CCFFCC;
}

.row11 {
	background-color: #FFCCFF;
}

.row12 {
	background-color: #FFFFCC;
}

#textAreaWrap {
	font-size: 12px;
	position: absolute;
	height: 100px;
	min-width: 100px;
	background: #fff;
	border: 1px solid #555;
	display: none;
	padding: 4px;
	text-align: right;
	z-index: 9999;
}

#textAreaWrap textarea {
	font-size: 12px;
	width: calc(100% - 6px);
}

.editor_btn {
	background: #ccc;
	border: 1px solid #555;
	cursor: pointer;
	margin: 2px;
	padding: 2px;
}

.nav_u {
	display: inline-block;
}

ul, ol {
	list-style: none;
	padding: 0;
	margin: 0;
}

.nav_u li {
	display: inline;
	white-space: nowrap;
	text-align: right;
}
</style>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				CONFIG SHEET 등록
			</div>
		</td>
		<td class="right">
			<input type="button" value="등록" title="등록" onclick="create();">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>

<div id="tabs">
	<ul>
		<li>
			<a href="#tabs-1">기본정보</a>
		</li>
		<li>
			<a href="#tabs-2">CONFIG SHEET</a>
		</li>
	</ul>
	<div id="tabs-1">
		<table class="create-table">
			<colgroup>
				<col width="200">
				<col width="*">
			</colgroup>
			<tr>
				<th class="req lb">CONFIG SHEET 제목</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-700">
				</td>
			</tr>
			<tr>
				<th class="req lb">KEK 작번</th>
				<td>
					<jsp:include page="/extcore/jsp/common/project-include.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
						<jsp:param value="false" name="multi" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">설명</th>
				<td class="indent5">
					<textarea name="description" id="description" rows="6"></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5">
					<jsp:include page="/extcore/jsp/common/attach-secondary.jsp">
						<jsp:param value="" name="oid" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="req lb">결재</th>
				<td>
					<jsp:include page="/extcore/jsp/common/approval-register.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
					</jsp:include>
				</td>
			</tr>
		</table>
	</div>
	<div id="tabs-2">
		<table class="button-table">
			<tr>
				<td class="left">
					<input type="button" value="불러오기" title="불러오기" class="blue" onclick="load();">
					<input type="button" value="열 추가" title="열 추가" class="red" onclick="toAppend();">
				</td>
			</tr>
		</table>
		<div id="grid_wrap" style="height: 800px; border-top: 1px solid #3180c3;"></div>
	</div>
</div>
<div id="textAreaWrap">
	<textarea id="myTextArea" class="aui-grid-custom-renderer-ext" style="height: 90px;"></textarea>
	<ul class="nav_u">
		<li>
			<button class="editor_btn" id="editEnd">확인</button>
		</li>
		<li>
			<button class="editor_btn" id="cancel">취소</button>
		</li>
	</ul>
</div>
<script type="text/javascript">
	let myGridID;
	const categorys =
<%=categorys%>
	let itemListMap = {};
	let specListMap = {};
	const columns = [ {
		dataField : "category_code",
		headerText : "CATEGORY",
		dataType : "string",
		style : "aui-left",
		width : 250,
		cellMerge : true,
		labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
			let retStr = "";
			for (let i = 0, len = categorys.length; i < len; i++) {
				if (categorys[i]["key"] == value) {
					retStr = categorys[i]["value"];
					break;
				}
			}
			return retStr == "" ? value : retStr;
		},
	}, {
		dataField : "item_code",
		headerText : "ITEM",
		dataType : "string",
		width : 350,
		cellMerge : true,
		style : "aui-left",
		mergeRef : "category_code",
		mergePolicy : "restrict",
		editable : false,
		labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
			let retStr = "";
			const param = item.category_code;
			const dd = itemListMap[param];
			if (dd === undefined) {
				return value;
			}
			for (let i = 0, len = dd.length; i < len; i++) {
				if (dd[i]["key"] == value) {
					retStr = dd[i]["value"];
					break;
				}
			}
			return retStr == "" ? value : retStr;
		},
	}, {
		dataField : "spec",
		headerText : "사양",
		dataType : "string",
		width : 250,
		renderer : {
			type : "templaterenderer"
		}
	}, {
		dataField : "note",
		headerText : "NOTE",
		dataType : "string",
	}, {
		dataField : "apply",
		headerText : "APPLY",
		dataType : "string",
		width : 350
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			selectionMode : "multipleCells",
			enableSorting : false,
			enableCellMerge : true,
			editable : true,
			enableRowCheckShiftKey : true,
			wordWrap : true,
			rowStyleFunction : function(rowIndex, item) {
				const value = item.category_code;
				if (value === "CATEGORY_2") {
					return "row1";
				} else if (value === "CATEGORY_3") {
					return "row2";
				} else if (value === "CATEGORY_4") {
					return "row3";
				} else if (value === "CATEGORY_5") {
					return "row4";
				} else if (value === "CATEGORY_6") {
					return "row5";
				} else if (value === "CATEGORY_7") {
					return "row6";
				} else if (value === "CATEGORY_8" || value === "CATEGORY_9") {
					return "row7";
				} else if (value === "CATEGORY_10") {
					return "row8";
				} else if (value === "CATEGORY_11") {
					return "row9";
				} else if (value === "CATEGORY_12") {
					return "row4";
				} else if (value === "CATEGORY_13") {
					return "row10";
				} else if (value === "CATEGORY_14") {
					return "row11";
				} else if (value === "CATEGORY_15") {
					return "row12";
				}
				return "";
			}
		};
		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		AUIGrid.bind(myGridID, "cellEditEnd", auiCellEditEndHandler);
		AUIGrid.bind(myGridID, "cellEditBegin", auiCellEditBeginHandler);
		readyHandler();
		auiReadyHandler();
		AUIGrid.bind(myGridID, "pasteBegin", auiPasteBeginHandler);
	}

	let toIndex = 0;
	function toAppend() {
		const dataField = "spec" + toIndex;
		var columnObj = {
			headerText : "사양" + toIndex,
			dataField : dataField, // dataField 는 중복되지 않게 설정
			defaultValue : "", // 칼럼 추가 할 때 해당 칼럼의 기본값 지정, 만약 지정하지 않으면 초기화 하지 않음
			width : 250,
			renderer : {
				type : "templaterenderer"
			}
		};
// 		var columnPos = document.getElementById("addSelect").value;
		// columnPos : columnIndex 인 경우 해당 index 에 삽입, "first" : 맨처음, "last" : 맨끝, "selectionLeft" : 선택 열 왼쪽에,  "selectionRight" : 선택 열 오른쪽에 추가
		AUIGrid.addColumn(myGridID, columnObj, "selectionRight");
		toIndex++;
	}

	function auiPasteBeginHandler(event) {
		const data = event.clipboardData;
		let arr;
		let i, j, len, len2, str;
		// 엑셀 개행 문자가 없는 경우
		if (data.indexOf("\n") === -1) {
			return data;
		}

		arr = CSVToArray(data, "\t"); // tab 문자 구성 String 을 배열로 반환
		if (arr && arr.length) {
			if (String(arr[arr.length - 1]).trim() == "") { // 마지막 빈 값이 삽입되는 경우가 존재함.
				arr.pop();
			}
			for (i = 0, len = arr.length; i < len; i++) {
				arr2 = arr[i];
				if (arr2 && arr2.length) {
					for (j = 0, len2 = arr2.length; j < len2; j++) {
						str = arr2[j];
						arr[i][j] = str.replace(/\n/g, "<br/>"); // 엑셀 개행 문자를 br 태그로 변환
					}
				}
			}
		}
		return arr;
	}

	function CSVToArray(strData, strDelimiter) {
		strDelimiter = (strDelimiter || ",");
		const objPattern = new RegExp(("(\\" + strDelimiter + "|\\r?\\n|\\r|^)" + "(?:\"([^\"]*(?:\"\"[^\"]*)*)\"|" + "([^\"\\" + strDelimiter + "\\r\\n]*))"), "gi");
		let arrData = [ [] ];
		let arrMatches = null;
		while (arrMatches = objPattern.exec(strData)) {
			const strMatchedDelimiter = arrMatches[1];
			if (strMatchedDelimiter.length && strMatchedDelimiter !== strDelimiter) {
				arrData.push([]);
			}
			let strMatchedValue;
			if (arrMatches[2]) {
				strMatchedValue = arrMatches[2].replace(new RegExp("\"\"", "g"), "\"");
			} else {
				strMatchedValue = arrMatches[3];
			}
			arrData[arrData.length - 1].push(strMatchedValue);
		}
		return (arrData);
	};

	function load() {
		const url = getCallUrl("/configSheet/copy?method=copy&multi=false");
		popup(url, 1500, 700);
	}

	function copy(data, callBack) {
		const oid = data.item.oid;
		const params = new Object();
		const url = getCallUrl("/configSheet/copy");
		params.oid = oid;
		openLayer();
		call(url, params, function(data) {
			if (data.result && data.list.length > 0) {
				AUIGrid.clearGridData(myGridID);
				AUIGrid.addRow(myGridID, data.list);
				callBack(true, "");
			} else {
				callBack(true, data.msg);
			}
			closeLayer();
		})
	}

	function auiCellEditBeginHandler(event) {
		const dataField = event.dataField;
		if (dataField === "category_code") {
			return false;
		}

		if (event.isClipboard) {
			return true;
		}
		if (event.dataField.indexOf("spec") > -1) {
			openTextarea(event);
		} else {
			return true;
		}

		return true;
	}

	function contextItemHandler(event) {
		const item = new Object();
		switch (event.contextIndex) {
		case 0:
			const selectedItems = AUIGrid.getSelectedItems(myGridID);
			const rows = AUIGrid.getRowCount(myGridID);
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

	function openTextarea(event) {
		const dataField = event.dataField;
		const obj = document.getElementById("textAreaWrap");
		const textArea = document.getElementById("myTextArea");
		obj.style.left = event.position.x + "px";
		obj.style.top = event.position.y + "px";
		obj.style.width = (event.size.width - 8) + "px";
		obj.style.height = "125px";
		obj.style.display = "block";
		textArea.value = String(event.value).replace(/[<]br[/][>]/gi, "\r\n");
		obj.setAttribute("data-field", dataField);
		// 행인덱스 보관
		obj.setAttribute("data-row-index", event.rowIndex);

		// 포커싱
		setTimeout(function() {
			textArea.focus();
			textArea.select();
		}, 16);
	}

	function forceEditngTextArea(value, event) {
		const dataField = document.getElementById("textAreaWrap").getAttribute("data-field"); // 보관한 dataField 얻기
		const rowIndex = Number(document.getElementById("textAreaWrap").getAttribute("data-row-index")); // 보관한 rowIndex 얻기
		value = value.replace(/\r|\n|\r\n/g, "<br/>");

		const item = {};
		item[dataField] = value;

		AUIGrid.updateRow(myGridID, item, rowIndex);
		document.getElementById("textAreaWrap").style.display = "none";
		event.preventDefault();
	};

	document.getElementById("myTextArea").addEventListener("blur", function(event) {
		const relatedTarget = event.relatedTarget || document.activeElement;

		// 확인 버튼 클릭한 경우
		if (relatedTarget.getAttribute("id") === "editEnd") {
			return;
		} else if (relatedTarget.getAttribute("id") === "cancel") { // 취소 버튼
			return;
		}
		forceEditngTextArea(this.value, event);
	});

	function auiReadyHandler(event) {
		const item = AUIGrid.getGridData(myGridID);
		for (let i = 0; i < item.length; i++) {
			if (itemListMap.length === undefined) {
				const categoryCode = item[i].category_code;
				const url = getCallUrl("/configSheetCode/getChildrens?parentCode=" + categoryCode + "&codeType=CATEGORY");
				call(url, null, function(data) {
					itemListMap[categoryCode] = data.list;
				}, "GET");
			}
			// 			if (specListMap.length === undefined) {
			// 				const itemCode = item[i].item_code;
			// 				if (itemCode !== "") {
			// 					const url = getCallUrl("/configSheetCode/getChildrens?parentCode=" + itemCode + "&codeType=CATEGORY_ITEM");
			// 					call(url, null, function(data) {
			// 						specListMap[itemCode] = data.list;
			// 					}, "GET");
			// 				}
			// 			}
		}
	}

	function readyHandler() {
		const data =
<%=baseData%>
	AUIGrid.addRow(myGridID, data);
	}

	function auiCellEditEndHandler(event) {
		const dataField = event.dataField;
		const item = event.item;
		const rowIndex = event.rowIndex;
		if (dataField === "category_code") {
			const categoryCode = item.category_code;
			const url = getCallUrl("/commonCode/getChildrens?parentCode=" + categoryCode + "&codeType=CATEGORY");
			call(url, null, function(data) {
				itemListMap[categoryCode] = data.list;
			}, "GET");
		}

		// 		if (dataField === "item_code") {
		// 			const itemCode = item.item_code;
		// 			const url = getCallUrl("/commonCode/getChildrens?parentCode=" + itemCode + "&codeType=CATEGORY_ITEM");
		// 			call(url, null, function(data) {
		// 				specListMap[itemCode] = data.list;
		// 			}, "GET");
		// 		}
	}

	function deleteRow() {
		const checked = AUIGrid.getCheckedRowItems(myGridID);
		for (let i = checked.length - 1; i >= 0; i--) {
			const rowIndex = checked[i].rowIndex;
			AUIGrid.removeRow(myGridID, rowIndex);
		}
	};

	function create() {
		if (!confirm("등록 하시겠습니까?")) {
			return false;
		}

		const url = getCallUrl("/configSheet/create");
		const params = new Object();
		const addRows = AUIGrid.getGridData(myGridID);
		const addRows9 = AUIGrid.getAddedRowItems(myGridID9);
		const addRows8 = AUIGrid.getAddedRowItems(myGridID8);

		addRows.sort(function(a, b) {
			return a.sort - b.sort;
		});

		params.name = document.getElementById("name").value;
		params.description = document.getElementById("description").value;
		params.addRows = addRows;
		params.addRows9 = addRows9;
		params.secondarys = toArray("secondarys");
		toRegister(params, addRows8);
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				closeLayer();
			}
		})
	}

	document.getElementById("cancel").addEventListener("click", function(event) {
		document.getElementById("textAreaWrap").style.display = "none";
		event.preventDefault();
	});

	document.getElementById("editEnd").addEventListener("click", function(event) {
		const value = document.getElementById("myTextArea").value;
		forceEditngTextArea(value, event);
	});

	document.addEventListener("DOMContentLoaded", function() {
		document.getElementById("name").focus();
		$("#tabs").tabs({
			active : 0,
			activate : function(event, ui) {
				const tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-1":
					const isCreated9 = AUIGrid.isCreated(myGridID9);
					if (isCreated9) {
						AUIGrid.resize(myGridID9);
					} else {
						createAUIGrid9(columns9);
					}
					const isCreated8 = AUIGrid.isCreated(myGridID8);
					if (isCreated8) {
						AUIGrid.resize(myGridID8);
					} else {
						createAUIGrid8(columns8);
					}
					break;
				case "tabs-2":
					const isCreated = AUIGrid.isCreated(myGridID);
					if (isCreated) {
						AUIGrid.resize(myGridID);
					} else {
						createAUIGrid(columns);
						openLayer();
						setTimeout(function() {
							AUIGrid.refresh(myGridID);
							closeLayer();
						}, 100);
					}
					break;
				}
			}
		});
		createAUIGrid9(columns9);
		createAUIGrid8(columns8);
		createAUIGrid(columns);
		AUIGrid.resize(myGridID9);
		AUIGrid.resize(myGridID8);
		AUIGrid.resize(myGridID);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID9);
		AUIGrid.resize(myGridID8);
		AUIGrid.resize(myGridID);
	});
</script>