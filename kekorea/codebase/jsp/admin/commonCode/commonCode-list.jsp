<%@page import="e3ps.admin.commonCode.CommonCodeType"%>
<%@page import="org.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
CommonCodeType[] codeTypes = (CommonCodeType[]) request.getAttribute("codeTypes");
JSONArray jsonList = (JSONArray) request.getAttribute("jsonList");
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
<body onload="loadGridData();">
	<input type="hidden" name="sessionid" id="sessionid">
	<input type="hidden" name="curPage" id="curPage">
	<table class="search_table">
		<tr>
			<th>코드 명</th>
			<td>
				<input type="text" name="name" id="name" class="wid200 AXInput">
			</td>
			<th>코드</th>
			<td>
				<input type="text" name="code" id="code" class="wid200 AXInput">
			</td>
			<th>코드 타입</th>
			<td>
				<select name="codeType" id="codeType" class="wid200 AXSelect">
					<option value="">선택</option>
					<%
					for (CommonCodeType codeType : codeTypes) {
					%>
					<option value="<%=codeType.toString()%>"><%=codeType.getDisplay()%></option>
					<%
					}
					%>
				</select>
			</td>
		</tr>
		<tr>
			<th>설명</th>
			<td colspan="3">
				<input type="text" name="description" id="description" class="wid500 AXInput">
			</td>
			<th>사용여부</th>
			<td>
				<select name="uses" id="uses" class="wid200 AXSelect">
					<option value="">선택</option>
					<option value="true">사용</option>
					<option value="false">미사용</option>
				</select>
			</td>
		</tr>
	</table>

	<!-- button table -->
	<table class="btn_table">
		<tr>
			<td class="right">
				<input type="button" value="추가" class="redBtn" id="addRowBtn" title="추가">
				<input type="button" value="삭제" class="orangeBtn" id="deleteRowBtn" title="삭제">
				<input type="button" value="저장" class="" id="saveBtn" title="저장">
				<input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회">
			</td>
		</tr>
	</table>
	<div id="grid_wrap" style="height: 650px; border-top: 1px solid #3180c3;"></div>
</body>
<script type="text/javascript">
	let myGridID;
	const jsonList = <%=jsonList%>
	const columns = [ {
		dataField : "name",
		headerText : "코드 명",
		dataType : "string",
		width : 300
	}, {
		dataField : "code",
		headerText : "코드",
		dataType : "string",
		width : 150
	}, {
		dataField : "codeType",
		headerText : "코드 타입",
		dataType : "string",
		width : 200,
		renderer : {
			type : "DropDownListRenderer",
			list : jsonList, //key-value Object 로 구성된 리스트
			keyField : "key", // key 에 해당되는 필드명
			valueField : "value" // value 에 해당되는 필드명
		},
		labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
			var retStr = ""; // key 값에 맞는 value 를 찾아 반환함.
			for (var i = 0, len = jsonList.length; i < len; i++) {
				if (jsonList[i]["key"] == value) {
					retStr = jsonList[i]["value"];
					break;
				}
			}
			return retStr == "" ? value : retStr;
		}
	}, {
		dataField : "description",
		headerText : "설명",
		dataType : "string",
		style : "left indent10",
	}, {
		dataField : "enable",
		headerText : "사용여부",
		dataType : "boolean",
		width : 120,
		renderer : {
			type : "TemplateRenderer",
		},
		labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // HTML 템플릿 작성
			var template = '';
			var checkedTag = '';
			if (item.enable) {
				checkedTag = ' checked="checked"';
			}
			template += '<input type="checkbox"' + checkedTag + '">';
			return template;
		},
	}, {
		dataField : "createDate",
		headerText : "작성일",
		dataType : "date",
		formatString : "yyyy-mm-dd",
		width : 120
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
		showRowNumColumn : true,
		rowNumHeaderText : "번호",
		showRowCheckColumn : true, // 체크 박스 출력
		fillColumnSizeMode : true, // 화면 꽉채우기
		editable : true,
		showStateColumn : true
	// 상태값 표시
	};

	myGridID = AUIGrid.create("#grid_wrap", columns, props);
	// LazyLoading 바인딩
	AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);
	// 클릭 이벤트 바인딩
	AUIGrid.bind(myGridID, "cellClick", function(event) {
		let dataField = event.dataField;
		let oid = event.item.oid;
		if ("name" === dataField || "number" === dataField) {
			// 뷰 생성
		}
	});

	function loadGridData() {
		let params = new Object();
		let url = getCallUrl("/commonCode/list");
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
		$("#searchBtn").click(function() {
			loadGridData();
		})

		// 그리드 행 추가
		$("#addRowBtn").click(function() {
			let item = new Object();
			item.createDate = new Date();
			AUIGrid.addRow(myGridID, item, "last");
		})

		$("#saveBtn").click(function() {
			let addRows = AUIGrid.getAddedRowItems(myGridID);
			let removeRows = AUIGrid.getRemovedItems(myGridID);
			let editRows = AUIGrid.getEditedRowItems(myGridID);
			let params = new Object();
			let url = getCallUrl("/commonCode/create");
			params.addRows = addRows;
			params.removeRows = removeRows;
			params.editRows = editRows;
			parent.open();
			call(url, params, function(data) {
				alert(data.msg);
				if (data.result) {
					loadGridData();
				}
			}, "POST");
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