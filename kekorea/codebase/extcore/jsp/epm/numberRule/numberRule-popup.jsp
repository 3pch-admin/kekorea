<%@page import="net.sf.json.JSONObject"%>
<%@page import="java.util.ListIterator"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.admin.numberRuleCode.NumberRuleCode"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="wt.org.WTUser"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.admin.commonCode.CommonCodeType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
JSONArray sizes = (JSONArray) request.getAttribute("sizes");
JSONArray drawingCompanys = (JSONArray) request.getAttribute("drawingCompanys");
JSONArray writtenDocuments = (JSONArray) request.getAttribute("writtenDocuments");
JSONArray businessSectors = (JSONArray) request.getAttribute("businessSectors");
JSONArray classificationWritingDepartments = (JSONArray) request.getAttribute("classificationWritingDepartments");
String method = (String) request.getAttribute("method");
boolean multi = (boolean) request.getAttribute("multi");
%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js"></script>
<input type="hidden" name="isAdmin" id="isAdmin" value="<%=isAdmin%>">
<input type="hidden" name="sessionName" id="sessionName" value="<%=sessionUser.getFullName()%>">
<input type="hidden" name="sessionId" id="sessionId" value="<%=sessionUser.getName()%>">
<input type="hidden" name="sessionid" id="sessionid"><input type="hidden" name="lastNum" id="lastNum">
<input type="hidden" name="curPage" id="curPage">
<table class="search-table">
	<tr>
		<th>도면번호</th>
		<td class="indent5">
			<input type="text" name="number" id="number">
		</td>
		<th>사이즈</th>
		<td class="indent5">
			<select name="size" id="size" class="width-200">
				<option value="">선택</option>
				<%
				ListIterator lit = sizes.listIterator();
				while (lit.hasNext()) {
					JSONObject node = (JSONObject) lit.next();
					String key = node.getString("key");
					String value = node.getString("value");
				%>
				<option value="<%=key%>"><%=value%></option>
				<%
				}
				%>
			</select>
		</td>
		<th>LOT</th>
		<td class="indent5">
			<input type="text" name="lotNo" id="lotNo" maxlength="4">
		</td>
		<th>UNIT NAME</th>
		<td class="indent5">
			<input type="text" name="unitName" id="unitName">
		</td>
	</tr>
	<tr>
		<th>도면명</th>
		<td class="indent5">
			<input type="text" name="name" id="name">
		</td>
		<th>작성부서구분</th>
		<td class="indent5">
			<select name="classificationWritingDepartments_code" id="classificationWritingDepartments_code" class="width-200">
				<option value="">선택</option>
				<%
				ListIterator lit2 = classificationWritingDepartments.listIterator();
				while (lit2.hasNext()) {
					JSONObject node = (JSONObject) lit2.next();
					String key = node.getString("key");
					String value = node.getString("value");
				%>
				<option value="<%=key%>"><%=value%></option>
				<%
				}
				%>
			</select>
		</td>
		<th>작성문서구분</th>
		<td class="indent5">
			<select name="writtenDocuments_code" id="writtenDocuments_code" class="width-200">
				<option value="">선택</option>
				<%
				ListIterator lit3 = writtenDocuments.listIterator();
				while (lit3.hasNext()) {
					JSONObject node = (JSONObject) lit3.next();
					String key = node.getString("key");
					String value = node.getString("value");
				%>
				<option value="<%=key%>"><%=value%></option>
				<%
				}
				%>
			</select>
		</td>
		<th>상태</th>
		<td class="indent5">
			<select name="state" id="state" class="width-200">
				<option value="">선택</option>
				<option value="작업 중">작업 중</option>
				<option value="승인중">승인중</option>
				<option value="승인됨">승인됨</option>
				<option value="반려됨">반려됨</option>
			</select>
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
		<th>버전</th>
		<td colspan="3">
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="latest" value="true" checked="checked">
				<div class="state p-success">
					<label>
						<b>죄신버전</b>
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
			<input type="button" value="추가" title="추가" class="blue" onclick="<%=method%>();">
			<input type="button" value="닫기" title="닫기" class="red" onclick="self.close();">
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

