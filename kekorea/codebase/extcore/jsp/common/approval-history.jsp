<%@page import="e3ps.workspace.service.WorkspaceHelper"%>
<%@page import="net.sf.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
JSONArray jsonAuiHistory = WorkspaceHelper.manager.jsonAuiHistory(oid);
%>
<div id="grid_wrap100" style="height: 350px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let myGridID100;
	const columns100 = [ {
		dataField : "type",
		headerText : "타입",
		dataType : "string",
		width : 80
	}, {
		dataField : "role",
		headerText : "역할",
		dataType : "string",
		width : 80
	}, {
		dataField : "name",
		headerText : "제목",
		dataType : "string",
		style : "aui-left"
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 100
	}, {
		dataField : "owner",
		headerText : "담당자",
		dataType : "string",
		width : 100
	}, {
		dataField : "receiveDate_txt",
		headerText : "수신일",
		dataType : "string",
		width : 130
	}, {
		dataField : "completeDate_txt",
		headerText : "완료일",
		dataType : "string",
		width : 130
	}, {
		dataField : "description",
		headerText : "결재의견",
		dataType : "string",
		style : "aui-left",
		width : 450,
	}, ]
	function createAUIGrid100(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			selectionMode : "multipleCells",
			noDataMessage : "결재이력이 없습니다.",
			enableSorting : false,
// 			autoGridHeight : true
		}
		myGridID100 = AUIGrid.create("#grid_wrap100", columnLayout, props);
		AUIGrid.setGridData(myGridID100, <%=jsonAuiHistory%>);
	}
</script>