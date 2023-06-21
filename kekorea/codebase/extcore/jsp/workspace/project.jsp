<%@page import="e3ps.project.Project"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.workspace.service.WorkspaceHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				관련 작번
			</div>
		</td>
	</tr>
</table>

<div id="grid_wrap" style="height: 250px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "kekNumber",
		headerText : "KEK 작번",
		dataType : "string",
		width : 100,
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/project/info?oid=" + oid);
				popup(url);
			}
		},
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "keNumber",
		headerText : "KE 작번",
		dataType : "string",
		width : 100,
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/project/info?oid=" + oid);
				popup(url);
			}
		},
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "projectType",
		headerText : "작번유형",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "customer",
		headerText : "거래처",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "install",
		headerText : "설치장소",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : false,
		},
	}, {
		dataField : "mak",
		headerText : "막종",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : false,
		},
	}, {
		dataField : "detail",
		headerText : "막종상세",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : false,
		},
	}, {
		dataField : "description",
		headerText : "작업내용",
		dataType : "string",
		// 			width : 150,
		filter : {
			showIcon : false,
		},
	}, {
		dataField : "pDate_txt",
		headerText : "발행일",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : false,
		},
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableFilter : true,
			useContextMenu : true,
			enableRightDownFocus : true,
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		AUIGrid.setGridData(myGridID, <%=WorkspaceHelper.manager.getProjects(oid)%>);
	}
</script>
