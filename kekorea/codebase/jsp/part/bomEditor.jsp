<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="wt.part.WTPart"%>
<%@taglib uri="http://www.ptc.com/windchill/taglib/util" prefix="util"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String oid = (String) request.getAttribute("oid");
	WTPart part = (WTPart) request.getAttribute("part");
	String codebase = (String) request.getAttribute("codebase");
	String version = "";
	String number = "";

	if (StringUtils.isNull(oid)) {
		oid = "";
	}
	if (part != null) {
		version = part.getVersionIdentifier().getSeries().getValue();
		number = part.getNumber();
	}
%>
<td valign="top"><script type="text/javascript">
	$(document).ready(function() {
		resizeTable();

		$(window).resize(function() {
			resizeTable();
		});

		function resizeTable() {
			document.getElementById("BomEditor").style.height = ($(window).height() - 100) + 'px';
		}
	})

	function getFormValue(name) {
		var f = document.forms[0];
		if (f != null && f[name] != null) {
			return f[name].value;
		}
		return "";
	}
</script>
	<table class="btn_table top_margin0">
		<tr>
			<td class="left"><input type="hidden" name="bomReqEntriesJson" id="bomReqEntriesJson" value="[]">
				<div class="header_title pos5">
					<i class="axi axi-subtitles"></i><span>BOM 에디터</span>
				</div></td>
	</table>
	<table id="bom_table">
		<tr>
			<th><util:plugin code="e3ps/part/editor/BOMEditorApplet.class" codebase="<%=codebase%>" archive="wt/security/security.jar" width="100%" height="500px" id="BomEditor" align="absmiddle">
					<util:params>
						<util:param name="cabinets" value="wt/security/security.cab" />
						<util:param name="cache_option" value="Plugin" />
						<util:param name="cache_archive" value="wtWork.jar,lib/json.jar" />
<%-- 						<util:param name="cache_archive" value="wtWork.jar,lib/json.jar,lib/plugin.jar" /> --%>
						<util:param name="MAYSCRIPT" value="true" />
						<util:param name="oid" value="<%=oid%>" />
						<util:param name="reqType" value="POST" />
						<util:param name="topNumber" value="<%=number%>" />
						<util:param name="topVersion" value="<%=version%>" />
					</util:params>
				</util:plugin></th>
		</tr>
	</table></td>