<%@page import="java.util.HashMap"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
JSONArray maks = (JSONArray) request.getAttribute("maks");
JSONArray installs = (JSONArray) request.getAttribute("installs");
JSONArray customers = (JSONArray) request.getAttribute("customers");
String userId = (String) request.getAttribute("userId");
String name = (String) request.getAttribute("name");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/jsp/common/layouts/include_css.jsp"%>
<%@include file="/jsp/common/layouts/include_script.jsp"%>
<!-- auigrid -->
<%@include file="/jsp/include/auigrid.jsp"%>
</head>
<body>
	<input type="hidden" name="sessionid" id="sessionid">
	<input type="hidden" name="curPage" id="curPage">
	<table class="search_table">
		<colgroup>
			<col width="130">
			<col width="*">
			<col width="130">
			<col width="*">
			<col width="130">
			<col width="*">
			<col width="130">
			<col width="*">
		</colgroup>
		<tr>
			<th>항목</th>
			<td>
				<input type="text" name="item" class="AXInput wid200">
			</td>
			<th>개선내용</th>
			<td>
				<input type="text" name="improvements" class="AXInput wid200">
			</td>
			<th>개선책</th>
			<td>
				<input type="text" name="improvement" class="AXInput wid200">
			</td>
			<th>적용/미적용</th>
			<td>
				<select name="apply" id="apply" class="AXSelect wid200">
					<option value="">선택</option>
					<option value="적용완료">적용완료</option>
					<option value="일부적용">일부적용</option>
					<option value="미적용">미적용</option>
					<option value="검토중">검토중</option>
				</select>
			</td>
		</tr>
		<tr>
			<th>막종</th>
			<td>
				<select name="mak" id="mak" class="AXSelect wid200">
					<option value="">선택</option>
					<%
					for (int i = 0; i < maks.length(); i++) {
						HashMap obj = (HashMap) maks.get(i);
					%>
					<option value="<%=obj.get("key")%>"><%=obj.get("value")%></option>
					<%
					}
					%>
				</select>
			</td>
			<th>설치장소</th>
			<td>
				<select name="install" id="install" class="AXSelect wid200">
					<option value="">선택</option>
					<%
					for (int i = 0; i < installs.length(); i++) {
						HashMap obj = (HashMap) installs.get(i);
					%>
					<option value="<%=obj.get("key")%>"><%=obj.get("value")%></option>
					<%
					}
					%>
				</select>
			</td>
			<th>거래처</th>
			<td>
				<select name="customer" id="customer" class="AXSelect wid200">
					<option value="">선택</option>
					<%
					for (int i = 0; i < customers.length(); i++) {
						HashMap obj = (HashMap) customers.get(i);
					%>
					<option value="<%=obj.get("key")%>"><%=obj.get("value")%></option>
					<%
					}
					%>
				</select>
			</td>
			<th>비고</th>
			<td>
				<input type="text" name="note" class="AXInput wid200">
			</td>
		</tr>
	</table>
	<!-- button table -->
	<table class="btn_table">
		<tr>
			<td class="left">
				<input type="button" value="추가" class="redBtn" id="addRowBtn" title="추가">
				<input type="button" value="삭제" class="orangeBtn" id="deleteRowBtn" title="삭제">
				<input type="button" value="저장" class="blueBtn" id="saveBtn" title="저장">
			</td>
			<td class="right">
				<input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회">
			</td>
		</tr>
	</table>
	<div id="grid_wrap" style="height: 690px; border-top: 1px solid #3180c3;"></div>
