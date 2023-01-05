<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTUser"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String required = (String) request.getParameter("required");
	String colspan = (String) request.getParameter("colspan");

	if (StringUtils.isNull(colspan)) {
		colspan = "3";
	}

	boolean isReq = false;
	if ("true".equalsIgnoreCase(required)) {
		isReq = true;
	}
	WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
%>
<tr>
	<th>
	<%
		if(isReq) {
	%>
	<font class="req">결재</font>
	<%
		} else {
	%>
	<span class="lineMsg">결재</span>
	<%
		}
	%>
	</th>
	<td colspan="<%=colspan %>">
		<table class="in_btn_table">
			<tr id="series_add">
				<td class="add">
					<input type="button" value="결재선 지정" title="결재선 지정" id="addLine_series">
					<input type="button" value="결재선 삭제" title="결재선 삭제" id="delLine" class="blueBtn">
					<!-- <input type="button" value="병렬" title="병렬" id="parallel_table_btn" class="redBtn"> -->
					<%
						if(isReq) {
					%>
					&nbsp;<span class="start">*</span>
					<font class="msg">
					결재가 필수인 경우 결재자가 반드시 한명 이상 지정되어야 합니다.
					</font>
					<%
						}
					%>
				</td>
			</tr>
			
			<tr id="parallel_add">
				<td class="add">
					<input type="button" value="결재선 지정" title="결재선 지정" id="addLine_parallel">
					<input type="button" value="결재선 삭제" title="결재선 삭제" id="delLine" class="blueBtn">
					<!-- <input type="button" value="직렬" title="직렬" id="series_table_btn" class="redBtn"> -->
					<%
						if(isReq) {
					%>
					&nbsp;<span class="start">*</span>
					<font class="msg">
					결재가 필수인 경우 결재자가 반드시 한명 이상 지정되어야 합니다.
					</font>
					<%
						}
					%>
				</td>
			</tr>				
		</table>
		
        <table id="tblBackground">
            <tr>
                <td>
					<div id="app_container">
						<!-- 직렬 -->
						<table id="create_series_table" class="create_table_in create_series_table fix_table">
							<colgroup>
								<col width="40">
								<col width="80">
								<col width="200">
								<col width="200">
								<col width="200">
								<col width="200">
								<col width="200">
							</colgroup>
							<thead>
								<tr>
									<th>
										<input type="checkbox" name="allLines">
										<input type="hidden" name="lineType" value="series">
									</th>
									<th>순서</th>
									<th>결재타입</th>
									<th>이름</th>
									<th>아이디</th>
									<th>직급</th>
									<th>부서</th>
								</tr>
							</thead>
							<tbody id="addLineBody_series">
								<tr id="nodataSeriesLine">
									<td class="nodata" colspan="7">지정된 결재라인이 없습니다.</td>
								</tr>				
							</tbody>
						</table>		
						
						<!-- <table id="create_parallel_table" class="create_table_in create_parallel_table fix_table">
							<colgroup>
								<col width="40">
								<col width="100">
								<col width="*">
								<col width="150">
								<col width="150">
								<col width="150">
							</colgroup>
							<thead>
								<tr>
									<th>
										<input type="checkbox" name="allLines">
									</th>
									<th>결재타입</th>
									<th>이름</th>
									<th>아이디</th>
									<th>부서</th>
									<th>직급</th>
								</tr>				
							</thead>
							<tbody id="addLineBody_parallel">
								<tr id="nodataParallelLine">
									<td class="nodata" colspan="6">지정된 병렬 결재라인이 없습니다.</td>
								</tr>					
							</tbody>
						</table> -->				
					</div>
				</td>
			</tr>
		</table>
	</td>
</tr>