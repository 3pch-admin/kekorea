<%@page import="net.sf.json.JSONArray"%>
<%@page import="wt.org.WTUser"%>
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
JSONArray departments = JSONArray.fromObject(list);
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
		<input type="hidden" name="lastNum" id="lastNum">
		<input type="hidden" name="curPage" id="curPage">
		<input type="hidden" name="oid" id="oid">

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
				<th>부서</th>
				<td class="indent5" colspan="5">
					<span id="departmentText"><%=OrgHelper.DEPARTMENT_ROOT%></span>
				</td>
			</tr>
			<tr>
				<th>이름</th>
				<td class="indent5">
					<input type="text" name="userName" id="userName">
				</td>
				<th>아이디</th>
				<td class="indent5">
					<input type="text" name="userId" id="userId">
				</td>
				<th>퇴사여부</th>
				<td>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="resign" value="" checked="checked">
						<div class="state p-success">
							<label>
								<b>재직</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="resign" value="true">
						<div class="state p-success">
							<label>
								<b>퇴사</b>
							</label>
						</div>
					</div>
				</td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();">
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('organization-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('organization-list');">
					<input type="button" value="저장" title="저장" onclick="save();">
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
					<jsp:include page="/extcore/jsp/common/department-include.jsp">
						<jsp:param value="list" name="mode" />
						<jsp:param value="705" name="height" />
					</jsp:include>
				</td>
				<td>&nbsp;</td>
				<td>
					<div id="grid_wrap" style="height: 705px; border-top: 1px solid #3180c3;"></div>
				</td>
			</tr>
		</table>

		<%@include file="/extcore/jsp/common/aui/aui-context.jsp"%>
		<script type="text/javascript">
			let myGridID;
			const maks =
		<%=maks%>
			const departments =
		<%=departments%>
			const dutys = [ "사장", "부사장", "PL", "TL" ];
			function _layout() {
				return [ {
					dataField : "name",
					headerText : "이름",
					dataType : "string",
					width : 80,
					editable : false,
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.woid;
							const url = getCallUrl("/org/view?oid=" + oid);
							popup(url, 1000, 320);
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
					width : 80,
					editable : false,
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.woid;
							const url = getCallUrl("/org/view?oid=" + oid);
							popup(url, 1000, 320);
						}
					},
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "duty",
					headerText : "직급",
					dataType : "string",
					width : 80,
					filter : {
						showIcon : true,
						inline : true
					},
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
						type : "DropDownListRenderer",
						showEditorBtn : false,
						showEditorBtnOver : false,
						multipleMode : false,
						showCheckAll : false,
						list : dutys,
					},
				}, {
					dataField : "department_oid",
					headerText : "부서",
					dataType : "string",
					width : 150,
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
						type : "DropDownListRenderer",
						showEditorBtn : false,
						showEditorBtnOver : false,
						multipleMode : false,
						showCheckAll : false,
						list : departments,
						keyField : "oid",
						valueField : "name",
					},
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
						let retStr = "";
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
					style : "aui-left",
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
						type : "DropDownListRenderer",
						showEditorBtn : false,
						showEditorBtnOver : false,
						multipleMode : true,
						showCheckAll : true,
						list : maks,
						keyField : "key",
						valueField : "value",
					},
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
						let retStr = "";
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
					dataField : "email",
					headerText : "이메일",
					dataType : "string",
					width : 250,
					style : "aui-left",
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "gap",
					headerText : "비밀번호 기간 설정",
					dataType : "numeric",
					width : 130,
					postfix : "일",
					formatString : "###0",
					editRenderer : {
						type : "InputEditRenderer",
						onlyNumeric : true,
						maxlength : 3
					},
					filter : {
						showIcon : false,
						inline : false
					},
				}, {
					dataField : "setting",
					headerText : "비밀번호 기간 유무",
					dataType : "boolean",
					width : 130,
					renderer : {
						type : "CheckBoxEditRenderer",
						editable : true
					},
					filter : {
						showIcon : false,
						inline : false
					},
				},{
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

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : true,
// 					showRowCheckColumn : true,
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
					editable : true,
					enableRowCheckShiftKey : true,
				};
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				loadGridData();
				AUIGrid.bind(myGridID, "cellEditBegin", auiCellEditBegin);
				AUIGrid.bind(myGridID, "contextMenu", auiContextMenuHandler);
				AUIGrid.bind(myGridID, "vScrollChange", function(event) {
					hideContextMenu();
					vScrollChangeHandler(event);
				});
				AUIGrid.bind(myGridID, "hScrollChange", function(event) {
					hideContextMenu();
				});
			}

			function auiCellEditBegin(event) {
				const item = event.item;
				const sessionId = document.getElementById("sessionId").value;
				const isAdmin = document.getElementById("isAdmin").value;
				if (!checker(sessionId, item.id) && !isAdmin) {
					return false;
				}
				return true;
			}

			function loadGridData() {
				let params = new Object();
				const url = getCallUrl("/org/list");
				const field = [ "userName", "userId", "oid", "_psize" ];
				const resign = !!document.querySelector("input[name=resign]:checked").value;
				params = toField(params, field);
				params.resign = resign;
				AUIGrid.showAjaxLoader(myGridID);
				parent.openLayer();
				call(url, params, function(data) {
					AUIGrid.removeAjaxLoader(myGridID);
					if (data.result) {
						document.getElementById("sessionid").value = data.sessionid;
						document.getElementById("curPage").value = data.curPage;
						document.getElementById("lastNum").value = data.list.length;
						AUIGrid.setGridData(myGridID, data.list);
					} else {
						alert(data.msg);
					}
					parent.closeLayer();
				});
			}

			function save() {
				const url = getCallUrl("/org/save");
				const params = new Object();
				const editRows = AUIGrid.getEditedRowItems(myGridID);

				if (editRows.length === 0) {
					alert("변경된 내용이 없습니다.");
					return false;
				}

				if (!confirm("저장 하시겠습니까?")) {
					return false;
				}

				params.editRows = editRows;
				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					parent.closeLayer();
					if (data.result) {
						loadGridData();
					}
				})
			}

			function exportExcel() {
				const exceptColumnFields = [ "resign" ];
				const sessionName = document.getElementById("sessionName").value;
				exportToExcel("조직도 리스트", "조직도", "조직도 리스트", exceptColumnFields, sessionName);
			}

			document.addEventListener("DOMContentLoaded", function() {
				document.getElementById("userName").focus();
				const columns = loadColumnLayout("organization-list");
				const contenxtHeader = genColumnHtml(columns);
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				_createAUIGrid(_columns); // 트리
				AUIGrid.resize(myGridID);
				AUIGrid.resize(_myGridID); // 트리
				selectbox("_psize");
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
				AUIGrid.resize(_myGridID); // 트리
			});
		</script>
	</form>
</body>
</html>