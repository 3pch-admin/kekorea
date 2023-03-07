<%@page import="wt.org.WTUser"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.admin.commonCode.CommonCodeType"%>
<%@page import="org.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
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
		<tr>
			<th>사업부문</th>
			<td>&nbsp;</td>
			<th>작성기간</th>
			<td>&nbsp;</td>
			<th>도면번호</th>
			<td>
				<input type="text" name="kekNumber" class="AXInput wid200">
			</td>
			<th>도면생성회사</th>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<th>사이즈</th>
			<td>&nbsp;</td>
			<th>도면구분</th>
			<td>&nbsp;</td>
			<th>년도</th>
			<td>&nbsp;</td>
			<th>관리번호</th>
			<td>
				<input type="text" name="kekNumber" class="AXInput wid200">
			</td>
		</tr>
		<tr>
			<th>부품도구분</th>
			<td>&nbsp;</td>
			<th>진행상태</th>
			<td>&nbsp;</td>
			<th>작성부서</th>
			<td>&nbsp;</td>
			<th>작성자</th>
			<td>
				<input type="text" name="kekNumber" class="AXInput wid200">
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
				<input type="button" value="초기화" class="" id="initGrid" title="초기화">
			</td>
		</tr>
	</table>
	<div id="grid_wrap" style="height: 665px; border-top: 1px solid #3180c3;"></div>
