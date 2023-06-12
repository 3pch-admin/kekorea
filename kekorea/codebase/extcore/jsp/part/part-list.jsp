<%@page import="e3ps.part.service.PartHelper"%>
<%@page import="wt.org.WTUser"%>
<%@page import="e3ps.bom.partlist.service.PartlistHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=1010"></script>
</head>
<body>
	<form>
		<input type="hidden" name="isAdmin" id="isAdmin" value="<%=isAdmin%>">
		<input type="hidden" name="sessionName" id="sessionName" value="<%=sessionUser.getFullName()%>">
		<input type="hidden" name="sessionId" id="sessionId" value="<%=sessionUser.getName()%>">
		<input type="hidden" name="sessionid" id="sessionid">
		<input type="hidden" name="curPage" id="curPage">
		<input type="hidden" name="oid" id="oid">
		<input type="hidden" name="container" id="container" value="product">
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
				<td colspan="7" class="indent5">
					<input type="hidden" name="location" id="location" value="<%=PartHelper.DEFAULT_ROOT%>">
					<span id="locationText"><%=PartHelper.DEFAULT_ROOT%></span>
				</td>
			</tr>
			<tr>
				<th>파일 이름</th>
				<td class="indent5">
					<input type="text" name="name" id="name">
				</td>
				<th>품번</th>
				<td class="indent5">
					<input type="text" name="partCode" id="partCode">
				</td>
				<th>품명</th>
				<td class="indent5">
					<input type="text" name="partName" id="partName">
				</td>
				<th>규격</th>
				<td class="indent5">
					<input type="text" name="number" id="number">
				</td>
			</tr>
			<tr>
				<th>MATERIAL</th>
				<td class="indent5">
					<input type="text" name="material" id="material">
				</td>
				<th>REMARK</th>
				<td class="indent5">
					<input type="text" name="remarkSs" id="remarks">
				</td>
				<th>MAKER</th>
				<td colspan="3" class="indent5">
					<input type="text" name="maker" id="maker">
				</td>
			</tr>
			<tr>
				<th>작성자</th>
				<td class="indent5">
					<input type="text" name="creator" id="creator" data-multi="false">
					<input type="hidden" name="creatorOid" id="creatorOid">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('creator')">
				</td>
				<th>작성일</th>
				<td class="indent5">
					<input type="text" name="createdFrom" id="createdFrom" class="width-100">
					~
					<input type="text" name="createdTo" id="createdTo" class="width-100">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearFromTo('createdFrom', 'createdTo')">
				</td>
				<th>수정자</th>
				<td class="indent5">
					<input type="text" name="modifier" id="modifier" data-multi="false">
					<input type="hidden" name="modifierOId" id="modifierOid" data-multi="false">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('modifier')">
				</td>
				<th>수정일</th>
				<td class="indent5">
					<input type="text" name="modifiedFrom" id="modifiedFrom" class="width-100">
					~
					<input type="text" name="modifiedTo" id="modifiedTo" class="width-100">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearFromTo('modifiedFrom', 'modifiedTo')">
				</td>
			</tr>
			<tr>
				<th>상태</th>
				<td class="indent5">
					<select name="state" id="state" class="width-200">
						<option value="">선택</option>
						<option value="INWORK">작업 중</option>
						<option value="UNDERAPPROVAL">승인 중</option>
						<option value="APPROVED">승인됨</option>
						<option value="RETURN">반려됨</option>
					</select>
				</td>
				<th>버전</th>
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

		<!-- 버튼 테이블 -->
		<table class="button-table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();">
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('part-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('part-list');">
					<input type="button" value="라이브러리" title="라이브러리" class="blue" onclick="toggle('product');">
				</td>
				<td class="right">
					<select name="_psize" id="_psize">
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

		<table>
			<colgroup>
				<col width="230">
				<col width="10">
				<col width="*">
			</colgroup>
			<tr>
				<td valign="top">
					<jsp:include page="/extcore/jsp/common/folder-include.jsp">
						<jsp:param value="<%=PartHelper.DEFAULT_ROOT%>" name="location" />
						<jsp:param value="product" name="container" />
						<jsp:param value="list" name="mode" />
						<jsp:param value="670" name="height" />
					</jsp:include>
				</td>
				<td>&nbsp;</td>
				<td>
					<div id="grid_wrap" style="height: 670px; border-top: 1px solid #3180c3;"></div>
					<%@include file="/extcore/jsp/common/aui/aui-context.jsp"%>
				</td>
			</tr>
		</table>
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
					style : "aui-left",
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.oid;
							const url = getCallUrl("/part/view?oid=" + oid);
							popup(url, 1500, 500);
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
					width : 120,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "name_of_parts",
					headerText : "품명",
					dataType : "string",
					width : 300,
					style : "aui-left",
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "number",
					headerText : "규격",
					dataType : "string",
					width : 150,
					style : "aui-left",
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.oid;
							const url = getCallUrl("/part/view?oid=" + oid);
							popup(url, 1500, 500);
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
					width : 150,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "remarks",
					headerText : "REMARKS",
					dataType : "string",
					width : 150,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "maker",
					headerText : "MAKER",
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
						inline : true,
						displayFormatValues : true
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
						inline : true,
						displayFormatValues : true
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
					width : 150,
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
					rowNumHeaderText : "번호",
					showAutoNoDataMessage : false,
					enableFilter : true,
					selectionMode : "multipleCells",
					enableMovingColumn : true,
					showInlineFilter : true,
					useContextMenu : true,
					enableRightDownFocus : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
				};

				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				// 				loadGridData();
				AUIGrid.bind(myGridID, "contextMenu", auiContextMenuHandler);

				AUIGrid.bind(myGridID, "vScrollChange", function(event) {
					hideContextMenu();
					vScrollChangeHandler(event);
				});

				AUIGrid.bind(myGridID, "hScrollChange", function(event) {
					hideContextMenu();
				});
				AUIGrid.bind(myGridID, "cellClick", auiCellClickHandler);
			}

			function auiCellClickHandler(event) {
				const dataField = event.dataField;
				if (dataField === "thumnail_mini") {
					const url = event.item.creoViewURL;
					if (url !== "") {
						popup(url, 600, 200);
					} else {
						alert("뷰어 생성하기!");
					}
				}
			}

			function loadGridData() {
				let params = new Object();
				const url = getCallUrl("/part/list");
				const field = ["_psize","oid","name","partCode","partName","number","material","remarks","maker","creatorOid","createdFrom","createdTo","modifier","modifiedFrom","modifiedTo","state"];
				const psize = document.getElementById("_psize").value;
// 				const container = document.getElementById("_psize").value;
				const latest = !!document.querySelector("input[name=latest]:checked").value;
// 				params.container = container;
				params = toField(params, field);
				params.latest = latest;
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

			function toggle(container) {
				const iframe = parent.document.getElementById("content");
				iframe.src = getCallUrl("/part/library");
			}

			function exportExcel() {
				const exceptColumnFields = [ "thumnail" ];
				const sessionName = document.getElementById("sessionName").value;
				exportToExcel("부품 리스트", "부품", "부품 리스트", exceptColumnFields, sessionName);
			}

			document.addEventListener("DOMContentLoaded", function() {
				toFocus("name");
				const columns = loadColumnLayout("part-list");
				const contenxtHeader = genColumnHtml(columns);
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				_createAUIGrid(_columns);
				AUIGrid.resize(myGridID);
				AUIGrid.resize(_myGridID);
				selectbox("state");
				selectbox("_psize");
				selectbox("state");
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
	</form>
</body>
</html>