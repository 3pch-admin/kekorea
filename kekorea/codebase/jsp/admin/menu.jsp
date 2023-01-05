<%@page import="e3ps.common.util.CommonUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<td valign="top" id="left_menu_td"><script type="text/javascript">
	$(document).ready(function() {
		var pathname = document.location.pathname;
		var idx = pathname.lastIndexOf("/");
		var s = pathname.substring(idx + 1);

		if ("setPassword" == s) {
			$(".setPassword").addClass("left_hover");
		} else if ("manageCode" == s) {
			$(".manageCode").addClass("left_hover");
		} else if ("initPassword" == s) {
			$(".initPassword").addClass("left_hover");
		} else if ("createCode" == s) {
			$(".createCode").addClass("left_hover");
		}else if ("searchErpOutput" == s) {
			$(".searchErpOutput").addClass("left_hover");
		}else if ("searchErpPjtBom" == s) {
			$(".searchErpPjtBom").addClass("left_hover");
		}else if ("searchErpPart" == s) {
			$(".searchErpPart").addClass("left_hover");
		}else if ("searchErpUnitBom" == s) {
			$(".searchErpUnitBom").addClass("left_hover");
		}
		
	})
</script> <!-- left menu -->
	<table id="menu_table">
		<tr>
			<td class="menu_title">
				<p>관리자</p>
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
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title setPassword" data-url="/admin/setPassword" title="비밀번호세팅">비밀번호세팅</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title manageCode" data-url="/admin/manageCode" title="코드관리">코드관리</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title createCode" data-url="/admin/createCode" title="설치장소 생성">설치장소 생성</span></td>
					</tr>
					
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title searchErpOutput" data-url="/admin/searchErpOutput" title="산출물연동">산출물 연동</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title searchErpPjtBom" data-url="/admin/searchErpPjtBom" title="수배표 연동">수배표 연동</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title searchErpPart" data-url="/admin/searchErpPart" title="품목 연동">품목 연동</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title searchErpUnitBom" data-url="/admin/searchErpUnitBom" title="UNUT_BOM 연동">UNUT_BOM 연동</span></td>
					</tr>
					<!-- 
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title manageUser" data-url="/admin/manageUser" title="사용자 관리">사용자 관리</span></td>
					</tr>
					<tr>
						<td><img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title initPassword" data-url="/admin/initPassword" title="비밀번호 초기화">비밀번호 초기화</span></td>
					</tr>
					 -->
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
	</table></td>