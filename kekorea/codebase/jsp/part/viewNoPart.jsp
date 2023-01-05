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

	String number = (String) request.getAttribute("number");
%>

<td valign="top">
	<!-- script --> <script type="text/javascript">
		$(document).ready(function() {
			var dialogs = $(document).setOpen();
			dialogs.alert({
				theme : "alert",
				title : "경고",
				width : 400,
				msg : "<%=number %>가 시스템에 존재하지 않습니다."
			}, function() {
				if(this.key == "ok" || this.state == "close") {
					self.close();
				}
			})
		})
	</script>
</td>