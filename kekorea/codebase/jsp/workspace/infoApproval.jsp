<%@page import="wt.epm.EPMDocument"%>
<%@page import="e3ps.workspace.ApprovalContract"%>
<%@page import="wt.fc.Persistable"%>
<%@page import="e3ps.partlist.PartListMaster"%>
<%@page import="e3ps.approval.service.ApprovalHelper"%>
<%@page import="e3ps.approval.beans.ApprovalLineViewData"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	// popup check
	String popup = (String) request.getParameter("popup");
	boolean isPopup = Boolean.parseBoolean(popup);

	// data
	ApprovalLineViewData data = (ApprovalLineViewData) request.getAttribute("data");
	// isAdmin
	boolean isAdmin = CommonUtils.isAdmin();
	if("확인안함".equals(data.read)) {
		ApprovalHelper.manager.read(data.approvalLine);
	}

	String btnKey = "listAppBtn";
	boolean isComplete = false;
	boolean isApp = false;
	boolean isReceive = false;
	boolean isAgree = false;
	String ss = data.state;
	if(ss.equals("제출됨") || ss.equals("검토완료") || ss.equals("결재완료") || ss.equals("반려됨") || ss.equals("합의완료") || ss.equals("수신완료") ) {
		isComplete = true;
		btnKey = "listComplete";
	}

	if(ss.equals("승인중")) {
		isApp = true;
		btnKey = "listAppBtn"; 
	}
	
	if(ss.equals("수신중")) {
		isReceive = true;
		btnKey = "listReceiveBtn"; 
	}
	
	if(ss.equals("검토중")) {
		isAgree = true;
		btnKey = "listAgreeBtn"; 
	}
%>
<script type="text/javascript" src="/Windchill/jsp/js/approvals.js"></script>
<td valign="top">
	<input type="hidden" name="oid" id="oid" value="<%=data.oid %>">
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
						if(data.appBtn && !isComplete) {
					%>
					<input type="button" value="승인" id="approvalBtn" title="승인">
					<input type="button" value="반려" id="returnBtn" title="반려" class="redBtn">
					<%
						}
					%>
					<%
						if(data.agreeBtn && !isComplete) {
					%>
					<input type="button" value="검토완료" id="agreeBtn" title="검토완료">
					<input type="button" value="검토반려" id="unagreeBtn" title="검토반려" class="redBtn">
					<%
						}
					%>
					<%
						if(data.receiveBtn || ApprovalHelper.LINE_RECEIVE_STAND.equals(data.state)) {
					%>
					<input type="button" value="수신확인" id="receiveBtn" title="수신확인">
					<%
						}
					%>		
					<input type="button" value="닫기" id="closeDocBtn" title="닫기" class="redBtn">
				</div>
				<%
					}
				%>
				
				<%
					if(!isPopup) {
				%>
						<td class="right">
						<%
							if(data.appBtn && !isComplete) {
						%>
							<input type="button" value="승인" id="approvalBtn" title="승인">
							<input type="button" value="반려" id="returnBtn" title="반려" class="redBtn">		
						<%
							}
							if(data.agreeBtn && !isComplete) {
						%>
						<input type="button" value="검토완료" id="agreeBtn" title="검토완료">
						<input type="button" value="검토반려" id="unagreeBtn" title="검토반려" class="redBtn">
						<%
							}
						%>
						<%
							if(data.receiveBtn) {
						%>
						<input type="button" value="수신확인" id="receiveBtn" title="수신확인">
						<%
							}
						%>
						<input type="button" value="목록" id="<%=btnKey %>" title="목록" class="blueBtn">				
				<%
					}
				%>
				</td>
			</tr>
		</table>
	<table class="view_table">
		<colgroup>
			<col width="200">
			<col width="500">
			<col width="200">
			<col width="500">
		</colgroup>
		<tr>
			<th>결재 제목</th>
			<td colspan="3"><%=data.name %></td>
		</tr>
		<tr>
			<th>담당자</th>
			<td><%=data.creator %></td>
			<th>수신일</th>
			<td><%=data.startTime %></td>			
		</tr>		
		<tr>
			<th>구분</th>
			<td><%=data.type %></td>
			<th>역할</th>
			<td><%=data.role %></td>			
		</tr>	
		<tr>
			<th>기안자</th>
			<td><%=data.submiter %></td>
			<th>상태</th>
			<td><%=data.state %></td>			
		</tr>			
		<%
			// 결재라인만 위임 가능하도록..
			if(isApp || isAgree) {
		%>
		<tr>
			<th>위임</th>
			<td colspan="3">
				<input type="text" name="reassignUser" id="reassignUser" class="AXInput wid200" data-dbl="true"> 
				<input type="hidden" name="reassignUserOid" id="reassignUserOid" >
				<input type="button" title="위임" class="pos2" value="위임" id="reassignApprovalBtn" data-oid="<%=data.oid %>">
				<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="reassignUser"></i>			
			</td>
		</tr>			
		<%
			}
		%>	
		<tr>
			<th>결재의견</th>
			<td colspan="3">
				<textarea name="description" id="description" rows="3" cols="" class="AXTextarea" <%if(isComplete){ %> readonly="readonly" <%} %> ><%=data.description %></textarea>
			</td>			
		</tr>					
	</table>
	
	<div class="refAppObject_wrap">
		<div class="header_title margin_top10">
			<i class="axi axi-subtitles"></i><span>결재객체</span>
		</div>
	<%
		if(data.per instanceof ApprovalContract) {
			Persistable perData = ApprovalHelper.manager.getPersist((ApprovalContract)data.per);
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
	
</td>