<%@page import="e3ps.admin.service.AdminHelper"%>
<%@page import="e3ps.common.util.DateUtils"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.util.Calendar"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@page import="e3ps.admin.PasswordSetting"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="e3ps.approval.ApprovalLine"%>
<%@page import="e3ps.project.Project"%>
<%@page import="e3ps.project.column.ProjectColumnData"%>
<%@page import="wt.epm.EPMDocument"%>
<%@page import="wt.part.WTPart"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="e3ps.approval.Notice"%>
<%@page import="wt.fc.PagingQueryResult"%>
<%@page import="e3ps.approval.column.ApprovalColumnData"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%

	PasswordSetting ps = AdminHelper.manager.getPasswordSetting();

	
	Calendar ca3 = Calendar.getInstance();
	Timestamp tt =  DateUtils.getCurrentTimestamp();
	ca3.setTimeInMillis(tt.getTime());
 	ca3.add(Calendar.DAY_OF_MONTH, -6);
	/* out.println(ca3.getTime().getDate());
	out.println(ca3.getTime().getMonth()); */
	
	boolean isSix = (boolean) request.getAttribute("isSix");
	boolean isThree = (boolean) request.getAttribute("isThree");
	PagingQueryResult nResult = (PagingQueryResult) request.getAttribute("nResult");
	PagingQueryResult pResult = (PagingQueryResult) request.getAttribute("pResult");
	PagingQueryResult aResult = (PagingQueryResult) request.getAttribute("aResult");
	
	boolean isKO = true;
	String ll = (String)session.getAttribute("locales");
	
	if("ko".equals(ll)) {
		isKO = true;
	} else if("ja".equals(ll)) {
		isKO = false;
	}
