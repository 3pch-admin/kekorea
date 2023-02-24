<%@page import="e3ps.bom.tbom.beans.TBOMMasterViewData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
TBOMMasterViewData data = (TBOMMasterViewData) request.getAttribute("data");
%>
<%@include file="/jsp/include/auigrid.jsp"%>
<table class="btn_table">
	<tr>
		<td>
			<div class="header_title">
				<i class="axi axi-subtitles"></i>
				<span>T-BOM 정보</span>
			</div>
		</td>
		<td class="right">
			<input type="button" value="닫기" id="closeBtn" title="닫기" class="redBtn">
		</td>
	</tr>
</table>

<div id="grid_wrap" style="height: 860px; border-top: 1px solid #3180c3; margin-top: 5px;"></div>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
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

	const props = {
		headerHeight : 30,
		rowHeight : 30,
		showRowNumColumn : true,
		rowNumHeaderText : "번호",
		editable : false,
		fillColumnSizeMode : true
	};

	$(function() {
		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		AUIGrid.setGridData(myGridID, <%=data.getTbomArr()%>);
		
		
		$("#closeBtn").click(function() {
			self.close();
		})
		
	})

	$(window).resize(function() {
		AUIGrid.resize(myGridID);
	})
</script>