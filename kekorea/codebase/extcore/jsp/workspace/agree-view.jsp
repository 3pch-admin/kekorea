<%@page import="e3ps.workspace.ApprovalLine"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="e3ps.workspace.dto.ApprovalLineDTO"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ApprovalLineDTO dto = (ApprovalLineDTO) request.getAttribute("dto");
%>
<%@include file="/extcore/include/auigrid.jsp"%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
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
		<li>
			<a href="#tabs-2">결재객체</a>
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
				<td class="indent5"><%=dto.getReceiveTime().toString().substring(0, 10)%></td>
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
			<tr>
				<th class="lb">위임</th>
				<td class="indent5" colspan="3">
					<input type="text" name="reassignUser" id="reassignUser" data-multi="false" data-method="setUser">
					<input type="button" title="위임" value="위임" id="reassignApprovalBtn" data-oid="<%=dto.getOid()%>">
					<input type="hidden" name="reassignUserOid" id="reassignUserOid">
				</td>
				<tr>
				<th class="lb">결재의견</th>
				<td class="indent5" colspan="3">
					<textarea name="descriptionAgree" id="descriptionAgree" rows="6" cols="" readonly="readonly"><%=dto.getDescription()%></textarea>
				</td>
			</tr>
		</table>
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						결재 라인
					</div>
			</tr>
		</table>
		<table class="view-table">
			<colgroup>
				<col width="100">
				<col width="100">
				<col width="500">
				<col width="100">
				<col width="100">
				<col width="100">
				<col width="100">
				<col width="300">
			</colgroup>
			<tr>
				<th class="lb">구분</th>
				<th class="lb">역할</th>
				<th class="lb">결재제목</th>
				<th class="lb">상태</th>
				<th class="lb">담당자</th>
				<th class="lb">수신일</th>
				<th class="lb">완료일</th>
				<th class="lb">결재의견</th>
			</tr>
			<tr align="center">
				<td class="indent5"><%=dto.getType()%></td>
				<td class="indent5"><%=dto.getRole()%></td>
				<td class="indent5"><%=dto.getName()%></td>
				<td class="indent5"><%=dto.getState()%></td>
				<td class="indent5"><%=dto.getCreator()%></td>
				<td class="indent5"><%=dto.getReceiveTime().toString().substring(0, 10)%></td>
<%-- 				<td class="indent5"><%=dto.getCompleteTime().toString().substring(0, 10) %></td> --%>
				<td class="indent5"></td>
				<td class="indent5"><%=dto.getDescription()%></td>
			</tr>
		</table>
	</div>
	<div id="tabs-2">
		<table class="view-table">
			<colgroup>
				<col width="200">
				<col width="400">
				<col width="100">
				<col width="100">
				<col width="100">
				<col width="200">
				<col width="100">
			</colgroup>
			<tr>
				<th class="lb">문서번호</th>
				<th class="lb">문서제목</th>
				<th class="lb">상태</th>
				<th class="lb">버전</th>
				<th class="lb">수정자</th>
				<th class="lb">수정일</th>
				<th class="lb">첨부파일</th>
			</tr>
			<tr>
				<td class="indent5"></td>
				<td class="indent5"></td>
				<td class="indent5"></td>
				<td class="indent5"></td>
				<td class="indent5"></td>
				<td class="indent5"></td>
				<td class="indent5"></td>
			</tr>
		</table>
	</div>
</div>

<script type="text/javascript">
	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0,
			create : function(event, ui) {
				const tabId = ui.panel.prop("id");
				switch (tabId) {
				case "tabs-1":
					selectbox("reassignUser");
					_createAUIGrid(_columns);
					AUIGrid.resize(_myGridID);

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
					const _isCreated = AUIGrid.isCreated(_myGridID);
					if (_isCreated) {
						AUIGrid.resize(_myGridID);
					} else {
						_createAUIGrid(_columns);
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
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
		AUIGrid.resize(_myGridID);
	});
</script>