<%@page import="java.util.Locale"%>
<%@page import="e3ps.common.code.CommonCodeType"%>
<%@page import="e3ps.org.service.OrgHelper"%>
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
	if (isAdmin) {
		isBox = false;
	}
	// module
	String module = ModuleKeys.list_code.name();
	System.out.println("모듈 이름 : " + module);
	// htmlUtils..
	HtmlUtils html = new HtmlUtils();
	// table header setting
	String[] headers = ColumnUtils.getColumnHeaders(module);
	
	System.out.println("headers 이름 : " + headers.toString());
	
	// table header key
	String[] keys = ColumnUtils.getColumnKeys(module);
	// table cols
	String[] cols = ColumnUtils.getColumnCols(module, headers);
	// table style
// 	String[] styles = ColumnUtils.getColumnStyles(module, headers);
	
	String[] styles = new String[]{"40", "40", "40", "40", "40"};
	
	String codetype = request.getParameter("codetype");
	
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
		
		// view url
		var url = "/Windchill/plm/admin/viewCode";
		grid.cellClickSmallPopup("name", url);
		grid.cellClickSmallPopup("read", url);
		grid.cellClickSmallPopup("role", url);
		grid.cellClickSmallPopup("type", url);
		grid.cellClickSmallPopup("objType", url);
		
		$.fn.getColumn = function() {
			
			var url = "/Windchill/plm/admin/listCodeAction";
			var params = grid.getParams();
			grid.preLoading();
			$(document).ajaxCallServer(url, params, function(data) {
				grid.getData(data, headers, "<%=isBox%>", false, url);
				grid.completeLoading(data.sessionid, data.curPage);
				$("input[name=oid]").checks();
			}, false);
		}
		
		$("#searchBtn").click(function() {
			$(document).getColumn();
		});
		
		
		
		
		$("#codeType").change(function() {
			console.log("###");
			$(document).getColumn();
		});
		
		
		
		
		grid.bindHeaderContextmenu();
		grid.allCheckbox();
		grid.listCheckbox();
		grid.setBoxs();
		
		$(".list_table").tableHeadFixer();
		$(document).getColumn();
		
	}).keypress(function(e) {
		var keyCode = e.keyCode;
		if(keyCode == 13 && e.target.tagName != "BUTTON") {
			$(document).getColumn();
		}
	})
	function create() {
		/* $cnt = $("span#agreeCnt").text();
		$("span#agreeCnt").text($cnt-1);
		$(document).getColumn(); */
	}
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
		<i class="axi axi-subtitles"></i><span>코드관리</span>
		<!-- info search -->
		<jsp:include page="/jsp/common/search_info.jsp"></jsp:include>
	</div>
	
	<!-- container table -->
	<table class="container_table">
		<tr>
			<!-- container -->
			<td id="container_td">
				<!-- create table -->
				<table class="search_table">
					<tr>
						<th>코드타입</th>
						<td>
							<select name="codeType" id="codeType" class="AXSelect wid200">
							<%
							String selectCodeTypeKey = "";
							CommonCodeType[] cct = CommonCodeType.getCommonCodeTypeSet();
							for(CommonCodeType codeType: cct){
								String codeTypeKey = codeType.toString();
								String typeName = codeType.getFullDisplay();
								boolean select = false;
								if(codeTypeKey.equals(codetype)){
									selectCodeTypeKey = codeTypeKey;
									select  = true;
								}
							%>
							
								<option value="<%=codeTypeKey %>" <%=select?"selected":"" %>><%=typeName %></option>
							<%
							}
							%>
							</select>
						</td>
						<th>코드</th>
						<td>
							<input type="text" name="code" id="code" class="AXInput wid200">
						</td>
					</tr>
					<tr>
						<th>이름</th>
						<td>
							<input type="text" name="name" id="name" class="AXInput wid200">
						</td>
						<th>설명</th>
						<td>
							<input type="text" name="description" id="description" class="AXInput wid200">
						</td>
					</tr>
				</table> 
				
				<!-- button table -->
				<table class="btn_table">
					<tr>
					<td>
							<!-- list jsp.. -->
							<div class="non_paging_layer">
								<span class="left_sub_data_search"> 
								<label title="모두보기"> 
									<input name="sub_data" id="sub_data" type="checkbox" value="ok">모두보기
								</label>
								</span> 							
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
							 <input type="button" value="등록" class="blueBtn" id="createCodesBtn" title="등록">
							 <input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회"> 
							 <input type="button" value="초기화" class="" id="initGrid" title="초기화">
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