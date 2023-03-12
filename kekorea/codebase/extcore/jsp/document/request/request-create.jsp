<%@page import="org.json.JSONArray"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String, String>>) request.getAttribute("list");
JSONArray elecs = (JSONArray) request.getAttribute("elecs");
JSONArray softs = (JSONArray) request.getAttribute("softs");
JSONArray machines = (JSONArray) request.getAttribute("machines");
JSONArray maks = (JSONArray) request.getAttribute("maks");
JSONArray installs = (JSONArray) request.getAttribute("installs");
JSONArray customers = (JSONArray) request.getAttribute("customers");
%>
<!-- AUIGrid -->
<%@include file="/extcore/include/auigrid.jsp"%>
<table class="button-table">
	<tr>
		<td class="right">
			<input type="button" value="등록" title="등록" onclick="create();">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>

<table class="create-table">
	<colgroup>
		<col width="130">
		<col width="700">
		<col width="130">
		<col width="700">
	</colgroup>
	<tr>
		<th class="req">의뢰서 제목</th>
		<td class="indent5">
			<input type="text" name="name" id="name" class="AXInput width-500">
		</td>
		<th class="req">작번 템플릿</th>
		<td class="indent5">
			<input type="text" name="name" id="name" class="AXInput width-500">
		</td>
	</tr>
	<tr>
		<th class="req">내용</th>
		<td class="indent5" colspan="3">
			<textarea name="description" id="description" rows="8"></textarea>
		</td>
	</tr>
	<tr>
		<th>첨부파일</th>
		<td class="indent5" colspan="3">
			<jsp:include page="/extcore/include/secondary-include.jsp">
				<jsp:param value="" name="oid" />
				<jsp:param value="create" name="mode" />
			</jsp:include>
		</td>
	</tr>
</table>
<div id="grid_wrap" style="height: 455px; border-top: 1px solid #3180c3; margin-top: 5px;"></div>
<script type="text/javascript">
	let myGridID;
	let maks =
<%=maks%>
	let installs =
<%=installs%>
	let customers =
<%=customers%>
	;
	let elecs =
<%=elecs%>
	;
	let machines =
<%=machines%>
	;
	let softs =
