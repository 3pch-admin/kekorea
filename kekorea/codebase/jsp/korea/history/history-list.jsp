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
<body>
	<input type="hidden" name="sessionid" id="sessionid">
	<input type="hidden" name="curPage" id="curPage">
	<!-- button table -->
	<table class="btn_table">
		<tr>
			<td class="right">
				<input type="button" value="추가" class="redBtn" id="addRowBtn" title="추가">
				<input type="button" value="삭제" class="orangeBtn" id="deleteRowBtn" title="삭제">
				<input type="button" value="저장" class="" id="saveBtn" title="저장">
				<input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회">
			</td>
		</tr>
	</table>
	<div id="grid_wrap" style="height: 786px; border-top: 1px solid #3180c3;"></div>
</body>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "pDate",
		headerText : "발행일",
		dataType : "date",
		formatString : "yyyy-mm-dd",
		width : 120
	}, {
		dataField : "line",
		headerText : "LINE",
		dataType : "string",
		width : 100
	}, {
		dataField : "kekNumber",
		headerText : "KEK작번",
		dataType : "string",
		width : 140,
		editRenderer : {
			type: "RemoteListRenderer",
			fieldName: "value",
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
		dataField : "ke_number",
		headerText : "KE작번",
		dataType : "string",
		width : 140
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
		width : 130		
	},
	<%}%>
	{
		dataField : "oid",
		headerText : "oid",
		dataType : "string",
		visible : false
	} ]

	
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
				editableOnFixedCell : true
			};
			myGridID = AUIGrid.create("#grid_wrap", columns, props);
			// 그리드 데이터 로딩
			loadGridData();
			// LazyLoading 바인딩
			AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);
			// 셀 편집 종료 이벤트
			AUIGrid.bind(myGridID, "cellEditEnd", editEndHandler);
	}
	
	function editEndHandler(event) {
		if (event.type == "cellEditEnd") {
		}
	};



	function loadGridData() {
		let params = new Object();
		let url = getCallUrl("/commonCode/list");
		AUIGrid.showAjaxLoader(myGridID);
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			$("input[name=sessionid]").val(data.sessionid);
			$("input[name=curPage]").val(data.curPage);
			AUIGrid.setGridData(myGridID, data.list);
			parent.close();
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
		createAUIGrid();
		$("#searchBtn").click(function() {
			loadGridData();
		})

		// 그리드 행 추가
		$("#addRowBtn").click(function() {
			let item = new Object();
			AUIGrid.addRow(myGridID, item, "last");
		})

		$("#saveBtn").click(function() {
			let addRows = AUIGrid.getAddedRowItems(myGridID);
			let removeRows = AUIGrid.getRemovedItems(myGridID);
			let editRows = AUIGrid.getEditedRowItems(myGridID);
			let params = new Object();
			let url = getCallUrl("/commonCode/create");
			params.addRows = addRows;
			params.removeRows = removeRows;
			params.editRows = editRows;
			call(url, params, function(data) {

			}, "POST");
		})

		// 그리드 행 삭제
		$("#deleteRowBtn").click(function() {
			let checkedItems = AUIGrid.getCheckedRowItems(myGridID);
			for (let i = checkedItems.length - 1; i >= 0; i--) {
				let rowIndex = checkedItems[i].rowIndex;
				AUIGrid.removeRow(myGridID, rowIndex);
			}
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