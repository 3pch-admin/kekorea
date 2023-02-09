<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/jsp/common/layouts/include_css.jsp"%>
<%@include file="/jsp/common/layouts/include_script.jsp"%>
<!-- auigrid -->
<%@include file="/jsp/include/auigrid.jsp"%>
<body>
	<input type="hidden" name="sessionid" id="sessionid">
	<input type="hidden" name="curPage" id="curPage">
	<!-- list header title -->
	<div class="header_title">
		<i class="axi axi-subtitles"></i><span>반려함</span>
		<!-- info search -->
		<jsp:include page="/jsp/common/search_info.jsp"></jsp:include>
	</div>
	<!-- search table -->
	<table class="search_table">
		<tr>
			<th>결재제목</th>
			<td colspan="3">
				<input type="text" name="name" class="AXInput wid300">
				<input type="text" style="display: none;" class="AXInput wid300">
			</td>
		</tr>
	</table> 
	
	<!-- button table -->
	<table class="btn_table">
		<tr>
			<td class="right">
				 <input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회"> 
				 <input type="button" value="초기화" class="" id="initGrid" title="초기화">
			</td>
		</tr>
	</table> 
	<!-- end button table --> 
	<div id="grid_wrap" style="height: 650px; border-top: 1px solid #3180c3;"></div>
</body>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		
		dataField : "name",
		headerText : "결재제목",
		width : "40%"
	},{
		
		dataField : "returnPoint",
		headerText : "반려단계",
		width : "40%"
	},{
		
		dataField : "createDate",
		headerText : "기안일",
		width : "10%"
	},{
		
		dataField : "completeTime",
		headerText : "반려일",
		width : "10%"
	},{
		
		dataField : "oid",
		headerText : "oid",
		visible : false
	} ];
	
	const props = {
		rowIdField : "oid",
		headerHeight : 30,
		rowHeight : 30,
		showRowNumColumn : true,
		rowNumHeaderText : "번호"
		};
	myGridID = AUIGrid.create("#grid_wrap", columns, props);
	loadGridData();	
	
	function loadGridData() {
		let params = new Object();
		let url = getCallUrl("/approval/listReturn");
		AUIGrid.showAjaxLoader(myGridID);
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			$("input[name=sessionid]").val(data.sessionid);
			$("input[name=curPage]").val(data.curPage);
			AUIGrid.setGridData(myGridID, data.list);
			parent.close();
		});
	}
	
	$(function() {
		$("#searchBtn").click(function() {
			loadGridData();
		});
	}).keypress(function(e) {
		let keyCode = e.keyCode;
		if (keyCode == 13) {
			loadGridData();
		}
	});
</script>
</html>