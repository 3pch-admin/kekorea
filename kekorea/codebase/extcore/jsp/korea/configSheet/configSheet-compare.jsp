<%@page import="java.util.Map"%>
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
ArrayList<Map<String, String>> fixedList = (ArrayList<Map<String, String>>) request.getAttribute("fixedList");
%>
<%@include file="/extcore/include/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=11210"></script>
<style type="text/css">
.compare {
	font-weight: bold;
	color: red;
}

.row1 {
	background-color: #99CCFF;
}

.row2 {
	background-color: #FFCCFF;
}

.row3 {
	background-color: #CCFFCC;
}

.row4 {
	background-color: #FFFFCC;
}

.row5 {
	background-color: #FFCC99;
}

.row6 {
	background-color: #CCCCFF;
}

.row7 {
	background-color: #99FF66;
}

.row8 {
	background-color: #CC99FF;
}

.row9 {
	background-color: #66CCFF;
}

.row10 {
	background-color: #CCFFCC;
}

.row11 {
	background-color: #FFCCFF;
}

.row12 {
	background-color: #FFFFCC;
}
</style>
<input type="hidden" name="oid" id="oid" value="<%=oid%>">
<input type="hidden" name="compareArr" id="compareArr" value="<%=compareArr%>">
<table class="button-table">
	<tr>
		<td class="right">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>


<div id="grid_wrap" style="height: 900px; border-top: 1px solid #3180c3;"></div>
<%@include file="/extcore/jsp/common/aui/aui-context.jsp"%>
<script type="text/javascript">
	let myGridID;
	const data = <%=data%>
	function _layout() {
		return [ {
			dataField : "category_name",
			headerText : "",
			dataType : "string",
			width : 150,
			style : "aui-left",
			cellMerge : true,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "item_name",
			headerText : "",
			dataType : "string",
			width : 250,
			cellMerge : true,
			style : "aui-left",
			mergeRef : "category_code",
			mergePolicy : "restrict",
			filter : {
				showIcon : true,
				inline : true
			},
		},
		{
			headerText : "<%=p1.getKekNumber()%>",
			dataField : "P1",
			dataType : "string",
			width : 200,
			styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
				return "";
			},
			filter : {
				showIcon : true,
				inline : true
			},
		}, 
		<%int key = 2;
for (Project project : destList) {
	String dataField = "P" + key;%>
		{
			headerText : "<%=project.getKekNumber()%>",
			dataField : "<%=dataField%>",
			dataType : "string",
			width : 250,
			styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
				const P1 = item.P1;
				if(P1 !== value) {
					return "compare";
				}
				return "";
			},
			filter : {
				showIcon : true,
				inline : true
			},
		}, 
		<%key++;
}%>
		 ]
	}

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableFilter : true,
			selectionMode : "multipleCells",
			enableMovingColumn : true,
			showInlineFilter : true,
			useContextMenu : true,
			enableRightDownFocus : true,
			enableCellMerge : true,
			fixedColumnCount : 3,
// 			autoGridHeight : true
	rowStyleFunction : function(rowIndex, item) {
				const value = item.category_code;
				if (value === "CATEGORY_2") {
					return "row1";
				} else if (value === "CATEGORY_3") {
					return "row2";
				} else if (value === "CATEGORY_4") {
					return "row3";
				} else if (value === "CATEGORY_5") {
					return "row4";
				} else if (value === "CATEGORY_6") {
					return "row5";
				} else if (value === "CATEGORY_7") {
					return "row6";
				} else if (value === "CATEGORY_8" || value === "CATEGORY_9") {
					return "row7";
				} else if (value === "CATEGORY_10") {
					return "row8";
				} else if (value === "CATEGORY_11") {
					return "row9";
				} else if (value === "CATEGORY_12") {
					return "row4";
				} else if (value === "CATEGORY_13") {
					return "row10";
				} else if (value === "CATEGORY_14") {
					return "row11";
				} else if (value === "CATEGORY_15") {
					return "row12";
				}
				return "";
			}
		}
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		AUIGrid.setGridData(myGridID, data);
		AUIGrid.bind(myGridID, "contextMenu", auiContextMenuHandler);
		AUIGrid.bind(myGridID, "vScrollChange", function(event) {
			hideContextMenu();
		});
		AUIGrid.bind(myGridID, "hScrollChange", function(event) {
			hideContextMenu();
		});
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
	})

	document.addEventListener("click", function(event) {
		hideContextMenu();
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>
