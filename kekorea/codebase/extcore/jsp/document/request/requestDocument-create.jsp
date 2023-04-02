<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="org.json.JSONArray"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%
ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String, String>>) request.getAttribute("list");
JSONArray elecs = (JSONArray) request.getAttribute("elecs");
JSONArray softs = (JSONArray) request.getAttribute("softs");
JSONArray machines = (JSONArray) request.getAttribute("machines");
JSONArray maks = (JSONArray) request.getAttribute("maks");
JSONArray installs = (JSONArray) request.getAttribute("installs");
JSONArray customers = (JSONArray) request.getAttribute("customers");
JSONArray projectTypes = (JSONArray) request.getAttribute("projectTypes");
%>
<%@include file="/extcore/include/auigrid.jsp"%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				의뢰서 등록
			</div>
		</td>
		<td class="right">
			<input type="button" value="등록" title="등록" onclick="create();">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>

<div id="tabs">
	<ul>
		<li>
			<a href="#tabs-1">기본정보</a>
		</li>
		<li>
			<a href="#tabs-2">관련작번</a>
		</li>
	</ul>
	<div id="tabs-1">
		<table class="create-table">
			<colgroup>
				<col width="150">
				<col width="500">
				<col width="150">
				<col width="500">
			</colgroup>
			<tr>
				<th class="req lb">의뢰서 제목</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-300">
				</td>
				<th class="req">작번 템플릿</th>
				<td class="indent5">
					<select name="template" id="template" class="width-300">
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
			</tr>
			<tr>
				<th class="lb">내용</th>
				<td class="indent5" colspan="3">
					<textarea name="description" id="description" rows="6"></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/include/primary-include.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
						<jsp:param value="150" name="height" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="req lb">결재</th>
				<td colspan="3">
					<jsp:include page="/extcore/include/register-include.jsp">
						<jsp:param value="250" name="height" />
					</jsp:include>
				</td>
			</tr>
		</table>
	</div>
	<div id="tabs-2">
		<div id="grid_wrap" style="height: 610px; border-top: 1px solid #3180c3; margin-top: 5px;"></div>
	</div>
	<script type="text/javascript">
		let myGridID;
		const maks = <%=maks%>
		const installs = <%=installs%>
		const customers = <%=customers%>
		const elecs = <%=elecs%>
		const machines = <%=machines%>
		const softs = <%=softs%>
		const projectTypes = <%=projectTypes%>
		let detailMap = {};
		let installMap = {};
		const columns = [ {
			dataField : "projectType_code",
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
		}, {
			dataField : "customer_code",
			headerText : "거래처",
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
		}, {
			dataField : "install_code",
			headerText : "설치장소",
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
				keyField : "key",
				valueField : "value",
				validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
					const param = item.customer_code;
					const dd = installMap[param];
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
				descendantDefaultValues : [ "" ],
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
					var param = item.mak_code;
					var dd = detailMap[param];
					if (dd === undefined) {
						return [];
					}
					return dd;
				},
			},
			labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
				let retStr = "";
				let param = item.mak_code;
				let dd = detailMap[param];
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
		}, {
			dataField : "kekNumber",
			headerText : "KEK 작번",
			dataType : "string",
			width : 130,
		}, {
			dataField : "keNumber",
			headerText : "KE 작번",
			dataType : "string",
			width : 130,
		}, {
			dataField : "userId",
			headerText : "USER ID",
			dataType : "string",
			width : 100,
		}, {
			dataField : "customDate",
			headerText : "요구 납기일",
			dataType : "date",
			dateInputFormat : "yyyy-mm-dd",
			formatString : "yyyy년 mm월 dd일",
			width : 130,
			renderer : {
				type : "IconRenderer",
				iconWidth : 16,
				iconHeight : 16,
				iconPosition : "aisleRight",
				iconTableRef : {
					"default" : "/Windchill/extcore/component/AUIGrid/images/calendar-icon.png"
				},
				onClick : function(event) {
					AUIGrid.openInputer(event.pid);
				}
			},
			editRenderer : {
				type : "CalendarRenderer",
				defaultFormat : "yyyy-mm-dd",
				showEditorBtnOver : false,
				onlyCalendar : true,
				showExtraDays : true,
				showTodayBtn : true,
				showUncheckDateBtn : true,
				todayText : "오늘 선택",
				uncheckDateText : "날짜 선택 해제",
				uncheckDateValue : "",
			}
		}, {
			dataField : "description",
			headerText : "작업 내용",
			dataType : "string",
			width : 250,
			style : "aui-left",
		}, {
			dataField : "model",
			headerText : "모델",
			dataType : "string",
			width : 130,
		}, {
			dataField : "pdate",
			headerText : "발행일",
			dataType : "date",
			dateInputFormat : "yyyy-mm-dd",
			formatString : "yyyy년 mm월 dd일",
			width : 130,
			renderer : {
				type : "IconRenderer",
				iconWidth : 16,
				iconHeight : 16,
				iconPosition : "aisleRight",
				iconTableRef : {
					"default" : "/Windchill/extcore/component/AUIGrid/images/calendar-icon.png"
				},
				onClick : function(event) {
					AUIGrid.openInputer(event.pid);
				}
			},
			editRenderer : {
				type : "CalendarRenderer",
				defaultFormat : "yyyy-mm-dd",
				showEditorBtnOver : false,
				onlyCalendar : true,
				showExtraDays : true,
				showTodayBtn : true,
				showUncheckDateBtn : true,
				todayText : "오늘 선택",
				uncheckDateText : "날짜 선택 해제",
				uncheckDateValue : "",
			}
		}, {
			dataField : "machine",
			headerText : "기계 담당자",
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
				list : machines,
				keyField : "oid",
				valueField : "name",
				validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
					let isValid = false;
					for (let i = 0, len = machines.length; i < len; i++) {
						if (machines[i] == newValue) {
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
			dataField : "elec",
			headerText : "전기 담당자",
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
				list : elecs,
				keyField : "oid",
				valueField : "name",
				validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
					let isValid = false;
					for (let i = 0, len = elecs.length; i < len; i++) {
						if (elecs[i] == newValue) {
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
			dataField : "soft",
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
						if (softs[i] == newValue) {
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
		} ]

		function createAUIGrid(columnLayout) {
			const props = {
				headerHeight : 30,
				showRowNumColumn : true,
				showRowCheckColumn : true,
				rowNumHeaderText : "번호",
				noDataMessage : "작성된 작번내용이 없습니다.",
				selectionMode : "multipleCells",
				editable : true,
				enableSorting : false,
				useContextMenu : true,
				enableRightDownFocus : true,
				$compaEventOnPaste : true,
				contextMenuItems : [ {
					label : "선택된 행 이전 추가",
					callback : contextItemHandler
				}, {
					label : "선택된 행 이후 추가",
					callback : contextItemHandler
				}, {
					label : "_$line"
				}, {
					label : "선택된 행 삭제",
					callback : contextItemHandler
				} ],
			};
			myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
			readyHandler();
			AUIGrid.bind(myGridID, "cellEditEnd", auiCellEditEndHandler);
		}

		function contextItemHandler(event) {
			const item = new Object();
			switch (event.contextIndex) {
			case 0:
				item.createdDate = new Date();
				AUIGrid.addRow(myGridID, item, "selectionUp");
				break;
			case 1:
				item.createdDate = new Date();
				AUIGrid.addRow(myGridID, item, "selectionDown");
				break;
			case 3:
				const selectedItems = AUIGrid.getSelectedItems(myGridID);
				for (let i = selectedItems.length - 1; i >= 0; i--) {
					const rowIndex = selectedItems[i].rowIndex;
					AUIGrid.removeRow(myGridID, rowIndex);
				}
				break;
			}
		}

		function auiCellEditEndHandler(event) {
			const dataField = event.dataField;
			const item = event.item;
			const rowIndex = event.rowIndex;
			if (dataField === "mak_code") {
				const mak = item.mak_code;
				const url = getCallUrl("/commonCode/getChildrens?parentCode=" + mak + "&codeType=MAK");
				call(url, null, function(data) {
					detailMap[mak] = data.list;
				}, "GET");
			}

			if (dataField === "customer_code") {
				const customer = item.customer_code;
				const url = getCallUrl("/commonCode/getChildrens?parentCode=" + customer + "&codeType=CUSTOMER");
				call(url, null, function(data) {
					installMap[customer] = data.list;
				}, "GET");
			}

			if (dataField === "kekNumber" || dataField === "projectType_code") {
				if (!isNull(item.kekNumber) && !isNull(item.projectType_code)) {
					const params = new Object();
					const url = getCallUrl("/requestDocument/validate");
					params.kekNumber = item.kekNumber;
					params.projectType_code = item.projectType_code;
					call(url, params, function(data) {
						if (data.validate) {
							alert(rowIndex + "행에 입력한 작번은 이미 등록되어있습니다.");
							item.kekNumber = "";
							item.projectType_code = "";
							AUIGrid.updateRow(myGridID, item, rowIndex);
							return false;
						}
					})
				}
			}
		}

		function readyHandler() {
			const item = new Object();
			AUIGrid.addRow(myGridID, item, "first");
		}

		function create() {

			const params = new Object();
			const url = getCallUrl("/requestDocument/create");
			const name = document.getElementById("name");
			const template = document.getElementById("template");
			const addRows = AUIGrid.getAddedRowItems(myGridID);
			const _addRows_ = AUIGrid.getAddedRowItems(_myGridID_);
			if (isNull(name.value)) {
				alert("의뢰서 제목을 입력하세요.");
				name.focus();
				return false;
			}

			if (isNull(template.value)) {
				alert("작번 템플릿을 선택하세요.");
				return false;
			}

			if (_addRows_.length === 0) {
				alert("결재선을 지정하세요.");
				_register();
				return false;
			}

			if (!confirm("등록 하시겠습니까?")) {
				return false;
			}

			params.name = name.value;
			params.addRows = addRows;
			params._addRows_ = _addRows_;
			params.primarys = toArray("primarys");
			params.template = template.value;
			toRegister(params, _addRows_);
			openLayer();
			call(url, params, function(data) {
				alert(data.msg);
				if (data.result) {
					opener.loadGridData();
					self.close();
				}
			})
		}
	</script>
</div>
<script type="text/javascript">
	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0,
			create : function(event, ui) {
				const tabId = ui.panel.prop("id");
				switch (tabId) {
				case "tabs-1":
					_createAUIGrid_(_columns_);
					AUIGrid.resize(_myGridID_);
					break;
				case "tabs-2":
					createAUIGrid(columns);
					AUIGrid.resize(myGridID);
					break;
				}
			},
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-1":
					const _isCreated_ = AUIGrid.isCreated(_myGridID_);
					if (_isCreated_) {
						AUIGrid.resize(_myGridID_);
					} else {
						_createAUIGrid_(_columns_);
					}
					break;
				case "tabs-2":
					const isCreated = AUIGrid.isCreated(myGridID);
					if (isCreated) {
						AUIGrid.resize(myGridID);
					} else {
						createAUIGrid(columns);
					}
					break;
				}
			}
		});
		document.getElementById("name").focus();
		selectbox("template");
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(_myGridID_);
		AUIGrid.resize(myGridID);
	});
</script>