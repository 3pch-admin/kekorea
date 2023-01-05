<%@page import="e3ps.document.service.DocumentHelper"%>
<%@page import="java.util.HashMap"%>
<%@page import="e3ps.document.column.DocumentColumnData"%>
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
	// module
	String module = ModuleKeys.list_document.name();
	// controller param..
	PageQueryUtils pager = (PageQueryUtils)request.getAttribute("pager");
	// paging result
	PagingQueryResult result = pager.find();
	
	// lastpage
	int lastPage = pager.getLastPage();
	
	// parameter
	HashMap<String, Object> param = (HashMap<String, Object>)pager.getParam();
	
	// htmlUtils..
	HtmlUtils html = new HtmlUtils(); 
	// checkbox parameter ... 
	boolean isBox = StringUtils.parseBoolean(request.getParameter("isBox"));
	boolean isMulti = StringUtils.parseBoolean(request.getParameter("isMulti"));
	if(isAdmin) {
		isBox = true;
		isMulti = true;
	}
	
	// search value param
	String name = StringUtils.removeNull((String)param.get("name"));
	String number = StringUtils.removeNull((String)param.get("number"));
	String creators = StringUtils.removeNull((String)param.get("creators"));
	String creatorsOid = StringUtils.removeNull((String)param.get("creatorsOid"));
	String predate = StringUtils.removeNull((String)param.get("predate"));
	String postdate = StringUtils.removeNull((String)param.get("postdate"));
	String states = StringUtils.removeNull((String)param.get("states"));
	String statesStr = StringUtils.removeNull((String)param.get("statesStr"));
	String s = (String)param.get("latest");
	if(s == null) {
		s = "true";
	}
	boolean latest = Boolean.parseBoolean(s);
	String location = StringUtils.getParam((String)param.get("location"), root);
	String search = (String)param.get("search");
	
	// sub folder search
	String sub_folder = (String)param.get("sub_folder");
