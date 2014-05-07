package load_service.web.controllers;

import load_test_service.api.LoadService;
import load_test_service.api.model.BuildID;
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
        return "buildDependencies";
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
}