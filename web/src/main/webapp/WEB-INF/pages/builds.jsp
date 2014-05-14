<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@taglib prefix="base" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>

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
<div class="header">
    <a id="back" href='${baseURL}'><= Back</a>

    <div style="float: right">
        <form action="/statistic/showStat" method="get" target="_blank">
            <input type="hidden" name="buildTypeID" value="${buildTypeID}">
            <a href="javascript:;" onclick="parentNode.submit();" title="Click to show calculated statistic">Show Performance Statistic</a>
        </form>
    </div>

</div>

    <input type="hidden" id="btID" value="${buildTypeID}">
    <div class="frame" style="border-right: 1px solid #86a4c3; max-height: 90%; height: 90%; padding-bottom: 5px; width: 60%">
        <div class="frame-title">Builds (<span id="count">${count}</span>) </div>
        <div class="bt-container">
            <table style="width: 100%; border-spacing: 0; max-height: 100%" id="builds">
               <tr>
                   <th></th>
                   <th style=" text-align: left !important;">ID</th>
                   <th>Number</th>
                   <th>Status</th>
                   <th>Date</th>
                   <th style="padding: 0 10px; text-align: left !important;">Statistic</th>
                   <th style="padding: 0 10px; text-align: left !important;">Artifacts</th>
               </tr>
                <c:set var="i" value="1"/>
                <c:forEach items="${builds}" var="build">
                    <tr id="${build.ID.buildID}" class="build">
                        <td><div class="remove" onclick="removeBuild(${i}, this, '${build.ID.buildID}', '${build.ID.buildTypeID}')"></div></td>


                        <td class="clickable">${build.ID.buildID}</td>
                        <td class="clickable" style="text-align: center">${build.buildNumber}</td>
                        <td class="clickable" style="text-align: center"><base:buildStatus status="${build.status}"/></td>
                        <td class="clickable" style="text-align: center">${build.finishFormattedDate}</td>

                        <td class="statistic" style="cursor: default">
                            <c:forEach items="${build.artifacts}" var="artifact">
                                <c:if test="${artifact.value == true}">
                                    <forms:showStatForm buildID="${build.ID.buildID}" buildTypeID="${build.ID.buildTypeID}" artifact="${artifact.key}"/>
                                </c:if>
                            </c:forEach>
                        </td>

                        <td style="cursor: default">
                            <div class="artifacts">
                                <forms:loadArtifactForms  buildID="${build.ID.buildID}" buildTypeID="${build.ID.buildTypeID}" artifacts="${build.artifacts}"/>
                            </div>
                        </td>
                    </tr>
                    <c:set var="i" value="${i+1}"/>
                </c:forEach>
            </table>
        </div>
    </div>


    <div class="frame" style="max-height: 90%; height: 90%; padding-bottom: 5px; overflow: visible; width: 35%">
        <div id="buildInfo" class="frame-block"></div>
        <div id="statForm" class="frame-block"></div>
    </div>
</body>
</html>
