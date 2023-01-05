<%@page import="e3ps.erp.service.ErpHelper"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="e3ps.doc.service.DocumentHelper"%>
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


//	ReferenceFactory rf = new ReferenceFactory();
	//WTDocument d = (WTDocument)rf.getReference("wt.doc.WTDocument:105810892").getObject();
	//ErpHelper.service.sendOutputToERP(d);


	// folder root
	String root = DocumentHelper.DEFAULT;
	// admin
	boolean isAdmin = CommonUtils.isAdmin();

	StateKeys[] states = (StateKeys[]) request.getAttribute("states");
	
	boolean isBox = true;
	if (isAdmin) {
		isBox = true;
	}
	// module
	String module = ModuleKeys.contents_list.name();

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
		var url = "/Windchill/plm/document/viewDocument";
		grid.cellClickSmallPopup("name", url);
		grid.cellClickSmallPopup("number", url);
	
		$.fn.getColumn = function() {
			var url = "/Windchill/plm/document/listContentsAction";
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
		$("#statesDoc").bindSelectSetValue("RELEASED");
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
		<i class="axi axi-subtitles"></i><span>첨부파일조회</span>
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
						<th>저장위치</th>
						<td colspan="7">
							<input type="hidden" name="location" value="<%=root%>"> <span id="location"><%=root%></span>
						</td>
					</tr>
					<tr>
						<th>파일이름</th>
						<td>
							<input type="text" name="fileName" class="AXInput wid200">
						</td>
						<th>문서이름</th>
						<td>
							<input type="text" name="name" class="AXInput wid200">
						</td>
						<!-- <th>MODEL_NAME</th>
						<td>
							<input type="text" name="MODEL_NAME" class="AXInput wid200">
						</td>	 -->					
						<th>설명</th>
						<td>
							<input type="text" name="description" class="AXInput wid200">
						</td>
						<th>문서번호</th>
						<td>
							<input type="text" name="number" class="AXInput wid200">
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
									for(StateKeys state : states) {
								%>
								<option value="<%=state.name() %>"><%=state.getDisplay() %></option>
								<%
									}
								%>
							</select>
						</td>
					</tr>		
<!-- 					<tr>	 -->
<!-- 						<th>검색 결과 값 조회</th> -->
<!-- 						<td colspan="3"> -->
<!-- 							<input type="text" class="AXInput wid200" id="table_search" name="table_search" placeholder="테이블 내 결과값 검색">&nbsp; -->
<!-- 							<i class="axi axi-ion-android-search" id="table_search_icon" title="테이블 내 검색"></i> -->
<!-- 						</td> -->
<!-- 					</tr>									 -->
				</table> 
				
				<!-- button table -->
				<table class="btn_table">
					<tr>
<!-- 				start sub table -->
						<td class="left">
							<!-- list jsp.. -->
							<div class="non_paging_layer">
								<span class="left_sub_folder_search"> 
								<label title="하위폴더검색"> 
									<input name="sub_folder" id="sub_folder" type="checkbox" value="ok" checked="checked">하위폴더검색
								</label>
								</span> 
								<span class="count_span"><span id="count_text"></span></span>
								<select name="paging_count" id="paging_count" class="AXSelectSmall">
									<option value="15">15</option>
									<option value="30">30</option>
									<option value="50">50</option>
									<option value="100">100</option>
								</select>
							</div>
						</td>
						<!-- 				end button table  -->
						<td class="right">
							 <input type="button" value="다운로드" class="redBtn" id="downDocContents" title="다운로드">
							 <input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회"> 
							 <input type="button" value="초기화" class="" id="initGrid" title="초기화">
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