<%@ page import="javax.servlet.http.*, javax.servlet.*" %>
<%
    HttpSession session1 = request.getSession(false);
    if (session1 == null || session1.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>Dashboard</title>
</head>
<body>
    <h2>Welcome, <%= session1.getAttribute("user") %>!</h2>
    <p>You have successfully logged in.</p>
    <a href="logout.jsp">Logout</a>
</body>
</html>
