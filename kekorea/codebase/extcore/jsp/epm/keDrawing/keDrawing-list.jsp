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
					<input type="button" value="테이블 저장" title="테이블 저장" class="orange" onclick="saveColumnLayout('keDrawing-list');">
					<input type="button" value="저장" title="저장" onclick="create();">
					<input type="button" value="개정" title="개정" class="red" onclick="revise();">
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
			let recentGridItem = null;
			function _layout() {
				return [ {
					dataField : "lotNo",
					headerText : "LOT",
					dataType : "numeric",
					width : 100,
					formatString : "###0",
					editRenderer : {
						type : "InputEditRenderer",
						onlyNumeric : true, // 0~9만 입력가능
					},
					filter : {
						showIcon : true,
						useExMenu : true
					},
				}, {
					dataField : "name",
					headerText : "DRAWING TITLE",
					dataType : "string",
				}, {
					dataField : "keNumber",
					headerText : "DWG. NO",
					dataType : "string",
					width : 200,
					editable : false,
				}, {
					dataField : "version",
					headerText : "버전",
					dataType : "numeric",
					width : 80,
					editable : false,
				}, {
					dataField : "latest",
					headerText : "최신버전",
					dataType : "boolean",
					width : 100,
					renderer : {
						type : "CheckBoxEditRenderer"
					},
					editable : false,
				}, {
					dataField : "creator",
					headerText : "작성자",
					dataType : "string",
					width : 100,
					editable : false,
				}, {
					dataField : "createdDate",
					headerText : "작성일",
					dataType : "date",
					formatString : "yyyy-mm-dd",
					width : 100,
					editable : false,
				}, {
					dataField : "modifier",
					headerText : "수정자",
					dataType : "string",
					width : 100,
					editable : false,
				}, {
					dataField : "modifiedDate",
					headerText : "수정일",
					dataType : "date",
					formatString : "yyyy-mm-dd",
					width : 100,
					editable : false,
				}, {
					dataField : "primary",
					headerText : "도면파일",
					dataType : "string",
					width : 100,
					editable : false,
					renderer : {
						type : "TemplateRenderer",
					},
				}, {
					dataField : "",
					headerText : "",
					width : 100,
					editable : false,
					renderer : {
						type : "ButtonRenderer",
						labelText : "파일선택",
						onclick : function(rowIndex, columnIndex, value, item) {
							recentGridItem = item;
							let oid = item.oid;
							let url = getCallUrl("/aui/primary?oid=" + oid + "&method=attach");
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
					editable : false
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
					editable : true,
					showRowCheckColumn : true,
				};

				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				// 화면 첫 진입시 리스트 호출 함수
				// 등록이 있는곳은 제외 한다.
				// loadGridData();
				// Lazy Loading 이벤트 바인딩
				AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);
			}

			function loadGridData() {
				let params = new Object();
				let url = getCallUrl("/keDrawing/list");
				AUIGrid.showAjaxLoader(myGridID);
				call(url, params, function(data) {
					AUIGrid.removeAjaxLoader(myGridID);
					$("input[name=sessionid]").val(data.sessionid);
					$("input[name=curPage]").val(data.curPage);
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
				let params = new Object();
				let curPage = $("input[name=curPage]").val();
				params.sessionid = $("input[name=sessionid]").val();
				params.start = (curPage * 100);
				params.end = (curPage * 100) + 100;
				let url = getCallUrl("/appendData");
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

			// 행 추가
			function addRow() {
				let item = new Object();
				item.createdDate = new Date();
				item.modifiedDate = new Date();
				item.creator = "<%=sessionUser.getFullName()%>";
				item.modifier = "<%=sessionUser.getFullName()%>";
				item.latest = true;
				AUIGrid.addRow(myGridID, item, "first");
			}

			function attach(data) {
				let name = data.name;
				let start = name.indexOf("-");
				let end = name.lastIndexOf(".");
				let number = name.substring(0, start);
				let version = name.substring(start + 1, end);
				let template = "<img src='" + data.icon + "' style='position: relative; top: 2px;'>";
				AUIGrid.updateRowsById(myGridID, {
					oid : recentGridItem.oid,
					number : number,
					version : Number(version),
					file : name,
					primary : template,
					primaryPath : data.fullPath
				});
			}

			// 로딩 레이어 삭제
			parent.closeLayer();

			// 저장
			function create() {

				if (!confirm("저장 하시겠습니까?")) {
					return false;
				}

				let url = getCallUrl("/keDrawing/create");
				let params = new Object();
				let addRows = AUIGrid.getAddedRowItems(myGridID);
				let removeRows = AUIGrid.getRemovedItems(myGridID);
				let editRows = AUIGrid.getEditedRowItems(myGridID);
				params.addRows = addRows;
				params.removeRows = removeRows;
				params.editRows = editRows;
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						loadGridData();
					}
				}, "POST");
			}

			function revise() {
				let checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				if (checkedItems.length == 0) {
					alert("개정할 도면을 선택하세요.");
					return false;
				}
				let url = getCallUrl("/keDrawing/revise");
				let panel;
				panel = popup(url, 1600, 550);
				panel.list = checkedItems;
			}

			// jquery 모든 DOM구조 로딩 후 
			$(function() {
				// 로컬 스토리지에 저장된 컬럼 값 불러오기 see - base.js
				let columns = loadColumnLayout("keDrawing-list");
				createAUIGrid(columns);
			}).keypress(function(e) {
				let keyCode = e.keyCode;
				if (keyCode === 13) {
					loadGridData();
				}
			})
		</script>
	</form>
</body>
</html>