%>
<td>
	<style>
	html {
	overflow-y: scroll !important;
	}
	</style>
	<script type="text/javascript">
	$(document).ready(function() {
		var url = "/Windchill/plm/common/alertPassword";
		<%
			if(isThree) {
		%>
		$(document).openURLViewOpt(url, 1000, 300, "no");
		<%
			} else if(isSix) {
		%>
		$(document).openURLViewOpt(url, 1000, 300, "no");
		<%
			}
		%>
		$(".info_notice").click(function() {
			$(document).onLayer();
			$oid = $(this).data("oid");
			document.location.href = "/Windchill/plm/approval/viewNotice?oid=" + $oid;
		}).mouseover(function() {
			$(this).attr("title", "공지사항정보보기").css("cursor", "pointer");
		})
		
		$(".info_app").click(function() {
			$(document).onLayer();
			$oid = $(this).data("oid");
			document.location.href = "/Windchill/plm/approval/infoApproval?oid=" + $oid;
		}).mouseover(function() {
			$(this).attr("title", "결재정보보기").css("cursor", "pointer");
		})
		
		$(".info_myProject").click(function() {
			$oid = $(this).data("oid");
			var $url = "/Windchill/plm/project/viewProject?popup=true&oid=" + $oid;
			$(document).openURLViewOpt($url, 1200, 700, "");
		}).mouseover(function() {
			$(this).attr("title", "작번정보보기").css("cursor", "pointer");
		})
		
		
	})
	</script>
	<style>
	html {
		overflow-y: hidden !important;
	}
	</style>
	<%
		if(isKO) {
	%>
	<table id="main_table">
		<colgroup>
			<col width="50%">
			<col width="10px">
			<col width="50%">
		</colgroup>
		<tr>
			<td class="main_header_td" valign="top">
				<table id="notice_header_table">
					<colgroup>
						<col width="90%">
						<col width="10%">
					</colgroup>
					<tr>
						<td>공지사항</td>
						<td title="더 보기" class="more_notice" data-url="/Windchill/plm/approval/listNotice"><span class="main_plus">+</span>&nbsp;더 보기</td>
					</tr>
				</table>
		
				<table class="main_notice_list">
					<colgroup>
						<col width="90%">
						<col width="10%">
					</colgroup>
					<%
						while(nResult.hasMoreElements()) {
							Object[] obj = (Object[])nResult.nextElement();
							Notice n = (Notice)obj[0];
							String oid = n.getPersistInfo().getObjectIdentifier().getStringValue();
					%>
					<tr>
						<td class="info_notice" data-oid="<%=oid%>"><span class="main_square">▪</span>&nbsp;<%=n.getName()%></td>
						<td><%=n.getCreateTimestamp().toString().substring(0, 16)%></td>
					</tr>
					<%
						}
					%>	
				</table>
			</td>
			<td>&nbsp;</td>
			<td class="main_header_td_app" valign="top" rowspan="20">
				<table id="checkout_header_table">
					<colgroup>
						<col width="90%">
						<col width="10%">
					</colgroup>
					<tr>
						<td>결재 및 검토 리스트</td>
<!-- 						<td title="더 보기" class="more_checkout" data-url="/Windchill/plm/approval/listNotice"><span class="main_plus">+</span>&nbsp;더 보기</td> -->
						<td><span class="main_plus">&nbsp;</span></td>
					</tr>
				</table>	
				<table class="main_checkout_list">
					<colgroup>
						<col width="90%">
						<col width="10%">
					</colgroup>
					<%
						while(aResult.hasMoreElements()) {
							Object[] obj = (Object[])aResult.nextElement();
							ApprovalLine aline = (ApprovalLine) obj[0];
							String oid = aline.getPersistInfo().getObjectIdentifier().getStringValue();
					%>
					<tr>
						<td class="info_app" data-oid="<%=oid%>"><span class="main_square">▪</span>&nbsp;<%=aline.getName()%></td>
						<td><%=aline.getCreateTimestamp().toString().substring(0, 16)%></td>
					</tr>
					<%
						}
					%>	
				</table>
			</td>
		</tr>
		<tr>
			<td colspan="3" class="main_empty">&nbsp;</td>
		</tr>		
		<tr>
			<td valign="top" class="main_header_td">
				<table id="app_info_table">
					<colgroup>
						<col width="90%">
						<col width="200px">
					</colgroup>
					<tr>
						<td style="border-radius: 0px !important;">나의 작번 리스트</td>
						<td style="border-radius: 0px !important;" title="더 보기" class="more_app" data-url="/Windchill/plm/approval/myProject"><span class="main_plus_app">+</span>&nbsp;더 보기</td>
					</tr>
				</table>
				<table id="app_list_table">
					<colgroup>
						<col width="95%">
						<col width="100px">
					</colgroup>
					<%
						while(pResult.hasMoreElements()) {
							Object[] obj = (Object[])pResult.nextElement();
							Project project = (Project) obj[0];
							String oid = project.getPersistInfo().getObjectIdentifier().getStringValue();
					%>
					<tr>
						<td class="info_myProject" data-oid="<%=oid%>"><span class="main_square">▪</span>&nbsp;<%=project.getKekNumber()%>&nbsp;-&nbsp;<%=project.getPType() %></td>
						<td><%=project.getPDate()%></td>
					</tr>
					<%
						}
					%>	
				</table>
			</td>
		</tr>
	</table>
	<%
		} else {
	%>
	<table id="main_table">
		<colgroup>
			<col width="50%">
			<col width="10px">
			<col width="50%">
		</colgroup>
		<tr>
			<td class="main_header_td" valign="top">
				<table id="notice_header_table">
					<colgroup>
						<col width="90%">
						<col width="10%">
					</colgroup>
					<tr>
						<td>公知事項</td>
						<td title="もっと見る" class="more_notice" data-url="/Windchill/plm/approval/listNotice"><span class="main_plus">+</span>&nbsp;もっと見る</td>
					</tr>
				</table>
		
				<table class="main_notice_list">
					<colgroup>
						<col width="90%">
						<col width="10%">
					</colgroup>
					<%
						while(nResult.hasMoreElements()) {
							Object[] obj = (Object[])nResult.nextElement();
							Notice n = (Notice)obj[0];
							String oid = n.getPersistInfo().getObjectIdentifier().getStringValue();
					%>
					<tr>
						<td class="info_notice" data-oid="<%=oid%>"><span class="main_square">▪</span>&nbsp;<%=n.getName()%></td>
						<td><%=n.getCreateTimestamp().toString().substring(0, 16)%></td>
					</tr>
					<%
						}
					%>	
				</table>
			</td>
			<td>&nbsp;</td>
			<td class="main_header_td_app" valign="top" rowspan="20">
				<table id="checkout_header_table">
					<colgroup>
						<col width="90%">
						<col width="10%">
					</colgroup>
					<tr>
						<td>決裁及び検討リスト</td>
<!-- 						<td title="더 보기" class="more_checkout" data-url="/Windchill/plm/approval/listNotice"><span class="main_plus">+</span>&nbsp;もっと見る</td> -->
						<td><span class="main_plus">&nbsp;</span></td>
					</tr>
				</table>	
				<table class="main_checkout_list">
					<colgroup>
						<col width="90%">
						<col width="10%">
					</colgroup>
					<%
						while(aResult.hasMoreElements()) {
							Object[] obj = (Object[])aResult.nextElement();
							ApprovalLine aline = (ApprovalLine) obj[0];
							String oid = aline.getPersistInfo().getObjectIdentifier().getStringValue();
					%>
					<tr>
						<td class="info_app" data-oid="<%=oid%>"><span class="main_square">▪</span>&nbsp;<%=aline.getName()%></td>
						<td><%=aline.getCreateTimestamp().toString().substring(0, 16)%></td>
					</tr>
					<%
						}
					%>	
				</table>
			</td>
		</tr>
		<tr>
			<td colspan="3" class="main_empty">&nbsp;</td>
		</tr>		
		<tr>
			<td valign="top" class="main_header_td">
				<table id="app_info_table">
					<colgroup>
						<col width="90%">
						<col width="200px">
					</colgroup>
					<tr>
						<td style="border-radius: 0px !important;">私の作番リスト</td>
						<td style="border-radius: 0px !important;" title="もっと見る" class="more_app" data-url="/Windchill/plm/approval/myProject"><span class="main_plus_app">+</span>&nbsp;もっと見る</td>
					</tr>
				</table>
				<table id="app_list_table">
					<colgroup>
						<col width="95%">
						<col width="100px">
					</colgroup>
					<%
						while(pResult.hasMoreElements()) {
							Object[] obj = (Object[])pResult.nextElement();
							Project project = (Project) obj[0];
							String oid = project.getPersistInfo().getObjectIdentifier().getStringValue();
					%>
					<tr>
						<td class="info_myProject" data-oid="<%=oid%>"><span class="main_square">▪</span>&nbsp;<%=project.getKekNumber()%>&nbsp;-&nbsp;<%=project.getPType() %></td>
						<td><%=project.getPDate()%></td>
					</tr>
					<%
						}
					%>	
				</table>
			</td>
		</tr>
	</table>	
	<%
		}
	%>
</td>