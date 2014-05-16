function fillUpdateForm(prName, btID, btName, patterns) {
    __clearPatternForm();
    __fillPatternForm(prName, btName, patterns, function(event) {
        updateBuildType(btID);
    });
}

function fillAddForm(prID, prName, btID, btName) {
    __clearPatternForm();
    __fillPatternForm(prName, btName, null, function(event) {
        addBuildType(prID, prName, btID, btName);
    });
}


function __fillPatternForm(prName, btName, patterns, func) {
    var form = $("#edit-bt-form");
    $(form).find("td.edit-bt-project").text(prName);
    $(form).find("td.edit-bt-name").text(btName);

    if (patterns) {
        var area = $("#edit-bt-patterns");
        for(var i = 0; i < patterns.length; i++)
            area.append(patterns[i]).append('\n');
    }
    $("#edit-bt-save").click(func);
    $(form).css("display", "table");
}

function __clearPatternForm() {
    var form = $("#edit-bt-form");
    $(form).find("td.edit-bt-project").text("");
    $(form).find("td.edit-bt-name").text("");
    $("#edit-bt-patterns").text("");
    $("#edit-bt-save").unbind("click");
    $("#edit-bt-patterns").css("color", "black");
}

function __checkPatterns() {
/*    var area = $("#edit-bt-patterns").text().trim();
    var patterns = area.split("\n");
    for(var i = 0; i < patterns.length; i++) {
        try{
            var regex = new RegExp(patterns[i]);
        } catch(e) {
            $("#edit-bt-patterns").css("color", "red");
            return false;
        }
    }*/
    return true;
}

function updateBuildType(btID) {
    if (__checkPatterns()) {
        $.ajax({
            type: "post",
            url: "/buildTypes/setPatterns",
            data: { buildTypeID: btID,
                patterns: $("#edit-bt-patterns").val().trim()}
        }).done(function () {
                $("#edit-bt-form").css("display", "none");
                __clearPatternForm();
        });
    }
    return false;
}
function addBuildType(prID, prName, btID, btName) {
    if (__checkPatterns()) {

        $.ajax({
        type: "post",
        url: "/buildTypes/add",
        data: {projectID: prID, projectName: prName,
            buildTypeID: btID, buildTypeName: btName,
            patterns: $("#edit-bt-patterns").val().trim()}
    }).done(function (data) {
            $("#edit-bt-form").css("display", "none");
            $("tbody.bt-item-saved").html(data);
            __clearPatternForm();
        });
    }
    return false;
}

function removeBuildType(btID) {
    $.ajax({
        type: "post",
        url: "/buildTypes/remove",
        data: {buildTypeID: btID}
    }).done(function (data) {
            $("tbody.bt-item-saved").html(data);
            $("#edit-bt-form").css("display", "none");
        });
}

function changeMonitoring(me, btID) {
    var startMonitor = $(me).attr("class") == "start-monitor";
    $.ajax({
        type: "post",
        url: "/buildTypes/changeMonitoring",
        data: {buildTypeID: btID, start: startMonitor}
    }).done(function () {
            if (startMonitor) {
                $(me).removeClass("start-monitor").addClass("stop-monitor");
            } else {
                $(me).removeClass("stop-monitor").addClass("start-monitor");
            }
    });
}



$(function() {
    $("div.bt-item-list").bind("click", function(){
        var id = $(this).attr("id");
        $.ajax({
            type: "get",
            url: "/buildTypes/get",
            data: {projectID: id}
        }).done(function (data) {
                $("div.bt-list").html(data);
            });
    });

    $("#projectFilter").keyup(function () {
        var filter = $(this).val();
        $(".tc-projects").children().each(function () {
            if ($(this).text().search(new RegExp(filter, "i")) != -1) {
                $(this).removeClass("hidden");

            } else {
                $(this).addClass("hidden");
            }
        });
    });
});