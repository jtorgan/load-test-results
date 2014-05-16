<%@attribute name="build" type="load_test_service.api.model.TestBuild" required="true" %>
<%@taglib prefix="base" tagdir="/WEB-INF/tags" %>


<style>
    #common {
        width: 98%;
        border: 1px solid #d0d0d0;
        margin: 1%;
    }
    #common th {
        background-color: #e5e5e5;
        text-align: left;
        padding: 5px 10px;
    }
    #buildInfo {
        width: 35%;
    }
    #buildInfo td {
        padding: 2px 10px;
    }
    #dependencies {
        width: 65%;
    }

    #dependencies td {
        padding: 2px 10px;
    }
</style>

<table id="common">
    <thead>
    <tr>
        <th>Build info</th>
        <th>Dependencies</th>
    </tr>
    </thead>
    <tr>
        <td id="buildInfo">
            <table>
                <tr>
                    <td>ID</td>
                    <td>${build.ID.buildID}</td>
                </tr>
                <tr>
                    <td>Build #</td>
                    <td>${build.buildNumber}</td>
                </tr>
                <tr>
                    <td>Status</td>
                    <td><base:buildStatus status="${build.status}"/></td>
                </tr>
                <tr>
                    <td>Finish date</td>
                    <td><base:buildStatus status="${build.finishFormattedDate}"/></td>
                </tr>
            </table>
        </td>
        <td id="dependencies">
            <div style="max-height: 150px; overflow: auto">
                <base:dependencies dependencyList="${build.dependencyList}"/>
            </div>
        </td>
    </tr>
</table>