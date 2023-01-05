<%@page import="e3ps.project.beans.ProjectViewData"%>
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
	
	ProjectViewData data = (ProjectViewData) request.getAttribute("data");
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
					setValue:"<%=data.ins_location%>",
                    alwaysOnChange: true,
				})
			}
		})
		
// 		$("#ins_location").bindSelect();
		
		$("#pType").bindSelectSetValue("<%=data.pType%>");
		$("#customer").bindSelectSetValue("<%=data.customer%>");
<%-- 		$("#ins_location").bindSelectSetValue("<%=data.ins_location%>"); --%>
		
		$("input").checks();
	})
	</script>
	<input type="hidden" name="oid" id="oid" value="<%=data.oid%>">
	<table class="btn_table">
	<!-- create header title -->
		<tr>
		<td>
				<div class="header_title">
				<i class="axi axi-subtitles"></i><span>작번 수정</span>
				<!-- req msg -->
				<font class="reqMsg">(빨간색 속성 값은 필수 입력 값입니다.)</font>
			
			</div>
			</td>
			<td class="right">
				<input type="button" value="수정" id="modifyProjectActionBtn" title="수정" >
				<input type="button" value="닫기" id="closeDocBtn" title="닫기" class="redBtn">
<!-- 				<input type="button" value="취소" id="backBtn" title="취소" class="blueBtn"> -->
			</td>
		</tr>
	</table>
	
	<!-- create button -->
	
	
	<!-- create table -->
	<table class="create_table">
		<tr>
			<th class="min-wid200"><font class="req">KEK 작번</font></th>
			<td>
				<input type="text" name="kekNumber" id="kekNumber" class="AXInput wid300" readonly="readonly" value="<%=data.kek_number%>">	
			</td>
			<th class="min-wid200">작번 발행일</th>
			<td>
				<input type="text" name="postdate" id="postdate" class="AXInput datePicker" value="<%=data.pDate%>"> 
			</td>			
		</tr>
		<tr>
			<th>KE 작번</th>
			<td>
				<input type="text" name="keNumber" id="keNumber" class="AXInput wid300" value="<%=data.ke_number%>">
			</td>
			<th>요구납기일</th>
			<td>
				<input type="text" name="postdate_m" id="postdate_m" class="AXInput datePicker" value="<%=data.customDate%>"> 
			</td>	
						
		</tr>
		<tr>
			<th>USER ID</th>
			<td>
				<input type="text" name="userId" id="userId" class="AXInput wid300" value="<%=data.userID%>">
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
		</tr>
		<tr>
			<th>막종</th>
			<td>
				<input type="text" name="mak" id="mak" class="AXInput wid300" value="<%=data.mak%>">
			</td>
			<th>설치 장소</th>
			<td>
				<select name="ins_location" id="ins_location" class="AXSelect wid100">
					<option value="">선택</option>
				</select>
			</td>	
		</tr>
		<tr>
		<th>모델</th>
			<td>
				<input type="text" name="model" id="model" class="AXInput wid300" value="<%=data.model%>">
			</td>	
			<th>작번 유형</th>
			<td>
				<select name="pType" id="pType" class="AXSelect wid100">
					<option value="">선택</option>
					<%
						for(int i=0;i<project_type.size();i++){
					%>
						<option value="<%=project_type.get(i).trim()%>"><%=project_type.get(i).trim()%></option>
					<%	} %>
				</select>
			</td>
		</tr>
		<tr>
			<th>작업 내용<br><span id="descDocCnt">0</span>/1000</th>
			<td colspan="3">
				<!-- rows 세로, cols 가로 -->
				<textarea class="AXTextarea" name="description" id="description" rows="3" cols=""><%=data.description %></textarea>
			</td>			
		</tr>			
	</table> 
	
</td>