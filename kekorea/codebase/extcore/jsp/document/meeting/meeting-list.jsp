<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="wt.org.WTUser"%>
<%@page import="org.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<Map<String, String>> customers = (ArrayList<Map<String, String>>) request.getAttribute("customers");
ArrayList<Map<String, String>> maks = (ArrayList<Map<String, String>>) request.getAttribute("maks");
ArrayList<Map<String, String>> projectTypes = (ArrayList<Map<String, String>>) request.getAttribute("projectTypes");
ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String, String>>) request.getAttribute("list");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
String before = (String) request.getAttribute("before");
String end = (String) request.getAttribute("end");
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
				<th>회의록 제목</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-200">
				</td>
				<th>KEK 작번</th>
				<td class="indent5">
					<input type="text" name="kekNumber" id="kekNumber" class="width-200">
				</td>
				<th>KE 작번</th>
				<td class="indent5">
					<input type="text" name="keNumber" id="keNumber" class="width-200">
				</td>
				<th>발행일</th>
				<td class="indent5">
					<input type="text" name="pdateFrom" id="pdateFrom" class="width-100">
					~
					<input type="text" name="pdateTo" id="pdateTo" class="width-100">
				</td>
			</tr>
			<tr>
				<th>작번 상태</th>
				<td class="indent5">
					<select name="kekState" id="kekState" class="width-200">
						<option value="">선택</option>
						<option value="준비">준비</option>
						<option value="설계중">설계중</option>
						<option value="설계완료">설계완료</option>
						<option value="작업완료">작업완료</option>
						<option value="중단됨">중단됨</option>
						<option value="취소">취소</option>
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
					<input type="hidden" name="machineOid" id="machineOid">
				</td>
				<th>전기 담당자</th>
				<td class="indent5">
					<input type="text" name="elec" id="elec">
					<input type="hidden" name="elecOid" id="elecOid">
				</td>
				<th>SW 담당자</th>
				<td class="indent5">
					<input type="text" name="soft" id="soft">
					<input type="hidden" name="softOid" id="softOid">
				</td>
			</tr>
			<tr>
				<th>막종</th>
				<td class="indent5">
					<select name="mak_name" id="mak_name" class="width-200">
						<option value="">선택</option>
						<%
						for (Map<String, String> map : maks) {
							String oid = map.get("key");
							String name = map.get("value");
						%>
						<option value="<%=oid%>"><%=name%></option>
						<%
						}
						%>
					</select>
				</td>
				<th>막종상세</th>
				<td class="indent5">
					<select name="detail_name" id="detail_name" class="width-200">
						<option value="">선택</option>
					</select>
				</td>
				<th>USER ID</th>
				<td class="indent5">
					<input type="text" name="userId" id="userId">
				</td>
				<th>작업 내용</th>
				<td colspan="3" class="indent5">
					<input type="text" name="description" id="description" class="width-200">
				</td>
			</tr>
		</table>


		<table class="button-table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();">
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('meeting-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('meeting-list');">
					<input type="button" value="확장" title="확장" class="red" onclick="expand();">
					<input type="button" value="등록" title="등록" class="blue" onclick="create();">
					<%
					if (isAdmin) {
					%>
					<input type="button" value="저장" title="저장" onclick="save();">
					<input type="button" value="행 삭제" title="행 삭제" class="red" onclick="deleteRow();">
					<%
					}
					%>
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

		<div id="grid_wrap" style="height: 630px; border-top: 1px solid #3180c3;"></div>
		<%@include file="/extcore/jsp/common/aui/aui-context.jsp"%>
		<script type="text/javascript">
			let myGridID;
			function _layout() {
				return [ {
					// 				const columns = [ {
					dataField : "name",
					headerText : "회의록 제목",
					dataType : "string",
					width : 350,
					style : "underline",
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.oid;
							const url = getCallUrl("/meeting/view?oid=" + oid);
							popup(url);
						}
					},
					filter : {
						showIcon : true,
						inline : true
					},
					cellMerge : true,
				}, {
					dataField : "projectType_name",
					headerText : "작번유형",
					dataType : "string",
					width : 80,
					filter : {
						showIcon : true,
						inline : true
					},
					editable : false
				}, {
					dataField : "customer_name",
					headerText : "거래처",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
					editable : false
				}, {
					dataField : "install_name",
					headerText : "설치장소",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
					editable : false
				}, {
					dataField : "mak_name",
					headerText : "막종",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
					editable : false
				}, {
					dataField : "detail_name",
					headerText : "막종상세",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
					editable : false
				}, {
					dataField : "kekNumber",
					headerText : "KEK 작번",
					dataType : "string",
					width : 100,
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
					editable : false
				}, {
					dataField : "keNumber",
					headerText : "KE 작번",
					dataType : "string",
					width : 100,
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
					editable : false
				}, {
					dataField : "userId",
					headerText : "USER ID",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
					editable : false
				}, {
					dataField : "description",
					headerText : "작업 내용",
					dataType : "string",
					width : 450,
					style : "aui-left",
					filter : {
						showIcon : true,
						inline : true
					},
					editable : false
				}, {
					dataField : "model",
					headerText : "모델",
					dataType : "string",
					width : 130,
					filter : {
						showIcon : true,
						inline : true
					},
					editable : false
				}, {
					dataField : "pdate_txt",
					headerText : "발행일",
					dataType : "date",
					formatString : "yyyy-mm-dd",
					width : 100,
					filter : {
						showIcon : true,
						inline : true,
						displayFormatValues : true
					},
					editable : false
				}, {
					dataField : "state",
					headerText : "상태",
					dataType : "string",
					width : 80,
					filter : {
						showIcon : true,
						inline : true
					},
					cellMerge : true,
					mergeRef : "name",
					mergePolicy : "restrict"
				}, {
					dataField : "creator",
					headerText : "작성자",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
					cellMerge : true,
					mergeRef : "name",
					mergePolicy : "restrict"
				}, {
					dataField : "createdDate_txt",
					headerText : "작성일",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true,
					},
					cellMerge : true,
					mergeRef : "name",
					mergePolicy : "restrict"
				} ]
			}

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : true,
					showRowCheckColumn : true,
					showStateColumn : true,
					rowNumHeaderText : "번호",
					showAutoNoDataMessage : false,
					enableFilter : true,
					selectionMode : "singleRow",
					enableMovingColumn : true,
					showInlineFilter : true,
					useContextMenu : true,
					enableRightDownFocus : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
					fixedColumnCount : 1,
					enableCellMerge : true,
					forceTreeView : true
				};
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				loadGridData();
				AUIGrid.bind(myGridID, "contextMenu", auiContextMenuHandler);
				AUIGrid.bind(myGridID, "vScrollChange", function(event) {
					hideContextMenu();
					vScrollChangeHandler(event);
				});
				AUIGrid.bind(myGridID, "hScrollChange", function(event) {
					hideContextMenu();
				});
				AUIGrid.bind(myGridID, "cellDoubleClick", auiCellDoubleClick);
			}

			function auiCellDoubleClick(event) {
				const dataField = event.dataField;
				const item = event.item;
				if (dataField === "name") {
					const url = getCallUrl("/meeting/view?oid=" + item.loid);
					popup(url);
				}
			}

			function loadGridData() {
				const params = new Object();
				const url = getCallUrl("/meeting/list");
				const kekNumber = document.getElementById("kekNumber").value;
				const keNumber = document.getElementById("keNumber").value;
				const pdateFrom = document.getElementById("pdateFrom").value;
				const pdateTo = document.getElementById("pdateTo").value;
				const userId = document.getElementById("userId").value;
				const kekState = document.getElementById("kekState").value;
				const model = document.getElementById("model").value;
				const customer_name = document.getElementById("customer_name").value;
				const install_name = document.getElementById("install_name").value;
				const projectType = document.getElementById("projectType").value;
				const machineOid = document.getElementById("machineOid").value;
				const elecOid = document.getElementById("elecOid").value;
				const softOid = document.getElementById("softOid").value;
				const mak_name = document.getElementById("mak_name").value;
				const detail_name = document.getElementById("detail_name").value;
				const description = document.getElementById("description").value;
				const psize = document.getElementById("psize").value;
				const name = document.getElementById("name").value;
				params.name = name;
				params.kekNumber = kekNumber;
				params.keNumber = keNumber;
				params.pdateFrom = pdateFrom;
				params.pdateTo = pdateTo;
				params.userId = userId;
				params.kekState = kekState;
				params.model = model;
				params.customer_name = customer_name;
				params.install_name = install_name;
				params.projectType = projectType;
				params.machineOid = machineOid;
				params.elecOid = elecOid;
				params.softOid = softOid;
				params.mak_name = mak_name;
				params.detail_name = detail_name;
				params.description = description;
				params.psize = psize;
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

			function create() {
				const url = getCallUrl("/meeting/create");
				popup(url);
			}

			function save() {
				const url = getCallUrl("/meeting/delete");
				const params = new Object();
				const removeRows = AUIGrid.getRemovedItems(myGridID);
				if (removeRows.length === 0) {
					alert("변경된 내용이 없습니다.");
					return false;
				}

				params.removeRows = removeRows;

				if (!confirm("저장 하시겠습니까?")) {
					return false;
				}
				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					parent.closeLayer();
					if (data.result) {
						loadGridData();
					}
				});
			}

			function deleteRow() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				for (let i = checkedItems.length - 1; i >= 0; i--) {
					const item = checkedItems[i].item;
					const rowIndex = checkedItems[i].rowIndex;
					if (!checker(sessionId, item.creatorId)) {
						alert(rowIndex + "행 데이터의 작성자 혹은 수정자가 아닙니다.");
						return false;
					}
					AUIGrid.removeRow(myGridID, rowIndex);
				}
			}

			function exportExcel() {
				const exceptColumnFields = [];
				const sessionName = document.getElementById("sessionName").value;
				exportToExcel("회의록 리스트", "회의록", "회의록 리스트", exceptColumnFields, sessionName);
			}

			document.addEventListener("DOMContentLoaded", function() {
				const columns = loadColumnLayout("meeting-list");
				const contenxtHeader = genColumnHtml(columns);
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
				twindate("pdate");
				selectbox("kekState");
				$("#customer_name").bindSelect({
					onchange : function() {
						const oid = this.optionValue;
						$("#install_name").bindSelect({
							ajaxUrl : getCallUrl("/commonCode/getChildrens?parentOid=" + oid),
							reserveKeys : {
								options : "list",
								optionValue : "value",
								optionText : "name"
							},
							setValue : this.optionValue,
							alwaysOnChange : true,
						})
					}
				})
				selectbox("install_name");
				selectbox("projectType");
				finderUser("machine");
				finderUser("elec");
				finderUser("soft");
				$("#mak_name").bindSelect({
					onchange : function() {
						const oid = this.optionValue;
						$("#detail_name").bindSelect({
							ajaxUrl : getCallUrl("/commonCode/getChildrens?parentOid=" + oid),
							reserveKeys : {
								options : "list",
								optionValue : "value",
								optionText : "name"
							},
							setValue : this.optionValue,
							alwaysOnChange : true,
						})
					}
				})
				selectbox("detail_name");
				selectbox("psize");
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
			});
		</script>
	</form>
</body>
</html>