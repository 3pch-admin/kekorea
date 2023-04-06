<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.project.Project"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.bom.partlist.dto.PartListDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
JSONArray data = (JSONArray) request.getAttribute("data");
Project p1 = (Project) request.getAttribute("p1");
ArrayList<Project> destList = (ArrayList<Project>) request.getAttribute("destList");
String oid = (String) request.getAttribute("oid");
String compareArr = (String) request.getAttribute("compareArr");
%>
<%@include file="/extcore/include/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=11210"></script>
<style type="text/css">
.compare {
	background-color: yellow;
	color: red;
	font-weight: bold;
}
</style>
<input type="hidden" name="oid" id="oid" value="<%=oid%>">
<input type="hidden" name="compareArr" id="compareArr" value="<%=compareArr%>">
<table class="button-table">
	<tr>
		<td class="left">
			<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('tbom-compare');">
			<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('tbom-compare');">
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
	const data = <%=data%>
	function _layout() {
		return [ {
			dataField : "lotNo",
			headerText : "LOT",
			dataType : "numeric",
			width : 100,
			formatString : "###0",
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "code",
			headerText : "중간코드",
			dataType : "string",
			width : 130,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "keNumber",
			headerText : "부품번호",
			dataType : "string",
			width : 120,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			headerText : "<%=p1.getKekNumber()%>",
			children : [ {
				dataField : "qty1",
				headerText : "수량",
				dataType : "numeric",
				width : 100,
				filter : {
					showIcon : true,
					inline : true
				},
			} ]
		}, 
		<%
			int i = 2;
			for(Project project : destList) {
		%>
		{
			headerText : "<%=project.getKekNumber()%>",
			children : [ {
				dataField : "qty<%=i%>",
				headerText : "수량",
				dataType : "numeric",
				width : 100,
				labelFunction : function(rowIndex, columnIndex, value, headerText, item, dataField, cItem) {
					if(item.qty<%=i%> === undefined) {
						return 0;
					}
					return value;
				},
				styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
					const qty1 = item.qty1;
					if (value !== qty1) {
						return "compare";
					}
					return "";
				},
				filter : {
					showIcon : true,
					inline : true
				},
			} ]
		}, 
		<%
			i++;
			}
		%>
		{
			dataField : "name",
			headerText : "부품명",
			dataType : "string",
			width : 200,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "model",
			headerText : "KokusaiModel",
			dataType : "string",
			width : 200,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "unit",
			headerText : "UNIT",
			dataType : "string",
			width : 130,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "provide",
			headerText : "PROVIDE",
			dataType : "string",
			width : 130,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "discontinue",
			headerText : "DISCONTINUE",
			dataType : "string",
			width : 200,
			filter : {
				showIcon : true,
				inline : true
			},
		} ]
	}

// 	const footerLayout = [ {
// 		labelText : "∑",
// 		positionField : "#base",
// 	}, {
// 		dataField : "qty1",
// 		positionField : "qty1",
// 		operation : "SUM",
// 		dataType : "numeric",
// 		postfix : "개"
// 	}, {
// 		dataField : "qty2",
// 		positionField : "qty2",
// 		operation : "SUM",
// 		dataType : "numeric",
// 		postfix : "개"
// 	}, ];

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
// 			showFooter : true,
			enableFilter : true,
			selectionMode : "multipleCells",
			enableMovingColumn : true,
			showInlineFilter : true,
			useContextMenu : true,
			enableRightDownFocus : true,
// 			footerPosition : "top",
		}
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
// 		AUIGrid.setFooter(myGridID, footerLayout);
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
		const columns = loadColumnLayout("tbom-compare");
		const contenxtHeader = genColumnHtml(columns);
		$("#h_item_ul").append(contenxtHeader);
		$("#headerMenu").menu({
			select : headerMenuSelectHandler
		});
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
		selectbox("sort");
	})

	document.addEventListener("click", function(event) {
		hideContextMenu();
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>
