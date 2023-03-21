<%@page import="wt.org.WTUser"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.admin.commonCode.CommonCodeType"%>
<%@page import="org.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
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
				<th>DRAWING TITLE</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-200">
				</td>
				<th>LOT NO</th>
				<td class="indent5">
					<input type="number" name="lotNo" id="lotNo" class="width-200">
				</td>
				<th>작성자</th>
				<td class="indent5">
					<input type="text" name="creator" id="creator" class="width-200">
				</td>
				<th>작성일</th>
				<td class="indent5">
					<input type="text" name="createdFrom" id="createdFrom" class="width-100">
					~
					<input type="text" name="createdTo" id="createdTo" class="width-100">
				</td>
			</tr>
			<tr>
				<th>DWG NO</th>
				<td class="indent5">
					<input type="text" name="keNumber" id="keNumber" class="width-200">
				</td>
				<th>버전</th>
				<td>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="latest" value="true" checked="checked">
						<div class="state p-success">
							<label>
								<b>죄신버전</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="latest" value="">
						<div class="state p-success">
							<label>
								<b>모든버전</b>
							</label>
						</div>
					</div>
				</td>
				<th>수정자</th>
				<td class="indent5">
					<input type="text" name="modifier" id="modifier" class="width-200">
				</td>
				<th>수정일</th>
				<td class="indent5">
					<input type="text" name="modifiedFrom" id="modifiedFrom" class="width-100">
					~
					<input type="text" name="modifiedTo" id="modifiedTo" class="width-100">
				</td>
			</tr>
		</table>

		<!-- 버튼 테이블 -->
		<table class="button-table">
			<tr>
				<td class="left">
					<!-- exportExcel 함수참고 -->
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();">
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('kePart-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('kePart-list');">
					<input type="button" value="저장" title="저장" onclick="create();">
					<input type="button" value="개정" title="개정" class="red" onclick="revise();">
					<input type="button" value="행 추가" title="행 추가" class="blue" onclick="addRow();">
					<%
					if (isAdmin) {
					%>
					<input type="button" value="행 삭제" title="행 삭제" class="red" onclick="deleteRow();">
					<%
					}
					%>
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
		<div id="grid_wrap" style="height: 670px; border-top: 1px solid #3180c3;"></div>
		<!-- 컨텍스트 메뉴 사용시 반드시 넣을 부분 -->
		<%@include file="/extcore/jsp/common/aui/aui-context.jsp"%>
		<script type="text/javascript">
			let myGridID;
			const list = [ "작업중", "승인중", "승인됨", "폐기됨" ];
			function _layout() {
				return [ {
					dataField : "lotNo",
					headerText : "LOT",
					dataType : "numeric",
					width : 80,
					formatString : "###0",
					editRenderer : {
						type : "InputEditRenderer",
						onlyNumeric : true, // 0~9만 입력가능
						maxlength : 3,
					// 						validator : function(oldValue, newValue, item) {
					// 							var isValid = false;
					// 							if (newValue && newValue.length == 3) {
					// 								isValid = true;
					// 							}
					// 							return {
					// 								"validate" : isValid,
					// 								"message" : "LOT NO 값은 반드시 3자리로 작성이 되어야합니다."
					// 							};
					// 						}
					},
					filter : {
						showIcon : true,
						inline : true,
						displayFormatValues : true
					},
				}, {
					dataField : "code",
					headerText : "중간코드",
					dataType : "string",
					width : 100,
					editRenderer : {
						// 한글 입력 못하도록 정규식
						type : "InputEditRenderer",
						regExp : "^[a-zA-Z0-9]+$",
						autoUpperCase : true,
						maxlength : 10,
					// 						validator : function(oldValue, newValue, item) {
					// 							var isValid = false;
					// 							if (newValue && newValue.length == 10) {
					// 								isValid = true;
					// 							}
					// 							return {
					// 								"validate" : isValid,
					// 								"message" : "중간코드의 값은 반드시 10자리로 작성이 되어야합니다."
					// 							};
					// 						}
					},
					filter : {
						showIcon : true,
						inline : true,
					},
				}, {
					dataField : "keNumber",
					headerText : "부품번호",
					dataType : "string",
					width : 100,
					style : "underline",
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.oid;
							const moid = item.moid;
							const url = getCallUrl("/kePart/tabper?oid=" + oid + "&moid=" + moid);
							popup(url, 1400, 700);
						}
					},
					editRenderer : {
						// 한글 입력 못하도록 정규식
						type : "InputEditRenderer",
						regExp : "^[a-zA-Z0-9]+$",
						autoUpperCase : true,
						maxlength : 10,
					},
					filter : {
						showIcon : true,
						inline : true,
					},
				}, {
					dataField : "name",
					headerText : "부품명",
					dataType : "string",
					width : 200,
					style : "underline",
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.oid;
							const url = getCallUrl("/kePart/view?oid=" + oid);
							popup(url, 1300, 650);
						}
					},
					editRenderer : {
						// 한글 입력 못하도록 정규식
						type : "InputEditRenderer",
						autoUpperCase : true
					},
					filter : {
						showIcon : true,
						inline : true,
					},
				}, {
					dataField : "model",
					headerText : "KokusaiModel",
					dataType : "string",
					width : 300,
					editRenderer : {
						// 한글 입력 못하도록 정규식
						type : "InputEditRenderer",
						autoUpperCase : true
					},
					filter : {
						showIcon : true,
						inline : true,
					},
				}, {
					dataField : "version",
					headerText : "버전",
					dataType : "numeric",
					formatString : "###0",
					width : 80,
					editable : false,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "latest",
					headerText : "최신버전",
					dataType : "string",
					width : 80,
					renderer : {
						type : "CheckBoxEditRenderer",
						edtiable : false,
					},
					filter : {
						showIcon : false,
						inline : false
					},
				}, {
					dataField : "state",
					headerText : "상태",
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
						type : "ComboBoxRenderer",
						autoCompleteMode : true, // 자동완성 모드 설정
						autoEasyMode : true,
						matchFromFirst : false, // 처음부터 매치가 아닌 단순 포함되는 자동완성
						showEditorBtnOver : false, // 마우스 오버 시 에디터버턴 보이기
						list : list,
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							for (let i = 0, len = list.length; i < len; i++) { // keyValueList 있는 값만..
								if (list[i] == newValue) {
									isValid = true;
									break;
								}
							}
							return {
								"validate" : isValid,
								"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
							};
						}
					},
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "creator",
					headerText : "등록자",
					dataType : "string",
					width : 100,
					editable : false,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "createdDate",
					headerText : "등록일",
					dataType : "date",
					formatString : "yyyy-mm-dd",
					width : 100,
					editable : false,
					filter : {
						showIcon : true,
						inline : true,
						displayFormatValues : true
					},
				}, {
					dataField : "modifier",
					headerText : "수정자",
					dataType : "string",
					width : 100,
					editable : false,
					filter : {
						showIcon : true
					}
				}, {
					dataField : "modifiedDate",
					headerText : "수정일",
					dataType : "date",
					formatString : "yyyy-mm-dd",
					width : 100,
					editable : false,
					filter : {
						showIcon : true,
						inline : true,
						displayFormatValues : true
					},
				}, {
					dataField : "primary",
					headerText : "첨부파일",
					dataType : "string",
					width : 100,
					renderer : {
						type : "TemplateRenderer",
					},
					filter : {
						showIcon : false,
						inline : false
					},
				}, {
					dataField : "button",
					headerText : "",
					width : 100,
					editable : false,
					renderer : {
						type : "ButtonRenderer",
						labelText : "파일선택",
						onclick : function(rowIndex, columnIndex, value, item) {
							recentGridItem = item;
							const _$uid = item._$uid;
							const url = getCallUrl("/aui/primary?oid=" + _$uid + "&method=attach");
							popup(url, 1000, 200);
						}
					},
					filter : {
						showIcon : false,
						inline : false
					},
				}, {
					dataField : "note",
					headerText : "개정사유",
					dateType : "string",
					width : 250,
					editable : false,
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
					// 그리드 공통속성 시작
					headerHeight : 30,
					rowHeight : 30,
					showRowNumColumn : true,
					showRowCheckColumn : true,
					showStateColumn : true,
					rowNumHeaderText : "번호",
					noDataMessage : "검색 결과가 없습니다.",
					enableFilter : true,
					selectionMode : "multipleCells",
					enableMovingColumn : true,
					showInlineFilter : true,
					useContextMenu : true,
					enableRightDownFocus : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
					// 그리드 공통속성 끝
					editable : true
				};

				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				loadGridData();

				// 컨텍스트 메뉴 이벤트 바인딩
				AUIGrid.bind(myGridID, "contextMenu", auiContextMenuHandler);

				// 스크롤 체인지 핸들러.
				AUIGrid.bind(myGridID, "vScrollChange", function(event) {
					hideContextMenu(); // 컨텍스트 메뉴 감추기
					vScrollChangeHandler(event); // lazy loading
				});

				AUIGrid.bind(myGridID, "hScrollChange", function(event) {
					hideContextMenu(); // 컨텍스트 메뉴 감추기
				});

				// 행 추가후
				AUIGrid.bind(myGridID, "pasteEnd", auiPasteEnd);
			}

			function auiPasteEnd(event) {
				const clipboardData = event.clipboardData;
				for (let i = 0; i < clipboardData.length; i++) {
					AUIGrid.setCellValue(myGridID, i, "latest", true);
					AUIGrid.setCellValue(myGridID, i, "state", "작업중");
				}
			}

			function loadGridData() {
				const params = new Object();
				const url = getCallUrl("/kePart/list");
				const latest = !!document.querySelector("input[name=latest]:checked").value;
				const psize = document.getElementById("psize").value;
				params.latest = latest;
				params.psize = psize;
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

			// 저장
			function create() {
				const url = getCallUrl("/kePart/create");
				const params = new Object();
				const addRows = AUIGrid.getAddedRowItems(myGridID);
				const removeRows = AUIGrid.getRemovedItems(myGridID);
				const editRows = AUIGrid.getEditedRowItems(myGridID);

				if (addRows.length === 0 && removeRows.length === 0 && editRows.length === 0) {
					alert("변경된 내용이 없습니다.");
					return false;
				}

				// 새로 추가한 행 검증
				for (let i = 0; i < addRows.length; i++) {
					const item = addRows[i];

					if (item.lotNo === 0) {
						AUIGrid.showToastMessage(myGridID, i, 0, "LOT NO의 값은 0을 입력 할 수 없습니다.");
						return false;
					}

					if (isNull(item.code)) {
						AUIGrid.showToastMessage(myGridID, i, 1, "중간코드의 값은 공백을 입력 할 수 없습니다.");
						return false;
					}

					if (isNull(item.keNumber)) {
						AUIGrid.showToastMessage(myGridID, i, 2, "부품번호의 값은 공백을 입력 할 수 없습니다.");
						return false;
					}

					if (isNull(item.name)) {
						AUIGrid.showToastMessage(myGridID, i, 3, "부품명 값은 공백을 입력 할 수 없습니다.");
						return false;
					}

					if (isNull(item.model)) {
						AUIGrid.showToastMessage(myGridID, i, 4, "KokusaiModel 값은 공백을 입력 할 수 없습니다.");
						return false;
					}

					if (isNull(item.primary)) {
						AUIGrid.showToastMessage(myGridID, i, 11, "첨부파일을 선택하세요.");
						return false;
					}
				}

				// 수정행
				for (let i = 0; i < editRows.length; i++) {
					const item = editRows[i];

					if (item.lotNo === 0) {
						AUIGrid.showToastMessage(myGridID, i, 0, "LOT NO의 값은 0을 입력 할 수 없습니다.");
						return false;
					}

					if (isNull(item.code)) {
						AUIGrid.showToastMessage(myGridID, i, 1, "중간코드의 값은 공백을 입력 할 수 없습니다.");
						return false;
					}

					if (isNull(item.keNumber)) {
						AUIGrid.showToastMessage(myGridID, i, 2, "부품번호의 값은 공백을 입력 할 수 없습니다.");
						return false;
					}

					if (isNull(item.name)) {
						AUIGrid.showToastMessage(myGridID, i, 3, "부품명 값은 공백을 입력 할 수 없습니다.");
						return false;
					}

					if (isNull(item.model)) {
						AUIGrid.showToastMessage(myGridID, i, 4, "KokusaiModel 값은 공백을 입력 할 수 없습니다.");
						return false;
					}

					if (isNull(item.primary)) {
						AUIGrid.showToastMessage(myGridID, i, 11, "첨부파일을 선택하세요.");
						return false;
					}
				}

				if (!confirm("저장 하시겠습니까?")) {
					return false;
				}

				params.addRows = addRows;
				params.removeRows = removeRows;
				params.editRows = editRows;
				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					parent.closeLayer();
					if (data.result) {
						loadGridData();
					}
				});
			}

			// 행 추가
			function addRow() {
				const item = {
					latest : true,
				};
				AUIGrid.addRow(myGridID, item, "first");
			}

			// 행 삭제
			function deleteRow() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				for (let i = checkedItems.length - 1; i >= 0; i--) {
					const rowIndex = checkedItems[i].rowIndex;
					AUIGrid.removeRow(myGridID, rowIndex);
				}
			}

			function attach(data) {
				const template = "<img src='" + data.icon + "' style='position: relative; top: 2px;'>";
				AUIGrid.updateRowsById(myGridID, {
					_$uid : recentGridItem._$uid,
					primary : template,
					primaryPath : data.fullPath
				});
			}

			function revise() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				if (checkedItems.length == 0) {
					alert("개정할 부품을 선택하세요.");
					return false;
				}

				console.log(checkedItems);
				for (let i = 0; i < checkedItems.length; i++) {
					const oid = checkedItems[i].item.oid;
					const latest = checkedItems[i].item.latest;
					const rowIndex = checkedItems[i].rowIndex;
					const state = checkedItems[i].state;

					if (state !== "승인됨") {
						alert("승인되지 않은 부품이 포함되어있습니다.\n" + rowIndex + "행 데이터");
						return false;
					}

					if (!latest) {
						alert("최신버전이 아닌 부품이 포함되어있습니다.\n" + rowIndex + "행 데이터");
						return false;
					}

					if (oid === undefined) {
						alert("신규로 작성한 데이터가 존재합니다.\n" + rowIndex + "행 데이터");
						return false;
					}
				}
				console.log(checkedItems);
				const url = getCallUrl("/kePart/revise");
				const panel = popup(url, 1600, 550);
				panel.list = checkedItems;
			}

			function exportExcel() {
				const exceptColumnFields = [ "button", "primary" ];
				exportToExcel("KE 부품 리스트", "KE 부품", "KE 부품 리스트", exceptColumnFields, "<%=sessionUser.getFullName()%>	");
			}

			document.addEventListener("DOMContentLoaded", function() {
				// DOM이 로드된 후 실행할 코드 작성
				const columns = loadColumnLayout("kePart-list");
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
	</form>
</body>
</html>