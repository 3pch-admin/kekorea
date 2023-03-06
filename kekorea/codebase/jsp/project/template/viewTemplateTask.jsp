<%@page import="e3ps.project.dto.TaskViewData"%>
<%@page import="e3ps.project.dto.TemplateViewData"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	// popup check
	String popup = (String) request.getParameter("popup");
	boolean isPopup = Boolean.parseBoolean(popup);

	// data
	TaskViewData data = (TaskViewData) request.getAttribute("data");
	TemplateViewData tdata = (TemplateViewData) request.getAttribute("tdata");
	
	// isAdmin
	boolean isAdmin = CommonUtils.isAdmin();
	String toid = tdata.oid;
	String oid = data.oid;
%>

<td valign="top">
	<script type="text/javascript">
	$(document).ready(function() {
		var len = "<%=tdata.description.length() %>";
		$("#descTempCnt").text(len);
		
		var len2 = "<%=data.description.length() %>";
		$("#descTaskCnt").text(len2);
		
		$("input").checks();
		
// 		$("#left_menu_td").hide();
// 		$("img.right_switch").show();
		$("#colGroups").remove();
		$(document).setHTML();
		
	})
	</script>
	<input type="hidden" name="oid" id="oid" value="<%=data.oid %>">
	<input type="hidden" name="popup" id="popup" value="<%=isPopup %>">
	
	<table class="btn_table">
		<tr>
			<td>
				<div class="header_title">
					<i class="axi axi-subtitles"></i><span>템플릿 정보</span>
				</div>
			</td>
			<td>
				<div class="right">
					<input type="button" value="수정" id="modifyBtn" title="수정">
					<input type="button" value="닫기" id="closeTemplate" title="닫기" class="redBtn">
				</div>
			</td>
		</tr>
	</table>

	<table class="container_table">
		<tr>
			<!-- only folder tree.. -->
			<jsp:include page="/jsp/project/template/include_templateTask.jsp">
				<jsp:param value="<%=toid %>" name="oid" />
			</jsp:include>
			<td id="container_td" valign="top">
				<table class="view_table">
					<tr>
						<th>템플릿 이름</th>
						<td><%=tdata.name %></td>
						<th>총 기간</th>
						<td><%=tdata.duration %></td>			
					</tr>		
					<tr>
						<th>작성자</th>
						<td><%=tdata.creator %></td>
						<th>작성일</th>
						<td><%=tdata.createDate %></td>			
					</tr>			
					<tr>
						<th>수정자</th>
						<td><%=tdata.modifier %></td>
						<th>수정일</th>
						<td><%=tdata.modifyDate %></td>			
					</tr>					
					<tr>
						<th>설명<br><span id="descTempCnt">0</span>/4000</th>
						<td colspan="3">
							<textarea rows="3" cols="" class="AXTextarea bgk theight50" readonly="readonly"><%=tdata.description %></textarea>
						</td>			
					</tr>		
				</table>
				
				<div class="header_title margin_top10">
					<i class="axi axi-subtitles"></i><span>태스크 정보</span>
				</div>
				
				<table class="project_table">
					<tr>
						<th>태스크 타입</th>
						<th>태스크 명</th>
						<th>기간</th>
						<th>할당율</th>
						<th>작성자</th>
						<th>작성일</th>	
					</tr>		
					<tr>
						<td class="center"><%=data.taskType %></td>
						<td class="center"><%=data.name %></td>
						<td class="center"><%=data.duration %>일</td>	
						<td class="center"><%=data.allocate %>%</td>	
						<td class="center"><%=data.creator %></td>	
						<td class="center"><%=data.createDate %></td>
					</tr>
				</table>
				
				<div class="header_title margin_top10">
					<i class="axi axi-subtitles"></i><span>선행 태스크</span>
				</div>		
				
				<jsp:include page="/jsp/project/template/refDependencyTask.jsp">
					<jsp:param value="<%=data.oid %>" name="oid"/>
				</jsp:include>

			</td>
		</tr>
	</table>
</td>
