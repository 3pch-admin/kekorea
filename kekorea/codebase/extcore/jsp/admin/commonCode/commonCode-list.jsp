<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.admin.commonCode.CommonCodeType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
CommonCodeType[] codeTypes = (CommonCodeType[]) request.getAttribute("codeTypes");
JSONArray jsonList = (JSONArray) request.getAttribute("jsonList");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=1010"></script>
</head>
<body>
	<form>
		<table class="search-table">
			<tr>
				<th>코드 명</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-200">
				</td>
				<th>코드</th>
				<td class="indent5">
					<input type="text" name="code" id="code" class="width-200">
				</td>
				<th>코드 타입</th>
				<td class="indent5">
					<select name="codeType" id="codeType" class="width-200">
						<option value="">선택</option>
						<%
						for (CommonCodeType codeType : codeTypes) {
							String value = codeType.toString();
							if (value.equals("MAK_DETAIL") || value.equals("INSTALL") || value.equals("CATEGORY")
							|| value.equals("CATEGORY_ITEM") || value.equals("CATEGORY_SPEC")) {
								continue;
							}
						%>
						<option value="<%=codeType.toString()%>"><%=codeType.getDisplay()%></option>
						<%
						}
						%>
					</select>
				</td>
			</tr>
			<tr>
				<th>설명</th>
				<td class="indent5" colspan="3">
					<input type="text" name="description" id="description" class="width-500">
				</td>
				<th>사용여부</th>
				<td>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="enable" value="true" checked="checked">
						<div class="state p-success">
							<label>
								<b>사용</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="enable" value="">
						<div class="state p-success">
							<label>
								<b>미사용</b>
							</label>
						</div>
					</div>
				</td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="left">
					<input type="button" value="저장" class="" id="saveBtn" title="저장" onclick="save()">
					<input type="button" value="행 추가" class="blue" id="addRowBtn" title="추가" onclick="addRow();">
					<input type="button" value="행 삭제" class="red" id="deleteRowBtn" title="삭제" onclick="deleteRow()">
				</td>
				<td class="right">
					<input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회" onclick="loadGridData();">
				</td>
			</tr>
		</table>
		<div id="grid_wrap" style="height: 700px; border-top: 1px solid #3180c3;"></div>
	</form>
</body>
<script type="text/javascript">
	let myGridID;
	let parentList = [];
	const jsonList =
