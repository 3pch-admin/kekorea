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
	// folder root
	String root = PartHelper.EPLAN_ROOT;
	
	StateKeys[] states = (StateKeys[]) request.getAttribute("states");

	// admin
	boolean isAdmin = CommonUtils.isAdmin();
	boolean isBox = true;
	boolean isDbl = StringUtils.parseBoolean(request.getParameter("dbl"));
	// 함수
	String fun = (String) request.getParameter("fun");
	
	// module
	String module = ModuleKeys.add_list_document.name();
	
	// context check
	String context = (String) request.getParameter("context");
	
	// 추가 상태
	String state = (String) request.getParameter("state");
	
	String location = (String) request.getParameter("location");
	
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
		
		// view url
		<%
			if(!isDbl) {
		%>
		var url = "/Windchill/plm/part/viewDocument";
		grid.cellClick("name", url);
		grid.cellClick("number", url);
		<%
			} else {
		%>
		$(document).on("dblclick", ".list_tr", function(e) {
			$value = $(this).data("oid");
			documents.<%=fun %>(this, $value, "<%=state %>");
		})
		<%
			}
		%>
	
		var url = "/Windchill/plm/part/listDocumentAction";
		$.fn.getColumn = function() {
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
		
		// table header fixed table class name
		$(".list_table").tableHeadFixer();
		
		$("#addBtn").click(function() {
			documents.addDocumentsAction("<%=state %>");
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
		<i class="axi axi-subtitles"></i><span>문서 조회</span>
		<!-- info search -->
		
			<jsp:include page="/jsp/common/search_info.jsp"></jsp:include>
		
	</div>
	
	<!-- container table -->
	<table class="container_table">
		<tr>
				<jsp:include page="/jsp/common/layouts/include_tree.jsp">
					<jsp:param value="<%=root%>" name="root" />
					<jsp:param value="EPLAN" name="context" />
				</jsp:include>
			<!-- container -->
			<td id="container_td">
				<!-- search table -->
				<table class="search_table">
					<tr>
						<th>문서분류</th>
						<td colspan="7">
							<input type="hidden" name="location" value="<%=root%>"> <span id="location"><%=root%></span>
						</td>
					</tr>
					<tr>
						<th>문서제목</th>
						<td>
							<input type="text" name="name" class="AXInput wid200">
						</td>
						<th>문서번호</th>
						<td>
							<input type="text" name="number" class="AXInput wid200">
						</td>
						<th>설명</th>
						<td>
							<input type="text" name="description" class="AXInput wid200">
						</td>
						<th>작번</th>
						<td>
							<input type="text" name="kNumber" class="AXInput wid200">
						</td>
					</tr>	
					<tr>
						<th>KEK 작번</th>
						<td>
							<input type="text" name="kekNumber" class="AXInput wid200">
						</td>
						<th>막종</th>
						<td>
							<input type="text" name="mak" class="AXInput wid200">
						</td>
						<th>작업내용</th>
						<td colspan="3">
							<input type="text" name="description2" class="AXInput wid400">
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
						<th>버전</th>
						<td>
							<label title="최신버전"> 
								<input type="radio" name="latest" value="true" checked="checked"> 
								<span class="latest">최신버전</span>
							</label> 
							<label title="모든버전"> 
								<input type="radio" name="latest" value="false"> 
								<span class="latest">모든버전</span>
							</label>
						</td>
						<th>상태</th>
						<td>
							<select name="statesDoc" id="statesDoc" class="AXSelect wid200">
								<option value="">선택</option>
								<%
									for(StateKeys stateData : states) {
								%>
								<option value="<%=stateData.name() %>"><%=stateData.getDisplay() %></option>
								<%
									}
								%>
							</select>
						</td>
					</tr>					
				</table>
				
				<!-- button table -->
				<table class="btn_table">
					<tr>
						<td>
							<!-- list jsp.. -->
							<div class="non_paging_layer">
								<%if(!("SPEC".equals(location) || "OLDSPEC".equals(location)) ) { %>
									<span class="left_sub_folder_search"> 
									<label title="하위폴더검색"> 
										<input name="sub_folder" id="sub_folder" type="checkbox" value="ok" checked="checked">하위폴더검색
									</label>
									</span> 
								<%} %>
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