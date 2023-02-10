<%@page import="e3ps.doc.service.DocumentHelper"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
// folder root
String root = DocumentHelper.DEFAULT;
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
</head>
<body>
	<input type="hidden" name="sessionid" id="sessionid">
	<input type="hidden" name="curPage" id="curPage">
	<!-- list header title -->
	<div class="header_title">
		<i class="axi axi-subtitles"></i>
		<span>첨부파일조회</span>
		<!-- info search -->
		<jsp:include page="/jsp/common/search_info.jsp"></jsp:include>
	</div>
	<!-- only folder tree.. -->
	<%-- 	<jsp:include page="/jsp/common/layouts/include_tree.jsp"> --%>
	<%-- 		<jsp:param value="<%=root%>" name="root" /> --%>
	<%-- 		<jsp:param value="PRODUCT" name="context" /> --%>
	<%-- 	</jsp:include> --%>
	<!-- search table -->
	<table class="search_table">
		<tr>
			<th>저장위치</th>
			<td colspan="7">
				<input type="hidden" name="location" value="<%=root%>">
				<span id="location"><%=root%></span>
			</td>
		</tr>
		<tr>
			<th>파일이름</th>
			<td>
				<input type="text" name="fileName" class="AXInput wid200">
			</td>
			<th>문서이름</th>
			<td>
				<input type="text" name="name" class="AXInput wid200">
			</td>
			<!-- <th>MODEL_NAME</th>
						<td>
							<input type="text" name="MODEL_NAME" class="AXInput wid200">
						</td>	 -->
			<th>설명</th>
			<td>
				<input type="text" name="description" class="AXInput wid200">
			</td>
			<th>문서번호</th>
			<td>
				<input type="text" name="number" class="AXInput wid200">
			</td>
		</tr>
		<tr>
			<th>수정자</th>
			<td>
				<input type="text" name="modifier" id="modifier" class="AXInput wid200" data-dbl="true">
				<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="modifier"></i>
			</td>
			<th>수정일</th>
			<td>
				<input type="text" name="predate_m" id="predate_m" class="AXInput">
				~
				<input type="text" name="postdate_m" id="postdate_m" class="AXInput twinDatePicker_m" data-start="predate_m">
				<i title="삭제" class="axi axi-ion-close-circled delete-calendar" data-start="predate_m" data-end="postdate_m"></i>
			</td>
			<th>버전</th>
			<td>
				<label title="최신버전">
					<input type="radio" name="latest" value="true" checked="checked">
					<span class="latest">최신버전</span>
				</label>
				<label title="모든버전">
					<input type="radio" name="latest" value="false">
					<span class="latest">모든버전</span>
				</label>
			</td>
			<th>상태</th>
			<td>
				<select name="statesDoc" id="statesDoc" class="AXSelect wid200">
					<option value="">선택</option>
					<%-- 						<% --%>
					<!--  						for (StateKeys state : states) { -->
					<%-- 						%> --%>
					<%-- 						<option value="<%=state.name()%>"><%=state.getDisplay()%></option> --%>
					<%-- 						<% --%>
					<!--  						} -->
					<%-- 						%> --%>
				</select>
			</td>
		</tr>
		<!-- 					<tr>	 -->
		<!-- 						<th>검색 결과 값 조회</th> -->
		<!-- 						<td colspan="3"> -->
		<!-- 							<input type="text" class="AXInput wid200" id="table_search" name="table_search" placeholder="테이블 내 결과값 검색">&nbsp; -->
		<!-- 							<i class="axi axi-ion-android-search" id="table_search_icon" title="테이블 내 검색"></i> -->
		<!-- 						</td> -->
		<!-- 					</tr>									 -->
	</table>

	<!-- button table -->
	<table class="btn_table">
		<tr>
			<!-- 				start sub table -->
			<td class="left">
				<!-- list jsp.. -->
				<div class="non_paging_layer">
					<span class="left_sub_folder_search">
						<label title="하위폴더검색">
							<input name="sub_folder" id="sub_folder" type="checkbox" value="ok" checked="checked">
							하위폴더검색
						</label>
					</span>
					<span class="count_span">
						<span id="count_text"></span>
					</span>
					<select name="paging_count" id="paging_count" class="AXSelectSmall">
						<option value="15">15</option>
						<option value="30">30</option>
						<option value="50">50</option>
						<option value="100">100</option>
					</select>
				</div>
			</td>
			<!-- 				end button table  -->
			<td class="right">
				<input type="button" value="다운로드" class="redBtn" id="downDocContents" title="다운로드">
				<input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회">
				<input type="button" value="초기화" class="" id="initGrid" title="초기화">
			</td>
		</tr>
	</table>
	<!-- end sub_table -->
	<div id="grid_wrap" style="height: 740px; border-top: 1px solid #3180c3;"></div>
</body>
<script type="text/javascript">
	let myGridID;
	const columns = [ {

		dataField : "filename",
		headerText : "파일이름",
		width : 250

	}, {

		dataField : "name",
		headerText : "문서제목",
		width : 220

	}, {

		dataField : "number",
		headerText : "문서번호",
		width : 220

	}, {

		dataField : "description",
		headerText : "설명",
		width : 300
	}, {

		dataField : "version",
		headerText : "버전",
		width : 100

	}, {

		dataField : "state",
		headerText : "상태",
		width : 100

	}, {

		dataField : "modifier",
		headerText : "수정자",
		width : 150

	}, {

		dataField : "modifyDate",
		headerText : "수정일",
		width : 150

	}, {

		dataField : "primary",
		headerText : "파일",
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
		};
		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		loadGridData();
		// LazyLoading 바인딩
		AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);

	}

	function loadGridData() {
		let params = new Object();
		let url = getCallUrl("/document/listContents");
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