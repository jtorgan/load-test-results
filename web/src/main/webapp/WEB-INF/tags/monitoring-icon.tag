<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@attribute name="buildTypeID" type="java.lang.String" required="true" %>
<%@attribute name="isMonitored" type="java.lang.Boolean" required="true" %>

<c:choose>
    <c:when test="${isMonitored}">
        <div class="stop-monitor" onclick="changeMonitoring(this, '${buildTypeID}');"></div>
    </c:when>
    <c:otherwise>
        <div class="start-monitor" onclick="changeMonitoring(this, '${buildTypeID}');"></div>
    </c:otherwise>
</c:choose>
