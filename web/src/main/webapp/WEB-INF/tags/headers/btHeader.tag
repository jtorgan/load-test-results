<%@attribute name="buildType" type="load_test_service.api.model.BuildType" required="true" %>
<%@taglib prefix="base" tagdir="/WEB-INF/tags" %>

<style>
    .btItem {
        float: left;
        margin: 0 10px;
    }
</style>

<link href="/resources/css/headers.css" rel="stylesheet">
<script src="/resources/js/projects.js" language="JavaScript"></script>

<div style="width: 100%;">
    <div style="font-size: 110%">
        <div class="btItem" style="font-weight: bolder">Build Configuration: </div>
        <div class="btItem">${buildType.projectName}&nbsp;&nbsp;&nbsp;>&nbsp;&nbsp;&nbsp;${buildType.name}</div>
        <div class="btItem"><base:monitoring-icon buildTypeID="${buildType.ID}" isMonitored="${buildType.monitored}"/></div>
        <div class="btItem">${buildType.lastBuildID} <small>(last loaded build, ID)</small></div>
    </div>
</div>
