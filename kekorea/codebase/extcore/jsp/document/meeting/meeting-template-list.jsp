<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
%>
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
<!-- AUIGrid 리스트페이지에서만 사용할 js파일 -->
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js"></script>
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
				<td class="indent5">
					<input type="text" name="fileName" class="width-300">
				</td>
				<th>설명</th>
				<td class="indent5">
					<input type="text" name="partCode" class="width-300">
				</td>
				<th>작성자</th>
				<td class="indent5">
					<input type="text" name="partName" class="width-100">
				</td>
				<th>작성일</th>
				<td class="indent5">
					<input type="text" name="created" id="created" class="width-200" readonly="readonly">
					<img src="/Windchill/extcore/images/calendar.gif" class="calendar" title="달력열기">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" data-target="created">
					<!-- data-target 달력 태그 ID -->
					<input type="hidden" name="createdFrom" id="createdFrom">
					<!-- 달력 태그 아이디값 + From -->
					<input type="hidden" name="createdTo" id="createdTo">
					<!-- 달력 태그 아이디값 + To -->
				</td>
			</tr>
		</table>

		<!-- 버튼 테이블 -->
		<table class="button-table">
			<tr>
				<td class="left">
					<input type="button" value="테이블 저장" title="테이블 저장" class="orange" onclick="saveColumnLayout('meeting-template-list');">
					<input type="button" value="테이블 초기화" title="테이블 초기화" onclick="resetColumnLayout('meeting-template-list');">
					<input type="button" value="등록" title="등록" class="blue" onclick="create();">
					<input type="button" value="저장" title="저장" onclick="save();">
					<input type="button" value="행 삭제" title="행 삭제" class="red" onclick="deleteRow();">
				</td>
				<td class="right">
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
					dataField : "name",
					headerText : "회의록 템플릿 제목",
					dataType : "string",
					style : "left indent10 underline",
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "creator",
					headerText : "작성자",
					dataType : "string", // 날짜 및 사람명 컬럼 사이즈 100
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
					fillColumnSizeMode : true,
					showRowCheckColumn : true,
					useContextMenu : true
				};

				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				//화면 첫 진입시 리스트 호출 함수
				loadGridData();
				// Lazy Loading 이벤트 바인딩
				AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);
				// cellclick
				AUIGrid.bind(myGridID, "cellClick", auiCellClickHandler);

				// 컨텍스트 메뉴 이벤트 바인딩
				AUIGrid.bind(myGridID, "contextMenu", auiContextMenuHandler);

				// 스크롤 체인지 핸들러.
				AUIGrid.bind(myGridID, "vScrollChange", function(event) {
					hideContextMenu(); // 컨텍스트 메뉴 감추기
				});

				AUIGrid.bind(myGridID, "hScrollChange", function(event) {
					hideContextMenu(); // 컨텍스트 메뉴 감추기
				});
			}

			function auiCellClickHandler(event) {
				const dataField = event.dataField;
				const item = event.item;
				if (dataField === "name") {
					const url = getCallUrl("/meeting/info?oid=" + item.oid);
					popup(url);
				}
			}

			function loadGridData() {
				const params = new Object();
				const url = getCallUrl("/meeting/template");
				AUIGrid.showAjaxLoader(myGridID);
				parent.openLayer();
				call(url, params, function(data) {
					AUIGrid.removeAjaxLoader(myGridID);
					document.getElementById("sessionid").value = data.sessionid;
					document.getElementById("curPage").value = data.curPage;
					AUIGrid.setGridData(myGridID, data.list);
					parent.closeLayer();
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
				const params = new Object();
				const curPage = document.getElementById("curPage").value;
				const sessionid = document.getElementById("sessionid").value;
				params.sessionid = sessionid;
				params.start = (curPage * 100);
				params.end = (curPage * 100) + 100;
				const url = getCallUrl("/aui/appendData");
				AUIGrid.showAjaxLoader(myGridID);
				parent.openLayer();
				call(url, params, function(data) {
					if (data.list.length == 0) {
						last = true;
					} else {
						AUIGrid.appendData(myGridID, data.list);
						document.getElementById("curPage").value = parseInt(curPage) + 1;
					}
					AUIGrid.removeAjaxLoader(myGridID);
					parent.closeLayer();
				})
			}

			// 등록
			function create() {
				const url = getCallUrl("/meeting/format");
				popup(url);
			}

			function save() {

				if (!confirm("저장 하시겠습니까?")) {
					return false;
				}

				const url = getCallUrl("/meeting/save");
				const params = new Object();
				const removeRows = AUIGrid.getRemovedItems(myGridID);
				params.removeRows = removeRows;
				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					parent.closeLayer();
					if (data.result) {
						loadGridData();
					}
				});
			}

			// 행 삭제
			function deleteRow() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				for (let i = checkedItems.length - 1; i >= 0; i--) {
					const rowIndex = checkedItems[i].rowIndex;
					AUIGrid.removeRow(myGridID, rowIndex);
				}
			}

			document.addEventListener("DOMContentLoaded", function() {
				// DOM이 로드된 후 실행할 코드 작성
				const columns = loadColumnLayout("meeting-template-list");
				// 컨텍스트 메뉴 시작
				let contenxtHeader = genColumnHtml(columns); // see auigrid.js
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
				
				// 범위 달력
				fromToCalendar("created", "calendar");
				// 범위 달력 값 삭제
				fromToDelete("delete")
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
	</form>
</body>
</html>