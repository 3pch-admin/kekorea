<%@page import="org.json.JSONArray"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.org.service.OrgHelper"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>) request.getAttribute("list");
JSONArray maks = (JSONArray)request.getAttribute("maks");
JSONArray departments = new JSONArray(list);
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
	<div class="header_title">
		<i class="axi axi-subtitles"></i>
		<span>조직도</span>
	</div>
	<table class="search_table">
		<colgroup>
			<col width="130">
			<col width="800">
			<col width="130">
			<col width="800">
		</colgroup>
		<tr>
			<th>부서</th>
			<td>
				<select name="department" id="department" class="AXSelect wid200">
					<option value="">선택</option>
					<%
					for (HashMap<String, Object> map : list) {
						String name = (String) map.get("name");
						String oid = (String) map.get("oid");
					%>
					<option value="<%=oid%>"><%=name%></option>
					<%
					}
					%>
				</select>
			</td>
			<th>퇴사여부</th>
			<td>
				<select name="resign" id="resign" class="AXSelect wid100">
					<option value="">선택</option>
					<option value="false">재직중</option>
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

	<table class="btn_table">
		<tr>
			<td class="left">
				<%
				if (isAdmin) {
				%>
				<input type="button" value="저장" class="redBtn" id="saveBtn" title="저장">
				<%
				}
				%>
			</td>
			<td class="right">
				<input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회">
				<input type="button" value="초기화" class="" id="initGrid" title="초기화">
			</td>
		</tr>
	</table>
	<div id="grid_wrap" style="height: 650px; border-top: 1px solid #3180c3;"></div>
</body>
<script type="text/javascript">
	let myGridID;
	let maks = <%=maks%>;
	let departments = <%=departments%>;
	const columns = [ {
		dataField : "name",
		headerText : "사용자 이름",
		dataType : "string",
		width : 100
	}, {
		dataField : "id",
		headerText : "사용자 아이디",
		dataType : "string",
		width : 100,
		editable : false
	}, {
		dataField : "duty",
		headerText : "직급",
		dataType : "string",
		width : 130
	}, {
		dataField : "department_name",
		headerText : "부서",
		dataType : "string",
		width : 180
	}, {
		dataField : "mak",
		headerText : "관련막종",
		dataType : "string",
		width : 200,
		style : "left indent10",
		renderer: {
			type: "IconRenderer",
			iconWidth: 16, // icon 사이즈, 지정하지 않으면 rowHeight에 맞게 기본값 적용됨
			iconHeight: 16,
			iconPosition: "aisleRight",
			iconTableRef: { // icon 값 참조할 테이블 레퍼런스
				"default": "/Windchill/jsp/asset/AUIGrid/images/list-icon.png" // default
			},
			onClick: function (event) {
				// 아이콘을 클릭하면 수정으로 진입함.
				AUIGrid.openInputer(event.pid);
			}
		},		
		editRenderer: {
			type: "DropDownListRenderer",
			showEditorBtn: false,
			showEditorBtnOver: false, // 마우스 오버 시 에디터버턴 보이기
			multipleMode: true, // 다중 선택 모드(기본값 : false)
			showCheckAll: true, // 다중 선택 모드에서 전체 체크 선택/해제 표시(기본값:false);
			list: maks,
			keyField : "key", // key 에 해당되는 필드명
			valueField : "value", // value 에 해당되는 필드명,			
		},
		labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
			let retStr = ""; // key 값에 맞는 value 를 찾아 반환함.
			for (let i = 0, len = maks.length; i < len; i++) {
				if (maks[i]["key"] == value) {
					retStr = maks[i]["value"];
					break;
				}
			}
			return retStr == "" ? value : retStr;
		},		
	}, {
		dataField : "email",
		headerText : "이메일",
		dataType : "string",
		width : 250,
		style : "left indent10"
	}, {
		dataField : "resign",
		headerText : "퇴사여부",
		dataType : "string",
		width : 100,
		renderer : {
			type : "CheckBoxEditRenderer",
		},
	}, {
		dataField : "createDate",
		headerText : "등록일",
		dataType : "date",
		formatString : "yyyy-mm-dd",
		width : 100
	}, {
		dataField : "oid",
		headerText : "oid",
		dataType : "string",
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
			fillColumnSizeMode : true,
			showStateColumn : true,
			noDataMessage : "검색 결과가 없습니다.",
			selectionMode : "multipleCells",
			enableFilter : true,
			editable : true
		};
		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);
		loadGridData();
	}

	function loadGridData() {
		let params = new Object();
		let url = getCallUrl("/org/list");
		AUIGrid.showAjaxLoader(myGridID);
		parent.openLayer();
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			$("input[name=sessionid]").val(data.sessionid);
			$("input[name=curPage]").val(data.curPage);
			AUIGrid.setGridData(myGridID, data.list);
			parent.closeLayer();
		});
	};

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

		selectBox("department");
		selectBox("resign");

		$("#searchBtn").click(function() {
			loadGridData();
		})

		$("#saveBtn").click(function() {
			let editRows = AUIGrid.getEditedRowItems(myGridID);
			let params = new Object();
			let url = getCallUrl("/org/save");
			params.editRows = editRows;
			parent.openLayer();
			call(url, params, function(data) {
				alert(data.msg);
				if (data.result) {
					loadGridData();
				}
			}, "POST");
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