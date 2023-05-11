<%@page import="wt.org.WTUser"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.project.Project"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.bom.partlist.dto.PartListDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
JSONArray data = (JSONArray) request.getAttribute("data");
System.out.println(data);
Project p1 = (Project) request.getAttribute("p1");
ArrayList<Project> destList = (ArrayList<Project>) request.getAttribute("destList");
String oid = (String) request.getAttribute("oid");
String compareArr = (String) request.getAttribute("compareArr");
ArrayList<Map<String, String>> fixedList = (ArrayList<Map<String, String>>) request.getAttribute("fixedList");
WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=11210"></script>
<style type="text/css">
.compare {
	background: #FFFF00;
	color: #FF0000;
	font-weight: bold;
}

.row1 {
	background-color: #fed7be;
	font-weight: bold;
}

.row2 {
	background-color: #FFCCFF;
	font-weight: bold;
}

.row3 {
	background-color: #CCFFCC;
	font-weight: bold;
}

.row4 {
	background-color: #FFFFCC;
	font-weight: bold;
}
.none {
	color: black;
	font-weight: bold;
	cursor: pointer;
	text-align: center !important;
}

.link {
	color: blue;
	font-weight: bold;
	cursor: pointer;
	text-align: center !important;
}

.link:hover {
	color: blue !important;
	text-decoration: underline;
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
		</td>
		<td class="right">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>


<div id="grid_wrap" style="height: 100px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let myGridID;
	const data = <%=data%>
	const columns = [ {
		dataField : "key",
		headerText : "",
		dataType : "string",
		width : 300,
		style : "aui-left",
		styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
			const id = item.id;
			if(!isNull(id)) {
				return "none";
			}
			return "";
		},
		filter : {
			showIcon : true,
			inline : true
		},
	}, 
	<%
	int i = 0;
	for (Project project : destList) {
		String dataField = String.valueOf(project.getPersistInfo().getObjectIdentifier().getId());
	%>
	{
		headerText : "<%=project.getKekNumber()%>",
		dataField : "P<%=i%>",
		dataType : "string",
		width : 200,
		styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
			const id = item.id;
			if(!isNull(id)) {
				return "link";
			}
			if(!isNull(value)) {
				if(item.P0 !== value) {
					return "compare";
				}
			}
			return "";
		},
		filter : {
			showIcon : true,
			inline : true
		},
	},
	<%
	i++;
	}
	%>
	]

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
			fixedColumnCount : 2,
			autoGridHeight : true,
			rowStyleFunction : function(rowIndex, item) {
				const value = item.key;
				if(value === "막종 / 막종상세") {
					return "row1";
				} else if(value === "고객사 / 설치장소") {
					return "row2";
				} else if(value === "KE 작번") {
					return "row3";
				} else if(value === "발행일") {
					return "row4";
				}
				return "";
			}
		}
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		AUIGrid.setGridData(myGridID, data);
		AUIGrid.bind(myGridID, "cellClick", auiCellClickHandler);
	}
	
	function auiCellClickHandler(event) {
		const dataField = event.dataField;
		const item = event.item;
		const oid = item.oid;
		const id = item.id;
		if(dataField !== "key" && !isNull(id)) {
			const url = getCallUrl("/project/info?oid="+oid);
			popup(url);
		}
	}

	
	function exportExcel() {
		const exceptColumnFields = [  ];
		const sessionName = document.getElementById("sessionName").value;
		exportToExcel("이력관리 비교", "이력관리", "이력관리 비교", exceptColumnFields, sessionName);
	}

	
	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>
