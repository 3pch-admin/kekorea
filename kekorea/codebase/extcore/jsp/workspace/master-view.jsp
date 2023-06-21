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
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=1010"></script>
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
				<col width="10%">
				<col width="300">
				<col width="130">
				<col width="300">
				<col width="130">
				<col width="300">
			</colgroup>
			<tr>
				<th class="lb">결재 제목</th>
				<td class="indent5" colspan="5"><%=dto.getName()%></td>
			</tr>
			<tr>
				<th class="lb">수신일</th>
				<td class="indent5"><%=dto.getReceiveTime()%></td>
				<th class="lb">기안자</th>
				<td class="indent5"><%=dto.getSubmiter()%></td>
				<th class="lb">상태</th>
				<td class="indent5"><%=dto.getState()%></td>
			</tr>
			<tr>
				<th class="lb">결재의견</th>
				<td class="indent5" colspan="5">
					<textarea name="description" id="description" rows="6" readonly="readonly"><%=dto.getDescription() != null ? dto.getDescription() : ""%></textarea>
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
	document.addEventListener("DOMContentLoaded", function() {
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
					const isCreated = AUIGrid.isCreated(columns);
					if (isCreated) {
						AUIGrid.resize(myGridID);
					} else {
						createAUIGrid(columns);
					}
					break;
				}
			}
		});
		createAUIGrid100(columns100);
		AUIGrid.resize(myGridID100);
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID100);
		AUIGrid.resize(myGridID);
	});
</script>