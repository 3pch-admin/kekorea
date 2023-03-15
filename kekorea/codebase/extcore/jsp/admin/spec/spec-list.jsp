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
					<input type="text" name="" id="" class="AXInput">
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
					<input type="button" value="저장" title="저장" onclick="save();">
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
			const list = [ {
				"key" : "SPEC",
				"value" : "사양"
			}, {
				"key" : "OPTION",
				"value" : "옵션"
			} ];
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
						list : list,
						keyField : "key", // key 에 해당되는 필드명
						valueField : "value", // value 에 해당되는 필드명,
					},
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
						let retStr = ""; // key 값에 맞는 value 를 찾아 반환함.
						for (let i = 0, len = list.length; i < len; i++) {
							if (list[i]["key"] == value) {
								retStr = list[i]["value"];
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
					dataField : "sort",
					headerText : "정렬",
					dataType : "numeric",
					width : 100,
					formatString : "###0",
					editRenderer : {
						type : "InputEditRenderer",
						onlyNumeric : true, // 0~9만 입력가능
					},
					filter : {
						showIcon : false,
						inline : false
					},
				}, {
					dataField : "enable",
					headerText : "사용여부",
					dataType : "boolean",
					width : 100,
					renderer : {
						type : "CheckBoxEditRenderer",
						editable : true,
						disabledFunction : function(rowIndex, columnIndex, value, isChecked, item, dataField) {
							if (rowIndex != 0) {
								return false;
							}
							return true;
						},
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
					editable : true,
					enableRowCheckShiftKey : true,
					displayTreeOpen : true
				};

				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				//화면 첫 진입시 리스트 호출 함수
				loadGridData();
				AUIGrid.bind(myGridID, "addRowFinish", auiAddRowFinish);
				AUIGrid.bind(myGridID, "cellEditBegin", auiCellEditBegin);
			}
			
			// 저장
			function save() {

				if (!confirm("저장 하시겠습니까?")) {
					return false;
				}

				const url = getCallUrl("/spec/save");
				const params = new Object();
				const addRows = AUIGrid.getAddedRowItems(myGridID);
				const removeRows = AUIGrid.getRemovedItems(myGridID);
				const editRows = AUIGrid.getEditedRowItems(myGridID);
				
				for (let i = 0; i < addRows.length; i++) {
					const item = addRows[i];
					if(isNull(item.name)) {
						AUIGrid.showToastMessage(myGridID, i+1, 0, "코드 명 값은 공백을 입력 할 수 없습니다.");
						return false;
					}
					if(isNull(item.code)) {
						AUIGrid.showToastMessage(myGridID, i+1, 1, "코드 값은 공백을 입력 할 수 없습니다.");
						return false;
					}
					if(isNull(item.codeType)) {
						AUIGrid.showToastMessage(myGridID, i+1, 2, "코드 타입 값을 선택하세요.");
						return false;
					}
					if(isNull(item.sort)) {
						AUIGrid.showToastMessage(myGridID, i+1, 3, "정렬 값은 공백을 입력 할 수 없습니다.");
						return false;
					}
				}
				
				
				params.addRows = addRows;
				params.removeRows = removeRows;
				params.editRows = editRows;
				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						loadGridData();
					} else {
						// 실패...
					}
				})
			}

			function auiCellEditBegin(event) {
				const dataField = event.dataField;
				const rowIndex = event.rowIndex;
				if (rowIndex == 0) {
					return false;
				}

				if (dataField === "codeType") {
					return false;
				}
				return true;
			}

			function auiAddRowFinish(event) {
				const item = event.items[0];
				const depth = item._$depth;
				if (depth == 2) {
					const item = {
						"codeType" : "SPEC"
					};
					AUIGrid.updateRow(myGridID, item, "selectedIndex");
				}

				if (depth == 3) {
					const item = {
						"codeType" : "OPTION"
					};
					AUIGrid.updateRow(myGridID, item, "selectedIndex");
				}

				if (depth > 3 || depth === undefined) {
					AUIGrid.removeRow(myGridID, "selectedIndex");
				}

				const selected = AUIGrid.getSelectedIndex(myGridID);
				if (selected.length <= 0) {
					return;
				}

				const rowIndex = selected[0];
				const colIndex = AUIGrid.getColumnIndexByDataField(myGridID, "name");
				AUIGrid.setSelectionByIndex(myGridID, rowIndex, colIndex);
				AUIGrid.openInputer(myGridID);
			}

			function loadGridData() {
				const params = new Object();
				const url = getCallUrl("/spec/list");
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
				const selectedItems = AUIGrid.getSelectedItems(myGridID);
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
				newItem.enable = true;
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
				newItem.enable = true;
				AUIGrid.addTreeRow(myGridID, newItem, parentRowId, "selectionDown");
			}

			// 행 삭제
			function deleteRow() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				for (const i = checkedItems.length - 1; i >= 0; i--) {
					const rowIndex = checkedItems[i].rowIndex;
					AUIGrid.removeRow(myGridID, rowIndex);
				}
			}

			// jquery 삭제를 해가는 쪽으로 한다..
			document.addEventListener("DOMContentLoaded", function() {
				// DOM이 로드된 후 실행할 코드 작성
				const columns = loadColumnLayout("spec-list");
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
			});

			document.addEventListener("keydown", function(event) {
				// 키보드 이벤트 객체에서 눌린 키의 코드 가져오기
				const keyCode = event.keyCode || event.which;
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