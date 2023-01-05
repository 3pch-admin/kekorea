<%@page import="e3ps.org.service.OrgHelper"%>
<%@page import="wt.epm.EPMDocumentType"%>
<%@page import="e3ps.epm.service.EpmHelper"%>
<%@page import="e3ps.part.service.PartHelper"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="e3ps.common.util.HtmlUtils"%>
<%@page import="e3ps.common.util.ColumnUtils"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@page import="wt.fc.PagingQueryResult"%>
<%@page import="e3ps.common.ModuleKeys"%>
<%@page import="e3ps.common.util.PageQueryUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	// admin
	boolean isAdmin = CommonUtils.isAdmin();

	boolean isBox = false;
// 	if (isAdmin) {
// 		isBox = true;
// 	}
	// module
	String module = ModuleKeys.list_viewer.name();

	// htmlUtils..
	HtmlUtils html = new HtmlUtils();
	// table header setting
	String[] headers = ColumnUtils.getColumnHeaders(module);
	// table header key
	String[] keys = ColumnUtils.getColumnKeys(module);
	// table cols
	String[] cols = ColumnUtils.getColumnCols(module, headers);
	// table style
	//String[] styles = ColumnUtils.getColumnStyles(module, headers);
	String[] 	styles = new String[]{"40", "250", "250", "250", "80", "80", "40"};
%>
<td valign="top">
	<!-- script area --> 
	<script type="text/javascript">
	$(document).ready(function() {

		var headers = [
			<%for (int i = 0; i < headers.length; i++) {%>
			{
				display : "<%=headers[i]%>",
				key : "<%=keys[i]%>",
				style : <%=styles[i]%>,
			},
			<%}%>
		]
		grid.gridStart(headers, "<%=isBox%>");
// 		grid.gridImgStart();
		
		// view url
		var url = "/Windchill/plm/epm/viewEpm";
// 		grid.cellClickSmallPopup("name", url);
// 		grid.cellClickSmallPopup("number", url);
	
		$.fn.getColumn = function() {
			var url = "/Windchill/plm/epm/listViewerAction";
			var params = grid.getParams();
		
			grid.preLoading();
			$(document).ajaxCallServer(url, params, function(data) {
				grid.getData(data, headers, "<%=isBox%>", false, url, true);
<%-- 				grid.getImgData(data, headers, "<%=isBox%>", false, url); --%>
				grid.completeLoading(data.sessionid, data.curPage);
				$("input[name=oid]").checks();
			}, false);
		}
		
		// search 
		$("#searchBtn").click(function() {
			$(document).getColumn();
		})
		
		grid.bindHeaderContextmenu();
		grid.allCheckbox();
		grid.listCheckbox();
		grid.setBoxs();
		
		// table header fixed table class name
		$(".list_table").tableHeadFixer();
		//$('.sortable-table').tableSorter();
	}).keypress(function(e) {
		var keyCode = e.keyCode;
		if(keyCode == 13 && e.target.tagName != "BUTTON") {
			$(document).getColumn();
		}
	})
	</script> <!-- items.. --> 
	<input type="hidden" name="items" id="items"> 
	<!-- module -->
	<input type="hidden" name="module" id="module" value="<%=module%>"> 
	<!-- paging script -->
	<input type="hidden" name="sessionid" id="sessionid">
	<input type="hidden" name="tpage" id="tpage">
	<input type="hidden" name="psize" id="psize">
	
	<!-- list header title -->
	<div class="header_title">
		<i class="axi axi-subtitles"></i><span>뷰어 조회</span>
		<!-- info search -->
		<jsp:include page="/jsp/common/search_info.jsp"></jsp:include>
	</div>
	
	<!-- container table -->
	<table class="container_table">
		<tr>
			<td id="container_td">
				<!-- search table -->
				<table class="search_table">
					<tr>
						<th>품명</th>
						<td>
							<input type="text" name="name" class="AXInput wid200">
						</td>
						<th>규격</th>
						<td>
							<input type="text" name="number" class="AXInput wid200">
						</td>
					</tr>
					<tr>
						<th>작성자</th>
						<td>
							<input type="text" name="creators" id="creators" class="AXInput wid200" data-dbl="true"> 
							<input type="hidden" name="creatorsOid" id="creatorsOid" class="AXInput wid200" data-dbl="true"> 
							<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="creators"></i>
						</td>
						<th>파일이름</th>
						<td>
							<input type="text" name="fileName" class="AXInput wid200">
						</td>
					</tr>	
					<tr>
						<th>작성일</th>
						<td colspan="3">
							<input type="text" name="predate" id="predate" class="AXInput"> ~ 
							<input type="text" name="postdate" id="postdate" class="AXInput twinDatePicker" data-start="predate"> 
							<i title="삭제" class="axi axi-ion-close-circled delete-calendar" data-start="predate" data-end="postdate"></i>
						</td>
					</tr>
				</table>  
				
				<!-- button table -->
				<table class="btn_table">
					<tr>
						<td>
							<!-- list jsp.. -->
							<div class="non_paging_layer">
								<%
									if(isBox) {
								%>
								<span class="count_span"><span id="count_text"></span></span>
								<%
									}
									String psize = OrgHelper.manager.getUserPaging(module);
								%>
								<select name="paging_count" id="paging_count" class="AXSelectSmall">
									<option value="15" <%if(psize.equals("15")) { %> selected="selected"  <%} %>>15</option>
									<option value="30" <%if(psize.equals("30")) { %> selected="selected"  <%} %>>30</option>
									<option value="50" <%if(psize.equals("50")) { %> selected="selected"  <%} %>>50</option>
									<option value="100" <%if(psize.equals("100")) { %> selected="selected"  <%} %>>100</option>
								</select>
							</div>
						</td>
						<td class="right">
							 <input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회"> 
							 <input type="button" value="초기화" class="" id="initGrid" title="초기화">
						</td>
					</tr>
				</table> 
				<!-- end button table --> 
				
				<!-- object list div area -->
		        <table id="tblBackground">
		            <tr>
		                <td>
							<div class="list_container">
								<table class="list_table indexed sortable-table">
									<thead id="grid_header">
									</thead>
									<tbody id="grid_list">
									</tbody>
								</table>
							</div> 
						</td>
					</tr>
				</table>
				
				<div class="img_container">
					<!-- object img table -->
					<table class="img_table">
						<tbody id="grid_img">
						</tbody>
					</table>
					<!-- end img table -->
				</div>
			</td>
		</tr>
	</table>

	<table class="page_table" id="grid_paginate">
	</table> 
<%
 	// header context
//  	out.println(html.setContextmenu(module));
 	// context
//  	out.println(html.setRightMenu(module));
 	// multi context
//  	out.println(html.setRightMenuMulti(module));
 %>
</td>