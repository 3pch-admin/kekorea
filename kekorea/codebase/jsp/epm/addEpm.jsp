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
	String root = EpmHelper.PRODUCT_ROOT;
	// admin
	boolean isAdmin = CommonUtils.isAdmin();
	boolean isBox = true;
	boolean isDbl = StringUtils.parseBoolean(request.getParameter("dbl"));
	// 함수
	String fun = (String) request.getParameter("fun");
	
	String state = (String) request.getParameter("state");
	// module
	String module = null;
	
	
	boolean changeable = StringUtils.parseBoolean((String) request.getParameter("changeable"));
	
	// opener param
	String cadtype = request.getParameter("cadtype");
	// context check
	String context = (String) request.getParameter("context");
	boolean isProduct = false;
	boolean isLibrary = false;
	String purl = "/Windchill/plm/epm/addEpm?context=product&dbl=" + isDbl + "&fun=" + fun + "&state=" + state + "&cadtype=" + cadtype + "&changeable=" + changeable;
	String lurl = "/Windchill/plm/epm/addEpm?context=library&dbl=" + isDbl + "&fun=" + fun + "&state=" + state + "&cadtype=" + cadtype + "&changeable=" + changeable;
	String title = "";
	
	if("product".equals(context)) {
		isProduct = true;
		module = ModuleKeys.add_product_epm.name();
		title = "도면";
	} else if("library".equals(context)) {
		isLibrary = true;
		module = ModuleKeys.add_library_epm.name();
		title = "라이브러리";
	}
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
	String[] 	styles = new String[]{"50", "50", "250", "250", "250", "100", "180", "180", "180", "60", "120", "120", "120", "120", "80", "140", "40"};
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
		var url = "/Windchill/plm/epm/viewEpm";
		grid.cellClick("name", url);
		grid.cellClick("number", url);
		<%
			} else {
		%>
		$(document).on("dblclick", ".list_tr", function(e) {
			$value = $(this).data("oid");
			epms.<%=fun %>(this, $value, "<%=state %>");
		})
		<%
			}
		%>
	
		$.fn.getColumn = function() {
			var url;	
			<%
				if(isProduct) {
			%>
			url = "/Windchill/plm/epm/listProductEpmAction";
			<%
				} else if(isLibrary) {
			%>
			url = "/Windchill/plm/epm/listLibraryEpmAction";
			<%
				}
			%>
			var params = grid.getParams();
			grid.preLoading();
			console.log(params);
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
		
		$("#addDblEpms").click(function() {
			epms.addEpmsAction("<%=state %>");
		})
		
		$("#addPrintEpm").click(function() {
			epms.addPrintEpmsAction("<%=state %>");
		})
		
		$("#addDblCodeEpms").click(function() {
			epms.codeEpmAction("<%=state %>");
		})
		
		$("#addDblCodeLibrarys").click(function(){
			epms.codeLibraryAction("<%=state%>");
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
		<i class="axi axi-subtitles"></i><span><%=title %> 조회</span>
		<!-- info search -->
		<jsp:include page="/jsp/common/search_info.jsp"></jsp:include>
	</div>
	
	<!-- container table -->
	<table class="container_table">
		<tr>
			<!-- only folder tree.. -->
			<jsp:include page="/jsp/common/layouts/include_tree.jsp">
				<jsp:param value="<%=root%>" name="root" />
				<jsp:param value="<%=context.toUpperCase() %>" name="context" />
			</jsp:include>
			<!-- container -->
			<td id="container_td">
				<!-- search table -->
				<table class="search_table">
					<tr>
						<th>부품분류</th>
						<td colspan="3">
							<%
								if(!StringUtils.isNull(cadtype)) {
							%>
							<input type="hidden" name="epmTypes" id="epmTypes" value="<%=cadtype%>">
							<%
								}
							%>
							<input type="hidden" name="location" value="<%=root%>"> <span id="location"><%=root%></span>
						</td>
					</tr>
					<%
						if(isProduct) {
					%>
					<tr>
						<th>규격</th>
						<td>
							<input type="text" name="number" class="AXInput wid200">
						</td>
						<th>품번</th>
						<td>
							<input type="text" name="partCode" class="AXInput wid200">
						</td>
					</tr>
					<tr>
						<th>품명</th>
						<td>
							<input type="text" name="partName" class="AXInput wid200">
						</td>
						<th>REFERENCE 도면</th>
						<td>
							<input type="text" name="referenceDrwing" class="AXInput wid200">
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
					</tr>
					<tr>
						<th>파일이름</th>
						<td colspan="3">
							<input type="text" name="fileName" class="AXInput wid200">
						</td>
					</tr>
					<tr class="detailEpm">
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
					</tr>	
					<tr class="detailEpm">
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
					</tr>	
					<tr class="detailEpm">
						<th>상태</th>
						<td>
							<select name="statesEpm" id="statesEpm" class="AXSelect wid200">
								<option value="">선택</option>
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
					</tr>
					<%
						} else {
					%>	
						<tr>
						<th>DWG_NO</th>
						<td>
							<input type="text" name="number" class="AXInput wid200">
						</td>
						<th>PART_CODE</th>
						<td>
							<input type="text" name="partCode" class="AXInput wid200">
						</td>
					</tr>
					<tr>
						<th>부품명</th>
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
					</tr>	
					<tr>
						<th>상태</th>
						<td>
							<select name="statesEpm" id="statesEpm" class="AXSelect wid200">
								<option value="">선택</option>
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
					</tr>
					<%
						}
					%>	
				</table>
				
				<!-- button table -->
				<table class="btn_table">
					<tr>
						<td>
							<div class="view_layer">
								<%
									if(changeable) {
								%>
								<ul>
									<li data-url="<%=purl %>" <%if(isProduct) { %> class="active_view" <%} %> id="product_view" title="도면">도면</li>
									<li data-url="<%=lurl %>" <%if(isLibrary) { %> class="active_view"  <%} %> id="library_view" title="라이브러리">라이브러리</li>
									<li class="hidden">
										<span id="thumbnail" title="썸네일 리스트로 확인하세요.">썸네일 리스트로 확인하세요 </span>
									</li>
								</ul>
								<%
									}
								%>
								<span class="left2_sub_folder_search"> 
								<label title="하위폴더검색"> 
									<input name="sub_folder" id="sub_folder" type="checkbox" value="ok" checked="checked">하위폴더검색
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
								 <input type="button" value="추가" class="redBtn" id="<%=fun %>" title="추가">  
							 <%
							 	if(isProduct) {
							 %>
							 <input type="button" value="상세조회" class="blueBtn" id="detailEpmBtn" title="상세조회">
							 <%
							 	}
							 %> 
							 <input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회"> 
							 <input type="button" value="초기화" id="init_table" title="초기화">
							 <input type="button" value="닫기" class="redBtn" id="popupPartBtn" title="닫기">
						</td>
					</tr>
				</table> 
				<!-- end button table --> 
				
				<!-- start sub table -->
				<%-- <table class="sub_table">
					<tr>
						<td>
							<div class="view_layer">
								<%
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
								%>
							</div>
						</td>
					</tr>
				</table>  --%>
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