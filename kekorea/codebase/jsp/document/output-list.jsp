<%@page import="e3ps.doc.service.DocumentHelper"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
// folder root
String root = DocumentHelper.OUTPUT_ROOT;
// admin
boolean isAdmin = CommonUtils.isAdmin();

String context = (String) request.getParameter("context");

if (context == null) {
	context = "new";
}

boolean isNew = false;
boolean isOld = false;
String nurl = "/Windchill/plm/document/listOutput?context=new";
String ourl = "/Windchill/plm/document/listOutput?context=old";

String title = "";
if ("new".equals(context)) {
	isNew = true;
	// 		module = ModuleKeys.list_output.name();
	title = "산출물";
	root = DocumentHelper.OUTPUT_ROOT;
} else if ("old".equals(context)) {
	isOld = true;
	// 		module = ModuleKeys.list_old_output.name();
	title = "구 산출물";
	root = DocumentHelper.OLDOUTPUT_ROOT;
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
</head>
<body>
	<input type="hidden" name="sessionid" id="sessionid">
	<input type="hidden" name="curPage" id="curPage">
	<!-- list header title -->
	<div class="header_title">
		<i class="axi axi-subtitles"></i>
		<span><%=title%>
			조회
		</span>
		<!-- info search -->
		<jsp:include page="/jsp/common/search_info.jsp"></jsp:include>
	</div>

	<!-- only folder tree.. -->
	<%-- 	<jsp:include page="/jsp/common/layouts/include_tree.jsp"> --%>
	<%-- 		<jsp:param value="<%=root%>" name="root" /> --%>
	<%-- 		<jsp:param value="PRODUCT" name="context" /> --%>
	<%-- 		<jsp:param value="output" name="type" /> --%>
	<%-- 	</jsp:include> --%>
	<!-- search table -->
	<table class="search_table">
		<tr>
			<th>산출물 분류</th>
			<td colspan="7">
				<input type="hidden" name="location" value="<%=root%>">
				<span id="location"><%=root%></span>
			</td>
		</tr>
		<tr>
			<th>산출물 제목</th>
			<td>
				<input type="text" name="name" class="AXInput wid200">
			</td>
			<th>산출물 번호</th>
			<td>
				<input type="text" name="number" class="AXInput wid200">
			</td>
			<th>설명</th>
			<td>
				<input type="text" name="description" class="AXInput wid200">
			</td>
			<th>KE 작번</th>
			<td>
				<input type="text" name="keNumber" class="AXInput wid200">
			</td>
		</tr>
		<tr>
			<th>KEK 작번</th>
			<td>
				<input type="text" name="kekNumber" class="AXInput wid200">
			</td>
			<th>막종</th>
			<td>
				<input type="text" name="mak" class="AXInput wid200">
			</td>
			<th>작업내용</th>
			<td colspan="3">
				<input type="text" name="kek_description" class="AXInput wid400">
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
				<input type="text" name="predate" id="predate" class="AXInput">
				~
				<input type="text" name="postdate" id="postdate" class="AXInput twinDatePicker" data-start="predate">
				<i title="삭제" class="axi axi-ion-close-circled delete-calendar" data-start="predate" data-end="postdate"></i>
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
					<%-- 								<% --%>
					<!--  									for(StateKeys state : states) { -->
					<%-- 								%> --%>
					<%-- 								<option value="<%=state.name() %>"><%=state.getDisplay() %></option> --%>
					<%-- 								<% --%>
					<!--  									} -->
					<%-- 								%> --%>
				</select>
			</td>
		</tr>
	</table>

	<!-- button table -->
	<table class="btn_table">
		<tr>
			<td class="left">
				<!-- list jsp.. -->
				<!-- 							<div class="non_paging_layer"> -->
<!-- 					<input type="button" value="등록" class="blueBtn" id="createBtn" title="등록" style="margin-left:50px;"> -->
				<div class="left" style="margin-right:5px;">
					<input type="button" value="등록" class="blueBtn" id="createBtn" title="등록" >
				</div>
				<div class="view_layer" style="margin-top:2px;">
					<ul>
						<li data-url="<%=nurl%>" <%if (isNew) {%> class="active_view" <%}%> id="newOutput_view" title="NEW">NEW</li>
						<li data-url="<%=ourl%>" <%if (isOld) {%> class="active_view" <%}%> id="oldOutput_view" title="OLD">OLD</li>
						<li class="hidden">
							<span id="thumbnail" title="썸네일 리스트로 확인하세요.">썸네일 리스트로 확인하세요 </span>
						</li>
					</ul>
					<span class="left2_sub_folder_search">
						<label title="하위폴더검색">
							<input name="sub_folder" id="sub_folder" type="checkbox" value="ok" checked="checked">
							하위폴더검색
						</label>
					</span>
					<!-- 								<span class="count_span"><span id="count_text"></span></span> -->
					<%-- 								<% --%>
					<!--  									String psize = OrgHelper.manager.getUserPaging(module); -->
					<%-- 								%> --%>
					<!-- 								<select name="paging_count" id="paging_count" class="AXSelectSmall"> -->
					<%-- 									<option value="15" <%if(psize.equals("15")) { %> selected="selected"  <%} %>>15</option> --%>
					<%-- 									<option value="30" <%if(psize.equals("30")) { %> selected="selected"  <%} %>>30</option> --%>
					<%-- 									<option value="50" <%if(psize.equals("50")) { %> selected="selected"  <%} %>>50</option> --%>
					<%-- 									<option value="100" <%if(psize.equals("100")) { %> selected="selected"  <%} %>>100</option> --%>
					<!-- 								</select> -->
				</div>
			</td>
			<td class="right">
<!-- 			<input type="button" value="등록" class="blueBtn" id="createBtn" title="등록"> -->
				<%
				if (isAdmin) {
				%>
				<input type="button" value="삭제" class="redBtn" id="deleteListDocBtn" title="삭제">
				<%
				}
				%>
				<input type="button" value="상세조회" class="orangeBtn" id="detailEpmBtn" title="상세조회">
				<input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회">
				<input type="button" value="초기화" class="" id="initGrid" title="초기화" data-location="<%=root%>">
			</td>
		</tr>
	</table>
	<!-- end button table -->
	<div id="grid_wrap" style="height: 740px; border-top: 1px solid #3180c3;"></div>
</body>
<script type="text/javascript">
	let myGridID;
	const columns = [ {

		dataField : "name",
		headerText : "산출물 제목",
		width : 200

	}, {

		dataField : "number",
		headerText : "산출물 번호",
		width : 130

	}, {

		dataField : "description",
		headerText : "설명",
		width : 300

	}, {

		dataField : "location",
		headerText : "산출물 분류",
		width : 200

	}, {

		dataField : "state",
		headerText : "상태",
		width : 60

	}, {

		dataField : "version",
		headerText : "버전",
		width : 60

	}, {

		dataField : "creator",
		headerText : "작성자",
		width : 80

	}, {

		dataField : "createDate",
		headerText : "작성일",
		width : 110

	}, {

		dataField : "modifier",
		headerText : "수정자",
		width : 80

	}, {

		dataField : "modifyDate",
		headerText : "수정일",
		width : 110

	}, {

		dataField : "primary",
		headerText : "파일",
		width : 60

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
		let url = getCallUrl("/document/listOutput");
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

		// 등록페이지
		$("#createBtn").click(function() {
			let url = getCallUrl("/document/createOutput");
			popup(url, 1400, 570);
		})
		
		// 그리드 행 삭제
		$("#deleteRowBtn").click(function() {
			let checkedItems = AUIGrid.getCheckedRowItems(myGridID);
			for (let i = checkedItems.length - 1; i >= 0; i--) {
				let rowIndex = checkedItems[i].rowIndex;
				AUIGrid.removeRow(myGridID, rowIndex);
			}
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