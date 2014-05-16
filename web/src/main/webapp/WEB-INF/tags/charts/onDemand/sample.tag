<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@attribute name="id" type="java.lang.String" required="true" %>
<%@attribute name="sample" type="load_test_service.api.statistic.results.SampleStatistic" required="true" %>
<%@attribute name="settings" type="load_service.web.DefaultChartSettings" required="true" %>

<%@taglib prefix="base" tagdir="/WEB-INF/tags" %>

<div id="sample${id}" class="sample sample-hide">

    <div id="sampleTitle${id}" class="title-hide" title="Click to show chart">
        <span style="font-size: 110%">
            <base:sampleName threadGroup="${sample.threadGroup}" name="${sample.name}"/>
        </span>
        <%--<strong>--%>
        <%--</strong>--%>
    </div>

    <div id="stat${id}" style="height: 250px; display: none; padding-bottom: 10px">
        <div id="load${id}" style="display: none">
            <img src="/resources/img/loading.gif">
        </div>

        <div id="statSVG${id}" style="height: 280px; padding-top: 15px ">
            <svg></svg>
        </div>
    </div>


    <script type="text/javascript">
        (function() {
            var statData = [
                <c:forEach items="${sample.metricValues}" var="metric" varStatus="loopOuter">
                    <c:set var="key" value="${metric.key}"/>
                    {
                        key: "${key}",
                        disabled: ${settings.settings[key] == true},
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
                document.getElementById("sample${id}").className = "sample sample-hide";
                document.getElementById('sampleTitle${id}').className = "title-hide";
            } else if (charts.style.display == "none") {
                charts.style.display = "inline";
                document.getElementById("sample${id}").className = "sample sample-open";
                document.getElementById('sampleTitle${id}').className = "title-open";
            }
            if (!loaded) {
                document.getElementById("load${id}").style.display = "block";
                createStatChart(${id}, statData, 800, 320);
                document.getElementById("load${id}").style.display = "none";
                loaded = true;
            }
        };
        })();
    </script>
</div>