
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
	
	boolean isBox = false;
	if(isAdmin) {
		isBox = true;
	}
	// module
	String module = ModuleKeys.list_partlist.name();
	// htmlUtils..
	HtmlUtils html = new HtmlUtils();
	// table header setting
	String[] headers = ColumnUtils.getColumnHeaders(module);
	// table header key
	String[] keys = ColumnUtils.getColumnKeys(module);
	// table cols
	String[] cols = ColumnUtils.getColumnCols(module, headers);
	// table style
// 	String[] styles = ColumnUtils.getColumnStyles(module, headers);
	//컬럼
	String[] styles = new String[]{"40", "60","30", "300", "150", "110", "150", "100", "300", "150", "80", "80", "150", "80", "110", "110", "80"};
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
		var url = "/Windchill/plm/partList/viewPartListMaster";
		grid.cellClickSmallPopup("name", url);
		grid.cellClickSmallPopup("number", url);
		grid.cellClickPopupValue("kekNumber", "/Windchill/plm/project/viewProjectByKekNumber");
		
		grid.cellClickStandardPopup("info", "/Windchill/plm/partList/viewPartListMasterInfo");
	
		$.fn.getColumn = function() {
			var url = "/Windchill/plm/partList/listPartListAction";
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
		
 		$("#engType").bindSelect();
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
		<i class="axi axi-subtitles"></i><span>수배표 조회</span>
		<!-- info search -->
		<jsp:include page="/jsp/common/search_info.jsp"></jsp:include>
	</div>
	
	<!-- container table -->
	<table class="container_table">
		<tr>
			<!-- only folder tree.. -->
			<%-- <jsp:include page="/jsp/common/layouts/include_tree.jsp">
				<jsp:param value="<%=root%>" name="root" />
				<jsp:param value="PRODUCT" name="context" />
			</jsp:include> --%>
			<!-- container -->
			<td id="container_td">
				<!-- search table -->
				<table class="search_table">
					<tr>
						<th>수배표 제목</th>
						<td>
							<input type="text" name="name" class="AXInput wid200">
						</td>
						<th>상태</th>
						<td>
							<select name="statesDoc" id="statesDoc" class="AXSelect wid200">
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
						<th>KEK 작번</th>
						<td>
							<input type="text" name="kekNumber" class="AXInput wid200">
						</td>
						<th>KE 작번</th>
						<td>
							<input type="text" name="keNumber" class="AXInput wid200">
						</td>
					</tr>
					<tr>
						<th>설명</th>
						<td>
							<input type="text" name="description" class="AXInput wid200">
						</td>
						<th>설계 구분</th>
						<td>
							<select name="engType" id="engType" class="AXSelect wid100">
								<option value="">선택</option>
								<option value="개조">개조</option>
								<option value="견적">견적</option>
								<option value="양산">양산</option>
								<option value="연구개발">연구개발</option>
								<option value="이설">이설</option>
								<option value="판매">판매</option>
								<option value="평가용">평가용</option>
							</select>
						</td>
						<th>막종</th>
						<td>
							<input type="text" name="mak" class="AXInput wid200">
						</td>
						<th>작업 내용</th>
						<td>
							<input type="text" name="pDescription" class="AXInput wid200">
						</td>
					</tr>														
					<tr class="detailEpm">
						<th>작성자</th>
						<td>
							<input type="text" name="creators" id="creators" class="AXInput wid200" data-dbl="true"> 
							<input type="hidden" name="creatorsOid" id="creatorsOid" class="AXInput wid200" data-dbl="true"> 
							<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="creators"></i>
						</td>
						<th>작성일</th>
						<td>
							<input type="text" name="predate" id="predate" class="AXInput"> ~ 
							<input type="text" name="postdate" id="postdate" class="AXInput twinDatePicker" data-start="predate"> 
							<i title="삭제" class="axi axi-ion-close-circled delete-calendar" data-start="predate" data-end="postdate"></i>
						</td>
						<th>수정자</th>
						<td>
							<input type="text" name="modifier" id="modifier" class="AXInput wid200" data-dbl="true"> 
							<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="creators"></i>
						</td>
						<th>수정일</th>
						<td>
							<input type="text" name="predate_m" id="predate_m" class="AXInput"> ~ 
							<input type="text" name="pstdate_m" id="postdate_m" class="AXInput twinDatePicker_m" data-start="predate_m"> 
							<i title="삭제" class="axi axi-ion-close-circled delete-calendar" data-start="predate_m" data-end="postdate_m"></i>
						</td>
					</tr>
				</table> 
				
				<!-- button table -->
				<table class="btn_table">
					<tr>
					<!-- start sub table -->
						<td class="left">
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
							<%
								if (isAdmin) {
							%> 
							<input type="button" value="삭제" class="redBtn" id="deletePartListBtn" title="삭제"> 
							<%
							 	}
							 %> 
							 <input type="button" value="상세조회" class="orangeBtn" id="detailEpmBtn" title="상세조회"> 
							 <input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회"> 
							 <input type="button" value="초기화" class="" id="initGrid" title="초기화" data-location="<%=root %>">
						</td>
					</tr>
				</table> 
				<!-- end button table --> 
				
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