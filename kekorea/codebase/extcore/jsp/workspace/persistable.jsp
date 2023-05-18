<%@page import="wt.content.ContentHolder"%>
<%@page import="e3ps.bom.tbom.TBOMMasterProjectLink"%>
<%@page import="e3ps.bom.tbom.service.TBOMHelper"%>
<%@page import="e3ps.project.output.OutputProjectLink"%>
<%@page import="e3ps.project.output.service.OutputHelper"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="e3ps.korea.configSheet.service.ConfigSheetHelper"%>
<%@page import="e3ps.korea.configSheet.ConfigSheetProjectLink"%>
<%@page import="e3ps.epm.workOrder.WorkOrder"%>
<%@page import="e3ps.bom.tbom.TBOMMaster"%>
<%@page import="e3ps.korea.configSheet.ConfigSheet"%>
<%@page import="e3ps.workspace.ApprovalContract"%>
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
if (per instanceof ApprovalContract) {
	ApprovalContract contract = (ApprovalContract) per;
	ArrayList<Map<String, Object>> list = WorkspaceHelper.manager.contractData(contract);
	if (contract.getContractType().equals("EPMDOCUMENT")) {
%>
<!-- 일괄결재 도면 -->
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				도면 결재
			</div>
		</td>
	</tr>
</table>

<table class="view-table">
	<colgroup>
		<col width="250">
		<col width="*">
		<col width="100">
		<col width="100">
		<col width="100">
		<col width="100">
		<col width="100">
	</colgroup>
	<tr>
		<th class="lb">파일이름</th>
		<th class="lb">품명</th>
		<th class="lb">규격</th>
		<th class="lb">상태</th>
		<th class="lb">버전</th>
		<th class="lb">작성자</th>
		<th class="lb">작성일</th>
	</tr>
	<%
	for (Map<String, Object> map : list) {
		if (((String)map.get("oid")).indexOf("EPMDocument") > -1) {
	%>
	<tr>
		<td class="indent5">
			<a href="javascript:_detail('<%=map.get("oid")%>');"><%=map.get("name")%></a>
		</td>
		<td class="indent5">
			<a href="javascript:_detail('<%=map.get("oid")%>');"><%=map.get("nameOfParts")%></a>
		</td>
		<td class="center"><%=map.get("dwgNo")%></td>
		<td class="center"><%=map.get("state")%></td>
		<td class="center"><%=map.get("version")%></td>
		<td class="center"><%=map.get("creator")%></td>
		<td class="center"><%=map.get("createdDate_txt")%></td>
	</tr>
	<%
		}
	}
	%>
</table>

<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				도번 결재
				<a href="/Windchill/plm/workspace/print?oid=<%=oid%>" title="도면승인 요청서 다운로드">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif">
				</a>
			</div>
		</td>
	</tr>
</table>

<table class="view-table">
	<colgroup>
		<col width="80">
		<col width="250">
		<col width="100">
		<col width="*">
		<col width="100">
		<col width="100">
		<col width="100">
		<col width="100">
	</colgroup>
	<tr>
		<th class="lb">LOT</th>
		<th class="lb">UNIT NAME</th>
		<th class="lb">도번</th>
		<th class="lb">도명</th>
		<th class="lb">상태</th>
		<th class="lb">버전</th>
		<th class="lb">작성자</th>
		<th class="lb">작성일</th>
	</tr>
	<%
	for (Map<String, Object> map : list) {
		if (((String)map.get("oid")).indexOf("NumberRule") > -1) {
	%>
	<tr>
		<td class="center"><%=map.get("lotNo")%></td>
		<td class="center"><%=map.get("unitName")%></td>
		<td class="center"><%=map.get("number")%></td>
		<td class="indent5"><%=map.get("name")%></td>
		<td class="center"><%=map.get("state")%></td>
		<td class="center"><%=map.get("version")%></td>
		<td class="center"><%=map.get("creator")%></td>
		<td class="center"><%=map.get("createdDate_txt")%></td>
	</tr>
	<%
		}
	}
	%>
</table>

<%
	} else if(contract.getContractType().equals("OUTPUT")) {
%>
<!-- 작번 산출물 문서 -->
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				작번 산출물 일괄 결재
			</div>
		</td>
	</tr>
</table>

<table class="view-table">
	<colgroup>
		<col width="100">
		<col width="130">
		<col width="100">
		<col width="*">
		<col width="100">
		<col width="100">
		<col width="100">
		<col width="100">
		<col width="130">
		<col width="160">
	</colgroup>
	<tr>
		<th class="lb">KEK 작번</th>
		<th class="lb">태스크 명</th>
		<th class="lb">산출물 타입</th>
		<th class="lb">산출물 제목</th>
		<th class="lb">상태</th>
		<th class="lb">버전</th>
		<th class="lb">작성자</th>
		<th class="lb">작성일</th>
		<th class="lb">주 첨부파일</th>
		<th class="lb">첨부파일</th>
	</tr>
	<%
	for (Map<String, Object> map : list) {
		ContentHolder holder = (ContentHolder) map.get("contentHolder");
		Map<String, Object> primary = ContentUtils.getPrimary(holder);
		Vector<Map<String, Object>> secondarys = ContentUtils.getSecondary(holder);
	%>
	<tr>
		<td class="center"><%=map.get("kekNumber")%></td>
		<td class="center"><%=map.get("taskName")%></td>
		<td class="center"><%=map.get("type")%></td>
		<td class="indent5"><%=map.get("name")%></td>
		<td class="center"><%=map.get("state")%></td>
		<td class="center"><%=map.get("version")%></td>
		<td class="center"><%=map.get("creator")%></td>
		<td class="center"><%=map.get("createdDate_txt")%></td>
		<td class="center">
			<%
				if(primary != null) {
			%>
			<a href="javascript:download('<%=primary.get("aoid")%>');">
				<img src="<%=primary.get("fileIcon")%>">
			</a>
			<%
				}
			%>
		</td>
		<td class="center">
		<%
			for(Map<String, Object> secondary : secondarys) {
		%>
			<a href="javascript:download('<%=secondary.get("aoid")%>');">
				<img src="<%=secondary.get("fileIcon")%>">
			</a>
		<%
			}
		%>		
		</td>
	</tr>
	<%
	}
	%>
</table>

<%
} else {
%>
<!-- 일괄결재 문서 -->
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				문서 결재
			</div>
		</td>
	</tr>
</table>

<table class="view-table">
	<colgroup>
		<col width="100">
		<col width="*">
		<col width="100">
		<col width="100">
		<col width="100">
		<col width="100">
	</colgroup>
	<tr>
		<th class="lb">문서번호</th>
		<th class="lb">문서명</th>
		<th class="lb">상태</th>
		<th class="lb">버전</th>
		<th class="lb">작성자</th>
		<th class="lb">작성일</th>
	</tr>
	<%
	for (Map<String, Object> map : list) {
	%>
	<tr>
		<td class="center">
			<a href="javascript:_detail('<%=map.get("oid")%>');"><%=map.get("number")%></a>
		</td>
		<td class="indent5">
			<a href="javascript:_detail('<%=map.get("oid")%>');"><%=map.get("name")%></a>
		</td>
		<td class="center"><%=map.get("state")%></td>
		<td class="center"><%=map.get("version")%></td>
		<td class="center"><%=map.get("creator")%></td>
		<td class="center"><%=map.get("createdDate_txt")%></td>
	</tr>
	<%
	}
	%>
</table>
<%
}
}
%>


