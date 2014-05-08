package load_test_service.storage.schema;

import org.jetbrains.annotations.NotNull;

/**
 * HELPER, not used
 */
public enum EntityTypes {
    BUILD_TYPE("buildType") {
        //  PROPERTIES
        public static final String PROPERTY_ID = "btID";
        public static final String PROPERTY_PROJECT_ID ="projectID";
        public static final String PROPERTY_MONITORED = "monitored";

        //    BLOBS
        public static final String BLOB_NAME = "name";
        public static final String BLOB_PROJECT_NAME = "projectName";
        public static final String BLOB_PATTERNS = "patterns";
        public static final String BLOB_LAST_BUILD_ID ="last";

        //    LINKS
        public static final String LINK_TO_BUILDS = "builds";
    },


    TEST_BUILD("testBuild") {
        //  PROPERTIES
        public final String PROPERTY_ID = "buildID";
        public final String PROPERTY_STATUS = "status";
        public final String PROPERTY_FINISH_DATE = "finish";

        //  BLOBS
        public final String BLOB_BUILD_NUMBER = "number";
        public final String BLOB_ARTIFACT_NAMES = "artifactNames"; // is need?

        //  LINKS
        public final String LINK_TO_BUILD_TYPE = "buildType";
        public final String LINK_TO_DEPENDENCIES = "dependencies";
        public final String LINK_TO_ARTIFACTS = "artifacts";
    },


    TEST_DEPENDENCY("testBuild.dependency") { // like a build but with additional properties, and has no some of properties like dependencies
        //  PROPERTIES
        public static final String PROPERTY_ID = "buildID";
        public static final String PROPERTY_STATUS = "status";
        public static final String PROPERTY_FINISH_DATE = "finish";

        //  BLOBS
        public static final String BLOB_BUILD_NUMBER = "number";
        public static final String BLOB_CHANGES = "changes";

        //  LINKS
        //public static final String LINK_TO_TEST_BUILD = "testBuild";
    },


    TEST_ARTIFACT("testBuild.artifact"){
        //  PROPERTIES
        public static final String PROPERTY_NAME = "name";
        public static final String PROPERTY_IS_PROCESSED = "processed";

        //  BLOBS
        public static final String BLOB_CONTENT = "content";

        //  LINKS
        public static final String LINK_TO_STAT_SAMPLES_VALUES = "statSampleValues";
    },


    TEST_STATISTIC_SAMPLE_VALUE("testBuild.stat.sample"){
        //  PROPERTIES
        public static final String PROPERTY_THREAD_GROUP = "threadGroup"; // if not set used empty string
        public static final String PROPERTY_SAMPLE_NAME = "sampleName"; //
        public static final String PROPERTY_METRIC = "metric"; //

        //  BLOBS
        public static final String BLOB_VALUE = "value";
    };

    public final String type;
    EntityTypes(@NotNull String type) {
        this.type = type;
    }
}
