<%@page import="e3ps.bom.tbom.beans.TBOMMasterViewData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
TBOMMasterViewData data = (TBOMMasterViewData) request.getAttribute("data");
%>
<%@include file="/jsp/include/auigrid.jsp"%>
<div id="tbom_grid_wrap" style="height: 550px; border-top: 1px solid #3180c3; margin-top: 5px;"></div>
<script type="text/javascript">
	let tbomGridID;
	const tbom_columns = [ {
		dataField : "lotNo",
		headerText : "LOT",
		dataType : "string",
		width : 100,
	}, {
		dataField : "code",
		headerText : "중간코드",
		dataType : "string",
		width : 130,
	}, {
		dataField : "kePartNumber",
		headerText : "부품번호",
		dataType : "string",
		width : 150,
	}, {
		dataField : "kePartName",
		headerText : "부품명",
		dataType : "string",
		width : 270,
	}, {
		dataField : "model",
		headerText : "KokusaiModel",
		dataType : "string",
	}, {
		dataField : "qty",
		headerText : "Qty",
		dataType : "numeric",
		width : 100,
		formatString : "###0",
		editRenderer : {
			type : "InputEditRenderer",
			onlyNumeric : true, // 0~9만 입력가능
		},
	}, {
		dataField : "unit",
		headerText : "Unit",
		dataType : "string",
		width : 130
	}, {
		dataField : "provide",
		headerText : "Provide",
		dataType : "string",
		width : 130
	}, {
		dataField : "discontinue",
		headerText : "Discontinue",
		dataType : "string",
		width : 200
	}, {
		dataField : "oid",
		headerText : "oid",
		dataType : "string",
		visible : false
	} ]

	const tbom_props = {
		headerHeight : 30,
		rowHeight : 30,
		showRowNumColumn : true,
		rowNumHeaderText : "번호",
		editable : false,
		fillColumnSizeMode : true
	};

	$(function() {
		tbomGridID = AUIGrid.create("#tbom_grid_wrap", tbom_columns, tbom_props);
		AUIGrid.setGridData(tbomGridID, <%=data.getTbomArr()%>);
	})

	$(window).resize(function() {
		AUIGrid.resize(tbomGridID);
	})
</script>