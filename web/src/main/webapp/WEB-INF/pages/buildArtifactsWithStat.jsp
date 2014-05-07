<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%--@elvariable id="build" type="load_test_service.api.model.TestBuild"--%>
<%--@elvariable id="artifacts" type="java.util.List<java.lang.String>"--%>

<div class="frame-title" style="width: 98%">Artifacts (with statistic) <span class="art-build-title">in build # ${build.ID.buildID}</span></div>

<div class="frame-content">
    <c:set var="index" value="1"/>
    <c:forEach items="${artifacts}" var="art">
        <div style="float: left; width: 100%">
            <form id="${index}ready" action="/builds/download" method="get" enctype="multipart/form-data" target="${art}" style="display: inline">
                <input type="hidden" name="buildID" value="${build.ID.buildID}">
                <input type="hidden" name="buildTypeID" value="${build.ID.buildTypeID}">
                <input type="hidden" name="path" value="${art}" >

                <span class="art" style="margin-right: 10px" onclick="$('#${index}ready').submit();">${art}</span>
                <button onclick="">show statistic</button>
            </form>
        </div>
        <c:set var="index" value="${index+1}"/>
    </c:forEach>
</div>







