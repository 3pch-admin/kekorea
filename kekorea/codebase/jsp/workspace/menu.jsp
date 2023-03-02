<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="e3ps.approval.service.ApprovalHelper"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
int[] count = WorkspaceHelper.manager.getLineCount();
	boolean isKO = true;
	String ll = (String)session.getAttribute("locales");
	
	if("ko".equals(ll)) {
		isKO = true;
	} else if("ja".equals(ll)) {
		isKO = false;
	}
%>
<td valign="top" id="left_menu_td"><script type="text/javascript">
	$(document).ready(function() {
		var pathname = document.location.pathname;
		var idx = pathname.lastIndexOf("/");
		var s = pathname.substring(idx + 1);

		if ("listNotice" == s) {
			$(".listNotice").addClass("left_hover");
		} else if ("listApproval" == s) {
			$(".listApproval").addClass("left_hover");
		} else if ("listIng" == s) {
			$(".listIng").addClass("left_hover");
		} else if ("listComplete" == s) {
			$(".listComplete").addClass("left_hover");
		} else if ("listReceive" == s) {
			$(".listReceive").addClass("left_hover");
		} else if ("listAgree" == s) {
			$(".listAgree").addClass("left_hover");
		} else if ("listReturn" == s) {
			$(".listReturn").addClass("left_hover");
		} else if ("changePassword" == s) {
			$(".changePassword").addClass("left_hover");
		} else if ("viewOrg" == s) {
			$(".viewOrg").addClass("left_hover");
		} else if ("myProject" == s) {
			$(".myProject").addClass("left_hover");
		}/* else if("myListDocument" == s) {
			$(".myListDocument").addClass("left_hover");
		} */
	})
