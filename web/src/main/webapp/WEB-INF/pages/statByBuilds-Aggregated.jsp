<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@taglib prefix="base" tagdir="/WEB-INF/tags" %>

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

    <script type="text/javascript" src="/resources/js/charts.js"></script>
    <style>
        .nv-axisMaxMin text {
            font-weight: normal !important;
        }

        .sample {
            display: block;
            width: 100%;
            padding: 10px 0;
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

</div>
<c:set var="id" value="1"/>
<c:forEach var="item" items="${samples}">

    <div id="sample${id}" class="sample">

        <c:set var="sampleStat" value="${item.value}"/>

        <div id="sampleTitle${id}">
            <strong>
                <c:if test="${not empty sampleStat.threadGroup}">
                    ${sampleStat.threadGroup} -
                </c:if>
                    ${sampleStat.name}
            </strong>
        </div>

        <div id="statSVG${id}" class="chart" style="display: none">
            <svg></svg>
        </div>

        <script type="text/javascript">
            (function() {
                var statData = [
                    <c:forEach items="${sampleStat.metricValues}" var="metric" varStatus="loopOuter">
                    {
                        key: "${metric.key}",
                        values: [
                            <c:forEach items="${metric.value}" var="value" varStatus="loopInner">
                            { x: "${value.x}" , y: ${value.y} }
                            ${not loopInner.last ? "," : ""}
                            </c:forEach>
                        ]
                    }
                    ${not loopOuter.last ? "," : ""}
                    </c:forEach>
                ];
                var loaded = false;
                document.getElementById('sampleTitle${id}').onclick = function(){
                    var charts = document.getElementById("statSVG${id}");
                    if (!loaded) {
                        createStatChart(${id}, statData);
                        loaded = true;
                    }
                    if (charts.style.display == "inline") {
                        charts.style.display = "none";
                    } else if (charts.style.display == "none") {
                        charts.style.display = "inline";
                    }
                };
            })();
        </script>
    </div>

    <c:set var="id" value="${id+1}"/>
</c:forEach>

</body>
</html>
