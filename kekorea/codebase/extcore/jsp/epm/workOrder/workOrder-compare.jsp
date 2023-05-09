<%@page import="wt.org.WTUser"%>
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
WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>    
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=1010"></script>
<style type="text/css">
.compare {
	background-color: yellow;
	color: red;
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
	font-weight: normal;
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
			cellColMerge: true, // 셀 가로 병합 실행
			cellColSpan: 3, // 셀 가로 병합 대상은 6개로 설정
		}, {
			dataField : "name",
			headerText : "DRAWING TITLE",
			dataType : "string",
			width : 250,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "number",
			headerText : "DWG. NO",
			dataType : "string",
			width : 120,
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
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
					jsCallback : function(rowIndex, columnIndex, value, item) {
						const oid = item.oid;
						const number = item.number;
						const rev = item.version;
						let url;
						if(oid.indexOf("Project") > -1) {
							url = getCallUrl("/project/info?oid="+oid);
							popup(url);
						} else if (oid.indexOf("KeDrawing") > -1) {
							url = getCallUrl("/keDrawing/viewByNumberAndRev?number=" + number + "&rev=" + rev);
							popup(url, 1400, 700);
						} else {
							url = getCallUrl("/project/info?oid=" + oid);
						}
					}
				},
				labelFunction : function(rowIndex, columnIndex, value, headerText, item, dataField, cItem) {
					if(item.rev1 === undefined) {
						return "";
					}
					return value;
				},
				styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
					const rev1 = item.rev1;
					if(typeof rev1 === "string" ) {
						return "none";
					}
					return "";
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
						width : 120,
						renderer : {
							type : "LinkRenderer",
							baseUrl : "javascript",
							jsCallback : function(rowIndex, columnIndex, value, item) {
								const oid = item.oid;
								const number = item.number;
								const rev = item.version;
								let url;
								if(oid.indexOf("Project") > -1) {
									url = getCallUrl("/project/info?oid="+oid);
									popup(url);
								} else if (oid.indexOf("KeDrawing") > -1) {
									url = getCallUrl("/keDrawing/viewByNumberAndRev?number=" + number + "&rev=" + rev);
									popup(url, 1400, 700);
								} else {
									url = getCallUrl("/project/info?oid=" + oid);
								}
							}
						},
						labelFunction : function(rowIndex, columnIndex, value, headerText, item, dataField, cItem) {
							if(item.rev<%=i%> === undefined) {
								return "";
							}
							return value;
						},
						styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
							const rev1 = item.rev1;
							if(typeof value === "string" ) {
								return "none";
							}
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
	
	function exportExcel() {
		const exceptColumnFields = [  ];
		const sessionName = document.getElementById("sessionName").value;
		exportToExcel("도면일람표 비교", "도면일람표", "도면일람표 비교", exceptColumnFields, sessionName);
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
			fixedColumnCount : 3,
			autoGridHeight : true,
			enableCellMerge: true,
			rowStyleFunction : function(rowIndex, item) {
				const value = item.lotNo;
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
