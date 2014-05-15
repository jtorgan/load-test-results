<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@attribute name="threadGroup" type="java.lang.String" required="true" %>
<%@attribute name="name" type="java.lang.String" required="true" %>


<c:if test="${not empty threadGroup}">
    ${threadGroup} -
</c:if> ${name}