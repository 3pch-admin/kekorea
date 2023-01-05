<%@page import="e3ps.org.Department"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTUser"%>
<%@page import="e3ps.common.util.DateUtils"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.util.Calendar"%>
<%@page import="e3ps.common.code.service.CommonCodeHelper"%>
<%@page import="e3ps.common.StateKeys"%>
<%@page import="e3ps.org.service.OrgHelper"%>
<%@page import="wt.part.PartType"%>
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
	
	WTUser user = (WTUser)SessionHelper.manager.getPrincipal();
	
	Department dept = (Department) request.getAttribute("dept");
	
	String mvalue = "";
	String mname = "";
	String evalue = "";
	String ename = "";
	String svalue = "";
	String sname = "";
	if("기계설계".equals(dept.getName())) {
		mvalue = user.getPersistInfo().getObjectIdentifier().getStringValue();
		mname = user.getFullName() + "&nbsp;[" + user.getName() + "]";
	} else if("전기설계".equals(dept.getName())) {
		evalue = user.getPersistInfo().getObjectIdentifier().getStringValue();
		ename = user.getFullName() + "&nbsp;[" + user.getName() + "]";
	} else if("SW설계".equals(dept.getName())) {
		svalue = user.getPersistInfo().getObjectIdentifier().getStringValue();
		sname = user.getFullName() + "&nbsp;[" + user.getName() + "]";
	}
	
	
	// 함수
	String fun = (String) request.getParameter("fun");
	
	String state = (String) request.getParameter("state");
	
	StateKeys[] states = (StateKeys[]) request.getAttribute("states");
	
	ArrayList<String> customer = (ArrayList<String>) request.getAttribute("customer");
	ArrayList<String> project_type = (ArrayList<String>) request.getAttribute("project_type");
	
	// module
	String module = ModuleKeys.add_list_project.name();
	
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
	
	String[] styles = new String[]{"40", "60", "90", "90", "100", "100", "70", "300", "120", "80", "80", "80", "100", "80", "80", "60", "60"};
	
	Calendar calendar = Calendar.getInstance();
// 	calendar.add(Calendar.DATE, -90);
	calendar.add(Calendar.MONTH, -3);
	Timestamp before = new Timestamp(calendar.getTime().getTime());
	String pre = before.toString().substring(0, 10);
	String today = DateUtils.getCurrentTimestamp().toString().substring(0, 10);
