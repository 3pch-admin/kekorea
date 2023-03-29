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
		<div id="grid_wrap4" style="height: 780px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID4;
			const columns4 = [ {
				dataField : "kekNumber",
				headerText : "KEK 작번",
				dataType : "string",
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "projectType_name",
				headerText : "작번 유형",
				dataType : "string",
				width : 450,
				style : "aui-left",
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "mak_name",
				headerText : "막종",
				dataType : "string",
				width : 100,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "detail_name",
				headerText : "막종상세",
				dataType : "string",
				width : 100,
				filter : {
					showIcon : true,
					inline : true,
				},
			}, {
				dataField : "customer_name",
				headerText : "고객사",
				dataType : "string",
				width : 100,
				filter : {
					showIcon : true,
					inline : true,
				},
			}, {
				dataField : "install_name",
				headerText : "설치장소",
				dataType : "string",
				width : 100,
				filter : {
					showIcon : true,
					inline : true,
				},
			}, {
				dataField : "description",
				headerText : "작업내용",
				dataType : "string",
				width : 100,
				filter : {
					showIcon : true,
					inline : true,
				},
			}, {
				dataField : "pdate_txt",
				headerText : "발행일",
				dataType : "string",
				width : 100,
				filter : {
					showIcon : true,
					inline : true,
				},
			}, {
				dataField : "customDate_txt",
				headerText : "요청납기일",
				dataType : "string",
				width : 100,
				filter : {
					showIcon : true,
					inline : true,
				},
			} ]

			function createAUIGrid4(columnLayout) {
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
				myGridID4 = AUIGrid.create("#grid_wrap4", columnLayout, props);
				AUIGrid.setGridData(myGridID4,
		<%=list%>
			);
			}

			document.addEventListener("DOMContentLoaded", function() {
				createAUIGrid4(columns4);
				AUIGrid.resize(myGridID4);
			})

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID4);
			});
		</script>
	</form>
</body>
</html>