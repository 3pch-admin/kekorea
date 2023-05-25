<%@page import="e3ps.common.util.ThumnailUtils"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="e3ps.project.service.ProjectHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
%>
<div id="grid_wrap20" style="height: 100px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let myGridID20;
	const columns20 = [ {
		dataField : "thumnail",
		headerText : "",
		dataType : "string",
		width : 150,
		renderer : {
			type : "ImageRenderer",
			altField : null,
		},
	}, {
		dataField : "name",
		headerText : "이름",
		dataType : "string",
		width : 200
	}, {
		dataField : "description",
		headerText : "설명",
		dataType : "string",
		style : "aui-left"
	}, {
		dataField : "creator",
		headerText : "작성자",
		dataType : "string",
		width : 100
	}, {
		dataField : "createdDate_txt",
		headerText : "작성일",
		dataType : "string",
		width : 150
	} ]

	function createAUIGrid20(columnLayout) {
		const props = {
			headerHeight : 30,
			rowHeight : 100,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			selectionMode : "multipleCells",
			autoGridHeight : true
		}
		myGridID20 = AUIGrid.create("#grid_wrap20", columnLayout, props);
		AUIGrid.setGridData(myGridID20, <%=ThumnailUtils.markUpData(oid)%>);
		AUIGrid.bind(myGridID20, "cellClick", function(event) {
			const dataField = event.dataField;
			const callUrl = event.item.creoViewURL;
			if(dataField === "thumnail") {
				popup(callUrl, 600, 200);
			}
		})
	}
</script>