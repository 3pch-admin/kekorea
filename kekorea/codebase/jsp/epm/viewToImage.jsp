<%@page import="e3ps.epm.dto.PRODUCTAttr"%>
<%@page import="e3ps.common.util.IBAUtils"%>
<%@page import="e3ps.epm.dto.CADAttr"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@page import="e3ps.epm.dto.EpmViewData"%>
<%@page import="e3ps.part.beans.PartViewData"%>
<%@page import="wt.part.WTPart"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	EpmViewData data = (EpmViewData) request.getAttribute("data");
	String oid = (String) request.getAttribute("oid");
	boolean isAdmin = CommonUtils.isAdmin();
	boolean isPopup = Boolean.parseBoolean((String) request.getParameter("popup"));
	boolean is2D = data.is2D;
	
	boolean isProduct = data.isProduct;
	boolean isLibrary = data.isLibrary;

	String titleText = "가공품";
	if (isLibrary) {
		titleText = "구매품";
	}
%>
<td valign="top"><script type="text/javascript">
	$(document).ready(function() {
		$(".viewPartInfo").click(function() {
			$oid = $(this).data("oid");
			var url = "/Windchill/plm/part/viewPart?oid=" + $oid + "&popup=true";
			var opt = "scrollbars=yes, resizable=yes";
			$(document).openURLViewOpt(url, 1200, 600, opt);
		}).mouseover(function() {
			$(this).css("cursor", "pointer").attr("title", "<%=titleText%>정보보기");
		})
		
		
		$(".viewToImage").click(function() {
			$(document).onLayer();
			document.location.href = "/Windchill/plm/epm/viewToImage?oid=<%=oid%>&popup=<%=isPopup%>";
		})

		$(".epmView").click(function() {
			$(document).onLayer();
			document.location.href = "/Windchill/plm/epm/viewEpm?oid=<%=oid%>&popup=<%=isPopup%>";
		})
		
		$(".viewToEChange").click(function() {
			$(document).onLayer();
			document.location.href = "/Windchill/plm/epm/viewToEChange?oid=<%=oid%>&popup=<%=isPopup%>";
		})
	})
</script> <%
 	if (isPopup) {
 %>
	<table class="btn_table top_margin0">
		<tr>
			<td class="left">
				<div class="AXTabs">
					<div class="AXTabsTray">
						<a class="AXTab epmView" title="<%=titleText%>정보"><%=titleText%>정보</a> <a class="AXTab viewToEChange" title="설계변경 정보">설계변경 정보</a>
						<%
							if (is2D) {
						%>
						<a class="AXTab viewToImage on" title="이미지 파일">이미지 파일</a>
						<%
							}
						%>
						<div class="ax-clear"></div>
					</div>
				</div>
			</td>
			<td class="right"><input type="button" class="" value="버전정보 (I)" id="historyBtn" data-oid="<%=data.oid%>" title="버전정보 (I)"> <input type="button" value="닫기 (C)" title="닫기 (C)" id="closeBtn"
				class="redBtn"></td>
		</tr>
	</table> <%
 	} else {
 %>
	<div class="AXTabs">
		<div class="AXTabsTray">
			<a class="AXTab epmView" title="<%=titleText%>정보"><%=titleText%>정보</a> <a class="AXTab viewToEChange" title="설계변경 정보">설계변경 정보</a>
			<%
				if (is2D) {
			%>
			<a class="AXTab viewToImage on" title="이미지 파일">이미지 파일</a>
			<%
				}
			%>
			<div class="ax-clear"></div>
		</div>
	</div> <%
 	}
 %>
	<table class="view_table">
		<colgroup>
<!-- 			<col width="230"> -->
<!-- 			<col width="400"> -->
<!-- 			<col width="230"> -->
<!-- 			<col width="400"> -->
<!-- 			<col width="288"> -->
		</colgroup>
		<tr>
			<th><%=titleText%> 도면명</th>
			<td colspan="3"><%=data.name%></td>
			<td class="center pview_td" rowspan="7"><jsp:include page="/jsp/common/thumnail.jsp">
					<jsp:param value="<%=data.oid%>" name="oid" />
					<jsp:param value="<%=data.creoView%>" name="url" />
					<jsp:param value="<%=data.is2D%>" name="is2D" />
				</jsp:include></td>
		</tr>
		<tr>
			<th><%=titleText%> 도면번호</th>
			<td colspan="3"><%=data.number%></td>
		</tr>
		<tr>
			<th>버전</th>
			<td><%=data.version%>.<%=data.iteration%> <%
 	if (!data.isLatest) {
 %> <font class="goLatest" data-popup="<%=isPopup%>" data-oid="<%=data.latestOid%>" title="최신버전으로 이동">최신버전으로 이동</font> <%
 	}
 %></td>
			<th>상태</th>
			<td><%=data.state%></td>
		</tr>
		<tr>
			<th>저장위치</th>
			<td><%=data.location%></td>
			<th><%=titleText%></th>
			<%
				if (data.pdata != null) {
			%>
			<td class="viewPartInfo" data-oid="<%=data.pdata.oid%>"><%=data.pdata.number%> <%
 	} else {
 %>
			<td><font color="red"><%=titleText%>이 없습니다.</font></td>
			<%
				}
			%>
		</tr>
		<tr>
			<th>작성자</th>
			<td><%=data.creator%></td>
			<th>작성일</th>
			<td><%=data.createDate%></td>
		</tr>
		<tr>
			<th>수정자</th>
			<td><%=data.modifier%></td>
			<th>수정일</th>
			<td><%=data.modifyDate%></td>
		</tr>
		<tr>
			<th><%=titleText%> 도면파일</th>
			<td <%if (!data.is2D) {%> colspan="3" <%}%>><a href="<%=data.cadData[5]%>"> <img class="pos2" src="<%=data.iconPath%>"> <%=data.cadData[2]%>&nbsp;[<%=data.cadData[3]%>]
			</a></td>
			<%
				if (data.is2D) {
			%>
			<th>PDF</th>
			<td>
				<%
					if (data.pdf[5] != null) {
				%> <a href="<%=data.pdf[5]%>"> <img class="pos2" src="<%=data.pdf[4]%>"> <%=data.pdf[2]%>&nbsp;[<%=data.pdf[3]%>]
			</a> <%
 	} else {
 %> <font color="red" class="noPDF" data-oid="<%=data.oid%>">클릭해서 PDF 생성하세요.</font> <%
 	}
 	}
 %>
			</td>
		</tr>
	</table>
	<div class="header_title margin_top10">
		<i class="axi axi-subtitles"></i><span>이미지 파일</span>
	</div>

	<table class="view_table">
		<colgroup>
<!-- 			<col width="230"> -->
<!-- 			<col width="*"> -->
		</colgroup>
		<jsp:include page="/jsp/common/images.jsp">
			<jsp:param value="<%=data.oid%>" name="oid" />
		</jsp:include>
	</table>


	<table class="btn_table">
		<tr>
			<td class="center">
				<%
					if (!isPopup) {
				%> <input type="button" class="" value="버전정보 (I)" id="historyBtn" data-oid="<%=data.oid%>" title="버전정보 (I)"> <input type="button" class="blueBtn" value="뒤로 (B)" id="backBtn" title="뒤로 (B)">
			</td>
			<%
				}
			%>
		</tr>
	</table></td>