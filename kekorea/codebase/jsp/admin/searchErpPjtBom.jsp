<%@page import="e3ps.common.util.DateUtils"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.util.Calendar"%>
<%@page import="e3ps.common.code.CommonCodeType"%>
<%@page import="e3ps.org.service.OrgHelper"%>
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

	boolean isBox = false;
	if (isAdmin) {
		isBox = false;
	}
	// module
	String module = ModuleKeys.list_code.name();
	System.out.println("모듈 이름 : " + module);
	// htmlUtils..
	HtmlUtils html = new HtmlUtils();
	// table header setting
	String[] headers = {"문서번호", "작번내부코드", "특이사항","수배리스트번호","USERID","ITEMSEQ", "작성일", "FLAG", "INTERFACE_DATE", "RESULT"};
	
	System.out.println("headers 이름 : " + headers.toString());
	
	// table header key
	String[] keys = {"DISNO", "PJTSEQ", "REMARKM", "DISTRIBUTIONNO","USERID","ITEMSEQ", "CREATE_TIME", "FLAG", "INTERFACE_DATE", "RESULT"};
	// table cols
	String[] cols = {"DISNO", "PJTSEQ", "REMARKM", "DISTRIBUTIONNO","USERID","ITEMSEQ", "CREATE_TIME", "FLAG", "INTERFACE_DATE", "RESULT"};
	// table style
// 	String[] styles = ColumnUtils.getColumnStyles(module, headers);
	
	String[] styles = new String[]{"40", "40", "40", "40", "40", "40", "40", "40", "40", "40"};
	

	Calendar calendar = Calendar.getInstance();
	calendar.add(Calendar.MONTH, -2);
	Timestamp before = new Timestamp(calendar.getTime().getTime());
	String pre = before.toString().substring(0, 10);
	String today = DateUtils.getCurrentTimestamp().toString().substring(0, 10);
	
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
		var url = "/Windchill/plm/admin/viewCode";
		grid.cellClickSmallPopup("name", url);
		grid.cellClickSmallPopup("read", url);
		grid.cellClickSmallPopup("role", url);
		grid.cellClickSmallPopup("type", url);
		grid.cellClickSmallPopup("objType", url);
		
		$.fn.getColumn = function() {
			
			var url = "/Windchill/plm/admin/searchErpPjtBomAction";
			var params = grid.getParams();
			grid.preLoading();
			$(document).ajaxCallServer(url, params, function(data) {
				grid.getData(data, headers, "<%=isBox%>", false, url);
				grid.completeLoading(data.sessionid, data.curPage);
				$("input[name=oid]").checks();
			}, false);
		}
		
		$("#searchBtn").click(function() {
			$(document).getColumn();
		});
		
		
		
		$("#codeType").change(function() {
			console.log("###");
			$(document).getColumn();
		});
		
		
		
		
		grid.bindHeaderContextmenu();
		grid.allCheckbox();
		grid.listCheckbox();
		grid.setBoxs();
		
		$(".list_table").tableHeadFixer();
		$(document).getColumn();
		
	}).keypress(function(e) {
		var keyCode = e.keyCode;
		if(keyCode == 13 && e.target.tagName != "BUTTON") {
			$(document).getColumn();
		}
	})
	function create() {
		/* $cnt = $("span#agreeCnt").text();
		$("span#agreeCnt").text($cnt-1);
		$(document).getColumn(); */
	}
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
		<i class="axi axi-subtitles"></i><span>산출물 연동</span>
		<!-- info search -->
		<jsp:include page="/jsp/common/search_info.jsp"></jsp:include>
	</div>
	
	<!-- container table -->
	<table class="container_table">
		<tr>
			<!-- container -->
			<td id="container_td">
				<!-- create table -->
				<table class="search_table">
					<tr>
						<th>코드타입</th>
						<td>
							<select name="ifFlag" id="ifFlag" class="AXSelect wid200">
								<option value="1">미처리</option>
								<option value="2">정상처리</option>
								<option value="3">오류처리</option>
							</select>
						</td>
						<th>결과</th>
						<td>
							<select name="resultMsg" id="resultMsg" class="AXSelect wid200">
							<option value="">선택</option>
								<!--  <option value="정상처리">정상처리</option>-->
								<option value="/이미 중단처리 된 작번입니다.">/이미 중단처리 된 작번입니다.</option>
								<option value="/품목이 누락되었습니다.">/품목이 누락되었습니다.</option>
								<option value="/수량이 누락되었습니다.">/수량이 누락되었습니다.</option>
							</select>
						</td>
					</tr>
					<tr>
						<th>작번코드</th>
						<td>
							<input type="text" name="pjtSeq" id="pjtSeq" class="AXInput wid200">
						</td>
						<th>전송일</th>
						<td>
							<input type="text" name="predate" id="predate" class="AXInput" value="<%=pre %>"> ~ 
							<input type="text" name="postdate" id="postdate" class="AXInput twinDatePicker" data-start="predate" value="<%=today %>"> 
							<i title="삭제" class="axi axi-ion-close-circled delete-calendar" data-start="predate" data-end="postdate"></i>
						</td>
					</tr>
				</table> 
				
				<!-- button table -->
				<table class="btn_table">
					<tr>
					<td>
						</td>
						<td class="right">
							 <input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회"> 
							 <input type="button" value="초기화" class="" id="initGrid" title="초기화">
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
