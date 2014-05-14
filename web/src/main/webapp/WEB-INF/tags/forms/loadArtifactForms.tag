<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="template" tagdir="/WEB-INF/tags" %>

<%@attribute name="buildID" type="java.lang.String" required="true" %>
<%@attribute name="buildTypeID" type="java.lang.String" required="true" %>
<%@attribute name="artifacts" type="java.util.Map<java.lang.String,java.lang.Boolean>" required="true" %>

<c:set var="index" value="1"/>
<c:forEach items="${artifacts}" var="art">
    <div style="float: left; width: 100%">
        <form id="${index}ready" action="/builds/download" method="get" enctype="multipart/form-data" target="${art.key}" style="display: inline">
            <input type="hidden" name="buildID" value="${buildID}">
            <input type="hidden" name="buildTypeID" value="${buildTypeID}">
            <input type="hidden" name="path" value="${art.key}" >

            <span class="art" style="margin-right: 10px" onclick="$('#${index}ready').submit();" title="Download">${art.key}</span>
        </form>
    </div>
    <c:set var="index" value="${index+1}"/>
</c:forEach>