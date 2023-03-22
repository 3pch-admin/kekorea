<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="e3ps.epm.keDrawing.dto.KeDrawingDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="org.json.JSONArray"%>
<%
JSONArray categorys = (JSONArray) request.getAttribute("categorys");
net.sf.json.JSONArray baseData = (net.sf.json.JSONArray) request.getAttribute("baseData");
out.println(baseData);
%>
<%@include file="/extcore/include/auigrid.jsp"%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				CONFIG SHEET 등록
			</div>
		</td>
		<td class="right">
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
				<col width="150">
				<col width="*">
			</colgroup>
			<tr>
				<th class="req lb">CONFIG SHEET 제목</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="AXInput width-700">
				</td>
			</tr>
			<tr>
				<th class="req lb">KEK 작번</th>
				<td>
					<jsp:include page="/extcore/include/project-include.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
						<jsp:param value="true" name="multi" />
						<jsp:param value="" name="obj" />
						<jsp:param value="150" name="height" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">설명</th>
				<td class="indent5">
					<textarea name="description" id="description" rows="6" cols=""></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5">
					<jsp:include page="/extcore/include/secondary-include.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="req lb">결재</th>
				<td>
					<jsp:include page="/extcore/include/register-include.jsp">
						<jsp:param value="200" name="height" />
					</jsp:include>
				</td>
			</tr>
		</table>
	</div>
	<div id="tabs-2">
		<table class="button-table">
			<tr>
				<td class="left">
					<input type="button" value="행 추가" title="행 추가" class="blue" onclick="addRow();">
					<input type="button" value="행 삭제" title="행 삭제" class="red" onclick="deleteRow();">
				</td>
			</tr>
		</table>
		<div id="grid_wrap" style="height: 800px; border-top: 1px solid #3180c3;"></div>
	</div>
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
		width : 250,
		cellMerge : true,
		renderer : {
			type : "IconRenderer",
			iconWidth : 16,
			iconHeight : 16,
			iconPosition : "aisleRight",
			iconTableRef : {
				"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png" // default
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
			list : categorys,
			keyField : "key",
			valueField : "value",
			descendants : [ "item_code" ],
			validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
				let isValid = false;
				if (fromClipboard) {
					for (let i = 0; i < categorys.length; i++) {
						const key = categorys[i]["key"];
						if (newValue === key) {
							isValid = true;
						}
					}
				}
				for (let i = 0, len = categorys.length; i < len; i++) { // keyValueList 있는 값만..
					if (categorys[i]["value"] == newValue) {
						isValid = true;
						break;
					}
				}
				return {
					"validate" : isValid,
					"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
				};
			},
		},
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
		width : 200,
		cellMerge : true,
		mergeRef : "category_code",
		mergePolicy : "restrict",
		renderer : {
			type : "IconRenderer",
			iconWidth : 16,
			iconHeight : 16,
			iconPosition : "aisleRight",
			iconTableRef : {
				"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png" // default
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
			keyField : "key",
			valueField : "value",
			descendants : [ "spec_code" ],
			validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
				const param = item.category_code;
				const dd = itemListMap[param];
				let isValid = false;
				if (fromClipboard) {
					for (let i = 0; i < dd.length; i++) {
						const key = dd[i]["key"];
						if (newValue === key) {
							isValid = true;
						}
					}
				}
				for (let i = 0, len = dd.length; i < len; i++) {
					if (dd[i]["value"] == newValue) {
						isValid = true;
						break;
					}
				}
				return {
					"validate" : isValid,
					"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
				};
			},
			listFunction : function(rowIndex, columnIndex, item, dataField) {
				const param = item.category_code;
				const dd = itemListMap[param];
				if (dd === undefined) {
					return [];
				}
				return dd;
			},
		},
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
		dataField : "spec_code",
		headerText : "사양",
		dataType : "string",
		width : 250,
		renderer : {
			type : "IconRenderer",
			iconWidth : 16,
			iconHeight : 16,
			iconPosition : "aisleRight",
			iconTableRef : {
				"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png" // default
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
			keyField : "key",
			valueField : "value",
			validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
				const param = item.item_code;
				const dd = specListMap[param];
				let isValid = false;
				if (fromClipboard) {
					for (let i = 0; i < dd.length; i++) {
						const key = dd[i]["key"];
						if (newValue === key) {
							isValid = true;
						}
					}
				}
				for (let i = 0, len = dd.length; i < len; i++) { // keyValueList 있는 값만..
					if (dd[i]["value"] == newValue) {
						isValid = true;
						break;
					}
				}
				return {
					"validate" : isValid,
					"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
				};
			},
			listFunction : function(rowIndex, columnIndex, item, dataField) {
				const param = item.item_code;
				const dd = specListMap[param];
				if (dd === undefined) {
					return [];
				}
				return dd;
			},
		},
		labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
			let retStr = "";
			const param = item.item_code;
			const dd = specListMap[param];
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
			rowHeight : 30,
			showRowNumColumn : true,
			showStateColumn : true,
			rowNumHeaderText : "번호",
			selectionMode : "multipleCells",
			showRowCheckColumn : true,
			enableCellMerge : true,
			enterKeyColumnBase : true,
			editable : true,
		};
		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		AUIGrid.bind(myGridID, "cellEditEnd", auiCellEditEndHandler);
		readyHandler();
		AUIGrid.bind(myGridID, "cellEditBegin", auiCellEditBegin);
	}

	function auiCellEditBegin(event) {
		const item = event.item;
		const dataField = event.dataField;
		if (dataField === "item_code") {
			const categoryCode = item.category_code;
			const url = getCallUrl("/commonCode/getChildrens?parentCode=" + categoryCode + "&codeType=CATEGORY");
			call(url, null, function(data) {
				itemListMap[categoryCode] = data.list;
			}, "GET");
		}

		if (dataField === "item_code") {
			const itemCode = item.item_code;
			const url = getCallUrl("/commonCode/getChildrens?parentCode=" + itemCode + "&codeType=CATEGORY_ITEM");
			call(url, null, function(data) {
				specListMap[itemCode] = data.list;
			}, "GET");
		}
	}

	function readyHandler() {
		AUIGrid.addRow(myGridID,
<%=baseData%>
	);
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

		if (dataField === "item_code") {
			const itemCode = item.item_code;
			const url = getCallUrl("/commonCode/getChildrens?parentCode=" + itemCode + "&codeType=CATEGORY_ITEM");
			call(url, null, function(data) {
				specListMap[itemCode] = data.list;
			}, "GET");
		}
	}

	function addRow() {
		const item = new Object();
		AUIGrid.addRow(myGridID, item, "last");
	}

	function deleteRow() {
		const checked = AUIGrid.getCheckedRowItems(myGridID);
		for (let i = checked.length - 1; i >= 0; i--) {
			const rowIndex = checked[i].rowIndex;
			AUIGrid.removeRow(myGridID, rowIndex);
		}
	};

	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0,
			create : function(event, ui) {
				const tabId = ui.panel.prop("id");
				switch (tabId) {
				case "tabs-1":
					createAUIGrid("#myGrid1", "normal");
					break;
				case "tabs-2":
					createAUIGrid("#myGrid2", "tree");
					break;
				case "tabs-3":
					createAUIGrid("#myGrid3", "paging");
					break;
				}
			},
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-1":
					const _isCreated_ = AUIGrid.isCreated(_myGridID);
					const _isCreated = AUIGrid.isCreated(_myGridID_);
					if (_isCreated_ && _isCreated) {
						AUIGrid.resize(_myGridID);
						AUIGrid.resize(_myGridID_);
					} else {
						_createAUIGrid(_columns);
						_createAUIGrid_(_columns_);
					}
					break;
				case "tabs-2":
					const isCreated = AUIGrid.isCreated(myGridID);
					if (isCreated) {
						AUIGrid.resize(myGridID);
					} else {
						createAUIGrid(columns);
					}
					break;
				}
			}
		});

		createAUIGrid(columns);
		_createAUIGrid(_columns);
		_createAUIGrid_(_columns_);
		AUIGrid.resize(myGridID);
		AUIGrid.resize(_myGridID);
		AUIGrid.resize(_myGridID_);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
		AUIGrid.resize(_myGridID);
		AUIGrid.resize(_myGridID_);
	});
</script>