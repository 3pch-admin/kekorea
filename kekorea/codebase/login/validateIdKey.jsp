<%@page contentType="text/plain" pageEncoding="UTF-8"

%><%@page import="wt.idp.UserIdentityKeyProvider"

%><%--
  Returns the user name associated with the given identity id, removing the
  association in the process.

  Anonymous access to this URL must be allowed.

  See wtcore/jsp/genIdKey.jsp for the other side of this mechanism.
--%><%
  // process request parameters
  final String  key = request.getParameter( "key" );

  // find user for specified key
  final String  user = UserIdentityKeyProvider.getUser( key );

  // return user as UTF-8 plain text response body
  // could return an error code when the key is invalid, but instead we just return an empty result
  if ( user != null )
    out.print( user );
  return;
%>