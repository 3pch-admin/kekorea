<%@page session="false"%><%@page contentType="text/html" pageEncoding="UTF-8"%><%@page import="wt.session.SessionUsers"%><%@page import="wt.util.WTProperties"%>
<%
	final String remoteUser = request.getRemoteUser();

	// invalidate HttpSession
	final HttpSession httpSession = request.getSession(false);
	if (httpSession != null)
		httpSession.invalidate();

	// signal logout to SessionUsers instrumentation
	SessionUsers.logout(remoteUser);

	/*
	 // try to call new Servlet 3.0 API for logout
	 try
	 {
	   request.logout();
	 }
	 catch ( ServletException e )
	 {
	   // ignore
	 }
	*/

	// redirect to web app entry page
	final WTProperties props = WTProperties.getLocalProperties();
	String redirectUrl = props.getProperty("wt.logout.url");
	if (redirectUrl == null) {
		redirectUrl = props.getProperty("wt.server.codebase");
		if (!redirectUrl.endsWith("/"))
			redirectUrl += "/";
	}
	redirectUrl = "index.html";
	response.sendRedirect(redirectUrl);
%>