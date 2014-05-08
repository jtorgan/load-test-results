package load_test_service.api.model;

import org.jetbrains.annotations.NotNull;

public class BuildID implements Comparable<BuildID>{
    private final String buildID;
    private final String buildTypeID;

    public BuildID(@NotNull final String buildTypeID, @NotNull final String buildID) {
        this.buildID = buildID;
        this.buildTypeID = buildTypeID;
    }

    public String getBuildID() {
        return buildID;
    }

    public String getBuildTypeID() {
        return buildTypeID;
    }


    @Override
    public int compareTo(@NotNull BuildID o) {
        if (o == this) return 0;
        return buildID.compareTo(o.buildID);
    }

    @Override
    public int hashCode() {
// todo: replace temp hashcode; 
// todo: set everyplace buildId as long type like at TeamCity
        int result = 1;
        result = 17 * result + buildTypeID.length();
        result = 17 * result + Integer.valueOf(buildID) ;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (obj instanceof BuildID) {
            BuildID objID = (BuildID) obj;
            return buildTypeID.equals(objID.buildTypeID) && buildID.equals(objID.buildID);
        }
        return false;
    }
}
