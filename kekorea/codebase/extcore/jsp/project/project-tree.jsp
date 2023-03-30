<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/include/css.jsp"%>
<%@include file="/extcore/include/script.jsp"%>
<%@include file="/extcore/include/auigrid.jsp"%>
</head>
<body>
	<form>
		<div id="grid_wrap" style="height: 800px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			const columns = [ {
				dataField : "name",
				headerText : "태스크명",
				dataType : "string",
				filter : {
					showIcon : true,
					inline : true
				},
			} ]

			function createAUIGrid(columnLayout) {
				const props = {
					rowIdField : "oid",
					headerHeight : 30,
					rowHeight : 30,
					showRowNumColumn : true,
					rowNumHeaderText : "번호",
					fillColumnSizeMode : true,
					selectionMode : "singleRow",
					enableFilter : true, 
					showInlineFilter : true,
					displayTreeOpen : true
				}
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				load();
				AUIGrid.bind(myGridID, "selectionChange", auiGridSelectionChangeHandler);
			}

			let timerId = null;
			function auiGridSelectionChangeHandler(event) {
				if (timerId) {
					clearTimeout(timerId);
				}

				timerId = setTimeout(function() {
					const primeCell = event.primeCell;
					const rowItem = primeCell.item;
					const oid = rowItem.oid; 
				}, 500);
			}

			function load() {
				const url = getCallUrl("/project/load?oid=<%=oid%>");
				call(url, null, function(data) {
					AUIGrid.setGridData(myGridID, data.list);
				}, "GET");
			}

			document.addEventListener("DOMContentLoaded", function() {
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
			});

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID); 
			});
		</script>
	</form>
</body>
</html>