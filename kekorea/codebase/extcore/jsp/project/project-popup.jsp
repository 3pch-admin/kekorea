<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String method = (String) request.getAttribute("method");
%>
<!-- AUIGrid -->
<%@include file="/extcore/include/auigrid.jsp"%>
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
			<input type="text" name="fileName" class="AXInput">
		</td>
		<th>설명</th>
		<td>
			<input type="text" name="partCode" class="AXInput">
		</td>
		<th>작성자</th>
		<td>
			<input type="text" name="partName" class="AXInput">
		</td>
		<th>작성일</th>
		<td>
			<input type="text" name="number" class="AXInput">
		</td>
	</tr>
</table>

<!-- 버튼 테이블 -->
<table class="button-table">
	<tr>
		<td class="left">
			<input type="button" value="테이블 저장" title="테이블 저장" class="orange" onclick="saveColumnLayout('project-popup');">
			<input type="button" value="추가" title="추가" class="blue" onclick="<%=method%>();">
			<input type="button" value="닫기" title="닫기" class="red" onclick="self.close();">
		</td>
		<td class="right">
			<input type="button" value="조회" title="조회" onclick="loadGridData();">
		</td>
	</tr>
</table>

<!-- 그리드 리스트 -->
<div id="grid_wrap" style="height: 665px; border-top: 1px solid #3180c3;"></div>
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
			rowIdField : "oid",
			// 그리드 공통속성 시작
			headerHeight : 30, // 헤더높이
			rowHeight : 30, // 행 높이
			showRowNumColumn : true, // 번호 행 출력 여부
			showStateColumn : true, // 상태표시 행 출력 여부
			rowNumHeaderText : "번호", // 번호 행 텍스트 설정
			noDataMessage : "검색 결과가 없습니다.", // 데이터 없을시 출력할 내용
			enableFilter : true, // 필터 사용 여부
			selectionMode : "multipleCells",
			enableMovingColumn : true,
			showInlineFilter : true,
			showRowCheckColumn : true
		// 그리드 공통속성 끝
		};

		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		//화면 첫 진입시 리스트 호출 함수
		loadGridData();
		// Lazy Loading 이벤트 바인딩
		AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);
		AUIGrid.bind(myGridID, "cellClick", auiCellClickHandler);
	}
	
	function auiCellClickHandler(event) {
		let item = event.item;
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
		let params = new Object();
		let url = getCallUrl("/project/list");
		AUIGrid.showAjaxLoader(myGridID);
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			$("input[name=sessionid]").val(data.sessionid);
			$("input[name=curPage]").val(data.curPage);
			AUIGrid.setGridData(myGridID, data.list);
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
		params.start = (curPage * 100);
		params.end = (curPage * 100) + 100;
		let url = getCallUrl("/aui/appendData");
		AUIGrid.showAjaxLoader(myGridID);
		call(url, params, function(data) {
			if (data.list.length == 0) {
				last = true;
			} else {
				AUIGrid.appendData(myGridID, data.list);
				$("input[name=curPage]").val(parseInt(curPage) + 1);
			}
			AUIGrid.removeAjaxLoader(myGridID);
		})
	}

	
	function <%=method%>() {
		let checkedItems = AUIGrid.getCheckedRowItems(myGridID);
		if (checkedItems.length == 0) {
			alert("추가할 작번을 선택하세요.");
			return false;
		}
		opener.<%=method%>(checkedItems);
	}
	
	
	// jquery 모든 DOM구조 로딩 후 
	$(function() {
		// 로컬 스토리지에 저장된 컬럼 값 불러오기 see - base.js
		let columns = loadColumnLayout("project-popup");
		createAUIGrid(columns);
	}).keypress(function(e) {
		let keyCode = e.keyCode;
		if (keyCode === 13) {
		}
	})
</script>