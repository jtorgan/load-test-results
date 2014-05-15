<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@attribute name="id" type="java.lang.String" required="true" %>
<%@attribute name="sample" type="load_test_service.api.statistic.results.SampleStatistic" required="true" %>

<%@taglib prefix="base" tagdir="/WEB-INF/tags" %>

<div id="sample${id}" class="sample">

    <div id="sampleTitle${id}" style="padding: 10px 0 10px 0; cursor: pointer" title="Click to show chart">
        <strong>
            <base:sampleName threadGroup="${sample.threadGroup}" name="${sample.name}"/>
        </strong>
    </div>

    <div id="stat${id}" style="height: 250px; display: none; padding-bottom: 10px">
        <div id="load${id}" style="display: none">
            <img src="/resources/img/loading.gif">
        </div>

        <div id="statSVG${id}" style="height: 280px;">
            <svg></svg>
        </div>
    </div>


    <script type="text/javascript">
        (function() {
            var statData = [
                <c:forEach items="${sample.metricValues}" var="metric" varStatus="loopOuter">
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
            var charts = document.getElementById("stat${id}");
            if (charts.style.display == "inline") {
                charts.style.display = "none";
            } else if (charts.style.display == "none") {
                charts.style.display = "inline";
            }
            if (!loaded) {
                document.getElementById("load${id}").style.display = "block";
                createStatChart(${id}, statData);
                document.getElementById("load${id}").style.display = "none";
                loaded = true;
            }
        };
        })();
    </script>
</div>