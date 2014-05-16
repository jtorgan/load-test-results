<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@attribute name="buildTypeID" type="java.lang.String" required="true" %>
<%@attribute name="settings" type="load_service.web.DefaultChartSettings" required="true" %>

<style>
    #defSettings div {
        float: left;
        padding: 0 10px;
    }
</style>

<form id="defSettings" action="/statistic/saveDefaultSettings" method="post">
    <input type="hidden" name="buildTypeID" value="${buildTypeID}">
        <div style="padding-left: 15px !important;">
            <label>Default deselected series: </label>
        </div>
        <div>
            <label>
                <input type="checkbox" name="min" <c:if test="${settings.settings['Min time'] == true}">checked</c:if> >Min
            </label>
        </div>
        <div>
            <label>
                <input type="checkbox" name="average" <c:if test="${settings.settings['Average time'] == true}">checked</c:if> >Average
            </label>
        </div>
        <div>
            <label>
                <input type="checkbox" name="max" <c:if test="${settings.settings['Max time'] == true}">checked</c:if> >Max
            </label>
        </div>
        <div>
            <label>
                <input type="checkbox" name="line90" <c:if test="${settings.settings['90% line'] == true}">checked</c:if> >90% line
            </label>
        </div>

        <div style="padding-left: 20px !important;">
            <input type="submit" value="Save">
        </div>
</form>

