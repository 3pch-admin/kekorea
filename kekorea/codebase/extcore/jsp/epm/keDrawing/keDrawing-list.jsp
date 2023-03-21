<%@page import="wt.org.WTUser"%>
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
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('keDrawing-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('keDrawing-list');">
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
			let recentGridItem = null;
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
					},
					filter : {
						showIcon : true,
						inline : true,
						displayFormatValues : true
					},
				}, {
					dataField : "name",
					headerText : "DRAWING TITLE",
					dataType : "string",
					style : "left indent10 underline",
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.oid;
							const moid = item.moid;
							const url = getCallUrl("/keDrawing/tabper?oid=" + oid + "&moid=" + moid);
							popup(url, 1400, 700);
						}
					},
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "keNumber",
					headerText : "DWG NO",
					dataType : "string",
					width : 100,
					editable : false,
					style : "underline",
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.oid;
							const moid = item.moid;
							const url = getCallUrl("/keDrawing/tabper?oid=" + oid + "&moid=" + moid);
							popup(url, 1400, 700);
						}
					},
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "version",
					headerText : "버전",
					dataType : "numeric",
					width : 80,
					editable : false,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "latest",
					headerText : "최신버전",
					dataType : "boolean",
					width : 80,
					renderer : {
						type : "CheckBoxEditRenderer"
					},
					editable : false,
					filter : {
						showIcon : false,
						inline : false
					},
				}, {
					dataField : "creator",
					headerText : "작성자",
					dataType : "string",
					width : 100,
					editable : false,
					headerTooltip : {
						show : true,
						tooltipHtml : "데이터 저장시 접속한 사용자의 이름이 입력되어집니다."
					},
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
					editable : false,
					headerTooltip : {
						show : true,
						tooltipHtml : "데이터 저장하는 날짜가 입력되어집니다."
					},
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
					headerTooltip : {
						show : true,
						tooltipHtml : "데이터 저장시 접속한 사용자의 이름이 입력되어집니다."
					},
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "modifiedDate",
					headerText : "수정일",
					dataType : "date",
					formatString : "yyyy-mm-dd",
					width : 100,
					editable : false,
					headerTooltip : {
						show : true,
						tooltipHtml : "데이터 저장하는 날짜가 입력되어집니다."
					},
					filter : {
						showIcon : true,
						inline : true,
						displayFormatValues : true
					},
				}, {
					dataField : "preView",
					headerText : "미리보기",
					width : 80,
					editable : false,
					renderer : {
						type : "ImageRenderer",
						altField : null,
						imgHeight : 34,
					},
					filter : {
						showIcon : false,
						inline : false
					},
				}, {
					dataField : "primary",
					headerText : "도면파일",
					dataType : "string",
					width : 80,
					editable : false,
					headerTooltip : {
						show : true,
						tooltipHtml : "도면파일명의 양식을 정확하게 해주세요. EX) DCB000000-001 (DWG NO : 9자리, 버전 : 3자리)"
					},
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
					width : 80,
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

				AUIGrid.bind(myGridID, "beforeRemoveRow", auiBeforeRemoveRowHandler);

				// 행 추가후
				AUIGrid.bind(myGridID, "addRowFinish", auiAddRowFinishHandler);
				AUIGrid.bind(myGridID, "cellClick", auiCellClickHandler);
			}

			function auiCellClickHandler(event) {
				const dataField = event.dataField;
				const oid = event.item.oid;
				if (dataField === "preView") {
					const url = getCallUrl("/aui/thumbnail?oid=" + oid);
					popup(url);
				}
			}

			function auiAddRowFinishHandler(event) {
				const selected = AUIGrid.getSelectedIndex(myGridID);
				if (selected.length <= 0) {
					return false;
				}

				const rowIndex = selected[0];
				const colIndex = AUIGrid.getColumnIndexByDataField(myGridID, "lotNo");
				AUIGrid.setSelectionByIndex(myGridID, rowIndex, colIndex);
				AUIGrid.openInputer(myGridID);
			}

			function auiBeforeRemoveRowHandler(event) {
				const items = event.items;
				for (let i = 0; i < items.length; i++) {
					const latest = items[i].latest;
					if (!latest) {
						alert("최신버전의 도면이 아닌 데이터가 있습니다.\n" + i + "행 데이터");
						return false;
					}
				}
				return true;
			}

			function loadGridData() {
				const params = new Object();
				const url = getCallUrl("/keDrawing/list");
				const psize = document.getElementById("psize").value;
				const lotNo = Number(document.getElementById("lotNo").value);
				const latest = !!document.querySelector("input[name=latest]:checked").value;
				params.latest = latest;
				params.lotNo = lotNo;
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
				const name = data.name;
				if (name.length !== 17) {
					alert("도면파일 이름명을 체크하세요. \nDWG NO : 9자리, 버전 3자리의 양식을 맞춰주세요.");
					return false;
				}

				const start = name.indexOf("-");
				if (start <= -1) {
					alert("도면파일 이름의 양식이 맞지 않습니다.\nDWG NO-버전 형태의 파일명만 허용됩니다.");
					return false;
				}

				const end = name.lastIndexOf(".");
				if (end <= -1) {
					alert("도면파일 확장자를 체크해주세요.");
					return false;
				}

				const ext = name.substring(end + 1);
				if (ext.toLowerCase() !== "pdf") {
					alert("PDF 파일 형식의 도면파일만 허용됩니다.");
					return false;
				}
				const number = name.substring(0, start);
				if (number.length !== 9) {
					alert("도면파일의 DWG NO의 자리수를 확인해주세요. 등록가능한 도번의 자리수는 9자리여야 합니다.");
					return false;
				}
				const version = name.substring(start + 1, end);
				if (version.length !== 3) {
					alert("도면파일의 버전 자리수를 확인해주세요. 등록가능한 버전의 자리수는 3자리여야 합니다.");
					return false;
				}
				const template = "<img src='" + data.icon + "' style='position: relative; top: 2px;'>";
				AUIGrid.updateRowsById(myGridID, {
					_$uid : recentGridItem._$uid,
					keNumber : number,
					version : Number(version),
					file : name,
					primary : template,
					primaryPath : data.fullPath
				});
			}

			// 저장
			function create() {
				const url = getCallUrl("/keDrawing/create");
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

					if (isNull(item.name)) {
						AUIGrid.showToastMessage(myGridID, i, 1, "DRAWING TITLE의 값은 공백을 입력 할 수 없습니다.");
						return false;
					}

					if (isNull(item.primary)) {
						AUIGrid.showToastMessage(myGridID, i, 9, "도면파일을 선택하세요.");
						return false;
					}
				}

				// 수정한 행 검증
				for (let i = 0; i < editRows.length; i++) {
					const item = editRows[i];
					if (item.lotNo === 0) {
						AUIGrid.showToastMessage(myGridID, i, 0, "LOT NO의 값은 0을 입력 할 수 없습니다.");
						return false;
					}

					if (isNull(item.name)) {
						AUIGrid.showToastMessage(myGridID, i, 1, "DRAWING TITLE의 값은 공백을 입력 할 수 없습니다.");
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

			function revise() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				if (checkedItems.length == 0) {
					alert("개정할 도면을 선택하세요.");
					return false;
				}

				for (let i = 0; i < checkedItems.length; i++) {
					const oid = checkedItems[i].item.oid;
					const latest = checkedItems[i].item.latest;
					const rowIndex = checkedItems[i].rowIndex;

					if (!latest) {
						alert("최신버전이 아닌 도면이 포함되어있습니다.\n" + (rowindex + 1) + "행 데이터");
						return false;
					}

					if (oid === undefined) {
						alert("신규로 작성한 데이터가 존재합니다.\n" + (rowindex + 1) + "행 데이터");
						return false;
					}
				}

				const url = getCallUrl("/keDrawing/revise");
				const panel = popup(url, 1600, 550);
				panel.list = checkedItems;
			}

			function exportExcel() {
				const exceptColumnFields = [ "preView", "button", "primary" ];
				exportToExcel("KE 도면 리스트", "KE 도면", "KE 도면 리스트", exceptColumnFields, "<%=sessionUser.getFullName()%>");
			}

			document.addEventListener("DOMContentLoaded", function() {
				// DOM이 로드된 후 실행할 코드 작성
				const columns = loadColumnLayout("keDrawing-list");
				// 컨텍스트 메뉴 시작
				const contenxtHeader = genColumnHtml(columns); // see auigrid.js
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);

				// 사용자 검색 바인딩 see base.js finderUser function 
				finderUser("creator");
				finderUser("modifier");

				// 날짜 검색용 바인딩 see base.js twindate funtion
				twindate("created");
				twindate("modified");

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