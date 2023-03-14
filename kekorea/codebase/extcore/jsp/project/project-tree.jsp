<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<!-- CSS 공통 모듈 -->
<%@include file="/extcore/include/css.jsp"%>
<!-- 스크립트 공통 모듈 -->
<%@include file="/extcore/include/script.jsp"%>
<!-- AUIGrid -->
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
					enableFilter : true, // 필터 사용 여부
					showInlineFilter : true,
					displayTreeOpen : true
				}
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				load();
				AUIGrid.bind(myGridID, "selectionChange", auiGridSelectionChangeHandler);
			}

			let timerId = null;
			function auiGridSelectionChangeHandler(event) {
				// 500ms 보다 빠르게 그리드 선택자가 변경된다면 데이터 요청 안함
				if (timerId) {
					clearTimeout(timerId);
				}

				timerId = setTimeout(function() {
					// 선택 대표 셀 정보 
					const primeCell = event.primeCell;
					// 대표 셀에 대한 전체 행 아이템
					const rowItem = primeCell.item;
					const oid = rowItem.oid; // oid로 할지 location 으로 할지...
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
				AUIGrid.resize(myGridID); // 특이사항
			});
		</script>
	</form>
</body>
</html>