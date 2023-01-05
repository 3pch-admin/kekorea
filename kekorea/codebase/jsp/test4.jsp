<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="java.util.HashMap"%>
<%@page import="e3ps.epm.beans.EpmViewData"%>
<%@page import="wt.epm.EPMDocument"%>
<%@page import="wt.util.WTAttributeNameIfc"%>
<%@page import="wt.fc.PagingQueryResult"%>
<%@page import="e3ps.part.beans.PartViewData"%>
<%@page import="wt.part.WTPart"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="e3ps.common.util.PageQueryUtils"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="e3ps.common.util.HtmlUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	HtmlUtils html = new HtmlUtils();
	PageQueryUtils pager = (PageQueryUtils) request.getAttribute("pager");
	PagingQueryResult result = pager.find();
	HashMap<String, Object> param = (HashMap<String, Object>) pager.getParam();
	String name = (String) param.get("name");
	String number = (String) param.get("number");
	String state = StringUtils.removeNull((String) param.get("state"));
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" href="/Windchill/jsp/css/common.css">
<link rel="stylesheet" href="/Windchill/jsp/asset/axicon/axicon.css">
<link rel="stylesheet" href="/Windchill/jsp/asset/axisj/ui/bulldog/AXJ.min.css">
<link rel="stylesheet" href="/Windchill/jsp/asset/magnify/dist/jquery.magnify.css">
<link rel="stylesheet" href="/Windchill/jsp/asset/radio/skins/css/checks.css">
<!-- <script type="text/javascript" src="/Windchill/jsp/js/jquery-3.3.1.js"></script> -->
<script type="text/javascript" src="/Windchill/jsp/asset/axisj/jquery/jquery-1.12.3.min.js"></script>
<script type="text/javascript" src="/Windchill/jsp/js/common.js"></script>
<script type="text/javascript" src="/Windchill/jsp/asset/magnify/dist/jquery.magnify.js"></script>
<script type="text/javascript" src="/Windchill/jsp/asset/axisj/dist/AXJ.all.js"></script>
<script type="text/javascript" src="/Windchill/jsp/asset/radio/dist/jquery.checks.js"></script>
<script type="text/javascript" src="/Windchill/jsp/asset/popup/dist/jquery.window.popup.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$(document).cellClick("name");

		$("#list_view").click(function() {
			$(document).onLayer();
			document.location.href = "/Windchill/plm/part/test";
		})

		$("#img_view").click(function() {
			$(document).onLayer();
			document.location.href = "/Windchill/plm/part/test2";
		})

		$("#searchBtn").click(function() {
			$("input[name=tpage]").val(1);
			$("input[name=sessionid]").val(0);
			$(document).onLayer();
			$("form").submit();
		})

		$("select[name=state]").bindSelect();
		$("input").checks();
	}).keypress(function(e) {

			$("input[name=tpage]").val(1);
			$("input[name=sessionid]").val(0);
			$(document).onLayer();
			$("form").submit();
		}
	})
