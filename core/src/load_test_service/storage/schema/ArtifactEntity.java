package load_test_service.storage.schema;

/**
 * Created by Yuliya.Torhan on 5/8/14.
 */
public final class ArtifactEntity {
    private ArtifactEntity(){}

    public static final String TYPE = "testBuild.artifact";

    public static enum Property {
        NAME, IS_PROCESSED
    }

    public static enum Blob {
        CONTENT
    }

    public static enum Link {
        TO_STAT_SAMPLES_VALUES,
    }
}
