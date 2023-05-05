<%@page import="e3ps.project.service.ProjectHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
boolean isAdmin = Boolean.parseBoolean(request.getParameter("isAdmin"));
String poid = (String) request.getParameter("poid");
String toid = (String) request.getParameter("toid");
%>
<div class="info-header">
	<img src="/Windchill/extcore/images/header.png">
	태스크 수배표 정보
</div>
<div id="grid_wrap" style="height: 450px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "name",
		headerText : "수배표 제목",
		dataType : "string",
		style : "aui-left",
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/partlist/view?oid=" + oid);
				popup(url, 1700, 800);
			}
		},
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 100,
	}, {
		dataField : "creator",
		headerText : "작성자",
		dataType : "string",
		width : 100,
	}, {
		dataField : "createdDate_txt",
		headerText : "작성일",
		dataType : "string",
		width : 100,
	}, {
		dataField : "secondary",
		headerText : "첨부파일",
		dataType : "string",
		width : 100,
		renderer : {
			type : "TemplateRenderer"
		}
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			selectionMode : "multipleCells",
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		AUIGrid.setGridData(myGridID,
<%=ProjectHelper.manager.jsonAuiOutput(poid, toid)%>
	);
	}
</script>