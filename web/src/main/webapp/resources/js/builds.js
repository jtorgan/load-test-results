var currentBuild;

$(function() {
    $('#back').bind('click' ,function() {
        history.back();
        return false;
    });

    $('td.clickable').bind('click' ,function() {
        var buildID = $(this).closest("tr").attr("id");
        var buildTypeID = $("#btID").val();
        loadBuildInfo(buildID, buildTypeID);
        loadStatForm(buildID, buildTypeID);
//        loadArtifacts(buildID, buildTypeID);
    });

    $('td.clickable').bind('click' ,function() {
        var buildID = $(this).closest("tr").attr("id");
        var buildTypeID = $("#btID").val();
        loadBuildInfo(buildID, buildTypeID);
        loadStatForm(buildID, buildTypeID);
//        loadArtifacts(buildID, buildTypeID);
    });
});

function loadBuildInfo(buildID, buildTypeID) {
    $.ajax({
        type: "get",
        url: "/builds/info",
        data: {buildTypeID: buildTypeID, buildID: buildID}
    }).done(function (data) {
        $("#buildInfo").html(data);
        currentBuild = buildID;
    });
}

function loadStatForm(buildID, buildTypeID) {
    $.ajax({
        type: "get",
        url: "/statistic/statForm",
        data: {buildTypeID: buildTypeID, buildID: buildID}
    }).done(function (data) {
        $("#statForm").html(data);
        currentBuild = buildID;
    });
}

function removeBuild(index, div, bID, btID) {
    $.ajax({
        type: "post",
        url: "/builds/remove",
        data: {buildID: bID, buildTypeID: btID}
    }).done(function () {
//          var table = document.getElementById("builds");
            var item = $(div).closest("tr").get(0);
            item.parentNode.removeChild(item);
            var text = $("#count").text().trim();
            var count = parseInt(text);
            $("#count").text(count - 1);

//          empty if this info shown
            if (bID == currentBuild) {
                $("#buildInfo").empty();
                $("#statForm").empty();
                $("#artWithStat").empty();
            }
        });
}

function calculateStatistic(bID, btID) {

    var total = $("#aggregationCalc input[name='total']").is(":checked");
    var threadGroup =  $("#aggregationCalc input[name='threadGroup']").is(":checked");
    var artifact =  $("#aggregationCalc input[name='artChecked']").val();

    var metrics = [];
    $.each($("#metrics :selected"), function(i, option) {
        metrics.push($(option).val());
    });

    $.ajax({
        type: "post",
        url: "/statistic/calculate",
        traditional: true,
        data: {
            buildTypeID: btID,
            buildID: bID,
            metrics: metrics,
            artifact: artifact,
            threadGroup: threadGroup,
            total: total
        },
        dataType: "json",
        success: function(){
//            loadArtifacts(bID, btID);
            loadStatForm(bID, btID);
            $("#" + bID + " .statistic").html(artifact);
            $("#" + bID + " .compare").html('<input type="checkbox" name="compBuildID">');

        },
        error: function(){
            alert('Error happened during calculate load statistic');
        }
    });
}


function submitCompare() {
    var builds = "";
    $("input[name=compBuildID]").each(function() {
        var set = $(this).prop("checked");
        if (set) {
            var buildId = $(this).closest("tr").attr("id");
            builds += buildId + ",";
        }
    });
    $("input[name=buildIDs]").val(builds);
    $("#compareForm").submit();
}