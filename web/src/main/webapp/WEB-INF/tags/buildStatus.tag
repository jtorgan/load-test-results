<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@attribute name="status" type="java.lang.String" required="true" %>
<c:choose>
    <c:when test="${status == 'SUCCESS'}">
        <span style="color: darkgreen">
    </c:when>
    <c:otherwise>
        <span style="color: darkred">
    </c:otherwise>
</c:choose>
${status}</span>
