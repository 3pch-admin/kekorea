<%@page import="e3ps.epm.dto.ACADAttr"%>
<%@page import="e3ps.epm.dto.PROEAttr"%>
<%@page import="wt.viewmarkup.WTMarkUp"%>
<%@page import="wt.viewmarkup.Viewable"%>
<%@page import="com.ptc.wvs.server.util.PublishUtils"%>
<%@page import="wt.representation.Representation"%>
<%@page import="wt.representation.Representable"%>
<%@page import="e3ps.common.util.ThumnailUtils"%>
<%@page import="wt.viewmarkup.ViewMarkUpHelper"%>
<%@page import="wt.part.WTPartUsageLink"%>
<%@page import="wt.vc.struct.StructHelper"%>
<%@page import="wt.epm.build.EPMBuildRule"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="e3ps.epm.dto.PRODUCTAttr"%>
<%@page import="e3ps.common.util.IBAUtils"%>
<%@page import="e3ps.epm.dto.CADAttr"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="e3ps.part.beans.PartViewData"%>
<%@page import="e3ps.common.util.CommonUtils"%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	// popup check
	String popup = (String) request.getParameter("popup");
	boolean isPopup = Boolean.parseBoolean(popup);
	
	String checkY = (String) request.getAttribute("checkY");
	
	// data
	PartViewData data = (PartViewData) request.getAttribute("data");
	// isAdmin
	boolean isAdmin = CommonUtils.isAdmin();
	
	boolean isProduct = data.isProduct;
	boolean isLibrary = data.isLibrary;
	boolean isEPM = data.epm != null ? true : false;
	
%>

<td valign="top">
	<!-- script -->
	<script type="text/javascript">
	$(document).ready(function() {
		$("#closePartBtn").click(function() {
			self.close();
		})
	})
	</script>
	
	<input type="hidden" name="oid" id="oid" value="<%=data.oid %>">
	<input type="hidden" name="popup" id="popup" value="<%=isPopup %>">
	<table class="btn_table">
		<tr>
			<td>
				<div class="header_title">
					<i class="axi axi-subtitles"></i><span>부품정보</span>
				</div>
			</td>
			<td>
				<div class="right">
<%-- 					<input type="button" value="상위부품" id="infoUpPart" title="상위부품" class="blueBtn" data-oid="<%=data.oid %>" data-context="<%=data.context %>"> --%>
<%-- 					<input type="button" value="하위부품" id="infoDownPart" title="하위부품" data-oid="<%=data.oid %>" data-context="<%=data.context %>"> --%>
<%-- 					<input type="button" value="제품" id="infoEndPart" title="제품" class="blueBtn" data-oid="<%=data.oid %>" data-context="<%=data.context %>"> --%>
					<input type="button" value="결재이력" data-oid="<%=data.oid%>" class="infoApprovalHistory" id="infoApprovalHistory" title="결재이력">
					<input type="button" value="버전정보" id="infoVersionBtn" title="버전정보">
					<%-- <%
						if(data.isLibrary && data.isRevise) {
					%>
					<input type="button" value="개정" id="reviseLibraryPartBtn" title="개정" class="blueBtn">
					<%
						}
						if(data.isLibrary && data.isModify) {
					%>
					<input type="button" value="수정" id="modifyLibraryPartBtn" title="수정">
					<%
						}
					%>	 --%>	
					<%
					if(data.isModify) {
					%>	
					<input type="button" value="수정" id="modifyPartBtn" title="수정">
					<%} %>
					<%
					if(isPopup) {
				%>
					<input type="button" value="닫기" id="closePartBtn" title="닫기" class="redBtn">
				<%
					}
				%>
					
				</div>
			</td>
		</tr>
	</table>
	
	<table class="view_table">
		<colgroup>
			<col width="180">
			<col width="200">
			<col width="180">
			<col width="200">
			<col width="350">
		</colgroup>
		<tr>
			<th>파일이름</th>
