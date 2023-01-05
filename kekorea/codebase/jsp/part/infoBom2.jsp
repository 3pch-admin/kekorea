<%@page import="e3ps.part.column.BomColumnData"%>
<%@page import="e3ps.common.util.ColumnUtils"%>
<%@page import="e3ps.common.ModuleKeys"%>
<%@page import="java.util.HashMap"%>
<%@page import="e3ps.common.util.HtmlUtils"%>
<%@page import="e3ps.part.beans.PartViewData"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String module = ModuleKeys.list_bom.name();
	ArrayList<BomColumnData> list = (ArrayList<BomColumnData>) request.getAttribute("list");
	String oid = (String) request.getAttribute("oid");
	HtmlUtils html = new HtmlUtils();
	String popup = (String) request.getParameter("popup");
	boolean isPopup = Boolean.parseBoolean(popup);
%>
<td valign="top"><input type="hidden" id="module" name="module" value="<%=module%>"> <input type="hidden" id="oid" value="<%=oid%>"> <script type="text/javascript">
	$(document).ready(function() {

		$(".list_table").tableHeadFixer();

		$("input").checks();
	})
</script> <%
 	if (isPopup) {
 %>
	<table class="btn_table top_margin0">
		<tr>
			<td class="left">
				<div class="header_title pos5">
					<i class="axi axi-subtitles"></i><span>BOM</span>
				</div>
			</td>
			<td class="right"><input type="button" class="" value="닫기 (C)" id="closeBtn" title="닫기 (C)"></td>
		</tr>
	</table> <%
 	}
 %>
	<div class="div_scroll_bom">
		<table class="list_table resiable">
			<%
				String[] headers = ColumnUtils.getColumnHeaders(module);
				String[] keys = ColumnUtils.getColumnKeys(module);
				String[] cols = ColumnUtils.getColumnCols(module, headers);
				String[] styles = ColumnUtils.getColumnStyles(module, headers);
				out.println(html.setHeader(false, headers, keys, cols, styles, "false"));
			%>
			<tbody>
				<%
					int idx = 0;
					for (BomColumnData data : list) {
						idx++;
				%>
				<tr data-oid="<%=data.oid%>" data-key="quickmenu<%=idx%>">
					<%
						for (int i = 0; i < styles.length; i++) {
								if (i == 2) {
					%>
					<td data-column="<%=keys[i]%>_column" <%=styles[i]%> class="left indent5">
						<%
							for (int k = 0; k < data.level; k++) {
											out.println("&nbsp;");
										}
						%> <img class="pos3" src="<%=data.iconPath%>">&nbsp;<%=data.getValue(keys[i])%></td>
					<%
						} else {
					%>
					<td data-column="<%=keys[i]%>_column" <%=styles[i]%> class="center"><%=data.getValue(keys[i])%></td>
					<%
						}
							}
					%>
				</tr>
				<%
					}
				%>
			</tbody>
		</table>
	</div> <%
 	out.println(html.setContextmenu(module));
 %></td>