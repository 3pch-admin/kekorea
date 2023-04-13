<%@page import="e3ps.bom.partlist.PartListMaster"%>
<%@page import="wt.fc.Persistable"%>
<%@page import="e3ps.workspace.ApprovalLine"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="e3ps.workspace.dto.ApprovalLineDTO"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ApprovalLineDTO dto = (ApprovalLineDTO) request.getAttribute("dto");
Persistable per = (Persistable) request.getAttribute("per");
String poid = (String) request.getAttribute("poid");
JSONArray history = (JSONArray) request.getAttribute("history");
%>
<%@include file="/extcore/include/auigrid.jsp"%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<input type="hidden" name="poid" id="poid" value="<%=poid%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				결재 정보
			</div>
		</td>
		<td class="right">
			<%
			if (dto.isApprovalLine()) {
			%>
			<input type="button" value="승인" title="승인" onclick="_approval();">
			<input type="button" value="반려" title="반려" class="red" onclick="_reject();">
			<%
			}
			if (dto.isAgreeLine()) {
			%>
			<input type="button" value="검토완료" title="검토완료" onclick="_agree();">
			<input type="button" value="검토반려" title="검토반려" class="red" onclick="_unagree()">
			<%
			}
			%>
			<%
			if (dto.isReceiveLine()) {
			%>
			<input type="button" value="수신확인" title="수신확인" onclick="_receive();">
			<%
			}
			%>
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>
<div id="tabs">
	<ul>
		<li>
			<a href="#tabs-1">결재정보</a>
		</li>
		<li>
			<a href="#tabs-2">상세정보</a>
		</li>
	</ul>
	<div id="tabs-1">
		<table class="view-table">
			<colgroup>
				<col width="10%">
				<col width="400">
				<col width="130">
				<col width="400">
			</colgroup>
			<tr>
				<th class="lb">결재 제목</th>
				<td class="indent5" colspan="3"><%=dto.getName()%></td>
			</tr>
			<tr>
				<th class="lb">담당자</th>
				<td class="indent5"><%=dto.getCreator()%></td>
				<th class="lb">수신일</th>
				<td class="indent5"><%=dto.getReceiveTime()%></td>
			</tr>
			<tr>
				<th class="lb">구분</th>
				<td class="indent5"><%=dto.getType()%></td>
				<th class="lb">역할</th>
				<td class="indent5"><%=dto.getRole()%></td>
			</tr>
			<tr>
				<th class="lb">기안자</th>
				<td class="indent5"><%=dto.getSubmiter()%></td>
				<th class="lb">상태</th>
				<td class="indent5"><%=dto.getState()%></td>
			</tr>
			<%
			if (dto.isAgreeLine() || dto.isApprovalLine()) {
			%>
			<tr>
				<th class="lb">위임</th>
				<td class="indent5" colspan="3">
					<input type="text" name="reassignUser" id="reassignUser" data-multi="false" data-method="setUser">
					<input type="button" title="위임" value="위임" id="reassignApprovalBtn" data-oid="<%=dto.getOid()%>">
					<input type="hidden" name="reassignUserOid" id="reassignUserOid">
				</td>
			</tr>
			<%
			}
			%>
			<tr>
				<th class="lb">결재의견</th>
				<td class="indent5" colspan="3">
					<textarea name="description" id="description" rows="6"><%=dto.getDescription() != null ? dto.getDescription() : ""%></textarea>
				</td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						관련 객체
					</div>
				</td>
			</tr>
		</table>

		<jsp:include page="/extcore/jsp/workspace/include/persistable.jsp">
			<jsp:param value="<%=per.getPersistInfo().getObjectIdentifier().getStringValue()%>" name="oid" />
		</jsp:include>

		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						결재 라인
					</div>
				</td>
			</tr>
		</table>
		<div id="_grid_wrap_" style="height: 230px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let _myGridID_;
			const history =
		<%=history%>
			const _columns_ = [ {
				dataField : "type",
				headerText : "타입",
				dataType : "string",
				width : 60
			}, {
				dataField : "role",
				headerText : "역할",
				dataType : "string",
				width : 60
			}, {
				dataField : "name",
				headerText : "제목",
				dataType : "string",
				style : "aui-left",
				width : 350
			}, {
				dataField : "state",
				headerText : "상태",
				dataType : "string",
				width : 100
			}, {
				dataField : "owner",
				headerText : "담당자",
				dataType : "string",
				width : 100
			}, {
				dataField : "receiveDate_txt",
				headerText : "수신일",
				dataType : "string",
				width : 130
			}, {
				dataField : "completeDate_txt",
				headerText : "완료일",
				dataType : "string",
				width : 130
			}, {
				dataField : "description",
				headerText : "결재의견",
				dataType : "string",
				style : "aui-left",
				width : 450,
			}, ]
			function _createAUIGrid_(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : true,
					rowNumHeaderText : "번호",
					selectionMode : "singleRow",
					enableSorting : false,
					autoGridHeight : true
				}
				_myGridID_ = AUIGrid.create("#_grid_wrap_", columnLayout, props);
				AUIGrid.setGridData(_myGridID_, history);
			}
		</script>
	</div>
	<div id="tabs-2">
		<%
		// 수배표 정보
		if (per instanceof PartListMaster) {
		%>
		<jsp:include page="/extcore/jsp/workspace/partlist.jsp">
			<jsp:param value="<%=per.getPersistInfo().getObjectIdentifier().getStringValue()%>" name="oid" />
		</jsp:include>
		<%
		}
		%>
	</div>
</div>

<script type="text/javascript">
	const oid = document.getElementById("oid").value;
	const poid = document.getElementById("poid").value;

	function _receive() {
		if (!confirm("수신확인 하시겠습니까?")) {
			return false;
		}
		const url = getCallUrl("/workspace/_receive");
		const params = new Object();
		const description = document.getElementById("description").value;
		params.oid = oid;
		params.description = description;
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			}
		})
	}

	function _unagree() {
		if (!confirm("검토 반려 하시겠습니까?")) {
			return false;
		}
		const url = getCallUrl("/workspace/_unagree");
		const params = new Object();
		const description = document.getElementById("description").value;
		params.oid = oid;
		params.description = description;
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			}
		})
	}

	function _reject() {
		if (!confirm("결재 반려 하시겠습니까?")) {
			return false;
		}
		const url = getCallUrl("/workspace/_reject");
		const params = new Object();
		const description = document.getElementById("description").value;
		params.oid = oid;
		params.description = description;
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			}
		})
	}

	function _agree() {
		if (!confirm("검토완료 하시겠습니까?")) {
			return false;
		}
		const url = getCallUrl("/workspace/_agree");
		const params = new Object();
		const description = document.getElementById("description").value;
		params.oid = oid;
		params.description = description;
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			}
		})
	}

	function _approval() {

		if (!confirm("승인 하시겠습니까?")) {
			return false;
		}
		const url = getCallUrl("/workspace/_approval");
		const params = new Object();
		const description = document.getElementById("description").value;
		params.oid = oid;
		params.poid = poid;
		params.description = description;
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			}
		})
	}

	document.addEventListener("DOMContentLoaded", function() {
		document.getElementById("description").focus();
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
					if (_isCreated) {
						AUIGrid.resize(_myGridID_);
					} else {
						_createAUIGrid_(_columns);
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
		finderUser("reassignUser");
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(_myGridID_);
		AUIGrid.resize(myGridID);
	});
</script>