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
				<th>수배표 제목</th>
				<td>
					<input type="text" name="fileName" class="AXInput">
				</td>
				<th>상태</th>
				<td>
					<select name="size" id="size" class="AXSelect w200">
						<option value="">선택</option>
					</select>
				</td>
				<th>KEK 작번</th>
				<td>
					<input type="text" name="partName" class="AXInput">
				</td>
				<th>KE 작번</th>
				<td>
					<input type="text" name="number" class="AXInput">
				</td>
				</tr>
				<tr>
				<th>설명</th>
				<td>
					<input type="text" name="number" class="AXInput">
				</td>
				<th>설계 구분</th>
				<td>
					<select name="size" id="size" class="AXSelect w200">
						<option value="">선택</option>
					</select>
				</td>
				<th>막종</th>
				<td>
					<input type="text" name="number" class="AXInput">
				</td>
				<th>작업 내용</th>
				<td>
					<input type="text" name="number" class="AXInput">
				</td>
				</tr>
				<tr>
				<th>작성자</th>
				<td>
					<input type="text" name="number" class="AXInput">
				</td>
				<th>작성일</th>
				<td colspan="5">
					<input type="text" name="partNamea" class="AXInput width-100"> ~
					<input type="text" name="partNamea" class="AXInput width-100">
				</td>
				</tr>
				<tr>
				<th>수정자</th>
				<td>
					<input type="text" name="number" class="AXInput">
				</td>
				<th>수정일</th>
				<td colspan="5">
					<input type="text" name="partNamea" class="AXInput width-100"> ~
					<input type="text" name="partNamea" class="AXInput width-100">
				</td>
			</tr>
		</table>

		<!-- 버튼 테이블 -->
		<table class="button-table">
			<tr>
				<td class="left">
					<input type="button" value="테이블 저장" title="테이블 저장" class="orange" onclick="saveColumnLayout('partlist-list');">
					<input type="button" value="비교" title="비교" class="red" onclick="compare();">
					<input type="button" value="등록" title="등록" class="blue" onclick="create();">
				</td>
				<td class="right">
					<input type="button" value="조회" title="조회" onclick="loadGridData();">
				</td>
			</tr>
		</table>

		<!-- 그리드 리스트 -->
		<div id="grid_wrap" style="height: 430px; border-top: 1px solid #3180c3;"></div>
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
					style : "cursor",
					renderer : {
						type : "IconRenderer",
						iconWidth : 16, // icon 사이즈, 지정하지 않으면 rowHeight에 맞게 기본값 적용됨
						iconHeight : 16,
						iconTableRef : { // icon 값 참조할 테이블 레퍼런스
							"default" : "/Windchill/extcore/images/details.gif" // default
						},
						onClick : function(event) {
							const oid = event.item.loid;
							const url = getCallUrl("/partlist/info?oid=" + oid);
							popup(url);
						}
					},
					filter : {
						showIcon : false,
						inline : false
					},
				}, {
					dataField : "name",
					headerText : "수배표제목",
					dataType : "string",
					width : 300,
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript", 
						jsCallback : function(rowIndex, columnIndex, value, item) {
							alert("( " + rowIndex + ", " + columnIndex + " ) " + item.color + "  Link 클릭\r\n자바스크립트 함수 호출하고자 하는 경우로 사용하세요!");
						}
					},
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
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript", 
						jsCallback : function(rowIndex, columnIndex, value, item) {
							alert("( " + rowIndex + ", " + columnIndex + " ) " + item.color + "  Link 클릭\r\n자바스크립트 함수 호출하고자 하는 경우로 사용하세요!");
						}
					},
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "keNumber",
					headerText : "KE 작번",
					dataType : "string",
					style : "underline",
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
				};
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				loadGridData();
				// LazyLoading 바인딩
				AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);
				AUIGrid.bind(myGridID, "cellClick", auiCellClickHandler);
			}

			function compare() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				if (checkedItems.length <= 0) {
					alert("비교할 수배표를 선택하세요.");
					return;
				}
				if (checkedItems.length > 1) {
					alert("비교할 수배표를 하나만 선택하세요.");
					return;
				}
				const loid = checkedItems[0].item.loid;
				const oid = checkedItems[0].item.oid;
				const url = getCallUrl("/partlist/compare?loid=" + loid + "&oid=" + oid);
				popup(url);
			}

			function auiCellClickHandler(event) {
				const dataField = event.dataField;
				const item = event.item;
				if (dataField === "name") {
					const url = getCallUrl("/partlist/view?oid=" + item.oid);
					popup(url);
				}
			}

			function loadGridData() {
				const params = new Object();
				const url = getCallUrl("/partlist/list");
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
				const curPage = document.getElementById("curPage").value
				const sessionid = document.getElementById("sessionid").value
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

			function create() {
				const url = getCallUrl("/partlist/create");
				popup(url);
			}

			// jquery 삭제를 해가는 쪽으로 한다..
			document.addEventListener("DOMContentLoaded", function() {
				// DOM이 로드된 후 실행할 코드 작성
				const columns = loadColumnLayout("partlist-list");
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