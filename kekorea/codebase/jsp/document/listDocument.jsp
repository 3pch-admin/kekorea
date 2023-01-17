<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/jsp/common/layouts/include_css.jsp"%>
<%@include file="/jsp/common/layouts/include_script.jsp"%>
</head>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "no",
		headerText : "번호",
		dataType : "string",
		width : 50
	}, {
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

	const props = {
		rowIdField : "oid",
		headerHeight : 30,
		rowHeight : 30,
		enableFilter : true,
		showRowCheckColumn : false,
		showRowNumColumn : false,
	};

	function loadGridData() {
		let params = new Object();
		let url = getCallUrl("/document/listDocumentAction");
		AUIGrid.showAjaxLoader(myGridID);
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			AUIGrid.setGridData(myGridID, data.list);
		})
	}

	$(function() {
		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		// resize ...
		$(parent.document).find("#toggle").click(function() {
			$(parent.document).find("#body").css("width", "100vw");
			AUIGrid.resize("#grid_wrap");
		})

		$("#searchBtn").click(function() {
			loadGridData();
		})

		AUIGrid.bind(myGridID, "cellClick", function(event) {
			let dataField = event.dataField;
			let oid = event.item.oid;
			if ("name" === dataField || "number" === dataField) {
				// 뷰 생성
			}
		});

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
<body onload="loadGridData();">
	<table class="search_table">
		<tr>
			<th>문서분류</th>
			<td colspan="7">
				<%-- 				<input type="hidden" name="location" value="<%=root%>"> --%>
				<%-- 				<span id="location"><%=root%></span> --%>
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
			<td class="right">
				<input type="button" value="삭제" class="redBtn" id="deleteListDocBtn" title="삭제">
				<input type="button" value="상세조회" class="orangeBtn" id="detailEpmBtn" title="상세조회">
				<input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회">
				<%-- 				<input type="button" value="초기화" class="" id="initGrid" title="초기화" data-location="<%=root%>"> --%>
			</td>
		</tr>
	</table>
	<div id="grid_wrap" style="height: 650px; border-top: 2px solid #3180c3;"></div>
</body>
</html>