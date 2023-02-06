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
	String root = PartHelper.PRODUCT_ROOT;
	// admin
	boolean isAdmin = CommonUtils.isAdmin();

	boolean isBox = true;
		isBox = true;
		
		String context = (String) request.getParameter("context");
		
		if(context==null){
			context = "product";
		}
	boolean isProduct = false;
	boolean isLibrary = false;
	boolean isEplan = false;
	String purl = "/Windchill/plm/part/listProductPart2?context=product";
	String lurl = "/Windchill/plm/part/listProductPart2?context=library";
	String eurl = "/Windchill/plm/part/listProductPart2?context=eplan";
		
	// module
	String module = ModuleKeys.list_product_part.name();

	String title = "";
	if("product".equals(context)) {
		isProduct = true;
		module = ModuleKeys.list_product_part.name();
		title = "부품";
	} else if("library".equals(context)) {
		isLibrary = true;
		module = ModuleKeys.list_library_part.name();
		title = "라이브러리";
	} else if("eplan".equals(context)) {
		isEplan = true;
		module = ModuleKeys.list_eplan_part.name();
		title = "EPLAN";
	}
%>
<%@include file="/jsp/common/layouts/include_css.jsp"%>
<%@include file="/jsp/common/layouts/include_script.jsp"%>
<!-- auigrid -->
<%@include file="/jsp/include/auigrid.jsp"%>
<body onload="loadGridData();">
	<!-- module -->
	<input type="hidden" name="module" id="module" value="<%=module%>"> 
	<!-- paging script -->
	<input type="hidden" name="sessionid" id="sessionid">
	<input type="hidden" name="curPage" id="curPage">
	<input type="hidden" name="psize" id="psize">
	<!-- only folder tree.. -->
	<jsp:include page="/jsp/common/layouts/include_tree.jsp">
		<jsp:param value="<%=root%>" name="root" />
		<jsp:param value="<%=context.toUpperCase() %>" name="context" />
	</jsp:include>
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
			<th>파일이름</th>
			<td>
				<input type="text" name="fileName" class="AXInput wid200">
			</td>
			
			<th>품번</th>
			<td>
				<input type="text" name="partCode" class="AXInput wid200">
			</td>
			<th>품명</th>
			<td>
				<input type="text" name="partName" class="AXInput wid200">
			</td>
			<th>규격</th>
			<td>
				<input type="text" name="number" class="AXInput wid200">
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
			<th>MAKER</th>
			<td colspan="3">
				<input type="text" name="maker" class="AXInput wid200">
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
			<th>파일이름</th>
			<td>
				<input type="text" name="fileName" class="AXInput wid200">
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
			<th>규격</th>
			<td>
				<input type="text" name="number" class="AXInput wid200">
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
			<th>MAKER</th>
			<td colspan="3">
				<input type="text" name="maker" class="AXInput wid200">
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
		</tr>
		<tr class="detailPart">
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
			<td colspan="3">
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
				<!-- list jsp.. -->
				<div class="view_layer">
<!--					<div class="non_paging_layer"> -->
					<ul>
						<li data-url="<%=purl %>" <%if(isProduct) { %> class="active_view" <%} %> id="product_view" title="부품">부품</li>
						<li data-url="<%=lurl %>" <%if(isLibrary) { %> class="active_view"  <%} %> id="library_view" title="라이브러리">라이브러리</li>
						<li data-url="<%=eurl %>" <%if(isEplan) { %> class="active_view"  <%} %> id="eplan_view" title="EPLAN">EPLAN</li>
						<li class="hidden">
							<span id="thumbnail" title="썸네일 리스트로 확인하세요.">썸네일 리스트로 확인하세요 </span>
						</li>
					</ul>
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
				<input type="button" value="상세조회" class="orangeBtn" id="detailPartBtn" title="상세조회"> 
				<input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회"> 
				<input type="button" value="초기화" class="" id="initGrid" title="초기화">
			</td>
		</tr>
	</table> 
	<div id="grid_wrap" style="height: 650px; border-top: 1px solid #3180c3;"></div>
</body>
<script type="text/javascript" >
	let myGridID;
	const columns = [ {
       dataField : "thumnail",
       headerText : "",
       width : 50
       
	}, {

       dataField : "name",
       headerText : "파일이름",
       width : 250

  	}, {

       dataField : "part_code",
       headerText : "품번",
       width : 130

 	}, {

       dataField : "name_of_parts",
       headerText : "품명",
       width : 200
 	}, {

       dataField : "number",
       headerText : "규격",
       width : 150

   }, {

       dataField : "material",
       headerText : "MATERIAL",
       width : 150

   }, {

       dataField : "remark",
       headerText : "REMARK",
       width : 120
       
   }, {
	
       dataField : "maker",
       headerText : "MAKER",
       width : 120
 	}, {

       dataField : "version",
       headerText : "버전",
       width : 80
       
 	}, {
	
       dataField : "creator",
       headerText : "작성자",
       width : 80

   }, {

       dataField : "createDate",
       headerText : "작성일",
       width : 110

   }, {

       dataField : "modifier",
       headerText : "수정자",
       width : 80
       
   }, {
	
       dataField : "modifyDate",
       headerText : "수정일",
       width : 110
 	}, {

       dataField : "state",
       headerText : "상태",
       width : 100
       
 	}, {
	
       dataField : "location",
       headerText : "FOLDER",
       width : 80
       
 	}, {
						
	    dataField : "oid",
	    headerText : "oid",
	    visible : false
	}];
	
		const props = {
				showRowNumColumn : true,
				showRowCheckColumn: true,
				rowIdField : "oid",
				headerHeight : 30,
				rowHeight : 30,
				rowNumHeaderText : "번호"
		};
		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		
// 	function createAUIGrid(columns) {
		
// 		AUIGrid.bind(myGridID, "cellDoubleClick", function(event) {
// 				if(event.dataField == "name" || event.dataField == "number") {
// 					var rowItem = event.item;
// 					var url = "/Windchill/plm/part/viewPart";
// 					var popupUrl = url + "?oid=" + rowItem.oid + "&popup=true";
// 					$(document).openURLViewOpt(popupUrl, 1500, 750, "no");
// 				}
// 			});
// 	};
	
	function loadGridData() {
		let params = new Object();
		let url = getCallUrl("/part/listPart");
		<%
		if(isProduct) {
		%>
			url = "/Windchill/plm/part/listPart";
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
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			$("input[name=sessionid]").val(data.sessionid);
			$("input[name=curPage]").val(data.curPage);
			AUIGrid.setGridData(myGridID, data.list);
			parent.close();
		});
	};
	
	$(function() {
		// resize ...
		$(parent.document).find("#toggle").click(function() {
			$(parent.document).find("#body").css("width", "100vw");
			AUIGrid.resize("#grid_wrap");
		})

		$("#searchBtn").click(function() {
			loadGridData();
		})

		// 셀렉트박스 바인딩
		select("state");
		// 라디오박스 바인딩
		radio("latest");
		$(document).setHTML();
	}).keypress(function(e) {
		let keyCode = e.keyCode;
		if (keyCode == 13) {
			loadGridData();
		}
	})
</script>