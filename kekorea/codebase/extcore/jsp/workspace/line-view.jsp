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
%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>    
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
	</ul>
	<div id="tabs-1">
		<table class="view-table">
			<colgroup>
				<col width="130">
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
					<input type="text" name="reassignUser" id="reassignUser">
					<input type="hidden" name="reassignUserOid" id="reassignUserOid">
					<input type="button" title="위임" value="위임" onclick="reassign();">
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

		<jsp:include page="/extcore/jsp/workspace/persistable.jsp">
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
		<!-- 결재이력 -->
		<jsp:include page="/extcore/jsp/common/approval-history.jsp">
			<jsp:param value="<%=dto.getPoid()%>" name="oid" />
		</jsp:include>
	</div>
</div>

<script type="text/javascript">
	const oid = document.getElementById("oid").value;
	const poid = document.getElementById("poid").value;

	function reassign() {
		const reassignUser = document.getElementById("reassignUser");
		const reassignUserOid = document.getElementById("reassignUserOid").value;
		if (isNull(reassignUser.value)) {
			alert("해당 결재를 위임할 사용자를 선택하세요.");
			return false;
		}

		if (!confirm(reassignUser.value + " 사용자에게 결재를 위임하시겠습니까?")) {
			return false;
		}

		const url = getCallUrl("/workspace/reassign");
		const params = new Object();
		params.reassignUserOid = reassignUserOid;
		params.oid = oid;
		openLayer();
		call(url, params, function(data) {
			alert(reassignUser.value + "사용자에게 " + data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				closeLayer();
			}
		})
	}

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
			} else {
				closeLayer();
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
			} else {
				closeLayer();
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
			} else {
				closeLayer();
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
			} else {
				closeLayer();
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
			} else {
				closeLayer();
			}
		})
	}

	document.addEventListener("DOMContentLoaded", function() {
		document.getElementById("description").focus();
		$("#tabs").tabs({
			active : 0,
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-1":
					const isCreated100 = AUIGrid.isCreated(myGridID100);
					if (isCreated100) {
						AUIGrid.resize(myGridID100);
					} else {
						createAUIGrid100(columns100);
					}
					break;
				}
			}
		});
		createAUIGrid100(columns100);
		AUIGrid.resize(myGridID100);
		finderUser("reassignUser");
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID100);
	});
</script>