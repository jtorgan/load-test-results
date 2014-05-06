package load_service.web.controllers;

import load_test_service.api.LoadService;
import load_test_service.api.exeptions.FileFormatException;
import load_test_service.api.model.BuildID;
import load_test_service.api.model.TestBuild;
import load_test_service.api.statistic.StatisticProperties;
import load_test_service.api.statistic.metrics.Metric;
import load_test_service.statistic.BaseMetrics;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Yuliya.Torhan on 5/5/14.
 */
@Controller
@RequestMapping("/builds")
public class BuildController {
    private final LoadService service;

    public BuildController(@NotNull final LoadService service) {
        this.service = service;
    }


    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public String getBuildInfo(@RequestParam(value = "buildTypeID", required = true) String buildTypeID, @RequestParam(value = "buildID", required = true) String buildID, ModelMap model) {
        model.addAttribute("build", service.getBuild(new BuildID(buildTypeID, buildID)));
        return "buildInfo";
    }

    @RequestMapping(value = "/artifacts", method = RequestMethod.GET)
    public String getArtifacts(@RequestParam(value = "buildTypeID", required = true) String buildTypeID, @RequestParam(value = "buildID", required = true) String buildID, ModelMap model) {
        TestBuild build = service.getBuild(new BuildID(buildTypeID, buildID));
        model.addAttribute("build", build);
        model.addAttribute("artifacts", build.getArtifacts());
        model.addAttribute("metrics", Arrays.asList(service.getBaseMetrics()));
        return "artifacts";
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public @ResponseBody
    void removeBuild(@RequestParam(value = "buildTypeID", required = true) String buildTypeID, @RequestParam(value = "buildID", required = true) String buildID) {
        service.removeBuild(new BuildID(buildTypeID, buildID));
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void download(@RequestParam(value = "path", required = true) String name, @RequestParam(value = "buildTypeID", required = true) String buildTypeID,
                         @RequestParam(value = "buildID", required = true) String buildID, HttpServletResponse response) {
        try {
            response.addHeader("content-disposition", "attachment; filename='" + name +"'");
            OutputStream outputStream = response.getOutputStream();
            InputStream inputStream = service.loadArtifact(new BuildID(buildTypeID, buildID), name);
            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
            response.flushBuffer();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @RequestMapping(value = "/calculateStatistic", method = RequestMethod.POST)
    public @ResponseBody void calculateStatistic(@RequestParam(value = "buildTypeID", required = true) String buildTypeID,
                                                 @RequestParam(value = "buildID", required = true) String buildID,
                                                 @RequestParam(value = "metrics", required = true) String[] metrics,
                                                 @RequestParam(value = "artifact", required = true) String artifact,
                                                 @RequestParam(value = "threadGroup", required = true) boolean threadGroup,
                                                 @RequestParam(value = "total", required = true) boolean total) {
        BuildID id = new BuildID(buildID, buildTypeID);
        StatisticProperties props = new StatisticProperties();
        List<Metric> metricList = new ArrayList<Metric>();
        for (String metric : metrics) {
            Metric m = BaseMetrics.valueOf(metric);
            if (m == BaseMetrics.RESPONSE_CODE) {
                props.calculateResponseCodes(true); continue;
            }
            if (m != null)
                metricList.add(m);
        }
        props.setMetrics(metricList.toArray(new Metric[metricList.size()]));
        props.useTestGroups(threadGroup);
        props.calculateTotal(total);

//      todo:
        try {
            service.countStatistic(id, artifact, props);
        } catch (FileFormatException e) {
            e.printStackTrace();
        }
    }
}