</script> <!-- left menu -->
	<%
		if(isKO) {
	%>
	<table id="menu_table">
		<tr>
			<td class="menu_title">
				<p>나의업무</p>
			</td>
			<td rowspan="8" class="switch" valign="top"><img src="/Windchill/jsp/images/leftmenu_click01.gif" class="left_switch"></td>
		</tr>
		<tr>
			<td><img src="/Windchill/jsp/images/left_img01.gif"></td>
		</tr>
		<tr>
			<td background="/Windchill/jsp/images/left_img02.gif">
				<table id="left_menu_table">
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title listNotice" data-url="/approval/listNotice" title="공지사항">공지사항</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title myProject" data-url="/approval/myProject" title="나의 작번">나의 작번</span></td>
					</tr>
					<tr>
						<td class="topPadding"><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title">작업공간</span>
							<ul class="left_menu_ul">
								<li><span class="left_title listAgree" data-url="/approval/listAgree" title="검토함 (<%=count[0]%>건)">▪&nbsp;검토함&nbsp;<span class="vsp">(</span><span id="agreeCnt"><%=count[0]%></span>건<span class="vsp">)</span></span></li>
								<li><span class="left_title listApproval" data-url="/approval/listApproval" title="결재함 (<%=count[1]%>건)">▪&nbsp;결재함&nbsp;<span class="vsp">(</span><span id="approvalCnt"><%=count[1]%></span>건<span class="vsp">)</span></span></li>
								<li><span class="left_title listReceive" data-url="/approval/listReceive" title="수신함 (<%=count[2]%>건)">▪&nbsp;수신함&nbsp;<span class="vsp">(</span><%=count[2]%>건<span class="vsp">)</span></span></li>
								<li><span class="left_title listIng" data-url="/approval/listIng" title="진행함 (<%=count[3]%>건)">▪&nbsp;진행함&nbsp;<span class="vsp">(</span><%=count[3]%>건<span class="vsp">)</span></span></li>
								<li><span class="left_title listComplete" data-url="/approval/listComplete" title="완료함 (<%=count[4]%>건)">▪&nbsp;완료함&nbsp;<span class="vsp">(</span><%=count[4]%>건<span class="vsp">)</span></span></li>
								<li><span class="left_title listReturn" data-url="/approval/listReturn" title="반려함 (<%=count[5]%>건)">▪&nbsp;반려함&nbsp;<span class="vsp">(</span><%=count[5]%>건<span class="vsp">)</span></span></li>
							</ul></td>
					</tr>
<!-- 					<tr> -->
<!-- 						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title myListDocument" data-url="/document/myListDocument" title="개인문서함">개인문서함</span></td> -->
<!-- 					</tr>					 -->
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title viewOrg" data-url="/org/viewOrg" title="조직도">조직도</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title changePassword" data-url="/org/changePassword" title="비밀번호 변경">비밀번호 변경</span></td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td><img src="/Windchill/jsp/images/left_img03.gif"></td>
		</tr>
		<%@include file="/jsp/common/layouts/include_left.jsp"%>
		<tr>
			<td><img src="/Windchill/jsp/images/left_img07.gif">
		</tr>
	</table>
	<%
		} else {
	%>	
	<table id="menu_table">
		<tr>
			<td class="menu_title">
				<p>私の業務</p>
			</td>
			<td rowspan="8" class="switch" valign="top"><img src="/Windchill/jsp/images/leftmenu_click01.gif" class="left_switch"></td>
		</tr>
		<tr>
			<td><img src="/Windchill/jsp/images/left_img01.gif"></td>
		</tr>
		<tr>
			<td background="/Windchill/jsp/images/left_img02.gif">
				<table id="left_menu_table">
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title listNotice" data-url="/approval/listNotice" title="公知事項">公知事項</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title myProject" data-url="/approval/myProject" title="私の作番">私の作番</span></td>
					</tr>
					<tr>
						<td class="topPadding"><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title">作業空間</span>
							<ul class="left_menu_ul">
								<li style="padding-left: 34px !important;"><span class="left_title listAgree" data-url="/approval/listAgree" title="検討箱 (<%=count[0]%>건)">▪&nbsp;&nbsp;検討箱&nbsp;<span class="vsp">(</span><%=count[0]%>건<span class="vsp">)</span></span></li>
								<li><span class="left_title listApproval" data-url="/approval/listApproval" title="決裁箱 (<%=count[1]%>건)">▪&nbsp;決裁箱&nbsp;<span class="vsp">(</span><%=count[1]%>건<span class="vsp">)</span></span></li>
								<li><span class="left_title listReceive" data-url="/approval/listReceive" title="受信箱 (<%=count[2]%>건)">▪&nbsp;受信箱&nbsp;<span class="vsp">(</span><%=count[2]%>건<span class="vsp">)</span></span></li>
								<li><span class="left_title listIng" data-url="/approval/listIng" title="進行箱 (<%=count[3]%>건)">▪&nbsp;進行箱&nbsp;<span class="vsp">(</span><%=count[3]%>건<span class="vsp">)</span></span></li>
								<li><span class="left_title listComplete" data-url="/approval/listComplete" title="完了箱 (<%=count[4]%>건)">▪&nbsp;完了箱&nbsp;<span class="vsp">(</span><%=count[4]%>건<span class="vsp">)</span></span></li>
								<li><span class="left_title listReturn" data-url="/approval/listReturn" title="返還箱 (<%=count[5]%>건)">▪&nbsp;返還箱&nbsp;<span class="vsp">(</span><%=count[5]%>건<span class="vsp">)</span></span></li>
							</ul></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title viewOrg" data-url="/org/viewOrg" title="組織図">組織図</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title changePassword" data-url="/org/changePassword" title="パスワード変更">パスワード変更</span></td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td><img src="/Windchill/jsp/images/left_img03.gif"></td>
		</tr>
		<%@include file="/jsp/common/layouts/include_left.jsp"%>
		<tr>
			<td><img src="/Windchill/jsp/images/left_img07.gif">
		</tr>
	</table>	
	<%
		}
	%>
</td>