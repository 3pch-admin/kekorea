<%@page import="wt.org.WTUser"%>
<%@page import="org.json.JSONArray"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.org.service.OrgHelper"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>) request.getAttribute("list");
JSONArray maks = (JSONArray) request.getAttribute("maks");
JSONArray installs = (JSONArray) request.getAttribute("installs");
JSONArray departments = new JSONArray(list);
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
		<!-- 부서 OID -->
		<input type="hidden" name="oid" id="oid">
		<!-- 검색 테이블 -->
		<table class="search-table">
			<colgroup>
				<col width="130">
				<col width="600">
				<col width="130">
				<col width="600">
				<col width="130">
				<col width="600">
			</colgroup>
			<tr>
				<th>이름</th>
				<td class="indent5">
					<input type="text" name="userName" id="userName" class="AXInput">
				</td>
				<th>아이디</th>
				<td class="indent5">
					<input type="text" name="userId" id="userId" class="AXInput">
				</td>
				<th>퇴사여부</th>
				<td>
					&nbsp;
					<div class="pretty p-switch">
						<input type="checkbox" name="resign" value="true">
						<div class="state p-success">
							<label>&nbsp;</label>
						</div>
					</div>
				</td>
			</tr>
		</table>

		<!-- 버튼 테이블 -->
		<table class="button-table">
			<tr>
				<td class="left">
					<input type="button" value="테이블 저장" title="테이블 저장" class="orange" onclick="saveColumnLayout('organization-list');">
					<input type="button" value="테이블 초기화" title="테이블 초기화" onclick="resetColumnLayout('organization-list');">
					<input type="button" value="저장" title="저장" onclick="save();">
				</td>
				<td class="right">
					<input type="button" value="조회" title="조회" onclick="loadGridData();">
				</td>
			</tr>
		</table>

		<!-- 리스트 테이블 -->
		<table>
			<colgroup>
				<col width="230">
				<col width="10">
				<col width="*">
			</colgroup>
			<tr>
				<td>
					<jsp:include page="/extcore/include/department-include.jsp">
						<jsp:param value="list" name="mode" />
						<jsp:param value="705" name="height" />
					</jsp:include>
				</td>
				<td>&nbsp;</td>
				<td>
					<!-- 그리드 리스트 -->
					<div id="grid_wrap" style="height: 705px; border-top: 1px solid #3180c3;"></div>
				</td>
			</tr>
		</table>
		<!-- 컨텍스트 메뉴 사용시 반드시 넣을 부분 -->
		<%@include file="/extcore/jsp/common/aui/aui-context.jsp"%>
		<script type="text/javascript">
			let myGridID;
			const maks =
		<%=maks%>
			const installs =
		<%=installs%>
			const departments =
		<%=departments%>
			const dutys = [ "사장", "부사장", "PL", "TL" ];
			function _layout() {
				return [ {
					dataField : "name",
					headerText : "이름",
					dataType : "string",
					width : 100,
					editable : false,
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
					dataField : "id",
					headerText : "아이디",
					dataType : "string",
					width : 100,
					editable : false,
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
					dataField : "duty",
					headerText : "직급",
					dataType : "string", // 날짜 및 사람명 컬럼 사이즈 100
					width : 130,
					filter : {
						showIcon : true,
						inline : true
					},
					editable : true,
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
						list : dutys,
					},
				}, {
					dataField : "department_oid",
					headerText : "부서",
					dataType : "string",
					width : 150,
					editable : true,
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
						list : departments,
						keyField : "oid", // key 에 해당되는 필드명
						valueField : "name", // value 에 해당되는 필드명,			
					},
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
						let retStr = ""; // key 값에 맞는 value 를 찾아 반환함.
						for (let i = 0, len = departments.length; i < len; i++) {
							if (departments[i]["oid"] == value) {
								retStr = departments[i]["name"];
								break;
							}
						}
						return retStr == "" ? value : retStr;
					},
				}, {
					dataField : "mak",
					headerText : "막종",
					dataType : "string",
					style : "left indent10",
					editable : true,
					headerTooltip : {
						show : true,
						tooltipHtml : "한국 생산의 차트에서 사용자가 원하는 막종만 볼 수 있도록 설정 하는 컬럼입니다."
					},
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
						multipleMode : true, // 다중 선택 모드(기본값 : false)
						showCheckAll : true, // 다중 선택 모드에서 전체 체크 선택/해제 표시(기본값:false);
						list : maks,
						keyField : "key", // key 에 해당되는 필드명
						valueField : "value", // value 에 해당되는 필드명,			
					},
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
						let retStr = ""; // key 값에 맞는 value 를 찾아 반환함.
						for (let i = 0, len = maks.length; i < len; i++) {
							if (maks[i]["key"] == value) {
								retStr = maks[i]["value"];
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
					dataField : "install",
					headerText : "설치장소",
					dataType : "string",
					style : "left indent10",
					editable : true,
					headerTooltip : {
						show : true,
						tooltipHtml : "한국 생산의 차트에서 사용자가 원하는 설치장소만 볼 수 있도록 설정 하는 컬럼입니다."
					},
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
						multipleMode : true, // 다중 선택 모드(기본값 : false)
						showCheckAll : true, // 다중 선택 모드에서 전체 체크 선택/해제 표시(기본값:false);
						list : installs,
						keyField : "key", // key 에 해당되는 필드명
						valueField : "value", // value 에 해당되는 필드명,			
					},
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
						let retStr = ""; // key 값에 맞는 value 를 찾아 반환함.
						for (let i = 0, len = installs.length; i < len; i++) {
							if (installs[i]["key"] == value) {
								retStr = installs[i]["value"];
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
					dataField : "email",
					headerText : "이메일",
					dataType : "string",
					width : 250,
					editable : true,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "resign",
					headerText : "퇴사여부",
					dataType : "boolean",
					width : 80,
					renderer : {
						type : "CheckBoxEditRenderer",
						editable : true
					},
					filter : {
						showIcon : false,
						inline : false
					},
				}, {
					dataField : "createdDate",
					headerText : "등록일",
					dataType : "date",
					formatString : "yyyy-mm-dd",
					width : 100,
					filter : {
						showIcon : true,
						inline : true,
						displayFormatValues : true
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
				
				// 동적 수정여부 체크
				AUIGrid.bind(myGridID, "cellEditBegin", auiCellEditBegin );
				
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
			
			function auiCellEditBegin(event) {
				const item = event.item;
				if("<%=sessionUser.getName() %>" !== item.id) {
					return false;
				}
				return true;
			}

			function loadGridData() {
				const params = new Object();
				const url = getCallUrl("/org/list");
				const userName = document.getElementById("userName").value;
				const userId = document.getElementById("userId").value;
				const oid = document.getElementById("oid").value;
				params.oid = oid;
				params.userName = userName;
				params.userId = userId;
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

			function save() {
				if (!confirm("저장 하시겠습니까?")) {
					return false;
				}

				const url = getCallUrl("/org/save");
				const params = new Object();
				const editRows = AUIGrid.getEditedRowItems(myGridID);
				params.editRows = editRows;
				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					// 레이더 닫고
					parent.closeLayer();
					if (data.result) {
						// 다시 안에서 레이어 오픈..
						loadGridData();
					} else {
						// 실패 처리..
					}
				})
			}

			// jquery 삭제를 해가는 쪽으로 한다..
			document.addEventListener("DOMContentLoaded", function() {
				// DOM이 로드된 후 실행할 코드 작성
				const columns = loadColumnLayout("organization-list");
				// 컨텍스트 메뉴 시작
				let contenxtHeader = genColumnHtml(columns); // see auigrid.js
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns); // 리스트
				AUIGrid.resize(myGridID); // 리스트
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
				AUIGrid.resize(myGridID); // 리스트
			});
		</script>
	</form>
</body>
</html>