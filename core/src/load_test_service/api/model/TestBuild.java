package load_test_service.api.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TestBuild extends BaseBuildInfo {
    private List<DependencyBuild> dependencyList;

    private Map<String, Boolean> artifacts;

    public TestBuild(@NotNull BuildID id) {
        super(id);
    }

    @Nullable
    public Map<String, Boolean> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(Map<String, Boolean> artifacts) {
        this.artifacts = artifacts;
    }

    @Nullable
    public List<DependencyBuild> getDependencyList() {
        return dependencyList;
    }

    public void addDependency(@NotNull DependencyBuild dependency) {
        if (dependencyList == null) {
            dependencyList = new ArrayList<>();
        }
        dependencyList.add(dependency);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(super.toString());
        result.append("\nTest build:\n");
        if (artifacts != null && !artifacts.isEmpty())
            result.append("artifacts: ").append(Arrays.toString(artifacts.keySet().toArray()));
        if (dependencyList != null && !dependencyList.isEmpty()) {
            result.append("dependencyCount: ").append(dependencyList.size());
            result.append("dependencyNames: [");
            for (DependencyBuild dep : dependencyList)
                result.append(dep.getName()).append(", ");
            result.append("]");
        }
        return result.toString();
    }

    public boolean hasStatistic() {
        return artifacts != null && artifacts.values().contains(true);
    }

    public void addArtifact(@NotNull String artifactName, boolean isProcessed) {
        if (artifacts == null) {
            artifacts = new HashMap<>();
        }
        artifacts.put(artifactName, isProcessed);
    }
}
