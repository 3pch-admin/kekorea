<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.part.beans.BomTreeData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String oid = (String) request.getParameter("oid");
	// popup check
	String popup = (String) request.getParameter("popup");
	boolean isPopup = Boolean.parseBoolean(popup);
%>
<td valign="top" class="bomtd">
<script type="text/javascript">
	$(document).ready(function() {
		$("#closeBomBtn").click(function() {
			self.close();
		})

		boms.bomStart("bomView", "<%=oid %>");
		
		var h = $(document).height();
		$("ul.fancytree-container").css("height", h - 80);
	})

	$(window).resize(function() {
		var h = $(document).height();
		$("ul.fancytree-container").css("height", h - 80);
	})
</script> 
	<input type="hidden" name="oid" value="<%=oid %>">
<!-- 	<input type="text" name="items" id="items"> -->
<%
 	if (isPopup) {
 %>
	<div class="right">
		<input type="button" value="닫기" id="closeBomBtn" title="닫기">
	</div> 
<%
 	}
 %>
	<div class="clear5"></div>
	<table class="container_table">
		<colgroup>
			<col width="60%">
			<col width="14px;">
			<col width="40%">
		</colgroup>
		<tr>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>
				<div class="AXTabs pos2">
					<div class="AXTabsTray">
						<a class="AXTab on">정보</a>
						<a class="AXTab">시각화</a>
						<a class="AXTab">???</a>
					</div>
				</div>
			</td>
		</tr>
		<tr>
			<td>
				<table id="bomView" class="bom_table">
					<colgroup>
						<col width="30px">
						<col width="50px">
						<col width="*">
						<col width="40px">
						<col width="80px">
						<col width="100px">
					</colgroup>
					<thead>
						<tr>
							<th></th>
							<th>&nbsp;</th>
							<th></th>
							<th></th>
							<th>수량</th>
							<th>버전</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td></td>
							<td></td>
							<td></td>
							<td class="center"></td>
							<td class="center"></td>
							<td class="center"></td>
						</tr>
					</tbody>
				</table>
			</td>
			<td>&nbsp;</td>
			<td valign="top">
				<table id="infoBom" class="infoBom">
					<tr>
						<th>&nbsp;</th>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</td>