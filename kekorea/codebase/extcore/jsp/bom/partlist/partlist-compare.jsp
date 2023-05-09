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

<div id="grid_wrap" style="height: 900px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let myGridID;
	const data =
<%=data%>
	function _layout() {
		return [ {
			dataField : "engType",
			headerText : "수배표 타입",
			dataType : "string",
			width : 120,
			filter : {
				showIcon : true,
				inline : true
			},
			cellColMerge: true, // 셀 가로 병합 실행
			cellColSpan: 4, // 셀 가로 병합 대상은 6개로 설정
		},{
			dataField : "lotNo",
			headerText : "LOT_NO",
			dataType : "numeric",
			width : 80,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "unitName",
			headerText : "UNIT NAME",
			dataType : "string",
			width : 120,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "partNo",
			headerText : "부품번호",
			dataType : "string",
			width : 100,
			styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
				const oid = item.oid;
				if (oid !== "") {
					return "underline";
				}
				return "";
			},
			filter : {
				showIcon : true,
				inline : true
			},
		},{
			headerText : "<%=p1.getKekNumber()%>",
			children : [ {
				dataField : "quantity1",
				headerText : "수량",
				dataType : "numeric",
				width : 100,
				labelFunction : function(rowIndex, columnIndex, value, headerText, item, dataField, cItem) {
					if(item.quantity1 === undefined) {
						return 0;
					}
					return value;
				},
				styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
					const quantity1 = item.quantity1;
					if(typeof quantity1 === "string" ) {
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
						dataField : "quantity<%=i%>",
						headerText : "수량",
						dataType : "numeric",
						width : 100,
						labelFunction : function(rowIndex, columnIndex, value, headerText, item, dataField, cItem) {
							if(item.quantity<%=i%> === undefined) {
								return 0;
							}
							return value;
						},
						styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
							const quantity1 = item.quantity1;
							if(typeof quantity1 === "string" ) {
								return "none";
							}
							if (value !== quantity1) {
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
			dataField : "partName",
			headerText : "부품명",
			dataType : "string",
			width : 200,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "standard",
			headerText : "규격",
			dataType : "string",
			width : 250,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "maker",
			headerText : "MAKER",
			dataType : "string",
			width : 130,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "customer",
			headerText : "거래처",
			dataType : "string",
			width : 130,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "unit",
			headerText : "단위",
			dataType : "string",
			width : 80,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "price",
			headerText : "단가",
			dataType : "numeric",
			width : 120,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "currency",
			headerText : "화폐",
			dataType : "string",
			width : 60,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "won",
			headerText : "원화금액",
			dataType : "numeric",
			width : 120,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "partListDate_txt",
			headerText : "수배일자",
			dataType : "string",
			width : 100,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "exchangeRate",
			headerText : "환율",
			dataType : "numeric",
			width : 80,
			formatString : "#,##0.0000",
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "referDrawing",
			headerText : "참고도면",
			dataType : "string",
			width : 120,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "classification",
			headerText : "조달구분",
			dataType : "string",
			width : 120,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "note",
			headerText : "비고",
			dataType : "string",
			width : 250,
			filter : {
				showIcon : true,
				inline : true
			},
		} ]
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
			fixedColumnCount : 5,
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
		AUIGrid.bind(myGridID, "cellClick", auiCellClickHandler);
		AUIGrid.setGridData(myGridID, data);
	}
	
	function auiCellClickHandler(event) {
		const dataField = event.dataField;
		const item = event.item;
		if(dataField === "partNo" && !isNull(item.oid)) {
			const oid = item.oid;
			const url = getCallUrl("/part/view?oid=" + oid);
			popup(url, 1500, 800);
		}
	}
	
	
	function exportExcel() {
		const exceptColumnFields = [  ];
		const sessionName = document.getElementById("sessionName").value;
		exportToExcel("수배표 비교", "수배표", "수배표 비교", exceptColumnFields, sessionName);
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
