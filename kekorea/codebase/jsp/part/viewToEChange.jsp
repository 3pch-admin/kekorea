<%@page import="e3ps.echange.EBOM"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="e3ps.epm.dto.PRODUCTAttr"%>
<%@page import="e3ps.common.util.IBAUtils"%>
<%@page import="e3ps.epm.dto.CADAttr"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@page import="e3ps.epm.dto.EpmViewData"%>
<%@page import="e3ps.part.beans.PartViewData"%>
<%@page import="wt.part.WTPart"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	PartViewData data = (PartViewData) request.getAttribute("data");
	String oid = (String) request.getAttribute("oid");
	boolean isAdmin = CommonUtils.isAdmin();
	boolean isPopup = Boolean.parseBoolean((String) request.getParameter("popup"));
	
	boolean isProduct = data.isProduct;
	boolean isLibrary = data.isLibrary;

	String titleText = "가공품";
	if (isLibrary) {
		titleText = "구매품";
	}
%>
<td valign="top"><script type="text/javascript">
	$(document).ready(function() {

		$("#downPartBtn").click(function() {
			$oid = $(this).data("oid");
			$url = "/Windchill/plm/part/infoDownPart?oid=" + $oid + "&popup=true";
			$opt = "resizable=no, scrollbars=yes";
			$(document).openURLViewOpt($url, 1200, 600, $opt);
		})

		$("#upPartBtn").click(function() {
			$oid = $(this).data("oid");
			$url = "/Windchill/plm/part/infoUpPart?oid=" + $oid + "&popup=true";
			$opt = "resizable=no, scrollbars=yes";
			$(document).openURLViewOpt($url, 1200, 600, $opt);
		})

		$("#endPartBtn").click(function() {
			$oid = $(this).data("oid");
			$url = "/Windchill/plm/part/infoEndPart?oid=" + $oid + "&popup=true";
			$opt = "resizable=no, scrollbars=yes";
			$(document).openURLViewOpt($url, 1200, 200, $opt);
		})

		$(".viewEpmInfo").click(function() {
			$oid = $(this).data("oid");
			var url = "/Windchill/plm/epm/viewEpm?oid=" + $oid + "&popup=true";
			var opt = "scrollbars=yes, resizable=yes";
			$(document).openURLViewOpt(url, 1200, 600, opt);
		}).mouseover(function() {
			$(this).css("cursor", "pointer").attr("title", "도면정보보기");
		})
		
		$(".partView").click(function() {
			$(document).onLayer();
			document.location.href = "/Windchill/plm/part/viewPart?oid=<%=oid%>&popup=<%=isPopup%>";
		})
		
		$(".viewToEChange").click(function() {
			$(document).onLayer();
			document.location.href = "/Windchill/plm/part/viewToEChange?oid=<%=oid%>&popup=<%=isPopup%>";
		})
		
		$.fn.deletePart = function() {
			var box = $(document).setNonOpen();
			box.confirm({
				theme : "info",
				title : "<%=titleText%> 삭제",
				msg : "<%=titleText%>을 삭제 하시겠습니까?"
			}, function() {
				if (this.key == "ok") {
					var url = "/Windchill/plm/part/deletePartAction";
					var params = new Object();
					params.oid = "<%=oid%>";
					$(document).ajaxCallServer(url, params, function(data) {
						var msg = "<%=titleText%>이 삭제되었습니다.";
						box.alert({
							theme : "alert",
							title : "결과",
							msg : msg
						}, function() {
							if (this.key == "ok" || this.state == "close") {
								if("<%=titleText%>" == "구매품") {
									document.location.href = "/Windchill/plm/part/listLibraryPart";
								} else {
									document.location.href = "/Windchill/plm/part/listProductPart";
								}
							}
						})
					}, true);
				} else if (this.key == "cancel" || this.state == "close") {
					mask.close();
				}
			})
		}
		
		$("#deleteBtn").click(function() {
			$(documnet).deletePart();
		})
	}).keypress(function(e) {
		var keyCode = e.keyCode;
		if(keyCode == 73) {
			$(document).infoHistory("<%=oid%>");
		} else if(keyCode == 68) {
			$(document).deletePart();
		}
	})