<%=jsonList%>
	const columns = [ {
		dataField : "name",
		headerText : "코드 명",
		dataType : "string",
		width : 300
	}, {
		dataField : "code",
		headerText : "코드",
		dataType : "string",
		width : 150
	}, {
		dataField : "codeType",
		headerText : "코드 타입",
		dataType : "string",
		width : 200,
		editRenderer : {
			type : "ComboBoxRenderer",
			autoCompleteMode : true,
			showEditorBtnOver : true,
			list : jsonList,
			keyField : "key",
			valueField : "value",
			validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
				let isValid = false;
				for (let i = 0, len = jsonList.length; i < len; i++) { // keyValueList 있는 값만..
					if (jsonList[i]["value"] == newValue) {
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
		labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
			let retStr = ""; // key 값에 맞는 value 를 찾아 반환함.
			for (let i = 0, len = jsonList.length; i < len; i++) {
				if (jsonList[i]["key"] == value) {
					retStr = jsonList[i]["value"];
					break;
				}
			}
			return retStr == "" ? value : retStr;
		}
	}, {
		dataField : "parent_name",
		headerText : "상위코드 명",
		dataType : "string",
		width : 180,
		editRenderer : {
			type : "RemoteListRenderer",
			fieldName : "value",
			showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
			remoter : function(request, response) { // remoter 지정 필수
				if (String(request.term).length < 2) {
					alert("2글자 이상 입력하십시오.");
					response(false); // 데이터 요청이 없는 경우 반드시 false 삽입하십시오.
					return;
				}
				// 데이터 요청
				let url = getCallUrl("/commonCode/remoter");
				let params = new Object();
				params.term = request.term;
				params.target = "code";
				call(url, params, function(data) {
					parentList = data.list;
					response(data.list);
				}, "POST");
			}
		},
	}, {
		dataField : "description",
		headerText : "설명",
		dataType : "string",
		style : "aui-left",
	}, {
		dataField : "sort",
		headerText : "정렬",
		dataType : "numeric",
		width : 80,
		editRenderer : {
			type : "InputEditRenderer",
			onlyNumeric : true,
		},
	}, {
		dataField : "enable",
		headerText : "사용여부",
		dataType : "boolean",
		width : 120,
		renderer : {
			type : "CheckBoxEditRenderer",
			editable : true, // 체크박스 편집 활성화 여부(기본값 : false)
		},
	}, {
		dataField : "createDate_txt",
		headerText : "작성일",
		dataType : "string",
		width : 120
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			rowIdField : "oid",
			headerHeight : 30,
			showRowNumColumn : true,
			showAutoNoDataMessage : false,
			rowNumHeaderText : "번호",
			showRowCheckColumn : true,
			editable : true,
			showStateColumn : true,
			selectionMode : "multipleCells",
			treeIdField : "oid",
			treeIdRefField : "parent",
			enableRowCheckShiftKey : true,
			useContextMenu : true,
			enableRightDownFocus : true,
			contextMenuItems : [ {
				label : "선택된 행 이전 추가",
				callback : contextItemHandler
			}, {
				label : "선택된 행 이후 추가",
				callback : contextItemHandler
			}, {
				label : "_$line"
			}, {
				label : "선택된 행 삭제",
				callback : contextItemHandler
			} ],
		};

		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		loadGridData();
		AUIGrid.bind(myGridID, "cellEditEnd", auiCellEditHandler);
	}

	function contextItemHandler(event) {
		const item = {
			enable : true,
		}
		switch (event.contextIndex) {
		case 0:
			AUIGrid.addRow(myGridID, item, "selectionUp");
			break;
		case 1:
			AUIGrid.addRow(myGridID, item, "selectionDown");
			break;
		case 3:
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

	function auiCellEditHandler(event) {
		if (event.dataField === "parentName") {
			const item = getItem(event.value);
			const poid = item.key;
			AUIGrid.updateRow(myGridID, {
				poid : poid
			}, event.rowIndex);
		}
	}

	function getItem(value) {
		let item;
		$.each(parentList, function(n, v) {
			if (v.value === value) {
				item = v;
				return false;
			}
		});
		return item;
	};

	function loadGridData() {
		let params = new Object();
		const url = getCallUrl("/commonCode/list");
		const field = ["name","code","description","codeType"];
		const enable = !!document.querySelector("input[name=enable]:checked").value;
		params = toField(params, field);
		params.enable = enable;
		AUIGrid.showAjaxLoader(myGridID);
		parent.openLayer();
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			$("input[name=sessionid]").val(data.sessionid);
			$("input[name=curPage]").val(data.curPage);
			AUIGrid.setGridData(myGridID, data.list);
			parent.closeLayer();
		});
	}

	// 그리드 행 추가
	function addRow() {
		const item = new Object();
		item.enable = true;
		AUIGrid.addRow(myGridID, item, "first");
	}

	// 그리드 행 삭제(클릭 순서에 따라 삭제 행이 달라짐 위에서부터 눌러야 문제 없음.. )
	function deleteRow() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
		for (let i = checkedItems.length - 1; i >= 0; i--) {
			const rowIndex = checkedItems[i].rowIndex;
			AUIGrid.removeRow(myGridID, rowIndex);
		}
	}

	function save() {

		const url = getCallUrl("/commonCode/create");
		const params = new Object();
		const addRows = AUIGrid.getAddedRowItems(myGridID);
		const removeRows = AUIGrid.getRemovedItems(myGridID);
		const editRows = AUIGrid.getEditedRowItems(myGridID);

		if (addRows.length === 0 && removeRows.length === 0 && editRows.length === 0) {
			alert("변경된 내용이 없습니다.");
			return false;
		}

		for (let i = 0; i < addRows.length; i++) {
			let item = addRows[i];
			let rowIndex = AUIGrid.rowIdToIndex(myGridID, item.oid);

			if (isNull(item.name)) {
				AUIGrid.showToastMessage(myGridID, rowIndex, 0, "코드 명의 값은 공백을 입력 할 수 없습니다.");
				return false;
			}

			if (isNull(item.code)) {
				AUIGrid.showToastMessage(myGridID, rowIndex, 1, "코드의 값은 공백을 입력 할 수 없습니다.");
				return false;
			}

			if (isNull(item.codeType)) {
				AUIGrid.showToastMessage(myGridID, rowIndex, 2, "코드 타입은 리스트에 있는 값을 선택(입력)해야합니다 .");
				return false;
			}
		}

		for (let i = 0; i < editRows.length; i++) {
			const item = editRows[i];
			const rowIndex = AUIGrid.rowIdToIndex(myGridID, item.oid);

			if (isNull(item.name)) {
				AUIGrid.showToastMessage(myGridID, rowIndex, 0, "코드 명의 값은 공백을 입력 할 수 없습니다.");
				return false;
			}

			if (isNull(item.code)) {
				AUIGrid.showToastMessage(myGridID, rowIndex, 1, "코드의 값은 공백을 입력 할 수 없습니다.");
				return false;
			}
		}

		if (!confirm("저장 하시겠습니까?")) {
			return false;
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
		}, "POST");

	}

	document.addEventListener("DOMContentLoaded", function() {
		document.getElementById("name").focus();
		createAUIGrid(columns);
		selectbox("codeType");
	});

	document.addEventListener("keydown", function(event) {
		const keyCode = event.keyCode || event.which;
		if (keyCode === 13) {
			loadGridData();
		}
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>
</html>