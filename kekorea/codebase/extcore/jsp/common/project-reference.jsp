<%@page import="e3ps.project.service.ProjectHelper"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
String header = request.getParameter("header");
String height = StringUtils.replaceToValue(request.getParameter("height"));
%>
<div id="grid_wrap50" style="height: <%=height%>px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<script type="text/javascript">
	let myGridID50;
	const columns50 = [ {
		dataField : "name",
		headerText : "<%=header%>",
		dataType : "string",
		width : 300,
		style : "aui-left",
		cellMerge : true
	}, {
		dataField : "projectType_name",
		headerText : "설계구분",
		dataType : "string",
		width : 80,
	}, {
		dataField : "mak_name",
		headerText : "막종",
		dataType : "string",
		width : 100,
	}, {
		dataField : "detail_name",
		headerText : "막종상세",
		dataType : "string",
		width : 100,
	}, {
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
	}, {
		dataField : "keNumber",
		headerText : "KE 작번",
		dataType : "string",
		style : "underline",
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
	}, {
		dataField : "userId",
		headerText : "USER ID",
		dataType : "string",
		width : 100,
	}, {
		dataField : "description",
		headerText : "작업내용",
		dataType : "string",
		width : 300,
		style : "left",
	}, {
		dataField : "customer_name",
		headerText : "거래처",
		dataType : "string",
		width : 100,
	}, {
		dataField : "install_name",
		headerText : "설치 장소",
		dataType : "string",
		width : 100,
	}, {
		dataField : "pdate_txt",
		headerText : "발행일",
		dataType : "string",
		width : 100,
	}, {
		dataField : "model",
		headerText : "모델",
		dataType : "string",
		width : 100,
	}, {
		dataField : "creator",
		headerText : "작성자",
		dataType : "string",
		width : 100,
		cellMerge : true,
		mergeRef : "name",
		mergePolicy : "restrict"
	}, {
		dataField : "createdDate_txt",
		headerText : "작성일",
		dataType : "string",
		width : 100,
		cellMerge : true,
		mergeRef : "name",
		mergePolicy : "restrict"
	} ]

	function createAUIGrid50(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			selectionMode : "multipleCells",
			fixedColumnCount : 1,
			cellMergePolicy : "withNull",
			enableCellMerge : true,
		}
		myGridID50 = AUIGrid.create("#grid_wrap50", columnLayout, props);
		AUIGrid.setGridData(myGridID50, <%=ProjectHelper.manager.jsonAuiReferenceProject(oid)%>);
	}
</script>