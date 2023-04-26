<%@page import="e3ps.project.output.dto.OutputDTO"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTUser"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
OutputDTO dto = (OutputDTO) request.getAttribute("dto");
%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
<input type="hidden" name="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				산출물 정보
			</div>
		</td>
		<td class="right">
			<input type="button" value="수정" title="수정" onclick="modify();">
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
			<a href="#tabs-2">버전정보</a>
		</li>
		<li>
			<a href="#tabs-3">결재이력</a>
		</li>
	</ul>
	<div id="tabs-1">
		<table class="view-table">
			<colgroup>
				<col width="150">
				<col width="500">
				<col width="150">
				<col width="500">
			</colgroup>
			<tr>
				<th class="lb">산출물 제목</th>
				<td class="indent5"><%=dto.getName()%></td>
				<th>진행율</th>
				<td class="indent5"><%=dto.getNumber()%></td>
			</tr>
			<tr>
				<th class="lb">작성자</th>
				<td class="indent5"><%=dto.getCreator()%></td>
				<th>작성일</th>
				<td class="indent5"><%=dto.getCreatedDate_txt()%></td>
			</tr>
			<tr>
				<th class="lb">수정자</th>
				<td class="indent5"><%=dto.getModifier()%></td>
				<th>수정일</th>
				<td class="indent5"><%=dto.getModifiedDate_txt()%></td>
			</tr>
			<tr>
				<th class="lb">상태</th>
				<td class="indent5"><%=dto.getState()%></td>
				<th>버전</th>
				<td class="indent5"><%=dto.getVersion()%></td>
			</tr>
			<tr>
				<th class="lb">저장위치</th>
				<td class="indent5" colspan="3"><%=dto.getLocation()%></td>
			</tr>
			<tr>
				<th class="req lb">KEK 작번</th>
				<td colspan="5">
					<jsp:include page="/extcore/jsp/common/project-include.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
						<jsp:param value="view" name="mode" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">내용</th>
				<td class="indent5" colspan="3">
					<textarea name="description" id="description" rows="6" readonly="readonly"><%=dto.getDescription() != null ? dto.getDescription() : ""%></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">주 첨부파일</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/common/primary-view.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/common/secondary-view.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
					</jsp:include>
				</td>
			</tr>
		</table>
	</div>
	<div id="tabs-3">
		<!-- 결재이력 -->
		<jsp:include page="/extcore/jsp/common/approval-history.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
		</jsp:include>
	</div>
</div>
<script type="text/javascript">
	function modify() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/output/modify?oid=" + oid);
		document.location.href = url;
	}

	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0,
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-1":
					const isCreated9 = AUIGrid.isCreated(myGridID9);
					if (isCreated9) {
						AUIGrid.resize(myGridID9);
					} else {
						createAUIGrid9(columns9);
					}
					break;
				case "tabs-2":
					const isCreated_ = AUIGrid.isCreated(myGridID_);
					if (isCreated_) {
						AUIGrid.resize(myGridID_);
					} else {
						createAUIGrid_(columns_);
					}
					break;
				case "tabs-3":
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
		createAUIGrid9(columns9);
		createAUIGrid100(columns100);
		AUIGrid.resize(myGridID9);
		AUIGrid.resize(myGridID100);
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID9);
		AUIGrid.resize(myGridID100);
	});
</script>