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
					<input type="text" name="name" id="name" class="AXInput">
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
					<input type="button" value="테이블 저장" title="테이블 저장" class="orange" onclick="saveColumnLayout('spec-list');">
					<input type="button" value="저장" title="저장" onclick="create();">
					<input type="button" value="자식 추가" title="자식 추가" class="orange" onclick="addTreeRow();">
					<input type="button" value="행 추가" title="행 추가" class="blue" onclick="addRow();">
					<input type="button" value="행 삭제" title="행 삭제" class="red" onclick="deleteRow();">
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
					dataField : "name",
					headerText : "코드 명",
					dataType : "string",
					width : 350,
					style : "left",
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "code",
					headerText : "코드",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "codeType",
					headerText : "코드타입",
					dataType : "string", // 날짜 및 사람명 컬럼 사이즈 100
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "enable",
					headerText : "사용여부",
					dataType : "boolean",
					width : 100,
					renderer : {
						type : "CheckBoxEditRenderer"
					},
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "description",
					headerText : "설명",
					dataType : "string",
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
					editable : true
				};

				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				//화면 첫 진입시 리스트 호출 함수
				loadGridData();
			}

			function loadGridData() {
				let params = new Object();
				let url = getCallUrl("/spec/list");
				AUIGrid.showAjaxLoader(myGridID);
				parent.openLayer();
				call(url, params, function(data) {
					AUIGrid.removeAjaxLoader(myGridID);
					AUIGrid.setGridData(myGridID, data.list);
					parent.closeLayer();
				});
			}

			// 행 추가
			function addRow() {
				var selectedItems = AUIGrid.getSelectedItems(myGridID);
				var selItem;
				var parentItem;
				var parentRowId;

				if (selectedItems.length > 0) {
					selItem = selectedItems[0].item;

					// 선택 행의 동급 레벨로 추가하기 위해 선택행의 부모 가져오기
					parentItem = AUIGrid.getParentItemByRowId(myGridID, selItem.oid);
					parentRowId;

					if (parentItem) {
						parentRowId = parentItem.oid;
					} else {
						parentRowId = null; // parentRowId 를 null 로 하면 최상위 행이 생깁니다.
					}
				} else {
					// 선택행이 없으므로 최상단에 행 추가시킴.
					parentRowId = null;
				}

				var newItem = new Object();
				newItem.parentRowId = parentRowId; // 부모의 rowId 값을 보관해 놓음...나중에 개발자가 유용하게 쓰기 위함...실제 그리드는 사용하지 않음.

				if (parentItem == null) {
					newItem.id = "새 자식";
				} else {
					newItem.id = parentItem.id + "-자식";
				}
				newItem.enable = true;

				// 행 위치 시킬 곳, 셀렉트 값.

				// parameter
				// item : 삽입하고자 하는 아이템 Object 또는 배열(배열인 경우 다수가 삽입됨)
				// rowId : 삽입되는 행의 부모 rowId 값 (null 인 경우 root 에 해당됨)
				// rowPos : first : 상단, last : 하단, selectionUp : 선택된 곳 위, selectionDown : 선택된 곳 아래
				AUIGrid.addTreeRow(myGridID, newItem, parentRowId, "last");
			}

			// 행 추가
			function addTreeRow() {
				var selectedItems = AUIGrid.getSelectedItems(myGridID);
				if (selectedItems.length <= 0)
					return;

				var selItem = selectedItems[0].item;
				var parentRowId = selItem.oid; // 선택행의 자식으로 행 추가

				var newItem = new Object();
				newItem.parentRowId = parentRowId; // 부모의 rowId 값을 보관해 놓음...나중에 개발자가 유용하게 쓰기 위함...실제 그리드는 사용하지 않음.
				newItem.oid = selItem.oid + "-자식";
				newItem.enable = true;

				// parameter
				// item : 삽입하고자 하는 아이템 Object 또는 배열(배열인 경우 다수가 삽입됨)
				// rowId : 삽입되는 행의 부모 rowId 값
				// rowPos : first : 상단, last : 하단, selectionUp : 선택된 곳 위, selectionDown : 선택된 곳 아래
				AUIGrid.addTreeRow(myGridID, newItem, parentRowId, "selectionDown");
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
				let columns = loadColumnLayout("spec-list");
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
	</form>
</body>
</html>