<%
if (per instanceof PartListMaster) {
	PartListMaster mm = (PartListMaster) per;
	String moid = mm.getPersistInfo().getObjectIdentifier().getStringValue();
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
		<td class="indent5">
			<a href="javascript:_detail('<%=moid%>');"><%=mm.getName()%></a>
		</td>
		<td class="center"><%=mm.getLifeCycleState().getDisplay()%></td>
		<td class="center">
			<img src="/Windchill/extcore/images/details.gif" onclick="_detail('<%=moid%>');">
		</td>
		<td class="center"><%=mm.getCreatorFullName()%></td>
		<td class="center"><%=CommonUtils.getPersistableTime(mm.getCreateTimestamp())%></td>
		<td class="center"></td>
	</tr>
</table>

<jsp:include page="/extcore/jsp/workspace/project.jsp">
	<jsp:param value="<%=moid%>" name="oid" />
</jsp:include>

<%
} else if (per instanceof RequestDocument) {
RequestDocument requestDocument = (RequestDocument) per;
String roid = requestDocument.getPersistInfo().getObjectIdentifier().getStringValue();
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
		<td class="indent5">
			<a href="javascript:detail();"><%=requestDocument.getName()%></a>
		</td>
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

<jsp:include page="/extcore/jsp/workspace/project.jsp">
	<jsp:param value="<%=roid%>" name="oid" />
</jsp:include>

<%
} else if (per instanceof ConfigSheet) {
ConfigSheet configSheet = (ConfigSheet) per;
String coid = configSheet.getPersistInfo().getObjectIdentifier().getStringValue();
%>
<!-- CONFIG SHEET -->
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				CONFIG SHEET
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
	</colgroup>
	<tr>
		<th class="lb">CONFIG SHEET 제목</th>
		<th class="lb">상태</th>
		<th class="lb">작성자</th>
		<th class="lb">작성일</th>
	</tr>
	<tr>
		<td class="indent5">
			<a href="javascript:_detail('<%=coid%>');"><%=configSheet.getName()%></a>
		</td>
		<td class="center"><%=configSheet.getLifeCycleState().getDisplay()%></td>
		<td class="center"><%=configSheet.getCreatorFullName()%></td>
		<td class="center"><%=CommonUtils.getPersistableTime(configSheet.getCreateTimestamp())%></td>
	</tr>
</table>

<jsp:include page="/extcore/jsp/workspace/project.jsp">
	<jsp:param value="<%=coid%>" name="oid" />
</jsp:include>

<%
} else if (per instanceof TBOMMaster) {
TBOMMaster master = (TBOMMaster) per;
String toid = master.getPersistInfo().getObjectIdentifier().getStringValue();
%>
<!-- T-BOM -->
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				T-BOM
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
		<col width="100">
	</colgroup>
	<tr>
		<th class="lb">T-BOM 제목</th>
		<th class="lb">버전</th>
		<th class="lb">상태</th>
		<th class="lb">작성자</th>
		<th class="lb">작성일</th>
	</tr>
	<tr>
		<td class="indent5">
			<a href="javascript:_detail('<%=toid%>');"><%=master.getName()%></a>
		</td>
		<td class="center"><%=master.getVersion()%></td>
		<td class="center"><%=master.getLifeCycleState().getDisplay()%></td>
		<td class="center"><%=master.getCreatorFullName()%></td>
		<td class="center"><%=CommonUtils.getPersistableTime(master.getCreateTimestamp())%></td>
	</tr>
</table>

<jsp:include page="/extcore/jsp/workspace/project.jsp">
	<jsp:param value="<%=toid%>" name="oid" />
</jsp:include>

<%
} else if (per instanceof WorkOrder) {
WorkOrder workOrder = (WorkOrder) per;
String woid = workOrder.getPersistInfo().getObjectIdentifier().getStringValue();
%>
<!-- 도면 일람표 -->
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				도면 일람표
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
		<col width="100">
	</colgroup>
	<tr>
		<th class="lb">도면 일람표 제목</th>
		<th class="lb">버전</th>
		<th class="lb">상태</th>
		<th class="lb">작성자</th>
		<th class="lb">작성일</th>
	</tr>
	<tr>
		<td class="indent5">
			<a href="javascript:_detail('<%=woid%>');"><%=workOrder.getName()%></a>
		</td>
		<td class="center"><%=workOrder.getVersion()%></td>
		<td class="center"><%=workOrder.getLifeCycleState().getDisplay()%></td>
		<td class="center"><%=workOrder.getCreatorFullName()%></td>
		<td class="center"><%=CommonUtils.getPersistableTime(workOrder.getCreateTimestamp())%></td>
	</tr>
</table>

<jsp:include page="/extcore/jsp/workspace/project.jsp">
	<jsp:param value="<%=woid%>" name="oid" />
</jsp:include>
<%
} else if (per instanceof WTDocument) {
WTDocument document = (WTDocument) per;
String doid = document.getPersistInfo().getObjectIdentifier().getStringValue();
Map<String, Object> primary = ContentUtils.getPrimary(document);
Vector<Map<String, Object>> secondarys = ContentUtils.getSecondary(document);
%>
<!-- 산출물 -->
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				산출물
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
		<col width="100">
		<col width="130">
		<col width="160">		
	</colgroup>
	<tr>
		<th class="lb">산출물 제목</th>
		<th class="lb">버전</th>
		<th class="lb">상태</th>
		<th class="lb">작성자</th>
		<th class="lb">작성일</th>
		<th class="lb">주 첨부파일</th>
		<th class="lb">첨부파일</th>		
	</tr>
	<tr>
		<td class="indent5">
			<a href="javascript:_detail('<%=doid%>');"><%=document.getName()%></a>
		</td>
		<td class="center"><%=CommonUtils.getFullVersion(document)%></td>
		<td class="center"><%=document.getLifeCycleState().getDisplay()%></td>
		<td class="center"><%=document.getCreatorFullName()%></td>
		<td class="center"><%=CommonUtils.getPersistableTime(document.getCreateTimestamp())%></td>
		<td class="center">
			<a href="javascript:download('<%=primary.get("aoid")%>');">
				<img src="<%=primary.get("fileIcon")%>">
			</a>
		</td>
		<td class="center">
		<%
			for(Map<String, Object> secondary : secondarys) {
		%>
			<a href="javascript:download('<%=secondary.get("aoid")%>');">
				<img src="<%=secondary.get("fileIcon")%>">
			</a>
		<%
			}
		%>		
		</td>		
	</tr>
</table>



<jsp:include page="/extcore/jsp/workspace/project.jsp">
	<jsp:param value="<%=doid%>" name="oid" />
</jsp:include>
<%
}
%>
<script type="text/javascript">
	function _detail(oid) {
		let url = "/Windchill/plm";
		if (oid.indexOf("Project") > -1) {
			url += "/project/info?oid=" + oid;
			popup(url);
		} else if (oid.indexOf("PartListMaster") > -1) {
			url += "/partlist/view?oid=" + oid;
			popup(url, 1700, 800);
		} else if (oid.indexOf("EPMDocument") > -1) {
			url += "/epm/view?oid=" + oid;
			popup(url, 1400, 600);
		} else if (oid.indexOf("TBOMMaster") > -1) {
			url += "/tbom/view?oid=" + oid;
			popup(url, 1500, 700);
		} else if (oid.indexOf("WorkOrder") > -1) {
			url += "/workOrder/view?oid=" + oid;
			popup(url, 1600, 800);
		}
	}
</script>
