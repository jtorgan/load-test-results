<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="template" tagdir="/WEB-INF/tags" %>

<%@attribute name="buildTypeID" type="java.lang.String" required="true" %>
<%@attribute name="linkText" type="java.lang.String" required="true" %>

<%--<%@attribute name="artifact" type="java.lang.String" required="true" %>--%>

<form action="/statistic/showStat" method="get" target="_blank">
    <input type="hidden" name="buildTypeID" value="${buildTypeID}">
    <a href="javascript:;" onclick="parentNode.submit();" title="Click to show calculated statistic">${linkText}</a>
</form>