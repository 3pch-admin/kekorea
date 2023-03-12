<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.admin.commonCode.CommonCodeType"%>
<%@page import="org.json.JSONArray"%>
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
</head>
<body>
	<input type="hidden" name="sessionid" id="sessionid">
	<input type="hidden" name="curPage" id="curPage">
	<table class="search_table">
		<colgroup>
			<col width="130">
			<col width="800">
			<col width="130">
			<col width="800">
		</colgroup>
		<tr>
			<th>템플릿 이름</th>
			<td>
				<input type="text" name="templateName" id="templateName" class="AXInput wid200">
			</td>
			<th>기간</th>
			<td>
				<input type="text" name="duration" class="AXInput wid200">
			</td>
		</tr>
		<tr>
			<th>작성자</th>
			<td>
				<input type="text" name="creators" id="creators" class="AXInput wid200" data-dbl="true">
				<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="creators"></i>
			</td>
			<th>작성일</th>
			<td>
				<input type="text" name="predate" id="predate" class="AXInput">
				~
				<input type="text" name="postdate" id="postdate" class="AXInput">
				<i title="삭제" class="axi axi-ion-close-circled delete-calendar" data-start="predate" data-end="postdate"></i>
			</td>
		</tr>
	</table>
	<table class="btn_table">
		<tr>
			<td class="left">
				<input type="button" value="등록" class="blueBtn" id="createBtn" title="등록">
			</td>
			<td class="right">
				<input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회">
				<input type="button" value="초기화" class="" id="initGrid" title="초기화">
			</td>
		</tr>
	</table>
	<div id="grid_wrap" style="height: 700px; border-top: 1px solid #3180c3;"></div>
</body>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "name",
		headerText : "템플릿 명",
		dataType : "string",
		width : 300,
	}, {
		dataField : "description",
		headerText : "설명",
		dataType : "string",
		style : "left indent10"
	}, {
		dataField : "enable",
		headerText : "사용여부",
		dataType : "boolean",
		width : 100,
		renderer : {
			type : "CheckBoxEditRenderer"
		}
	}, {
		dataField : "duration",
		headerText : "기간",
		dataType : "numeric",
		postfix : "일",
		width : 100
	}, {
		dataField : "creator",
		headerText : "작성자",
		dataType : "string",
		width : 80
	}, {
		dataField : "createDate",
		headerText : "설명",
		dataType : "date",
		formatString : "yyyy-mm-dd",
		width : 120
	}, {
		dataField : "oid",
		headerText : "oid",
		dataType : "string",
		visible : false
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			rowIdField : "rowId",
			headerHeight : 30,
			rowHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			fillColumnSizeMode : true
		};

		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		loadGridData();
		// LazyLoading 바인딩
		AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);
		AUIGrid.bind(myGridID, "cellClick", auiCellClickHandler);
	}

	function auiCellClickHandler(event) {
		let oid = event.item.oid;
		let dataField = event.dataField;
		if (dataField === "name" || dataField === "description") {
			let url = getCallUrl("/template/view?oid=" + oid);
			popup(url);
		}
	}

	function loadGridData() {
		let params = new Object();
		let url = getCallUrl("/template/list");
		
		let templateName = document.getElementById("templateName").value;
		params.templateName = templateName;
		AUIGrid.showAjaxLoader(myGridID);
		params = form(params, "search_table");
		parent.openLayer();
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			$("input[name=sessionid]").val(data.sessionid);
			$("input[name=curPage]").val(data.curPage);
			AUIGrid.setGridData(myGridID, data.list);
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
		rangeDate("postdate", "predate");

		createAUIGrid(columns);
		$("#searchBtn").click(function() {
			loadGridData();
		})

		// 등록페이지
		$("#createBtn").click(function() {
			let url = getCallUrl("/template/create");
			popup(url, 1200, 420);
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