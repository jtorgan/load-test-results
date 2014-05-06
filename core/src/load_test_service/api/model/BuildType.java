package load_test_service.api.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class BuildType implements Comparable<BuildType>{
    private final String id;

    private String name;
    private String projectID;
    private String projectName;

    private volatile String lastBuildID = "-1";
    private volatile boolean isMonitored = false;
    private volatile Collection<String> patterns;

    public BuildType(@NotNull final String id) {
        this.id = id;
    }

    @NotNull
    public String getID() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getProjectID() {
        return projectID;
    }

    @NotNull
    public String getProjectName() {
        return projectName;
    }

    @NotNull
    public String getLastBuildID() {
        return lastBuildID == null ? "-1" : lastBuildID;
    }

    @Nullable
    public Collection<String> getPatterns() {
        return patterns;
    }

    public boolean isMonitored() {
        return isMonitored;
    }




    public void setName(@NotNull String name) {
        this.name = name;
    }

    public void setProjectID(@NotNull String projectID) {
        this.projectID = projectID;
    }

    public void setProjectName(@NotNull String projectName) {
        this.projectName = projectName;
    }

    public void setMonitored(boolean isMonitored) {
        this.isMonitored = isMonitored;
    }

    public void setLastBuildID(@NotNull String lastBuildID) {
        this.lastBuildID = lastBuildID;
    }

    public void setPatterns(@NotNull Collection<String> patterns) {
        this.patterns = patterns;
    }



    @Override
    public int compareTo(@NotNull BuildType o) {
        return this.id.compareTo(o.id);
    }

}