package load_test_service.api.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TestBuild extends BaseBuildInfo {
    private List<DependencyBuild> dependencyList;

    private Collection<String> artifacts;

    public TestBuild(@NotNull BuildID id) {
        super(id);
    }

    @Nullable
    public Collection<String> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(Collection<String> artifacts) {
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
}
