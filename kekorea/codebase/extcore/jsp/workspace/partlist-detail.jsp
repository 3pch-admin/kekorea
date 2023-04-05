<%@page import="e3ps.bom.partlist.service.PartlistHelper"%>
<%@page import="net.sf.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getParameter("oid");
JSONArray list = PartlistHelper.manager.jsonAuiWorkSpaceData(oid);
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				수배표 정보
			</div>
		</td>
	</tr>
</table>
<div id="grid_wrap" style="height: 200px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "name",
		headerText : "수배표 제목",
		dataType : "string",
		width : 350,
		style : "aui-left",
		filter : {
			showIcon : true,
			inline : true
		},
		cellMerge : true
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 80,
		filter : {
			showIcon : true,
			inline : true
		},
		cellMerge : true,
		mergeRef : "name",
		mergePolicy : "restrict"
	}, {
		dataField : "creator",
		headerText : "작성자",
		dataType : "string",
		width : 80,
		filter : {
			showIcon : true,
			inline : true
		},
		cellMerge : true,
		mergeRef : "name",
		mergePolicy : "restrict"
	}, {
		dataField : "createdDate_txt",
		headerText : "작성일",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true,
			inline : true
		},
		cellMerge : true,
		mergeRef : "name",
		mergePolicy : "restrict"
	}, {
		dataField : "primary",
		headerText : "첨부파일",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true,
			inline : true
		},
		cellMerge : true,
		mergeRef : "name",
		mergePolicy : "restrict"
	}, {
		dataField : "projectType_name",
		headerText : "작번유형",
		dataType : "string",
		width : 100,
		style : "underline",
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "kekNumber",
		headerText : "KEK 작번",
		dataType : "string",
		width : 100,
		style : "underline",
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "keNumber",
		headerText : "KE 작번",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "customer",
		headerText : "고객사",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "mak_name",
		headerText : "막종",
		dataType : "string",
		width : 130,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "detail_name",
		headerText : "막종상세",
		dataType : "string",
		width : 130,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "description",
		headerText : "작업내용",
		dataType : "string",
		width : 350,
		style : "aui-left",
		filter : {
			showIcon : true,
			inline : true
		},
	} ];

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			selectionMode : "singleRow",
			enableCellMerge : true,
			fixedColumnCount : 1,
			enableSorting : false
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		AUIGrid.setGridData(myGridID,
<%=list%>
	);
	}
</script>