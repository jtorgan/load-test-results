package load_test_service.storage.schema;

/**
 * Created by Yuliya.Torhan on 5/8/14.
 */
public final class DependencyEntity {
    private DependencyEntity(){}

    public static final String TYPE = "testBuild.dependency";

    public static enum Property {
        BUILD_ID, BUILD_TYPE_ID, STATUS, FINISH_DATE
    }

    public static enum Blob {
        NUMBER, CHANGES, BUILD_TYPE_NAME
    }

    public static enum Link {
        TO_TEST_BUILD
    }
}
