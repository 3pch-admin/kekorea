<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.admin.commonCode.CommonCodeType"%>
<%@page import="org.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/jsp/common/layouts/include_css.jsp"%>
<%@include file="/jsp/common/layouts/include_script.jsp"%>
<!-- auigrid -->
<%@include file="/jsp/include/auigrid.jsp"%>
<!-- highchart -->
<%@include file="/jsp/include/highchart.jsp"%>
</head>
<body onload="loadGridData();">
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
	<div id="grid_wrap" style="height: 383px; border-top: 1px solid #3180c3;"></div>
</body>
<script type="text/javascript">
	let myGridID;

	const columns = [ {
		dataField : "state",
		headerText : "발행일",
		dataType : "string",
		width : 100
	}, {
		dataField : "pType",
		headerText : "LINE",
		dataType : "string",
		width : 100
	}, {
		dataField : "customer",
		headerText : "KEK작번",
		dataType : "string",
		width : 100
	}, {
		dataField : "ins_location",
		headerText : "KE작번",
		dataType : "string",
		width : 130
	}, {
		dataField : "mak",
		headerText : "TUV유무",
		dataType : "string",
		width : 130
	}, {
		dataField : "oid",
		headerText : "oid",
		dataType : "string",
		visible : false
	} ]

	const props = {
		rowIdField : "oid",
		headerHeight : 30,
		rowHeight : 30,
		showRowNumColumn : true,
		rowNumHeaderText : "번호",
		showRowCheckColumn : true, // 체크 박스 출력
		fixedColumnCount : 7,

		// 컨텍스트 메뉴 사용
		useContextMenu : true,

		// 컨텍스트 메뉴 아이템들
		contextMenuItems : [ {
			label : "BOM 비교",
			callback : contextHandler
		}, {
			label : "CONFIG 비교",
			callback : contextHandler
		}, {
			label : "도면일람표 비교",
			callback : contextHandler
		} ],
	};

	myGridID = AUIGrid.create("#grid_wrap", columns, props);
	// LazyLoading 바인딩
	AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);

	function contextHandler(event) {

		let checkedItems = AUIGrid.getCheckedRowItems(myGridID);
		if (checkedItems.length < 2) {
			alert("2개 이상의 작번을 선택하세요.");
			return false;
		}

		switch (event.contextIndex) {
		case 0:
			alert(event.value + ", rowIndex : " + event.rowIndex + ", columnIndex : " + event.columnIndex);
			break;

		case 2:
			// 내보내기 실행
			AUIGrid.exportToXlsx(event.pid);
			break;

		case 3:
			window.open("https://www.google.com", "_blank");
			break;
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

	$(function() {
		$("#searchBtn").click(function() {
			loadGridData();
		})

		// 그리드 행 추가
		$("#addRowBtn").click(function() {
			let item = new Object();
			item.createDate = new Date();
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
			console.log(params);
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

		check("install");
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