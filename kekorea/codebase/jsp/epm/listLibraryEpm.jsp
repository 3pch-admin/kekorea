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
	// folder root
	String root = EpmHelper.LIBRARY_ROOT;
	// admin
	boolean isAdmin = CommonUtils.isAdmin();

	boolean isBox = true;
	if (isAdmin) {
		isBox = true;
	}
	// module
	String module = ModuleKeys.list_library_epm.name();

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
	String[] 	styles = new String[]{"50", "50", "250", "250", "250", "100", "180", "180", "180", "60", "80", "110", "80", "110", "80", "140", "40"};
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
		grid.cellClickSmallPopup("name", url);
		grid.cellClickSmallPopup("number", url);
	
		$.fn.getColumn = function() {
			var url = "/Windchill/plm/epm/listLibraryEpmAction";
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
		<i class="axi axi-subtitles"></i><span>??????????????? ??????</span>
		<!-- info search -->
		<jsp:include page="/jsp/common/search_info.jsp"></jsp:include>
	</div>
	
	<!-- container table -->
	<table class="container_table">
		<tr>
			<!-- only folder tree.. -->
			<jsp:include page="/jsp/common/layouts/include_tree.jsp">
				<jsp:param value="<%=root%>" name="root" />
				<jsp:param value="LIBRARY" name="context" />
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
						<th>DWG_NO</th>
						<td>
							<input type="text" name="number" class="AXInput wid200">
						</td>
						<th>PART_CODE</th>
						<td>
							<input type="text" name="partCode" class="AXInput wid200">
						</td>
						<th>?????????</th>
						<td>
							<input type="text" name="partName" class="AXInput wid200">
						</td>
						<th>MAKER</th>
						<td>
							<input type="text" name="maker" class="AXInput wid200">
						</td>
					</tr>
					<tr>
						<th>MATERIAL</th>
						<td>
							<input type="text" name="material" class="AXInput wid200">
						</td>
						<th>REMARK</th>
						<td>
							<input type="text" name="remark" class="AXInput wid200">
						</td>
						<th>??????</th>
						<td>
							<select name="statesEpm" id="statesEpm" class="AXSelect wid200">
								<option value="">??????</option>
								<%
								String[] displays = EpmHelper.EPM_STATE_DISPLAY;
								String[] values = EpmHelper.EPM_STATE_VALUE;
									for(int i=0; i<displays.length; i++) {
								%>
								<option value="<%=values[i] %>"><%=displays[i] %></option>
								<%
									}
								%>
							</select>
						</td>
						<th>??????</th>
						<td>
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
					<tr>
						<th>?????????</th>
						<td>
							<input type="text" name="creators" id="creators" class="AXInput wid200" data-dbl="true"> 
							<i title="??????" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="creators"></i>
						</td>
						<th>?????????</th>
						<td>
							<input type="text" name="predate" id="predate" class="AXInput"> ~ 
							<input type="text" name="postdate" id="postdate" class="AXInput twinDatePicker" data-start="predate"> 
							<i title="??????" class="axi axi-ion-close-circled delete-calendar" data-start="predate" data-end="postdate"></i>
						</td>
						<th>?????????</th>
						<td>
							<input type="text" name="modifier" id="modifier" class="AXInput wid200" data-dbl="true"> 
							<i title="??????" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="modifier"></i>
						</td>
						<th>?????????</th>
						<td>
							<input type="text" name="predate_m" id="predate_m" class="AXInput"> ~ 
							<input type="text" name="postdate_m" id="postdate_m" class="AXInput twinDatePicker_m" data-start="predate_m"> 
							<i title="??????" class="axi axi-ion-close-circled delete-calendar" data-start="predate_m" data-end="postdate_m"></i>
						</td>
					</tr>	
					<!-- <tr>
						<th>??????</th>
						<td colspan="3">
							<label title="ASM"> 
								<input type="checkbox" name="partTypes" value="ASM"> 
								<span class="latest">ASM</span>
							</label> 
							<label title="PRT"> 
								<input type="checkbox" name="partTypes" value="PRT"> 
								<span class="latest">PRT</span>
							</label>
							<label title="DRW"> 
								<input type="checkbox" name="partTypes" value="DRW"> 
								<span class="latest">DRW</span>
							</label>
							<label title="AUTOCAD"> 
								<input type="checkbox" name="partTypes" value="AUTOCAD"> 
								<span class="latest">AUTOCAD</span>
							</label>
							<label title="EPLAN"> 
								<input type="checkbox" name="partTypes" value="EPLAN"> 
								<span class="latest">EPLAN</span>
							</label>
							<label title="??????"> 
								<input type="checkbox" name="partTypes" value="chkPart"> 
								<span class="latest">??????</span>
							</label>
							<label title="ALL"> 
								<input type="checkbox" name="partTypes" value="chkAll"> 
								<span class="latest">ALL</span>
							</label>
						</td>
					</tr> -->
									
					<%-- <tr>
						<th>CAD ??????</th>
						<td>
							<select name="epmTypes" id="epmTypes" class="AXSelect wid200">
								<option value="">??????</option>
								<%
									String[] displays = EpmHelper.CADTYPE_DISPLAY;
									String[] values = EpmHelper.CADTYPE_VALUE;
									for(int i=0; i<displays.length; i++) {
								%>
								<option value="<%=values[i] %>"><%=displays[i] %></option>
								<%
									}
								%>
							</select>
						</td>
											
					</tr>	 --%>																						
				</table>  
				
				<!-- button table -->
				<table class="btn_table">
					<tr>
						<td>
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
<!-- 							<input type="button" value="????????????" class="greenBtn" id="createViewer" title="????????????">  -->
							 <input type="button" value="??????" class="blueBtn" id="searchBtn" title="??????"> 
							 <input type="button" value="?????????" class="" id="initGrid" title="?????????">
						</td>
					</tr>
				</table> 
				<!-- end button table --> 
				
				<!-- start sub table -->
				<!-- <table class="sub_table">
					<tr>
						<td>
							<div class="view_layer">
								<ul>
									<li id="list_view_epm" class="active_view" title="????????? ???">????????? ???</li>
									<li id="img_view_epm" title="????????? ???">????????? ???</li>
									<li class="hidden"><span id="thumbnail" class="hidden" title="????????? ???????????? ???????????????.">????????? ???????????? ??????????????? </span></li>
								</ul>
							</div>
						</td>
					</tr>
				</table>  -->
				<!-- end sub_table --> 
				
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
 	out.println(html.setContextmenu(module));
 	// context
 	out.println(html.setRightMenu(module));
 	// multi context
 	out.println(html.setRightMenuMulti(module));
 %>
</td>