</body>
<script type="text/javascript">
	let myGridID;
	let maks = <%=maks%>
	let installs = <%=installs%>
	let customers = <%=customers%>
	let recentGridItem = null;
	let subListMap = {};
	let list = [ "적용완료", "일부적용", "미적용", "검토중" ];
	const columns = [ {
		dataField : "item",
		headerText : "항목",
		dataType : "string",
		width : 120,
		filter : {
			showIcon : true
		}
	}, {
		dataField : "improvements",
		headerText : "개선내용",
		dataType : "string",
		width : 300,
		filter : {
			showIcon : true
		}
	}, {
		dataField : "improvement",
		headerText : "개선책",
		dataType : "string",
		width : 300,
		filter : {
			showIcon : true
		}
	}, {
		dataField : "apply",
		headerText : "적용/미적용",
		width : 100,
		editRenderer : {
			type : "ComboBoxRenderer",
			autoCompleteMode : true,
			showEditorBtnOver : true,
			list : list,
			validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
				let isValid = false;
				for (let i = 0, len = list.length; i < len; i++) { // keyValueList 있는 값만..
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
		filter : {
			showIcon : true
		}
	}, {
		dataField : "mak",
		headerText : "막종",
		width : 150,
		editRenderer : {
			type : "ComboBoxRenderer",
			autoCompleteMode : true,
			list : maks, //key-value Object 로 구성된 리스트
			keyField : "key", // key 에 해당되는 필드명
			valueField : "value", // value 에 해당되는 필드명,
			descendants : [ "detail" ], // 자손 필드들
			descendantDefaultValues : [ "-" ], // 변경 시 자손들에게 기본값 지정
			validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
				let isValid = false;
				for (let i = 0, len = maks.length; i < len; i++) { // keyValueList 있는 값만..
					if (maks[i]["value"] == newValue) {
						isValid = true;
						break;
					}
				}
				// 리턴값은 Object 이며 validate 의 값이 true 라면 패스, false 라면 message 를 띄움
				return {
					"validate" : isValid,
					"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
				};
			}
		},
		labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
			let retStr = ""; // key 값에 맞는 value 를 찾아 반환함.
			for (let i = 0, len = maks.length; i < len; i++) {
				if (maks[i]["key"] == value) {
					retStr = maks[i]["value"];
					break;
				}
			}
			return retStr == "" ? value : retStr;
		},
		filter : {
			showIcon : true
		}
	}, {
		dataField : "detail",
		headerText : "막종상세",
		width : 150,
		editRenderer : {
			type : "ComboBoxRenderer",
			autoCompleteMode : true,
			keyField : "key", // key 에 해당되는 필드명
			valueField : "value", // value 에 해당되는 필드명,
			listFunction : function(rowIndex, columnIndex, item, dataField) {
				var param = item.mak;
				var dd = subListMap[param]; // param으로 보관된 리스트가 있는지 여부
				if(dd === undefined) {
					return [];
				}
				return dd;
			},			
			validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
				let isValid = false;
				let param = item.mak;
				let dd = subListMap[param]; // param으로 보관된 리스트가 있는지 여부
				if(dd === undefined) return value;
				for (let i = 0, len = dd.length; i < len; i++) { // keyValueList 있는 값만..
					if (dd[i]["value"] == newValue) {
						isValid = true;
						break;
					}
				}
				// 리턴값은 Object 이며 validate 의 값이 true 라면 패스, false 라면 message 를 띄움
				return {
					"validate" : isValid,
					"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
				};
			}
		},
		labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
			let retStr = ""; // key 값에 맞는 value 를 찾아 반환함.
			let param = item.mak;
			let dd = subListMap[param]; // param으로 보관된 리스트가 있는지 여부
			if(dd === undefined) return value;
			for (let i = 0, len = dd.length; i < len; i++) {
				if (dd[i]["key"] == value) {
					retStr = dd[i]["value"];
					break;
				}
			}
			return retStr == "" ? value : retStr;
		},
		filter : {
			showIcon : true
		}
	}, {
		dataField : "install",
		headerText : "설치장소",
		width : 150,
		editRenderer : {
			type : "ComboBoxRenderer",
			autoCompleteMode : true,
			list : installs, //key-value Object 로 구성된 리스트
			keyField : "key", // key 에 해당되는 필드명
			valueField : "value",
			validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
				let isValid = false;
				for (let i = 0, len = installs.length; i < len; i++) { // keyValueList 있는 값만..
					if (installs[i]["value"] == newValue) {
						isValid = true;
						break;
					}
				}
				// 리턴값은 Object 이며 validate 의 값이 true 라면 패스, false 라면 message 를 띄움
				return {
					"validate" : isValid,
					"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
				};
			}
		},
		labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
			let retStr = ""; // key 값에 맞는 value 를 찾아 반환함.
			for (let i = 0, len = installs.length; i < len; i++) {
				if (installs[i]["key"] == value) {
					retStr = installs[i]["value"];
					break;
				}
			}
			return retStr == "" ? value : retStr;
		},
		filter : {
			showIcon : true
		}
	}, {
		dataField : "customer",
		headerText : "거래처",
		width : 150,
		editRenderer : {
			type : "ComboBoxRenderer",
			autoCompleteMode : true,
			list : customers, //key-value Object 로 구성된 리스트
			keyField : "key", // key 에 해당되는 필드명
			valueField : "value",
			validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
				let isValid = false;
				for (let i = 0, len = customers.length; i < len; i++) { // keyValueList 있는 값만..
					if (customers[i]["value"] == newValue) {
						isValid = true;
						break;
					}
				}
				// 리턴값은 Object 이며 validate 의 값이 true 라면 패스, false 라면 message 를 띄움
				return {
					"validate" : isValid,
					"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
				};
			}
		},
		labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
			let retStr = ""; // key 값에 맞는 value 를 찾아 반환함.
			for (let i = 0, len = customers.length; i < len; i++) {
				if (customers[i]["key"] == value) {
					retStr = customers[i]["value"];
					break;
				}
			}
			return retStr == "" ? value : retStr;
		},
		filter : {
			showIcon : true
		}
	}, {
		dataField : "note",
		headerText : "비고",
		dataType : "string",
		width : 150,
		filter : {
			showIcon : true
		}
	}, {
		dataField : "preView",
		headerText : "미리보기",
		width : 100,
		editable : false,
		renderer : {
			type : "ImageRenderer",
			altField : null,
			imgHeight : 34,
		}
	}, {
		dataField : "",
		headerText : "",
		width : 100,
		editable : false,
		renderer : {
			type : "ButtonRenderer",
			labelText : "파일선택",
			onclick : function(rowIndex, columnIndex, value, item) {
				recentGridItem = item;
				let oid = item.oid;
				let url = getCallUrl("/aui/preview?oid=" + oid + "&method=preView");
				popup(url, 1000, 200);
			}
		}
	}, {
		dataField : "icons",
		headerText : "첨부파일",
		width : 100,
		editable : false,
		renderer : {
			type : "TemplateRenderer",
		},
	}, {
		dataField : "",
		headerText : "",
		width : 100,
		editable : false,
		renderer : {
			type : "ButtonRenderer",
			labelText : "파일선택",
			onclick : function(rowIndex, columnIndex, value, item) {
				recentGridItem = item;
				let oid = item.oid;
				let url = getCallUrl("/aui/secondary?oid=" + oid + "&method=setSecondary");
				popup(url, 1000, 400);
			}
		}
	}, {
		dataField : "creator",
		headerText : "작성자",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true
		}
	}, {
		dataField : "createdDate",
		headerText : "작성일",
		dataType : "date",
		formatString : "yyyy-mm-dd",
		width : 100,
		filter : {
			showIcon : true
		}
	}, {
		dataField : "secondaryPaths",
		headerText : "",
		dataType : "string",
		visible : false
	}, {
		dataField : "preViewPath",
		headerText : "",
		dataType : "string",
		visible : false
	}, {
		dataField : "oid",
		headerText : "oid",
		dataType : "string",
		visible : false
	} ]

	function createAUIGrid(columns) {
		const props = {
			rowIdField : "oid",
			headerHeight : 30,
			rowHeight : 36,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			editable : true,
			showStateColumn : true,
			showRowCheckColumn : true,
			noDataMessage : "검색 결과가 없습니다.",
			enableFilter : true,
			fixedColumnCount : 3,
			editableOnFixedCell : true
		};
		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		loadGridData();
		AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);
		AUIGrid.bind(myGridID, "addRowFinish", auiAddRowHandler);
		AUIGrid.bind(myGridID, "cellClick", auiCellClickHandler);
		AUIGrid.bind(myGridID, "cellEditEnd", auiCellEditEndHandler);
	}
	
	function auiCellEditEndHandler(event) {
		let dataField = event.dataField;
		let item = event.item;
		let rowIndex = event.rowIndex;
		let mak = item.mak;
		if(dataField === "mak") {
			let mak = item.mak;
			let url = getCallUrl("/commonCode/getChildrens?parentCode=" + mak + "&codeType=MAK");
			call(url, null, function(data) {
				subListMap[mak] = data.list;
			}, "GET");
		}
	}

	function auiCellClickHandler(event) {
		let oid = event.item.oid;
		let dataField = event.dataField;
		if (dataField == "preView" && oid.indexOf("Cip") > -1) {
			let url = getCallUrl("/aui/thumbnail?oid=" + oid);
			popup(url);
		}
	}

	function loadGridData() {
		let params = new Object();
		params.apply = $("#apply").val();
		let url = getCallUrl("/cip/list");
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

	let last = false;
	function vScrollChangeHandler(event) {
		if (event.position == event.maxPosition) {
			if (!last) {
				requestAdditionalData();
			}
		}
	}

	function requestAdditionalData() {
		let params = new Object();
		let curPage = $("input[name=curPage]").val();
		params.sessionid = $("input[name=sessionid]").val();
		params.start = (curPage * 30);
		params.end = (curPage * 30) + 30;
		let url = getCallUrl("/appendData");
		AUIGrid.showAjaxLoader(myGridID);
		call(url, params, function(data) {
			if (data.list.length == 0) {
				last = true;
				alert("마지막 데이터 입니다.");
				AUIGrid.removeAjaxLoader(myGridID);
			} else {
				AUIGrid.appendData(myGridID, data.list);
				AUIGrid.removeAjaxLoader(myGridID);
				$("input[name=curPage]").val(parseInt(curPage) + 1);
			}
		})
	}

	function auiAddRowHandler(event) {
		let selected = AUIGrid.getSelectedIndex(myGridID);
		if (selected.length <= 0) {
			return;
		}

		let rowIndex = selected[0];
		let colIndex = AUIGrid.getColumnIndexByDataField(myGridID, "item");
		AUIGrid.setSelectionByIndex(myGridID, rowIndex, colIndex);
		AUIGrid.openInputer(myGridID);
	}

	function preView(data) {
		let preView = data.base64;
		let preViewPath = data.fullPath;
		AUIGrid.updateRowsById(myGridID, {
			oid : recentGridItem.oid,
			preView : preView,
			preViewPath : preViewPath
		});
	}

	function setSecondary(data) {
		let template = "";
		let arr = new Array();
		for (let i = 0; i < data.length; i++) {
			template += "<img src='" + data[i].icon + "'>&nbsp;";
			arr.push(data[i].fullPath);
		}

		AUIGrid.updateRowsById(myGridID, {
			oid : recentGridItem.oid,
			secondaryPaths : arr,
			icons : template
		});
	}

	$(function() {
		createAUIGrid(columns);

		selectBox("apply");
		selectBox("mak");
		selectBox("install");
		selectBox("customer");

		$("#saveBtn").click(function() {
			let addRows = AUIGrid.getAddedRowItems(myGridID);
			let removeRows = AUIGrid.getRemovedItems(myGridID);
			let editRows = AUIGrid.getEditedRowItems(myGridID);
			let params = new Object();
			let url = getCallUrl("/cip/create");
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
		})

		$("#addRowBtn").click(function() {
			let item = new Object();
			item.createdDate = new Date();
			item.creator = "<%=name %>";
			AUIGrid.addRow(myGridID, item, "first");
		})

		// 그리드 행 삭제
		$("#deleteRowBtn").click(function() {
			let checkedItems = AUIGrid.getCheckedRowItems(myGridID);
			for (let i = checkedItems.length - 1; i >= 0; i--) {
				let rowIndex = checkedItems[i].rowIndex;
				AUIGrid.removeRow(myGridID, rowIndex);
			}
		})

		$("#searchBtn").click(function() {
			loadGridData();
		})
	})
</script>
</html>