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
			<i class="axi axi-subtitles"></i><span>검토함</span>
			<!-- info search -->
			<jsp:include page="/jsp/common/search_info.jsp"></jsp:include>
		</div>
		<table class="search_table">
			<tr>
				<th>결재제목</th>
				<td colspan="3">
					<input type="text" name="name" class="AXInput wid300">
				</td>
			</tr>
			<tr>
				<th>기안자</th>
				<td>
					<input type="text" name="creators" id="creators" class="AXInput wid200" data-dbl="true"> 
					<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="creators"></i>
				</td>
				<th>수신일</th>
				<td>
					<input type="text" name="predate" id="predate" class="AXInput"> ~ 
					<input type="text" name="postdate" id="postdate" class="AXInput twinDatePicker" data-start="predate"> 
					<i title="삭제" class="axi axi-ion-close-circled delete-calendar" data-start="predate" data-end="postdate"></i>
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
		<div id="grid_wrap" style="height: 650px; border-top: 1px solid #3180c3;"></div>
</body>
<script type="text/javascript">
	let myGridID;
	const columns =[ {
		dataField : "read",
        headerText : "확인",
        dataType : "string",
        width : 100
        
	},{ 
		 
        dataField : "type",
        headerText : "구분",
        dataType : "string",
        width : 100
	},{
		
		dataField : "role",
		headerText : "역할",
        dataType : "string",
		width : 100
	},{
		
		dataField : "name",
		headerText : "결재제목",
        dataType : "string",
		width : 600
	},{
		
		dataField : "ingPoint",
		headerText : "진행단계",
        dataType : "string",
		width : 400
	},{
		
		dataField : "submiter",
		headerText : "기안자",
        dataType : "string",
		width : 100
	},{
		
		dataField : "state",
		headerText : "상태",
        dataType : "string",
		width : 100
	},{
		
		dataField : "receiveTime",
		headerText : "수신일",
        dataType : "string",
		width : 115
	},{
		
		dataField : "oid",
		headerText : "oid",
        dataType : "string",
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
		let url = getCallUrl("/approval/listAgree");
		AUIGrid.showAjaxLoader(myGridID);
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			$("input[name=sessionid]").val(data.sessionid);
			$("input[name=curPage]").val(data.curPage);
			AUIGrid.setGridData(myGridID, data.list);
			parent.close();
		})
	}
	
	window.onresize = function () {

		// 크기가 변경되었을 때 AUIGrid.resize() 함수 호출 
		if (typeof myGridID !== "undefined") {
			AUIGrid.resize(myGridID);
		}
	};
	
	$(function() {
		// resize ...
		$(parent.document).find("#toggle").click(function() {
			$(parent.document).find("#body").css("width", "100vw");
			AUIGrid.resize("#grid_wrap");
// 			AUIGrid.resize(myGridID);
		})

		$("#searchBtn").click(function() {
			loadGridData();
		})

		// 셀렉트박스 바인딩
		select("state");
		// 라디오박스 바인딩
		radio("latest");
		$(document).setHTML();
	}).keypress(function(e) {
		let keyCode = e.keyCode;
		if (keyCode == 13) {
			loadGridData();
		}
	})
</script>
</html>