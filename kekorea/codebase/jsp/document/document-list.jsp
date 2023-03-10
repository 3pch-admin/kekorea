<%@page import="e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%
String root = DocumentHelper.ROOT;
%>
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
		<span>문서 조회</span>
		<!-- info search -->
		<jsp:include page="/jsp/common/search_info.jsp"></jsp:include>
	</div>
	<table class="container-table">
		<tr>
			<!-- only folder tree.. -->
			<jsp:include page="/jsp/common/layouts/include_tree.jsp">
				<jsp:param value="<%=root%>" name="root" />
				<jsp:param value="PRODUCT" name="context" />
			</jsp:include>
			<!-- container -->
			<td id="container_td">
				<table class="search_table">
					<tr>
						<th>문서분류</th>
						<td colspan="7">
							<input type="hidden" name="location" value="<%=root%>">
							<span id="location"><%=root%></span>
						</td>
					</tr>
					<tr>
						<th>문서제목</th>
						<td>
							<input type="text" name="name" id="name" class="AXInput wid200">
						</td>
						<th>문서번호</th>
						<td>
							<input type="text" name="number" class="AXInput wid200">
						</td>
						<th>설명</th>
						<td>
							<input type="text" name="description" class="AXInput wid200">
						</td>
						<th>상태</th>
						<td>
							<select name="state" id="state" class="AXSelect wid200">
								<option value="">선택</option>
								<%-- 					<option value="<%=state.name()%>"><%=state.getDisplay()%></option> --%>
							</select>
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
						<td colspan="3">
							<label title="최신버전">
								<input type="radio" name="latest" value="true" checked="checked">
								<span class="latest">최신버전</span>
							</label>
							<label title="모든버전">
								<input type="radio" name="latest" value="false">
								<span class="latest">모든버전</span>
							</label>
						</td>
					</tr>
				</table>

				<!-- button table -->
				<table class="btn_table">
					<tr>
						<td class="left">
							<input type="button" value="등록" class="blueBtn" id="createBtn" title="등록">
						</td>
						<td class="right">
							<input type="button" value="삭제" class="redBtn" id="deleteListDocBtn" title="삭제">
							<input type="button" value="상세조회" class="orangeBtn" id="detailEpmBtn" title="상세조회">
							<input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회">
							<%-- 				<input type="button" value="초기화" class="" id="initGrid" title="초기화" data-location="<%=root%>"> --%>
						</td>
					</tr>
				</table>
				<div id="grid_wrap" style="height: 650px; border-top: 1px solid #3180c3;"></div>
			</td>
		</tr>
	</table>
</body>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "name",
		headerText : "문서제목",
		dataType : "string",
		width : 300
	}, {
		dataField : "number",
		headerText : "문서번호",
		dataType : "string",
		width : 120
	}, {
		dataField : "description",
		headerText : "설명",
		dataType : "string",
		style : "left",
		width : 400
	}, {
		dataField : "location",
		headerText : "문서분류",
		dataType : "string",
		width : 200
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 100
	}, {
		dataField : "version",
		headerText : "버전",
		dataType : "string",
		width : 80
	}, {
		dataField : "creator",
		headerText : "작성자",
		dataType : "string",
		width : 100
	}, {
		dataField : "createDate",
		headerText : "작성일",
		dataType : "string",
		width : 120
	}, {
		dataField : "modifier",
		headerText : "수정자",
		dataType : "string",
		width : 100
	}, {
		dataField : "modifyDate",
		headerText : "수정일",
		dataType : "string",
		width : 120
	}, {
		dataField : "",
		headerText : "파일",
		dataType : "string",
	}, {
		dataField : "oid",
		headerText : "oid",
		dataType : "string",
		visible : false
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			rowIdField : "oid",
			headerHeight : 30,
			rowHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			fillColumnSizeMode : true, // 화면 꽉채우기
		};
		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		loadGridData();
		// LazyLoading 바인딩
		AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);
		AUIGrid.bind(myGridID, "cellClick", function(event) {
			let dataField = event.dataField;
			let oid = event.item.oid;
			if ("name" === dataField || "number" === dataField) {
				// 뷰 생성
			}
		});
	}

	function loadGridData() {
		let params = new Object();
		let url = getCallUrl("/document/list");
		AUIGrid.showAjaxLoader(myGridID);
		parent.openLayer();
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			$("input[name=sessionid]").val(data.sessionid);
			$("input[name=curPage]").val(data.curPage);
			AUIGrid.setGridData(myGridID, data.list);
			// 			parent.close();
			parent.closeLayer();
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

		// 등록페이지
		$("#createBtn").click(function() {
			let url = getCallUrl("/document/create");
			popup(url, 1400, 570);
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