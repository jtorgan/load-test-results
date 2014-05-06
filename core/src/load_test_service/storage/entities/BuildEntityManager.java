package load_test_service.storage.entities;

import jetbrains.exodus.database.Entity;
import jetbrains.exodus.database.EntityIterable;
import jetbrains.exodus.database.StoreTransaction;
import jetbrains.exodus.database.impl.bindings.StringBinding;
import load_test_service.api.model.BuildID;
import load_test_service.api.model.TestBuild;
import load_test_service.storage.binding.BuildBinding;
import load_test_service.storage.binding.CollectionConverter;
import load_test_service.storage.queries.BuildQuery;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.Collection;

/**
 * Created by Yuliya.Torhan on 4/23/14.
 */
public class BuildEntityManager implements BuildQuery {
//  ENTITY TYPES
    public static final String TEST_ENTITY_TYPE = "TestBuild";
    public static final String DEP_ENTITY_TYPE = "DependencyBuild";

//  PROPERTIES
    public static final String PROPERTY_BUILD_ID = "buildID";
    public static final String PROPERTY_BUILD_STATUS = "status";
    public static final String PROPERTY_BUILD_FINISH_DATE = "finish";
    public static final String PROPERTY_BUILD_CHANGES = "changes";

//    BLOBS
    public static final String BLOB_BUILD_NUMBER = "buildNumber";
    public static final String BLOB_ARTIFACT_NAMES = "artifactNames";

//  LINKS
    public static final String LINK_BUILD_TO_BUILD_TYPE = "build-bt";
    public static final String LINK_TO_DEPENDENCIES = "build-deps";
    public static final String LINK_DEPENDENCY_TO_BUILD = "dep-build";

    @Override
    @Nullable
    public TestBuild getBuild(@NotNull StoreTransaction txn, @NotNull BuildID buildID) {
        Entity entity = getBuildEntity(txn, buildID);
        return entity == null ? null : BuildBinding.entityToTestBuild(entity);
    }

    @Override
    public void removeBuildDependencies(@NotNull Entity build) {
        Entity bt = build.getLink(LINK_BUILD_TO_BUILD_TYPE);
        if (bt != null)
            bt.deleteLink(BuildTypeEntityManager.LINK_TO_BUILDS, build);
        for (Entity dependency : build.getLinks(LINK_TO_DEPENDENCIES)) {
            dependency.delete();
        }
    }

    @Override
    public Collection<String> getBuildArtifactNames(@NotNull StoreTransaction txn, @NotNull BuildID buildID) {
        Entity build = getBuildEntity(txn, buildID);
        if (build == null) return null;
        InputStream stream = build.getBlob(BLOB_ARTIFACT_NAMES);
        if (stream == null) return null;
        return CollectionConverter.fromInputStream(stream, StringBinding.BINDING);
    }

    @Override
    public InputStream loadArtifact(@NotNull StoreTransaction txn, @NotNull BuildID buildID, @NotNull String artifactName) {
        Entity build = getBuildEntity(txn, buildID);
        if (build == null) return null;
        return build.getBlob(artifactName);
    }

    /**
     * TeamCity Analyzer INNER query
     */
    @Override
    public void addBuildArtifact(@NotNull StoreTransaction txn, @NotNull BuildID buildID, @NotNull String artifactName, @NotNull InputStream artifact) {
        Entity entity = getBuildEntity(txn, buildID);
        if (entity != null)
            entity.setBlob(artifactName, artifact);
        /*                            final StoreTransaction txn = executor.beginTransaction();
                            while(true) {
                                try {
                                    store.addBuildArtifact(txn, build.getID(), artifact.getCharKey(), stream);
                                } catch(Throwable t) {
                                    txn.abort();
                                    t.printStackTrace();
                                    break;
                                }
                                if (txn.flush()) {
                                    break;
                                }
                            }*/
    }

    @Nullable
    public Entity getBuildEntity(StoreTransaction txn, BuildID buildID) {
        EntityIterable builds = txn.find(TEST_ENTITY_TYPE, PROPERTY_BUILD_ID, buildID.getBuildID());
        return builds.getFirst();
    }
}
