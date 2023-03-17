<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.epm.keDrawing.dto.KeDrawingDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
JSONArray list = (JSONArray) request.getAttribute("list");
%>
<!-- AUIGrid -->
<%@include file="/extcore/include/auigrid.jsp"%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				KE 도면 버전이력
			</div>
		</td>
		<td class="right">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>
<div id="_grid_wrap" style="height: 440px; border-top: 1px solid #3180c3;"></div>

<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "lotNo",
		headerText : "LOT",
		dataType : "numeric",
		width : 100,
		formatString : "###0",
		editRenderer : {
			type : "InputEditRenderer",
			onlyNumeric : true, // 0~9만 입력가능
		},
		filter : {
			showIcon : true,
			inline : true,
			displayFormatValues : true
		},
	}, {
		dataField : "name",
		headerText : "DRAWING TITLE",
		dataType : "string",
		style : "left indent10",
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/keDrawing/view?oid=" + oid);
				popup(url, 1100, 600);
			}
		},
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "keNumber",
		headerText : "DWG NO",
		dataType : "string",
		width : 200,
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/keDrawing/view?oid=" + oid);
				popup(url, 1100, 600);
			}
		},
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "version",
		headerText : "버전",
		dataType : "numeric",
		width : 80,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "latest",
		headerText : "최신버전",
		dataType : "boolean",
		width : 100,
		renderer : {
			type : "CheckBoxEditRenderer"
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
	}, {
		dataField : "modifier",
		headerText : "수정자",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "modifiedDate_txt",
		headerText : "수정일",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true,
			inline : true,
		},
	}, {
		dataField : "note",
		headerText : "개정사유",
		dateType : "string",
		width : 250,
		filter : {
			showIcon : true,
			inline : true
		},
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			// 그리드 공통속성 시작
			headerHeight : 30,
			rowHeight : 30,
			showRowNumColumn : true,
			showStateColumn : true,
			rowNumHeaderText : "번호",
			enableFilter : true,
			selectionMode : "multipleCells",
			showInlineFilter : true,
			enableRightDownFocus : true,
			filterLayerWidth : 320,
			filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
		// 그리드 공통속성 끝
		}
		myGridID = AUIGrid.create("#_grid_wrap", columnLayout, props);
		AUIGrid.setGridData(myGridID, <%=list%>);
	}

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>