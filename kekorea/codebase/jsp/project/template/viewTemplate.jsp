<%@page import="wt.org.WTUser"%>
<%@page import="e3ps.project.beans.TemplateViewData"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	// popup check
	String popup = (String) request.getParameter("popup");
	boolean isPopup = Boolean.parseBoolean(popup);
	// data
	TemplateViewData data = (TemplateViewData) request.getAttribute("data");
	// isAdmin
	boolean isAdmin = CommonUtils.isAdmin();
	String oid = data.oid;
	WTUser pm = data.pm;
	WTUser subPm = data.subPm;
%>

<td valign="top">
	<script type="text/javascript">
	$(document).ready(function() {
		var len = "<%=data.description.length() %>";
		$("#descTempCnt").text(len);
		
		$("input").checks();
		
		/* $("#left_menu_td").hide();
		$("img.right_switch").show(); */
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
					<%
						if(isAdmin) {
					%>
					<input type="button" value="수정" id="modifyTemplateBtn" title="수정" >
					<input type="button" value="삭제" id="deleteTemplateBtn" title="삭제" class="redBtn">
					<%
						}
					%>						
					<input type="button" value="닫기" id="closeTemplate" title="닫기" class="redBtn">
				</div>
			</td>
		</tr>
	</table>
	
	
	<table class="container_table">
		<tr>
			<!-- only folder tree.. -->
			<jsp:include page="/jsp/project/template/include_templateTask.jsp">
				<jsp:param value="<%=oid %>" name="oid" />
			</jsp:include>
			<td id="container_td" valign="top">
				<!-- search table -->
				
				<table class="view_table">
					<tr>
						<th>템플릿 이름</th>
						<td><input type="text" name="name" value="<%=data.name %>" class="AXInput"></td>
						<th>총 기간</th>
						<td><%=data.duration %></td>			
					</tr>		
					<tr>
						<th>작성자</th>
						<td><%=data.creator %></td>
						<th>작성일</th>
						<td><%=data.createDate %></td>			
					</tr>			
					<tr>
						<th>수정자</th>
						<td><%=data.modifier %></td>
						<th>수정일</th>
						<td><%=data.modifyDate %></td>			
					</tr>					
					<tr>
						<th>설명<br><span id="descTempCnt">0</span>/4000</th>
						<td colspan="3">
							<textarea rows="3" cols="" class="AXTextarea bgk theight50" readonly="readonly"><%=data.description %></textarea>
						</td>			
					</tr>		
				</table>
				
				<div class="header_title margin_top10">
					<i class="axi axi-subtitles"></i><span>구성원 정보</span>
				</div>
				
				<table class="role_table">
					<colgroup>
						<col width="50%">
						<col width="50%">
<!-- 						<col width="300"> -->
<!-- 						<col width="300"> -->
<!-- 						<col width="300"> -->
					</colgroup>
					<tr>
						<th>총괄 책임자</th>
						<th>세부일정 책임자</th>
					<tr>
						<td class="center">
						<%
							if(!isAdmin) {
						%>
						<%=pm != null ? pm.getFullName() : "" %>
						<%	
							} else {
								String value = pm != null ? pm.getFullName() + " [" + pm.getName() + "]" : "";
								String poid = pm != null ? pm.getPersistInfo().getObjectIdentifier().getStringValue() : "";
						%>
							<input type="text" name="pm" id="pm" class="AXInput wid200" data-dbl="true" value="<%=value %>"> 
							<input type="hidden" name="pmOid" value="<%=poid %>">
							<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="pm"></i>						
						<%
							}
						%>
						</td>
						<td class="center">
							<%
								if(!isAdmin) {
							%>					
							<%=subPm != null ? subPm.getFullName() : "" %>
							<%
								} else {
									String value = subPm != null ? subPm.getFullName() + " [" + subPm.getName() + "]" : "";
									String poid = subPm != null ? subPm.getPersistInfo().getObjectIdentifier().getStringValue() : "";
							%>
							<input type="text" name="sub_pm" id="sub_pm" class="AXInput wid200" data-dbl="true" value="<%=value %>"> 
							<input type="hidden" name="sub_pmOid" value="<%=poid %>">
							<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="sub_pm"></i>
							<%
								}
							%>
						</td>
					</tr>
				</table>
						
				<%
					if(!isPopup) {
				%>
				<!-- <table class="btn_table">
					<tr>
						<td class="center">
							<input type="button" value="목록" id="listTempBtn" title="목록" class="blueBtn">
						</td>
					</tr>
				</table> -->
				<%
					}
				%>
			</td>
		</tr>
	</table>
</td>