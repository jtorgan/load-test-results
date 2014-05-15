<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@taglib prefix="charts" tagdir="/WEB-INF/tags/charts" %>
<%@taglib prefix="chartsOnDemand" tagdir="/WEB-INF/tags/charts/onDemand" %>

<%--@elvariable id="buildType" type="load_test_service.api.model.BuildType"--%>
<%--@elvariable id="samples" type="java.util.Map<load_test_service.api.statistic.TestID, load_test_service.api.statistic.results.SampleStatistic>"--%>
<%--@elvariable id="settings" type="load_service.web.DefaultChartSettings"--%>

<c:set var="req" value="${pageContext.request}" />
<c:set var="baseURL" value="${req.scheme}://${req.serverName}:${req.serverPort}${req.contextPath}" />

<html>
<head>
    <title>Performance statistic</title>

    <script type="text/javascript" src="/resources/nvd3/d3.v3.js"></script>
    <script type="text/javascript" src="/resources/nvd3/nv.d3.js"></script>
    <link href="/resources/nvd3/nv.d3.css" rel="stylesheet" type="text/css">

    <link rel="stylesheet" href="/resources/css/charts.css">

    <script type="text/javascript" src="/resources/js/charts.js"></script>
</head>
<body>

<div id="btInfo">
    <%--todo: build type info--%>
    <table>
        <tr><td>Build configuration</td><td>${buildType.name}</td></tr>
        <tr><td>Parent project</td><td>${buildType.projectName}</td></tr>
        <tr><td>Monitoring status</td><td>${buildType.monitored}</td></tr>
        <tr><td>Last loaded build (ID)</td><td>${buildType.lastBuildID}</td></tr>
    </table>
</div>

<div id="default_settings">
    <charts:defaultChartSettings buildTypeID="${buildType.ID}" settings="${settings}"/>
</div>

<c:set var="id" value="1"/>
<c:forEach var="sample" items="${samples}">
    <chartsOnDemand:sample id="${id}" sample="${sample.value}" settings="${settings}"/>
    <c:set var="id" value="${id+1}"/>
</c:forEach>

</body>
</html>
