<%@page import="e3ps.common.code.CommonCode"%>
<%@page import="e3ps.admin.service.AdminHelper"%>
<%@page import="e3ps.org.service.OrgHelper"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.doc.column.DocumentColumnKeys"%>
<%@page import="e3ps.doc.service.DocumentHelper"%>
<%@page import="java.util.HashMap"%>
<%@page import="e3ps.doc.column.DocumentColumnData"%>
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
	String codeValue = (String) request.getParameter("codeType");
	System.out.println("codeValue : " + codeValue);

	if(StringUtils.isNull(codeValue)) {
		codeValue = AdminHelper.INIT_CODE_LIST[0];
	}
	System.out.println("codeValue : " + codeValue);
	CommonCode c = AdminHelper.manager.getCodeForCodeValue(codeValue, codeValue);

	String poid = c.getPersistInfo().getObjectIdentifier().getStringValue();
	
	// dept root
	boolean isAdmin = CommonUtils.isAdmin();

	boolean isBox = true;
	if (isAdmin) {
		isBox = true;
	}
	// module
	String module = ModuleKeys.list_code.name();

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
		$("#uses").bindSelect();
		$("#codeType").bindSelect({
			onchange : function() {
				var value = $(this)[0].value;
				if(value == "") {
					value = "MACHINE_TYPE";
				}
				mask.open();
				$("#loading_layer").show();
				document.location.href = "/Windchill/plm/admin/manageCode?codeType=" + value;
			}
		})
		$(document).getColumn();
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
		<i class="axi axi-subtitles"></i><span>코드관리</span>
		<!-- info search -->
		<jsp:include page="/jsp/common/search_info.jsp"></jsp:include>
	</div>
	
	<!-- container table -->
	<table class="container_table">
		<tr>
			<!-- only folder tree.. -->
			<jsp:include page="/jsp/common/layouts/include_code.jsp">
				<jsp:param value="<%=codeValue %>" name="codeType"/>
			</jsp:include>
			<!-- container -->
			<td id="container_td">
				<!-- search table -->
				<table class="search_table">
					<tr>
						<th>코드</th>
						<td colspan="3">
							<select name="codeType" id="codeType" class="AXSelect wid200">
								<option value="">선택</option>
								<%
									for(String codeType : AdminHelper.INIT_CODE_LIST) {
								%>
								<option value="<%=codeType %>" <%if(codeValue.equals(codeType)) { %> selected="selected" <%} %>><%=codeType %></option>
								<%
									}
								%>
							</select>
						</td>
					</tr>
					<tr>
						<th>상위코드</th>
						<td>
							<input type="hidden" name="codeOid" id="codeOid">
							<span id="codeName"><%=c.getName() %>&nbsp;[<%=c.getCode() %>]</span>
						</td>
						<th>사용여부</th>
						<td>
							<select name="uses" id="uses" class="AXSelect wid200">
								<option value="">선택</option>
								<option value="true">사용</option>
								<option value="false">사용안함</option>
							</select>
						</td>
					</tr>
					<tr>
						<th>코드명</th>
						<td>
							<input type="text" name="name" class="AXInput wid200">
						</td>
						<th>코드</th>
						<td>
							<input type="text" name="code" class="AXInput wid200">
						</td>
					</tr>
				</table> 
				
				<!-- button table -->
				<table class="btn_table">
					<tr>
						<td class="right">
							 <input type="button" value="등록" class="redBtn" id="createCodeBtn" title="등록" data-depth="<%=c.getDepth() + 1 %>" data-poid="<%=poid %>" data-codetype="<%=codeValue %>">
							 <input type="button" value="수정" class="blueBtn" id="modifyCodeBtn" title="수정">
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
								<span class="count_span"><span id="count_text"></span></span>
								<select name="paging_count" id="paging_count" class="AXSelectSmall">
									<option value="15">15</option>
									<option value="30">30</option>
									<option value="50">50</option>
									<option value="100">100</option>
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