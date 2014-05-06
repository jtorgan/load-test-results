package load_test_service.api.model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class DependencyBuild extends BaseBuildInfo {
    private String name;
    private Collection<Change> changes = new ArrayList<>();

    public DependencyBuild(@NotNull BuildID id) {
        super(id);
    }

    public Collection<Change> getChanges() {
        return changes;
    }

    public void setChanges(Collection<Change> changes) {
        this.changes = changes;
    }

    public void addChange(@NotNull Change change) {
        changes.add(change);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
