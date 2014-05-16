<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@attribute name="id" type="java.lang.String" required="true" %>
<%@attribute name="sample" type="load_test_service.api.statistic.results.SampleRawResults" required="true" %>

<%@taglib prefix="base" tagdir="/WEB-INF/tags" %>


<div id="sample${id}" class="sample sample-hide">

    <div id="sampleTitle${id}" class="title-hide" title="Click to show SRT and RPS charts">
        <span style="font-size: 110%">
            <base:sampleName threadGroup="${sample.threadGroup}" name="${sample.name}"/>
        </span>
    </div>

    <div id="chart${id}" style="width: 100%; display: none; height: 500px">
        <div id="load${id}" style="display: none; width: 100%">
            <img src="/resources/img/loading.gif">
        </div>
        <div id="srtSVG${id}" style="height: 250px; width: 1800px">
            <svg></svg>
        </div>
        <div id="rpsSVG${id}" style="height: 250px; width: 1800px">
            <svg></svg>
        </div>
    </div>

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

                if (charts.style.display == "none") {
                    charts.style.display = "inline";
                    document.getElementById("sample${id}").className = "sample sample-open";
                    document.getElementById('sampleTitle${id}').className = "title-open";
                } else if (charts.style.display == "inline") {
                    charts.style.display = "none";
                    document.getElementById("sample${id}").className = "sample sample-hide";
                    document.getElementById('sampleTitle${id}').className = "title-hide";
                }

                if (!loaded) {
                    document.getElementById("load${id}").style.display = "block";
                    createSRTChart(${id}, srtData, 1800, 320);
                    createRPSChart(${id}, rpsData, 1800, 320);
                    document.getElementById("load${id}").style.display = "none";
                    loaded = true;
                }

            };
        })();
    </script>
</div>