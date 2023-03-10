<%@page import="wt.content.ContentHelper"%>
<%@page import="e3ps.doc.service.DocumentHelper"%>
<%@page import="e3ps.org.service.OrgHelper"%>
<%@page import="e3ps.common.StateKeys"%>
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
	// folder root
	String root = DocumentHelper.ROOT;
	// admin
	boolean isAdmin = CommonUtils.isAdmin();

	StateKeys[] states = (StateKeys[]) request.getAttribute("states");
	
	boolean isBox = true;
	// module
	String module = ModuleKeys.list_document.name();
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
	
	String[] styles = new String[]{"40", "200", "200", "300", "200", "60", "60", "80", "110", "80", "110", "40"};
// 	styles = new String[]{"40", "200", "200", "200", "200", "200", "200", "200", "200", "200"};
%>
<%@include file="/jsp/common/layouts/include_css.jsp" %>
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
		
		// view url
		var url = "/Windchill/plm/document/viewDocument";
		grid.cellClickSmallPopup("name", url);
		grid.cellClickSmallPopup("number", url);
		grid.cellClickSmallPopup("modelName", url);
	
		$.fn.getColumn = function() {
			var url = "/Windchill/plm/document/listDocumentAction";
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
		
		$(".list_table").tableHeadFixer();
// 		//$('.sortable-table').tableSorter();
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
		<i class="axi axi-subtitles"></i><span>?????? ??????</span>
		<!-- info search -->
		<jsp:include page="/jsp/common/search_info.jsp"></jsp:include>
	</div>
	
	<!-- container table -->
	<table class="container_table">
		<tr>
			<!-- only folder tree.. -->
			<jsp:include page="/jsp/common/layouts/include_tree.jsp">
				<jsp:param value="<%=root%>" name="root" />
				<jsp:param value="PRODUCT" name="context" />
			</jsp:include>
			<!-- container -->
			<td id="container_td">
				<!-- search table -->
				<table class="search_table">
					<tr>
						<th>????????????</th>
						<td colspan="7">
							<input type="hidden" name="location" value="<%=root%>"> <span id="location"><%=root%></span>
						</td>
					</tr>
					<tr>
						<th>????????????</th>
						<td>
							<input type="text" name="name" id="name" class="AXInput wid200">
						</td>
						<th>????????????</th>
						<td>
							<input type="text" name="number" class="AXInput wid200">
						</td>
						<th>??????</th>
						<td>
							<input type="text" name="description" class="AXInput wid200">
						</td>
						<th>??????</th>
						<td>
							<select name="statesDoc" id="statesDoc" class="AXSelect wid200">
								<option value="">??????</option>
								<%
									for(StateKeys state : states) {
								%>
								<option value="<%=state.name() %>"><%=state.getDisplay() %></option>
								<%
									}
								%>
							</select>
						</td>
<!-- 						<th>KE ??????</th> -->
<!-- 						<td> -->
<!-- 							<input type="text" name="keNumber" class="AXInput wid200"> -->
<!-- 						</td> -->
					</tr>	
					<tr>
<!-- 						<th>KEK ??????</th> -->
<!-- 						<td> -->
<!-- 							<input type="text" name="kekNumber" class="AXInput wid200"> -->
<!-- 						</td> -->
<!-- 						<th>??????</th> -->
<!-- 						<td> -->
<!-- 							<input type="text" name="mak" class="AXInput wid200"> -->
<!-- 						</td> -->
					</tr>		
					<tr class="detailEpm">
<!-- 						<th>????????????</th> -->
<!-- 						<td colspan="3"> -->
<!-- 							<input type="text" name="kek_description" class="AXInput wid400"> -->
<!-- 						</td> -->
					</tr>												
					<tr class="detailEpm">
						<th>?????????</th>
						<td>
							<input type="text" name="creators" id="creators" class="AXInput wid200" data-dbl="true"> 
							<input type="hidden" name="creatorsOid" id="creatorsOid" class="AXInput wid200" data-dbl="true"> 
							<i title="??????" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="creators"></i>
						</td>
						<th>?????????</th>
						<td>
							<input type="text" name="predate" id="predate" class="AXInput"> ~ 
							<input type="text" name="postdate" id="postdate" class="AXInput twinDatePicker" data-start="predate"> 
							<i title="??????" class="axi axi-ion-close-circled delete-calendar" data-start="predate" data-end="postdate"></i>
						</td>
						<th>??????</th>
						<td colspan="3">
							<label title="????????????"> 
								<input type="radio" name="latest" value="true" checked="checked"> 
								<span class="latest">????????????</span>
							</label> 
							<label title="????????????"> 
								<input type="radio" name="latest" value="false"> 
								<span class="latest">????????????</span>
							</label>
						</td>
					</tr>					
				</table> 
				
				<!-- button table -->
				<table class="btn_table">
					<tr>
					<td class="left">
							<!-- list jsp.. -->
							<div class="non_paging_layer">
								<span class="left_sub_folder_search"> 
								<label title="??????????????????"> 
									<input name="sub_folder" id="sub_folder" type="checkbox" value="ok" checked="checked">??????????????????
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
						<td class="right">
							<%
								if (isAdmin) {
							%> 
							<input type="button" value="??????" class="redBtn" id="deleteListDocBtn" title="??????"> 
							<%
							 	}
							 %> 
							 <input type="button" value="????????????" class="orangeBtn" id="detailEpmBtn" title="????????????"> 
							 <input type="button" value="??????" class="blueBtn" id="searchBtn" title="??????"> 
							 <input type="button" value="?????????" class="" id="initGrid" title="?????????" data-location="<%=root %>">
						</td>
					</tr>
				</table> 
				<!-- end button table --> 
				
				<!-- start sub table -->
				<table class="sub_table">
					<tr>
						
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