<%@page import="e3ps.bom.tbom.service.TBOMHelper"%>
<%@page import="java.util.Map"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.korea.cip.dto.CipDTO"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
JSONArray data = (JSONArray) request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
<style type="text/css">
.cell1 {
	background: #ffacac;
	font-weight: bold;
}

.cell2 {
	background: #fdf4bb;
	font-weight: bold;
}

.preView {
	background-color: #caf4fd;
	cursor: pointer;
}
</style>
</head>
<body>
	<form>
		<div id="grid_wrap9" style="height: 780px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID9;
			const data =
		<%=data%>
			const columns9 = [ {
				dataField : "workOrderType",
				headerText : "설계구분",
				width : 100,
				styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
					if (value === "기계") {
						return "cell1";
					} else if (value === "전기") {
						return "cell2";
					}
					return null;
				},
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "preView",
				headerText : "미리보기",
				width : 80,
				style : "preView",
				renderer : {
					type : "ImageRenderer",
					altField : null,
					imgHeight : 34,
				},
				filter : {
					showIcon : false,
					inline : false
				},
			}, {
				dataField : "name",
				headerText : "DRAWING TITLE",
				dataType : "string",
				style : "aui-left",
				width : 250,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "number",
				headerText : "DWG. NO",
				dataType : "string",
				width : 200,
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
					jsCallback : function(rowIndex, columnIndex, value, item) {
						const oid = item.oid;
						const doid = item.doid;
						const number = item.number;
						const rev = item.rev;
						let url;
						if (doid.indexOf("KeDrawing") > -1) {
							url = getCallUrl("/keDrawing/viewByNumberAndRev?number=" + number + "&rev=" + rev);
							popup(url, 1400, 700);
						} else {
							url = getCallUrl("/project/info?oid=" + oid);
						}
					}
				},
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "current",
				headerText : "CURRENT VER",
				dataType : "string",
				width : 130,
				styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
					const rev = item.rev;
					if (Number(value) !== Number(rev)) {
						return "compare";
					}
					return "";
				},
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
					jsCallback : function(rowIndex, columnIndex, value, item) {
						const oid = item.oid;
						const doid = item.doid;
						const number = item.number;
						const current = item.current;
						let url;
						if (doid.indexOf("KeDrawing") > -1) {
							url = getCallUrl("/keDrawing/viewByNumberAndRev?number=" + number + "&rev=" + current);
							popup(url, 1400, 700);
						} else {
							url = getCallUrl("/project/info?oid=" + oid);
						}
					}
				},
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "rev",
				headerText : "REV",
				dataType : "string",
				width : 130,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
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
				dataField : "createdData_txt",
				headerText : "등록일",
				dataType : "string",
				width : 100,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "note",
				headerText : "NOTE",
				dataType : "string",
				width : 350,
				filter : {
					showIcon : true,
					inline : true
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
					inline : false
				},
			} ]

			function createAUIGrid9(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : true,
					rowNumHeaderText : "번호",
					selectionMode : "multipleCells",
					showAutoNoDataMessage : false,
					enableFilter : true,
					showInlineFilter : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
				};
				myGridID9 = AUIGrid.create("#grid_wrap9", columnLayout, props);
				AUIGrid.setGridData(myGridID9, data);
				AUIGrid.bind(myGridID9, "cellClick", auiCellClickHandler);
			}

			function auiCellClickHandler(event) {
				const dataField = event.dataField;
				const oid = event.item.doid;
				const preView = event.item.preView;
				if (dataField === "preView") {
					const url = getCallUrl("/aui/thumbnail?oid=" + oid);
					popup(url);
				}
			}

			document.addEventListener("DOMContentLoaded", function() {
				// 화면 활성화시 불러오게 설정한다 속도 생각 
				createAUIGrid9(columns9);
				AUIGrid.resize(myGridID9);
			})

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID9);
			});
		</script>
	</form>
</body>
</html>