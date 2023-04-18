<%@page import="java.util.Map"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="e3ps.epm.keDrawing.dto.KeDrawingDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
KeDrawingDTO dto = (KeDrawingDTO) request.getAttribute("dto");
JSONArray history = (JSONArray) request.getAttribute("history");
%>
<%@include file="/extcore/include/auigrid.jsp"%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				KE 도면 정보
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
				<col width="10%">
				<col width="300">
				<col width="130">
				<col width="300">
				<col width="200">
			</colgroup>
			<tr>
				<th class="lb">DRAWING TITLE</th>
				<td class="indent5"><%=dto.getName()%></td>
				<th>DWG NO</th>
				<td class="indent5"><%=dto.getKeNumber()%></td>
				<td class="center" rowspan="5">
					<%
					if (!StringUtils.isNull(dto.getPreView())) {
					%>
					<img src="<%=dto.getPreView()%>" style="height: 140px; cursor: pointer;" onclick="preView();" title="클릭시 원본크기로 볼 수 있습니다.">
					<%
					}
					%>
				</td>
			</tr>
			<tr>
				<th class="lb">LOT NO</th>
				<td class="indent5"><%=dto.getLotNo()%></td>
				<th>버전</th>
				<td class="indent5"><%=dto.getVersion()%></td>
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
				<th class="lb">도면파일</th>
				<td class="indent5">
					<jsp:include page="/extcore/jsp/common/primary-view.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
					</jsp:include>
				</td>
				<th class="lb">개정사유</th>
				<td class="indent5">
					<%=dto.getNote() != null ? dto.getNote() : ""%>
				</td>
			</tr>
			<tr>
				<th class="lb">도면일람표</th>
				<td class="indent5" colspan="4">
					<jsp:include page="/extcore/jsp/common/project-reference.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
						<jsp:param value="도면일람표 제목" name="header" />
					</jsp:include>
				</td>
			</tr>
		</table>
	</div>
	<div id="tabs-2"></div>
	<div id="tabs-3">
		<!-- 결재이력 -->
		<jsp:include page="/extcore/jsp/common/approval-history.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
		</jsp:include>
	</div>
</div>
<script type="text/javascript">
	function preView() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/aui/thumbnail?oid=" + oid);
		popup(url);
	}

	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0,
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-1":
					const isCreated50 = AUIGrid.isCreated(myGridID50);
					if (isCreated50) {
						AUIGrid.resize(myGridID50);
					} else {
						createAUIGrid50(columns50);
					}
					break;
				case "tabs-2":
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
		createAUIGrid50(columns50);
		createAUIGrid100(columns100);
		AUIGrid.resize(myGridID50);
		AUIGrid.resize(myGridID100);
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID50);
		AUIGrid.resize(myGridID100);
	});
</script>