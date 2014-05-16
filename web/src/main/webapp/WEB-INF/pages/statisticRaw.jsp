<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@taglib prefix="base" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="headers" tagdir="/WEB-INF/tags/headers" %>

<%@taglib prefix="charts" tagdir="/WEB-INF/tags/charts" %>
<%@taglib prefix="chartsOnDemand" tagdir="/WEB-INF/tags/charts/onDemand" %>

<%--@elvariable id="build" type="load_test_service.api.model.TestBuild"--%>
<%--@elvariable id="rawResults" type="java.util.Map<load_test_service.api.statistic.TestID, load_test_service.api.statistic.results.SampleRawResults>"--%>

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
    <style>
        .testsTitle {
            float: left;
            font-size: 120%;
            /*border: 1px solid #86a4c3;*/
            margin: 1.5% 1% 1%;
            width: 99%;
            padding: 0 15px;
        }
    </style>
</head>
<body>

    <div>
       <headers:bHeader build="${build}"/>
    </div>

    <span class="testsTitle">
        Tests:
    </span>

    <div style="border: 1px solid #86a4c3; margin: 0 1%; float: left; width: 98%">
        <c:set var="id" value="1"/>
        <c:forEach var="sampleRaws" items="${rawResults}">
            <chartsOnDemand:sampleRaw id="${id}" sample="${sampleRaws.value}"/>
            <c:set var="id" value="${id+1}"/>
        </c:forEach>
    </div>

</body>
</html>