</body>
<script type="text/javascript">
	let myGridID;
	let list = [ "상태값1", "상태값2", "상태값3", "상태값4" ];
	const columns = [ {
		dataField : "lotNo",
		headerText : "LOT",
		dataType : "nemeric",
		width : 100,
		formatString : "###0",
		editRenderer : {
			type : "InputEditRenderer",
			onlyNumeric : true, // 0~9만 입력가능
		},		
		filter : {
			showIcon : true
		}		
	}, {
		dataField : "code",
		headerText : "중간코드",
		dataType : "string",
		width : 130,
		filter : {
			showIcon : true
		}		
	}, {
		dataField : "kePartNumber",
		headerText : "부품번호",
		dataType : "string",
		width : 130,
		filter : {
			showIcon : true
		}		
	}, {
		dataField : "kePartName",
		headerText : "부품명",
		dataType : "string",
		width : 200,
		filter : {
			showIcon : true
		}		
	}, {
		dataField : "model",
		headerText : "KokusaiModel",
		dataType : "string",
		filter : {
			showIcon : true
		}		
	}, {
		dataField : "latest",
		headerText : "최신버전",
		dataType : "string",
		width : 80,
		renderer : {
			type : "CheckBoxEditRenderer",
			edtiable : true
		},
		editable : true
	},{
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 100,
		editRenderer : {
			type : "ComboBoxRenderer",
			autoCompleteMode : true,
			showEditorBtnOver : true,
			list : list,
			validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
				let isValid = false;
				for (let i = 0, len = list.length; i < len; i++) { // keyValueList 있는 값만..
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
			showIcon : true
		}		
	},{
		dataField : "creator",
		headerText : "등록자",
		dataType : "string",
		width : 80,
		editable : false,
		filter : {
			showIcon : true
		}		
	}, {
		dataField : "createdDate",
		headerText : "등록일",
		dataType : "date",
		formatString : "yyyy-mm-dd",
		width : 80,
		editable : false,
		filter : {
			showIcon : true
		}		
	}, {
		dataField : "modifier",
		headerText : "수정자",
		dataType : "string",
		width : 80,
		editable : false,
		filter : {
			showIcon : true
		}		
	}, {
		dataField : "modifiedDate",
		headerText : "수정일",
		dataType : "date",
		formatString : "yyyy-mm-dd",
		width : 80,
		editable : false,
		filter : {
			showIcon : true
		}		
	}, {
		dataField : "primary",
		headerText : "도면파일",
		dataType : "string",
		width : 100,
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
				let url = getCallUrl("/aui/primary?oid=" + oid + "&method=attach");
				popup(url, 1000, 200);
			}
		}
	}, {
		dataField : "oid",
		headerText : "oid",
		dataType : "string",
		visible : false
	}, {
		dataField : "primaryPath",
		headerText : "",
		dataType : "string",
		visible : false
	}, ]

	function createAUIGrid(columnLayout) {
		const props = {
			rowIdField : "oid",
			headerHeight : 30,
			rowHeight : 36,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			fillColumnSizeMode : true,
			showStateColumn : true,
			showRowCheckColumn : true,
			noDataMessage : "검색 결과가 없습니다.",
			editable : true,
			enableFilter : true,
			selectionMode : "multipleCells"
		};

		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		loadGridData();
		// LazyLoading 바인딩
		AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);
		AUIGrid.bind(myGridID, "addRowFinish", auiAddRowHandler);
		AUIGrid.bind(myGridID, "cellEditEnd", auiCellEditEnd);
	}
	
	function auiCellEditEnd(event) {
		let dataField = event.dataField;
		if(dataField === "kePartNumber") {
			let item = {
					latest : true,
					state : "상태값1"
			}
			console.log(item);
			console.log(event.rowIndex);
			AUIGrid.updateRow(myGridID, item, event.rowIndex);
		}
	}

	function loadGridData() {
		let params = new Object();
		let url = getCallUrl("/kepart/list");
		AUIGrid.showAjaxLoader(myGridID);
		parent.openLayer();
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			$("input[name=sessionid]").val(data.sessionid);
			$("input[name=curPage]").val(data.curPage);
			AUIGrid.setGridData(myGridID, data.list);
			parent.closeLayer();
		})
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
		let url = getCallUrl("/aui/appendData");
		AUIGrid.showAjaxLoader(myGridID);
		call(url, params, function(data) {
			if (data.list.length == 0) {
				last = true;
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
		let colIndex = AUIGrid.getColumnIndexByDataField(myGridID, "lotNo");
		AUIGrid.setSelectionByIndex(myGridID, rowIndex, colIndex);
		AUIGrid.openInputer(myGridID);
	}

	function attach(data) {
		let template = "<img src='" + data.icon + "'>";
		AUIGrid.updateRowsById(myGridID, {
			oid : recentGridItem.oid,
			primary : template,
			primaryPath : data.fullPath
		});
	}

	$(function() {

		createAUIGrid(columns);

		$("#addRowBtn").click(function() {
			let item = new Object();
			item.latest = true;
			item.createdDate = new Date();
			item.modifiedDate = new Date();
			item.creator = "<%=sessionUser.getFullName()%>";
			item.modifier = "<%=sessionUser.getFullName()%>";
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

		$("#saveBtn").click(function() {
// 			let data = AUIGrid.getGridData(myGridID);
// 			for(let i=0; i<data.length; i++) {
// 				let isUnique = AUIGrid.isUniqueValue(myGridID, "oid", data[i].kePartNumber);
// 				console.log(isUnique);
// 				if(!isUnique) {
// 					AUIGrid.showToastMessage(myGridID, i, 2,  "중복된 번호입니다.");
// 				}
// 			}
			
			let url = getCallUrl("/kepart/create");
			let addRows = AUIGrid.getAddedRowItems(myGridID);
			let removeRows = AUIGrid.getRemovedItems(myGridID);
			let editRows = AUIGrid.getEditedRowItems(myGridID);
			let params = new Object();
			params.addRows = addRows;
			params.removeRows = removeRows;
			params.editRows = editRows;
			parent.openLayer();
			call(url, params, function(data) {
				alert(data.msg);
				if (data.result) {
					loadGridData();
				} else {
					parent.closeLayer();
				}
			}, "POST");
		})

	}).keypress(function(e) {
		let keyCode = e.keyCode;
		if (keyCode == 13) {
			loadGridData();
		}
	})

	$(window).resize(function() {
		AUIGrid.resize(myGridID);
	})
</script>
</html>