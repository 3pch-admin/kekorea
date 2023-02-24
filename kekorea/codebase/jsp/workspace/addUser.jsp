<%@page import="e3ps.common.util.ColumnUtils"%>
<%@page import="e3ps.common.util.HtmlUtils"%>
<%@page import="e3ps.common.ModuleKeys"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@page import="e3ps.org.service.OrgHelper"%>
<%@page import="e3ps.common.util.PageQueryUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	// folder root
	// admin
	boolean isAdmin = CommonUtils.isAdmin();
	boolean isBox = true;
	boolean isMulti = StringUtils.parseBoolean(request.getParameter("multi"));
	boolean isDbl = StringUtils.parseBoolean(request.getParameter("dbl"));
	
	String target = request.getParameter("target");
	
	// 함수
	String fun = (String) request.getParameter("fun");

	String root = OrgHelper.DEPARTMENT_ROOT;

	// module
	String module = ModuleKeys.add_list_user.name();
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
	<!-- script area --> <script type="text/javascript">
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
		<%if (!isDbl) {%>
		var url = "/Windchill/plm/document/viewDocument";
		grid.cellClick("name", url);
		grid.cellClick("number", url);
		<%} else {%>
		$(document).on("dblclick", ".list_tr", function(e) {
			$value = $(this).data("oid");
			orgs.<%=fun%>(this, $value, "<%=target %>");
		})
		<%}%>
	
		$.fn.getColumn = function() {
			var url = "/Windchill/plm/org/listUserAction";
			var params = grid.getParams();
			
			if($("#resigns").prop("checked") == true) {
				params.resigns = $("#resigns").val();				
			}
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
		grid.listCheckbox("<%=isMulti %>");
		grid.setBoxs();
		

		// table header fixed table class name
		$(".list_table").tableHeadFixer();
		$("#resigns").checks();
		$("#addBtn").click(function() {
			orgs.addUserAction("<%=target %>");
		})
		
	}).keypress(function(e) {
		var keyCode = e.keyCode;
		if(keyCode == 13 && e.target.tagName != "BUTTON") {
			$(document).getColumn();
		}
	})
	</script> <!-- items.. --> <input type="hidden" name="items" id="items"> <!-- module --> <input type="hidden" name="module" id="module" value="<%=module%>"> <!-- paging script --> <input
	type="hidden" name="sessionid" id="sessionid"> <input type="hidden" name="tpage" id="tpage"> <input type="hidden" name="psize" id="psize"> <!-- list header title -->
	<div class="header_title">
		<i class="axi axi-subtitles"></i><span>사용자 조회</span>
		<!-- info search -->
		<jsp:include page="/jsp/common/search_info.jsp"></jsp:include>
	</div> <!-- container table -->
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
						<td><input type="hidden" name="deptOid" id="deptOid" value=""> <span id="deptName"><%=root%></span></td>
						<th>퇴사여부</th>
						<td><label title="퇴사여부"> <input value="true" type="checkbox" name="resigns" id="resigns">
						</label></td>
					</tr>
					<tr>
						<th>이름</th>
						<td><input type="text" name="name" class="AXInput wid200"></td>
						<th>아이디</th>
						<td><input type="text" name="id" class="AXInput wid200"></td>
					</tr>
				</table> <!-- button table -->
<!-- 				<table class="btn_table"> -->
				<table class="btn_table">
					<tr>
						<td>
							<!-- list jsp.. -->
							<div class="non_paging_layer">
								<span class="left_sub_folder_search"> <label title="하위부서검색"> <input name="sub_folder" id="sub_folder" type="checkbox" value="ok">하위부서검색
								</label>
								</span> <span class="count_span"><span id="count_text"></span></span> <select name="paging_count" id="paging_count" class="AXSelectSmall">
									<option value="15">15</option>
									<option value="30">30</option>
									<option value="50">50</option>
									<option value="100">100</option>
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
				</table> <!-- end button table --> <!-- start sub table -->
				<!-- <table class="sub_table">
					<tr>
						<td>
							list jsp..
							<div class="non_paging_layer">
								<span class="sub_folder_search"> <label title="하위부서검색"> <input name="sub_folder" id="sub_folder" type="checkbox" value="ok">하위부서검색
								</label>
								</span> <span class="count_span"><span id="count_text"></span></span> <select name="paging_count" id="paging_count" class="AXSelectSmall">
									<option value="15">15</option>
									<option value="30">30</option>
									<option value="50">50</option>
									<option value="100">100</option>
								</select>
							</div>
						</td>
					</tr>
				</table> end sub_table object list div area -->
				<div class="list_container">
					<!-- object list table -->
					<table class="list_table indexed sortable-table">
						<thead id="grid_header">
						</thead>
						<tbody id="grid_list">
						</tbody>
					</table>
					<!-- end list table -->
				</div> <!-- end div area -->
			</td>
		</tr>
	</table>

	<table class="page_table" id="grid_paginate">
	</table> <%
 	// header context
 	if (!isDbl) {
 		out.println(html.setContextmenu(module));
 		// context
 		out.println(html.setRightMenu(module));
 		// multi context
 		out.println(html.setRightMenuMulti(module));
 	}
 %>
</td>