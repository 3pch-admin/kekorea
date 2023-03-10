<%@page import="e3ps.org.Department"%>
<%@page import="e3ps.org.service.OrgHelper"%>
<%@page import="e3ps.org.column.UserColumnData"%>
<%@page import="e3ps.org.User"%>
<%@page import="e3ps.document.column.DocumentColumnData"%>
<%@page import="e3ps.common.ModuleKeys"%>
<%@page import="e3ps.common.util.ColumnUtils"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.document.service.DocumentHelper"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@page import="wt.org.WTPrincipalReference"%>
<%@page import="wt.ownership.OwnershipHelper"%>
<%@page import="wt.ownership.Ownership"%>
<%@page import="wt.org.WTPrincipal"%>
<%@page import="wt.org.OrganizationServicesHelper"%>
<%@page import="wt.org.LdapServicesServerHelper"%>
<%@page import="com.infoengine.au.DirectoryService"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTUser"%>
<%@page import="wt.util.WTProperties"%>
<%@page import="wt.federation.PrincipalManager.DirContext"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="e3ps.document.beans.DocumentViewData"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="java.util.HashMap"%>
<%@page import="e3ps.epm.dto.EpmViewData"%>
<%@page import="wt.epm.EPMDocument"%>
<%@page import="wt.util.WTAttributeNameIfc"%>
<%@page import="wt.fc.PagingQueryResult"%>
<%@page import="e3ps.part.beans.PartViewData"%>
<%@page import="wt.part.WTPart"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="e3ps.common.util.PageQueryUtils"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="e3ps.common.util.HtmlUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String module = ModuleKeys.list_user.name();
	String root = OrgHelper.DEPARTMENT_ROOT;
	PageQueryUtils pager = (PageQueryUtils) request.getAttribute("pager");
	Department department = (Department) request.getAttribute("department");
	PagingQueryResult result = pager.find();
	HashMap<String, Object> param = (HashMap<String, Object>) pager.getParam();
	HtmlUtils html = new HtmlUtils(param);
	String id = (String) param.get("id");
	String name = (String) param.get("name");
	String resign = (String) param.get("resign");
	if (!StringUtils.isNull(department)) {
		root = department.getName();
	}
	String deptOid = department.getPersistInfo().getObjectIdentifier().getStringValue();

	boolean isRoot = department.getCode().equals("ROOT");
%>
<script type="text/javascript">
	$(document).ready(function() {
		var url = "/Windchill/plm/org/viewUser";
		$(document).cellClick("name", url);
		$(document).cellClick("id", url);

		$(".list_table").tableHeadFixer();

		$("#searchBtn").click(function() {
			$(document).list();
		})

		$("#resignBtn").click(function() {

			var check = $(document).isCheck();
			var dialogs = $(document).setOpen();
			if (check == false) {
				dialogs.alert({
					theme : "alert",
					title : "????????? ?????????",
					msg : "?????? ????????? ???????????? ???????????????."
				})
				return false;
			}

			dialogs.confirm({
				theme : "info",
				width : 330,
				title : "?????? ??????",
				msg : "????????? ???????????? ???????????? ???????????????????",
			}, function() {
				if (this.key == "ok") {
					var url = "/Windchill/plm/org/setResignAction";
					var params = $(document).getListFormData();
					$(document).ajaxCallServer(url, params, function(data) {
						dialogs.alert({
							width : 350,
							theme : "alert",
							title : "??????",
							msg : data.msg
						}, function() {
							if (this.key == "ok" || this.state == "close") {
								//document.location.href = data.url;
								document.location.reload();
							}
						})
					}, true);
				}
			})
		})

		$("#resign").bindSelect();
		$("input").checks();

		$("#setDuty").click(function() {
			var url = "/Windchill/plm/admin/setDuty";
			var opt = "scrollbars=yes, resizable=yes";
			$(document).openURLViewOpt(url, 600, 500, opt);
		})

		$("#setDept").click(function() {
			var oid = $(this).data("oid");
			var url = "/Windchill/plm/admin/setDept?oid=" + oid;
			var opt = "scrollbars=yes, resizable=yes";
			$(document).openURLViewOpt(url, 600, 500, opt);
		})
	})
