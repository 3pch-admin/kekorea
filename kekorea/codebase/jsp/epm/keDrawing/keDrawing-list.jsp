<%@page import="wt.org.WTUser"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.admin.commonCode.CommonCodeType"%>
<%@page import="org.json.JSONArray"%>
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
			<th>최신버전</th>
			<td>
				<label title="최신버전">
					<input type="radio" name="latest" value="true" checked="checked">
					<span class="latest">최신버전</span>
				</label>
				<label title="모든버전">
					<input type="radio" name="latest" value="false">
					<span class="latest">모든버전</span>
				</label>
			</td>
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
				<input type="button" value="테이블 저장" class="orangeBtn" id="saveColumnBtn" title="테이블 저장" onclick="saveColumnLayout('myGridID');">
				<input type="button" value="추가" class="redBtn" id="addRowBtn" title="추가">
				<%
				if (isAdmin) {
				%>
				<input type="button" value="삭제" class="orangeBtn" id="deleteRowBtn" title="삭제">
				<%
				}
				%>
				<input type="button" value="저장" class="blueBtn" id="saveBtn" title="저장">
				<input type="button" value="개정" id="reviseBtn" title="개정">
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
	function _layout() {
		return [ {
			dataField : "lotNo",
			headerText : "LOT",
			dataType : "numeric",
			width : 100,
			formatString : "###0",
			editRenderer : {
				type : "InputEditRenderer",
				onlyNumeric : true, // 0~9만 입력가능
			},
		}, {
			dataField : "name",
			headerText : "DRAWING TITLE",
			dataType : "string",
		}, {
			dataField : "keNumber",
			headerText : "DWG. NO",
			dataType : "string",
			width : 300,
			editable : false
		}, {
			dataField : "version",
			headerText : "버전",
			dataType : "numeric",
			width : 80,
			editable : false
		}, {
			dataField : "latest",
			headerText : "최신버전",
			dataType : "boolean",
			width : 100,
			renderer : {
				type : "CheckBoxEditRenderer",
			},
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
	};

	function createAUIGrid(columnLayout) {
		const props = {
			rowIdField : "oid",
			// 공통 시작
			headerHeight : 30, // 헤더높이
			rowHeight : 30, // 행 높이
			showRowNumColumn : true, // 번호 행 출력 여부
			showStateColumn : true, // 상태표시 행 출력 여부
			rowNumHeaderText : "번호", // 번호 행 텍스트 설정
			noDataMessage : "검색 결과가 없습니다.", // 데이터 없을시 출력할 내용
			enableFilter : true, // 필터 사용 여부
			showRowCheckColumn : true, // 엑스트라 체크 박스 사용 여부
			selectionMode : "multiCells",
			enableMovingColumn : true,
			// 공통 끝
			fillColumnSizeMode : true,
			editable : true,
		};

		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		loadGridData();
		// LazyLoading 바인딩
		AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);
		AUIGrid.bind(myGridID, "addRowFinish", auiAddRowHandler);
	}

	function loadGridData() {
		let params = new Object();
		let url = getCallUrl("/keDrawing/list");
		params = form(params, "search_table");
		AUIGrid.showAjaxLoader(myGridID);
		parent.openLayer();
		console.log(params);
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

	let recentGridItem = null;
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
		let columns = loadColumnLayout();
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
			let url = getCallUrl("/keDrawing/create");
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

		$("#reviseBtn").click(function() {
			let checkedItems = AUIGrid.getCheckedRowItems(myGridID);
			if (checkedItems.length == 0) {
				alert("개정할 도면을 선택하세요.");
				return false;
			}
			let url = getCallUrl("/keDrawing/revise");
			let panel;
			panel = popup(url, 1600, 550);
			panel.list = checkedItems;
		})

		radio("latest");

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