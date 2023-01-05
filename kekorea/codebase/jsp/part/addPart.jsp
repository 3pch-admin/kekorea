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
	// folder root
	
	// admin
	boolean isAdmin = CommonUtils.isAdmin();
	boolean isBox = true;
	boolean isDbl = StringUtils.parseBoolean(request.getParameter("dbl"));
	
	PartType[] partTypes = (PartType[]) request.getAttribute("partTypes");
	// 함수
	String fun = (String) request.getParameter("fun");
	
	String state = (String) request.getParameter("state");
	
	boolean changeable = StringUtils.parseBoolean((String) request.getParameter("changeable"));
	
	// module
	String module = null;
	
	// context check
	String context = (String) request.getParameter("context");
	String root = PartHelper.PRODUCT_ROOT;
	if("eplan".equals(context)) {
		root = PartHelper.EPLAN_ROOT;
	}
	boolean isProduct = false;
	boolean isLibrary = false;
	boolean isEplan = false;
	String purl = "/Windchill/plm/part/addPart?context=product&dbl=" + isDbl + "&fun=" + fun + "&state=" + state + "&changeable=" + changeable;
	String lurl = "/Windchill/plm/part/addPart?context=library&dbl=" + isDbl + "&fun=" + fun + "&state=" + state + "&changeable=" + changeable;
	String eurl = "/Windchill/plm/part/addPart?context=eplan&dbl=" + isDbl + "&fun=" + fun + "&state=" + state + "&changeable=" + changeable;
	String title = "";
	String span = "left_sub_folder_search";
	if("product".equals(context)) {
		isProduct = true;
		module = ModuleKeys.add_product_part.name();
		title = "부품";
	} else if("library".equals(context)) {
		isLibrary = true;
		module = ModuleKeys.add_library_part.name();
		title = "라이브러리";
	} else if("eplan".equals(context)) {
		isEplan = true;
		module = ModuleKeys.add_eplan_part.name();
		title = "EPLAN";
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
		var url = "/Windchill/plm/part/viewPart";
		grid.cellClick("name", url);
		grid.cellClick("number", url);
		<%
			} else {
		%>
		$(document).on("dblclick", ".list_tr", function(e) {
			$value = $(this).data("oid");
			parts.<%=fun %>(this, $value, "<%=state %>");
		})
		<%
			}
		%>
	
		$.fn.getColumn = function() {
			var url;	
			<%
				if(isProduct) {
			%>
			url = "/Windchill/plm/part/listProductPartAction";
			<%
				} else if(isLibrary) {
			%>
			url = "/Windchill/plm/part/listLibraryPartAction";
			<%
				} else if(isEplan) {
			%>
			url = "/Windchill/plm/part/listEplanPartAction";
			<%
				}
			%>
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
		
		$("#addDblParts").click(function() {
			parts.addPartsAction("<%=state %>");
		})
		
		$("#codeParts").click(function() {
			parts.addCodePartsAction("<%=state %>");
		})
		
		$("#codeLibrary").click(function() {
			parts.codeLibraryAction("<%=state %>");
		})
		
		<%
			if(!StringUtils.isNull(state)) {
		%>
		$("#statesPart").bindSelectSetValue("<%=state%>");
		$("#statesPart").bindSelectDisabled(true);
		<%
			}
		%>
		
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
						<td colspan="7">
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
						<th>품명</th>
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
						<th>파일이름</th>
						<td colspan="3">
							<input type="text" name="fileName" class="AXInput wid200">
						</td>
					</tr>
					<tr class="detailPart">
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
					<tr class="detailPart">
						<th>상태</th>
						<td>
							<select name="statesPart" id="statesPart" class="AXSelect wid200">
								<option value="">선택</option>
								<%
								String[] displays = PartHelper.PART_STATE_DISPLAY;
								String[] values = PartHelper.PART_STATE_VALUE;
									for(int i=0; i<displays.length; i++) {
								%>
								<option value="<%=values[i] %>"><%=displays[i] %></option>
								<%
									}
								%>
							</select>
						</td>
						<th>버전</th>
						<td colspan="5">
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
						<th>규격</th>
						<td>
							<input type="text" name="number" class="AXInput wid200">
						</td>
						<th>품번</th>
						<td>
							<input type="text" name="partCode" class="AXInput wid200">
						</td>
						<th>품명</th>
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
						<th>파일이름</th>
						<td colspan="3">
							<input type="text" name="fileName" class="AXInput wid200">
						</td>
					</tr>
					<tr class="detailPart">
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
					<tr class="detailPart">
						<th>상태</th>
						<td>
							<select name="statesPart" id="statesPart" class="AXSelect wid200">
								<option value="">선택</option>
								<%
								String[] displays = PartHelper.PART_STATE_DISPLAY;
								String[] values = PartHelper.PART_STATE_VALUE;
									for(int i=0; i<displays.length; i++) {
								%>
								<option value="<%=values[i] %>"><%=displays[i] %></option>
								<%
									}
								%>
							</select>
						</td>
						<th>버전</th>
						<td colspan="6">
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
						<td class="left">
							<div class="view_layer">
								<%
									if(changeable) {
										span = "left2_sub_folder_search";
								%>
								<ul>
									<li data-url="<%=purl %>" <%if(isProduct) { %> class="active_view" <%} %> id="product_view" title="부품">부품</li>
									<li data-url="<%=lurl %>" <%if(isLibrary) { %> class="active_view"  <%} %> id="library_view" title="라이브러리">라이브러리</li>
									<li data-url="<%=eurl %>" <%if(isEplan) { %> class="active_view"  <%} %> id="eplan_view" title="EPLAN">EPLAN</li>
									<li class="hidden">
										<span id="thumbnail" title="썸네일 리스트로 확인하세요.">썸네일 리스트로 확인하세요 </span>
									</li>
								</ul>
								<%
									}
								%>
								<span class="<%=span%>">
								<label title="하위폴더검색"> 
									<input name="sub_folder" id="sub_folder" type="checkbox" checked="checked" value="ok">하위폴더검색
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
							 <input type="button" value="상세조회" class="blueBtn" id="detailPartBtn" title="상세조회">
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
 	if(!isDbl) {
	 	out.println(html.setContextmenu(module));
	 	// context
	 	out.println(html.setRightMenu(module));
	 	// multi context
 		out.println(html.setRightMenuMulti(module));
 	}
 %>
</td>