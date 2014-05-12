<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="template" tagdir="/WEB-INF/tags" %>

<%--@elvariable id="build" type="load_test_service.api.model.TestBuild"--%>

<div class="frame-title">Dependencies in build # ${build.ID.buildID}</div>
<div id="dep-list" class="frame-content">
    <template:dependencies dependencyList="${build.dependencyList}"/>
</div>





