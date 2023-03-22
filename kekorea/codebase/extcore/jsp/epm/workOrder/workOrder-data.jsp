<%@page import="net.sf.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
JSONArray list = (JSONArray) request.getAttribute("list");
%>
<div id="grid_wrap" style="height: 800px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "preView",
		headerText : "미리보기",
		width : 80,
		editable : false,
		renderer : {
			type : "ImageRenderer",
			altField : null,
			imgHeight : 34,
		},
	}, {
		dataField : "dataType",
		headerText : "파일유형",
		dataType : "string",
		width : 100,
	}, {
		dataField : "name",
		headerText : "DRAWING TITLE",
		dataType : "string",
		style : "left indent10"
	}, {
		dataField : "number",
		headerText : "DWG. NO",
		dataType : "string",
		width : 200,
	}, {
		dataField : "current",
		headerText : "CURRENT VER",
		dataType : "string",
		width : 130,
	}, {
		dataField : "rev",
		headerText : "REV",
		dataType : "string",
		width : 130,
	}, {
		dataField : "latest",
		headerText : "REV (최신)",
		dataType : "string",
		width : 130,
	}, {
		dataField : "lotNo",
		headerText : "LOT",
		dataType : "numeric",
		width : 100,
		formatString : "###0",
	}, {
		dataField : "createdData_txt",
		headerText : "등록일",
		dataType : "string",
		width : 100,
	}, {
		dataField : "note",
		headerText : "NOTE",
		dataType : "string",
		width : 350,
	}, {
		dataField : "primary",
		headerText : "도면파일",
		dataType : "string",
		width : 80,
		editable : false,
		renderer : {
			type : "TemplateRenderer",
		},
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30, // 헤더높이
			rowHeight : 30, // 행 높이
			showRowNumColumn : true, // 번호 행 출력 여부
			showStateColumn : true, // 상태표시 행 출력 여부
			selectionMode : "multipleCells",
			rowNumHeaderText : "번호", // 번호 행 텍스트 설정
			// 복사 후 편집 이벤트 발생하는 속성
			$compaEventOnPaste : true,
			showRowCheckColumn : true,
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		AUIGrid.setGridData(myGridID,
<%=list%>
	);
	}

	createAUIGrid(columns);
	AUIGrid.resize(myGridID);
	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>
