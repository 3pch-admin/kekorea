<%@page import="wt.org.WTUser"%>
<%@page import="org.json.JSONArray"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.org.service.OrgHelper"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String method = (String) request.getAttribute("method");
boolean multi = (boolean) request.getAttribute("multi");
String openerId = (String) request.getAttribute("openerId");
ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>) request.getAttribute("list");
JSONArray maks = (JSONArray) request.getAttribute("maks");
JSONArray installs = (JSONArray) request.getAttribute("installs");
JSONArray departments = new JSONArray(list);
%>
<%@include file="/extcore/include/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js"></script>
<input type="hidden" name="sessionid" id="sessionid">
<input type="hidden" name="curPage" id="curPage">
<input type="hidden" name="oid" id="oid">
<table class="search-table">
	<colgroup>
		<col width="130">
		<col width="600">
		<col width="130">
		<col width="600">
		<col width="130">
		<col width="600">
	</colgroup>
	<tr>
		<th>이름</th>
		<td class="indent5">
			<input type="text" name="userName" id="userName" class="AXInput">
		</td>
		<th>아이디</th>
		<td class="indent5">
			<input type="text" name="userId" id="userId" class="AXInput">
		</td>
		<th>퇴사여부</th>
		<td>
			&nbsp;
			<div class="pretty p-switch">
				<input type="checkbox" name="resign" value="true">
				<div class="state p-success">
					<label>&nbsp;</label>
				</div>
			</div>
		</td>
	</tr>
</table>

<table class="button-table">
	<tr>
		<td class="left">
			<input type="button" value="추가" title="추가" class="orange" onclick="<%=method%>()">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
		<td class="right">
			<select name="psize" id="psize">
				<option value="30">30</option>
				<option value="50">50</option>
				<option value="100">100</option>
				<option value="200">200</option>
				<option value="300">300</option>
			</select>
			<input type="button" value="조회" title="조회" onclick="loadGridData();">
		</td>
	</tr>
</table>

<table>
	<colgroup>
		<col width="230">
		<col width="10">
		<col width="*">
	</colgroup>
	<tr>
		<td>
			<jsp:include page="/extcore/include/department-include.jsp">
				<jsp:param value="list" name="mode" />
				<jsp:param value="705" name="height" />
			</jsp:include>
		</td>
		<td>&nbsp;</td>
		<td>
			<div id="grid_wrap" style="height: 705px; border-top: 1px solid #3180c3;"></div>
		</td>
	</tr>
