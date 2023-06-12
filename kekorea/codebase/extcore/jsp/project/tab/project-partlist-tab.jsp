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
<style type="text/css">
.cell1 {
	background: #d3ffa8;
	font-weight: bold;
}

.cell2 {
	background: #cacaff;
	font-weight: bold;
}

.cell3 {
	background: #ffacac;
	font-weight: bold;
}

.cell4 {
	background: #fdf4bb;
	font-weight: bold;
}


</style>
</head>
<body>
	<form>
		<div id="grid_wrap4" style="height: 780px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID4;
			const data =
		<%=data%>
			const columns4 = [ {
				dataField : "engType",
				headerText : "설계구분",
				dataType : "string",
				width : 100,
				filter : {
					showIcon : true,
					inline : true
				},
				styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
					if (value === "기계_1차_수배") {
						return "cell1";
					} else if (value === "기계_2차_수배") {
						return "cell2";
					} else if(value === "전기_1차_수배") {
						return "cell3";
					} else if(value === "전기_2차_수배") {
						return "cell4";
					}
					return null;
				}
			}, {
				dataField : "lotNo",
				headerText : "LOT_NO",
				dataType : "numeric",
				width : 80,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "unitName",
				headerText : "UNIT NAME",
				dataType : "string",
				width : 120,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "partNo",
				headerText : "부품번호",
				dataType : "string",
				width : 100,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "partName",
				headerText : "부품명",
				dataType : "string",
				width : 200,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "standard",
				headerText : "규격",
				dataType : "string",
				width : 250,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "maker",
				headerText : "MAKER",
				dataType : "string",
				width : 130,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "customer",
				headerText : "거래처",
				dataType : "string",
				width : 130,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "quantity",
				headerText : "수량",
				dataType : "numeric",
				width : 60,
				filter : {
					showIcon : true,
					inline : false,
					type : "numeric"
				},
			}, {
				dataField : "unit",
				headerText : "단위",
				dataType : "string",
				width : 80,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "price",
				headerText : "단가",
				dataType : "numeric",
				width : 120,
				filter : {
					showIcon : true,
					inline : false,
					type : "numeric"
				},
			}, {
				dataField : "currency",
				headerText : "화폐",
				dataType : "string",
				width : 60,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "won",
				headerText : "원화금액",
				dataType : "numeric",
				width : 120,
				filter : {
					showIcon : true,
					inline : false,
					type : "numeric"
				},
			}, {
				dataField : "partListDate_txt",
				headerText : "수배일자",
				dataType : "string",
				width : 100,
				filter : {
					showIcon : false,
					inline : false
				},
			}, {
				dataField : "exchangeRate",
				headerText : "환율",
				dataType : "numeric",
				width : 80,
				formatString : "#,##0.0000",
				filter : {
					showIcon : false,
					inline : false
				},
			}, {
				dataField : "referDrawing",
				headerText : "참고도면",
				dataType : "string",
				width : 120,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "classification",
				headerText : "조달구분",
				dataType : "string",
				width : 120,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "note",
				headerText : "비고",
				dataType : "string",
				width : 250,
				filter : {
					showIcon : true,
					inline : true
				},
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
					enableFilter : true,
					showInlineFilter : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
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