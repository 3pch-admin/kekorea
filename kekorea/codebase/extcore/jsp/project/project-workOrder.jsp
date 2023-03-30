<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.korea.cip.dto.CipDTO"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
JSONArray list = (JSONArray) request.getAttribute("list");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/include/css.jsp"%>
<%@include file="/extcore/include/script.jsp"%>
<%@include file="/extcore/include/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=1010"></script>
</head>
<body>
	<form>
		<div id="grid_wrap2" style="height: 780px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID2;
			const columns2 = [ {
				dataField : "preView",
				headerText : "미리보기",
				width : 80,
				style : "cursor",
				renderer : {
					type : "ImageRenderer",
					altField : null,
					imgHeight : 34,
				},
				filter : {
					showIcon : false,
					inline : false,
				},
			}, {
				dataField : "name",
				headerText : "DRAWING TITLE",
				dataType : "string",
				style : "aui-left",
				filter : {
					showIcon : true,
					inline : true,
				},
			}, {
				dataField : "number",
				headerText : "DWG. NO",
				dataType : "string",
				width : 100,
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
					jsCallback : function(rowIndex, columnIndex, value, item) {
						const oid = item.oid;
						const url = getCallUrl("/keDrawing/view?oid=" + oid);
						popup(url);
					}
				},
				filter : {
					showIcon : true,
					inline : true,
				},
			}, {
				dataField : "current",
				headerText : "CURRENT VER",
				dataType : "string",
				width : 110,
				filter : {
					showIcon : true,
					inline : true,
				},
			}, {
				dataField : "rev",
				headerText : "REV",
				dataType : "string",
				width : 80,
				filter : {
					showIcon : true,
					inline : true,
				},
			}, {
				dataField : "latest",
				headerText : "REV (최신)",
				dataType : "string",
				width : 110,
				filter : {
					showIcon : true,
					inline : true,
				},
			}, {
				dataField : "lotNo",
				headerText : "LOT",
				dataType : "numeric",
				width : 100,
				formatString : "###0",
				filter : {
					showIcon : true,
					inline : true,
				},
			}, {
				dataField : "createdData_txt",
				headerText : "등록일",
				dataType : "string",
				width : 100,
				filter : {
					showIcon : true,
					inline : true,
				},
			}, {
				dataField : "note",
				headerText : "NOTE",
				dataType : "string",
				width : 350,
				filter : {
					showIcon : true,
					inline : true,
				},
			}, {
				dataField : "primary",
				headerText : "도면파일",
				dataType : "string",
				width : 80,
				renderer : {
					type : "TemplateRenderer",
				},
				filter : {
					showIcon : false,
					inline : false,
				},
			} ]

			function createAUIGrid2(columnLayout) {
				const props = {
					headerHeight : 30,
					rowHeight : 30,
					showRowNumColumn : true,
					showStateColumn : true,
					rowNumHeaderText : "번호",
					showAutoNoDataMessage : false,
					enableFilter : true,
					selectionMode : "multipleCells",
					showInlineFilter : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
				};
				myGridID2 = AUIGrid.create("#grid_wrap2", columnLayout, props);
				AUIGrid.setGridData(myGridID2,
		<%=list%>
			);
				AUIGrid.bind(myGridID2, "cellClick", auiCellClickHandler);
			}

			function auiCellClickHandler(event) {
				const dataField = event.dataField;
				const oid = event.item.oid;
				if (dataField === "preView") {
					const url = getCallUrl("/aui/thumbnail?oid=" + oid);
					popup(url);
				}
			}

			document.addEventListener("DOMContentLoaded", function() {
				createAUIGrid2(columns2);
				AUIGrid.resize(myGridID2);
			})

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID2);
			});
		</script>
	</form>
</body>
</html>