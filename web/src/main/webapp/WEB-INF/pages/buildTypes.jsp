<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="template" tagdir="/WEB-INF/tags" %>

<%--@elvariable id="projectID" type="java.lang.String"--%>
<%--@elvariable id="projectName" type="java.lang.String"--%>
<%--@elvariable id="bts" type="java.util.Map<java.lang.String,java.lang.String>"--%>

<c:choose>
    <c:when test="${empty bts}">
        <i>no build configurations</i>
    </c:when>

    <c:otherwise>
        <div class="inner-padding">
            <c:forEach items="${bts}" var="bt">
                <div class="bt-item" onclick="fillAddForm('${projectID}', '${projectName}', '${bt.key}', '${bt.value}');">
                    <span class="bt-item-name">${bt.value}</span>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>
