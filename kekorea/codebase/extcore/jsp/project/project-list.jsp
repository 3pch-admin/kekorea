<%@page import="net.sf.json.JSONArray"%>
<%@page import="wt.org.WTUser"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
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
JSONArray machines = (JSONArray) request.getAttribute("machines");
JSONArray elecs = (JSONArray) request.getAttribute("elecs");
JSONArray softs = (JSONArray) request.getAttribute("softs");
JSONArray maksJson = (JSONArray) request.getAttribute("maksJson");
JSONArray customersJson = (JSONArray) request.getAttribute("customersJson");
JSONArray projectTypesJson = JSONArray.fromObject(projectTypes);
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
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('machine');">
				</td>
				<th>전기 담당자</th>
				<td class="indent5">
					<input type="text" name="elec" id="elec" data-multi="false">
					<input type="hidden" name="elecOid" id="elecOid">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('elec');">
				</td>
				<th>SW 담당자</th>
				<td class="indent5">
					<input type="text" name="soft" id="soft" data-multi="false">
					<input type="hidden" name="softOid" id="softOid">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('soft');">
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
						for (Map<String, String> map : list) {
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
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('project-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('project-list');">
					<input type="button" value="저장" title="저장" onclick="save();">
					<input type="button" value="등록" title="등록" class="blue" onclick="create();">
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
			const elecs = <%=elecs%>
			const machines = <%=machines%>
			const softs = <%=softs%>
			const customers = <%=customersJson%>
			const maks = <%=maksJson%>
			const projectTypes = <%=projectTypesJson%>
			let detailMap = {};
			let installMap = {};
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
					editable : false
				}, {
					dataField : "projectType_oid",
					headerText : "작번유형",
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
						list : projectTypes,
						keyField : "key",
						valueField : "value",
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							for (let i = 0, len = projectTypes.length; i < len; i++) {
								if (projectTypes[i]["value"] == newValue) {
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
						for (let i = 0, len = projectTypes.length; i < len; i++) {
							if (projectTypes[i]["key"] == value) {
								retStr = projectTypes[i]["value"];
								break;
							}
						}
						return retStr == "" ? value : retStr;
					},					
					filter : {
						showIcon : true,
						inline : true
					},
					editable : true
				}, {
					dataField : "customer_code",
					headerText : "거래처",
					dataType : "string",
					width : 100,
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
						list : customers,
						keyField : "key",
						valueField : "value",
						descendants : [ "install_code" ],
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							for (let i = 0, len = customers.length; i < len; i++) {
								if (customers[i]["value"] == newValue) {
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
						for (let i = 0, len = customers.length; i < len; i++) {
							if (customers[i]["key"] == value) {
								retStr = customers[i]["value"];
								break;
							}
						}
						return retStr == "" ? value : retStr;
					},						
					filter : {
						showIcon : true,
						inline : true
					},
					editable : true
				}, {
					dataField : "install_code",
					headerText : "설치장소",
					dataType : "string",
					width : 100,
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
						keyField : "key",
						valueField : "value",
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							const param = item.customer_code;
							const dd = installMap[param];
							if (dd === undefined)
								return;
							let isValid = false;
							for (let i = 0, len = dd.length; i < len; i++) {
								if (dd[i]["value"] == newValue) {
									isValid = true;
									break;
								}
							}
							return {
								"validate" : isValid,
								"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
							};
						},
						listFunction : function(rowIndex, columnIndex, item, dataField) {
							const param = item.customer_code;
							const dd = installMap[param];
							if (dd === undefined) {
								return [];
							}
							return dd;
						},
					},
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
						let retStr = "";
						const param = item.customer_code;
						const dd = installMap[param];
						if (dd === undefined)
							return value;
						for (let i = 0, len = dd.length; i < len; i++) {
							if (dd[i]["key"] == value) {
								retStr = dd[i]["value"];
								break;
							}
						}
						return retStr == "" ? value : retStr;
					},					
					filter : {
						showIcon : true,
						inline : true
					},
					editable : true
				}, {
					dataField : "mak_code",
					headerText : "막종",
					dataType : "string",
					width : 100,
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
						list : maks,
						keyField : "key",
						valueField : "value",
						descendants : [ "detail_code" ],
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							for (let i = 0, len = maks.length; i < len; i++) {
								if (maks[i]["value"] == newValue) {
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
					editable : true
				}, {
					dataField : "detail_code",
					headerText : "막종상세",
					dataType : "string",
					width : 100,
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
						keyField : "key",
						valueField : "value",
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							const param = item.mak_code;
							const dd = detailMap[param];
							if (dd === undefined)
								return;
							let isValid = false;
							for (let i = 0, len = dd.length; i < len; i++) {
								if (dd[i]["value"] == newValue) {
									isValid = true;
									break;
								}
							}
							return {
								"validate" : isValid,
								"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
							};
						},
						listFunction : function(rowIndex, columnIndex, item, dataField) {
							const param = item.mak_code;
							const dd = detailMap[param];
							if (dd === undefined) {
								return [];
							}
							return dd;
						},
					},
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
						let retStr = "";
						const param = item.mak_code;
						const dd = detailMap[param];
						if (dd === undefined)
							return value;
						for (let i = 0, len = dd.length; i < len; i++) {
							if (dd[i]["key"] == value) {
								retStr = dd[i]["value"];
								break;
							}
						}
						return retStr == "" ? value : retStr;
					},					
					filter : {
						showIcon : true,
						inline : true
					},
					editable : true
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
					editable : false
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
					dataField : "machine_oid",
					headerText : "기계 담당자",
					width : 100,
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
						list : machines,
						keyField : "oid",
						valueField : "name",
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							for (let i = 0, len = machines.length; i < len; i++) {
								if (machines[i]["name"] == newValue) {
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
						for (let i = 0, len = machines.length; i < len; i++) {
							if (machines[i]["oid"] == value) {
								retStr = machines[i]["name"];
								break;
							}
						}
						return retStr == "" ? value : retStr;
					},
				}, {
					dataField : "elec_oid",
					headerText : "전기 담당자",
					width : 100,
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
						list : elecs,
						keyField : "oid",
						valueField : "name",
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							for (let i = 0, len = elecs.length; i < len; i++) {
								if (elecs[i]["name"] == newValue) {
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
						for (let i = 0, len = elecs.length; i < len; i++) {
							if (elecs[i]["oid"] == value) {
								retStr = elecs[i]["name"];
								break;
							}
						}
						return retStr == "" ? value : retStr;
					},
				}, {
					dataField : "soft_oid",
					headerText : "SW 담당자",
					dataType : "string",
					width : 100,
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
						list : softs,
						keyField : "oid",
						valueField : "name",
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							for (let i = 0, len = softs.length; i < len; i++) {
								if (softs[i]["name"] == newValue) {
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
						for (let i = 0, len = softs.length; i < len; i++) {
							if (softs[i]["oid"] == value) {
								retStr = softs[i]["name"];
								break;
							}
						}
						return retStr == "" ? value : retStr;
					},
				}, {
					dataField : "totalPrice",
					headerText : "작번 견적 금액",
					dataType : "numeric",
					width : 130,
					formatString : "#,##0",
					postfix : "원",
					filter : {
						showIcon : true,
						inline : true
					},
					editable : false
				}, {
					dataField : "machinePrice",
					headerText : "기계 견적 금액",
					dataType : "numeric",
					width : 130,
					formatString : "#,##0",
					postfix : "원",
					editRenderer : {
						type : "InputEditRenderer",
						onlyNumeric : true,
					},
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "elecPrice",
					headerText : "전기 견적 금액",
					dataType : "numeric",
					width : 130,
					formatString : "#,##0",
					postfix : "원",
					editRenderer : {
						type : "InputEditRenderer",
						onlyNumeric : true,
					},
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "progress",
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
					editable : false
				}, {
					dataField : "kekState",
					headerText : "작번상태",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
					editable : false
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
					editable : true,
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
				
				AUIGrid.bind(myGridID, "cellEditEnd", auiCellEditEndHandler);
				AUIGrid.bind(myGridID, "ready", readyHandler);
			}
			
			
			function readyHandler() {
				const item = AUIGrid.getGridData(myGridID);
				for (let i = 0; i < item.length; i++) {
					if (detailMap.length === undefined) {
						const mak = item[i].mak_code;
						const url = getCallUrl("/commonCode/getChildrens?parentCode=" + mak + "&codeType=MAK");
						call(url, null, function(data) {
							detailMap[mak] = data.list;
						}, "GET");
					}

					if (installMap.length === undefined) {
						const customer = item[i].customer_code;
						const url = getCallUrl("/commonCode/getChildrens?parentCode=" + customer + "&codeType=CUSTOMER");
						call(url, null, function(data) {
							installMap[customer] = data.list;
						}, "GET");
					}
				}
			}

			function auiCellEditEndHandler(event) {
				const dataField = event.dataField;
				const item = event.item;
				const rowIndex = event.rowIndex;
				if (dataField === "mak_code") {
					const item = {
							detail_code : ""
					}
					AUIGrid.updateRow(myGridID, item, rowIndex);
					const mak = item.mak_code;
					const url = getCallUrl("/commonCode/getChildrens?parentCode=" + mak + "&codeType=MAK");
					call(url, null, function(data) {
						detailMap[mak] = data.list;
					}, "GET");
				}

				if (dataField === "customer_code") {
					const item = {
							install_code : ""
					}
					AUIGrid.updateRow(myGridID, item, rowIndex);
					const customer = item.customer_code;
					const url = getCallUrl("/commonCode/getChildrens?parentCode=" + customer + "&codeType=CUSTOMER");
					call(url, null, function(data) {
						installMap[customer] = data.list;
					}, "GET");
				}
			}

			function loadGridData() {
				let params = new Object();
				const url = getCallUrl("/project/list");
				const field = [ "kekNumber", "keNumber", "pdateFrom", "pdateTo", "userId", "kekState", "model", "customer_name", "install_name", "projectType", "machineOid", "elecOid", "softOid", "mak_name", "detail_name", "template", "description", "psize" ];
				params = toField(params, field);
				AUIGrid.showAjaxLoader(myGridID);
				parent.openLayer();
				call(url, params, function(data) {
					AUIGrid.removeAjaxLoader(myGridID);
					if (data.result) {
						document.getElementById("sessionid").value = data.sessionid;
						document.getElementById("curPage").value = data.curPage;
						AUIGrid.setGridData(myGridID, data.list);
					} else {
						alert(data.msg);
					}
					parent.closeLayer();
				});
			}

			function save() {
				const params = new Object();
				const url = getCallUrl("/project/save");
				const editRows = AUIGrid.getEditedRowItems(myGridID);
				if (editRows.length == 0) {
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
					if (data.result) {
						loadGridData();
					}
				})
			}

			function exportExcel() {
				const exceptColumnFields = [ "kekProgress" ];
				const sessionName = document.getElementById("sessionName").value;
				exportToExcel("작번 리스트", "작번", "작번 리스트", exceptColumnFields, sessionName);
			}

			document.addEventListener("DOMContentLoaded", function() {
				document.getElementById("kekNumber").focus();
				const columns = loadColumnLayout("project-list");
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

			function create() {
				const url = getCallUrl("/project/create");
				popup(url, 1200, 460);
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