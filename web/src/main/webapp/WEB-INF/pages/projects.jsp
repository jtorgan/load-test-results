<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="template" tagdir="/WEB-INF/tags" %>

<%--@elvariable id="projects" type="java.util.List<load_test_service.ProjectTree>"--%>
<%--@elvariable id="saved_bt" type="java.util.List<load_test_service.api.model.BuildType>"--%>

<html>
<head>
    <title>Performance results</title>

    <script src="http://code.jquery.com/jquery-2.1.1.min.js"></script>
    <script src="/resources/js/projects.js" language="JavaScript"></script>

    <link href="/resources/css/projects.css" rel="stylesheet">
    <link href="/resources/css/common.css" rel="stylesheet">
</head>
<body>
<div class="frame" style="border-right: 1px solid #86a4c3; height: 60%; margin-top: 1%; padding-right: 1%">

    <div class="frame-title">TeamCity projects</div>

    <div style="float: left; padding: 10px; background-color: #f0f0f0; width: 95%; border: 1px solid #e1e1e1; border-bottom: none !important;">
        <input type="text" id="projectFilter" placeholder="filter projects" style="width: 300px">
    </div>

    <div class="tc-projects">
        <c:forEach items="${projects}" var="project">
            <template:projectTree project="${project}"/>
        </c:forEach>
    </div>
</div>

<div class="right frame" style="height: 60%; margin-top: 1%">
    <div class="frame-title">Monitored build configurations</div>

    <table class="bt-item-saved-container" style="table-layout: fixed;">
        <tbody class="bt-item-saved">

        <jsp:include page="savedBuildTypes.jsp">
            <jsp:param name="saved_bt" value="${saved_bt}"/>
        </jsp:include>

        </tbody>
    </table>
</div>

<div class="frame" style="height: 40%; padding-right: 1%">
    <div class="frame-title">Build configs in project</div>
    <div class="bt-list" style="float: left; padding: 5px 10px; width: 95%">
    </div>
</div>

<div class="right frame" style="height: 40%">
    <div class="frame-title">Add build type to monitor</div>

    <div class="edit-bt-container" style="float: left; padding: 10px 20px; width: 90%; border: 1px solid #e1e1e1;">
        <input type="hidden" name="bt">
        <table id="edit-bt-form" style="border-spacing: 3px; display: none">
            <tr>
                <td>Project:</td>
                <td class="edit-bt-project"></td>
            </tr>
            <tr>
                <td>Build type:</td>
                <td class="edit-bt-name"></td>
            </tr>
            <tr>
                <td style="vertical-align: top">Artifact patterns:</td>
                <td>
                    <textarea rows="4" cols="30" id="edit-bt-patterns" wrap="soft"></textarea>
                </td>
            </tr>
            <tr>
                <td colspan="2" style="text-align: right">
                    <input id="edit-bt-save" type="button" value="Save">
                </td>
            </tr>
        </table>
    </div>
</div>
</body>
</html>
