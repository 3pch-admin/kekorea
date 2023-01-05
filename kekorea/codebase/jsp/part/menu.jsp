<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="e3ps.part.beans.PartViewData"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%
	boolean isKO = true;
	String ll = (String)session.getAttribute("locales");
	
	if("ko".equals(ll)) {
		isKO = true;
	} else if("ja".equals(ll)) {
		isKO = false;
	}
		
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<td valign="top" id="left_menu_td"><script type="text/javascript">
	$(document).ready(function() {
		var pathname = document.location.pathname;
		var idx = pathname.lastIndexOf("/");
		var s = pathname.substring(idx + 1);
		if ("listProductPart" == s) {
			$(".listProductPart").addClass("left_hover");
		} else if ("listLibraryPart" == s) {
			$(".listLibraryPart").addClass("left_hover");
		} else if ("createCode" == s) {
			$(".createCode").addClass("left_hover");
		} else if ("createBundlePart" == s) {
			$(".createBundlePart").addClass("left_hover");
		} else if ("createUnitbom" == s) {
			$(".createUnitbom").addClass("left_hover");
		} else if ("modifyBom" == s) {
			$(".modifyBom").addClass("left_hover");
		} else if ("createSpec" == s) {
			$(".createSpec").addClass("left_hover");
		} else if ("approvalEplan" == s) {
			$(".approvalEplan").addClass("left_hover");
		}  else if ("listYcode" == s) {
			$(".listYcode").addClass("left_hover");
		}  else if("listUnitBom" == s) {
			$(".listUnitBom").addClass("left_hover");
		}
		
		$(".BOMEditor").click(function() {
			var url = "/Windchill/plm/part/bomEditor";
			var opt = "scrollbars=yes, resizable=yes";
			$(document).openURLViewOpt(url, 1400, 600, opt);
		}).mouseover(function() {
			$(this).css("cursor", "pointer");
		})
	})
</script> <!-- left menu -->
	<%
		if(isKO) {
	%>
	<table id="menu_table">
		<tr>
			<td class="menu_title">
				<p>부품관리</p>
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
							<img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title listProductPart" data-url="/part/listProductPart" title="부품 조회">부품 조회</span>
						</td>
					</tr>
<!-- 					<tr> -->
<!-- 						<td> -->
<!-- 							<img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title listLibraryPart" data-url="/part/listLibraryPart" title="라이브러리 조회">라이브러리 조회</span> -->
<!-- 						</td> -->
<!-- 					</tr> -->
					<tr>
						<td>
							<img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title createCode" data-url="/part/createCode" title="코드 생성">코드 생성</span>
						</td>
					</tr>
					<tr>
						<td>
							<img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title createBundlePart" data-url="/part/createBundlePart" title="부품 일괄 등록">부품 일괄 등록</span>
						</td>
					</tr>
					<tr>
						<td>
							<img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title createSpec" data-url="/part/createSpec" title="제작사양서 등록">제작사양서 등록</span>
						</td>
					</tr>
					<tr>
						<td>
							<img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title listUnitBom" data-url="/part/listUnitBom" title="UNIT BOM 조회">UNIT BOM 조회</span>
						</td>
					</tr>										
					<tr>
						<td>
							<img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title createUnitbom" data-url="/part/createUnitbom" title="UNIT BOM 등록">UNIT BOM 등록</span>
						</td>
					</tr>
					<tr>
						<td>
							<img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title approvalEplan" data-url="/part/approvalEplan" title="EPLAN 결재">EPLAN 결재</span>
						</td>
					</tr>
<!-- 					<tr> -->
<!-- 						<td> -->
<!-- 							<img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title listYcode" data-url="/part/listYcode" title="YCODE 조회">YCODE 조회</span> -->
<!-- 						</td> -->
<!-- 					</tr> -->
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
				<p>部品管理</p>
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
							<img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title listProductPart" data-url="/part/listProductPart" title="部品 檢索">部品 檢索</span>
						</td>
					</tr>
<!-- 					<tr> -->
<!-- 						<td> -->
<!-- 							<img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title listLibraryPart" data-url="/part/listLibraryPart" title="라이브러리 조회">라이브러리 조회</span> -->
<!-- 						</td> -->
<!-- 					</tr> -->
					<tr>
						<td>
							<img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title createCode" data-url="/part/createCode" title="コード生成">コード生成</span>
						</td>
					</tr>
					<tr>
						<td>
							<img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title createBundlePart" data-url="/part/createBundlePart" title="部品一括登録">部品一括登録</span>
						</td>
					</tr>
					<tr>
						<td>
							<img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title createSpec" data-url="/part/createSpec" title="制作仕様書登録">制作仕様書登録</span>
						</td>
					</tr>					
					<tr>
						<td>
							<img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title listUnitBom" data-url="/part/listUnitBom" title="UNIT BOM 檢索">UNIT BOM 檢索</span>
						</td>
					</tr>										
					<tr>
						<td>
							<img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title createUnitbom" data-url="/part/createUnitbom" title="UNIT BOM 등록">UNIT BOM 登録</span>
						</td>
					</tr>
					<tr>
						<td>
							<img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title approvalEplan" data-url="/part/approvalEplan" title="EPLAN 決裁">EPLAN 決裁</span>
						</td>
					</tr>
					<tr>
						<td>
							<img src="/Windchill/jsp/images/title_bullet.gif"> <span class="left_title listYcode" data-url="/part/listYcode" title="YCODE 檢索">YCODE 檢索</span>
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
		}
	%>
</td>