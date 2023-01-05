<%@page import="e3ps.project.Project"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%
	String poid = (String) request.getParameter("oid");
	WTUser pm = (WTUser) request.getAttribute("pm");
	WTUser subpm = (WTUser) request.getAttribute("subpm");
	WTUser machine = (WTUser) request.getAttribute("machine");
	WTUser elec = (WTUser) request.getAttribute("elec");
	WTUser soft = (WTUser) request.getAttribute("soft");
	boolean isPopup = Boolean.parseBoolean((String) request.getParameter("popup"));
	String kekState = (String) request.getAttribute("kekState");
	ReferenceFactory rf = new ReferenceFactory();
	Project project = (Project)rf.getReference(poid).getObject();
			
	boolean bool = project.getPType().equals("견적");
	
%>
<td valign="top">
	<!-- script area -->
	<script type="text/javascript">
	$(document).ready(function() {
		$("#kekState").bindSelect();
		$("#kekState").bindSelectSetValue("<%=kekState %>");
	})
	</script>
	<input type="hidden" name="oid" value="<%=poid %>">
	<input type="hidden" name="popup" value="<%=isPopup %>">
	<table class="btn_table">
	<!-- create header title -->
	<tr>
		<td>
			<div class="header_title">
				<i class="axi axi-subtitles"></i><span>담당자 지정</span>
			</div>
			</td>
			<td class="right">
				<input type="button" value="저장" title="저장" class="blueBtn" id="setUser">
				<input type="button" value="닫기" onclick="self.close();" title="닫기" class="redBtn">
			</td>
		</tr>
	</table>
	
	<table class="create_table">
		<tr>
			<th class="min-wid100">총괄책임자</th>
			<td>
				<%
					String value = pm != null ? pm.getFullName() + " [" + pm.getName() + "]" : "";
					String oid = pm != null ? pm.getPersistInfo().getObjectIdentifier().getStringValue() : "";
				%>
				<input type="text" name="pm" id="pm" class="AXInput wid200" data-dbl="true" value="<%=value %>"> 
				<input type="hidden" name="pmOid" id="pmOid" value="<%=oid %>">
				<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="pm"></i>		
			</td>
		</tr>
		<tr>
			<th class="min-wid100">세부일정 책임자</th>
			<td>
				<%
					value = subpm != null ? subpm.getFullName() + " [" + subpm.getName() + "]" : "";
					oid = subpm != null ? subpm.getPersistInfo().getObjectIdentifier().getStringValue() : "";
				%>			
				<input type="text" name="sub_pm" id="sub_pm" class="AXInput wid200" data-dbl="true" value="<%=value %>"> 
				<input type="hidden" name="sub_pmOid" id="sub_pmOid" value="<%=oid %>">
				<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="sub_pm"></i>		
			</td>
		</tr>
		<tr>
			<th class="min-wid100">기계</th>
			<td>
				<%
					value = machine != null ? machine.getFullName() + " [" + machine.getName() + "]" : "";
					oid = machine != null ? machine.getPersistInfo().getObjectIdentifier().getStringValue() : "";
				%>			
				<input type="text" name="machine" id="machine" class="AXInput wid200" data-dbl="true" value="<%=value %>" data-dept="기계설계"> 
				<input type="hidden" name="machineOid" id="machineOid" value="<%=oid %>">
				<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="machine"></i>		
			</td>
		</tr>
		<tr>
			<th class="min-wid100">전기</th>
			<td>
				<%
					value = elec != null ? elec.getFullName() + " [" + elec.getName() + "]" : "";
					oid = elec != null ? elec.getPersistInfo().getObjectIdentifier().getStringValue() : "";
				%>			
				<input type="text" name="elec" id="elec" class="AXInput wid200" data-dbl="true" value="<%=value %>"  data-dept="전기설계">
				<input type="hidden" name="elecOid" id="elecOid" value="<%=oid %>">
				<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="elec"></i>		
			</td>
		</tr>
		<tr>
			<th class="min-wid100">SOFT</th>
			<td>
				<%
					value = soft != null ? soft.getFullName() + " [" + soft.getName() + "]" : "";
					oid = soft != null ? soft.getPersistInfo().getObjectIdentifier().getStringValue() : "";
				%>				
				<input type="text" name="soft" id="soft" class="AXInput wid200" data-dbl="true" value="<%=value %>"  data-dept="SW설계">
				<input type="hidden" name="softOid" id="softOid" value="<%=oid %>">
				<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="soft"></i>		
			</td>
		</tr>
		<tr>
			<th>작번상태</th>
			<td>
				<select name="kekState" id="kekState" class="AXSelect wid150">
					<option value="">선택</option>
					<option value="준비">준비</option>	
					<option value="설계중">설계중</option>
					<option value="설계완료">설계완료</option>
					<%
						if(!bool) {
					%>
					<option value="작업완료">작업완료</option>
					<%
						}
					%>
					<option value="중단됨">중단됨</option>
					<option value="취소">취소</option>
				</select>
			</td>			
		</tr>								
	</table>
</td>