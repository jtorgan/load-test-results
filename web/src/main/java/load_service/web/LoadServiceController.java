package load_service.web;

import load_test_service.api.LoadService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/")
public class LoadServiceController {
    private final LoadService service;

    public LoadServiceController(@NotNull final LoadService service) {
        this.service = service;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String projects(ModelMap model, HttpServletResponse response) {
        model.addAttribute("projects", service.getTCProjectTree());
        model.addAttribute("saved_bt", service.getAllBuildTypes());

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        response.setDateHeader("Expires", 0);

        return "projects";
    }
}


