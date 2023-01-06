<%@page session="false"
%><%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"
%><c:set var="loginFailed" scope="request" value="true"
/><jsp:forward page="login.jsp"/>