<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@taglib prefix="base" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="headers" tagdir="/WEB-INF/tags/headers" %>
<%@taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>

<%--@elvariable id="count" type="java.lang.Integer"--%>
<%--@elvariable id="buildType" type="load_test_service.api.model.BuildType"--%>
<%--@elvariable id="builds" type="java.util.List<load_test_service.api.model.TestBuild>"--%>

<c:set var="req" value="${pageContext.request}" />
<c:set var="baseURL" value="${req.scheme}://${req.serverName}:${req.serverPort}${req.contextPath}" />

<c:set var="buildTypeID" value="${buildType.ID}" />

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
    <a id="back" style="float: left; width: 5%; text-align: left" href='${baseURL}'><= Back</a>

    <div style="float: left; padding-right: 25px">
       <headers:btHeader buildType="${buildType}"/>
    </div>

    <div style="float: left; width: 10%">
        <forms:showStatForm buildTypeID="${buildTypeID}" linkText="Performance Statistic"/>
    </div>
</div>

    <input type="hidden" id="btID" value="${buildTypeID}">

    <div class="frame" style="border-right: 1px solid #e4e4e4; max-height: 95%; height: 90%; padding-bottom: 5px; width: 60%">
        <div class="frame-title">Builds (<span id="count">${count}</span>) </div>
        <div class="bt-container" style="padding-left: 5px;">
            <table style="width: 100%; border-spacing: 0; max-height: 100%" id="builds">
               <tr>
                   <th></th>
                   <th style=" text-align: left !important;">ID</th>
                   <th>Date</th>
                   <th>Status</th>
                   <th>Number</th>
                   <th style="padding: 0 20px; text-align: left;">SRT+RPS with Artifacts</th>
                   <th style="padding: 0 10px; text-align: center !important; width: 90px">Statistic<br/><small>(from artifact)</small></th>
                   <th style="padding: 0 10px; text-align: center !important;">
                       <form id="compareForm" action="/statistic/compare" method="get" target="_blank">
                           <input type="hidden" name="buildIDs">
                           <input type="hidden" name="buildTypeID" value="${buildTypeID}">

                           <input type="button" value="Compare" onclick="submitCompare('${buildTypeID}');">
                       </form>
                   </th>
               </tr>
                <c:set var="i" value="1"/>
                <c:forEach items="${builds}" var="build">
                    <tr id="${build.ID.buildID}" class="build">
                        <td><div class="remove" onclick="removeBuild(${i}, this, '${build.ID.buildID}', '${build.ID.buildTypeID}')"></div></td>


                        <td class="clickable">${build.ID.buildID}</td>
                        <td class="clickable" style="text-align: center; width: 180px">${build.finishFormattedDate}</td>
                        <td class="clickable" style="text-align: center"><base:buildStatus status="${build.status}"/></td>
                        <td class="clickable" style="text-align: center">${build.buildNumber}</td>

                        <td style="cursor: default">
                            <div class="artifacts" style="text-align: center; width: auto">
                                <forms:loadArtifactForms  buildID="${build.ID.buildID}" buildTypeID="${build.ID.buildTypeID}" artifacts="${build.artifacts}"/>
                            </div>
                        </td>

                        <c:set var="hasStat" value="false"/>
                        <td class="statistic" style="cursor: default; text-align: center">
                            <c:forEach items="${build.artifacts}" var="artifact">
                                <c:if test="${artifact.value == true}">
                                    ${artifact.key}
                                    <c:set var="hasStat" value="true"/>
                                </c:if>
                            </c:forEach>
                        </td>

                        <td class="compare" style="text-align: center; cursor: default">
                            <c:if test="${hasStat}">
                                <input type="checkbox" name="compBuildID">
                            </c:if>
                        </td>
                    </tr>
                    <c:set var="i" value="${i+1}"/>
                </c:forEach>
            </table>
        </div>
    </div>

    <div class="frame" style="max-height: 90%; height: 90%; padding-bottom: 5px; overflow: visible; width: 35%">
        <div id="statForm" class="frame-block" style="height: 35%"></div>
        <div id="buildInfo" class="frame-block" style="height: 60%; max-height: 60%"></div>
    </div>
</body>
</html>
