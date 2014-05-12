<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="template" tagdir="/WEB-INF/tags" %>


<%--@elvariable id="buildTypeID" type="java.lang.String"--%>
<%--@elvariable id="count" type="java.lang.Integer"--%>
<%--@elvariable id="builds" type="java.util.List<load_test_service.api.model.TestBuild>"--%>

<c:set var="req" value="${pageContext.request}" />
<c:set var="baseURL" value="${req.scheme}://${req.serverName}:${req.serverPort}${req.contextPath}" />

<c:set var="buildTypeID" value="${buildTypeID}" />

<html>
<head>
    <title>Builds in ${buildTypeID}</title>

    <script src="http://code.jquery.com/jquery-2.1.1.min.js"></script>
    <script src="/resources/js/builds.js" language="JavaScript"></script>

    <link rel="stylesheet" href="/resources/css/common.css">
    <link rel="stylesheet" href="/resources/css/builds.css">
</head>
<body>
    <a id="back" href='${baseURL}'>< Back</a>

    <input type="hidden" id="btID" value="${buildTypeID}">
    <div class="frame" style="border-right: 1px solid #86a4c3; max-height: 90%; height: 90%; padding-bottom: 5px">
        <div class="frame-title">Builds (<span id="count">${count}</span>) </div>
        <div class="bt-container">
            <table style="width: 100%; border-spacing: 0; max-height: 100%" id="builds">
               <tr style="text-align: left">
                   <th>id</th>
                   <th>number</th>
                   <th>status</th>
                   <th>date</th>
                   <th></th>
               </tr>
                <c:set var="i" value="1"/>
                <c:forEach items="${builds}" var="build">
                    <tr id="${build.ID.buildID}" class="build">
                        <td class="clickable">${build.ID.buildID}</td>
                        <td class="clickable">${build.buildNumber}</td>
                        <td class="clickable"><template:buildStatus status="${build.status}"/></td>
                        <td class="clickable">${build.finishFormattedDate}</td>
                        <td><div class="remove" onclick="removeBuild(${i}, this, '${build.ID.buildID}', '${build.ID.buildTypeID}')"></div></td>
                    </tr>
                    <c:set var="i" value="${i+1}"/>
                </c:forEach>
            </table>
        </div>
    </div>


    <div class="frame" style="max-height: 90%; height: 90%; padding-bottom: 5px; overflow: visible">
        <div id="buildInfo" class="frame-block"></div>

        <div id="statForm" class="frame-block"></div>

        <div id="artWithStat" class="frame-block"></div>
    </div>
</body>
</html>
