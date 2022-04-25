<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<%--adds view for header--%>
<header>
    <jsp:include page="header.jsp" />
</header>
<body>
<h1><%= "Travel Experts" %>
</h1>
<br/>
<a href="getPackage.jsp">View</a>
<br/>
<a href="createPackage.jsp">Add</a>
<br/>
<a href="updatePackage.jsp">Edit</a>
<br/>
<a href="deletePackage.jsp">Delete</a>
</body>
<footer>
    <jsp:include page="footer.jsp" />
</footer>
</html>