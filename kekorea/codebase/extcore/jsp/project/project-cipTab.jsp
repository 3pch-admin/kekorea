<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.korea.cip.dto.CipDTO"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<CipDTO> list = (ArrayList<CipDTO>) request.getAttribute("list");
JSONArray data = JSONArray.fromObject(list);
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>    
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=1010"></script>
</head>
<body>
	<form>
		<div id="grid_wrap1" style="height: 780px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID1;
			const columns1 = [ {
				dataField : "item",
				headerText : "항목",
				dataType : "string",
				width : 120,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "improvements",
				headerText : "개선내용",
				dataType : "string",
				width : 300,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "improvement",
				headerText : "개선책",
				dataType : "string",
				width : 300,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "apply",
				headerText : "적용/미적용",
				width : 100,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "mak_code",
				headerText : "막종",
				width : 150,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "detail_code",
				headerText : "막종상세",
				width : 150,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "customer_code",
				headerText : "거래처",
				width : 150,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "install_code",
				headerText : "설치장소",
				width : 150,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "note",
				headerText : "비고",
				dataType : "string",
				width : 150,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "preView",
				headerText : "미리보기",
				width : 100,
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
				dataField : "icons",
				headerText : "첨부파일",
				width : 100,
				renderer : {
					type : "TemplateRenderer",
				},
				filter : {
					showIcon : false,
					inline : false
				},
			}, {
				dataField : "creator",
				headerText : "작성자",
				dataType : "string",
				width : 100,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "createdDate_txt",
				headerText : "작성일",
				dataType : "string",
				width : 100,
				filter : {
					showIcon : true,
					inline : true,
				},
			} ]

			function createAUIGrid1(columnLayout) {
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
				myGridID1 = AUIGrid.create("#grid_wrap1", columnLayout, props);
				AUIGrid.setGridData(myGridID1, <%=data%>);
			}

			document.addEventListener("DOMContentLoaded", function() {
				createAUIGrid1(columns1);
				AUIGrid.resize(myGridID1);
			})

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID1);
			});
		</script>
	</form>
</body>
</html>