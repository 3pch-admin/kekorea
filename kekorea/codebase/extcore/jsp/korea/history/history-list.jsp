<%@page import="net.sf.json.JSONArray"%>
<%@page import="java.util.Map"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.admin.commonCode.CommonCodeType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<Map<String, String>> headers = (ArrayList<Map<String, String>>) request.getAttribute("headers");
Map<String, ArrayList<Map<String, String>>> list = (Map<String, ArrayList<Map<String, String>>>) request
		.getAttribute("list");
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
					<input type="button" value="테이블 저장" title="테이블 저장" class="orange" onclick="saveColumnLayout('history-list');">
					<input type="button" value="테이블 초기화" title="테이블 초기화" onclick="resetColumnLayout('history-list');">
					<input type="button" value="저장" title="저장" onclick="save();">
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
			const list = ["유", "무"];
			function _layout() {
				return [ {
				dataField : "pdate",
				headerText : "발행일",
				dataType : "date",
				formatString : "yyyy-mm-dd",
				width : 120,
				editable : false,
				filter : {
					showIcon : true,
					inline : true
				},				
			}, {
				dataField : "install",
				headerText : "설치장소",
				dataType : "string",
				width : 100,
				editable : false,
				filter : {
					showIcon : true,
					inline : true
				},				
			}, {
				dataField : "kekNumber",
				headerText : "KEK작번",
				dataType : "string",
				width : 140,
				filter : {
					showIcon : true,
					inline : true
				},				
			}, {
				dataField : "keNumber",
				headerText : "KE작번",
				dataType : "string",
				width : 140,
				editable : false,
				filter : {
					showIcon : true,
					inline : true
				},				
			}, {
				dataField : "tuv",
				headerText : "TUV유무",
				dataType : "string",
				width : 130,
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
					multipleMode : false, // 다중 선택 모드(기본값 : false)
					showCheckAll : false, // 다중 선택 모드에서 전체 체크 선택/해제 표시(기본값:false);
					list : list
				},				
				filter : {
					showIcon : true,
					inline : true
				},			
			},
			<%for (Map<String, String> header : headers) {
	String key = header.get("key"); // spec code ....key
	String value = header.get("value");
	JSONArray array = JSONArray.fromObject(list.get(key));
	%>
			{
				dataField : "<%=key%>",
				headerText : "<%=value%>",
				dataType : "string",
				width : 130,
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
					multipleMode : false, // 다중 선택 모드(기본값 : false)
					showCheckAll : false, // 다중 선택 모드에서 전체 체크 선택/해제 표시(기본값:false);
					keyField : "key", // key 에 해당되는 필드명
					valueField : "value", // value 에 해당되는 필드명,
					list : <%=array%>
				},			
				labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
					let retStr = ""; // key 값에 맞는 value 를 찾아 반환함.
					for (let i = 0, len = <%=array%>.length; i < len; i++) {
						if (<%=array%>[i]["key"] == value) {
							retStr = <%=array%>[i]["value"];
							break;
						}
					}
					return retStr == "" ? value : retStr;
				},				
				filter : {
					showIcon : true,
					inline : true
				},
			},
			<%}%>
			]}

			function createAUIGrid(columnLayout) {
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
				// 그리드 데이터 로딩
				loadGridData();
				// LazyLoading 바인딩
				AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);
				// 셀 편집 종료 이벤트
				AUIGrid.bind(myGridID, "cellEditEnd", editEndHandler);
				AUIGrid.bind(myGridID, "addRowFinish", auiAddRowHandler);
				
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

			function auiAddRowHandler(event) {
				const selected = AUIGrid.getSelectedIndex(myGridID);
				if (selected.length <= 0) {
					return;
				}

				const rowIndex = selected[0];
				const colIndex = AUIGrid.getColumnIndexByDataField(myGridID, "kekNumber");
				AUIGrid.setSelectionByIndex(myGridID, rowIndex, colIndex);
				AUIGrid.openInputer(myGridID);
			}

			function editEndHandler(event) {
				const dataField = event.dataField;
				const item = event.item;

				if(dataField === "kekNumber") {
					const url = getCallUrl("/project/get?kekNumber="+item.kekNumber);
					call(url, null, function(data) {
						AUIGrid.updateRowsById(myGridID, {
							oid : item.oid,
							keNumber : data.keNumber,
							install : data.install,
							pDate : data.pDate,
							poid : data.oid
						});
					}, "GET");
				}
			};

			function save() {
				if(!confirm("저장 하시겠습니까?")) {
					return false;
				}
				const url = getCallUrl("/history/save");
				const editRows = AUIGrid.getEditedRowItems(myGridID);
				const removeRows = AUIGrid.getRemovedItems(myGridID);
				const params = new Object();
				params.editRows = editRows;
				params.removeRows = removeRows;
				console.log(params);
				call(url, params, function(data) {
					alert(data.msg);
					if(data.result) {
						loadGridData();
					} else {
						// 실패..
						
					}
				})
			}
			
			function loadGridData() {
				const params = new Object();
				const url = getCallUrl("/history/list");
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
				const url = getCallUrl("/aui/appendData");
				const params = new Object();
				const curPage = document.getElementById("curPage").value;
				params.sessionid = document.getElementById("sessionid").value;
				params.start = (curPage * 100);
				params.end = (curPage * 100) + 100;
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

			// jquery 삭제를 해가는 쪽으로 한다..
			document.addEventListener("DOMContentLoaded", function() {
				const columns = loadColumnLayout("history-list");
				// 컨텍스트 메뉴 시작
				let contenxtHeader = genColumnHtml(columns); // see auigrid.js
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				// DOM이 로드된 후 실행할 코드 작성
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
				
				// 범위 달력
				fromToCalendar("created", "calendar");
				// 범위 달력 값 삭제
				fromToDelete("delete");
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