%>
<td valign="top">
	<!-- script area -->
	<script type="text/javascript">
	$(document).ready(function() {

		// view url
		var url = "/Windchill/plm/document/viewDocument";
		$(document).cellClick("name", url);
		$(document).cellClick("number", url);

		// search 
		$("#searchBtn").click(function() {
// 			$(document).list();
			var url = "/Windchill/plm/document/listDocumentAction";
			var params = new Object();
			$(document).ajaxCallServer(url, params, function(data) {
				console.log(data);
				$(document).setGrid(data);
			}, false);
		})
		
		// delete
		$("#deleteBtn").click(function() {
			$(document).deleteDocument();
		})
		
		// table header fixed table class name
		$(".list_table").tableHeadFixer();
		// checkbox, radio box
		$("input").checks();
		
	}).keypress(function(e) {
		var keyCode = e.keyCode;
		if(keyCode == 13) {
			$(document).list();
		}
	})
	</script>
	<!-- hidden value -->
	<!-- 검색 -->
	<input type="hidden" name="search" id="search" value="search">
	<!-- items.. -->
	<input type="hidden" name="items" id="items">
	<!-- module -->
	<input type="hidden" name="module" id="module" value="<%=module %>">
	<!-- paging script -->
	<%
		out.println(pager.getScript());
	%>
	
	<!-- list header title -->
	<div class="header_title">
		<i class="axi axi-subtitles"></i><span>문서 조회</span>
		<!-- info search -->
		<jsp:include page="/jsp/common/search_info.jsp"></jsp:include>
	</div>
	
	<!-- container table -->
	<table class="container_table">
		<tr>
			<!-- only folder tree.. -->
			<jsp:include page="/jsp/common/layouts/include_tree.jsp">
				<jsp:param value="<%=root %>" name="root"/>
				<jsp:param value="PRODUCT" name="context"/>
			</jsp:include>
			<!-- container -->
			<td id="container_td">
			
				<!-- search table -->
				<table class="search_table">
					<tr>
						<th>문서 저장위치</th>
						<td colspan="3">
							<input type="hidden" name="location" value="<%=location %>"><span id="location"><%=location %></span>
						</td>
					</tr>				
					<tr>
						<th>문서번호</th>
						<td>
							<input type="text" name="name" class="AXInput wid200" value="<%=name %>">
						</td>
						<th>문서번호</th>
						<td>
							<input type="text" name="number" class="AXInput wid200" value="<%=number %>">
						</td>
					</tr>
					
					<tr>
						<th>작성자</th>
						<td>
							<input type="text" name="creators" id="creators" class="AXInput wid200" value="<%=creators %>" data-dbl="true">
							<input type="hidden" name="creatorsOid" id="creatorsOid" value="<%=creatorsOid %>">
							<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="creators"></i>
						</td>
						<th>작성일</th>
						<td>
							<input type="text" name="predate" id="predate" class="AXInput" value="<%=predate %>"> ~
							<input type="text" name="postdate" id="postdate" class="AXInput twinDatePicker" value="<%=postdate %>" data-start="predate">
							<i title="삭제" class="axi axi-ion-close-circled delete-calendar" data-start="predate" data-end="postdate"></i>
						</td>
					</tr>
					<tr>
						<th>버전</th>
						<td>
							<label title="최신버전">
							<input type="radio" name="latest" value="true" <%if(latest) { %> checked="checked" <%} %>><span class="latest">최신버전</span></label>
							<label title="모든버전">
							<input type="radio" name="latest" value="false" <%if(!latest) { %> checked="checked" <%} %>><span class="latest">모든버전</span></label>
						</td>
						<th>상태</th>
						<td>
							<input type="text" name="states" id="states" class="AXInput wid150" value="<%=states %>" data-key="DEFAULT_LIFECYCLE">
							<input type="hidden" name="statesStr" id="statesStr" value="<%=statesStr %>">
							<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Str" data-target="states"></i>
						</td>
					</tr>
				</table>
			
			
				<!-- button table -->
				<table class="btn_table">
					<tr>
						<td class="right">
						<%
							if(isAdmin) {
						%>
						<input type="button" value="삭제" class="redBtn" id="deleteBtn" title="삭제">
						<%
							}
						%>
						<input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회">
						<input type="button" value="초기화" class="" id="init_table" title="초기화">
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
								<span class="sub_folder_search">
									<label><input name="sub_folder" id="sub_folder" type="checkbox" value="ok" <%if("ok".equals(sub_folder)) { %> checked="checked" <%} %>>하위폴더검색</label>
								</span>
								<span class="count_span"><span id="count_text"></span></span>
								<%=pager.getPagingBox() %>
							</div>
						</td>		
					</tr>
				</table>
				<!-- end sub_table -->
				
				
				<!-- object list div area -->
				<div class="div_scroll">
					<!-- object list table -->
					<table class="list_table indexed sortable-table">
					<%
						// table header setting
						String[] headers = ColumnUtils.getColumnHeaders(module);
						// table header key
						String[] keys = ColumnUtils.getColumnKey(module);
						// table cols
						String[] cols = ColumnUtils.getColumnCols(module, headers);
						// table style
						String[] styles = ColumnUtils.getColumnCols(module, headers);
						// print table header
						out.println(html.setHeader(isBox, headers, keys, cols, styles, pager.getSort(), isMulti));
					%>
					<tbody>
					<%
						// object total 
						int total = pager.getTotal();
						while(result.hasMoreElements()) {
							Object[] obj = (Object[])result.nextElement();
							WTDocument document = (WTDocument)obj[0];
							DocumentColumnData data = new DocumentColumnData(document);
						
					%>
						<!-- data print -->
						<tr class="list_tr" data-oid="<%=data.oid %>" data-key="rightmenu">
						<%
							if(isBox) {
						%>
							<td><input type="checkbox" name="oid" value="<%=data.oid %>"></td>
						<%
							}
							for(int i=0; i<keys.length; i++) {
								String value = data.getValue(total, keys[i], styles[i]);
								out.println(value);
							}
							total--;
						%>
						</tr>
					<%
						// end data print
						} // end whild
						if(result.size() == 0) {
							int colspan = keys.length;
							if(isBox) {
								colspan = colspan + 1;
							}
					%>
					<tr>
						<td class="nodata_icon" colspan="<%=colspan %>">
							<a class="axi axi-info-outline"></a>
							<span>
							<%
								if(StringUtils.isNull(search)) {
									out.println("조회 버튼을 눌러서 문서를 검색하세요.");
								} else {
									out.println("조회 결과가 없습니다.");
								}
							%>
							</span>
						</td>
					</tr>
					<%
						} // empty object count
					%>
					</tbody>
					</table>
					<!-- end list table -->
				</div>
				<!-- end div area -->	
			</td>
		</tr>
	</table>
	<%
		// contextmenu and paging table
		out.println(pager.paging());
		// header context
		out.println(html.setContextmenu(module));
		// context
		out.println(html.setRightMenu(module));
		// multi context
		out.println(html.setRightMenuMulti(module));
	%>
</td>

<div id="grid" style="width: 500px; height: 300px;">

</div>