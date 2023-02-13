<%@page import="e3ps.org.service.OrgHelper"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
// dept root
String root = OrgHelper.DEPARTMENT_ROOT;

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
<title>조직도</title>
<%@include file="/jsp/common/layouts/include_css.jsp"%>
<%@include file="/jsp/common/layouts/include_script.jsp"%>
<!-- auigrid -->
<%@include file="/jsp/include/auigrid.jsp"%>
</head>
<body>
	<input type="hidden" name="sessionid" id="sessionid">
	<input type="hidden" name="curPage" id="curPage">
	<!-- list header title -->
	<div class="header_title">
		<i class="axi axi-subtitles"></i>
		<span>조직도</span>
		<!-- info search -->
		<jsp:include page="/jsp/common/search_info.jsp"></jsp:include>
	</div>
	<table class="container_table">
		<tr>
			<!-- only folder tree.. -->
			<jsp:include page="/jsp/common/layouts/include_dept.jsp" />
			<td id="container_td">
				<!-- search table -->
				<table class="search_table">
					<tr>
						<th>부서</th>
						<td>
							<input type="hidden" name="deptOid" id="deptOid">
							<span id="deptName"><%=root%></span>
						</td>
						<th>퇴사여부</th>
						<td>
							<select name="resigns" id="resigns" class="AXSelect wid100">
								<option value="">선택</option>
								<option value="false" selected="selected">재직중</option>
								<option value="true">퇴사</option>
							</select>
						</td>
					</tr>
					<tr>
						<th>아이디</th>
						<td>
							<input type="text" name="id" class="AXInput wid200">
						</td>
						<th>이름</th>
						<td>
							<input type="text" name="name" class="AXInput wid200">
						</td>
					</tr>
				</table>

				<!-- start sub table -->
				<!-- <table class="sub_table"> -->
				<table class="btn_table">
					<tr>
						<td class="right">
							<%
							if (isAdmin) {
							%>
							<input type="button" value="퇴사처리" class="redBtn" id="resignListUserBtn" title="퇴사처리">
							<%
							}
							%>
							<input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회">
							<input type="button" value="초기화" class="" id="initGrid" title="초기화">
						</td>
					</tr>
				</table>
				<!-- end sub_table -->
				<div id="grid_wrap" style="height: 650px; border-top: 1px solid #3180c3;"></div>
			</td>
		</tr>
	</table>
</body>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "name",
		headerText : "사용자 이름",
		width : 250
	}, {

		dataField : "id",
		headerText : "사용자 아이디",
		width : 200
	}, {

		dataField : "duty",
		headerText : "직급",
		width : 150
	}, {

		dataField : "departmentName",
		headerText : "부서",
		width : 180
	}, {

		dataField : "email",
		headerText : "이메일",
		width : 250
	}, {

		dataField : "resign",
		headerText : "퇴사여부",
		width : 150
	}, {

		dataField : "createDate",
		headerText : "등록일",
		width : 200
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
		};
		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		loadGridData();
		// LazyLoading 바인딩
		AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);
	}

	function loadGridData() {
		let params = new Object();
		let url = getCallUrl("/org/viewOrg");
		AUIGrid.showAjaxLoader(myGridID);
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			$("input[name=sessionid]").val(data.sessionid);
			$("input[name=curPage]").val(data.curPage);
			AUIGrid.setGridData(myGridID, data.list);
			parent.close();
		})
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
		let url = getCallUrl("/appendData");
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