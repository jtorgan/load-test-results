function loadCharts(sampleID, statData, srtData, rpsData) {
    createStatChart(sampleID, statData);
    createSRTChart(sampleID, srtData);
    createRPSChart(sampleID, rpsData);
}

function createStatChart(id, data, width, height) {
    nv.addGraph(function() {
        var margin = {top: 30, right: 50, bottom: 50, left: 80};

        var builds = data[0].values.map(function(d) {return d.x;});

        var chart = nv.models.lineChart()
                .width(width - margin.right - margin.left)
                .height(height - margin.top - margin.bottom)
                .margin(margin)
                .x(function(d, id) { return id })
                .color(d3.scale.category10().range())
                .useInteractiveGuideline(true)
            ;
        chart.xAxis
            .axisLabel('Build ID')
            .tickFormat(function(id) {
                return builds[id];
            })
        ;

        chart.yAxis
            .axisLabel('Time (ms)')
            .axisLabelDistance(40);

        chart.forceY([0]);
        d3.select('#statSVG' + id + ' svg')
            .datum(data)
            .transition(0)
            .call(chart)
        ;
        nv.utils.windowResize(chart.update);
        return chart;
    });
}

function createSRTChart(id, data, width, height) {
    nv.addGraph(function() {
        var margin = {top: 30, right: 50, bottom: 50, left: 80};

        var chart = nv.models.lineChart()
                .width(width - margin.right - margin.left)
                .height(height - margin.top - margin.bottom)
                .margin(margin)
                .showLegend(false)
                .color(d3.scale.category10().range())
                .useInteractiveGuideline(true)
            ;
        chart.xAxis
            .showMaxMin(false)
            .tickFormat(function(d) { return d3.time.format('%M:%S')(new Date(d)) })
        ;

        chart.yAxis
            .axisLabel('Time (ms)')
            .axisLabelDistance(40);

        chart.forceY([0]);

        chart.tooltipContent(function(key, x, y, e, graph) {
            return '<p> time: ' + y + ' ; value: ' + x + '</p>';
        });

        d3.select('#srtSVG' + id + ' svg')
            .datum(data)
            .transition(0)
            .call(chart)
        ;
        nv.utils.windowResize(chart.update);
        return chart;
    });
}

function createRPSChart(id, data, width, height) {
    nv.addGraph(function() {
        var margin = {top: 30, right: 50, bottom: 50, left: 80};

        var chart = nv.models.lineChart()
                .width(width - margin.right - margin.left)
                .height(height - margin.top - margin.bottom)
                .margin(margin)
                .showLegend(false)
                .color(d3.scale.category10().range())
                .useInteractiveGuideline(true)
            ;
        chart.xAxis
            .showMaxMin(false)
            .tickFormat(function(d) { return d3.time.format('%M:%S')(new Date(d)) })
        ;

        chart.yAxis
            .axisLabel('Time (ms)')
            .axisLabelDistance(40);

        chart.forceY([0]);

        chart.tooltipContent(function(key, x, y, e, graph) {
            return '<p> time: ' + y + ' ; value: ' + x + '</p>';
        });

        d3.select('#rpsSVG' + id + ' svg')
            .datum(data)
            .transition(0)
            .call(chart)
        ;
        nv.utils.windowResize(chart.update);
        return chart;
    });
}