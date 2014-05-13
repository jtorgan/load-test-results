<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="template" tagdir="/WEB-INF/tags" %>


<%--@elvariable id="build" type="load_test_service.api.model.TestBuild"--%>
<%--@elvariable id="samples" type="java.util.Collection<load_test_service.api.statistic.results.Sample>"--%>

<c:set var="req" value="${pageContext.request}" />
<c:set var="baseURL" value="${req.scheme}://${req.serverName}:${req.serverPort}${req.contextPath}" />

<html>
<head>
    <title>Performance statistic</title>

    <script type="text/javascript" src="/resources/nvd3/d3.v3.js"></script>
    <script type="text/javascript" src="/resources/nvd3/nv.d3.js"></script>
    <link href="/resources/nvd3/nv.d3.css" rel="stylesheet" type="text/css">

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
        .sample_title {
            padding: 5px 0;
        }
        .chart svg {
            /*width: 800px;*/
            height: 280px;
        }
        .nv-axisMaxMin text {
            font-weight: normal !important;
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
                   <td><template:buildStatus status="${build.status}"/></td>
               </tr>
               <tr>
                   <td>finish date</td>
                   <td><template:buildStatus status="${build.finishFormattedDate}"/></td>
               </tr>
           </table>
       </td>
       <td id="dependencies">
           <template:dependencies dependencyList="${build.dependencyList}"/>
       </td>
   </tr>
</table>

<br/>

<%--<table>--%>
    <c:set var="id" value="1"/>
    <c:forEach var="sample" items="${samples}">

        <div class="sample_charts">
            <div class="sample_title">
                <strong>
                    <c:if test="${not empty sample.threadGroup}">
                        ${sample.threadGroup} -
                    </c:if>
                  ${sample.name}
                </strong>
            </div>

            <div id="sample${id}" class="chart">
                <svg></svg>
            </div>

            <script type="text/javascript">
                (function() {
                    var data = [
                        <c:forEach items="${sample.metricValues}" var="metric" varStatus="loopOuter">
                        {
                            key: "${metric.key}",
                            values: [
                            <c:forEach items="${metric.value}" var="value" varStatus="loopInner">
                                { x: "${value.buildID}" , y: ${value.value} }
                                ${not loopInner.last ? "," : ""}
                            </c:forEach>
                            ]
                        }
                        ${not loopOuter.last ? "," : ""}
                        </c:forEach>
                    ];
                    nv.addGraph(function() {
                        var width = 800,
                            height = 320,
                            margin = {top: 30, right: 50, bottom: 50, left: 80};

                        var builds = data[0].values.map(function(d) {return d.x;});

                        var chart = nv.models.lineChart()
                                        .width(width - margin.right - margin.left)
                                        .height(height - margin.top - margin.bottom)
                                        .margin(margin)
                                        .x(function(d, id) { return id })
                                        .color(d3.scale.category10().range())
                                        .useInteractiveGuideline(true)
                         ;
                        chart.xAxis
                                .axisLabel('Build ID')
                                .tickFormat(function(id) {
                                    return builds[id];
                                })
                        ;

                        chart.yAxis
                                .axisLabel('Time (ms)')
                                .axisLabelDistance(40);

                        chart.forceY([0]);
                        d3.select('#sample${id} svg')
                                .datum(data)
                                .call(chart)
                        ;
                        nv.utils.windowResize(chart.update);
                        return chart;
                    });

                })();
            </script>

        </div>
        <c:set var="id" value="${id+1}"/>
    </c:forEach>
<%--</table>--%>

</body>
</html>
