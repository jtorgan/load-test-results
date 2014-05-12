<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="template" tagdir="/WEB-INF/tags" %>


<%--@elvariable id="build" type="load_test_service.api.model.TestBuild"--%>
<%--@elvariable id="samples" type="java.util.Collection<load_test_service.api.statistic.results.Sample>"--%>

<c:set var="req" value="${pageContext.request}" />
<c:set var="baseURL" value="${req.scheme}://${req.serverName}:${req.serverPort}${req.contextPath}" />

<html>
<head>
    <title>Performance statistic</title>

    <script src="http://code.jquery.com/jquery-2.1.1.min.js"></script>

    <%--<script src="http://d3js.org/d3.v3.min.js" charset="utf-8"></script>--%>
    <%--<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.16/angular.min.js"></script>--%>
<%----%>
    <%--<script src="https://raw.githubusercontent.com/novus/nvd3/master/nv.d3.min.js"></script>--%>
    <%--<link rel="stylesheet" href="https://raw.githubusercontent.com/novus/nvd3/master/src/nv.d3.css">--%>

    <%--<script src="/resources/js/builds.js" language="JavaScript"></script>--%>

<%--    <link rel="stylesheet" href="/resources/css/common.css">
    <link rel="stylesheet" href="/resources/css/builds.css">--%>

    <style>
        #common {
            width: 95%;
        }
        #buildInfo {
            width: 30%;
        }
        #dependencies {
            width: 70%;
        }
    </style>
</head>
<body>
<table id="common">
    <tr>
        <th>Build info</th>
        <th>Dependencies</th>
    </tr>
   <tr>
       <td id="buildInfo">
           <table>
               <tr>
                   <td>ID</td>
                   <td>${build.ID.buildID}</td>
               </tr>
               <tr>
                   <td>number</td>
                   <td>${build.buildNumber}</td>
               </tr>
               <tr>
                   <td>status</td>
                   <td><template:buildStatus status="${build.status}"/></td>
               </tr>
               <tr>
                   <td>finish date</td>
                   <td><template:buildStatus status="${build.finishFormattedDate}"/></td>
               </tr>
           </table>
       </td>
       <td id="dependencies">
           <template:dependencies dependencyList="${build.dependencyList}"/>
       </td>
   </tr>
</table>


<table>
    <c:forEach var="sample" items="${samples}">
        <tr><td>${sample.threadGroup}</td><td>${sample.name}</td></tr>
        <c:forEach var="metric" items="${sample.metricValues}">
            <tr>
                <td></td>
                <td>${metric.key}</td>
            </tr>

            <c:forEach var="value" items="${metric.value}">
            <tr>
                <td>${value.buildID}</td>
                <td>${value.value}</td>
            </tr>
            </c:forEach>
        </c:forEach>
    </c:forEach>
</table>

</body>
</html>
