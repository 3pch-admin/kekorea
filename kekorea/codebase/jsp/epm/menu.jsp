<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTUser"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
	// KEK
	boolean isKEK = false;
	if("kek1".equals(sessionUser.getName())) {
		isKEK = true;
	}
	
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

		if ("listProductEpm" == s || "listProductThumnail" == s) {
			$(".listProductEpm").addClass("left_hover");
		} else if ("listLibraryEpm" == s || "listLibraryThumnail" == s) {
			$(".listLibraryEpm").addClass("left_hover");
		} else if ("printEpm" == s) {
			$(".printEpm").addClass("left_hover");
		} else if ("inputDrwAttr" == s) {
			$(".inputDrwAttr").addClass("left_hover");
		} else if("reviseCadData" == s) {
			$(".reviseCadData").addClass("left_hover");
		} else if("approvalEpm" == s) {
			$(".approvalEpm").addClass("left_hover");
		} else if("viewerDist" == s) {
			$(".viewerDist").addClass("left_hover");
		} else if("createViewer" == s) {
			$(".createViewer").addClass("left_hover");
		}
	})
</script> <!-- left menu -->
	<%
		if(isKO) {
	%>
	<table id="menu_table">
		<tr>
			<td class="menu_title">
				<p>도면관리</p>
			</td>
			<td rowspan="8" class="switch" valign="top"><img src="/Windchill/jsp/images/leftmenu_click01.gif" class="left_switch"></td>
		</tr>
		<tr>
			<td><img src="/Windchill/jsp/images/left_img01.gif"></td>
		</tr>
		<tr>
			<td background="/Windchill/jsp/images/left_img02.gif">
				<table id="left_menu_table">
					<%
						if(!isKEK) {
					%>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title listProductEpm" data-url="/epm/listProductEpm" title="가공품 조회">도면 조회</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title listLibraryEpm" data-url="/epm/listLibraryEpm" title="구매품 조회">라이브러리 조회</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title approvalEpm" data-url="/epm/approvalEpm" title="도면 결재">도면 결재</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title printEpm" data-url="/epm/printEpm" title="도면 출력">도면 출력</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title createViewer" data-url="/epm/createViewer" title="뷰어 등록">뷰어 등록</span></td>
					</tr>					
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title viewDist" data-url="/epm/listViewer" title="뷰어 목록">뷰어 목록</span></td>
					</tr>
					<%
						} else {
					%>			
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title viewDist" data-url="/epm/listViewer" title="뷰어 목록">뷰어 목록</span></td>
					</tr>					
					<%
						}
					%>		
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
				<p>図面管理</p>
			</td>
			<td rowspan="8" class="switch" valign="top"><img src="/Windchill/jsp/images/leftmenu_click01.gif" class="left_switch"></td>
		</tr>
		<tr>
			<td><img src="/Windchill/jsp/images/left_img01.gif"></td>
		</tr>
		<tr>
			<td background="/Windchill/jsp/images/left_img02.gif">
				<table id="left_menu_table">
					<%
						if(!isKEK) {
					%>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title listProductEpm" data-url="/epm/listProductEpm" title="図面檢索">図面檢索</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title listLibraryEpm" data-url="/epm/listLibraryEpm" title="ライブラリ檢索">ライブラリ檢索</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title approvalEpm" data-url="/epm/approvalEpm" title="図面決裁">図面決裁</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title printEpm" data-url="/epm/printEpm" title="図面出力">図面出力</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title createViewer" data-url="/epm/createViewer" title="ﾋﾞｭｰｱｰ登録">ﾋﾞｭｰｱｰ登録</span></td>
					</tr>					
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title viewDist" data-url="/epm/listViewer" title="ﾋﾞｭｰｱｰ目録">ﾋﾞｭｰｱｰ目録</span></td>
					</tr>
					<%
						} else {
					%>			
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title viewDist" data-url="/epm/listViewer" title="ﾋﾞｭｰｱｰ目録">ﾋﾞｭｰｱｰ目録</span></td>
					</tr>					
					<%
						}
					%>		
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