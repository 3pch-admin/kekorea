<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.admin.commonCode.CommonCodeType"%>
<%@page import="org.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<CommonCode> sizes = (ArrayList<CommonCode>) request.getAttribute("sizes");
ArrayList<CommonCode> drawingCompanys = (ArrayList<CommonCode>) request.getAttribute("drawingCompanys");
ArrayList<CommonCode> writtenDocuments = (ArrayList<CommonCode>) request.getAttribute("writtenDocuments");
ArrayList<CommonCode> businessSectors = (ArrayList<CommonCode>) request.getAttribute("businessSectors");
ArrayList<CommonCode> classificationWritingDepartment = (ArrayList<CommonCode>) request
		.getAttribute("classificationWritingDepartment");
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
	<table class="search_table">
		<tr>
			<th>사업부문</th>
			<td>
				<select name="size" id="size" class="AXSelect w200">
					<option value="">선택</option>
					<%
					for (CommonCode commonCode : sizes) {
						String value = commonCode.getPersistInfo().getObjectIdentifier().getStringValue();
						String display = commonCode.getName();
					%>
					<option value="<%=value%>"><%=display%></option>
					<%
					}
					%>
				</select>
			</td>
			<th>작성기간</th>
			<td>&nbsp;</td>
			<th>도면번호</th>
			<td>
				<input type="text" name="kekNumber" class="AXInput wid200">
			</td>
			<th>도면생성회사</th>
			<td>
				<select name="size" id="size" class="AXSelect w200">
					<option value="">선택</option>
					<%
					for (CommonCode commonCode : sizes) {
						String value = commonCode.getPersistInfo().getObjectIdentifier().getStringValue();
						String display = commonCode.getName();
					%>
					<option value="<%=value%>"><%=display%></option>
					<%
					}
					%>
				</select>
			</td>
		</tr>
		<tr>
			<th>사이즈</th>
			<td>
				<select name="size" id="size" class="AXSelect w200">
					<option value="">선택</option>
					<%
					for (CommonCode commonCode : sizes) {
						String value = commonCode.getPersistInfo().getObjectIdentifier().getStringValue();
						String display = commonCode.getName();
					%>
					<option value="<%=value%>"><%=display%></option>
					<%
					}
					%>
				</select>
			</td>
			<th>도면구분</th>
			<td>&nbsp;</td>
			<th>년도</th>
			<td>&nbsp;</td>
			<th>관리번호</th>
			<td>
				<input type="text" name="kekNumber" class="AXInput wid200">
			</td>
		</tr>
		<tr>
			<th>부품도구분</th>
			<td>&nbsp;</td>
			<th>진행상태</th>
			<td>
				<select name="state" id="state" class="AXSelect w200">
					<option value="">선택</option>
					<option value="진행중">진행중</option>
					<option value="완료">완료</option>
					<option value="폐기">폐기</option>
				</select>
			</td>
			<th>작성부서</th>
			<td>&nbsp;</td>
			<th>작성자</th>
			<td>
				<input type="text" name="kekNumber" class="AXInput wid200">
			</td>
		</tr>
	</table>
	<!-- button table -->
	<table class="btn_table">
		<tr>
			<td class="left">
				<input type="button" value="등록" class="blueBtn" id="createBtn" title="등록">
				<input type="button" value="개정" class="greenBtn" id="reviseBtn" title="개정">
			</td>
			<td class="right">
				<input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회">
				<input type="button" value="초기화" class="" id="initGrid" title="초기화">
			</td>
		</tr>
	</table>
	<div id="grid_wrap" style="height: 660px; border-top: 1px solid #3180c3;"></div>
</body>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "number",
		headerText : "도면번호",
		dataType : "string",
		width : 150
	}, {
		dataField : "name",
		headerText : "도면명",
		dataType : "string",
// 		width : 150
	}, {
		dataField : "businessSector",
		headerText : "사업부문",
		dataType : "string",
		width : 200,
	}, {
		dataField : "drawingCompany",
		headerText : "도면생성회사",
		dataType : "string",
		width : 150,
	}, {
		dataField : "department",
		headerText : "작성부서구분",
		dataType : "string",
		width : 150,
	}, {
		dataField : "document",
		headerText : "작성문서구분",
		dataType : "string",
		width : 150,
	}, {
		dataField : "latest",
		headerText : "최신버전여부",
		dataType : "boolean",
		width : 100,
		renderer : {
			type : "CheckBoxEditRenderer",
		},
	}, {
		dataField : "version",
		headerText : "버전",
		dataType : "string",
		width : 100,
	}, {
		dataField : "creator",
		headerText : "작성자",
		dataType : "string",
		width : 80,
	}, {
		dataField : "createdDate",
		headerText : "작성일",
		dataType : "date",
		formatString : "yyyy-mm-dd",
		width : 100,
	}, {
		dataField : "modifier",
		headerText : "수정자",
		dataType : "string",
		width : 80,
	}, {
		dataField : "modifiedDate",
		headerText : "수정일",
		dataType : "date",
		formatString : "yyyy-mm-dd",
		width : 100,
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
			showRowCheckColumn : true, // 체크 박스 출력
			fillColumnSizeMode : true
		};

		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		loadGridData();
		// LazyLoading 바인딩
		AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);
	}

	function loadGridData() {
		let params = new Object();
		let url = getCallUrl("/numberRule/list");
		AUIGrid.showAjaxLoader(myGridID);
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
		let url = getCallUrl("/appendData");
		AUIGrid.showAjaxLoader(myGridID);
		parent.openLayer();
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
			parent.closeLayer();
		})
	}

	$(function() {

		createAUIGrid(columns);

		$("#searchBtn").click(function() {
			loadGridData();
		})

		// 등록페이지
		$("#createBtn").click(function() {
			let url = getCallUrl("/numberRule/create");
			popup(url, 1600, 550);
		})

		$("#reviseBtn").click(function() {
			let checkedItems = AUIGrid.getCheckedRowItems(myGridID);
			if (checkedItems.length == 0) {
				alert("개정할 도면을 선택하세요.");
				return false;
			}
			let url = getCallUrl("/numberRule/revise");
			let panel;
			panel = popup(url, 1600, 550);
			panel.list = checkedItems;
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