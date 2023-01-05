<%@page import="java.sql.Timestamp"%>
<%@page import="e3ps.common.util.DateUtils"%>
<%@page import="java.util.Calendar"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.query.OrderBy"%>
<%@page import="wt.query.ClassAttribute"%>
<%@page import="wt.query.SearchCondition"%>
<%@page import="e3ps.common.code.CommonCode"%>
<%@page import="wt.query.QuerySpec"%>
<%@page import="e3ps.common.code.service.CommonCodeHelper"%>
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
	// admin
	boolean isAdmin = CommonUtils.isAdmin();

	StateKeys[] states = (StateKeys[]) request.getAttribute("states");
	
	ArrayList<String> customer = (ArrayList<String>) request.getAttribute("customer");
	ArrayList<String> project_type = (ArrayList<String>) request.getAttribute("project_type");
	
	ArrayList<String> install = null;//(ArrayList<String>) request.getAttribute("install");
	String name="";
// 	boolean isBox = true;
	boolean isBox = false;
	if(isAdmin) {
		isBox = true;
	}
	// module
	String module = ModuleKeys.list_project.name();
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
	
	String[] styles = new String[]{"40", "60", "60", "100", "90", "110", "100", "100", "70", "200", "80", "80", "100", "80", "80", "80", "80", "60", "60"};
	
	
	Calendar calendar = Calendar.getInstance();
// 	calendar.add(Calendar.DATE, -90);
	calendar.add(Calendar.MONTH, -4);
	Timestamp before = new Timestamp(calendar.getTime().getTime());
	String pre = before.toString().substring(0, 10);
	String today = DateUtils.getCurrentTimestamp().toString().substring(0, 10);
	
%>
<td valign="top">
	<!-- script area --> 
	<script type="text/javascript">
	$(document).ready(function() {
		var parent_code;
		
		$("#stateStep").bindSelect();
		
		$("#customer").bindSelect({
			onchange : function() {
				parent_code = this.optionValue;
				$("#ins_location").bindSelect({
					ajaxUrl : "/Windchill/plm/bind/getInstall",
					ajaxPars: "name=" + parent_code,
					reserveKeys: {
						options: "list",
						optionValue: "value",
						optionText: "name"
					},
					setValue:this.optionValue,
                    alwaysOnChange: true,
				})
			}
		})

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
		var url = "/Windchill/plm/project/viewProject";
		grid.cellClickPopup("kek_number", url);
		grid.cellClickPopup("ke_number", url);
	
		$.fn.getColumn = function() {
			var url = "/Windchill/plm/project/listProjectAction";
			var params = grid.getParams();
			params.progress = "true";
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
		
		$("#ins_location").bindSelect();
		$("#pType").bindSelect();
		$("#kekState").bindSelect();
		$(".list_table").tableHeadFixer();
// 		$('.sortable-table').tableSorter();
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
		<i class="axi axi-subtitles"></i><span>작번 조회</span>
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
						<th>KEK 작번</th>
						<td>
							<input type="text" name="kekNumber" class="AXInput wid200">
						</td>
						<th>발행일</th>
						<td>
							<input type="text" name="predate" id="predate" class="AXInput" value="<%=pre %>"> ~ 
							<input type="text" name="postdate" id="postdate" class="AXInput twinDatePicker" data-start="predate" value="<%=today %>"> 
							<i title="삭제" class="axi axi-ion-close-circled delete-calendar" data-start="predate" data-end="postdate"></i>
						</td>
						<th>KE 작번</th>
						<td>
							<input type="text" name="keNumber" class="AXInput wid200">
						</td>
						<th>USER ID</th>
						<td>
							<input type="text" name="userId" id="userId" class="AXInput wid200"> 
						</td>
					</tr>
					<tr>
						<th>작번상태</th>
						<td>
							<select name="kekState" id="kekState" class="AXSelect wid200">
								<option value="">선택</option>
								<option value="준비">준비</option>	
								<option value="설계중">설계중</option>
								<option value="설계완료">설계완료</option>
								<option value="작업완료">작업완료</option>
								<option value="중단됨">중단됨</option>
								<option value="취소">취소</option>
							</select>
						</td>					
						<th>모델</th>
						<td>
							<input type="text" name="model" class="AXInput wid200">
						</td>
						<th>거래처</th>
						<td>
<!-- 							<input type="text" name="customer" class="AXInput wid200"> -->
							<select name="customer" id="customer" class="AXSelect wid200">
							<option value="">선택</option>
							<%for(int i=0;i<customer.size();i++) {%>
									<option value="<%=customer.get(i)%>"><%=customer.get(i)%></option>
								<%} %>
							</select>
						</td>
						<th>설치장소</th>
						<td>
<!-- 							<input type="text" name="ins_location" class="AXInput wid200"> -->
							<select name="ins_location" id="ins_location" class="AXSelect wid209">
								<option value="">선택</option>
							</select>
						</td>
					</tr>
					<tr class="detailEpm">
						<th>작번유형</th>
						<td>
							<select name="pType" id="pType" class="AXSelect wid200">
								<option value="">선택</option>
							<%
									for(int i=0;i<project_type.size();i++) {
								%>
									<option value="<%=project_type.get(i)%>"><%=project_type.get(i)%></option>
								<%} %>
							</select>
						</td>
						<th>기계 담당자</th>
						<td>
							<input type="text" name="machine" id="machine" class="AXInput wid200" data-dbl="true" data-dept="기계설계" data-resign="resign"> 
							<input type="hidden" name="machineOid" id="machineOid">
							<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="machine"></i>
						</td>
						<th>전기 담당자</th>
						<td>
							<input type="text" name="elec" id="elec" class="AXInput wid192" data-dbl="true" data-dept="전기설계" data-resign="resign"> 
							<input type="hidden" name="elecOid" id="elecOid">
							<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="elec"></i>
						</td>
						<th>SW 담당자</th>
						<td>
							<input type="text" name="soft" id="soft" class="AXInput wid200" data-dbl="true"  data-dept="SW설계" data-resign="resign"> 
							<input type="hidden" name="softOid" id="softOid">
							<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="soft"></i>
						</td>
					</tr>	
					<tr class="detailEpm">
						<th>막종</th>
						<td>
							<input type="text" name="mak" id="mak" class="AXInput wid200"> 
						</td>							
						<th>작업내용</th>
						<td colspan="5">
							<input type="text" name="description" class="AXInput wid500">
						</td>		
					</tr>						
				</table> 
				
				<!-- button table -->
				<table class="btn_table">
					<tr>
						<td class="left">
							<!-- list jsp.. -->
							<div class="non_paging_layer">
								<%
									if(isBox) {
								%>
								<span class="count_span"><span id="count_text"></span></span>
								<%
									}
								%>
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
							<select name="stateStep" id="stateStep" class="AXSelect wid100">
								<option value="">선택</option>
								<option value="1">1</option>	
								<option value="2">2</option>
								<option value="3">3</option>
								<option value="4">4</option>
								<option value="5">5</option>
								<option value="6">전체</option>
							</select>							
							<input type="button" value="완료" class="redBtn" id="completeStep" title="완료"> 
							<input type="button" value="삭제" class="redBtn" id="deleteProjectBtn" title="삭제"> 
							<%
							 	}
							 %>						
							<input type="button" value="상세조회" class="orangeBtn" id="detailEpmBtn" title="상세조회"> 
							<input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회"> 
							<input type="button" value="초기화" class="" id="initGrid" title="초기화">
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