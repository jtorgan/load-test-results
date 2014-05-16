<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@taglib prefix="charts" tagdir="/WEB-INF/tags/charts" %>
<%@taglib prefix="chartsOnDemand" tagdir="/WEB-INF/tags/charts/onDemand" %>

<%@taglib prefix="headers" tagdir="/WEB-INF/tags/headers" %>

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
    <style>
        #btInfo {
            margin: 1%;
            padding-left: 5px;
            float: left;
            width: 98%;
        }
        #default_settings {
            float: left;
            width: 99%;
            padding: 1%;
            border-top: 1px solid #e4e4e4;
            border-bottom: 1px solid #e4e4e4;
            background-color: #f0f0f0;
        }

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

<div id="btInfo">
  <headers:btHeader buildType="${buildType}"/>
</div>

<div id="default_settings">
    <charts:defaultChartSettings buildTypeID="${buildType.ID}" settings="${settings}"/>
</div>

<span class="testsTitle">
    Tests:
</span>

<div style="border: 1px solid #86a4c3; margin: 0 1%; float: left; width: 98%">
    <c:set var="id" value="1"/>
    <c:forEach var="sample" items="${samples}">
        <chartsOnDemand:sample id="${id}" sample="${sample.value}" settings="${settings}"/>
        <c:set var="id" value="${id+1}"/>
    </c:forEach>
</div>


</body>
</html>
