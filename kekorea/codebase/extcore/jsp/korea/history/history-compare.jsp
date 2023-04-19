<%@page import="wt.org.WTUser"%>
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
WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
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
<input type="hidden" name="sessionName" id="sessionName" value="<%=sessionUser.getFullName()%>">
<input type="hidden" name="sessionId" id="sessionId" value="<%=sessionUser.getName()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();">
			<!-- 			<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('partlist-compare');"> -->
			<!-- 			<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('partlist-compare');"> -->
		</td>
		<td class="right">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>


<div id="grid_wrap" style="height: 100px; border-top: 1px solid #3180c3;"></div>
<%-- <%@include file="/extcore/jsp/common/aui/aui-context.jsp"%> --%>
<script type="text/javascript">
	let myGridID;
	const data = <%=data%>
	function _layout() {
		return [ {
			dataField : "key",
			headerText : "",
			dataType : "string",
			width : 250,
			style : "aui-left",
			filter : {
				showIcon : true,
				inline : true
			},
		}, 
		<%int i = 1;
for (Project project : destList) {
	String dataField = String.valueOf(project.getPersistInfo().getObjectIdentifier().getId());%>
		{
			headerText : "<%=project.getKekNumber()%>",
			dataField : "<%=dataField%>",
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
			useContextMenu : true,
			enableRightDownFocus : true,
			fixedColumnCount : 1,
			autoGridHeight : true
		}
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		AUIGrid.setGridData(myGridID, data);
// 		AUIGrid.bind(myGridID, "contextMenu", auiContextMenuHandler);
// 		AUIGrid.bind(myGridID, "vScrollChange", function(event) {
// 			hideContextMenu();
// 		});
// 		AUIGrid.bind(myGridID, "hScrollChange", function(event) {
// 			hideContextMenu();
// 		});
	}

	
	function exportExcel() {
		const exceptColumnFields = [  ];
		const sessionName = document.getElementById("sessionName").value;
		exportToExcel("이력관리 비교", "이력관리", "이력관리 비교", exceptColumnFields, sessionName);
	}

	
	document.addEventListener("DOMContentLoaded", function() {
		const columns = loadColumnLayout("tbom-compare");
// 		const contenxtHeader = genColumnHtml(columns);
// 		$("#h_item_ul").append(contenxtHeader);
// 		$("#headerMenu").menu({
// 			select : headerMenuSelectHandler
// 		});
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
	})

// 	document.addEventListener("click", function(event) {
// 		hideContextMenu();
// 	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>
