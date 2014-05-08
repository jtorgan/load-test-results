package load_test_service.storage.schema;

/**
 * Created by Yuliya.Torhan on 5/8/14.
 */
public class SampleEntity {
    private SampleEntity(){}

    public static final String TYPE = "testBuild.stat.sample";

    public static enum Property {
        THREAD_GROUP,
        SAMPLE_NAME,
    }

    public static enum Blob {
    }

    public static enum Link {
        TO_SAMPLE_VALUE,
        TO_BUILD_TYPE
    }
}
