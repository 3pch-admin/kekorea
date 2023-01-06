<%@page contentType="text/plain" pageEncoding="UTF-8"%>
<%@page import="wt.idp.UserIdentityKeyProvider"%>
<%@page import="wt.idp.KeyEncodingUtility"%>
<%--
  Returns the user name associated with the given identity id, removing the
  association in the process.

  Anonymous access to this URL must be allowed.

  See wtcore/jsp/genIdKey.jsp for the other side of this mechanism.

  TODO - merge with validateIdKey.jsp
--%><%
 // process request parameters
  final String  key = request.getParameter( "key" );
  
  // if that param is set to y, proceed with multi-windchill process
  final String mwa = request.getParameter("mwa");

  // find user for specified key
  final String  user = UserIdentityKeyProvider.getUser( key );

  String encodedAssertion = null;
  if(mwa != null && mwa.equalsIgnoreCase("y")) {
    encodedAssertion = KeyEncodingUtility.createAssertion(user);
  }
  
  // returns username and assertion
  if ( encodedAssertion != null ) {
    out.print( encodedAssertion );
  } else if ( user != null ) {
	  out.print( user );
  }
  return;
%>