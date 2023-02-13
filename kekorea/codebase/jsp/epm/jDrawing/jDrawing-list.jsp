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
	const columns = [ {
		dataField : "name",
		headerText : "DRAWING TITLE",
		dataType : "string",
	}, {
		dataField : "number",
		headerText : "DWG. NO",
		dataType : "string",
		width : 300,
		editable : false
	}, {
		dataField : "latest",
		headerText : "최신버전여부",
		dataType : "string",
		width : 100,
		renderer : {
			type : "CheckBoxEditRenderer",
		},
		editable : false
	}, {
		dataField : "version",
		headerText : "REV",
		dataType : "numeric",
		width : 100,
		editable : false
	}, {
		dataField : "creator",
		headerText : "등록자",
		dataType : "string",
		width : 100,
		editable : false
	}, {
		dataField : "createdDate",
		headerText : "등록일",
		dataType : "date",
		formatString : "yyyy-mm-dd",
		width : 100,
		editable : false
	}, {
		dataField : "modifier",
		headerText : "수정자",
		dataType : "string",
		width : 100,
		editable : false
	}, {
		dataField : "modifiedDate",
		headerText : "수정일",
		dataType : "date",
		formatString : "yyyy-mm-dd",
		width : 100,
		editable : false
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
		};

		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		loadGridData();
		// LazyLoading 바인딩
		AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);
		AUIGrid.bind(myGridID, "addRowFinish", auiAddRowHandler);
	}

	function loadGridData() {
		let params = new Object();
		let url = getCallUrl("/jDrawing/list");
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
		let colIndex = AUIGrid.getColumnIndexByDataField(myGridID, "name");
		AUIGrid.setSelectionByIndex(myGridID, rowIndex, colIndex);
		AUIGrid.openInputer(myGridID);
	}

	function attach(data) {
		let name = data.name;
		let start = name.indexOf("-");
		let end = name.lastIndexOf(".");
		let number = name.substring(0, start);
		let version = name.substring(start + 1, end);
		let template = "<img src='" + data.icon + "'>";
		AUIGrid.updateRowsById(myGridID, {
			oid : recentGridItem.oid,
			number : number,
			version : Number(version),
			file : name,
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
			let url = getCallUrl("/jDrawing/create");
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