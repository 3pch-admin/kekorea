<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
			<th></th>
			<td></td>
			<th></th>
			<td></td>
		</tr>
		<tr>
			<th></th>
			<td></td>
			<th></th>
			<td></td>
		</tr>
	</table>
	<table class="btn_table">
		<tr>
			<td class="left">
				<input type="button" value="등록" class="blueBtn" id="createBtn" title="등록">
			</td>
			<td class="right">
				<input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회">
			</td>
		</tr>
	</table>
	<div id="grid_wrap" style="height: 665px; border-top: 1px solid #3180c3;"></div>
</body>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "",
		headerText : "",
		dataType : "string",
		width : 100
	}, {
		dataField : "",
		headerText : "",
		dataType : "string",
		width : 100
	}, {
		dataField : "",
		headerText : "",
		dataType : "string",
		width : 100
	}, {
		dataField : "",
		headerText : "",
		dataType : "string",
		width : 100
	}, {
		dataField : "",
		headerText : "",
		dataType : "string",
		width : 100
	}, {
		dataField : "",
		headerText : "",
		dataType : "string",
		width : 100
	}, {
		dataField : "",
		headerText : "",
		dataType : "string",
		width : 100
	}, {
		dataField : "",
		headerText : "",
		dataType : "string",
		width : 100
	}, {
		dataField : "oid",
		headerText : "oid",
		dataType : "string",
		visible : false
	} ]
	
	function createAUIGrid(columnLayout) {
		const props = {
			rowIdField : "rowId",
			headerHeight : 30,
			rowHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호"
		};
		
		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		loadGridData();
	};
	
	function loadGridData() {
		let params = new Object();
		let url = getCallUrl("/KDrawing/list");
		AUIGrid.showAjaxLoader(myGridID);
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			$("input[name=sessionid]").val(data.sessionid);
			$("input[name=curPage]").val(data.curPage);
			AUIGrid.setGridData(myGridID, data.list);
			parent.close();
		})
	}
	
	$(function() {

		createAUIGrid(columns);

		$("#searchBtn").click(function() {
			loadGridData();
		})

		// 등록페이지
// 		$("#createBtn").click(function() {
// 			let url = getCallUrl("/KDrawing/create");
// 			popup(url, 1200, 760);
// 		})

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