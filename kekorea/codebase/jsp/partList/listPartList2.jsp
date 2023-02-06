<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/jsp/common/layouts/include_css.jsp"%>
<%@include file="/jsp/common/layouts/include_script.jsp"%>
<!-- auigrid -->
<%@include file="/jsp/include/auigrid.jsp"%>
</head>
<body onload="loadGridData();">
	<input type="hidden" name="sessionid" id="sessionid">
	<input type="hidden" name="curPage" id="curPage">
	<table class="search_table">
		<tr>
			<th>수배표 제목</th>
			<td>
				<input type="text" name="name" class="AXInput wid200">
			</td>
			<th>상태</th>
			<td>
				<select name="statesDoc" id="statesDoc" class="AXSelect wid200">
					<option value="">선택</option>
<%-- 					<% --%>
<!--  						for(StateKeys state : states) { -->
<%-- 					%> --%>
<%-- 					<option value="<%=state.name() %>"><%=state.getDisplay() %></option> --%>
<%-- 					<% --%>
<!--  						} -->
<%-- 					%> --%>
				</select>
			</td>						
			<th>KEK 작번</th>
			<td>
				<input type="text" name="kekNumber" class="AXInput wid200">
			</td>
			<th>KE 작번</th>
			<td>
				<input type="text" name="keNumber" class="AXInput wid200">
			</td>
		</tr>
		<tr>
			<th>설명</th>
			<td>
				<input type="text" name="description" class="AXInput wid200">
			</td>
			<th>설계 구분</th>
			<td>
				<select name="engType" id="engType" class="AXSelect wid100">
					<option value="">선택</option>
					<option value="개조">개조</option>
					<option value="견적">견적</option>
					<option value="양산">양산</option>
					<option value="연구개발">연구개발</option>
					<option value="이설">이설</option>
					<option value="판매">판매</option>
					<option value="평가용">평가용</option>
				</select>
			</td>
			<th>막종</th>
			<td>
				<input type="text" name="mak" class="AXInput wid200">
			</td>
			<th>작업 내용</th>
			<td>
				<input type="text" name="pDescription" class="AXInput wid200">
			</td>
		</tr>														
		<tr class="detailEpm">
			<th>작성자</th>
			<td>
				<input type="text" name="creators" id="creators" class="AXInput wid200" data-dbl="true"> 
				<input type="hidden" name="creatorsOid" id="creatorsOid" class="AXInput wid200" data-dbl="true"> 
				<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="creators"></i>
			</td>
			<th>작성일</th>
			<td>
				<input type="text" name="predate" id="predate" class="AXInput"> ~ 
				<input type="text" name="postdate" id="postdate" class="AXInput twinDatePicker" data-start="predate"> 
				<i title="삭제" class="axi axi-ion-close-circled delete-calendar" data-start="predate" data-end="postdate"></i>
			</td>
			<th>수정자</th>
			<td>
				<input type="text" name="modifier" id="modifier" class="AXInput wid200" data-dbl="true"> 
				<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="creators"></i>
			</td>
			<th>수정일</th>
			<td>
				<input type="text" name="predate_m" id="predate_m" class="AXInput"> ~ 
				<input type="text" name="pstdate_m" id="postdate_m" class="AXInput twinDatePicker_m" data-start="predate_m"> 
				<i title="삭제" class="axi axi-ion-close-circled delete-calendar" data-start="predate_m" data-end="postdate_m"></i>
			</td>
		</tr>
	</table>
	<!-- button table -->
	<table class="btn_table">
		<tr>
			<td class="right">
				<input type="button" value="삭제" class="redBtn" id="deletePartListBtn" title="삭제"> 
				<input type="button" value="상세조회" class="orangeBtn" id="detailEpmBtn" title="상세조회"> 
				<input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회"> 
<%-- 				<input type="button" value="초기화" class="" id="initGrid" title="초기화" data-location="<%=root %>"> --%>
			</td>
		</tr>
	</table> 
	<div id="grid_wrap" style="height: 650px; border-top: 1px solid #3180c3;"></div>
</body>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "",
		headerText : "설계구분",
		dataType : "string",
		width : 80
	}, {
		dataField : "",
		headerText : "",
		dataType : "string",
		width : 40
	}, {
		dataField : "",
		headerText : "수배표제목",
		dataType : "string",
		style : "left",
		width : 300
	}, {
		dataField : "mak",
		headerText : "막종",
		dataType : "string",
		width : 100
	}, {
		dataField : "",
		headerText : "kek 작번",
		dataType : "string",
		width : 100
	}, {
		dataField : "",
		headerText : "ke 작번",
		dataType : "string",
		width : 100
	}, {
		dataField : "",
		headerText : "user id",
		dataType : "string",
		width : 100
	}, {
		dataField : "",
		headerText : "작업내용",
		dataType : "string",
		width : 300
	}, {
		dataField : "",
		headerText : "거래처",
		dataType : "string",
		width : 100
	}, {
		dataField : "",
		headerText : "설치 장소",
		dataType : "string",
		width : 100
	}, {
		dataField : "",
		headerText : "발행일",
		dataType : "string",
		width : 100
	}, {
		dataField : "",
		headerText : "모델",
		dataType : "string",
		width : 100
	}, {
		dataField : "",
		headerText : "작성자",
		dataType : "string",
		width : 100
	}, {
		dataField : "",
		headerText : "작성일",
		dataType : "string",
		width : 100
	}, {
		dataField : "",
		headerText : "수정일",
		dataType : "string",
		width : 100
	}, {
		dataField : "",
		headerText : "상태",
		dataType : "string",
		width : 100
	} ]
	
	const props = {
			rowIdField : "oid",
			headerHeight : 30,
			rowHeight : 30,
			showRowCheckColumn : true,
			showRowNumColumn : true,
			rowNumHeaderText : "번호"
	};
	myGridID = AUIGrid.create("#grid_wrap", columns, props);
	
	function loadGridData() {
		let params = new Object();
		let url = getCallUrl("/partlist/list");
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
		// resize ...
		$(parent.document).find("#toggle").click(function() {
			$(parent.document).find("#body").css("width", "100vw");
			AUIGrid.resize("#grid_wrap");
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