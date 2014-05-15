<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@attribute name="buildTypeID" type="java.lang.String" required="true" %>
<%@attribute name="settings" type="load_service.web.DefaultChartSettings" required="true" %>


<form action="/statistic/saveDefaultSettings" method="post">
    <input type="hidden" name="buildTypeID" value="${buildTypeID}">

    <h3> Default Charts Settings </h3>
    <table>
        <tr><td colspan="2" ></td></tr>
        <tr>
            <td>
                <label>
                    <input type="checkbox" name="min" <c:if test="${settings.settings['Min time'] == true}">checked</c:if> >Min
                </label>
            </td>
            <td>
                <label>
                    <input type="checkbox" name="average" <c:if test="${settings.settings['Average time'] == true}">checked</c:if> >Average
                </label>
            </td>
        </tr>
        <tr>
            <td>
                <label>
                    <input type="checkbox" name="max" <c:if test="${settings.settings['Max time'] == true}">checked</c:if> >Max
                </label>
            </td>
            <td>
                <label>
                    <input type="checkbox" name="line90" <c:if test="${settings.settings['90% line'] == true}">checked</c:if> >90% line
                </label>
            </td>
        </tr>
    </table>

    <div style="border-top: 1px dotted #CCCCCC">
        <input type="submit" value="Save">
    </div>
</form>

