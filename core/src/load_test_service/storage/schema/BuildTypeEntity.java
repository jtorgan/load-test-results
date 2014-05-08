package load_test_service.storage.schema;

/**
 * Created by Yuliya.Torhan on 5/8/14.
 */
public final class BuildTypeEntity {
    private BuildTypeEntity(){}

    public static final String TYPE = "buildType";

    public static enum Property {
        ID, PROJECT_ID, IS_MONITORED
    }

    public static enum Blob {
        BUILD_TYPE_NAME, PROJECT_NAME, PATTERNS, LAST_MONITORED_BUILD_ID
    }

    public static enum Link {
        TO_BUILDS,
        TO_SAMPLES
    }
}
