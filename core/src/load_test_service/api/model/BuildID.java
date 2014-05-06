package load_test_service.api.model;

import org.jetbrains.annotations.NotNull;

public class BuildID implements Comparable<BuildID>{
    private final String buildID;
    private final String btID;

    public BuildID(@NotNull final String btID, @NotNull final String buildID) {
        this.buildID = buildID;
        this.btID = btID;
    }

    public String getBuildID() {
        return buildID;
    }

    public String getBuildTypeID() {
        return btID;
    }


    @Override
    public int compareTo(@NotNull BuildID o) {
        if (o == this) return 0;
        return buildID.compareTo(o.buildID);
    }
}