</script>
</head>
<body>
	<form method="post">
		<%
			out.println(pager.getScript());
		%>
		<table id="header_table">
			<tr>
				<td id="logo" title="메인으로"><img src="/Windchill/jsp/images/logo.png" class="logoImg"></td>
				<td>
					<ul>
						<li>나의업무</li>
						<li>설계변경</li>
						<li>도면관리</li>
						<li>부품관리</li>
						<li>문서관리</li>
						<li>관리자</li>
					</ul>
				</td>
				<td id="infoBox">김준호 <i class="axi axi-lines"></i> <i class="axi axi-search2"></i>
				</td>
			</tr>
		</table>

		<table id="header_search_table">
			<tr>
				<td><input type="text" class="AXInput"></td>
			</tr>
		</table>

		<table id="content_table">
			<colgroup>
				<col width="210">
				<col width="*">
			</colgroup>
			<tr>
				<td valign="top">
					<!-- left menu -->
					<table id="menu_table">
						<tr>
							<td class="menu_title">
								<p>나의업무</p>
							</td>
							<td rowspan="8" class="switch" valign="top"><img src="/Windchill/jsp/images/leftmenu_click01.gif"></td>
						</tr>
						<tr>
							<td><img src="/Windchill/jsp/images/left_img01.gif"></td>
						</tr>
						<tr>
							<td background="/Windchill/jsp/images/left_img02.gif">
								<table id="left_menu_table">
									<tr>
										<td><img src="/Windchill/jsp/images/title_bullet.gif">메뉴</td>
									</tr>
									<tr>
										<td><img src="/Windchill/jsp/images/title_bullet.gif">메뉴1</td>
									</tr>
									<tr>
										<td><img src="/Windchill/jsp/images/title_bullet.gif">메뉴2</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td><img src="/Windchill/jsp/images/left_img03.gif"></td>
						</tr>
						<tr>
							<td><img src="/Windchill/jsp/images/left_img04.gif"></td>
						</tr>
						<tr>
							<td><img src="/Windchill/jsp/images/left_img05.gif"></td>
						</tr>
						<tr>
							<td><img src="/Windchill/jsp/images/left_img06.gif"></td>
						</tr>
						<tr>
							<td><img src="/Windchill/jsp/images/left_img07.gif"></td>
						</tr>
					</table>
				</td>
				<td>&nbsp;</td>
				<td valign="top">
					<!-- search table... -->
					<div class="header_title">
						<i class="axi axi-subtitles"></i><span>부품조회</span>
					</div>
					<table class="search_table">
						<colgroup>
							<col width="240">
							<col width="400">
							<col width="240">
							<col width="400">
						</colgroup>
						<tr>
							<th>부품번호</th>
							<td><input type="text" name="number" class="AXInput wid200" value="<%=StringUtils.removeNull(number)%>"></td>
							<th>부품명</th>
							<td><input type="text" name="name" class="AXInput wid200" value="<%=StringUtils.removeNull(name)%>"></td>
						</tr>
						<tr>
							<th>작성자</th>
							<td><input type="text" name="creator" class="AXInput wid100"></td>
							<th>작성일</th>
							<td><input type="text" class="AXInput"></td>
						</tr>
						<tr>
							<th>버전</th>
							<td><label> <input type="radio" name="latest" checked="checked">최신버전
							</label> <label> <input type="radio" name="latest">모든버전
							</label></td>
							<th>상태</th>
							<td>
								<%
									String[] boxKey = new String[] { "INWORK", "APPROVED" };
															String[] boxValue = new String[] { "작업중", "승인됨" };
								%> <%=html.selectBox("state", boxKey, boxValue, "wid100", state)%>
							</td>
						</tr>
					</table>


					<table class="btn_table">
						<tr>
							<td class="right"><input type="button" value="조회" id="searchBtn" title="조회"> <input type="button" value="초기화 (I)" id="initBtn" title="초기화 (I)"></td>
						</tr>
					</table>

					<div class="view_layer">
						<ul>
							<li id="list_view" class="active_view">리스트 뷰</li>
							<li id="img_view">이미지 뷰</li>
							<li><span id="thumbnail">썸네일 리스트로 확인하세요 </span></li>
						</ul>
					</div>

					<div class="paging_layer">
						<span class="page_count">15</span> <span class="page_count">30</span> <span class="page_count">50</span>
					</div>
					<table class="list_table indexed sortable-table">
						<%
							String[] widths = new String[] { "150", "*", "100", "100", "100", "100" };
											String[] keys = new String[] { "number", "name", "version", "states", "creator", "createDate" };
											String[] headers = new String[] { "부품번호", "부품명", "버전", "상태", "등록자", "등록일" };
											out.println(html.setHeader(headers, widths, keys, pager.getSort()));
											while (result.hasMoreElements()) {
												Object[] obj = (Object[]) result.nextElement();
												WTPart part = (WTPart) obj[0];
												PartViewData data = new PartViewData(part);
						%>
						<tr data-oid="<%=data.oid%>">
							<td data-column="number_column">
								<%
									out.println(html.setQuick(data.oid));
								%><%=data.number%></td>
							<td class="left indent10" data-column="name_column"><img class="pos3" src="<%=data.iconPath%>">&nbsp;<%=data.name%></td>
							<td data-column="version_column"><%=data.version%>.<%=data.iteration%></td>
							<td data-column="states_column"><%=data.state%></td>
							<td data-column="creator_column"><%=data.creator%></td>
							<td data-column="createDate_column"><%=data.createDate%></td>
						</tr>
						<%
							}
							if (result.size() == 0) {
						%>
						<tr>
							<td class="nodata" colspan="6">조회 결과가 없습니다.</td>
						</tr>
						<%
							}
						%>
					</table> <%
 	out.println(pager.paging());
 	out.println(html.setContextmenu(headers, keys));
 	out.println(html.loadingLayer());
 %>
				</td>
			</tr>
		</table>
		<%
			out.println(html.moveTop());
		%>
	</form>
</body>
</html>