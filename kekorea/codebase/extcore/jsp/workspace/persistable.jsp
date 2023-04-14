<%@page import="e3ps.bom.partlist.service.PartlistHelper"%>
<%@page import="e3ps.bom.partlist.PartListMasterProjectLink"%>
<%@page import="e3ps.project.Project"%>
<%@page import="e3ps.doc.request.RequestDocumentProjectLink"%>
<%@page import="e3ps.doc.request.service.RequestDocumentHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.doc.request.RequestDocument"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="java.util.Vector"%>
<%@page import="e3ps.workspace.service.WorkspaceHelper"%>
<%@page import="e3ps.bom.partlist.PartListMaster"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@page import="wt.fc.Persistable"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
Persistable per = (Persistable) CommonUtils.getObject(oid);
%>


<!-- 일괄결재 문서 & 도면 -->
<%
if (per instanceof PartListMaster) {
	PartListMaster mm = (PartListMaster) per;
	String _oid = mm.getPersistInfo().getObjectIdentifier().getStringValue();
	Vector<String[]> secondarys = ContentUtils.getSecondary(mm);
%>
<!-- 수배표 -->
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				수배표
			</div>
		</td>
	</tr>
</table>

<table class="view-table">
	<colgroup>
		<col width="*">
		<col width="100">
		<col width="80">
		<col width="100">
		<col width="100">
		<col width="130">
	</colgroup>
	<tr>
		<th class="lb">수배표 제목</th>
		<th class="lb">상태</th>
		<th class="lb">상세정보</th>
		<th class="lb">작성자</th>
		<th class="lb">작성일</th>
		<th class="lb">첨부파일</th>
	</tr>
	<tr>
		<td class="indent5"><%=mm.getName()%></td>
		<td class="center"><%=mm.getLifeCycleState().getDisplay()%></td>
		<td class="center">
			<img src="/Windchill/extcore/images/details.gif" onclick="_detail('<%=_oid%>');">
		</td>
		<td class="center"><%=mm.getCreatorFullName()%></td>
		<td class="center"><%=CommonUtils.getPersistableTime(mm.getCreateTimestamp())%></td>
		<td class="center">
			<%
			for (String[] secondary : secondarys) {
			%>
			<a href="<%=secondary[5]%>">
				<img src="<%=secondary[4]%>" class="pos2">
			</a>
			<%
			}
			%>
		</td>
	</tr>
</table>

<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				관련 작번
			</div>
		</td>
	</tr>
</table>

<table class="view-table">
	<colgroup>
		<col width="80">
		<col width="100">
		<col width="100">
		<col width="120">
		<col width="120">
		<col width="120">
		<col width="120">
		<col width="*">
	</colgroup>
	<tr>
		<th class="lb">작번유형</th>
		<th class="lb">KEK 작번</th>
		<th class="lb">KE 작번</th>
		<th class="lb">고객사</th>
		<th class="lb">설치장소</th>
		<th class="lb">막종</th>
		<th class="lb">막종상세</th>
		<th class="lb">작업내용</th>
	</tr>
	<%
	ArrayList<PartListMasterProjectLink> list = PartlistHelper.manager.getLinks(mm);
	for (PartListMasterProjectLink link : list) {
		Project project = link.getProject();
	%>
	<tr>
		<td class="center"><%=project.getProjectType().getName()%></td>
		<td class="center"><%=project.getKekNumber()%></td>
		<td class="center"><%=project.getKeNumber()%></td>
		<td class="center"><%=project.getCustomer().getName()%></td>
		<td class="center"><%=project.getInstall().getName()%></td>
		<td class="center"><%=project.getMak().getName()%></td>
		<td class="center"><%=project.getDetail().getName()%></td>
		<td class="indent5"><%=project.getDescription()%></td>
	</tr>
	<%
	}
	%>
</table>

<%
} else if (per instanceof RequestDocument) {
RequestDocument requestDocument = (RequestDocument) per;
Map<String, Object> primary = ContentUtils.getPrimary(requestDocument);
%>
<!-- 의뢰서 -->
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				의뢰서
			</div>
		</td>
	</tr>
</table>

<table class="view-table">
	<colgroup>
		<col width="*">
		<col width="100">
		<col width="100">
		<col width="100">
		<col width="130">
	</colgroup>
	<tr>
		<th class="lb">의뢰서 제목</th>
		<th class="lb">상태</th>
		<th class="lb">작성자</th>
		<th class="lb">작성일</th>
		<th class="lb">첨부파일</th>
	</tr>
	<tr>
		<td class="indent5"><%=requestDocument.getName()%></td>
		<td class="center"><%=requestDocument.getLifeCycleState().getDisplay()%></td>
		<td class="center"><%=requestDocument.getCreatorFullName()%></td>
		<td class="center"><%=CommonUtils.getPersistableTime(requestDocument.getCreateTimestamp())%></td>
		<td class="center">
			<a href="javascript:download('<%=primary.get("aoid")%>');">
				<img src="<%=primary.get("fileIcon")%>">
			</a>
		</td>
	</tr>
</table>

<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				관련 작번
			</div>
		</td>
	</tr>
</table>

<table class="view-table">
	<colgroup>
		<col width="80">
		<col width="100">
		<col width="100">
		<col width="120">
		<col width="120">
		<col width="120">
		<col width="120">
		<col width="*">
	</colgroup>
	<tr>
		<th class="lb">작번유형</th>
		<th class="lb">KEK 작번</th>
		<th class="lb">KE 작번</th>
		<th class="lb">고객사</th>
		<th class="lb">설치장소</th>
		<th class="lb">막종</th>
		<th class="lb">막종상세</th>
		<th class="lb">작업내용</th>
	</tr>
	<%
	ArrayList<RequestDocumentProjectLink> list = RequestDocumentHelper.manager.getLinks(requestDocument);
	for (RequestDocumentProjectLink link : list) {
		Project project = link.getProject();
	%>
	<tr>
		<td class="center"><%=project.getProjectType().getName()%></td>
		<td class="center"><%=project.getKekNumber()%></td>
		<td class="center"><%=project.getKeNumber()%></td>
		<td class="center"><%=project.getCustomer().getName()%></td>
		<td class="center"><%=project.getInstall().getName()%></td>
		<td class="center"><%=project.getMak().getName()%></td>
		<td class="center"><%=project.getDetail().getName()%></td>
		<td class="indent5"><%=project.getDescription()%></td>
	</tr>
	<%
	}
	}
	%>
</table>
<script type="text/javascript">
	function _detail(oid) {

	}
</script>
