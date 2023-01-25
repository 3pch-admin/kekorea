<%@page import="e3ps.admin.sheetvariable.beans.ItemsColumnData"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.admin.sheetvariable.Category"%>
<%@page import="e3ps.admin.commonCode.CommonCodeType"%>
<%@page import="org.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
Category category = (Category) request.getAttribute("category"); 
String oid = (String) request.getAttribute("oid");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<!-- auigrid -->
<%@include file="/jsp/include/auigrid.jsp"%>
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
				<input type="button" value="닫기" class="blueBtn" id="closeBtn" title="닫기">
			</td>
		</tr>
	</table>
	<div id="grid_wrap" style="height: 740px; border-top: 1px solid #3180c3;"></div>
</body>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "cname",
		headerText : "카테고리 명",
		dataType : "string",
		width : 350,
		editable : false,
		cellMerge : true,
	}, {
		dataField : "name",
		headerText : "아이템 명",
		dataType : "string",
	}, {
		dataField : "sort",
		headerText : "아이템 정렬 순서",
		dataType : "numeric",
		formatString : "###0",
		width : 140,
		editRenderer : {
			type : "InputEditRenderer",
			onlyNumeric : true, // 0~9만 입력가능
		},
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
		fillColumnSizeMode : true, // 화면 꽉채우기
		editable : true,
		showStateColumn : true,
		enableCellMerge : true,
		cellMergePolicy : "withNull",
		softRemoveRowMode : false,
	};

	myGridID = AUIGrid.create("#grid_wrap", columns, props);

	// LazyLoading 바인딩
	AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);

	function loadGridData() {
		let params = new Object();
		params.oid = "<%=oid%>";
		let url = getCallUrl("/items/list");
		AUIGrid.showAjaxLoader(myGridID);
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			$("input[name=sessionid]").val(data.sessionid);
			$("input[name=curPage]").val(data.curPage);
			AUIGrid.setGridData(myGridID, data.list);
			close();
			opener.loadGridData();
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
		
		$("#closeBtn").click(function() {
			self.close();
		})
		
		// 그리드 행 추가
		$("#addRowBtn").click(function() {
			let item = new Object();
			item.cname = "<%=category.getName()%>";
			AUIGrid.addRow(myGridID, item, "last");
		})

		$("#saveBtn").click(function() {
			let cnameValid = AUIGrid.validateGridData(myGridID, [ "iname" ], "아이템 명을 입력하세요.");
			if (!cnameValid) {
				return false;
			}

			let csortValid = AUIGrid.validateGridData(myGridID, [ "isort" ], "아이템 정렬 순서를 입력하세요.");
			if (!csortValid) {
				return false;
			}
			let addRows = AUIGrid.getAddedRowItems(myGridID);
			let removeRows = AUIGrid.getRemovedItems(myGridID);
			let editRows = AUIGrid.getEditedRowItems(myGridID);
			let params = new Object();
			let url = getCallUrl("/items/create");
			params.addRows = addRows;
			params.removeRows = removeRows;
			params.editRows = editRows;
			params.coid = "<%=oid%>";
			open();
			call(url, params, function(data) {
				alert(data.msg);
				if (data.result) {
					loadGridData();
				}
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