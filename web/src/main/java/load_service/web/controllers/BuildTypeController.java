package load_service.web.controllers;

import load_test_service.api.LoadService;
import load_test_service.api.model.BuildType;
import load_test_service.api.model.TestBuild;
import load_test_service.teamcity.RESTCommandImpl;
import load_test_service.teamcity.RESTHttpClient;
import load_test_service.teamcity.exceptions.TCException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/buildTypes")
public class BuildTypeController {
    private final LoadService service;

    public BuildTypeController(@NotNull final LoadService service) {
        this.service = service;
    }



    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public String get(@RequestParam(value = "projectID", required = true) String projectID, ModelMap modelMap) {
        RESTHttpClient client = RESTHttpClient.newDefaultInstance();
        modelMap.addAttribute("projectID", projectID);
        try {
            modelMap.addAttribute("projectName", RESTCommandImpl.GET_PROJECT_NAME.execute(client, projectID));
            modelMap.addAttribute("bts", RESTCommandImpl.GET_BUILD_TYPES.execute(client, projectID));
        } catch (TCException e) {
            e.printStackTrace(); // todo: show in UI
        }
        return "buildTypes";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String add(@RequestParam(value = "projectID", required = true) String projectID, @RequestParam(value = "projectName", required = true) String projectName,
                               @RequestParam(value = "buildTypeID", required = true) String buildTypeID, @RequestParam(value = "buildTypeName", required = true) String buildTypeName,
                               @RequestParam(value = "patterns", required = true) String patterns,
                               ModelMap model) {
        BuildType bt = new BuildType(buildTypeID);
        bt.setProjectName(projectName);
        bt.setProjectID(projectID);
        bt.setName(buildTypeName);
        if (!patterns.isEmpty()) {
            String[] regex = patterns.split("\n");
            bt.setPatterns(Arrays.asList(regex));
        }
        service.addBuildType(bt);
        model.addAttribute("saved_bt", service.getAllBuildTypes());
        return "savedBuildTypes";
    }

    @RequestMapping(value = "/savedProjects", method = RequestMethod.GET)
    public String savedProjects(ModelMap model) {
        model.addAttribute("saved_bt", service.getAllBuildTypes());
        return "savedBuildTypes";
    }

    /**
     * Remove build configuration, all builds, its artifacts, statistic values
     * @param buildTypeID
     * @param model
     * @return
     */
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public String remove(@RequestParam(value = "buildTypeID", required = true) String buildTypeID, ModelMap model) {
        service.removeBuildType(buildTypeID);
        model.addAttribute("saved_bt", service.getAllBuildTypes());
        return "savedBuildTypes";
    }

    /**
     * Change monitoring status (start / stop)
     * @param buildTypeID
     * @param start
     */
    @RequestMapping(value = "/changeMonitoring", method = RequestMethod.POST)
    public @ResponseBody void changeMonitoring(@RequestParam(value = "buildTypeID", required = true) String buildTypeID, @RequestParam(value = "start", required = true) boolean start) {
        if (start) {
            service.startMonitorBuildType(buildTypeID);
        } else {
            service.stopMonitorBuildType(buildTypeID);
        }
    }

    /**
     * Get all received from TC builds of build configuration
     * @param buildTypeID
     * @param model
     * @return
     */
    @RequestMapping(value = "/builds", method = RequestMethod.GET)
    public String getBuilds(@RequestParam(value = "buildTypeID", required = true) String buildTypeID, ModelMap model) {
        List<TestBuild> builds = service.getAllBuilds(buildTypeID);
        model.addAttribute("builds", builds);
        model.addAttribute("count", builds.size());
        model.addAttribute("buildType", service.getBuildType(buildTypeID));
        return "builds";
    }

    /**
     * Set patterns for artifact to download
     * @param buildTypeID
     * @param patterns
     */
    @RequestMapping(value = "/setPatterns", method = RequestMethod.POST)
    public @ResponseBody void setPatterns(@RequestParam(value = "buildTypeID", required = true) String buildTypeID, @RequestParam(value = "patterns", required = true) String patterns) {
        String[] regex = patterns.split("\n");
        service.setArtifactPatterns(buildTypeID, Arrays.asList(regex));
    }
}
