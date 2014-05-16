<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<%--@elvariable id="saved_bt" type="java.util.List<load_test_service.api.model.BuildType>"--%>
<%@taglib prefix="base" tagdir="/WEB-INF/tags" %>
<link href="/resources/css/headers.css" rel="stylesheet">

<tr>
    <th style="width: 2%">#</th>
    <th style="text-align: left; width: 44%">Project</th>
    <th style="text-align: left; width: 30%">Build config</th>
    <th style="width: 10%; text-align: left">Monitor</th>
    <th style="width: 6%; text-align: left">Del</th>
    <th style="width: 8%">Builds</th>
</tr>
<c:set value="1" var="num"/>

<c:forEach items="${saved_bt}" var="bt">
    <tr id="${bt.ID}" class="saved-bt-item">
        <script>
            var patterns_${bt.ID} = [
                <c:forEach items="${bt.patterns}" var="pattern" varStatus="loop">
                '${pattern}',
                </c:forEach>
            ];
        </script>

        <td class="edit-bt" onclick="fillUpdateForm('${bt.projectName}', '${bt.ID}', '${bt.name}', patterns_${bt.ID})"> ${num}.</td>
        <td class="edit-bt" onclick="fillUpdateForm('${bt.projectName}', '${bt.ID}', '${bt.name}', patterns_${bt.ID})"> ${bt.projectName} </td>
        <td class="edit-bt" onclick="fillUpdateForm('${bt.projectName}', '${bt.ID}', '${bt.name}', patterns_${bt.ID})"> ${bt.name} </td>

        <c:set value="${num + 1}" var="num"/>

        <td style="text-align: center">
            <base:monitoring-icon buildTypeID="${bt.ID}" isMonitored="${bt.monitored}"/>
        </td>

        <td style="text-align: center">
            <div class="remove" onclick="removeBuildType('${bt.ID}')"></div>
        </td>
        <td style="text-align: center">
            <form:form method="get" action="/buildTypes/builds" cssStyle="margin-bottom: 0 !important;">
                <input type="hidden" name="buildTypeID" value="${bt.ID}">
                <input class="show-saved-bt" type="submit" value="show">
            </form:form>
        </td>
    </tr>
</c:forEach>


