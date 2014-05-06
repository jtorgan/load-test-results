package load_test_service;

import load_test_service.teamcity.RESTCommandImpl;
import load_test_service.teamcity.RESTHttpClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProjectTree {
    public static final ProjectTree ROOT = new ProjectTree("_Root", "Root");

    private final String projectID;
    private final String projectName;
    private List<ProjectTree> children;

    public ProjectTree(String projectID, String projectName) {
        this.projectID = projectID;
        this.projectName = projectName;
    }

    @NotNull
    public String getID() {
        return projectID;
    }

    @NotNull
    public String getName() {
        return projectName;
    }

    @Nullable
    public synchronized List<ProjectTree> getSubProjects() {
        return children;
    }

    public boolean hasSubProjects() {
        return children != null && !children.isEmpty();
    }

    public  synchronized void loadChildren(RESTHttpClient client) {
        Map<String, String> subProjects = RESTCommandImpl.GET_SUB_PROJECTS.execute(client, projectID);
        if (!subProjects.isEmpty()) {
            children = new ArrayList<>(subProjects.size());
            for (Map.Entry<String, String> sub : subProjects.entrySet()) {
                ProjectTree child = new ProjectTree(sub.getKey(), sub.getValue());
                children.add(child);
                child.loadChildren(client);
            }
        }
    }
}