<div id="grid_wrap" style="height: 430px; border-top: 1px solid #3180c3;"></div>
<%@include file="/extcore/jsp/common/aui/aui-context.jsp"%>
<script type="text/javascript">
			let myGridID;
			const businessSector =
		<%=businessSectors%>
			const drawingCompany =
		<%=drawingCompanys%>
			const size =
		<%=sizes%>
			const writtenDocuments =
		<%=writtenDocuments%>
			const classificationWritingDepartments =
		<%=classificationWritingDepartments%>
			function _layout() {
				return [ {
					dataField : "number",
					headerText : "도면번호",
					dataType : "string",
					width : 100,
					editable : false,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "size_code",
					headerText : "사이즈",
					dataType : "string",
					width : 80,
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
						list : size,
						keyField : "key",
						valueField : "value",
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							for (let i = 0, len = size.length; i < len; i++) {
								if (size[i]["value"] == newValue) {
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
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
						let retStr = "";
						for (let i = 0, len = size.length; i < len; i++) {
							if (size[i]["key"] == value) {
								retStr = size[i]["value"];
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
					dataField : "lotNo",
					headerText : "LOT",
					dataType : "numeric",
					width : 80,
					formatString : "###0",
					editRenderer : {
						type : "InputEditRenderer",
						onlyNumeric : true,
						maxlength : 4,
					},
					filter : {
						showIcon : true,
						inline : true,
						displayFormatValues : true
					},
				}, {
					dataField : "unitName",
					headerText : "UNIT NAME",
					dataType : "string",
					width : 200,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "name",
					headerText : "도번명",
					dataType : "string",
					width : 250,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "businessSector_code",
					headerText : "사업부문",
					dataType : "string",
					width : 200,
					editable : false,
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
						let retStr = "";
						for (let i = 0, len = businessSector.length; i < len; i++) {
							if (businessSector[i]["key"] == value) {
								retStr = businessSector[i]["value"];
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
					dataField : "drawingCompany_code",
					headerText : "도면생성회사",
					dataType : "string",
					width : 150,
					editable : false,
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
						let retStr = "";
						for (let i = 0, len = drawingCompany.length; i < len; i++) {
							if (drawingCompany[i]["key"] == value) {
								retStr = drawingCompany[i]["value"];
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
					dataField : "classificationWritingDepartments_code",
					headerText : "작성부서구분",
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
						type : "ComboBoxRenderer",
						autoCompleteMode : true,
						autoEasyMode : true,
						matchFromFirst : false,
						showEditorBtnOver : false,
						list : classificationWritingDepartments,
						keyField : "key",
						valueField : "value",
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							for (let i = 0, len = classificationWritingDepartments.length; i < len; i++) {
								if (classificationWritingDepartments[i]["value"] == newValue) {
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
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
						let retStr = "";
						for (let i = 0, len = classificationWritingDepartments.length; i < len; i++) {
							if (classificationWritingDepartments[i]["key"] == value) {
								retStr = classificationWritingDepartments[i]["value"];
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
					dataField : "writtenDocuments_code",
					headerText : "작성문서구분",
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
						type : "ComboBoxRenderer",
						autoCompleteMode : true,
						autoEasyMode : true,
						matchFromFirst : false,
						showEditorBtnOver : false,
						list : writtenDocuments,
						keyField : "key",
						valueField : "value",
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							for (let i = 0, len = writtenDocuments.length; i < len; i++) {
								if (writtenDocuments[i]["value"] == newValue) {
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
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
						let retStr = "";
						for (let i = 0, len = writtenDocuments.length; i < len; i++) {
							if (writtenDocuments[i]["key"] == value) {
								retStr = writtenDocuments[i]["value"];
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
					dataField : "version",
					headerText : "버전",
					dataType : "string",
					width : 100,
					editable : false,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "state",
					headerText : "상태",
					dataType : "string",
					width : 80,
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
					dataField : "createdDate_txt",
					headerText : "작성일",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
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
					dataField : "modifiedDate_txt",
					headerText : "수정일",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
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
			enableMovingColumn : true,
			showInlineFilter : true,
			useContextMenu : true,
			enableRightDownFocus : true,
			filterLayerWidth : 320,
			filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
			<%if (!multi) {%>
			rowCheckToRadio : true
			<%}%>
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
	}

	function loadGridData() {
		let params = new Object();
		const url = getCallUrl("/numberRule/list");
		const field = [ "number", "name", "lotNo", "unitName", "size", "state", "writtenDocuments_code", "creatorOid", "createdFrom", "createdTo", "classificationWritingDepartments_code", "_psize" ];
		const latest = !!document.querySelector("input[name=latest]:checked").value;
		params = toField(params, field);
		params.latest = latest;
		AUIGrid.showAjaxLoader(myGridID);
		parent.openLayer();
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			$("input[name=sessionid]").val(data.sessionid);
			$("input[name=curPage]").val(data.curPage);
			AUIGrid.setGridData(myGridID, data.list);
			parent.closeLayer();
		})
	}
	
	function <%=method%>() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
		if (checkedItems.length == 0) {
			alert("추가할 도번을 선택하세요.");
			return false;
		}

		if(checkedItems[0].item.state !== "작업 중") {
			alert("작업 중 상태의 도번만 추가가 가능합니다.");
			return false;
		}
		
		// 승인된 도면 추가 못하게 설정
		
		openLayer();
		opener.<%=method%>(checkedItems[0], function(result) {
			if(result) {
				self.close();
			}
		});
	}
	

	document.addEventListener("DOMContentLoaded", function() {
		const columns = loadColumnLayout("numberRule-list");
		const contenxtHeader = genColumnHtml(columns);
		$("#h_item_ul").append(contenxtHeader);
		$("#headerMenu").menu({
			select : headerMenuSelectHandler
		});
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
		selectbox("_psize");
		selectbox("businessSector");
		selectbox("state");
		selectbox("size");
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