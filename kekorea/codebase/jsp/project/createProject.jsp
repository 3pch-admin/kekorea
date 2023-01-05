<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.project.Template"%>
<%@page import="e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	// module root
	String root = DocumentHelper.ROOT;

	ArrayList<Template> tmp = (ArrayList<Template>)request.getAttribute("template");
	ArrayList<String> customer = (ArrayList<String>) request.getAttribute("customer");
	ArrayList<String> project_type = (ArrayList<String>) request.getAttribute("project_type");
%>    
<td valign="top">
	<!-- script area -->
	<script type="text/javascript">
	$(document).ready(function() {

		var parent_code;
		$("#pTemplate").bindSelect();
		
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
		
		$("#ins_location").bindSelect();
		
		$("input").checks();
	})
	</script>
	
	<table class="btn_table">
	<!-- create header title -->
	
		<tr>
		<td>
				<div class="header_title">
				<i class="axi axi-subtitles"></i><span>작번 등록</span>
				<!-- req msg -->
				<font class="reqMsg">(빨간색 속성 값은 필수 입력 값입니다.)</font>
			
			</div>
			</td>
			<td class="right">
				<input type="button" value="등록" id="createProjectBtn" title="등록" >
				<input type="button" value="취소" id="backBtn" title="취소" class="blueBtn">
			</td>
		</tr>
	</table>
	
	<!-- create button -->
	
	
	<!-- create table -->
	<table class="create_table">
		<tr>
			<th class="min-wid200"><font class="req">KEK 작번</font></th>
			<td>
				<input type="text" name="kekNumber" id="kekNumber" class="AXInput wid300">	
			</td>
			<th class="min-wid200"><font class="req">작번 발행일</font></th>
			<td>
				<input type="text" name="postdate" id="postdate" class="AXInput datePicker"> 
			</td>			
		</tr>
		<tr>
			<th><font class="req">KE 작번</font></th>
			<td>
				<input type="text" name="keNumber" id="keNumber" class="AXInput wid300">
			</td>
			<th><font class="req">요구납기일</font></th>
			<td>
				<input type="text" name="postdate_m" id="postdate_m" class="AXInput datePicker"> 
			</td>	
						
		</tr>
		<tr>
			<th><font class="req">USER ID</font></th>
			<td>
				<input type="text" name="userId" id="userId" class="AXInput wid300">
			</td>
			<th><font class="req">거래처</font></th>
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
		</tr>
		<tr>
			<th><font class="req">막종</font></th>
			<td>
				<input type="text" name="mak" id="mak" class="AXInput wid300">
			</td>
			<th><font class="req">설치 장소</font></th>
			<td>
				<select name="ins_location" id="ins_location" class="AXSelect wid100">
					<option value="">선택</option>
				</select>
			</td>	
			
		</tr>
		<tr>
		<th><font class="req">모델</font></th>
			<td>
				<input type="text" name="model" id="model" class="AXInput wid300">
			</td>	
			<th><font class="req">작번 유형</font></th>
			<td>
				<select name="pType" id="pType" class="AXSelect wid100">
					<option value="">선택</option>
					<%
						for(int i=0;i<project_type.size();i++) {
					%>
						<option value="<%=project_type.get(i)%>"><%=project_type.get(i)%></option>
					<%} %>
				</select>
			</td>
		</tr>
		<tr>
			<th>작번 템플릿</th>
			<td colspan="3">
					<select name="pTemplate" id="pTemplate" class="AXSelect wid300">
						<option value="">선택</option>
								<%
									for(Template pTemplate : tmp) {
								%>
								<option value="<%=pTemplate.getName() %>"><%=pTemplate.getName() %></option>
								<%
									}
								%>
					</select>
				<!-- <input type="text" name="pTemplate" id="pTemplate" class="AXInput wid300"> -->
			</td>			
		</tr>
		<tr>
			<th><font class="req">작업 내용</font><br><span id="descDocCnt">0</span>/1000</th>
			<td colspan="3">
				<!-- rows 세로, cols 가로 -->
				<textarea class="AXTextarea" name="description" id="description" rows="3" cols=""></textarea>
			</td>			
		</tr>			
	</table> 
</td>