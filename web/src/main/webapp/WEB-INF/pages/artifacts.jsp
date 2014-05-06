<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%--@elvariable id="build" type="load_test_service.api.model.TestBuild"--%>
<%--@elvariable id="artifacts" type="java.util.Collection<java.lang.String>"--%>
<%--@elvariable id="metrics" type="java.util.Collection<load_test_service.api.statistic.metrics.Metric>"--%>

<style>
    #aggregationCalc {
        max-width: 30%;
        display: inline;
        text-align: left;
    }
</style>

<div class="frame-title" style="width: 98%">Artifacts <span class="art-build-title">in build # ${build.ID.buildID}</span></div>
<div id="artifact-list" style="height: 60%;  padding: 1%">
    <div id="statCalc">
        <table id="aggregationCalc">
            <thead>
            <tr>
                <th>Base metrics</th>
                <th>Settings</th>
                <th style="padding-left: 10px">Artifacts</th>
            </tr>
            </thead>
            <tr>
                <td style="width: 20%">
                    <select id="metrics" name="metrics" multiple size="5">
                        <c:forEach items="${metrics}" var="metric">
                            <option value="${metric}">${metric.key}</option>
                        </c:forEach>
                    </select>
                </td>
                <td style="width: 40%">
                    <div style="float: left; width: 100%">
                        <input type="checkbox" name="total">
                        <span>Calculate total</span>
                    </div>
                    <div style="float: left; width: 100%">
                        <input type="checkbox" name="threadGroup">
                        <span>Tests run on several thread groups</span>
                    </div>
                </td>
                <td style="width: 40%">
                    <c:set var="index" value="1"/>
                    <c:forEach items="${artifacts}" var="art">
                        <div style="float: left; width: 100%">
                            <input name="artChecked" type="radio" style="margin: 5px 10px" value="${art}">
                            <form id="${index}" action="/builds/download" method="get" enctype="multipart/form-data" target="${art}" style="display: inline">
                                <input type="hidden" name="buildID" value="${build.ID.buildID}">
                                <input type="hidden" name="buildTypeID" value="${build.ID.buildTypeID}">
                                <input name="path" value="${art}" type="hidden" >
                                <span class="art" onclick="$('#${index}').submit();">${art}</span>
                            </form>
                        </div>
                        <c:set var="index" value="${index+1}"/>
                    </c:forEach>
                </td>
            </tr>

            <tr>
                <td style="text-align: right" colspan="3">
                    <input type="button" value="Calculate" onclick="calculateStatistic('${build.ID.buildID}', '${build.ID.buildTypeID}')">
                </td>
            </tr>
        </table>
    </div>
</div>

<div></div>






