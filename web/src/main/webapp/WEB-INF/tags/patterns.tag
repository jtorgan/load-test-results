<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@attribute name="bt" type="java.lang.String" required="false" %>
<%@attribute name="patterns" type="java.lang.String[]" required="false"%>


<%--<script>
    $(function() {
        $('.update-patterns').bind('click' ,function() {

            var form = $(this).closest("form");
/*            var buildTypeID = $(form).filter("input").val();
            $.ajax({
                type: "post",
                url: "setPatterns",
                data: {buildTypeID: buildTypeID, buildID: buildID}
            }).done(function (data) {
                        $("#artifact-list").html(data);
                    });*/
        });
    });
</script>--%>

<input type="hidden" name="buildTypeID" value="${bt}">
<div>Artifacts patterns:</div>
<textarea rows="10" cols="45" name="patterns" wrap="soft">
    <c:if test="${not empty patterns}">
        <c:forEach items="${patterns}" var="pattern">
            ${pattern}<br/>
        </c:forEach>
    </c:if>
</textarea>
<input type="submit" value="Save" class="update-patterns">


