<%@ page contentType="text/html; charset=UTF-8" import="com.ptc.netmarkets.model.NmException"%>
<%@ page import="com.ptc.netmarkets.util.beans.NmClipboardItem"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Vector"%>
<%@ page import="com.ptc.wvs.common.ui.ClipboardHelper"%>
<%@ page import="java.util.Locale"%>
<%@ page import="wt.session.SessionHelper"%>
<%@ page import="wt.util.WTProperties"%>
<%
	response.setContentType("text/html; charset=UTF-8");
%>
<%@ page import="com.ptc.netmarkets.util.beans.NmClipboardUtility"%>
<%@ include file="/netmarkets/jsp/util/context.jsp"%>
<jsp:useBean id="clipboardBean" class="com.ptc.netmarkets.util.beans.NmClipboardBean" scope="session" />
<jsp:setProperty name="wtcontext" property="request" value="<%=request%>" />
<jsp:useBean id="clip" class="com.ptc.wvs.client.beans.Clipboard" scope="session" />
<jsp:setProperty name="clip" property="*" />

<%
	String itemsStr = request.getParameter("items");
	Locale locale = null; //language
	String hostName = ""; //host

	try {
		locale = SessionHelper.getLocale();
		hostName = WTProperties.getServerProperties().getProperty("wt.rmi.server.hostname");
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	//Clipboard Session remove
	clip.clearClipboard();

	//Clipboard list remove
	clipboardBean.removeAllClipboardItems();

	//Clipboard list add
	clipboardBean.add(NmClipboardUtility.decodeFromString(itemsStr));

	//Clipboard list item
	List<NmClipboardItem> selectedList = clipboardBean.getClipboardItems();

	//Clipboard Session add
	for (NmClipboardItem cbItem : selectedList) {
		String[] data = cbItem.parseForWVS();
		if ((data != null) && (data[0] != null)) {
			Vector<Object> clipArgs = ClipboardHelper.getAddToClipboardArgs(data[0], data[1],
					Boolean.valueOf("WVS".equals(data[2])));
			if ((clipArgs != null) && (clipArgs.size() >= 4)) {
				clip.addToClipboard((String) clipArgs.get(0), (String) clipArgs.get(1),
						(String) clipArgs.get(2), ((Boolean) clipArgs.get(3)).booleanValue(), locale);
			}
		} else {
			System.out.println("data is null");
		}

	}
%>

<script>
	var hostName ="<%=hostName%>";
	var str = "/Windchill/wtcore/jsp/wvs/edrview.jsp?fromclip=1&batchprint=1&url=http://" + hostName + "/Windchill/wtcore/jsp/wvs/edrclipboardview.jsp";
	location.href = str;
</script>