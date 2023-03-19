<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<Map<String, String>> customers = (ArrayList<Map<String, String>>) request.getAttribute("customers");
ArrayList<Map<String, String>> maks = (ArrayList<Map<String, String>>) request.getAttribute("maks");
ArrayList<Map<String, String>> projectTypes = (ArrayList<Map<String, String>>) request.getAttribute("projectTypes");
ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String, String>>) request.getAttribute("list");
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
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=1"></script>
</head>
<body>
	<form>
		<!-- 리스트 검색시 반드시 필요한 히든 값 -->
		<input type="hidden" name="sessionid" id="sessionid">
		<input type="hidden" name="curPage" id="curPage">
		<!-- 검색 테이블 -->
		<table class="search-table">
			<colgroup>
				<col width="100">
				<col width="500">
				<col width="100">
				<col width="500">
				<col width="100">
				<col width="500">
				<col width="100">
				<col width="500">
			</colgroup>
			<tr>
				<th>KEK 작번</th>
				<td class="indent5">
					<input type="text" name="kekNumber" id="kekNumber">
				</td>
				<th>KE 작번</th>
				<td class="indent5">
					<input type="text" name="keNumber" id="keNumber">
				</td>
				<th>발행일</th>
				<td class="indent5">
					<!-- input 박스의 AXInput 클래스 모두 제거한다 -->
					<input type="text" name="pdateFrom" id="pdateFrom" class="width-100">
					~
					<input type="text" name="pdateTo" id="pdateTo" class="width-100">
				</td>
				<th>USER ID</th>
				<td class="indent5">
					<input type="text" name="userId" id="userId">
				</td>
			</tr>
			<tr>
				<th>작번 상태</th>
				<td class="indent5">
					<!-- 셀렉트 박스의 AXSelect 모두 제거한다 -->
					<select name="kekState" id="kekState" class="width-200">
						<option value="">선택</option>
					</select>
				</td>
				<th>모델</th>
				<td class="indent5">
					<input type="text" name="model" id="model">
				</td>
				<th>거래처</th>
				<td class="indent5">
					<select name="customer_name" id="customer_name" class="width-200">
						<option value="">선택</option>
						<%
						for (Map customer : customers) {
						%>
						<option value="<%=customer.get("key")%>"><%=customer.get("value")%></option>
						<%
						}
						%>
					</select>
				</td>
				<th>설치장소</th>
				<td class="indent5">
					<select name="install_name" id="install_name" class="width-200">
						<option value="">선택</option>
					</select>
				</td>
			</tr>
			<tr>
				<th>작번 유형</th>
				<td class="indent5">
					<select name="projectType" id="projectType" class="width-200">
						<option value="">선택</option>
						<%
						for (Map projectType : projectTypes) {
						%>
						<option value="<%=projectType.get("key")%>"><%=projectType.get("value")%></option>
						<%
						}
						%>
					</select>
				</td>
				<th>기계 담당자</th>
				<td class="indent5">
					<input type="text" name="machine" id="machine">
				</td>
				<th>전기 담당자</th>
				<td class="indent5">
					<input type="text" name="elec" id="elec">
				</td>
				<th>SW 담당자</th>
				<td class="indent5">
					<input type="text" name="soft" id="soft">
				</td>
			</tr>
			<tr>
				<th>막종</th>
				<td class="indent5">
					<input type="text" name="mak_name" id="mak_name">
				</td>
				<th>막종상세</th>
				<td class="indent5">
					<input type="text" name="detail_name" id="detail_name">
				</td>
				<th>템플릿</th>
				<td class="indent5">
					<select name="template" id="template" class="width-200">
						<option value="">선택</option>
					</select>
				</td>
				<th>작업 내용</th>
				<td colspan="3" class="indent5">
					<input type="text" name="description" id="description" class="width-200">
				</td>
			</tr>
		</table>

		<!-- 버튼 테이블 -->
		<table class="button-table">
			<tr>
				<td class="left">
					<input type="button" value="테이블 저장" title="테이블 저장" class="orange" onclick="saveColumnLayout('project-list');">
					<input type="button" value="테이블 초기화" title="테이블 초기화" onclick="resetColumnLayout('project-list');">
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
		<!-- 검색 테이블 행4개일 경우 르기드 사이즈 600 즐겨찾기 및 기타 적인 요소로 스크롤 감안하여 조금 작게 -->
		<div id="grid_wrap" style="height: 600px; border-top: 1px solid #3180c3;"></div>
		<!-- 컨텍스트 메뉴 사용시 반드시 넣을 부분 -->
		<%@include file="/extcore/jsp/common/aui/aui-context.jsp"%>
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
							const oid = item.oid;
							const url = getCallUrl("/project/info?oid=" + oid);
							popup(url);
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
							const oid = item.oid;
							const url = getCallUrl("/project/info?oid=" + oid);
							popup(url);
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
						inline : true,
						displayFormatValues : true
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
						inline : true,
						displayFormatValues : true
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
				};

				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				//화면 첫 진입시 리스트 호출 함수
				loadGridData();
				// 컨텍스트 메뉴 이벤트 바인딩
				AUIGrid.bind(myGridID, "contextMenu", auiContextMenuHandler);

				// 스크롤 체인지 핸들러.
				AUIGrid.bind(myGridID, "vScrollChange", function(event) {
					hideContextMenu(); // 컨텍스트 메뉴 감추기
					vScrollChangeHandler(event);
				});

				AUIGrid.bind(myGridID, "hScrollChange", function(event) {
					hideContextMenu(); // 컨텍스트 메뉴 감추기
				});
			}

			function loadGridData() {
				const params = new Object();
				const url = getCallUrl("/project/list");
				// 검색 변수
				const kekNumber = document.getElementById("kekNumber").value;
				const description = document.getElementById("description").value;
				// 검색 변수 담기
				params.kekNumber = kekNumber;
				params.description = description;

				// params.서버에서 받을 변수명 = 웹에서 받아오는 값
				params.projectType = document.getElementById("projectType").value;
				
				
				// 페이징 개수 처리
				params.psize = document.getElementById("psize").value;
				
				// 웹화면에서 잘 받아서 가는지 확인 하려면 콘솔창 이용 F12
				console.log(params);

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

			// jquery 삭제를 해가는 쪽으로 한다..
			document.addEventListener("DOMContentLoaded", function() {
				// DOM이 로드된 후 실행할 코드 작성
				const columns = loadColumnLayout("project-list");
				// 컨텍스트 메뉴 시작
				const contenxtHeader = genColumnHtml(columns); // see auigrid.js
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);

				// 셀렉트 박스
				selectbox("kekState");
				selectbox("customer_name");
				selectbox("install_name");
				selectbox("projectType");
				selectbox("template");
				selectbox("psize");

				// 사용자 검색 바인딩 see base.js finderUser function 
				finderUser("soft");
				finderUser("elec");
				finderUser("machine");

				// 날짜 검색용 바인딩 see base.js twindate funtion
				twindate("pdate");
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