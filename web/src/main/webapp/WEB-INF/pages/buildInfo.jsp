<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%--@elvariable id="build" type="load_test_service.api.model.TestBuild"--%>
<%--@elvariable id="artifacts" type="java.util.Collection<java.lang.String>"--%>

<div class="frame-title" style="display: block; float: left">Dependencies in build # ${build.ID.buildID}</div>
<div id="dep-list" style="height: 40%; display: block; float: left; width: 95%; padding: 1%">
    <table style="display: inline-block; width: 100%; max-height: 100%">
        <c:forEach items="${build.dependencyList}" var="dependency">
            <tr style="padding: 10px 0 5px 0; border-bottom: 1px solid #cccccc">
                <td style="width: 55%">${dependency.name}</td>
                <td style="width: 10%">${dependency.buildNumber}</td>
                <td style="width: 10%">
                    <c:choose>
                        <c:when test="${build.status == 'SUCCESS'}">
                            <span style="color: darkgreen">
                        </c:when>
                        <c:otherwise>
                            <span style="color: darkred">
                        </c:otherwise>
                    </c:choose>
                    ${build.status}</span>
                </td>
                <td style="width: 25%">
                    ${build.finishFormattedDate}
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
</div>





