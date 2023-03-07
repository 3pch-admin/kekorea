<%@page import="e3ps.common.util.CommonUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
// admin
boolean isAdmin = CommonUtils.isAdmin();

boolean isBox = true;
if (isAdmin) {
	isBox = true;
}
%>
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
		<i class="axi axi-subtitles"></i>
		<span>완료함</span>
		<!-- info search -->
		<jsp:include page="/jsp/common/search_info.jsp"></jsp:include>
	</div>
	<!-- search table -->
	<table class="search_table">
		<tr>
			<th>결재제목</th>
			<td>
				<input type="text" name="name" class="AXInput wid300">
			</td>
			<th>구분</th>
			<td>
				<!--  검토 결재 수신 -->
				<select id="type" name="type" class="AXSelect wid200">
					<option value="">선택</option>
					<option value="검토">검토</option>
					<option value="결재">결재</option>
					<option value="수신">수신</option>
				</select>
			</td>
		</tr>
		<tr>
			<!--			<th>기안자</th> -->
			<!--			<td> -->
			<!--				<input type="text" name="creators" id="creators" class="AXInput wid200" data-dbl="true">  -->
			<!--				<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="creators"></i> -->
			<!--			</td> -->
			<th>수신일</th>
			<td colspan="3">
				<input type="text" name="predate" id="predate" class="AXInput">
				~
				<input type="text" name="postdate" id="postdate" class="AXInput twinDatePicker" data-start="predate">
				<i title="삭제" class="axi axi-ion-close-circled delete-calendar" data-start="predate" data-end="postdate"></i>
			</td>
		</tr>
		<!-- <tr>	
			<th>검색 결과 값 조회</th>
			<td colspan="3">
				<input type="text" class="AXInput wid200" id="table_search" name="table_search" placeholder="테이블 내 결과값 검색">&nbsp;
				<i class="axi axi-ion-android-search" id="table_search_icon" title="테이블 내 검색"></i>
			</td>
		</tr>	 -->
	</table>

	<!-- button table -->
	<table class="btn_table">
		<tr>
			<td class="right">
				<%
				if (isAdmin) {
				%>
				<input type="button" value="결재 초기화" class="blueBtn" id="initAppLineBtn" title="결재 초기화">
				<%
				}
				%>
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
		width : 500
	}, {

		dataField : "ingPoint",
		headerText : "진행단계",
		width : 690
	}, {

		dataField : "submiter",
		headerText : "기안자",
		width : 100
	}, {

		dataField : "state",
		headerText : "상태",
		width : 100
	}, {

		dataField : "receiveTime",
		headerText : "수신일",
		width : 100
	}, {

		dataField : "completeTime",
		headerText : "완료일",
		width : 100
	}, {

		dataField : "oid",
		headerText : "oid",
		visible : false
	} ];

	function createAUIGrid(columnLayout) {
		const props = {
			rowIdField : "oid",
			headerHeight : 30,
			rowHeight : 30,
			showRowCheckColumn : true,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			fillColumnSizeMode : true, // 화면 꽉채우기
			enableCellMerge : true,
			cellMergePolicy : "withNull",
		};

		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		loadGridData();
		// LazyLoading 바인딩
		AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);
	}

	function loadGridData() {
		let params = new Object();
		let url = getCallUrl("/approval/listComplete");
		AUIGrid.showAjaxLoader(myGridID);
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			$("input[name=sessionid]").val(data.sessionid);
			$("input[name=curPage]").val(data.curPage);
			AUIGrid.setGridData(myGridID, data.list);
			parent.close();
		});
	}

	let last = false;
	function vScrollChangeHandler(event) {
		if (event.position == event.maxPosition) {
			if (!last) {
				requestAdditionalData();
			}
		}
	}

	function requestAdditionalData() {
		let params = new Object();
		let curPage = $("input[name=curPage]").val();
		params.sessionid = $("input[name=sessionid]").val();
		params.start = (curPage * 30);
		params.end = (curPage * 30) + 30;
		let url = getCallUrl("/aui/appendData");
		AUIGrid.showAjaxLoader(myGridID);
		call(url, params, function(data) {
			if (data.list.length == 0) {
				last = true;
				alert("마지막 데이터 입니다.");
				AUIGrid.removeAjaxLoader(myGridID);
			} else {
				AUIGrid.appendData(myGridID, data.list);
				AUIGrid.removeAjaxLoader(myGridID);
				$("input[name=curPage]").val(parseInt(curPage) + 1);
			}
		})
	}

	$(function() {
		createAUIGrid(columns);

		$("#searchBtn").click(function() {
			loadGridData();
		})

	}).keypress(function(e) {
		let keyCode = e.keyCode;
		if (keyCode == 13) {
			loadGridData();
		}
	})

	$(window).resize(function() {
		AUIGrid.resize(myGridID);
	})
</script>
</html>