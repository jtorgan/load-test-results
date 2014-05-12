package load_test_service.storage.schema;

/**
 * Created by Yuliya.Torhan on 5/8/14.
 */
public final class BuildEntity {
    private BuildEntity(){}

    public static final String TYPE = "testBuild";

    public static enum Property {
        BUILD_ID, BUILD_TYPE_ID, STATUS, FINISH_DATE
    }

    public static enum Blob {
        NUMBER, ARTIFACT_NAMES, BUILD_TYPE_NAME
    }

    public static enum Link {
        TO_BUILD_TYPE, TO_DEPENDENCY, TO_ARTIFACT, TO_SAMPLE_VALUE
    }
}
