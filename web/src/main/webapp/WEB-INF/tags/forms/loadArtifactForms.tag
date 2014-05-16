<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="template" tagdir="/WEB-INF/tags" %>

<%@attribute name="buildID" type="java.lang.String" required="true" %>
<%@attribute name="buildTypeID" type="java.lang.String" required="true" %>
<%@attribute name="artifacts" type="java.util.Map<java.lang.String,java.lang.Boolean>" required="true" %>

<%@taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>

<c:set var="index" value="1"/>
<c:forEach items="${artifacts}" var="art">
    <div style="float: left; width: 13%">
        <forms:showRawStatForm buildID="${buildID}" buildTypeID="${buildTypeID}" artifact="${art.key}" linkTest="srt&rps"/>
    </div>
    <div style="float: left; width: 87%">
        <form id="${index}ready" action="/builds/download" method="get" enctype="multipart/form-data" target="${art.key}" style="display: inline; float: left; margin-bottom: 0 !important;">
            <input type="hidden" name="buildID" value="${buildID}">
            <input type="hidden" name="buildTypeID" value="${buildTypeID}">
            <input type="hidden" name="path" value="${art.key}" >

            <a style="margin: 0 5px; float: left"  href="javascript:;" onclick="parentNode.submit();" title="Download">${art.key}</a>
            <%--<spanonclick="$('#${index}ready').submit();">load</span>--%>
        </form>
    </div>
    <c:set var="index" value="${index+1}"/>
</c:forEach>