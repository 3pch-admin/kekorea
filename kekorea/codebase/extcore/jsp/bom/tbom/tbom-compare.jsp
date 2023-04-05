<%@page import="e3ps.project.Project"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.bom.partlist.dto.PartListDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
JSONArray data = (JSONArray) request.getAttribute("data");
Project p1 = (Project) request.getAttribute("p1");
Project p2 = (Project) request.getAttribute("p2");
String oid = (String) request.getAttribute("oid");
String _oid = (String) request.getAttribute("_oid");
%>
<%@include file="/extcore/include/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=1010"></script>
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
			<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('tbom-compare');">
			<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('tbom-compare');">
			&nbsp;
			<div class="pretty p-switch">
				<input type="checkbox" name="dataView" value="lotNo" onclick="checkboxHandler(event);" checked="checked">
				<div class="state p-success">
					<label>
						<b>LOT</b>
					</label>
				</div>
			</div>
			<div class="pretty p-switch">
				<input type="checkbox" name="dataView" value="code" onclick="checkboxHandler(event);" checked="checked">
				<div class="state p-success">
					<label>
						<b>중간코드</b>
					</label>
				</div>
			</div>
			<div class="pretty p-switch">
				<input type="checkbox" name="dataView" value="name" onclick="checkboxHandler(event);" checked="checked">
				<div class="state p-success">
					<label>
						<b>부품명</b>
					</label>
				</div>
			</div>
			<div class="pretty p-switch">
				<input type="checkbox" name="dataView" value="model" onclick="checkboxHandler(event);" checked="checked">
				<div class="state p-success">
					<label>
						<b>KokusaiModel</b>
					</label>
				</div>
			</div>
			<div class="pretty p-switch">
				<input type="checkbox" name="dataView" value="unit" onclick="checkboxHandler(event);" checked="checked">
				<div class="state p-success">
					<label>
						<b>UNIT</b>
					</label>
				</div>
			</div>
			<div class="pretty p-switch">
				<input type="checkbox" name="dataView" value="provide" onclick="checkboxHandler(event);" checked="checked">
				<div class="state p-success">
					<label>
						<b>PROVIDE</b>
					</label>
				</div>
			</div>
			<div class="pretty p-switch">
				<input type="checkbox" name="dataView" value="discontinue" onclick="checkboxHandler(event);" checked="checked">
				<div class="state p-success">
					<label>
						<b>DISCONTINUE</b>
					</label>
				</div>
			</div>
		</td>
		<td class="right">
			<select name="sort" id="sort" class="width-200">
				<option value="">선택</option>
				<option value="sort">등록순</option>
				<option value="partNo">부품번호</option>
				<option value="lotNo">LOT</option>
			</select>
			<input type="button" value="비교" title="비교" class="red" onclick="_compare('');">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>


<div id="grid_wrap" style="height: 730px; border-top: 1px solid #3180c3;"></div>
<%@include file="/extcore/jsp/common/aui/aui-context.jsp"%>
<script type="text/javascript">
	let myGridID;
	const data =
<%=data%>
	const columns = [ {
		dataField : "lotNo",
		headerText : "LOT",
		dataType : "numeric",
		width : 100,
		formatString : "###0",
	}, {
		dataField : "code",
		headerText : "중간코드",
		dataType : "string",
		width : 130,
	}, {
		dataField : "keNumber1",
		headerText : "부품번호",
		dataType : "string",
		width : 120,
		styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
			const partNo2 = item.partNo2;
			if (value !== partNo2) {
				return "compare";
			}
			return "";
		}
	}, {
		headerText : "<%=p1.getKekNumber()%>",
		children : [ {
			dataField : "qty1",
			headerText : "수량",
			dataType : "numeric",
			width : 120,
			styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
				const qty2 = item.qty2;
				if (value !== qty2) {
					return "compare";
				}
				return "";
			}
		} ]
	}, {
		headerText : "<%=p1.getKekNumber()%>",
		children : [ {
			dataField : "qty2",
			headerText : "수량",
			dataType : "numeric",
			width : 120,
			styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
				const qty1 = item.qty1;
				if (value !== qty1) {
					return "compare";
				}
				return "";
			}
		} ]
	}, {
		dataField : "name",
		headerText : "부품명",
		dataType : "string",
		width : 200,
	}, {
		dataField : "model",
		headerText : "KokusaiModel",
		dataType : "string",
		width : 200,
	}, {
		dataField : "unit",
		headerText : "UNIT",
		dataType : "string",
		width : 130
	}, {
		dataField : "provide",
		headerText : "PROVIDE",
		dataType : "string",
		width : 130
	}, {
		dataField : "discontinue",
		headerText : "DISCONTINUE",
		dataType : "string",
		width : 200
	} ]

	const footerLayout = [ {
		labelText : "∑",
		positionField : "#base",
	}, {
		dataField : "qty1",
		positionField : "qty1",
		operation : "SUM",
		dataType : "numeric",
		postfix : "개"
	}, {
		dataField : "qty2",
		positionField : "qty2",
		operation : "SUM",
		dataType : "numeric",
		postfix : "개"
	}, ];

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			showFooter : true,
			enableFilter : true,
			selectionMode : "multipleCells",
			enableMovingColumn : true,
			showInlineFilter : true,
			useContextMenu : true,
			enableRightDownFocus : true,
			footerPosition : "top",
		}
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		AUIGrid.setFooter(myGridID, footerLayout);
		AUIGrid.setGridData(myGridID, data);
		AUIGrid.bind(myGridID, "contextMenu", auiContextMenuHandler);
		AUIGrid.bind(myGridID, "vScrollChange", function(event) {
			hideContextMenu();
		});
		AUIGrid.bind(myGridID, "hScrollChange", function(event) {
			hideContextMenu();
		});
	}
</script>

<script type="text/javascript">
	function _compare() {
		if (!confirm("선택한 기준으로 데이터를 다시 비교합니다.")) {
			return false;
		}

		const oid = document.getElementById("oid").value;
		const _oid = document.getElementById("_oid").value;
		// 		const compareKey = document.querySelector("input[name=compareKey]:checked").value;
		const sort = document.getElementById("sort").value;
		const url = getCallUrl("/tbom/compare");
		const params = new Object();
		params.oid = oid;
		params._oid = _oid;
		// 		params.compareKey = compareKey;
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

	function checkboxHandler(event) {
		const target = event.target || event.srcElement;
		if (!target) {
			return;
		}
		const dataField = target.value;
		const checked = target.checked;

		if (checked) {
			AUIGrid.showColumnByDataField(myGridID, dataField);
		} else {
			AUIGrid.hideColumnByDataField(myGridID, dataField);
		}
	}

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columns);
		selectbox("sort");
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>
