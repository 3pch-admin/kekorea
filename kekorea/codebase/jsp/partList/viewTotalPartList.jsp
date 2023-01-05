<%@page import="e3ps.partlist.beans.PartListDataViewData"%>
<%@page import="e3ps.partlist.PartListData"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.partlist.beans.PartListMasterViewData"%>
<%@page import="e3ps.partlist.PartListMaster"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	// popup check
	String popup = (String) request.getParameter("popup");
	boolean isPopup = Boolean.parseBoolean(popup);

	// data
	ArrayList<PartListMaster> data = (ArrayList<PartListMaster>) request.getAttribute("data");
	// isAdmin
	boolean isAdmin = CommonUtils.isAdmin();
%>

<td valign="top"><script type="text/javascript">
	$(document).ready(function() {
	})
</script> <%-- 	<input type="hidden" name="oid" id="oid" value="<%=data.oid %>"> --%> <input type="hidden" name="popup" id="popup" value="<%=isPopup%>">

	<table class="btn_table">
		<tr>
			<td>
				<div class="header_title">
					<i class="axi axi-subtitles"></i><span>수배표 정보</span>

				</div>
			</td>
			<td>
				<div class="right">
					<input type="button" value="닫기" id="closeDocBtn" title="닫기" class="redBtn">
				</div>
			</td>
		</tr>
	</table>
	<table id="tblBackground">
		<tr>
			<td>
				<div>
					<table class="list_table fix_table partlist_table">
						<!-- 23개.. -->
						<colgroup>
							<col width="40">
							<col width="50">
							<col width="130">
							<col width="100">
							<col width="240">
							<col width="300">
							<col width="100">
							<col width="100">
							<col width="40">
							<col width="40">
							<col width="100">
							<col width="50">
							<col width="100">
							<col width="80">
							<col width="50">
							<col width="100">
							<col width="100">
							<col width="150">
						</colgroup>
						<thead>
							<tr>
								<th>NO</th>
								<th>LOT_NO</th>
								<th>UNIT_NAME</th>
								<th>부품번호</th>
								<th>부품명</th>
								<th>규격</th>
								<th>MAKER</th>
								<th>거래처</th>
								<th>수량</th>
								<th>단위</th>
								<th>단가</th>
								<th>화폐</th>
								<th>원화금액</th>
								<th>수배일자</th>
								<th>환율</th>
								<th>참고도면</th>
								<th>조달구분</th>
								<th>비고</th>
							</tr>
						</thead>
						<%
							int count = 1;
							double total = 0D;
							for (PartListMaster master : data) {
								PartListMasterViewData dd = new PartListMasterViewData(master);
// 								out.println(master.getPersistInfo().getObjectIdentifier().getStringValue());
// 								out.println(master.getName());
								ArrayList<PartListData> list = dd.list;
								total += master.getTotalPrice();
								for (PartListData datas : list) {
									PartListDataViewData vdata = new PartListDataViewData(datas);
						%>
						<tr>
							<td title="<%=count%>"><%=count++%></td>
							<td title="<%=vdata.lotNo%>"><%=vdata.lotNo%></td>
							<td title="<%=vdata.unitName%>"><%=vdata.unitName%></td>
							<td class="y_code" data-number="<%=vdata.partNo%>" title="<%=vdata.partNo%>"><%=vdata.partNo%></td>
							<td title="<%=vdata.partName%>"><%=vdata.partName%></td>
							<td class="left indent10" title="<%=vdata.standard%>"><%=vdata.standard%></td>
							<td title="<%=vdata.maker%>"><%=vdata.maker%></td>
							<td title="<%=vdata.customer %>"><%=vdata.customer%></td>
							<td  title="<%=vdata.quantity%>"><%=vdata.quantity%></td>
							<td  title="<%=vdata.unit%>"><%=vdata.unit%></td>
							<td class="right"  title="<%=vdata.price%>"><%=vdata.price%>&nbsp;</td>
							<td  title="<%=vdata.currency%>"><%=vdata.currency%></td>
							<td class="right" title="<%=String.format("%,f", vdata.won).substring(0, String.format("%,f", vdata.won).lastIndexOf("."))%>"><%=String.format("%,f", vdata.won).substring(0, String.format("%,f", vdata.won).lastIndexOf("."))%>&nbsp;</td>
							<td title="<%=vdata.partListDate%>"><%=vdata.partListDate%></td>
							<td title="<%=vdata.exchangeRate%>"><%=vdata.exchangeRate%></td>
							<td title="<%=vdata.referDrawing%>"><%=vdata.referDrawing%></td>
							<td title="<%=vdata.classification%>"><%=vdata.classification%></td>
							<td title="<%=vdata.note%>"><%=vdata.note%></td>
						</tr>
						<%
							}
							}
						%>
						<tr>
							<td  colspan="12" class="right" style="background-color: #C4FFBE; text-align: left;" title="<%
									String s = String.format("%,f", total);%> <%=s.substring(0, s.lastIndexOf("."))%>" >합계&nbsp;
							<td colspan="6" style="text-align: left;" title="<%=s.substring(0, s.lastIndexOf("."))%>">							
									<strong>&nbsp;&nbsp;
								 <%=s.substring(0, s.lastIndexOf("."))%>
								</strong></td>
<!-- 							<td>&nbsp;</td> -->
<!-- 							<td>&nbsp;</td> -->
<!-- 							<td>&nbsp;</td> -->
<!-- 							<td>&nbsp;</td> -->
						</tr>
					</table>
				</div>
			</td>
		</tr>
	</table></td>