<%@page import="java.util.HashMap"%>
<%@page import="wt.org.WTUser"%>
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
ArrayList<Map<String, String>> customers = (ArrayList<Map<String, String>>) request.getAttribute("customers");
ArrayList<Map<String, String>> maks = (ArrayList<Map<String, String>>) request.getAttribute("maks");
ArrayList<Map<String, String>> projectTypes = (ArrayList<Map<String, String>>) request.getAttribute("projectTypes");
ArrayList<HashMap<String, String>> templates = (ArrayList<HashMap<String, String>>) request.getAttribute("templates");
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
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=1"></script>
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
					<input type="text" name="pdateFrom" id="pdateFrom" class="width-100" value="<%=before%>">
					~
					<input type="text" name="pdateTo" id="pdateTo" class="width-100" value="<%=end%>">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearFromTo('pdateFrom', 'pdateTo')">
				</td>
				<th>USER ID</th>
				<td class="indent5">
					<input type="text" name="userId" id="userId">
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
					<input type="text" name="machine" id="machine" data-multi="false">
					<input type="hidden" name="machineOid" id="machineOid">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('machine')">
				</td>
				<th>전기 담당자</th>
				<td class="indent5">
					<input type="text" name="elec" id="elec" data-multi="false">
					<input type="hidden" name="elecOid" id="elecOid">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('elec')">
				</td>
				<th>SW 담당자</th>
				<td class="indent5">
					<input type="text" name="soft" id="soft" data-multi="false">
					<input type="hidden" name="softOid" id="softOid">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('soft')">
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
				<th>템플릿</th>
				<td class="indent5">
					<select name="template" id="template" class="width-200">
						<option value="">선택</option>
						<%
						for (Map<String, String> map : templates) {
							String oid = map.get("key");
							String name = map.get("value");
						%>
						<option value="<%=oid%>"><%=name%></option>
						<%
						}
						%>
					</select>
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
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('history-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('history-list');">
					<input type="button" value="저장" title="저장" onclick="save();">
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

		<div id="grid_wrap" style="height: 635px; border-top: 1px solid #3180c3;"></div>
		<%@include file="/extcore/jsp/common/aui/aui-context.jsp"%>
		<script type="text/javascript">
			let myGridID;
			const list = [ "유", "무" ];
			function _layout() {
				return [ {
					dataField : "pdate",
					headerText : "발행일",
					dataType : "date",
					formatString : "yyyy-mm-dd",
					width : 100,
					editable : false,
					filter : {
						showIcon : true,
						inline : true,
						displayFormatValues : true
					},
				}, {
					dataField : "mak",
					headerText : "막종",
					dataType : "string",
					width : 100,
					editable : false,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "detail",
					headerText : "막종상세",
					dataType : "string",
					width : 100,
					editable : false,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "customer",
					headerText : "고객사",
					dataType : "string",
					width : 100,
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
					width : 100,
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.oid;
							alert("링크 준비중");
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
					width : 100,
					editable : false,
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.oid;
							alert("링크 준비중");
						}
					},
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
						iconWidth : 16,
						iconHeight : 16,
						iconPosition : "aisleRight",
						iconTableRef : {
							"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png" 
						},
						onClick : function(event) {
							AUIGrid.openInputer(event.pid);
						}
					},
					editRenderer : {
						type : "ComboBoxRenderer",
						autoCompleteMode : true,
						autoEasyMode : true,
						matchFromFirst : false,
						showEditorBtnOver : false,
						list : list,
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							for (let i = 0, len = list.length; i < len; i++) { 
								if (list[i] == newValue) {
									isValid = true;
									break;
								}
							}
							return {
								"validate" : isValid,
								"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
							};
						}
					},
					filter : {
						showIcon : true,
						inline : true
					},
				},
			<%for (Map<String, String> header : headers) {
	String key = header.get("key");
	String value = header.get("value");%>
				{
					dataField : "<%=key%>",
					headerText : "<%=value%>",
					dataType : "string",
					width : 130,
					filter : {
						showIcon : true,
						inline : true
					},
				},
				<%}%>
				]
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
					editable : true
				};
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				loadGridData();
				AUIGrid.bind(myGridID, "contextMenu", auiContextMenuHandler);
				AUIGrid.bind(myGridID, "vScrollChange", function(event) {
					hideContextMenu();
				});
				AUIGrid.bind(myGridID, "hScrollChange", function(event) {
					hideContextMenu();
				});
			}

			function save() {
				const url = getCallUrl("/history/save");
				const editRows = AUIGrid.getEditedRowItems(myGridID);
// 				const editRows = AUIGrid.getGridData(myGridID);
				const removeRows = AUIGrid.getRemovedItems(myGridID);
				const params = new Object();

				if (editRows.length === 0 && removeRows.length === 0) {
					alert("변경된 내용이 없습니다.");
					return false;
				}

				if (!confirm("저장 하시겠습니까?")) {
					return false;
				}

				params.editRows = editRows;
				params.removeRows = removeRows;
				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						loadGridData();
					} else {
						parent.closeLayer();
					}
				})
			}

			function loadGridData() {
				let params = new Object();
				const url = getCallUrl("/history/list");
				const field =["kekNumber","keNumber","pdateFrom","pdateTo","userId","kekState","model","customer_name","install_name","projectType","machineOid","elecOid","softOid","mak_name","detail_name","template","description","psize"];
				params = toField(params, field);
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

			document.addEventListener("DOMContentLoaded", function() {
				const columns = loadColumnLayout("history-list");
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
				selectbox("template");
				selectbox("psize");
			});

			function exportExcel() {
				const exceptColumnFields = [];
				const sessionName = document.getElementById("sessionName").value;
				exportToExcel("이력관리 리스트", "이력관리", "이력관리 리스트", exceptColumnFields, sessionName);
			}

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