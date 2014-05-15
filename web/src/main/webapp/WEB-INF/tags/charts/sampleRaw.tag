<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@attribute name="id" type="java.lang.String" required="true" %>
<%@attribute name="sample" type="load_test_service.api.statistic.results.SampleRawResults" required="true" %>

<%@taglib prefix="base" tagdir="/WEB-INF/tags" %>


<div id="sample${id}" class="sample">

    <div id="sampleTitle${id}">
        <strong>
            <base:sampleName threadGroup="${sample.threadGroup}" name="${sample.name}"/>
        </strong>
    </div>

    <table id="chart${id}" style="width: 100%">
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
                        <c:forEach items="" var="value" varStatus="loop">
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
            createSRTChart(${id}, srtData);
            createRPSChart(${id}, rpsData);
        })();
    </script>
</div>