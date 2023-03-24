<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="wt.org.WTUser"%>
<%@page import="e3ps.doc.dto.DocumentDTO"%>
<%@page import="e3ps.project.dto.ProjectDTO"%>
<%@include file="/extcore/include/auigrid.jsp"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
DocumentDTO dto = (DocumentDTO) request.getAttribute("dto");
ProjectDTO pdto = (ProjectDTO) request.getAttribute("pdto");
%>
<input type="hidden" name="isAdmin" id="isAdmin" value="<%=isAdmin%>">
<input type="hidden" name="oid" id="oid">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				문서 정보
			</div>
		</td>
		<td class="right">
			<input type="button" value="개정" id="reviseBtn" title="개정" class="blue">
			<input type="button" value="수정" id="modifyDocBtn" title="수정">
			<%
			if (isAdmin) {
			%>
			<input type="button" value="삭제" id="deleteDocBtn" title="삭제" class="red">
			<%
			}
			%>
			<input type="button" value="결재이력" data-oid="" class="infoApprovalHistory" id="infoApprovalHistory" title="결재이력">
			<input type="button" value="버전정보" id="infoVersionBtn" title="버전정보">
			<input type="button" value="닫기" id="closeDocBtn" title="닫기" class="red" onclick="self.close();">
			<input type="button" value="뒤로" id="b" title="뒤로" class="blue">
		</td>
	</tr>
</table>
<div id="tabs">
	<ul>
		<li>
			<a href="#tabs-1">기본정보</a>
		</li>
	</ul>
	<div id="tabs-1">
		<table class="view-table">
			<colgroup>
				<col width="130">
				<col width="800">
				<col width="130">
				<col width="800">
			</colgroup>
			<tr>
				<th class="lb">문서제목</th>
				<td class="indent5"><%=dto.getName()%></td>
				<th class="lb">문서번호</th>
				<td class="indent5"><%=dto.getNumber()%></td>
			</tr>
			<tr>
				<th class="lb">버전</th>
				<td class="indent5"><%=dto.getVersion()%></td>
				<th class="lb">상태</th>
				<td class="indent5"><%=dto.getState()%></td>
			</tr>
			<tr>
				<th class="lb">작성자</th>
				<td class="indent5"><%=dto.getCreator()%></td>
				<th class="lb">작성일</th>
				<td class="indent5"><%=dto.getCreatedDate()%></td>
			</tr>
			<tr>
				<th class="lb">수정자</th>
				<td class="indent5"><%=dto.getModifier()%></td>
				<th class="lb">수정일</th>
				<td class="indent5"><%=dto.getModifiedDate()%></td>
			</tr>
			<tr>
				<th class="lb">저장위치</th>
				<td class="indent5" colspan="3"><%=dto.getLocation()%></td>
			</tr>
			<tr>
				<th class="lb">설명</th>
				<td class="indent5" colspan="3"><%=dto.getDescription()%></td>
			</tr>
			<tr>
				<th class="lb">관련부품</th>
				<td class="indent5" colspan="3">
					<%-- 			<jsp:include page="/extcore/include/part-include.jsp"> --%>
					<%-- 				<jsp:param value="<%=dto.getOid()%>" name="oid" /> --%>
					<%-- 				<jsp:param value="view" name="mode" /> --%>
					<%-- 				<jsp:param value="true" name="multi" /> --%>
					<%-- 				<jsp:param value="part" name="obj" /> --%>
					<%-- 				<jsp:param value="150" name="height" /> --%>
					<%-- 			</jsp:include> --%>
					<%-- <%=dto.getOid() %> --%>
				</td>
			</tr>
			<tr>
				<th class="lb">주 첨부파일</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/include/attachment-view.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
						<jsp:param value="primary" name="mode" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/include/attachment-view.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
						<jsp:param value="secondary" name="mode" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">연관된 프로젝트</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/include/project-include.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
						<jsp:param value="view" name="mode" />
						<jsp:param value="true" name="multi" />
						<jsp:param value="document" name="obj" />
						<jsp:param value="180" name="height" />
					</jsp:include>
				</td>
			</tr>
		</table>
	</div>
</div>
<script type="text/javascript">
document.addEventListener("DOMContentLoaded", function() {
	$("#tabs").tabs();
});
</script>