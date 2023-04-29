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
<input type="hidden" name="sessionid" id="sessionid">
<input type="hidden" name="curPage" id="curPage">
<table class="search-table">
	<tr>
		<th>사업부문</th>
		<td class="indent5">
			<select name="businessSector" id="businessSector" class="width-200">
				<option value="">선택</option>
			</select>
		</td>
		<th>작성기간</th>
		<td class="indent5">&nbsp;</td>
		<th>도면번호</th>
		<td class="indent5">
			<input type="text" name="kekNumber" class="width-200">
		</td>
		<th>도면생성회사</th>
		<td class="indent5">
			<select name="size" id="size" class="width-200">
				<option value="">선택</option>
			</select>
		</td>
	</tr>
	<tr>
		<th>사이즈</th>
		<td class="indent5">
			<select name="size" id="size" class="width-200">
				<option value="">선택</option>
			</select>
		</td>
		<th>도면구분</th>
		<td class="indent5">&nbsp;</td>
		<th>년도</th>
		<td class="indent5">&nbsp;</td>
		<th>관리번호</th>
		<td class="indent5">
			<input type="text" name="kekNumber" class="width-200">
		</td>
	</tr>
	<tr>
		<th>부품도구분</th>
		<td class="indent5">&nbsp;</td>
		<th>진행상태</th>
		<td class="indent5">
			<select name="state" id="state" class="width-200">
				<option value="">선택</option>
				<option value="진행중">진행중</option>
				<option value="완료">완료</option>
				<option value="폐기">폐기</option>
			</select>
		</td>
		<th>작성부서</th>
		<td class="indent5">&nbsp;</td>
		<th>작성자</th>
		<td class="indent5">
			<input type="text" name="kekNumber" class="width-200">
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
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "size",
			headerText : "사이즈",
			dataType : "string",
			width : 80,
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
			dataField : "businessSector",
			headerText : "사업부문",
			dataType : "string",
			width : 200,
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
			dataField : "drawingCompany",
			headerText : "도면생성회사",
			dataType : "string",
			width : 150,
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
			dataField : "classificationWritingDepartments",
			headerText : "작성부서구분",
			dataType : "string",
			width : 150,
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
			dataField : "writtenDocuments",
			headerText : "작성문서구분",
			dataType : "string",
			width : 150,
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
		const params = new Object();
		const url = getCallUrl("/numberRule/list");
		const psize = document.getElementById("psize").value;
		params.psize = psize;
		AUIGrid.showAjaxLoader(myGridID);
		parent.openLayer();
		call(url, params, function(data) {
			console.log(data);
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
		selectbox("psize");
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