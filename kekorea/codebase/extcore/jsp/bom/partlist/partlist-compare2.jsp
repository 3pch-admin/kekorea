<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.bom.partlist.dto.PartListDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
JSONArray data = (JSONArray) request.getAttribute("data");
String oid = (String) request.getAttribute("oid");
String _oid = (String) request.getAttribute("_oid");
%>
<%@include file="/extcore/include/auigrid.jsp"%>
<style type="text/css">
.compare {
	background-color: yellow;
	color: red;
	font-weight: bold;
}
</style>
<input type="hidden" name="oid" id="oid" value="<%=oid%>">
<input type="hidden" name="_oid" id="_oid" value="<%=_oid%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="pretty p-default p-curve">
				<input type="radio" name="compareKey" value="lotNo+partNo" checked="checked">
				<div class="state p-danger-o">
					<label>LOT+부품번호</label>
				</div>
			</div>
			<div class="pretty p-default p-curve">
				<input type="radio" name="compareKey" value="lotNo+partNo+quantity">
				<div class="state p-danger-o">
					<label>LOT+부품번호+수량</label>
				</div>
			</div>
		</td>
		<td class="right">
			<select name="sort" id="sort" class="width-100">
				<option value="">선택</option>
				<option value="sort">생성순</option>
				<option value="partNo">부품번호</option>
				<option value="lotNo">LOT</option>
			</select>
			<input type="button" value="비교" title="비교" class="red" onclick="_compare('');">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>


<div id="grid_wrap" style="height: 800px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let myGridID;
	const data =
<%=data%>
	const columns = [ {
		dataField : "lotNo1",
		headerText : "LOT1",
		dataType : "string",
		width : 80,
		styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
			const lotNo2 = item.lotNo2;
			if (value !== lotNo2) {
				return "compare";
			}
			return "";
		}
	}, {
		dataField : "lotNo2",
		headerText : "LOT2",
		dataType : "string",
		width : 80,
		styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
			const lotNo1 = item.lotNo1;
			if (value !== lotNo1) {
				return "compare";
			}
			return "";
		}
	}, {
		dataField : "unitName",
		headerText : "UNIT NAME",
		dataType : "string",
		width : 120,
	}, {
		dataField : "partNo1",
		headerText : "부품번호1",
		dataType : "string",
		width : 100,
		styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
			const partNo2 = item.partNo2;
			if (value !== partNo2) {
				return "compare";
			}
			return "";
		}
	}, {
		dataField : "partNo2",
		headerText : "부품번호2",
		dataType : "string",
		width : 100,
		styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
			const partNo1 = item.partNo1;
			if (value !== partNo1) {
				return "compare";
			}
			return "";
		}
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
		dataField : "quantity1",
		headerText : "수량1",
		dataType : "numeric",
		width : 60,
		postfix : "개",
		styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
			const quantity2 = item.quantity2;
			if (value !== quantity2) {
				return "compare";
			}
			return "";
		}
	}, {
		dataField : "quantity2",
		headerText : "수량2",
		dataType : "numeric",
		width : 60,
		postfix : "개",
		styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
			const quantity1 = item.quantity1;
			if (value !== quantity1) {
				return "compare";
			}
			return "";
		}
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
		dataField : "won1",
		headerText : "원화금액1",
		dataType : "numeric",
		width : 120,
		postfix : "원",
		styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
			const won2 = item.won2;
			if (value !== won2) {
				return "compare";
			}
			return "";
		}
	}, {
		dataField : "won2",
		headerText : "원화금액2",
		dataType : "numeric",
		width : 120,
		postfix : "원",
		styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
			const won1 = item.won1;
			if (value !== won1) {
				return "compare";
			}
			return "";
		}
	}, {
		dataField : "partListDate_txt",
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
	} ]

	const footerLayout = [ {
		labelText : "∑",
		positionField : "#base",
	}, {
		dataField : "lotNo1",
		positionField : "lotNo1",
		style : "right",
		colSpan : 9,
		labelFunction : function(value, columnValues, footerValues) {
			return "수배표 수량 합계";
		}
	}, {
		dataField : "quantity1",
		positionField : "quantity1",
		operation : "SUM",
		dataType : "numeric",
		postfix : "개"
	}, {
		dataField : "quantity2",
		positionField : "quantity2",
		operation : "SUM",
		dataType : "numeric",
		postfix : "개"
	}, {
		dataField : "unit",
		positionField : "unit",
		dataType : "numeric",
		postfix : "개",
		labelFunction : function(value, columnValues, footerValues) {
			return footerValues[2] - footerValues[3];
		},
	}, {
		dataField : "price",
		positionField : "price",
		style : "right",
		colSpan : 2,
		labelFunction : function(value, columnValues, footerValues) {
			return "수배표 수량 합계 금액";
		}
	}, {
		dataField : "won1",
		positionField : "won1",
		operation : "SUM",
		dataType : "numeric",
		formatString : "#,##0",
		postfix : "원"
	}, {
		dataField : "won2",
		positionField : "won2",
		operation : "SUM",
		dataType : "numeric",
		formatString : "#,##0",
		postfix : "원"
	}, {
		dataField : "partListDate_txt",
		positionField : "partListDate_txt",
		dataType : "numeric",
		formatString : "#,##0",
		labelFunction : function(value, columnValues, footerValues) {
			console.log(footerValues);
			return footerValues[6] - footerValues[7];
		},
	}, ];

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			rowHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			displayTreeOpen : true,
			enableSorting : false,
			showFooter : true,
			footerPosition : "top",
		}
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		AUIGrid.setFooter(myGridID, footerLayout);
		AUIGrid.setGridData(myGridID, data);
	}
</script>

<script type="text/javascript">
	function _compare() {
		if (!confirm("선택한 기준으로 데이터를 다시 비교합니다.")) {
			return false;
		}
		

		const oid = document.getElementById("oid").value;
		const _oid = document.getElementById("_oid").value;
		const compareKey = document.querySelector("input[name=compareKey]:checked").value;
		const sort = document.getElementById("sort").value;
		const url = getCallUrl("/partlist/compare");
		const params = new Object();
		params.oid = oid;
		params._oid = _oid;
		params.compareKey = compareKey;
		params.sort = sort;
		AUIGrid.showAjaxLoader(myGridID);
		openLayer();
		call(url, params, function(data) {
			if (data.result) {
				AUIGrid.removeAjaxLoader(myGridID);
				AUIGrid.setGridData(myGridID, data.list);
				closeLayer();
			}
		})
	}

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columns);
		selectbox("sort");
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>