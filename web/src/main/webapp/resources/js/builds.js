var currentBuild;

$(function() {
    $('#back').bind('click' ,function() {
        history.back();
        return false;
    });

    $('td.clickable').bind('click' ,function() {
        var buildID = $(this).closest("tr").attr("id");
        var buildTypeID = $("#btID").val();
        $.ajax({
            type: "get",
            url: "/builds/info",
            data: {buildTypeID: buildTypeID, buildID: buildID}
        }).done(function (data) {
                $("#build-info").html(data);
                currentBuild = buildID;
            });
        $.ajax({
            type: "get",
            url: "/builds/artifacts",
            data: {buildTypeID: buildTypeID, buildID: buildID}
        }).done(function (data) {
                $("#artifacts").html(data);
                currentBuild = buildID;
            });
    });

    $('#statCalc').bind('submit' ,function() {

    });
});

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
                $("#artifacts").empty();
                $("#build-info").empty();
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
        url: "/builds/calculateStatistic",
        traditional: true,
        data: {
            buildTypeID: btID,
            buildID: bID,
            metrics: metrics,
            artifact: artifact,
            threadGroup: threadGroup,
            total: total
        },
        dataType: "json"
    }).done(function () {
            alert("Ready!");
    });
}