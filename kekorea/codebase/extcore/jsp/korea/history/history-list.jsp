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
					<i class="axi axi-close2"></i>
				</td>
			</tr>
		</table>

		<!-- 버튼 테이블 -->
		<table class="button-table">
			<tr>
				<td class="left">
					<input type="button" value="저장" title="저장" onclick="save();">
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
			const columns = [ {
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
				editRenderer : {
					type : "RemoteListRenderer",
					fieldName : "value",
					noDataMessage : "검색결과가 없습니다.",
					showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
					remoter : function(request, response) { // remoter 지정 필수
						if (String(request.term).length < 2) {
							alert("2글자 이상 입력하십시오.");
							response(false); // 데이터 요청이 없는 경우 반드시 false 삽입하십시오.
							return;
						}
						// 데이터 요청
						const url = getCallUrl("/history/remoter");
						const params = new Object();
						params.term = request.term;
						params.target = "project";
						call(url, params, function(data) {
							response(data.list);
						});
					}
				},
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
				filter : {
					showIcon : true,
					inline : true
				},				
			},
			<%for (Map<String, String> header : headers) {
	String key = header.get("key"); // spec code ....key
	String value = header.get("value");
	JSONArray array = JSONArray.fromObject(list.get(key));%>
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
			]

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
					editable : true
				};
				myGridID = AUIGrid.create("#grid_wrap", columns, props);
				// 그리드 데이터 로딩
				loadGridData();
				// LazyLoading 바인딩
				AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);
				// 셀 편집 종료 이벤트
				AUIGrid.bind(myGridID, "cellEditEnd", editEndHandler);
				AUIGrid.bind(myGridID, "addRowFinish", auiAddRowHandler);
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
				const addRows = AUIGrid.getAddedRowItems(myGridID);
				const removeRows = AUIGrid.getRemovedItems(myGridID);
				const editRows = AUIGrid.getEditedRowItems(myGridID);
				const params = new Object();
				params.addRows = addRows;
				params.removeRows = removeRows;
				params.editRows = editRows;
				call(url, params, function(data) {
					alert(data.msg);
					if(data.result) {
						loadGridData();
					} else {
						// 실패..
						
					}
				})
			}
			
			// 행 추가
			function addRow() {
				const item = new Object();
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
				// DOM이 로드된 후 실행할 코드 작성
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