<%@page import="e3ps.doc.service.DocumentHelper"%>
<%@page import="e3ps.org.service.OrgHelper"%>
<%@page import="e3ps.common.StateKeys"%>
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
	boolean isBox = true;
	boolean isDbl = StringUtils.parseBoolean(request.getParameter("dbl"));
	boolean isMulti = StringUtils.parseBoolean(request.getParameter("isMulti"));
	// 함수
	String fun = (String) request.getParameter("fun");
	
	// module
	String module = ModuleKeys.list_template.name();
	
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
		grid.gridStart(headers, "<%=isBox%>", "", <%=isMulti %>);
		
		// view url
		<%
			if(!isDbl) {
		%>
		var url = "/Windchill/plm/template/viewTemplate";
		grid.cellClick("name", url);
		grid.cellClick("number", url);
		<%
			} else {
		%>
		$(document).on("dblclick", ".list_tr", function(e) {
			$value = $(this).data("oid");
			templates.<%=fun %>(this, $value);
		})
		<%
			}
		%>
	
		$.fn.getColumn = function() {
			var url = "/Windchill/plm/template/listTemplateAction";
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
		grid.listCheckbox(<%=isMulti%>);
		grid.setBoxs();
		$(document).getColumn();
		// table header fixed table class name
		$(".list_table").tableHeadFixer();
		
		$("#addBtn").click(function() {
			templates.addTemplatesAction();
		})
		
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
		<i class="axi axi-subtitles"></i><span>템플릿 조회</span>
		<!-- info search -->
		<jsp:include page="/jsp/common/search_info.jsp"></jsp:include>
	</div>
	
	<!-- container table -->
	<table class="container_table">
		<tr>
			<!-- container -->
			<td id="container_td">
				<!-- search table -->
				<table class="search_table">
					<tr>
						<th>템플릿 이름</th>
						<td>
							<input type="text" name="name" class="AXInput wid200">
						</td>
						<th>기간</th>
						<td>
							<input type="text" name="duration" class="AXInput wid200">
						</td>
					</tr>
					<tr>
						<th>작성자</th>
						<td>
							<input type="text" name="creators" id="creators" class="AXInput wid200" data-dbl="true"> 
							<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="creators"></i>
						</td>
						<th>작성일</th>
						<td>
							<input type="text" name="predate" id="predate" class="AXInput"> ~ 
							<input type="text" name="postdate" id="postdate" class="AXInput twinDatePicker" data-start="predate"> 
							<i title="삭제" class="axi axi-ion-close-circled delete-calendar" data-start="predate" data-end="postdate"></i>
						</td>
					</tr>
					<tr>
						<th>수정자</th>
						<td>
							<input type="text" name="modifier" id="modifier" class="AXInput wid200" data-dbl="true"> 
							<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="modifier"></i>
						</td>
						<th>수정일</th>
						<td>
							<input type="text" name="predate_m" id="predate_m" class="AXInput"> ~ 
							<input type="text" name="postdate_m" id="postdate_m" class="AXInput twinDatePicker_m" data-start="predate_m"> 
							<i title="삭제" class="axi axi-ion-close-circled delete-calendar" data-start="predate_m" data-end="postdate_m"></i>
						</td>
					</tr>
					<%-- <tr>
						<th>상태</th>
						<td>
							<select name="statesTemp" id="statesTemp" class="AXSelect wid200">
								<option value="">선택</option>
								<%
									for(StateKeys state : states) {
								%>
								<option value="<%=state.name() %>"><%=state.getDisplay() %></option>
								<%
									}
								%>
							</select>
						</td>
						<th>검색 결과 값 조회</th>
						<td>
							<input type="text" class="AXInput wid200" id="table_search" name="table_search" placeholder="테이블 내 결과값 검색">&nbsp;
							<i class="axi axi-ion-android-search" id="table_search_icon" title="테이블 내 검색"></i>
						</td>
					</tr>	 --%>					
				</table> 
				
				<!-- button table -->
				<table class="btn_table">
					<tr>
						<td>
							<!-- list jsp.. -->
							<div class="non_paging_layer">
<!-- 								<span class="left_sub_folder_search">  -->
<!-- 								<label title="하위폴더검색">  -->
<!-- 									<input name="sub_folder" id="sub_folder" type="checkbox" value="ok" checked="checked">하위폴더검색 -->
<!-- 								</label> -->
<!-- 								</span>  -->
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
						<td class="right">
							 <input type="button" value="추가" class="redBtn" id="addBtn" title="추가">  
							 <input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회"> 
							 <input type="button" value="초기화" id="init_table" title="초기화">
							 <input type="button" value="닫기" class="redBtn" id="popupPartBtn" title="닫기">
						</td>
					</tr>
				</table> 
				<!-- end button table --> 
				
				<!-- start sub table -->
				<!-- <table class="sub_table">
					<tr>
						
					</tr>
				</table>  -->
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
 	if(!isDbl) {
	 	out.println(html.setContextmenu(module));
	 	// context
	 	out.println(html.setRightMenu(module));
	 	// multi context
 		out.println(html.setRightMenuMulti(module));
 	}
 %>
</td>