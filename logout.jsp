<%@ page import="javax.servlet.http.*" %>
<%
    HttpSession session1 = request.getSession(false);
    if (session1 != null) {
        session1.invalidate();
    }
    response.sendRedirect("login.jsp");
%>
