package load_service.web.controllers;

import load_test_service.api.LoadService;
import load_test_service.api.model.BuildID;
import load_test_service.api.model.TestBuild;
import load_test_service.api.statistic.StatisticProperties;
import load_test_service.api.statistic.metrics.Metric;
import load_test_service.statistic.BaseMetrics;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Yuliya.Torhan on 5/5/14.
 */
@Controller
@RequestMapping("/statistic")
public class StatisticController {
    private final LoadService service;

    public StatisticController(@NotNull final LoadService service) {
        this.service = service;
    }


    @RequestMapping(value = "/statForm", method = RequestMethod.GET)
    public String getStatForm(@RequestParam(value = "buildTypeID", required = true) String buildTypeID, @RequestParam(value = "buildID", required = true) String buildID, ModelMap model) {
        BuildID id = new BuildID(buildTypeID, buildID);
        TestBuild build = service.getBuild(id);
        if (build != null) {
            model.addAttribute("build", build);
            model.addAttribute("artifacts", service.getArtifactsWithoutStat(id));
            model.addAttribute("metrics", Arrays.asList(service.getBaseMetrics()));
        }
        return "buildStatForm";
    }


    @RequestMapping(value = "/artWithStat", method = RequestMethod.GET)
    public String getArtifactsWithStat(@RequestParam(value = "buildTypeID", required = true) String buildTypeID, @RequestParam(value = "buildID", required = true) String buildID, ModelMap model) {
        BuildID id = new BuildID(buildTypeID, buildID);
        TestBuild build = service.getBuild(id);
        if (build != null) {
            model.addAttribute("build", build);
            model.addAttribute("artifacts", service.getArtifactsWithStat(id));
        }
        return "buildArtifactsWithStat";
    }

    @RequestMapping(value = "/calculate", method = RequestMethod.POST)
    public ResponseEntity calculateStatistic(@RequestParam(value = "buildTypeID", required = true) String buildTypeID,
                            @RequestParam(value = "buildID", required = true) String buildID,
                            @RequestParam(value = "metrics", required = true) String[] metrics,
                            @RequestParam(value = "artifact", required = true) String artifact,
                            @RequestParam(value = "threadGroup", required = true) boolean threadGroup,
                            @RequestParam(value = "total", required = true) boolean total) {
        BuildID id = new BuildID(buildTypeID, buildID);
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

        if (service.countStatistic(id, artifact, props)) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/showStat", method = RequestMethod.GET)
    public String showStat(@RequestParam(value = "buildTypeID", required = true) String buildTypeID, @RequestParam(value = "buildID", required = true) String buildID,
                           @RequestParam(value = "artifact", required = true) String artifact,
                           ModelMap model) {
		//todo: show page with statistic
        BuildID id = new BuildID(buildTypeID, buildID);
        return "statistic";
    }
}
