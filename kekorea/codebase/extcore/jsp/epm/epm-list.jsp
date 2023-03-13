<%@page import="e3ps.org.service.OrgHelper"%>
<%@page import="wt.epm.EPMDocumentType"%>
<%@page import="e3ps.epm.service.EpmHelper"%>
<%@page import="e3ps.part.service.PartHelper"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="e3ps.common.util.ColumnUtils"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@page import="wt.fc.PagingQueryResult"%>
<%@page import="e3ps.common.util.PageQueryUtils"%>
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
				<th>부품 분류</th>
				<td colspan="7">
					<input type="hidden" name="location" value=""> <span id="location">defaultttttttt</span>
				</td>
				</tr>
				<tr>
				<th>파일 이름</th>
				<td>
					<input type="text" name="partCode" class="AXInput">
				</td>
				<th>품번</th>
				<td>
					<input type="text" name="partName" class="AXInput">
				</td>
				<th>품명</th>
				<td>
					<input type="text" name="number" class="AXInput">
				</td>
				<th>규격</th>
				<td>
					<input type="text" name="number" class="AXInput">
				</td>
				</tr>
				<tr>
				<th>MATERIAL</th>
				<td>
					<input type="text" name="number" class="AXInput">
				</td>
				<th>REMARK</th>
				<td>
					<input type="text" name="number" class="AXInput">
				</td>
				<th>REFERENCE 도면</th>
				<td colspan="3">
					<input type="text" name="number" class="AXInput">
				</td>
				</tr>
				<tr>
				<th>작성자</th>
				<td>
					<input type="text" name="number" class="AXInput">
				</td>
				<th>작성일</th>
				<td colspan="3">
					<input type="text" name="partName" class="AXInput width-100"> ~
					<input type="text" name="partName" class="AXInput width-100">
				</td>
				<th>상태</th>
				<td>
					<input type="text" name="number" class="AXInput">
				</td>
				</tr>
				<tr>
				<th>수정자</th>
				<td>
					<input type="text" name="number" class="AXInput">
				</td>
				<th>수정일</th>
				<td colspan="3">
					<input type="text" name="partName" class="AXInput width-100"> ~
					<input type="text" name="partName" class="AXInput width-100">
				</td>
				<th>버전</th>
				<td>
					<input type="text" name="number" class="AXInput">
				</td>
			</tr>
		</table>

		<!-- 버튼 테이블 -->
		<table class="button-table">
			<tr>
				<td class="left">
					<input type="button" value="테이블 저장" title="테이블 저장" class="orange" onclick="saveColumnLayout('epm-list');">
				</td>
				<td class="right">
					<input type="button" value="조회" title="조회" onclick="loadGridData();">
				</td>
			</tr>
		</table>
		<!-- 그리드 리스트 -->
		<div id="grid_wrap" style="height: 390px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			function _layout() {
				return [ {
					dataField : "thumnail",
					headerText : "",
					dataType : "string",
					width : 60,
					renderer : {
						type : "ImageRenderer",
						altField : null,
						onClick : function(event) {
						}
					},
					filter : {
						showIcon : false,
						inline : false
					},					
				}, {
					dataField : "name",
					headerText : "파일이름",
					dataType : "string",
					width : 350,
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
					dataField : "part_code",
					headerText : "품번",
					dataType : "string",
					width : 130,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "name_of_parts",
					headerText : "품명",
					dataType : "string",
					width : 350,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "dwg_no",
					headerText : "규격",
					dataType : "string",
					width : 130,
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
					dataField : "material",
					headerText : "MATERIAL",
					dataType : "string",
					width : 130,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "remark",
					headerText : "REMARK",
					dataType : "string",
					width : 150,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "reference",
					headerText : "REFERENCE 도면",
					dataType : "string",
					width : 150,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "version",
					headerText : "버전",
					dataType : "string",
					width : 80,
					filter : {
						showIcon : false,
						inline : false
					},
				}, {
					dataField : "modifier",
					headerText : "수정자",
					dataType : "string",
					width : 100,
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
				}, {
					dataField : "location",
					headerText : "FOLDER",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : false
						inline : false
					},
				} ]
			}

			function createAUIGrid(columns) {
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
				};
				myGridID = AUIGrid.create("#grid_wrap", columns, props);
				loadGridData();
				AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);
				AUIGrid.bind(myGridID, "cellClick", auiCellClickHandler);
			}

			function auiCellClickHandler(event) {
				const oid = event.item.oid;
				const dataField = event.dataField;
			}

			function loadGridData() {
				const params = new Object();
				const url = getCallUrl("/epm/list");
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
				params.sessionid = document.getElementById("sessionid").value;
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

			// jquery 삭제를 해가는 쪽으로 한다..
			document.addEventListener("DOMContentLoaded", function() {
				// DOM이 로드된 후 실행할 코드 작성
				const columns = loadColumnLayout("epm-list");
				createAUIGrid(columns);
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