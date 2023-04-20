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
<%@include file="/extcore/include/css.jsp"%>
<%@include file="/extcore/include/script.jsp"%>
<%@include file="/extcore/include/auigrid.jsp"%>
</head>
<body>
	<form>
		<div id="grid_wrap8" style="height: 780px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID8;
			const data = <%=data%>
			const columns8 = [ {
				dataField : "item",
				headerText : "항목",
				dataType : "string",
				width : 120,
			}, {
				dataField : "improvements",
				headerText : "개선내용",
				dataType : "string",
				width : 300,
			}, {
				dataField : "improvement",
				headerText : "개선책",
				dataType : "string",
				width : 300,
			}, {
				dataField : "apply",
				headerText : "적용/미적용",
				width : 100,
			}, {
				dataField : "mak_code",
				headerText : "막종",
				width : 150,
			}, {
				dataField : "detail_code",
				headerText : "막종상세",
				width : 150,
			}, {
				dataField : "customer_code",
				headerText : "거래처",
				width : 150,
			}, {
				dataField : "install_code",
				headerText : "설치장소",
				width : 150,
			}, {
				dataField : "note",
				headerText : "비고",
				dataType : "string",
				width : 150,
			}, {
				dataField : "preView",
				headerText : "미리보기",
				width : 100,
				editable : false,
				renderer : {
					type : "ImageRenderer",
					altField : null,
					imgHeight : 34,
				},
			}, {
				dataField : "icons",
				headerText : "첨부파일",
				width : 100,
				editable : false,
				renderer : {
					type : "TemplateRenderer",
				},
			}, {
				dataField : "creator",
				headerText : "작성자",
				dataType : "string",
				width : 100,
				editable : false,
			}, {
				dataField : "createdDate",
				headerText : "작성일",
				dataType : "date",
				formatString : "yyyy-mm-dd",
				width : 100,
				editable : false,
			} ]

			function createAUIGrid8(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : true,
					rowNumHeaderText : "번호",
					selectionMode : "multipleCells",
					showAutoNoDataMessage : false,
				};
				myGridID8 = AUIGrid.create("#grid_wrap8", columnLayout, props);
				AUIGrid.setGridData(myGridID8, data);
				AUIGrid.bind(myGridID8, "cellClick", auiCellClickHandler);
			}


			function auiCellClickHandler(event) {
				const dataField = event.dataField;
				const oid = event.item.oid;
				const preView = event.item.preView;
				if (dataField === "preView") {
					const url = getCallUrl("/aui/thumbnail?oid=" + oid);
					popup(url);
				}
			}

			
			document.addEventListener("DOMContentLoaded", function() {
				// 화면 활성화시 불러오게 설정한다 속도 생각 
				createAUIGrid8(columns8);
				AUIGrid.resize(myGridID8);
			})

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID8);
			});
		</script>
	</form>
</body>
</html>