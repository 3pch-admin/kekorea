<%@page import="net.sf.json.JSONArray"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
int count = (int) request.getAttribute("count"); // 비교 개수..
ArrayList<Map<String, Object>>  headers = (ArrayList<Map<String, Object>>)request.getAttribute("headers");
JSONArray compareData = (JSONArray)request.getAttribute("compareData");
%>
<%@include file="/jsp/include/auigrid.jsp"%>
<table class="btn_table">
	<tr>
		<td>
			<div class="header_title">
				<i class="axi axi-subtitles"></i>
				<span>T-BOM 비교</span>
			</div>
		</td>
		<td class="right">
			<input type="button" value="닫기" id="closeBtn" title="닫기" class="blueBtn">
		</td>
	</tr>
</table>
<div id="grid_wrap" style="height: 650px; border-top: 1px solid #3180c3;"></div>
<style type="text/css">
.compare {
	font-weight: bold;
	color: red;
	background-color: #c1fbd1;
}
</style>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "lotNo",
		headerText : "LOT",
		dataType : "string",
		width : 80
	}, {
		dataField : "code",
		headerText : "중간코드",
		dataType : "string",
		width : 120
	},
	<%
		for(int i=0; i<headers.size(); i++) {
			Map<String, Object> header = (Map<String, Object>)headers.get(i);
	%>
	{
		 headerText : "<%=header.get("name"+i)%>",
	     children : [{
	             dataField : "kePartNumber<%=i%>",
	             headerText : "부품번호",
	             width : 120
	     }, {
             dataField : "kePartName<%=i%>",
             headerText : "부품명",
             width : 180
     	}, {
            dataField : "qty<%=i%>",
            headerText : "수량",
            width : 100,
            styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
            	console.log(value + ", = " + item.qty<%=i%>);
            	if(value != item.qty1) {
            		return "compare";
            	}
           	}             
    	}]
	},
	<%
		}
	%>
	]

	function createAUIGrid(columnLayout) {
		const props = {
			rowIdField : "oid",
			headerHeight : 30,
			rowHeight : 30,
			rowNumHeaderText : "번호",
// 			noDataMessage : "검색 결과가 없습니다.",
		};
		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		AUIGrid.setGridData(myGridID, <%=compareData%>);
	}
	
	$(function() {
		createAUIGrid(columns);
		$("#closeBtn").click(function() {
			self.close();
		})
	})

	$(window).resize(function() {
		AUIGrid.resize(tbomGridID);
	})
</script>