<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

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

    <script type="text/javascript" src="/resources/js/charts.js"></script>
    <style>
        #common {
            width: 95%;
            border: 1px solid #d0d0d0;
        }
        #common th {
            background-color: #e5e5e5;
        }
        #buildInfo {
            width: 35%;
        }
        #dependencies {
            width: 65%;
        }

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
        }
    </style>
</head>
<body>
<table id="common">
    <thead>
    <tr>
        <th>Build info</th>
        <th>Dependencies</th>
    </tr>
    </thead>
    <tr>
        <td id="buildInfo">
            <table>
                <tr>
                    <td>ID</td>
                    <td>${build.ID.buildID}</td>
                </tr>
                <tr>
                    <td>number</td>
                    <td>${build.buildNumber}</td>
                </tr>
                <tr>
                    <td>status</td>
                    <td><base:buildStatus status="${build.status}"/></td>
                </tr>
                <tr>
                    <td>finish date</td>
                    <td><base:buildStatus status="${build.finishFormattedDate}"/></td>
                </tr>
            </table>
        </td>
        <td id="dependencies">
            <base:dependencies dependencyList="${build.dependencyList}"/>
        </td>
    </tr>
</table>

<c:set var="id" value="1"/>
<c:forEach var="sampleRaws" items="${rawResults}">

    <div id="sample${id}" class="sample">

        <c:set var="sample" value="${sampleRaws.value}"/>

        <div id="sampleTitle${id}">
            <strong>
                <c:if test="${not empty sample.threadGroup}">
                    ${sample.threadGroup} -
                </c:if>
                    ${sample.name}
            </strong>
        </div>

        <input id="isLoaded${id}" type="hidden" value="false">
        <table id="chart${id}" style="width: 100%; display: none">
            <tr>
                <td>
                    <div id="srtSVG${id}"  class="chart">
                        <svg></svg>
                    </div>
                </td>
                <td class="chart">
                    <div id="rpsSVG${id}"  class="chart">
                        <svg></svg>
                    </div>
                </td>
            </tr>
        </table>

        <script type="text/javascript">
            (function() {
                var srtData = [
                    {
                        key: "Server Response Time",
                        values: [
                            <c:forEach items="${sample.SRTValues}" var="value" varStatus="loop">
                            { x: "${value.x}" , y: ${value.y} }
                            ${not loop.last ? "," : ""}
                            </c:forEach>
                        ]
                    }
                ];

                var rpsData = [
                    {
                        key: "Requests Per Seconds",
                        values: [
                            <c:forEach items="${sample.RPSValues}" var="value" varStatus="loop">
                            { x: "${value.x}" , y: ${value.y} }
                            ${not loop.last ? "," : ""}
                            </c:forEach>
                        ]
                    }
                ];
                var loaded = false;
                document.getElementById('sampleTitle${id}').onclick = function(){
                    var charts = document.getElementById("chart${id}");
                    if (!loaded) {
                        createSRTChart(${id}, srtData);
                        createRPSChart(${id}, rpsData);
                        loaded = true;
                    }
                    if (charts.style.display == "table") {
                        charts.style.display = "none";
                    } else if (charts.style.display == "none") {
                        charts.style.display = "table";
                    }
                };
            })();
        </script>
    </div>

    <c:set var="id" value="${id+1}"/>
</c:forEach>

</body>
</html>