</script>
<td valign="top"><input type="hidden" id="module" name="module" value="<%=module%>"> <%
 	out.println(pager.getScript());
 %> <!-- search table... -->
	<div class="header_title">
		<i class="axi axi-subtitles"></i><span class="">????????? ??????</span>
		<jsp:include page="/jsp/common/search_info.jsp"></jsp:include>
	</div>

	<table class="container_table">
		<tr>
			<jsp:include page="/jsp/common/layouts/include_dept.jsp" />
			<td id="container_td">
				<table class="search_table">
					<colgroup>
						<!-- 						<col width="160"> -->
						<!-- 						<col width="400"> -->
						<!-- 						<col width="160"> -->
						<!-- 						<col width="400"> -->
					</colgroup>
					<tr>
						<th>??????</th>
						<td><input type="hidden" name="deptOid" id="deptOid" value="<%=deptOid%>"> <span id="deptName"><%=root%></span></td>
						<th>????????????</th>
						<td><select name="resign" id="resign" class="AXSelect wid100">
								<option value="">??????</option>
								<option <%if ("false".equals(resign)) {%> selected="selected" <%}%> value="false">?????????</option>
								<option <%if ("true".equals(resign)) {%> selected="selected" <%}%> value="true">??????</option>
						</select></td>
					</tr>
					<tr>
						<th>?????????</th>
						<td><input type="text" name="id" class="AXInput wid200" value="<%=StringUtils.removeNull(id)%>"></td>
						<th>??????</th>
						<td><input type="text" name="name" class="AXInput wid200" value="<%=StringUtils.removeNull(name)%>"></td>
					</tr>
				</table>

				<table class="btn_table">
					<tr>
						<td class="right">
							<%
								if (isRoot) {
							%> <input type="button" value="????????????" class="greeBtn" id="setDuty" title="????????????"> <%
 	} else {
 %> <input type="button" value="????????????" data-oid="<%=deptOid%>" class="greeBtn" id="setDept" title="????????????"> <%
 	}
 %> <input type="button" value="????????????" class="redBtn" id="resignBtn" title="????????????"> <input type="button" value="??????" class="blueBtn" id="searchBtn" title="??????"> <input
							type="button" value="????????? (I)" id="init_table" title="????????? (I)">
						</td>
					</tr>
				</table>

				<table class="sub_table">
					<tr>
						<td>
							<div class="view_layer">
								<ul>
									<li id="list_view" class="active_view" title="????????? ???">????????? ???</li>
									<li class="hidden" id="img_view" title="????????? ???">????????? ???</li>
									<li class="hidden"><span id="thumbnail" title="????????? ???????????? ???????????????.">????????? ???????????? ??????????????? </span></li>
								</ul>
							</div>

							<div class="paging_layer">
								<span class="count_span"><span id="count_text"></span></span>
								<%=pager.getPagingBox(false)%>
							</div>
						</td>
					</tr>
				</table>

				<div class="div_scroll">
					<table class="list_table resiable">
						<%
							boolean isBox = true;
							boolean isMulti = false;
							if (CommonUtils.isAdmin()) {
								isMulti = true;
								isBox = true;
							}
							String[] headers = ColumnUtils.getColumnHeaders(module);
							String[] keys = ColumnUtils.getColumnKeys(module);
							String[] cols = ColumnUtils.getColumnCols(module, headers);
							String[] styles = ColumnUtils.getColumnStyles(module, headers);
							out.println(html.setHeader(isBox, headers, keys, cols, styles, pager.getSort(), isMulti));
						%>
						<tbody>
							<%
								int idx = 0;
								while (result.hasMoreElements()) {
									Object[] obj = (Object[]) result.nextElement();
									User user = (User) obj[0];
									UserColumnData data = new UserColumnData(user);
							%>
							<tr class="list_tr" data-oid="<%=data.oid%>" data-key="quickmenu<%=idx%>">
								<%
									if (isBox) {
								%>
								<td><input name="oid" id="oid" value="<%=data.oid%>" type="checkbox"> <%
 	out.println(html.setQuick(idx, data.oid, module));
 %></td>
								<%
									}
										for (int i = 0; i < styles.length; i++) {
											if (i == 1) {
								%>
								<td data-column="<%=keys[i]%>_column" <%=styles[i]%> class="left indent5"><img class="pos2" src="<%=data.iconPath%>">&nbsp; <%=data.getValue(keys[i])%> <%
 	out.println(html.setQuick(idx, data.oid, module));
 %></td>
								<%
									} else {
								%>
								<td data-column="<%=keys[i]%>_column" <%=styles[i]%>><%=data.getValue(keys[i])%></td>
								<%
									}
										}
								%>
							</tr>
							<%
								idx++;
								}
								if (result.size() == 0) {
									int colspan = keys.length;
									if (isBox) {
										colspan = colspan + 1;
									}
							%>
							<tr>
								<td class="nodata_icon" colspan="<%=colspan%>"><a class="axi axi-info-outline"></a> <span> ?????? ????????? ????????????.</span></td>
							</tr>
							<%
								}
							%>
						</tbody>
					</table>
				</div>
			</td>
		</tr>
	</table> <%
 	out.println(pager.paging());
 	out.println(html.setContextmenu(module));
 	out.println(html.setQuickScript(pager.getPsize()));
 %></td>