%>
<td valign="top">
	<!-- script area --> 
	<script type="text/javascript">
	$(document).ready(function() {
		var parent_code;
		
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
		<%
			if(!isDbl) {
		%>
		var url = "/Windchill/plm/project/viewProject;
		grid.cellClick("name", url);
		grid.cellClick("number", url);
		<%
			} else {
		%>
		$(document).on("dblclick", ".list_tr", function(e) {
			$value = $(this).data("oid");
			projects.<%=fun %>(this, $value, "<%=state %>");
		})
		<%
			}
		%>
	
		$.fn.getColumn = function() {
// 			var url;	
			<%-- <%
				if(isProduct) {
			%>
			url = "/Windchill/plm/part/listProductPartAction";
			<%
				} else if(isLibrary) {
			%>
			url = "/Windchill/plm/part/listLibraryPartAction";
			<%
				}
			%> --%>
			var url = "/Windchill/plm/project/listProjectAction";
			var params = grid.getParams();
			grid.preLoading();
			params.progress = "false";
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
		
		$("#addProjectBtn").click(function() {
			projects.addProjectsAction("<%=state %>");
		})
		
		<%
			if(!StringUtils.isNull(state)) {
		%>
		$("#statesPart").bindSelectSetValue("<%=state%>");
		$("#statesPart").bindSelectDisabled(true);
		<%
			}
		%>
		$("#ins_location").bindSelect();
		$("#pType").bindSelect();
		$("#kekState").bindSelect();
		$(".list_table").tableHeadFixer();
		//$('.sortable-table').tableSorter();
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
	<table class="btn_table">
		<tr>
			<td class="left">
				<div class="header_title">
					<i class="axi axi-subtitles"></i><span>작번 조회</span>
					<!-- info search -->
					<jsp:include page="/jsp/common/search_info.jsp"></jsp:include>
				</div>
			</td>
			<td class="right">
				 <input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회"> 
				 <input type="button" value="초기화" id="init_table" title="초기화">
				 <input type="button" value="닫기" class="redBtn" id="popupPartBtn" title="닫기">
			 </td>
		 </tr>
	 </table>
	
	<!-- container table -->
	<table class="container_table">
		<tr>
			<!-- only folder tree.. -->
			<%-- <jsp:include page="/jsp/common/layouts/include_tree.jsp">
				<jsp:param value="<%=root%>" name="root" />
				<jsp:param value="<%=context.toUpperCase() %>" name="context" />
			</jsp:include> --%>
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
							<select name="customer" id="customer" class="AXSelect wid200">
							<option value="">선택</option>
							<%
									for(int i=0;i<customer.size();i++) {
								%>
									<option value="<%=customer.get(i)%>"><%=customer.get(i)%></option>
								<%} %>
							</select>
						</td>
						<th>설치장소</th>
						<td>
							<select name="ins_location" id="ins_location" class="AXSelect wid209">
								<option value="">선택</option>
							</select>
						</td>
					</tr>
					<tr class="detailEpm">
						<th>작번 유형</th>
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
							<input type="text" name="machine" id="machine" class="AXInput wid200" data-dbl="true" value="<%=mname %>" data-dept="기계설계" data-resign="resign"> 
							<input type="hidden" name="machineOid" id="machineOid" value="<%=mvalue%>">
							<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="machine"></i>
						</td>
						<th>전기 담당자</th>
						<td>
							<input type="text" name="elec" id="elec" class="AXInput wid192" data-dbl="true" value="<%=ename %>" data-dept="전기설계" data-resign="resign"> 
							<input type="hidden" name="elecOid" id="elecOid" value="<%=evalue%>">
							<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="elec"></i>
						</td>
						<th>SW 담당자</th>
						<td>
							<input type="text" name="soft" id="soft" class="AXInput wid200" data-dbl="true" value="<%=sname %>" data-dept="SW설계" data-resign="resign"> 
							<input type="hidden" name="softOid" id="softOid" value="<%=svalue%>">
							<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="soft"></i>
						</td>
					</tr>	
					<tr class="detailEpm">
						<th>작업내용</th>
						<td colspan="7">
							<input type="text" name="description" class="AXInput wid400">
						</td>					
					</tr>						
				</table> 
				
				<!-- start sub table -->
				<!-- <table class="sub_table"> -->
					<table class="btn_table">
					<tr>
						<td>
							<div class="view_layer">
								<%-- <%
									if(changeable) {
								%>
								<ul>
									<li data-url="<%=purl %>" <%if(isProduct) { %> class="active_view" <%} %> id="product_view" title="가공품">가공품</li>
									<li data-url="<%=lurl %>" <%if(isLibrary) { %> class="active_view"  <%} %> id="library_view" title="구매품">구매품</li>
									<li class="hidden">
										<span id="thumbnail" title="썸네일 리스트로 확인하세요.">썸네일 리스트로 확인하세요 </span>
									</li>
								</ul>
								<%
									}
								%> --%>
							</div>
						</td>
						<td>
							<!-- list jsp.. -->
							<div class="non_paging_layer">
								<!-- <span class="left_sub_folder_search"> 
								<label title="하위폴더검색"> 
									<input name="sub_folder" id="sub_folder" type="checkbox" value="ok">하위폴더검색
								</label>
								</span>  -->
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
							 <input type="button" value="추가" class="redBtn" id="addProjectBtn" title="추가">
						<%-- 	 <%
							 	if(isProduct) {
							 %>
							 <input type="button" value="상세조회" class="blueBtn" id="detailPartBtn" title="상세조회">
							 <%
							 	}
							 %>  --%>
							
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
 	if(!isDbl) {
	 	out.println(html.setContextmenu(module));
	 	// context
	 	out.println(html.setRightMenu(module));
	 	// multi context
 		out.println(html.setRightMenuMulti(module));
 	}
 %>
</td>