<%@page import="e3ps.doc.service.DocumentHelper"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
boolean multi = (boolean) request.getAttribute("multi");
String method = (String) request.getAttribute("method");
%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=1010"></script>
<input type="hidden" name="isAdmin" id="isAdmin" value="<%=isAdmin%>">
<input type="hidden" name="sessionName" id="sessionName" value="<%=sessionUser.getFullName()%>">
<input type="hidden" name="sessionId" id="sessionId" value="<%=sessionUser.getName()%>">
<input type="hidden" name="sessionid" id="sessionid">
<input type="hidden" name="curPage" id="curPage">
<input type="hidden" name="oid" id="oid">

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
		<th>문서 분류</th>
		<td colspan="7" class="indent5">
			<input type="hidden" name="location" id="location" value="<%=DocumentHelper.DOCUMENT_ROOT%>">
			<span id="locationText"><%=DocumentHelper.DOCUMENT_ROOT%></span>
		</td>
	</tr>
	<tr>
		<th>문서 제목</th>
		<td class="indent5">
			<input type="text" name="name" id="name" class="width-300">
		</td>
		<th>문서 번호</th>
		<td class="indent5">
			<input type="text" name="number" id="number" class="width-200">
		</td>
		<th>설명</th>
		<td class="indent5">
			<input type="text" name="description" id="description" class="width-300">
		</td>
		<th>상태</th>
		<td class="indent5">
			<select name="state" id="state" class="width-200">
				<option value="">선택</option>
			</select>
		</td>
	</tr>
	<tr>
		<th>작성자</th>
		<td class="indent5">
			<input type="text" name="creator" id="creator" class="width-200">
			<input type="hidden" name="creatorOid" id="creatorOid">
		</td>
		<th>작성일</th>
		<td class="indent5">
			<input type="text" name="createdFrom" id="createdFrom" class="width-100">
			~
			<input type="text" name="createdTo" id="createdTo" class="width-100">
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

<table>
	<colgroup>
		<col width="230">
		<col width="10">
		<col width="*">
	</colgroup>
	<tr>
		<td valign="top">
			<jsp:include page="/extcore/jsp/common/folder-include.jsp">
				<jsp:param value="<%=DocumentHelper.DOCUMENT_ROOT%>" name="location" />
				<jsp:param value="product" name="container" />
				<jsp:param value="list" name="mode" />
				<jsp:param value="520" name="height" />
			</jsp:include>
		</td>
		<td valign="top">&nbsp;</td>
		<td valign="top">
			<div id="grid_wrap" style="height: 520px; border-top: 1px solid #3180c3;"></div>
			<%@include file="/extcore/jsp/common/aui/aui-context.jsp"%>
		</td>
	</tr>
</table>
<script type="text/javascript">
	let myGridID;
	function _layout() {
		return [ {
			dataField : "name",
			headerText : "문서제목",
			dataType : "string",
			width : 350,
			style : "aui-left",
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "number",
			headerText : "문서번호",
			dataType : "string",
			width : 120,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "description",
			headerText : "설명",
			dataType : "string",
			style : "aui-left",
			width : 350,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "location",
			headerText : "문서분류",
			dataType : "string",
			width : 250,
			style : "aui-left",
			filter : {
				showIcon : true,
				inline : true
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
			dataField : "version",
			headerText : "버전",
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
			dataField : "createdDate",
			headerText : "작성일",
			dataType : "date",
			width : 100,
			formatString : "yyyy-mm-dd",
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
			width : 100,
			formatString : "yyyy-mm-dd",
			filter : {
				showIcon : true,
				inline : true,
				displayFormatValues : true
			},
		}, {
			dataField : "primary",
			headerText : "첨부파일",
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
				showRowCheckColumn : true,
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

	function <%=method%>	() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
		if (checkedItems.length == 0) {
			alert("추가할 문서를 선택하세요.");
			return false;
		}
		
		for(let i=0; i<checkedItems.length; i++) {
			const item = checkedItems[i].item;
			const state = item.state;
			const numberRule = item.numberRule;
			if(state !== "작업 중") {
				alert("작업 중 상태의 문서만 추가 가능합니다.");
				return false;
			}
			
			if(numberRule === "") {
				alert("도번이 없는 문서가 있습니다.");
				return false;
			}
		}
		
		openLayer();
		opener.<%=method%>(checkedItems, function(result) {
			if (result) {
				setTimeout(function() {
					closeLayer();
				}, 500);
			}
		});
	}

	function loadGridData() {
		const url = getCallUrl("/doc/list");
		const params = new Object();
		const psize = document.getElementById("psize").value;
		const oid = document.getElementById("oid").value;
		const name = document.getElementById("name").value;
		const number = document.getElementById("number").value;
		const description = document.getElementById("description").value;
		const state = document.getElementById("state").value;
		const creatorOid = document.getElementById("creatorOid").value;
		const createdFrom = document.getElementById("createdFrom").value;
		const createdTo = document.getElementById("createdTo").value;
		const latest = !!document.querySelector("input[name=latest]:checked").value;
		params.name = name;
		params.number = number;
		params.oid = oid;
		params.description = description;
		params.creatorOid = creatorOid;
		params.createdFrom = createdFrom;
		params.createdTo = createdTo;
		params.latest = latest;
		params.psize = psize;
		AUIGrid.showAjaxLoader(myGridID);
		parent.openLayer();
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			AUIGrid.setGridData(myGridID, data.list);
			document.getElementById("sessionid").value = data.sessionid;
			document.getElementById("curPage").value = data.curPage;
			parent.closeLayer();
		});
	}

	document.addEventListener("DOMContentLoaded", function() {
		const columns = loadColumnLayout("document-list");
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
		finderUser("creator");
		twindate("created");
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
		AUIGrid.resize(_myGridID);
	});
</script>