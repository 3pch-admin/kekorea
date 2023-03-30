<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.epm.keDrawing.dto.KeDrawingDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
JSONArray list = (JSONArray) request.getAttribute("list");
%>
<%@include file="/extcore/include/auigrid.jsp"%>
<div id="grid_wrap" style="height: 550px; border-top: 1px solid #3180c3;"></div>

<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "lotNo",
		headerText : "LOT",
		dataType : "numeric",
		width : 80,
		formatString : "###0",
		filter : {
			showIcon : true,
			inline : true,
			displayFormatValues : true
		},
	}, {
		dataField : "code",
		headerText : "중간코드",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true,
			inline : true,
		},
	}, {
		dataField : "keNumber",
		headerText : "부품번호",
		dataType : "string",
		width : 100,
		style : "underline",
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/kePart/view?oid=" + oid);
				popup(url, 1100, 600);
			}
		},
		filter : {
			showIcon : true,
			inline : true,
		},
	}, {
		dataField : "name",
		headerText : "부품명",
		dataType : "string",
		width : 200,
		style : "underline",
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/kePart/view?oid=" + oid);
				popup(url, 1100, 600);
			}
		},
		filter : {
			showIcon : true,
			inline : true,
		},
	}, {
		dataField : "model",
		headerText : "KokusaiModel",
		dataType : "string",
		filter : {
			showIcon : true,
			inline : true,
		},
	}, {
		dataField : "version",
		headerText : "버전",
		dataType : "numeric",
		formatString : "###0",
		width : 80,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "latest",
		headerText : "최신버전",
		dataType : "string",
		width : 80,
		renderer : {
			type : "CheckBoxEditRenderer",
		},
		filter : {
			showIcon : false,
			inline : false
		},
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "creator",
		headerText : "등록자",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "createdDate_txt",
		headerText : "등록일",
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
			inline : true,
		}
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
		}
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		AUIGrid.setGridData(myGridID, <%=list%>);
	}
	createAUIGrid(columns);
	AUIGrid.resize(myGridID);
	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
 </script>