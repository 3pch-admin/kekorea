<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="e3ps.approval.service.ApprovalHelper"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
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

		if ("listProject" == s) {
			$(".listProject").addClass("left_hover");
		} else if ("createProject" == s) {
			$(".createProject").addClass("left_hover");
		} else if ("listTemplate" == s) {
			$(".listTemplate").addClass("left_hover");
		} else if ("createTemplate" == s) {
			$(".createTemplate").addClass("left_hover");
		} else if ("myOutput" == s) {
			$(".myOutput").addClass("left_hover");
		} else if ("issueList" == s) {
			$(".issueList").addClass("left_hover");
		} else if ("createCode" == s) {
			$(".createCode").addClass("left_hover");
		}
	})
</script> <!-- left menu -->
	<%
		if(isKO) {
	%>
	<table id="menu_table">
		<tr>
			<td class="menu_title">
				<p>프로젝트</p>
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
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title">작번 관리</span>
							<ul class="left_menu_ul">
								<li><span class="left_title listProject" data-url="/project/listProject" title="작번 조회">▪&nbsp;작번 조회&nbsp;</span></li>
								<li><span class="left_title createProject" data-url="/project/createProject" title="작번 등록">▪&nbsp;작번 등록&nbsp;</span></li>
							</ul></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title">템플릿 관리</span>
							<ul class="left_menu_ul">
								<li><span class="left_title listTemplate" data-url="/template/listTemplate" title="템플릿 조회">▪&nbsp;템플릿 조회&nbsp;</span></li>
								<li><span class="left_title createTemplate" data-url="/template/createTemplate" title="템플릿 등록">▪&nbsp;템플릿 등록&nbsp;</span></li>
							</ul></td>
					</tr>			
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title">기타</span>
							<ul class="left_menu_ul">
								<li><span class="left_title issueList" data-url="/project/issueList" title="특이사항 조회">▪&nbsp;특이사항 조회&nbsp;</span></li>
							</ul>
							</td>
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
				<p style="padding-left: 10px;"><span style="position: relative; left: 50px;">プロジェクト</span></p>
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
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title">作番管理</span>
							<ul class="left_menu_ul">
								<li><span class="left_title listProject" data-url="/project/listProject" title="作番檢索">▪&nbsp;作番檢索&nbsp;</span></li>
								<li style="padding-left: 34px !important;"><span class="left_title createProject" data-url="/project/createProject" title="作番登録">▪&nbsp;&nbsp;作番登録&nbsp;</span></li>
							</ul></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title">テンプレート管理</span>
							<ul class="left_menu_ul">
								<li><span class="left_title listTemplate" data-url="/template/listTemplate" title="テンプレート檢索">▪&nbsp;テンプレート檢索&nbsp;</span></li>
								<li><span class="left_title createTemplate" data-url="/template/createTemplate" title="テンプレート登録">▪&nbsp;テンプレート登録&nbsp;</span></li>
							</ul></td>
					</tr>			
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title">そのた</span>
							<ul class="left_menu_ul">
<!-- 								<li><span class="left_title myOutput" data-url="/project/myOutput" title="나의 산출물">▪&nbsp;나의 산출물&nbsp;</span></li> -->
								<li><span class="left_title issueList" data-url="/project/issueList" title="特異事項照会">▪&nbsp;特異事項照会&nbsp;</span></li>
							</ul></td>
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