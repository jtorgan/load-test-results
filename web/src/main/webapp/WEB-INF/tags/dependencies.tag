<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="template" tagdir="/WEB-INF/tags" %>

<%@attribute name="dependencyList" type="java.util.Collection<load_test_service.api.model.DependencyBuild>" required="true" %>


<table style="display: inline-block; width: 100%; max-height: 100%">
    <c:forEach items="${dependencyList}" var="dependency">
        <tr style="padding: 10px 0 5px 0; border-bottom: 1px solid #cccccc">
            <td style="width: 55%">${dependency.name}</td>
            <td style="width: 10%">${dependency.buildNumber}</td>
            <td style="width: 10%"><template:buildStatus status="${dependency.status}"/></td>
            <td style="width: 25%">
                    ${dependency.finishFormattedDate}
            </td>
        </tr>
        <c:forEach items="${dependency.changes}" var="change">
            <tr style="color: #777777">
                <td>${change.author}</td>
                <td colspan="3">${change.revision}</td>
            </tr>
        </c:forEach>
        <tr><td colspan="4" style="height: 15px"></td></tr>
    </c:forEach>
</table>