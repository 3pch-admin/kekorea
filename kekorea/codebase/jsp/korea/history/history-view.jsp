<%@page import="java.util.Map"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.korea.cip.dto.CipDTO"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<Map<String, Object>> list = (ArrayList<Map<String, Object>>) request.getAttribute("list");
ArrayList<Map<String, Object>> headers = (ArrayList<Map<String, Object>>) request.getAttribute("headers");
JSONArray data = JSONArray.fromObject(list);
%>
<%@include file="/jsp/include/auigrid.jsp"%>
<table class="btn_table">
	<tr>
		<td class="right">
			<input type="button" value="닫기" id="closeBtn" title="닫기" class="blueBtn">
		</td>
	</tr>
</table>
<div id="grid_wrap" style="height: 900px; border-top: 1px solid #3180c3;"></div>

<script type="text/javascript">
	let myGridID;
	let data = <%=data%>
	const columns = [ {
		dataField : "pDate",
		headerText : "발행일",
		dataType : "string",
		width : 120,
	}, {
		dataField : "install",
		headerText : "설치장소",
		dataType : "string",
		width : 100,
	}, {
		dataField : "kekNumber",
		headerText : "KEK작번",
		dataType : "string",
		width : 140,
	}, {
		dataField : "keNumber",
		headerText : "KE작번",
		dataType : "string",
		width : 140,
	}, {
		dataField : "tuv",
		headerText : "TUV유무",
		dataType : "string",
		width : 130
	}, 
	<%for (Map<String, Object> header : headers) {
	String dataField = (String) header.get("key");
	String headerText = (String) header.get("value");%>
	{
		dataField : "<%=dataField%>",
		headerText : "<%=headerText%>",
		dataType : "string",
		width : 150,
	},
	<%}%>
	{
		dataField : "oid",
		headerText : "oid",
		dataType : "string",
		visible : false
	},
	{
		dataField : "poid",
		headerText : "poid",
		dataType : "string",
		visible : false
	}]


	function createAUIGrid(columns) {
		const props = {
			rowIdField : "oid",
			headerHeight : 30,
			rowHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			fixedColumnCount : 5,
			editable : true,
			showStateColumn : true,
			noDataMessage : "검색 결과가 없습니다.",
		};
		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		AUIGrid.setGridData(myGridID, data);
	}

	$(function() {
		createAUIGrid(columns);
		
		$("#closeBtn").click(function() {
			self.close();
		})
	})
	
	$(window).resize(function() {
		AUIGrid.resize(myGridID);
	})
</script>