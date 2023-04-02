<%@page import="e3ps.project.Project"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.bom.partlist.dto.PartListDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
Project project = (Project) request.getAttribute("project");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
JSONArray integratedData = (JSONArray) request.getAttribute("integratedData");
%>
<%@include file="/extcore/include/auigrid.jsp"%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				수배표 통합 정보 (<%=project.getKekNumber() %>)
			</div>
		</td>
		<td class="right">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>
<div id="grid_wrap" style="height: 880px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let myGridID;
	const list =
<%=integratedData%>
	const columns = [ {
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

	const footerLayout = [ {
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

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			selectionMode : "multipleCells",
			showFooter : true,
			showAutoNoDataMessage : false,
			footerPosition : "top",
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		AUIGrid.setFooter(myGridID, footerLayout);
		AUIGrid.setGridData(myGridID, list);
	}
</script>
<script type="text/javascript">
	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columns);
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>
