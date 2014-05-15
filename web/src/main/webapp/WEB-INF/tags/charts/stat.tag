<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@attribute name="id" type="java.lang.String" required="true" %>
<%@attribute name="metricValues" type="java.util.Map<java.lang.String, java.util.List<load_test_service.api.statistic.results.Value>>" required="true" %>

<div id="statSVG${id}" class="chart" style="display: none">
    <svg></svg>
</div>

<script type="text/javascript">
    (function() {
        var statData = [
            <c:forEach items="${metricValues}" var="metric" varStatus="loopOuter">
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
    createStatChart(${id}, statData);
    })();
</script>