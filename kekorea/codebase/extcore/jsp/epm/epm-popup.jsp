<%@page import="e3ps.epm.service.EpmHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String method = (String) request.getAttribute("method");
boolean multi = (boolean) request.getAttribute("multi");
%>
<%@include file="/extcore/include/auigrid.jsp"%>
<input type="hidden" name="sessionid" id="sessionid">
<input type="hidden" name="curPage" id="curPage">
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=1"></script>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				도면 조회
			</div>
		</td>
	</tr>
</table>

<table class="search-table">
	<colgroup>
		<col width="250">
		<col width="400">
		<col width="250">
		<col width="400">
	</colgroup>
	<tr>
		<th class="lb">부품분류</th>
		<td class="indent5" colspan="3"><%=EpmHelper.PRODUCT_ROOT%></td>
	</tr>
	<tr>
		<th class="lb">규격</th>
		<td class="indent5">
			<input type="text" name="number">
		</td>
		<th class="lb">품번</th>
		<td class="indent5">
			<input type="text" name="partNumber">
		</td>
	</tr>
	<tr>
		<th class="lb">품명</th>
		<td class="indent5">
			<input type="text" name="partName">
		</td>
		<th class="lb">REFERENCE 도면</th>
		<td class="indent5">
			<input type="text" name="partName">
		</td>
	</tr>
	<tr>
		<th class="lb">MATERIAL</th>
		<td class="indent5">
			<input type="text" name="partName">
		</td>
		<th class="lb">REMARK</th>
		<td class="indent5">
			<input type="text" name="partName">
		</td>
	</tr>
	<tr>
		<th class="lb">파일이름</th>
		<td class="indent5" colspan="3">
			<input type="text" name="partName">
		</td>
	</tr>
	<tr>
		<th class="lb">작성자</th>
		<td class="indent5">
			<input type="text" name="creator" id="creator">
		</td>
		<th class="lb">작성일</th>
		<td class="indent5">
			<input type="text" name="createdFrom" id="createdFrom" class="width-100">
			~
			<input type="text" name="createdTo" id="createdTo" class="width-100">
		</td>
	</tr>
	<tr>
		<th class="lb">수정자</th>
		<td class="indent5">
			<input type="text" name="modifier" id="modifier">
		</td>
		<th class="lb">수정일</th>
		<td class="indent5">
			<input type="text" name="modifiedFrom" id="modifiedFrom" class="width-100">
			~
			<input type="text" name="modifiedTo" id="modifiedTo" class="width-100">
		</td>
	</tr>
	<tr>
		<th class="lb">상태</th>
		<td class="indent5">
			<select name="state" id="state" class="width-200">
				<option value="">선택</option>
			</select>
		</td>
		<th class="lb">버전</th>
		<td colspan="5">
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="latest" value="true" checked="checked">
				<div class="state p-success">
					<label>
						<b>최신버전</b>
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
	</tr>
</table>

<table class="button-table">
	<tr>
		<td class="left">
			<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('notice-list');">
			<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('notice-list');">
		</td>
		<td class="right">
			<select name="psize" id="psize">
				<option value="30">30</option>
				<option value="50">50</option>
				<option value="100">100</option>
				<option value="200">200</option>
				<option value="300">300</option>
			</select>
			<input type="button" value="추가" title="추가" class="red" onclick="<%=method%>();">
			<input type="button" value="조회" title="조회" class="blue" onclick="loadGridData();">
			<input type="button" value="초기화" title="초기화" class="green">
			<input type="button" value="닫기" title="닫기" class="red" onclick="self.close();">
		</td>
	</tr>
</table>

<table>
	<colgroup>
		<col width="230">
		<col width="10">
		<col width="*">
	</colgroup>
	<tr>
		<td valign="top">
			<jsp:include page="/extcore/include/folder-include.jsp">
				<jsp:param value="<%=EpmHelper.PRODUCT_ROOT%>" name="location" />
				<jsp:param value="product" name="container" />
				<jsp:param value="list" name="mode" />
				<jsp:param value="700px" name="height" />
			</jsp:include>
		</td>
		<td>&nbsp;</td>
		<td>
			<div id="grid_wrap" style="height: 400px; border-top: 1px solid #3180c3;"></div>
			<%@include file="/extcore/jsp/common/aui/aui-context.jsp"%>
			<script type="text/javascript">
				let myGridID;
				function _layout() {
					return [ {
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
					} ]
				}

				function createAUIGrid(columnLayout) {
					const props = {
							headerHeight : 30,
							showRowNumColumn : true,
							showRowCheckColumn : true,
							rowNumHeaderText : "번호",
							showAutoNoDataMessage : false,
							enableFilter : true,
							selectionMode : "multipleCells",
							enableMovingColumn : true,
							showInlineFilter : true,
							useContextMenu : true,
							enableRowCheckShiftKey : true,
							enableRightDownFocus : true,
							filterLayerWidth : 320,
							filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
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
					AUIGrid.bind(myGridID, "cellClick", auiCellClickHandler);
				}

				function loadGridData() {
					const url = getCallUrl("/epm/list");
					const params = new Object();
					const psize = document.getElementById("psize").value;
					params.latest = true;
					params.psize = psize;
					AUIGrid.showAjaxLoader(myGridID);
					openLayer();
					call(url, params, function(data) {
						AUIGrid.removeAjaxLoader(myGridID);
						AUIGrid.setGridData(myGridID, data.list);
						document.getElementById("sessionid").value = data.sessionid;
						document.getElementById("curPage").value = data.curPage;
						closeLayer();
					});
				}
				
				function <%=method%>() {
					const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
					if (checkedItems.length == 0) {
						alert("추가할 문서를 선택하세요.");
						return false;
					}
					opener.<%=method%>(checkedItems);
					self.close();
				}
				
				function auiCellClickHandler(event) {
					const item = event.item;
					rowIdField = AUIGrid.getProp(event.pid, "rowIdField"); // rowIdField 얻기
					rowId = item[rowIdField];
					
					// 이미 체크 선택되었는지 검사
					if(AUIGrid.isCheckedRowById(event.pid, rowId)) {
						// 엑스트라 체크박스 체크해제 추가
						AUIGrid.addUncheckedRowsByIds(event.pid, rowId);
					} else {
						// 엑스트라 체크박스 체크 추가
						AUIGrid.addCheckedRowsByIds(event.pid, rowId);
					}
				}
				
				document.addEventListener("DOMContentLoaded", function() {
					const columns = loadColumnLayout("epm-popup");
					const contenxtHeader = genColumnHtml(columns);
					$("#h_item_ul").append(contenxtHeader);
					$("#headerMenu").menu({
						select : headerMenuSelectHandler
					});
					createAUIGrid(columns);
					_createAUIGrid(_columns); // 트리
					
					selectbox("state");
					selectbox("psize");
					finderUser("creator");
					finderUser("modifier");
					twindate("created");
					twindate("modified");
				});

				document.addEventListener("keydown", function(event) {
					const keyCode = event.keyCode || event.which;
					if (keyCode === 13) {
						loadGridData();
					}
				})

							document.addEventListener("click", function(event) {
				hideContextMenu();
			})

				window.addEventListener("resize", function() {
					AUIGrid.resize(myGridID);
					AUIGrid.resize(_myGridID);
				});
			</script>
		</td>
	</tr>
</table>