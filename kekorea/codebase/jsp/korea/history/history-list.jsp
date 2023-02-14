<%@page import="java.util.Map"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.admin.commonCode.CommonCodeType"%>
<%@page import="org.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<Map<String, Object>> headers = (ArrayList<Map<String, Object>>) request.getAttribute("headers");
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
<body style="margin-bottom: 0px;">
	<input type="hidden" name="sessionid" id="sessionid">
	<input type="hidden" name="curPage" id="curPage">
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
	<div id="grid_wrap" style="height: 785px; border-top: 1px solid #3180c3;"></div>
</body>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "pDate",
		headerText : "발행일",
		dataType : "date",
		formatString : "yyyy-mm-dd",
		width : 120,
		editable : false
	}, {
		dataField : "install",
		headerText : "설치장소",
		dataType : "string",
		width : 100,
		editable : false
	}, {
		dataField : "kekNumber",
		headerText : "KEK작번",
		dataType : "string",
		width : 140,
		editRenderer : {
			type: "RemoteListRenderer",
			fieldName: "value",
			noDataMessage : "검색결과가 없습니다.",
			showEditorBtnOver: true, // 마우스 오버 시 에디터버턴 보이기
			remoter: function (request, response) { // remoter 지정 필수
				if (String(request.term).length < 2) {
					alert("2글자 이상 입력하십시오.");
					response(false); // 데이터 요청이 없는 경우 반드시 false 삽입하십시오.
					return;
				}
				// 데이터 요청
				let url = getCallUrl("/history/remoter");
				let params = new Object();
				params.term = request.term;
				params.target = "project";
				call(url, params, function(data) {
					response(data.list);
				}, "POST");
			}
		}
	}, {
		dataField : "keNumber",
		headerText : "KE작번",
		dataType : "string",
		width : 140,
		editable : false
	}, {
		dataField : "tuv",
		headerText : "TUV유무",
		dataType : "string",
		width : 130
	}, 
	<%for (Map<String, Object> header : headers) {
	String dataField = (String) header.get("key");
	String headerText = (String) header.get("value");%>
	{
		dataField : "<%=dataField%>",
		headerText : "<%=headerText%>",
		dataType : "string",
		width : 150,
		editRenderer : {
			type: "RemoteListRenderer",
			fieldName: "value",
			noDataMessage : "검색결과가 없습니다.",
			showEditorBtnOver: true, // 마우스 오버 시 에디터버턴 보이기
			remoter: function (request, response) { // remoter 지정 필수
				if (String(request.term).length < 2) {
					alert("2글자 이상 입력하십시오.");
					response(false); // 데이터 요청이 없는 경우 반드시 false 삽입하십시오.
					return;
				}
				// 데이터 요청
				let url = getCallUrl("/options/remoter");
				let params = new Object();
				params.term = request.term;
				params.columnKey = "<%=dataField%>";
				params.target = "options";
				call(url, params, function(data) {
					response(data.list);
				}, "POST");
			}
		}		
	},
	<%}%>
	{
		dataField : "oid",
		headerText : "oid",
		dataType : "string",
		visible : false
	},
	{
		dataField : "poid",
		headerText : "poid",
		dataType : "string",
		visible : false
	}]

	
	function createAUIGrid(columnLayout) {
		const props = {
				rowIdField : "oid",
				headerHeight : 30,
				rowHeight : 30,
				showRowNumColumn : true,
				rowNumHeaderText : "번호",
				showRowCheckColumn : true, // 체크 박스 출력
				fixedColumnCount : 5,
				editable : true,
				editableOnFixedCell : true,
				showStateColumn : true,
			};
			myGridID = AUIGrid.create("#grid_wrap", columns, props);
			// 그리드 데이터 로딩
			loadGridData();
			// LazyLoading 바인딩
			AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);
			// 셀 편집 종료 이벤트
			AUIGrid.bind(myGridID, "cellEditEnd", editEndHandler);
			AUIGrid.bind(myGridID, "addRowFinish", auiAddRowHandler); 
	}
	
	function auiAddRowHandler(event) {
		let selected = AUIGrid.getSelectedIndex(myGridID);
		if (selected.length <= 0) {
			return;
		}

		let rowIndex = selected[0];
		let colIndex = AUIGrid.getColumnIndexByDataField(myGridID, "kekNumber");
		AUIGrid.setSelectionByIndex(myGridID, rowIndex, colIndex);
		AUIGrid.openInputer(myGridID);
	}
	
	function editEndHandler(event) {
		let dataField = event.dataField;
		let item = event.item;
		
		if(dataField === "kekNumber") {
			let url = getCallUrl("/project/get?kekNumber="+item.kekNumber);
			call(url, null, function(data) {
				AUIGrid.updateRowsById(myGridID, {
					oid : item.oid,
					keNumber : data.keNumber,
					install : data.install,
					pDate : data.pDate,
					poid : data.oid
				});
			}, "GET");
		}
	};

	function loadGridData() {
		let params = new Object();
		let url = getCallUrl("/history/list");
		AUIGrid.showAjaxLoader(myGridID);
		parent.openLayer();
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			$("input[name=sessionid]").val(data.sessionid);
			$("input[name=curPage]").val(data.curPage);
			AUIGrid.setGridData(myGridID, data.list);
			parent.closeLayer();
		}, "POST");
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
		}, "POST");
	}

	$(function() {
		// 그리드 생성 함수 - 모든 이벤트 안에 바인딩 시킨다.
		createAUIGrid(columns);
		$("#searchBtn").click(function() {
			loadGridData();
		})


		$("#addRowBtn").click(function() {
			let item = new Object();
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

		$("#saveBtn").click(function() {
			let addRows = AUIGrid.getAddedRowItems(myGridID);
			let removeRows = AUIGrid.getRemovedItems(myGridID);
			let editRows = AUIGrid.getEditedRowItems(myGridID);
			let params = new Object();
			let url = getCallUrl("/history/create");
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