<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<!-- CSS 공통 모듈 -->
<%@include file="/extcore/include/css.jsp"%>
<!-- 스크립트 공통 모듈 -->
<%@include file="/extcore/include/script.jsp"%>
<!-- AUIGrid -->
<%@include file="/extcore/include/auigrid.jsp"%>
</head>
<body>
	<form>
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
					<input type="button" value="테이블 저장" title="테이블 저장" class="orange" onclick="saveColumnLayout('request-list');">
					<input type="button" value="저장" title="저장" onclick="save();">
					<input type="button" value="행 삭제" title="행 삭제" class="red" onclick="deleteRow();">
					<input type="button" value="등록" title="등록" class="blue" onclick="create();">
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
					headerText : "작번유형",
					dataType : "string",
					width : 80,
					filter : {
						showIcon : true,
						useExMenu : true
					},
				}, {
					dataField : "name",
					headerText : "의뢰서 제목",
					dataType : "string",
					width : 350,
					style : "left indent10 underline",
				}, {
					dataField : "customer_name",
					headerText : "거래처",
					dataType : "string",
					width : 100
				}, {
					dataField : "install_name",
					headerText : "설치장소",
					dataType : "string",
					width : 100
				}, {
					dataField : "mak_name",
					headerText : "막종",
					dataType : "string",
					width : 100
				}, {
					dataField : "detail_name",
					headerText : "막종상세",
					dataType : "string",
					width : 100
				}, {
					dataField : "kekNumber",
					headerText : "KEK 작번",
					dataType : "string",
					width : 130
				}, {
					dataField : "keNumber",
					headerText : "KE 작번",
					dataType : "string",
					width : 130
				}, {
					dataField : "userId",
					headerText : "USER ID",
					dataType : "string",
					width : 100
				}, {
					dataField : "description",
					headerText : "작업 내용",
					dataType : "string",
					width : 450,
					style : "left indent10"
				}, {
					dataField : "creator",
					headerText : "검토자",
					dataType : "string",
					width : 100
				}, {
					dataField : "version",
					headerText : "버전",
					dataType : "string",
					width : 80
				}, {
					dataField : "state",
					headerText : "상태",
					dataType : "string",
					width : 80
				}, {
					dataField : "model",
					headerText : "모델",
					dataType : "string",
					width : 100
				}, {
					dataField : "pdate",
					headerText : "발행일",
					dataType : "date",
					formatString : "yyyy-mm-dd",
					width : 100
				}, {
					dataField : "creator",
					headerText : "작성자",
					dataType : "string",
					width : 100
				}, {
					dataField : "createdDate",
					headerText : "작성일",
					dataType : "date",
					formatString : "yyyy-mm-dd",
					width : 100
				}, {
					dataField : "modifier",
					headerText : "수정자",
					dataType : "string",
					width : 100
				}, {
					dataField : "modifiedDate",
					headerText : "수정일",
					dataType : "date",
					formatString : "yyyy-mm-dd",
					width : 100
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
					// 그리드 공통속성 끝
					showRowCheckColumn : true,
				};

				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				//화면 첫 진입시 리스트 호출 함수
				// 				loadGridData();
				// Lazy Loading 이벤트 바인딩
				AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);
			}

			function save() {
				if (!confirm("저장 하시겠습니까?")) {
					return false;
				}
				let url = getCallUrl("/request/save");
				let params = new Object();
				let removeRows = AUIGrid.getRemovedItems(myGridID);
				params.removeRows = removeRows;
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						loadGridData();
					} else {
						// 실패..
					}
				})
			}

			function create() {
				let url = getCallUrl("/request/create");
				popup(url);
			}

			function loadGridData() {
				let params = new Object();
				let url = getCallUrl("/request/list");
				AUIGrid.showAjaxLoader(myGridID); // .. 프리로더 개선해야함..
				call(url, params, function(data) {
					document.getElementById("curPage").value = data.curPage;
					document.getElementById("sessionid").value = data.sessionid;
					AUIGrid.setGridData(myGridID, data.list);
					AUIGrid.removeAjaxLoader(myGridID);
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
				let curPage = document.getElementById("curPage").value;
				let sessionid = document.getElementById("sessionid").value
				params.sessionid = sessionid;
				params.start = (curPage * 100);
				params.end = (curPage * 100) + 100;
				let url = getCallUrl("/aui/appendData");
				AUIGrid.showAjaxLoader(myGridID);
				call(url, params, function(data) {
					if (data.list.length == 0) {
						last = true;
					} else {
						AUIGrid.appendData(myGridID, data.list);
						document.getElementById("curPage").value = parseInt(curPage) + 1;
					}
					AUIGrid.removeAjaxLoader(myGridID);
				})
			}

			// 행 삭제
			function deleteRow() {
				let checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				for (let i = checkedItems.length - 1; i >= 0; i--) {
					let rowIndex = checkedItems[i].rowIndex;
					AUIGrid.removeRow(myGridID, rowIndex);
				}
			}

			// jquery 삭제를 해가는 쪽으로 한다..
			document.addEventListener("DOMContentLoaded", function() {
				// DOM이 로드된 후 실행할 코드 작성
				let columns = loadColumnLayout("request-list");
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
				parent.closeLayer();
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
	</form>
</body>
</html>