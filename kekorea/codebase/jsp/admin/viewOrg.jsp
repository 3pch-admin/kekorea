<%@page import="e3ps.org.service.OrgHelper"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.document.column.DocumentColumnKeys"%>
<%@page import="e3ps.document.service.DocumentHelper"%>
<%@page import="java.util.HashMap"%>
<%@page import="e3ps.document.column.DocumentColumnData"%>
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

	// dept root
	String root = OrgHelper.DEPARTMENT_ROOT;

	// admin
	boolean isAdmin = CommonUtils.isAdmin();

	boolean isBox = true;
	if (isAdmin) {
		isBox = true;
	}
	// module
	String module = ModuleKeys.list_user.name();

	// htmlUtils..
	HtmlUtils html = new HtmlUtils();
	// table header setting
	String[] headers = ColumnUtils.getColumnHeaders(module);
	// table header key
	String[] keys = ColumnUtils.getColumnKeys(module);
	// table cols
	String[] cols = ColumnUtils.getColumnCols(module, headers);
	// table style
	String[] styles = ColumnUtils.getColumnStyles(module, headers);
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
		
// 		view url
		var url = "/Windchill/plm/org/viewUser";
		grid.cellClick("name", url);
		grid.cellClick("id", url);
	
		$.fn.getColumn = function() {
			var url = "/Windchill/plm/org/viewOrgAction";
			var params = grid.getParams();
			grid.preLoading();
			$(document).ajaxCallServer(url, params, function(data) {
				grid.getData(data, headers, "<%=isBox%>", false, url);
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
		
		// delete
		// table header fixed table class name
		$(".list_table").tableHeadFixer();
		//$('.sortable-table').tableSorter();
		$("#resigns").bindSelect();
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
		<i class="axi axi-subtitles"></i><span>사용자 관리</span>
		<!-- info search -->
		<jsp:include page="/jsp/common/search_info.jsp"></jsp:include>
	</div>
	
	<!-- container table -->
	<table class="container_table">
		<tr>
			<!-- only folder tree.. -->
			<jsp:include page="/jsp/common/layouts/include_dept.jsp" />
			<!-- container -->
			<td id="container_td">
				<!-- search table -->
				<table class="search_table">
					<tr>
						<th>부서</th>
						<td>
							<input type="hidden" name="deptOid" id="deptOid">
							<span id="deptName"><%=root %></span>
						</td>
						<th>퇴사여부</th>
						<td>
							<select name="resigns" id="resigns" class="AXSelect wid100">
								<option value="">선택</option>
								<option value="false" selected="selected">재직중</option>
								<option value="true">퇴사</option>
							</select>
						</td>
					</tr>
					<tr>
						<th>아이디</th>
						<td>
							<input type="text" name="id" class="AXInput wid200">
						</td>
						<th>이름</th>
						<td>
							<input type="text" name="name" class="AXInput wid200">
						</td>
					</tr>
					<tr>	
						<th>검색 결과 값 조회</th>
						<td colspan="3">
							<input type="text" class="AXInput wid200" id="table_search" name="table_search" placeholder="테이블 내 결과값 검색">&nbsp;
							<i class="axi axi-ion-android-search" id="table_search_icon" title="테이블 내 검색"></i>
						</td>
					</tr>						
				</table> 
				
				<!-- button table -->
				<table class="btn_table">
					<tr>
						<td class="right">
							<%
								if (isAdmin) {
							%> 
							<input type="button" value="퇴사처리" class="redBtn" id="resignListUserBtn" title="퇴사처리"> 
							<%
							 	}
							 %> 
							 <input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회"> 
							 <input type="button" value="초기화" class="" id="initGrid" title="초기화">
						</td>
					</tr>
				</table> 
				<!-- end button table --> 
				
				<!-- start sub table -->
				<table class="sub_table">
					<tr>
						<td>
							<!-- list jsp.. -->
							<div class="non_paging_layer">
								<span class="sub_folder_search"> 
								<label title="하위부서검색"> 
									<input name="sub_folder" id="sub_folder" type="checkbox" value="ok">하위부서검색
								</label>
								</span>
								<span class="count_span"><span id="count_text"></span></span>	
								<%
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
					</tr>
				</table>  
				<!-- end sub_table --> 
				
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
			</td>
		</tr>
	</table>

	<table class="page_table" id="grid_paginate">
	</table> 
<%
 	// header context
 	out.println(html.setContextmenu(module));
 	// context
 	out.println(html.setRightMenu(module));
 	// multi context
 	out.println(html.setRightMenuMulti(module));
 %>
</td>