<!-- 			<th>PRODUCT_NAME</th> -->
			<td colspan="3"><%=data.name %></td>
			<td rowspan="6" class="center">
				<jsp:include page="/jsp/common/thumnail.jsp">
					<jsp:param value="<%=data.oid %>" name="oid"/>
					<jsp:param value="<%=data.creoView %>" name="url"/>
				</jsp:include>
			</td>
		</tr>
		<%-- <tr>
			<th>파일이름</th>
			<td colspan="3"><%=data.number %></td>
		</tr>	 --%>	
		<tr>
			<th>버전</th>
			<td><%=data.fullVersion %></td>
			<th>상태</th>
			<td><%=data.state %></td>			
		</tr>		
		<tr>
			<th>저장위치</th>
			<td><%=data.location %></td>
			<th>도면</th>
			<%
				if(isEPM) {
					String eoid = data.epm.getPersistInfo().getObjectIdentifier().getStringValue();
			%>
			<td class="infoPer" data-oid="<%=eoid %>"><%=data.epm.getNumber() %></td>
			<%
				} else {
			%>
			<td><font color="red">도면이 없습니다.</font></td>
			<%
				}
			%>	
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
			<th>도면파일</th>
			<%
				if(isEPM) {
					String eoid = data.epm.getPersistInfo().getObjectIdentifier().getStringValue();
					String[] primarys = ContentUtils.getPrimary(data.epm);
					String icon = ContentUtils.getOpenIcon(data.epm);
					if(primarys[5] != null) {
			%>
			<td colspan="3">
				<a href="<%=primarys[5] %>"><img src="<%=icon %>" class="pos2">
				<%=primarys[2] %>
				</a>			
			</td>
			<%
					} else {
			%>
			<td colspan="3"><font color="red">도면파일이 없습니다.</font></td>
			<%
					}
				} else {
			%>
			<td colspan="3"><font color="red">도면파일이 없습니다.</font></td>
			<%
				}
			%>
		</tr>	
		<tr>
			<th>주 첨부파일</th>
			<td colspan="4">
				<jsp:include page="/jsp/common/primary.jsp">
					<jsp:param value="<%=data.oid %>" name="oid"/>
				</jsp:include>
			</td>
		</tr>	
<!-- 		<tr> -->
<!-- 			<th>첨부파일</th> -->
<!-- 			<td colspan="4"> -->
<%-- 				<jsp:include page="/jsp/common/secondary.jsp"> --%>
<%-- 					<jsp:param value="<%=data.oid %>" name="oid"/> --%>
<%-- 				</jsp:include> --%>
<!-- 			</td> -->
<!-- 		</tr>	 -->
		<%
// 			if(isProduct) {
// 				ACAD PROE
	//proe때 aCad는 다음에 정의
