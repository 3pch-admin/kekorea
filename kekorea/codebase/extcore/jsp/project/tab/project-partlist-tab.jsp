<%@page import="e3ps.bom.partlist.service.PartlistHelper"%>
<%@page import="java.util.Map"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.korea.cip.dto.CipDTO"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
String invoke = request.getParameter("invoke");
JSONArray data = (JSONArray) request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>    
</head>
<body>
	<form>
		<div id="grid_wrap4" style="height: 780px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID4;
			const data = <%=data%>
			const columns4 = [ {
				dataField : "engType",
				headerText : "설계구분",
				dataType : "string",
				width : 100,
			}, {
				dataField : "lotNo",
				headerText : "LOT_NO",
				dataType : "numeric",
				width : 80,
			}, {
				dataField : "unitName",
				headerText : "UNIT NAME",
				dataType : "string",
				width : 120
			}, {
				dataField : "partNo",
				headerText : "부품번호",
				dataType : "string",
				width : 100,
			}, {
				dataField : "partName",
				headerText : "부품명",
				dataType : "string",
				width : 200,
			}, {
				dataField : "standard",
				headerText : "규격",
				dataType : "string",
				width : 250,
			}, {
				dataField : "maker",
				headerText : "MAKER",
				dataType : "string",
				width : 130,
			}, {
				dataField : "customer",
				headerText : "거래처",
				dataType : "string",
				width : 130,
			}, {
				dataField : "quantity",
				headerText : "수량",
				dataType : "numeric",
				width : 60,
			}, {
				dataField : "unit",
				headerText : "단위",
				dataType : "string",
				width : 80,
			}, {
				dataField : "price",
				headerText : "단가",
				dataType : "numeric",
				width : 120,
			}, {
				dataField : "currency",
				headerText : "화폐",
				dataType : "string",
				width : 60,
			}, {
				dataField : "won",
				headerText : "원화금액",
				dataType : "numeric",
				width : 120,
			}, {
				dataField : "partListDate_txt",
				headerText : "수배일자",
				dataType : "string",
				width : 100,
			}, {
				dataField : "exchangeRate",
				headerText : "환율",
				dataType : "numeric",
				width : 80,
				formatString : "#,##0.0000"
			}, {
				dataField : "referDrawing",
				headerText : "참고도면",
				dataType : "string",
				width : 120,
			}, {
				dataField : "classification",
				headerText : "조달구분",
				dataType : "string",
				width : 120,
			}, {
				dataField : "note",
				headerText : "비고",
				dataType : "string",
				width : 250,
			} ];

			const footerLayout4 = [ {
				labelText : "∑",
				positionField : "#base",
			}, {
				dataField : "lotNo",
				positionField : "lotNo",
				style : "right",
				colSpan : 7,
				labelFunction : function(value, columnValues, footerValues) {
					return "수배표 수량 합계 금액";
				}
			}, {
				dataField : "quantity",
				positionField : "quantity",
				operation : "SUM",
				dataType : "numeric",
			}, {
				dataField : "unit",
				positionField : "unit",
				style : "right",
				colSpan : 3,
				labelFunction : function(value, columnValues, footerValues) {
					return "수배표 수량 합계 금액";
				}
			}, {
				dataField : "won",
				positionField : "won",
				operation : "SUM",
				dataType : "numeric",
				formatString : "#,##0",
			}, {
				dataField : "partListDate",
				positionField : "partListDate",
				colSpan : "5",
			}, ];

			function createAUIGrid4(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : true,
					rowNumHeaderText : "번호",
					showAutoNoDataMessage : false,
					selectionMode : "multipleCells",
					showFooter : true,
					footerPosition : "top",
				};
				myGridID4 = AUIGrid.create("#grid_wrap4", columnLayout, props);
				AUIGrid.setFooter(myGridID4, footerLayout4);
				AUIGrid.setGridData(myGridID4, data);
			}



			document.addEventListener("DOMContentLoaded", function() {
				createAUIGrid4(columns4);
				AUIGrid.resize(myGridID4);
			})

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID4);
			});
		</script>
	</form>
</body>
</html>