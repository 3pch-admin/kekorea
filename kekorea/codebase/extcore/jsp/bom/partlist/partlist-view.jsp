<%@page import="org.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
JSONArray data = (JSONArray) request.getAttribute("data");
%>
<!-- AUIGrid -->
<%@include file="/extcore/include/auigrid.jsp"%>
<table class="button-table">
	<tr>
		<td class="right">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>
<!-- 그리드 리스트 -->
<div id="grid_wrap" style="height: 665px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let myGridID;
	const data = <%=data%>
	const columns = [ {
		dataField : "lotNo",
		headerText : "LOT_NO",
		dataType : "string",
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
		style : "underline",
		width : 130,
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
		postfix : "개"
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
		postfix : "원"
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
		postfix : "원"
	}, {
		dataField : "partListDate",
		headerText : "수배일자",
		dataType : "date",
		formatString : "yyyy-mm-dd",
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
		colSpan : 7, // 자신을 포함하여 3개의 푸터를 가로 병합함.
		labelFunction : function(value, columnValues, footerValues) {
			return "수배표 수량 합계 금액";
		}
	}, {
		dataField : "quantity",
		positionField : "quantity",
		operation : "SUM",
		dataType : "numeric",
		postfix : "개"
	},{
		dataField : "unit",
		positionField : "unit",
		style : "right",
		colSpan : 3, // 자신을 포함하여 3개의 푸터를 가로 병합함.
		labelFunction : function(value, columnValues, footerValues) {
			return "수배표 수량 합계 금액";
		}
	}, {
		dataField : "won",
		positionField : "won",
		operation : "SUM",
		dataType : "numeric",
		formatString : "#,##0",
		postfix : "원"
	}, {
		dataField : "partListDate",
		positionField : "partListDate",
		colSpan : "5",
	}, ];

	function createAUIGrid(columnLayout) {
		const props = {
			// 그리드 공통속성 시작
			headerHeight : 30, // 헤더높이
			rowHeight : 30, // 행 높이
			showRowNumColumn : true, // 번호 행 출력 여부
			rowNumHeaderText : "번호", // 번호 행 텍스트 설정
			selectionMode : "multipleCells",
			showFooter : true,
			footerPosition : "top",
		// 그리드 공통속성 끝
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		AUIGrid.setFooter(myGridID, footerLayout);
	}
	// jquery 삭제를 해가는 쪽으로 한다..
	document.addEventListener("DOMContentLoaded", function() {
		// DOM이 로드된 후 실행할 코드 작성
		createAUIGrid(columns);
		AUIGrid.setGridData(myGridID, data);
		AUIGrid.resize(myGridID);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>
