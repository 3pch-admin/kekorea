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
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=11210"></script>
<style type="text/css">
.compare {
	background-color: yellow;
	color: red !important;
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


<div id="grid_wrap" style="height: 900px; border-top: 1px solid #3180c3;"></div>
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
			cellColMerge: true, // 셀 가로 병합 실행
			cellColSpan: 3, // 셀 가로 병합 대상은 6개로 설정
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
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
					jsCallback : function(rowIndex, columnIndex, value, item) {
						const oid = item.oid;
						let url;
						if(oid !== undefined) {
							if(oid.indexOf("Project") > -1) {
								url = getCallUrl("/project/info?oid="+oid);
								popup(url);
							} else {
								url = getCallUrl("/kePart/view?oid=" + oid);
								popup(url, 1500, 700);
							}
						}
					}
				},	
				labelFunction : function(rowIndex, columnIndex, value, headerText, item, dataField, cItem) {
					if(item.qty1 === undefined) {
						return 0;
					}
					return value;
				},
				styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
					const qty1 = item.qty1;
					if(typeof qty1 === "string" ) {
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
				dataField : "qty<%=i%>",
				headerText : "수량",
				dataType : "numeric",
				width : 100,
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
					jsCallback : function(rowIndex, columnIndex, value, item) {
						const oid = item.oid;
						let url;
						if(oid !== undefined) {
							if(oid.indexOf("Project") > -1) {
								url = getCallUrl("/project/info?oid="+oid);
								popup(url);
							} else {
								url = getCallUrl("/kePart/view?oid=" + oid);
								popup(url, 1500, 700);
							}
						}
					}
				},
				labelFunction : function(rowIndex, columnIndex, value, headerText, item, dataField, cItem) {
					if(item.qty<%=i%> === undefined) {
						return 0;
					}
					return value;
				},
				styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
					const qty1 = item.qty1;
					if(typeof value === "string" ) {
						return "none";
					}
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
		<%i++;
}%>
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
			enableFilter : true,
			selectionMode : "multipleCells",
			enableMovingColumn : true,
			showInlineFilter : true,
			useContextMenu : true,
			enableRightDownFocus : true,
// 			autoGridHeight : true,
			enableCellMerge: true,
			fixedRowCount : 4,
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
	
	function exportExcel() {
		const exceptColumnFields = [ "" ];
		const sessionName = document.getElementById("sessionName").value;
		exportToExcel("TBOM 비교 리스트", "TBOM 비교", "TBOM 비교 리스트", exceptColumnFields, sessionName);
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
