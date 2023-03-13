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
				<th>KEK 작번</th>
				<td>
					<input type="text" name="kekNumber"  id="kekNumber" class="AXInput">
				</td>
				<th>발행일</th>
				<td colspan="3">
					<input type="text" name="partName" class="AXInput width-100"> ~
					<input type="text" name="partName" class="AXInput width-100">
				</td>
				<th>KE 작번</th>
				<td>
					<input type="text" name="partName" class="AXInput">
				</td>
				</tr>
				<tr>
				<th>USER ID</th>
				<td>
					<input type="text" name="number" class="AXInput">
				</td>
				<th>작번 상태</th>
				<td>
					<select name="size" id="size" class="AXSelect w200">
						<option value="">선택</option>
					</select>
				</td>
				<th>모델</th>
				<td>
					<input type="text" name="model" id="model" class="AXInput">
				</td>
				<th>거래처</th>
				<td>
					<select name="size" id="size" class="AXSelect w200">
						<option value="">선택</option>
					</select>
				</td>
				</tr>
				<tr>
				<th>설치 장소</th>
				<td>
					<select name="size" id="size" class="AXSelect w200">
						<option value="">선택</option>
					</select>
				</td>
				<th>작번 유형</th>
				<td>
					<select name="size" id="size" class="AXSelect w200">
						<option value="">선택</option>
					</select>
				</td>
				<th>기계 담당자</th>
				<td>
					<input type="text" name="" id="" class="AXInput">
				</td>
				<th>전기 담당자</th>
				<td>
					<input type="text" name="" class="AXInput">
				</td>
				</tr>
				<tr>
				<th>SW 담당자</th>
				<td>
					<input type="text" name="" class="AXInput">
				</td>
				<th>막종</th>
				<td>
					<input type="text" name=""  id="" class="AXInput">
				</td>
				<th>작업 내용</th>
				<td colspan="3">
					<input type="text" name="description" id="description" class="AXInput width-300">
				</td>
			</tr>
		</table>

		<!-- 버튼 테이블 -->
		<table class="button-table">
			<tr>
				<td class="left">
					<input type="button" value="테이블 저장" title="테이블 저장" class="orange" onclick="saveColumnLayout('project-list');">
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
					dataField : "state",
					headerText : "진행상태",
					dataType : "string",
					width : 80,
					renderer : {
						type : "TemplateRenderer",
					},
					filter : {
						showIcon : false,
						inline : false
					},
				}, {
					dataField : "cip",
					headerText : "CIP",
					dataType : "string",
					width : 60,
					renderer : {
						type : "IconRenderer",
						iconWidth : 16, // icon 사이즈, 지정하지 않으면 rowHeight에 맞게 기본값 적용됨
						iconHeight : 16,
						iconTableRef : { // icon 값 참조할 테이블 레퍼런스
							"default" : "/Windchill/extcore/images/icon/search.gif" // default
						},
						onClick : function(event) {
							const item = event.item;
							const mak_oid = item.mak_oid;
							const detail_oid = item.detail_oid;
							const customer_oid = item.customer_oid;
							const install_oid = item.install_oid;
							const url = getCallUrl("/cip/view?mak_oid=" + mak_oid + "&detail_oid=" + detail_oid + "&customer_oid=" + customer_oid + "&install_oid=" + install_oid);
							popup(url);
						}
					},
					filter : {
						showIcon : false,
						inline : false
					},
				}, {
					dataField : "history",
					headerText : "이력관리",
					dataType : "string", // 날짜 및 사람명 컬럼 사이즈 100
					width : 60,
					renderer : {
						type : "IconRenderer",
						iconWidth : 16, // icon 사이즈, 지정하지 않으면 rowHeight에 맞게 기본값 적용됨
						iconHeight : 16,
						iconTableRef : { // icon 값 참조할 테이블 레퍼런스
							"default" : "/Windchill/extcore/images/icon/search.gif" // default
						},
						onClick : function(event) {
							const item = event.item;
							const oid = item.oid;
							const url = getCallUrl("/history/view?oid=" + oid);
							popup(url);
						}
					},
					filter : {
						showIcon : false,
						inline : false
					},
				}, {
					dataField : "projectType_name",
					headerText : "작번유형",
					dataType : "string",
					width : 80,
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
					headerText : "설치장소",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
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
					width : 130,
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript", // 자바스크립 함수 호출로 사용하고자 하는 경우에 baseUrl 에 "javascript" 로 설정
						// baseUrl 에 javascript 로 설정한 경우, 링크 클릭 시 callback 호출됨.
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
					headerText : "작업 내용",
					dataType : "string",
					width : 450,
					style : "left indent10",
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
					dataField : "completeDate",
					headerText : "설계 완료일",
					dataType : "date",
					formatString : "yyyy-mm-dd",
					width : 100,
				}, {
					dataField : "customDate",
					headerText : "요구 납기일",
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
					width : 130,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "machine",
					headerText : "기계 담당자",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "elec",
					headerText : "전기 담당자",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "soft",
					headerText : "SW 담당자",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "kekProgress",
					headerText : "진행율",
					postfix : "%",
					width : 80,
					renderer : {
						type : "BarRenderer",
						min : 0,
						max : 100
					},
					filter : {
						showIcon : false,
						inline : false
					},
				}, {
					dataField : "kekState",
					headerText : "작번상태",
					dataType : "string",
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
				};

				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				//화면 첫 진입시 리스트 호출 함수
				loadGridData();
				// Lazy Loading 이벤트 바인딩
				AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);
			}

			function loadGridData() {
				const params = new Object();
				const url = getCallUrl("/project/list");
				
				const kekNumber = document.getElementById("kekNumber").value;
				const description = document.getElementById("description").value;
				params.kekNumber = kekNumber;
				params.description = description;
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

			// jquery 삭제를 해가는 쪽으로 한다..
			document.addEventListener("DOMContentLoaded", function() {
				// DOM이 로드된 후 실행할 코드 작성
				const columns = loadColumnLayout("project-list");
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