<%=softs%>
	;
	let subListMap = {};
	const columns = [ {
		dataField : "projectType_name",
		headerText : "작번유형",
		dataType : "string",
		width : 80,
	}, {
		dataField : "customer_code",
		headerText : "거래처",
		dataType : "string",
		width : 100,
	}, {
		dataField : "install_code",
		headerText : "설치장소",
		dataType : "string",
		width : 100,
	}, {
		dataField : "mak_code",
		headerText : "막종",
		dataType : "string",
		width : 100,
		renderer : {
			type : "IconRenderer",
			iconWidth : 16, // icon 사이즈, 지정하지 않으면 rowHeight에 맞게 기본값 적용됨
			iconHeight : 16,
			iconPosition : "aisleRight",
			iconTableRef : { // icon 값 참조할 테이블 레퍼런스
				"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png" // default
			},
			onClick : function(event) {
				// 아이콘을 클릭하면 수정으로 진입함.
				AUIGrid.openInputer(event.pid);
			}
		},
		editRenderer : {
			type : "DropDownListRenderer",
			showEditorBtn : false,
			showEditorBtnOver : false, // 마우스 오버 시 에디터버턴 보이기						
			list : maks, //key-value Object 로 구성된 리스트
			keyField : "key", // key 에 해당되는 필드명
			valueField : "value", // value 에 해당되는 필드명,
			descendants : [ "detail_code" ], // 자손 필드들
			descendantDefaultValues : [ "-" ], // 변경 시 자손들에게 기본값 지정
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
		dataField : "detail_code",
		headerText : "막종상세",
		dataType : "string",
		width : 100,
		renderer : {
			type : "IconRenderer",
			iconWidth : 16, // icon 사이즈, 지정하지 않으면 rowHeight에 맞게 기본값 적용됨
			iconHeight : 16,
			iconPosition : "aisleRight",
			iconTableRef : { // icon 값 참조할 테이블 레퍼런스
				"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png" // default
			},
			onClick : function(event) {
				// 아이콘을 클릭하면 수정으로 진입함.
				AUIGrid.openInputer(event.pid);
			}
		},
		editRenderer : {
			type : "DropDownListRenderer",
			showEditorBtn : false,
			showEditorBtnOver : false, // 마우스 오버 시 에디터버턴 보이기						
			keyField : "key", // key 에 해당되는 필드명
			valueField : "value", // value 에 해당되는 필드명,
			listFunction : function(rowIndex, columnIndex, item, dataField) {
				var param = item.mak_code;
				var dd = subListMap[param]; // param으로 보관된 리스트가 있는지 여부
				if (dd === undefined) {
					return [];
				}
				return dd;
			},
		},
		labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
			let retStr = ""; // key 값에 맞는 value 를 찾아 반환함.
			let param = item.mak_code;
			let dd = subListMap[param]; // param으로 보관된 리스트가 있는지 여부
			if (dd === undefined)
				return value;
			for (let i = 0, len = dd.length; i < len; i++) {
				if (dd[i]["key"] == value) {
					retStr = dd[i]["value"];
					break;
				}
			}
			return retStr == "" ? value : retStr;
		},
	}, {
		dataField : "kekNumber",
		headerText : "KEK 작번",
		dataType : "string",
		width : 130,
	}, {
		dataField : "keNumber",
		headerText : "KE 작번",
		dataType : "string",
		width : 130,
		style : "underline"
	}, {
		dataField : "userId",
		headerText : "USER ID",
		dataType : "string",
		width : 100,
		style : "underline"
	}, {
		dataField : "customDate",
		headerText : "요구 납기일",
		dataType : "date",
		formatString : "yyyy-mm-dd",
		width : 100,
	}, {
		dataField : "description",
		headerText : "작업 내용",
		dataType : "string",
		width : 450,
		style : "left indent10"
	}, {
		dataField : "model",
		headerText : "모델",
		dataType : "string",
		width : 130,
	}, {
		dataField : "pdate",
		headerText : "발행일",
		dataType : "date",
		formatString : "yyyy-mm-dd",
		width : 100
	}, {
		dataField : "machine",
		headerText : "기계 담당자",
		dataType : "string",
		width : 100,
		renderer : {
			type : "IconRenderer",
			iconWidth : 16, // icon 사이즈, 지정하지 않으면 rowHeight에 맞게 기본값 적용됨
			iconHeight : 16,
			iconPosition : "aisleRight",
			iconTableRef : { // icon 값 참조할 테이블 레퍼런스
				"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png" // default
			},
			onClick : function(event) {
				// 아이콘을 클릭하면 수정으로 진입함.
				AUIGrid.openInputer(event.pid);
			}
		},
		editRenderer : {
			type : "DropDownListRenderer",
			showEditorBtn : false,
			showEditorBtnOver : false, // 마우스 오버 시 에디터버턴 보이기						
			list : machines, //key-value Object 로 구성된 리스트
			keyField : "oid", // key 에 해당되는 필드명
			valueField : "name", // value 에 해당되는 필드명,
		},
		labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
			let retStr = ""; // key 값에 맞는 value 를 찾아 반환함.
			for (let i = 0, len = machines.length; i < len; i++) {
				if (machines[i]["oid"] == value) {
					retStr = machines[i]["name"];
					break;
				}
			}
			return retStr == "" ? value : retStr;
		},
	}, {
		dataField : "elec",
		headerText : "전기 담당자",
		dataType : "string",
		width : 100,
		renderer : {
			type : "IconRenderer",
			iconWidth : 16, // icon 사이즈, 지정하지 않으면 rowHeight에 맞게 기본값 적용됨
			iconHeight : 16,
			iconPosition : "aisleRight",
			iconTableRef : { // icon 값 참조할 테이블 레퍼런스
				"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png" // default
			},
			onClick : function(event) {
				// 아이콘을 클릭하면 수정으로 진입함.
				AUIGrid.openInputer(event.pid);
			}
		},
		editRenderer : {
			type : "DropDownListRenderer",
			showEditorBtn : false,
			showEditorBtnOver : false, // 마우스 오버 시 에디터버턴 보이기						
			list : elecs, //key-value Object 로 구성된 리스트
			keyField : "oid", // key 에 해당되는 필드명
			valueField : "name", // value 에 해당되는 필드명,
		},
		labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
			let retStr = ""; // key 값에 맞는 value 를 찾아 반환함.
			for (let i = 0, len = elecs.length; i < len; i++) {
				if (elecs[i]["oid"] == value) {
					retStr = elecs[i]["name"];
					break;
				}
			}
			return retStr == "" ? value : retStr;
		},
	}, {
		dataField : "soft",
		headerText : "SW 담당자",
		dataType : "string",
		width : 100,
		renderer : {
			type : "IconRenderer",
			iconWidth : 16, // icon 사이즈, 지정하지 않으면 rowHeight에 맞게 기본값 적용됨
			iconHeight : 16,
			iconPosition : "aisleRight",
			iconTableRef : { // icon 값 참조할 테이블 레퍼런스
				"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png" // default
			},
			onClick : function(event) {
				// 아이콘을 클릭하면 수정으로 진입함.
				AUIGrid.openInputer(event.pid);
			}
		},
		editRenderer : {
			type : "DropDownListRenderer",
			showEditorBtn : false,
			showEditorBtnOver : false, // 마우스 오버 시 에디터버턴 보이기						
			list : softs, //key-value Object 로 구성된 리스트
			keyField : "oid", // key 에 해당되는 필드명
			valueField : "value", // value 에 해당되는 필드명,
		},
		labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
			let retStr = ""; // key 값에 맞는 value 를 찾아 반환함.
			for (let i = 0, len = softs.length; i < len; i++) {
				if (softs[i]["oid"] == value) {
					retStr = softs[i]["name"];
					break;
				}
			}
			return retStr == "" ? value : retStr;
		},
	} ]

	// AUIGrid 생성 함수
	function createAUIGrid(columnLayout) {
		// 그리드 속성
		const props = {
			rowIdField : "oid",
			// 그리드 공통속성 시작
			headerHeight : 30, // 헤더높이
			rowHeight : 30, // 행 높이
			showRowNumColumn : true, // 번호 행 출력 여부
			showStateColumn : true, // 상태표시 행 출력 여부
			rowNumHeaderText : "번호", // 번호 행 텍스트 설정
			noDataMessage : "검색 결과가 없습니다.", // 데이터 없을시 출력할 내용
			selectionMode : "multipleCells",
			// 그리드 공통속성 끝
			editable : true,
		// 			fillColumnSizeMode : true
		};

		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		readyHandler();
		AUIGrid.bind(myGridID, "cellEditEnd", auiCellEditEndHandler);
	}

	function auiCellEditEndHandler(event) {
		let dataField = event.dataField;
		let item = event.item;
		let rowIndex = event.rowIndex;
		if (dataField === "mak_code") {
			let mak = item.mak_code;
			let url = getCallUrl("/commonCode/getChildrens?parentCode=" + mak + "&codeType=MAK");
			call(url, null, function(data) {
				subListMap[mak] = data.list;
			}, "GET");
		}
	}

	function readyHandler() {
		let item = new Object();
		AUIGrid.addRow(myGridID, item, "first");
	}

	// 등록
	function create() {

		if (!confirm("등록 하시겠습니까?")) {
			return false;
		}

		let params = new Object();
		let url = getCallUrl("/request/create");
		let addRows = AUIGrid.getAddedRowItems(myGridID); // 프로젝트
		params.addRows = addRows
		params.secondarys = toArray("secondarys");
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				// 실패시 처리할 부분..
			}
		})
	}

	// jquery 삭제를 해가는 쪽으로 한다..
	document.addEventListener("DOMContentLoaded", function() {
		// DOM이 로드된 후 실행할 코드 작성
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>