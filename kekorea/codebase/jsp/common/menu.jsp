<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<td valign="top" id="left_menu_td"><script type="text/javascript">
	$(document).ready(function() {
		var pathname = document.location.pathname;
		var idx = pathname.lastIndexOf("/");
		var s = pathname.substring(idx + 1);

		if ("manual" == s) {
			$(".manual").addClass("left_hover");
		} else if ("setupFiles" == s) {
			$(".setupFiles").addClass("left_hover");
		} else if ("qna" == s) {
			$(".qna").addClass("left_hover");
		}
	})
</script> <!-- left menu -->
	<table id="menu_table">
		<tr>
			<td class="menu_title">
				<p>일반</p>
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
						<td>
							<img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title manual" data-url="/common/manual" title="메뉴얼">메뉴얼</span>
						</td>
					</tr>
					<tr>
						<td>
							<img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title setupFiles" data-url="/common/setupFiles" title="설치파일">설치파일</span>
						</td>
					</tr>
					<tr>
						<td>
							<img src="/Windchill/jsp/images/title_bullet.gif"> <span class=" qna" data-url="/common/setupFiles" title="FAQ">FAQ</span>
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