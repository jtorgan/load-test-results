<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<%--@elvariable id="metrics" type="load_test_service.api.statistic.metrics.MetricDescriptor[]"--%>

<form:form method="post" action="countStatistic">
    <table style="width: 100px">
        <tr>
            <td rowspan="2">
                <select multiple size="5" name="metrics">
                    <c:forEach items="${metrics}" var="metric">
                        <option value="${metric.key}">${metric.title}</option>
                    </c:forEach>
                </select>
            </td>
            <td>
                <div style="float: left">
                    Build id: <input type="text" name="buildID">
                </div>
                <div style="float: left">
                    Artifact name : <input type="text" name="artifact">
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div style="float: left">
                    <input type="checkbox" name="total"> Total
                </div>
                <div style="float: left">
                    <input type="checkbox" name="asserts"> Asserts
                </div>
            </td>
            <td rowspan="2">
                <input type="submit">
            </td>
        </tr>
    </table>
</form:form>

