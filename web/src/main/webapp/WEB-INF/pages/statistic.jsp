<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@taglib prefix="charts" tagdir="/WEB-INF/tags/charts" %>
<%@taglib prefix="chartsOnDemand" tagdir="/WEB-INF/tags/charts/onDemand" %>

<%--@elvariable id="buildTypeID" type="java.lang.String"--%>
<%--@elvariable id="samples" type="java.util.Map<load_test_service.api.statistic.TestID, load_test_service.api.statistic.results.SampleStatistic>"--%>

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
        .nv-axisMaxMin text {
            font-weight: normal !important;
        }

        .sample {
            display: block;
            width: 100%;
            border-bottom: 1px solid #86a4c3;
        }
        .chart {
            height: auto;
            width: 100%;
        }
    </style>
</head>
<body>

<div id="btInfo">
    <%--todo: build type info--%>
</div>

<c:set var="id" value="1"/>
<c:forEach var="sample" items="${samples}">
    <chartsOnDemand:sample id="${id}" sample="${sample.value}"/>
    <c:set var="id" value="${id+1}"/>
</c:forEach>

</body>
</html>
