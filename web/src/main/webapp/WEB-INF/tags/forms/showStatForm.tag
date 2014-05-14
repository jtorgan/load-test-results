<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="template" tagdir="/WEB-INF/tags" %>

<%@attribute name="buildID" type="java.lang.String" required="true" %>
<%@attribute name="buildTypeID" type="java.lang.String" required="true" %>
<%@attribute name="artifact" type="java.lang.String" required="true" %>

<form action="/statistic/showRawStat" method="get" target="_blank">
    <input type="hidden" name="buildID" value="${buildID}">
    <input type="hidden" name="buildTypeID" value="${buildTypeID}">
    <input type="hidden" name="artifact" value="${artifact}" >

    <%--todo: srt and rps can be shown not only for calculated stat--%>
    <a href="javascript:;" onclick="parentNode.submit();" title="Click to show RPS and STR artifacts">${artifact}</a>
</form>
