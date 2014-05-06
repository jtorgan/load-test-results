<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@attribute name="project" type="load_test_service.ProjectTree" required="true" %>
<%@taglib prefix="template" tagdir="/WEB-INF/tags" %>

<div class="sub-project">
    <div id="${project.ID}" class="bt-item-list">
        ${project.name}
    </div>
    <div class="bt-sublist">
        <c:forEach items="${project.subProjects}" var="sub">
            <template:projectTree project="${sub}"/>
        </c:forEach>
    </div>
</div>