</script> <%
 	if (isPopup) {
 %>
	<table class="btn_table top_margin0">
		<tr>
			<td class="left">
				<div class="AXTabs">
					<div class="AXTabsTray">
						<a class="AXTab partView" title="<%=titleText%>정보"><%=titleText%>정보</a> <a class="AXTab on viewToEChange" title="설계변경 정보">설계변경 정보</a>
						<div class="ax-clear"></div>
					</div>
				</div>
			</td>
			<td class="right"><input data-oid="<%=data.oid%>" type="button" class="" value="상위<%=titleText%>" id="upPartBtn" title="상위<%=titleText%>"> <input data-oid="<%=data.oid%>" type="button"
				class="blueBtn" value="하위<%=titleText%>" id="downPartBtn" title="하위<%=titleText%>"> <input data-oid="<%=data.oid%>" type="button" class="redBtn" value="제품" id="endPartBtn" title="제품">
				<input type="button" class="" value="버전정보 (I)" id="historyBtn" data-oid="<%=data.oid%>" title="버전정보 (I)"> <%
 	if (CommonUtils.isAdmin()) {
 %> <input type="button" class="redBtn" value="삭제 (D)" id="deleteBtn" title="삭제 (D)"> <%
 	}
 %> <input type="button" value="닫기 (C)" title="닫기 (C)" id="closeBtn" class="redBtn"></td>
		</tr>
	</table> <%
 	} else {
 %>
	<div class="AXTabs">
		<div class="AXTabsTray">
			<a class="AXTab partView" title="<%=titleText%>정보"><%=titleText%>정보</a> <a class="AXTab on viewToEChange" title="설계변경 정보">설계변경 정보</a>
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
			<th>도면 명</th>
			<td colspan="3"><%=data.name%></td>
			<td class="center pview_td" rowspan="7">
				<%
					String viewOid = data.oid;
					if (data.epm != null) {
						viewOid = data.epm.getPersistInfo().getObjectIdentifier().getStringValue();
					}
				%> <jsp:include page="/jsp/common/thumnail.jsp">
					<jsp:param value="<%=viewOid%>" name="oid" />
					<jsp:param value="<%=data.creoView%>" name="url" />
				</jsp:include></td>
		</tr>
		<tr>
			<th>도면 번호</th>
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
			<th>도면</th>
			<%
				if (data.epm != null) {
					String eoid = data.epm.getPersistInfo().getObjectIdentifier().getStringValue();
			%>
			<td class="viewPartInfo" data-oid="<%=eoid%>"><%=data.epm.getNumber()%> <%
 	} else {
 %>
			<td><font color="red">도면이 없습니다.</font></td>
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
			<th>도면파일</th>
			<td colspan="3">
				<%
					if (data.epm != null) {
						String[] primary = ContentUtils.getPrimary(data.epm);
						String icon = ContentUtils.getOpenIcon(data.epm);
				%> <a href="<%=primary[5]%>"> <img class="pos2" src="<%=icon%>"> <%=primary[2]%>&nbsp;[<%=primary[3]%>]
			</a> <%
 	} else {
 %> <font color="red">도면 파일이 없습니다.</font> <%
 	}
 %>
			</td>
		</tr>
	</table>
	
	<div class="header_title margin_top10">
		<i class="axi axi-subtitles"></i><span>관련 E-BOM LIST</span>
	</div>
	<table class="in_list_table left-border">
		<tr>
			<th>E-BOM LIST 번호</th>
			<th>SUBJECT</th>
			<th>상태</th>
			<th>작성자</th>
			<th>작성일</th>
		</tr>
		<%
			for(EBOM ebom : data.refEBOM) {
				String eoid = ebom.getPersistInfo().getObjectIdentifier().getStringValue();
		%>
		<tr>
			<td class="infoEBOM" data-oid="<%=eoid%>"><%=ebom.getNumber() %></td>
			<td class="infoEBOM" data-oid="<%=eoid%>"><%=ebom.getSubject() %></td>
			<td><%=ebom.getState() %></td>
			<td><%=ebom.getOwnership().getOwner().getFullName() %></td>
			<td><%=ebom.getCreateTimestamp().toString().substring(0, 16) %></td>
		</tr>
		<%
			}
			if(data.refEBOM.size() == 0) {
		%>
		<tr>
			<td class="nodata" colspan="5">관련 ECN이 없습니다.</td>
		</tr>
		<%
			}
		%>
	</table>

	<div class="header_title margin_top10">
		<i class="axi axi-subtitles"></i><span>관련 STN</span>
	</div>

	<table class="in_list_table left-border">
		<tr>
			<th>STN 번호</th>
			<th>STN 제목</th>
			<th>상태</th>
			<th>작성자</th>
			<th>작성일</th>
		</tr>
		<tr>
			<td class="nodata" colspan="5">관련 STN이 없습니다.</td>
		</tr>
	</table>

	<div class="header_title margin_top10">
		<i class="axi axi-subtitles"></i><span>관련 ECN</span>
	</div>
	<table class="in_list_table left-border">
		<tr>
			<th>ECN 번호</th>
			<th>ECN 제목</th>
			<th>상태</th>
			<th>작성자</th>
			<th>작성일</th>
		</tr>
		<tr>
			<td class="nodata" colspan="5">관련 ECN이 없습니다.</td>
		</tr>
	</table>

	<table class="btn_table">
		<tr>
			<td class="center">
				<%
					if (!isPopup) {
				%> <input data-oid="<%=data.oid%>" type="button" class="" value="상위<%=titleText%>" id="upPartBtn" title="상위<%=titleText%>"> <input data-oid="<%=data.oid%>" type="button" class="blueBtn" value="하위<%=titleText%>"
				id="downPartBtn" title="하위<%=titleText%>"> <input data-oid="<%=data.oid%>" type="button" class="redBtn" value="제품" id="endPartBtn" title="제품"> <input type="button" class="" value="버전정보 (I)"
				id="historyBtn" data-oid="<%=data.oid%>" title="버전정보 (I)"> <input type="button" class="blueBtn" value="뒤로 (B)" id="backBtn" title="뒤로 (B)">
			</td>
			<%
				}
			%>
		</tr>
	</table></td>