// 				NAME_OF_PARTS	DWG_NO
// 				MATERIAL			REMARKS
// 				PART_CODE		STD_UNIT
// 				MAKER			CUSNAME
// 				PRICE			CURRNAME		
// 				REF_NO

		if ("PROE".equals(data.cadType)) {
		%>
		<tr>
			<th>PROE 속성</th>
			<td colspan="4">
				<table class="view_table">
					<colgroup>
						<col width="250">
						<col width="400">
						<col width="250">
						<col width="400">
					</colgroup>
					<%
						PROEAttr[] proeAttrs = PROEAttr.values();
// 						CADAttr[] cadAttrs = CADAttr.values();
						for(int i=0; i<proeAttrs.length; i++) {
							String key = proeAttrs[i].name();
							String value = "";
							int intValue = 0;
							if("PRICE".equals(key)){
								intValue = IBAUtils.getIntegerValue(data.part, key);
							}else{
								value = IBAUtils.getStringValue(data.part, key);
							}
							
							if(i==proeAttrs.length-1){
					%>
						<tr>
								<th><%=key %></th>
					<%								
								if("PRICE".equals(key)){
					%>
									<td colspan="3"><%=intValue %></td>
					<%
								}else{
					%>
									<td colspan="3"><%=value %></td>
							<%
								}
								break;
							
							}
							%>
					<tr>
						<th><%=key %></th>
						<%
						if("PRICE".equals(key)){						
						%>
							<td><%=intValue %></td>
						<%
						} else{
						%>
						<td><%=value %></td>
						<%
						}
						%>
					<%
							i++;
							key = proeAttrs[i].name();
							value = IBAUtils.getStringValue(data.part, key);
					%>
						<th><%=key %></th>
						<%
						if("PRICE".equals(key)){						
						%>
							<td><%=intValue %></td>
						<%
						} else{
						%>
						<td><%=value %></td>
						<%
						}
						%>
					</tr>
					<%
						}
					%>
				</table>			
			</td>
		</tr>			
		<%
			}  else if ("ACAD".equals(data.cadType)) {
		%>
		<tr>
			<th>AUTOCAD 속성</th>
			<td colspan="4">
				<table class="view_table">
					<colgroup>
						<col width="250">
						<col width="400">
						<col width="250">
						<col width="400">
					</colgroup>
					<%
						ACADAttr[] acadAttrs = ACADAttr.values();
						for(int i=0; i<acadAttrs.length; i++) {
							String key = acadAttrs[i].name();
							String value ="";
							int intValue = 0;
							if("PRICE".equals(key)){
								intValue = IBAUtils.getIntegerValue(data.part, key);
							}else{
								value = IBAUtils.getStringValue(data.part, key);
							}
							
							if(i==acadAttrs.length-1){
					%>
								<tr>
								<th><%=key %></th>
							<%
							if("PRICE".equals(key)){
					%>
								<td colspan="3"><%=intValue %></td>
					<%
							} else {
						%>
								
								<td colspan="3"><%=value %></td>
							<%
							}
								break;
							}
							%>
					<tr>
						<th><%=key %></th>
					<%
						if("PRICE".equals(key)){						
						%>
							<td><%=intValue %></td>
						<%
						} else{
						%>
						<td><%=value %></td>
						<%
						}
						%>
					<%
							i++;
							key = acadAttrs[i].name();
							value = IBAUtils.getStringValue(data.part, key);
					%>
						<th><%=key %></th>
						<%
						if("PRICE".equals(key)){						
						%>
							<td><%=intValue %></td>
						<%
						} else{
						%>
						<td><%=value %></td>
						<%
						}
						%>
					</tr>
					<%
						}
					%>
				</table>			
			</td>
		</tr>
		<%
			} else {
		%>
	<tr>
			<th>PROE 속성</th>
			<td colspan="4">
				<table class="view_table">
					<colgroup>
						<col width="250">
						<col width="400">
						<col width="250">
						<col width="400">
					</colgroup>
					<%
						PROEAttr[] proeAttrs = PROEAttr.values();
// 						CADAttr[] cadAttrs = CADAttr.values();
						for(int i=0; i<proeAttrs.length; i++) {
							String key = proeAttrs[i].name();
							String value = "";
							int intValue = 0;
							if("PRICE".equals(key)){
								intValue = IBAUtils.getIntegerValue(data.part, key);
							}else{
								value = IBAUtils.getStringValue(data.part, key);
							}
							
							if(i==proeAttrs.length-1){
					%>
							<tr>
								<th><%=key %></th>
					<%
							if("PRICE".equals(key)){
					%>
								<td colspan="3"><%=intValue %></td>
					<%
							} else {
						%>
								
								<td colspan="3"><%=value %></td>
							<%
							}
								break;
							}
							%>
					<tr>
						<th><%=key %></th>
						<%
						if("PRICE".equals(key)){						
						%>
							<td><%=intValue %></td>
						<%
						} else{
						%>
						<td><%=value %></td>
						<%
						}
						%>
					<%
							i++;
							key = proeAttrs[i].name();
							value = IBAUtils.getStringValue(data.part, key);
					%>
						<th><%=key %></th>
						<%
						if("PRICE".equals(key)){						
						%>
							<td><%=intValue %></td>
						<%
						} else{
						%>
						<td><%=value %></td>
						<%
						}
						%>
					</tr>
					<%
						}
					%>
				</table>			
			</td>
		</tr>		
		<%
			}
		%>
		<tr>
			<th>관련문서</th>
			<td colspan="4">
				<jsp:include page="/jsp/part/refDocument.jsp">
					<jsp:param value="<%=data.oid %>" name="oid"/>
				</jsp:include>			
			</td>
		</tr>
		<%-- <tr>
			<th>관련 EBOM LIST</th>
			<td colspan="4">
				<jsp:include page="/jsp/part/refEBOM.jsp">
					<jsp:param value="<%=data.oid %>" name="oid"/>
				</jsp:include>			
			</td>
		</tr>	 --%>					
	</table>
</td>