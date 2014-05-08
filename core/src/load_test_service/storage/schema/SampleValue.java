package load_test_service.storage.schema;


public final class SampleValue {
    private SampleValue(){}

    public static final String TYPE = "testBuild.stat.sample.value";

    public static enum Property {
        BUILD_ID,
        METRIC, // Min, Max, Response codes, Average, 90 %line
        SUB_METRIC //used only for response codes: (http) 200, 404 or  (result type, type of exception) OK, InternalError, FileTooBig
    }

    public static enum Blob {
        VALUE
    }

    public static enum Link {
        TO_SAMPLE,
        TO_BUILD
    }
}
