<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String method = (String) request.getAttribute("method");
boolean multi = (boolean) request.getAttribute("multi");
WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
%>
<!-- AUIGrid -->
<%@include file="/extcore/include/auigrid.jsp"%>
<!-- AUIGrid 리스트페이지에서만 사용할 js파일 -->
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js"></script>
<!-- 리스트 검색시 반드시 필요한 히든 값 -->
<input type="hidden" name="sessionid" id="sessionid">
<input type="hidden" name="curPage" id="curPage">
<!-- 검색 테이블 -->
<table class="search-table">
	<colgroup>
		<col width="130">
		<col width="*">
		<col width="130">
		<col width="*">
		<col width="130">
		<col width="*">
		<col width="130">
		<col width="*">
	</colgroup>
	<tr>
		<th>공지사항 제목</th>
		<td>
			<input type="text" name="fileName">
		</td>
		<th>설명</th>
		<td>
			<input type="text" name="partCode">
		</td>
		<th>작성자</th>
		<td>
			<input type="text" name="partName">
		</td>
		<th>작성일</th>
		<td>
			<input type="text" name="number">
		</td>
	</tr>
</table>

<!-- 버튼 테이블 -->
<table class="button-table">
	<tr>
		<td class="left">
			<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('project-popup');">
			<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('project-popup');">
			<input type="button" value="추가" title="추가" class="blue" onclick="<%=method%>();">
			<input type="button" value="닫기" title="닫기" class="red" onclick="self.close();">
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

<!-- 그리드 리스트 -->
<div id="grid_wrap" style="height: 665px; border-top: 1px solid #3180c3;"></div>
<!-- 컨텍스트 메뉴 사용시 반드시 넣을 부분 -->
<%@include file="/extcore/jsp/common/aui/aui-context.jsp"%>
<script type="text/javascript">
	let myGridID;
	function _layout() {
		return [ {
			dataField : "state",
			headerText : "진행상태",
			dataType : "string",
			width : 80,
			renderer : {
				type : "TemplateRenderer",
			},
			filter : {
				showIcon : true,
				useExMenu : true
			},
		}, {
			dataField : "projectType_name",
			headerText : "작번유형",
			dataType : "string",
			width : 80,
		}, {
			dataField : "customer_name",
			headerText : "거래처",
			dataType : "string",
			width : 100,
		}, {
			dataField : "install_name",
			headerText : "설치장소",
			dataType : "string",
			width : 100,
		}, {
			dataField : "mak_name",
			headerText : "막종",
			dataType : "string",
			width : 100,
		}, {
			dataField : "detail_name",
			headerText : "막종상세",
			dataType : "string",
			width : 100,
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
		}, {
			dataField : "userId",
			headerText : "USER ID",
			dataType : "string",
			width : 100,
		}, {
			dataField : "description",
			headerText : "작업 내용",
			dataType : "string",
			width : 450,
			style : "left indent10"
		}, {
			dataField : "pdate",
			headerText : "발행일",
			dataType : "date",
			formatString : "yyyy-mm-dd",
			width : 100
		}, {
			dataField : "completeDate",
			headerText : "설계 완료일",
			dataType : "date",
			formatString : "yyyy-mm-dd",
			width : 100,
		}, {
			dataField : "customDate",
			headerText : "요구 납기일",
			dataType : "date",
			formatString : "yyyy-mm-dd",
			width : 100,
		}, {
			dataField : "model",
			headerText : "모델",
			dataType : "string",
			width : 130,
		}, {
			dataField : "machine",
			headerText : "기계 담당자",
			dataType : "string",
			width : 100,
		}, {
			dataField : "elec",
			headerText : "전기 담당자",
			dataType : "string",
			width : 100,
		}, {
			dataField : "soft",
			headerText : "SW 담당자",
			dataType : "string",
			width : 100
		}, {
			dataField : "kekProgress",
			headerText : "진행율",
			postfix : "%",
			width : 80,
			renderer : {
				type : "BarRenderer",
				min : 0,
				max : 100
			},
		}, {
			dataField : "kekState",
			headerText : "작번상태",
			dataType : "string",
			width : 100,
		} ]
	}

	// AUIGrid 생성 함수
	function createAUIGrid(columnLayout) {
		// 그리드 속성
		const props = {
			// 그리드 공통속성 시작
			headerHeight : 30,
			rowHeight : 30,
			showRowNumColumn : true,
			showRowCheckColumn : true,
			showStateColumn : true,
			rowNumHeaderText : "번호",
			noDataMessage : "검색 결과가 없습니다.",
			selectionMode : "multipleCells",
			enableMovingColumn : true,
			showInlineFilter : true,
			useContextMenu : true,
			enableRightDownFocus : true,
			filterLayerWidth : 320,
			filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
			// 그리드 공통속성 끝
		};

		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		//화면 첫 진입시 리스트 호출 함수
		loadGridData();

		AUIGrid.bind(myGridID, "vScrollChange", function(event) {
			hideContextMenu(); // 컨텍스트 메뉴 감추기
			vScrollChangeHandler(event); // lazy loading
		});
	
		AUIGrid.bind(myGridID, "hScrollChange", function(event) {
			hideContextMenu(); // 컨텍스트 메뉴 감추기
		});
				
		AUIGrid.bind(myGridID, "cellClick", auiCellClickHandler);
		
	}
	
	function auiCellClickHandler(event) {
		const item = event.item;
		rowIdField = AUIGrid.getProp(event.pid, "rowIdField"); // rowIdField 얻기
		rowId = item[rowIdField];
		
		// 이미 체크 선택되었는지 검사
		if(AUIGrid.isCheckedRowById(event.pid, rowId)) {
			// 엑스트라 체크박스 체크해제 추가
			AUIGrid.addUncheckedRowsByIds(event.pid, rowId);
		} else {
			// 엑스트라 체크박스 체크 추가
			AUIGrid.addCheckedRowsByIds(event.pid, rowId);
		}
	}
	
	function loadGridData() {
		const params = new Object();
		const url = getCallUrl("/project/list");
		const psize = document.getElementById("psize").value;
		params.psize = psize;
		AUIGrid.showAjaxLoader(myGridID);
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			$("input[name=sessionid]").val(data.sessionid);
			$("input[name=curPage]").val(data.curPage);
			AUIGrid.setGridData(myGridID, data.list);
		});
	}
	
	function <%=method%>() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
		if (checkedItems.length == 0) {
			alert("추가할 작번을 선택하세요.");
			return false;
		}
		opener.<%=method%>(checkedItems);
	}
	
	
	document.addEventListener("DOMContentLoaded", function() {
		// DOM이 로드된 후 실행할 코드 작성
		const columns = loadColumnLayout("project-popup");
		// 컨텍스트 메뉴 시작
		const contenxtHeader = genColumnHtml(columns); // see auigrid.js
		$("#h_item_ul").append(contenxtHeader);
		$("#headerMenu").menu({
			select : headerMenuSelectHandler
		});
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);

		selectbox("psize");
	});

	document.addEventListener("keydown", function(event) {
		// 키보드 이벤트 객체에서 눌린 키의 코드 가져오기
		const keyCode = event.keyCode || event.which;
		if (keyCode === 13) {
			loadGridData();
		}
	})

	// 컨텍스트 메뉴 숨기기
	document.addEventListener("click", function(event) {
		hideContextMenu();
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>