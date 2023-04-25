<%@page import="net.sf.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
JSONArray data = (JSONArray) request.getAttribute("data");
String invoke = (String) request.getAttribute("invoke");
String title = "";
if("m".equals(invoke)) {
	title = "기계";
} else if("e".equals(invoke)) {
	title = "전기";
}
%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				<%=title %> 수배표 정보
			</div>
		</td>
		<td class="right">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>
<div id="grid_wrap4" style="height: 870px; border-top: 1px solid #3180c3;"></div>
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