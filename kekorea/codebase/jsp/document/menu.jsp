<%@page import="e3ps.common.util.StringUtils"%>
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

		if ("listDocument" == s) {
			$(".listDocument").addClass("left_hover");
		} else if ("listPartList" == s) {
			$(".listPartList").addClass("left_hover");
		} else if ("listRequestDocument" == s) {
			$(".listRequestDocument").addClass("left_hover");
		} else if ("createDocument" == s) {
			$(".createDocument").addClass("left_hover");
		} else if("createOutput" == s) {
			$(".createOutput").addClass("left_hover");
		} else if("createPartListMaster" == s) {
			$(".createPartListMaster").addClass("left_hover");
		} else if("createRequestDocument" == s) {
			$(".createRequestDocument").addClass("left_hover");
		} else if("approvalDocument" == s) {
			$(".approvalDocument").addClass("left_hover");
		} else if("listContents" == s) {
			$(".listContents").addClass("left_hover");
		} else if("listOutput" == s) {
			$(".listOutput").addClass("left_hover");
		}
	})
</script> <!-- left menu -->
	<%
		if(isKO) {
	%>
		<table id="menu_table">
		<tr>
			<td class="menu_title">
				<p>문서관리</p>
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
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title listDocument" data-url="/document/listDocument" title="문서 조회">문서 조회</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title listOutput" data-url="/document/listOutput" title="산출물 조회">산출물 조회</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title listPartList" data-url="/partList/listPartList" title="수배표 조회">수배표 조회</span></td>
					</tr>
<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title listRequestDocument" data-url="/document/listRequestDocument" title="의뢰서 조회">의뢰서 조회</span></td>
					</tr>					
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title createDocument" data-url="/document/createDocument" title="문서 등록">문서 등록</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title createOutput" data-url="/document/createOutput" title="산출물 등록">산출물 등록</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title createPartListMaster" data-url="/partList/createPartListMaster" title="수배표 등록">수배표 등록</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title createRequestDocument" data-url="/document/createRequestDocument" title="의뢰서 등록">의뢰서 등록</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title approvalDocument" data-url="/document/approvalDocument" title="문서 결재">문서 결재</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title listContents" data-url="/document/listContents" title="첨부파일조회">첨부파일조회</span></td>
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
				<p>文書管理</p>
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
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title listDocument" data-url="/document/listDocument" title="文書檢索">文書檢索</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title listOutput" data-url="/document/listOutput" title="産出物照会">産出物照会</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title listPartList" data-url="/partList/listPartList" title="手配表照会">手配表照会</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title listRequestDocument" data-url="/document/listRequestDocument" title="依頼書 檢索">依頼書 檢索</span></td>
					</tr>					
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title createDocument" data-url="/document/createDocument" title="文書登録">文書登録</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title createOutput" data-url="/document/createOutput" title="産出物 登録">産出物 登録</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title createPartListMaster" data-url="/partList/createPartListMaster" title="手配票 登録">手配票 登録</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title createRequestDocument" data-url="/document/createRequestDocument" title="依頼書 登録">依頼書 登録</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title approvalDocument" data-url="/document/approvalDocument" title="文書 決裁">文書 決裁</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title listContents" data-url="/document/listContents" title="添付ファイル檢索">添付ファイル檢索
</span></td>
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