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
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=1010"></script>
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
			<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('partlist-compare');">
			<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('partlist-compare');">
		</td>
		<td class="right">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>

<div id="grid_wrap" style="height: 800px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let myGridID;
	const data =
<%=data%>
	function _layout() {
		return [ {
			dataField : "lotNo",
			headerText : "LOT_NO",
			dataType : "numeric",
			width : 80,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "name",
			headerText : "DRAWING TITLE",
			dataType : "string",
			width : 120,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "number",
			headerText : "DWG. NO",
			dataType : "string",
			width : 100,
			filter : {
				showIcon : true,
				inline : true
			},
		},{
			headerText : "<%=p1.getKekNumber()%>",
			children : [ {
				dataField : "rev1",
				headerText : "REV",
				dataType : "string",
				width : 100,
				labelFunction : function(rowIndex, columnIndex, value, headerText, item, dataField, cItem) {
					if(item.rev1 === undefined) {
						return "";
					}
					return value;
				},
				filter : {
					showIcon : true,
					inline : true
				},
			} ]
		}, 
		<%int i = 2;
for (Project project : destList) {%>
				{
					headerText : "<%=project.getKekNumber()%>",
					children : [ {
						dataField : "rev<%=i%>",
						headerText : "REV",
						dataType : "string",
						width : 100,
						labelFunction : function(rowIndex, columnIndex, value, headerText, item, dataField, cItem) {
							if(item.rev<%=i%> === undefined) {
								return "";
							}
							return value;
						},
						styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
							const rev1 = item.rev1;
							if (value !== rev1) {
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
				<%i++;
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
			enableRightDownFocus : true,
		}
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		AUIGrid.setGridData(myGridID, data);
	}

	document.addEventListener("DOMContentLoaded", function() {
		const columns = loadColumnLayout("partlist-compare");
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>
