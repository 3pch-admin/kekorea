<%@page import="e3ps.epm.beans.ACADAttr"%>
<%@page import="e3ps.epm.beans.PROEAttr"%>
<%@page import="e3ps.epm.beans.EpmViewData"%>
<%@page import="wt.vc.struct.StructHelper"%>
<%@page import="wt.epm.build.EPMBuildRule"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="e3ps.epm.beans.PRODUCTAttr"%>
<%@page import="e3ps.common.util.IBAUtils"%>
<%@page import="e3ps.epm.beans.CADAttr"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="e3ps.part.beans.PartViewData"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	// popup check
	String popup = (String) request.getParameter("popup");
	boolean isPopup = Boolean.parseBoolean(popup);

	// data
	EpmViewData data = (EpmViewData) request.getAttribute("data");
	// isAdmin
	boolean isAdmin = CommonUtils.isAdmin();
	
	boolean isProduct = data.isProduct;
	boolean isLibrary = data.isLibrary;
	boolean is2D = data.is2D;
	
// 	out.println(data.epm.getCabinetName());
// 	out.println(data.epm.
	
// 	IBAUtils.createIBA(data.epm, "s", "PART_CODE", "Y200038301");
	String titleText = "";
	if(isProduct) {
		titleText = "가공품";
	} else if(isLibrary) {
		titleText = "구매품";
	}
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
					<i class="axi axi-subtitles"></i><span>도면 정보</span>
				</div>
			</td>
			<td>
				<div class="right">
					<input type="button" value="결재이력" data-oid="<%=data.oid%>" class="infoApprovalHistory" id="infoApprovalHistory" title="결재이력">
					<input type="button" value="버전정보" id="infoVersionBtn" title="버전정보">
					<input type="button" value="닫기" id="closePartBtn" title="닫기" class="redBtn">
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
			<th>도면 번호</th>
			<td colspan="3"><%=data.number %></td>
		</tr>	
		<tr>
			<th>도면 이름</th>
			<td colspan="3"><%=data.name %></td>
			<td rowspan="9" class="center">
				<jsp:include page="/jsp/common/thumnail.jsp">
					<jsp:param value="<%=data.oid %>" name="oid"/>
					<jsp:param value="<%=data.creoView %>" name="url"/>
				</jsp:include>
			</td>
		</tr>
		<tr>
			<th>버전</th>
			<td colspan="3"><%=data.fullVersion %></td>
		</tr>
		<tr>
			<th>상태</th>
			<td><%=data.state %></td>		
			<th>저장위치</th>
			<td><%=data.location %></td>	
		</tr>
		<tr>
			<th>종류</th>
			<td></td>
			<th>응용프로그램</th>
			<td></td>
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
			<td>
				<%
					String[] primarys = data.cadData;
					String icon = ContentUtils.getOpenIcon(data.epm);
				%>
				<a href="<%=primarys[5] %>"><img src="<%=icon %>" class="pos2">
				<%=primarys[2] %>
				</a>					
			</td>
			<th>변환 파일</th>
			<td></td>
		</tr>
		<tr>
			<th>부품</th>
			<%
				if(data.part == null) {
			%>
			<td colspan="3"><font color="red">부품이 없습니다.</font></td>
			<%
				} else {
					String poid = data.part.getPersistInfo().getObjectIdentifier().getStringValue();
			%>
			<td colspan="3" class="infoPer" data-oid="<%=poid %>"><%=data.part.getNumber() %></td>
			<%
				}
			%>
		</tr>
		<tr>
			<th>설명</th>
			<td colspan="3"><%=data.description %></td>
		</tr>	
				
		
	</table>
	<%
		// 가공품 속성
		if ("PROE".equals(data.cadType)) {
	%>
	<div class="refAttr_wrap">
		<div class="header_title margin_top10">
			<i class="axi axi-subtitles"></i><span>도면 속성</span>
		</div>
		
		<table class="view_table">
			<colgroup>
				<col width="250">
				<col width="400">
				<col width="250">
				<col width="400">
			</colgroup>
			<%
				PROEAttr[] proeAttrs = PROEAttr.values();
				for(int i=0; i<proeAttrs.length; i++) {
					String key = proeAttrs[i].name();
					String value = IBAUtils.getStringValue(data.epm, key);
					
					if(i==proeAttrs.length-1){
			%>
			
			<tr>
				<th><%=key %></th>
				<td colspan="3"><%=value %></td>
					<%
						break;
					}
					%>
			<tr>
				<th><%=key %></th>
				<td><%=value %></td>
			<%
					i++;
					key = proeAttrs[i].name();
					value = IBAUtils.getStringValue(data.epm, key);
			%>
				<th><%=key %></th>
				<td><%=value %></td>
			</tr>
			<%
				}
			%>
		</table>
	</div>
	<%
		}  else if ("ACAD".equals(data.cadType)) {
	%>
	<div class="refAttr_wrap">
		<div class="header_title margin_top10">
			<i class="axi axi-subtitles"></i><span>도면 속성</span>
		</div>
		
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
					String value = IBAUtils.getStringValue(data.epm, key);
					
					if(i==acadAttrs.length-1){
			%>
			
			<tr>
				<th><%=key %></th>
				<td colspan="3"><%=value %></td>
					<%
						break;
					}
					%>
			<tr>
				<th><%=key %></th>
				<td><%=value %></td>
			<%
					i++;
					key = acadAttrs[i].name();
					value = IBAUtils.getStringValue(data.epm, key);
			%>
				<th><%=key %></th>
				<td><%=value %></td>
			</tr>
			<%
				}
			%>
		</table>
		</div>
	<%
		} else {
	%>
	<div class="refAttr_wrap">
		<div class="header_title margin_top10">
			<i class="axi axi-subtitles"></i><span>도면 속성</span>
		</div>
		
		<table class="view_table">
			<colgroup>
				<col width="250">
				<col width="400">
				<col width="250">
				<col width="400">
			</colgroup>
			<%
				PROEAttr[] proeAttrs = PROEAttr.values();
				for(int i=0; i<proeAttrs.length; i++) {
					String key = proeAttrs[i].name();
					String value = IBAUtils.getStringValue(data.epm, key);
					
					if(i==proeAttrs.length-1){
			%>
			
			<tr>
				<th><%=key %></th>
				<td colspan="3"><%=value %></td>
					<%
						break;
					}
					%>
			<tr>
				<th><%=key %></th>
				<td><%=value %></td>
			<%
					i++;
					key = proeAttrs[i].name();
					value = IBAUtils.getStringValue(data.epm, key);
			%>
				<th><%=key %></th>
				<td><%=value %></td>
			</tr>
			<%
				}
			%>	
	<%
		}
		if(!isPopup) {
	%>
	<table class="btn_table">
		<tr>
			<td class="center">
				<input type="button" value="버전정보" id="infoVersionBtn" title="버전정보">
				<%
					if(isProduct) {
				%>
				<input type="button" value="목록" id="listProductEpmBtn" title="목록" class="blueBtn">
				<%
					} else if(isLibrary) {
				%>			
				<input type="button" value="목록" id="listLibraryEpmBtn" title="목록" class="blueBtn">
				<%
					}
				%>			
			</td>
		</tr>
	</table>
	<%
		}
	%>
</td>