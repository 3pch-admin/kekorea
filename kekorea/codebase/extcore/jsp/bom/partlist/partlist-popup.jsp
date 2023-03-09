<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String method = (String) request.getAttribute("method");
boolean multi = (boolean) request.getAttribute("multi");
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
			<input type="button" value="테이블 저장" title="테이블 저장" class="orange" onclick="saveColumnLayout('partlist-popup');">
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
			dataField : "projectType_name",
			headerText : "설계구분",
			dataType : "string",
			width : 80,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "info",
			headerText : "",
			width : 40,
			renderer : {
				type : "IconRenderer",
				iconWidth : 16, // icon 사이즈, 지정하지 않으면 rowHeight에 맞게 기본값 적용됨
				iconHeight : 16,
				iconTableRef : { // icon 값 참조할 테이블 레퍼런스
					"default" : "/Windchill/extcore/images/details.gif" // default
				},
			},
			filter : {
				showIcon : false,
				inline : false
			},
		}, {
			dataField : "name",
			headerText : "수배표제목",
			dataType : "string",
			style : "left indent10",
			width : 300,
			filter : {
				showIcon : true,
				inline : true
			},
			cellMerge : true
		// 구분1 칼럼 셀 세로 병합 실행
		}, {
			dataField : "mak_name",
			headerText : "막종",
			dataType : "string",
			width : 100,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "detail_name",
			headerText : "막종상세",
			dataType : "string",
			width : 100,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "kekNumber",
			headerText : "KEK 작번",
			dataType : "string",
			width : 100,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "keNumber",
			headerText : "KE 작번",
			dataType : "string",
			width : 100,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "userId",
			headerText : "USER ID",
			dataType : "string",
			width : 100,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "description",
			headerText : "작업내용",
			dataType : "string",
			width : 300,
			style : "left",
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "customer_name",
			headerText : "거래처",
			dataType : "string",
			width : 100,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "install_name",
			headerText : "설치 장소",
			dataType : "string",
			width : 100,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "pdate",
			headerText : "발행일",
			dataType : "date",
			formatString : "yyyy-mm-dd",
			width : 100,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "model",
			headerText : "모델",
			dataType : "string",
			width : 100,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "creator",
			headerText : "작성자",
			dataType : "string",
			width : 100,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "createdDate",
			headerText : "작성일",
			dataType : "date",
			formatString : "yyyy-mm-dd",
			width : 100,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "state",
			headerText : "상태",
			dataType : "string",
			width : 100,
			filter : {
				showIcon : true,
				inline : true
			},
		} ]
	};

	function createAUIGrid(columnLayout) {
		const props = {
			rowIdField : "loid",
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
			// 그리드 공통속성 끝
			showRowCheckColumn : true,
			enableCellMerge : true,
			// 멀티 선택 여부
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
		// LazyLoading 바인딩
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
	
	function <%=method%>() {
		let checkedItems = AUIGrid.getCheckedRowItems(myGridID);
		if (checkedItems.length == 0) {
			alert("추가할 수배표 선택하세요.");
			return false;
		}
		
		opener.<%=method%>(checkedItems);
	}
	
	function loadGridData() {
		let params = new Object();
		let url = getCallUrl("/partlist/list");
		AUIGrid.showAjaxLoader(myGridID);
// 		openLayer();
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			document.getElementById("sessionid").value = data.sessionid;
			document.getElementById("curPage").value = data.curPage;
			AUIGrid.setGridData(myGridID, data.list);
// 			closeLayer();
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
		let curPage = document.getElementById("curPage").value
		let sessionid = document.getElementById("sessionid").value
		params.sessionid = sessionid;
		params.start = (curPage * 100);
		params.end = (curPage * 100) + 100;
		let url = getCallUrl("/aui/appendData");
		AUIGrid.showAjaxLoader(myGridID);
// 		openLayer();
		call(url, params, function(data) {
			if (data.list.length == 0) {
				last = true;
			} else {
				AUIGrid.appendData(myGridID, data.list);
				document.getElementById("curPage").value = parseInt(curPage) + 1;
			}
			AUIGrid.removeAjaxLoader(myGridID);
// 			closeLayer();
		})
	}

	// jquery 삭제를 해가는 쪽으로 한다..
	document.addEventListener("DOMContentLoaded", function() {
		// DOM이 로드된 후 실행할 코드 작성
		let columns = loadColumnLayout("partlist-popup");
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
	});

	document.addEventListener("keydown", function(event) {
		// 키보드 이벤트 객체에서 눌린 키의 코드 가져오기
		let keyCode = event.keyCode || event.which;
		if (keyCode === 13) {
			loadGridData();
		}
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>