</table>
<script type="text/javascript">
	let myGridID;
	const maks = <%=maks%>
	const installs = <%=installs%>
	const departments = <%=departments%>
	const dutys = [ "사장", "부사장", "PL", "TL" ];
	const columns = [ {
		dataField : "name",
		headerText : "이름",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "id",
		headerText : "아이디",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "duty",
		headerText : "직급",
		dataType : "string",
		width : 130,
		filter : {
			showIcon : true,
			inline : true
		},
		renderer : {
			type : "IconRenderer",
			iconWidth : 16,
			iconHeight : 16,
			iconPosition : "aisleRight",
			iconTableRef : {
				"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png" // default
			},
		},
		editRenderer : {
			type : "DropDownListRenderer",
			showEditorBtn : false,
			showEditorBtnOver : false,
			multipleMode : false,
			showCheckAll : false,
			list : dutys,
		},
	}, {
		dataField : "department_oid",
		headerText : "부서",
		dataType : "string",
		width : 150,
		renderer : {
			type : "IconRenderer",
			iconWidth : 16,
			iconHeight : 16,
			iconPosition : "aisleRight",
			iconTableRef : {
				"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png" // default
			},
		},
		editRenderer : {
			type : "DropDownListRenderer",
			showEditorBtn : false,
			showEditorBtnOver : false,
			multipleMode : false,
			showCheckAll : false,
			list : departments,
			keyField : "oid",
			valueField : "name",
		},
		labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
			let retStr = "";
			for (let i = 0, len = departments.length; i < len; i++) {
				if (departments[i]["oid"] == value) {
					retStr = departments[i]["name"];
					break;
				}
			}
			return retStr == "" ? value : retStr;
		},
	}, {
		dataField : "mak",
		headerText : "막종",
		dataType : "string",
		style : "aui-left",
		headerTooltip : {
			show : true,
			tooltipHtml : "한국 생산의 차트에서 사용자가 원하는 막종만 볼 수 있도록 설정 하는 컬럼입니다."
		},
		renderer : {
			type : "IconRenderer",
			iconWidth : 16,
			iconHeight : 16,
			iconPosition : "aisleRight",
			iconTableRef : {
				"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png" // default
			}
		},
		editRenderer : {
			type : "DropDownListRenderer",
			showEditorBtn : false,
			showEditorBtnOver : false,
			multipleMode : true,
			showCheckAll : true,
			list : maks,
			keyField : "key",
			valueField : "value",
		},
		labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
			let retStr = "";
			for (let i = 0, len = maks.length; i < len; i++) {
				if (maks[i]["key"] == value) {
					retStr = maks[i]["value"];
					break;
				}
			}
			return retStr == "" ? value : retStr;
		},
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "install",
		headerText : "설치장소",
		dataType : "string",
		style : "aui-left",
		headerTooltip : {
			show : true,
			tooltipHtml : "한국 생산의 차트에서 사용자가 원하는 설치장소만 볼 수 있도록 설정 하는 컬럼입니다."
		},
		renderer : {
			type : "IconRenderer",
			iconWidth : 16,
			iconHeight : 16,
			iconPosition : "aisleRight",
			iconTableRef : {
				"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png" // default
			},
		},
		editRenderer : {
			type : "DropDownListRenderer",
			showEditorBtn : false,
			showEditorBtnOver : false,
			multipleMode : true,
			showCheckAll : true,
			list : installs,
			keyField : "key",
			valueField : "value",
		},
		labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
			let retStr = "";
			for (let i = 0, len = installs.length; i < len; i++) {
				if (installs[i]["key"] == value) {
					retStr = installs[i]["value"];
					break;
				}
			}
			return retStr == "" ? value : retStr;
		},
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "email",
		headerText : "이메일",
		dataType : "string",
		width : 250,
		style : "aui-left",
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "resign",
		headerText : "퇴사여부",
		dataType : "boolean",
		width : 80,
		renderer : {
			type : "CheckBoxEditRenderer",
		},
		filter : {
			showIcon : false,
			inline : false
		},
	}, {
		dataField : "createdDate",
		headerText : "등록일",
		dataType : "date",
		formatString : "yyyy-mm-dd",
		width : 100,
		filter : {
			showIcon : true,
			inline : true,
			displayFormatValues : true
		},
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			rowHeight : 30,
			showRowNumColumn : true,
			showRowCheckColumn : true,
			showStateColumn : true,
			rowNumHeaderText : "번호",
			noDataMessage : "검색 결과가 없습니다.",
			enableFilter : true,
			selectionMode : "multipleCells",
			showInlineFilter : true,
			filterLayerWidth : 320,
			filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
			<%
				if(!multi) {
			%>
			rowCheckToRadio : true
			<%
				}
			%>
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		loadGridData();
		AUIGrid.bind(myGridID, "vScrollChange", function(event) {
			vScrollChangeHandler(event);
		});
		AUIGrid.bind(myGridID, "cellClick", auiCellClickHandler);
	}
	
	function auiCellClickHandler(event) {
		const item = event.item;
		rowIdField = AUIGrid.getProp(event.pid, "rowIdField"); // rowIdField 얻기
		rowId = item[rowIdField];
		if(AUIGrid.isCheckedRowById(event.pid, rowId)) {
			AUIGrid.addUncheckedRowsByIds(event.pid, rowId);
		} else {
			AUIGrid.addCheckedRowsByIds(event.pid, rowId);
		}
	}

	function loadGridData() {
		const params = new Object();
		const url = getCallUrl("/org/list");
		const userName = document.getElementById("userName").value;
		const userId = document.getElementById("userId").value;
		const oid = document.getElementById("oid").value;
		const psize = document.getElementById("psize").value;
		params.psize = psize;
		params.oid = oid;
		params.userName = userName;
		params.userId = userId;
		AUIGrid.showAjaxLoader(myGridID);
		openLayer();
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			document.getElementById("sessionid").value = data.sessionid;
			document.getElementById("curPage").value = data.curPage;
			AUIGrid.setGridData(myGridID, data.list);
			closeLayer();
		});
	}

	function <%=method%>() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
		if(checkedItems.length ==0) {
			alert("추가할 사용자를 선택하세요.");
			return;
		}
		inputUser("<%=openerId%>", checkedItems[0]);
		self.close();
	}

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columns); // 리스트
		AUIGrid.resize(myGridID); // 리스트
		selectbox("psize");
	});

	document.addEventListener("keydown", function(event) {
		const keyCode = event.keyCode || event.which;
		if (keyCode === 13) {
			loadGridData();
		}
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID); // 리스트
	});
</script>