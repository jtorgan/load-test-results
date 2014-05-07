<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%--@elvariable id="build" type="load_test_service.api.model.TestBuild"--%>
<%--@elvariable id="artifacts" type="java.util.List<java.lang.String>"--%>
<%--@elvariable id="metrics" type="java.util.Collection<load_test_service.api.statistic.metrics.Metric>"--%>

<div class="frame-title">Calculate load statistics <span class="art-build-title">in build # ${build.ID.buildID}</span></div>
<style>
    .stat-form {

    }
    .stat-form th {
        text-align: left;
    }
    .setting-item {

    }
</style>
<div id="aggregationCalc" class="frame-content">

    <table class="stat-form" cellspacing="5">
        <tr>
            <th>Performance metrics</th>
            <th>Settings</th>
            <th>Enable artifacts</th>
        </tr>
        <tr>
            <td>
                <select id="metrics" class="input-medium" name="metrics" multiple size="5">
                    <c:forEach items="${metrics}" var="metric">
                        <option value="${metric}">${metric.key}</option>
                    </c:forEach>
                </select>
            </td>
            <td>
               <div>
                   <input type="checkbox" name="asserts" id="asserts">
                   <span class="setting-item">based on asserts only</span>
               </div>
                <div>
                    <input type="checkbox" name="total" id="total">
                    <span class="setting-item">calculate total values</span>
                </div>
                <div>
                    <input type="checkbox" name="threadGroup" id="threadGroup">
                    <span class="setting-item">tests run on several thread groups</span>
                </div>
            </td>
            <td>
                <c:set var="index" value="1"/>
                <c:forEach items="${artifacts}" var="art">
                    <form id="${index}" action="/builds/download" method="get" enctype="multipart/form-data" target="${art}" style="display: inline">
                        <input type="hidden" name="buildID" value="${build.ID.buildID}">
                        <input type="hidden" name="buildTypeID" value="${build.ID.buildTypeID}">
                        <input type="hidden" name="path" value="${art}" >
                        <div style="display: inline-block; width: 100%">
                            <input type="radio" name="artChecked" id="${art}" value="${art}">
                            <span class="setting-item" onclick="$('#${index}').submit();">${art}</span>
                        </div>
                    </form>
                    <c:set var="index" value="${index+1}"/>
                </c:forEach>
            </td>
        </tr>
        <tr>
            <td colspan="3" style="text-align: right; padding-right: 10%">
                <button id="calculate" name="calculate" onclick="calculateStatistic('${build.ID.buildID}', '${build.ID.buildTypeID}')">Calculate</button>
            </td>
        </tr>
    </table>
</div>









