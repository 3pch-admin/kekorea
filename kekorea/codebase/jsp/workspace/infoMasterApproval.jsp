<%@page import="wt.doc.WTDocument"%>
<%@page import="wt.fc.Persistable"%>
<%@page import="e3ps.workspace.ApprovalContract"%>
<%@page import="wt.epm.EPMDocument"%>
<%@page import="e3ps.partlist.PartListMaster"%>
<%@page import="e3ps.approval.beans.ApprovalMasterViewData"%>
<%@page import="e3ps.approval.service.ApprovalHelper"%>
<%@page import="e3ps.approval.beans.ApprovalLineViewData"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	// popup check
	String popup = (String) request.getParameter("popup");
	boolean isPopup = Boolean.parseBoolean(popup);
	// data
	ApprovalMasterViewData mdata = (ApprovalMasterViewData) request.getAttribute("data");
	ApprovalLineViewData data = null;
	String btnKey = "";
	if (mdata.returnData != null) {
		data = mdata.returnData;
		btnKey = "listReturn";
	} else if (mdata.ingData != null) {
		data = mdata.ingData;
		btnKey = "listIng";
	}
	if(data == null) {
// 		data = mdata;
	}
	// isAdmin
	boolean isAdmin = CommonUtils.isAdmin();
	
// 	boolean isLastAppLine = ApprovalHelper.manager.isLastAppLine(mdata.approvalMaster, data.sort);
%>

<td valign="top">
	<input type="hidden" name="oid" id="oid" value="<%=mdata != null ? mdata.oid : "" %>">
	<input type="hidden" name="popup" id="popup" value="<%=isPopup %>">

	<table class="btn_table">
		<tr>
			<td>
				<div class="header_title">
					<i class="axi axi-subtitles"></i><span>결재정보</span>
				</div>
			</td>
			<td>
	
	<%
		if(isPopup) {
			
	%>
	<div class="right">
		<%
		if( WorkspaceHelper.MASTER_RETURN.equals(data.state)){
		%>
		<input type="button" value="반려정보삭제" id="deleteReturn" title="반려정보삭제" class="redBtn">
		<%
		}
		%>
		<%
		if(data.per instanceof ApprovalContract) {
				String poid = data.per.getPersistInfo().getObjectIdentifier().getStringValue();
				String moid = data.oid;
		%>
		<input type="button" value="수정" id="modifyApprovalEpm" title="수정" data-oid="<%=poid%>" data-moid="<%=moid%>">
		<%
		}
		%>
		<input type="button" value="닫기" id="closeDocBtn" title="닫기" class="redBtn">
	</div>
	<%
	}
	%>
	</td></tr></table>
	<table class="view_table">
		<colgroup>
			<col width="250">
			<col width="600">
			<col width="250">
			<col width="600">
		</colgroup>
		<tr>
			<th>결재 제목</th>
			<td colspan="3"><%=data.name%></td>
		</tr>
		<tr>
			<th>담당자</th>
			<td><%=data.creator%></td>
			<th>수신일</th>
			<td><%=data.startTime%></td>			
		</tr>		
		<tr>
			<th>구분</th>
			<td><%=data.type%></td>
			<th>역할</th>
			<td><%=data.role%></td>			
		</tr>	
		<tr>
			<th>기안자</th>
			<td><%=data.submiter%></td>
			<th>상태</th>
			<td><%=data.state%></td>			
		</tr>			
		<tr>
			<th>결재의견</th>
			<td colspan="3">
				<textarea name="description" id="description" rows="3" cols="" class="AXTextarea" readonly="readonly"><%=data.description%></textarea>
			</td>			
		</tr>					
	</table>
	
	<div class="refAppObject_wrap">
		<div class="header_title margin_top10">
			<i class="axi axi-subtitles"></i><span>결재객체</span>
		</div>
	<%
	if(data.per instanceof ApprovalContract) {
		Persistable perData = WorkspaceHelper.manager.getPersist((ApprovalContract)data.per);
		if(perData instanceof EPMDocument) {
	%>
		<jsp:include page="/jsp/approval/refAppEpmObject.jsp">
			<jsp:param value="<%=data.oid %>" name="oid"/>
		</jsp:include>
	<%
		} else {
	%>	
		<jsp:include page="/jsp/approval/refAppObject.jsp">
			<jsp:param value="<%=data.oid %>" name="oid"/>
		</jsp:include>	
	<%
			}
		// 일괄결재 아닐경우..
		} else {

			if(data.per instanceof PartListMaster) {
	%>
		<jsp:include page="/jsp/approval/refPartListMaster.jsp">
			<jsp:param value="<%=data.oid %>" name="oid"/>
		</jsp:include>		
	<%
		} else {
	%>
		<jsp:include page="/jsp/approval/refAppObject.jsp">
			<jsp:param value="<%=data.oid %>" name="oid"/>
		</jsp:include>	
	<%
		}
		}
	%>
		</div>
	<%
		if(data.per instanceof PartListMaster) {
	%>
	<div class="refAppObject_wrap">
		<div class="header_title margin_top10">
			<i class="axi axi-subtitles"></i><span>관련작번</span>
		</div>
		
		<jsp:include page="/jsp/approval/refProject.jsp">
			<jsp:param value="<%=data.oid %>" name="oid"/>
		</jsp:include>
	</div>		
	<%
		} else if(data.per instanceof WTDocument) {
	%>
	<div class="refAppObject_wrap">
		<div class="header_title margin_top10">
			<i class="axi axi-subtitles"></i><span>관련작번</span>
		</div>
		
		<jsp:include page="/jsp/approval/refProjectDoc.jsp">
			<jsp:param value="<%=data.oid %>" name="oid"/>
		</jsp:include>
	</div>			
	<%
		}
	%>
	
	<div class="refAppLine_wrap">
		<div class="header_title margin_top10">
			<i class="axi axi-subtitles"></i><span>결재라인</span>
		</div>
		
		<jsp:include page="/jsp/approval/refAppLine.jsp">
			<jsp:param value="<%=data.oid %>" name="oid"/>
		</jsp:include>
	</div>
	
	<%
		if(!isPopup) {
	%>
	<table class="btn_table">
		<tr>
			<td class="center">
				<input type="button" value="목록" id="<%=btnKey %>" title="목록" class="blueBtn">
				<%-- <%
					if(!isLastAppLine) {
				%>		
 				<input type="button" value="스킵???" id="skipApproval" title="스킵???" data-oid="<%=data.oid %>"> 
				<%
					}
				%> --%>
			</td>
		</tr>
	</table>
	<%
		}
	%>
</td>