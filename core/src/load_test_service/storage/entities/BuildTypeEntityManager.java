package load_test_service.storage.entities;

import jetbrains.exodus.database.Entity;
import jetbrains.exodus.database.EntityIterable;
import jetbrains.exodus.database.StoreTransaction;
import jetbrains.exodus.database.impl.bindings.StringBinding;
import load_test_service.api.model.BuildType;
import load_test_service.api.model.TestBuild;
import load_test_service.storage.binding.BuildBinding;
import load_test_service.storage.binding.BuildTypeBinding;
import load_test_service.storage.binding.CollectionConverter;
import load_test_service.storage.queries.BuildTypeQuery;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class BuildTypeEntityManager implements BuildTypeQuery {
    public static final String ENTITY_TYPE = "BuildType";

//  PROPERTIES
    public static final String PROPERTY_BT_ID = "buildTypeID";
    public static final String PROPERTY_BT_PROJECT_ID ="projectID";
    public static final String PROPERTY_BT_MONITORED = "monitored";
    public static final String PROPERTY_BT_PATTERNS = "patterns";

//    BLOBS
    public static final String BLOB_BT_NAME = "buildType";
    public static final String BLOB_BT_PROJECT_NAME = "projectName";
    public static final String BLOB_BT_LAST_BUILD_ID ="last";

//    LINKS
    public static final String LINK_TO_BUILDS = "bt-builds";


    public static ReentrantLock blobUpdateLock = new ReentrantLock();

    @Override
    public void addBuildType(@NotNull final StoreTransaction txn, @NotNull final BuildType buildType) {
        BuildTypeBinding.createEntity(buildType, txn);
    }

    @Override
    @Nullable
    public BuildType getBuildType(@NotNull StoreTransaction txn, @NotNull String btID) {
        Entity btEntity = getBuildTypeEntity(txn, btID);
        return btEntity == null ? null : BuildTypeBinding.entityToBuildType(btEntity);
    }

    @Override
    public void removeBuildType(@NotNull StoreTransaction txn, @NotNull String btID) {
        blobUpdateLock.tryLock();
        try {
            final Entity bt = getBuildTypeEntity(txn, btID);
            if (bt == null) return;
            EntityIterable builds = bt.getLinks(LINK_TO_BUILDS);
            if (!builds.isEmpty()) {
                for (Entity build : builds) {
                    for (Entity dependency : build.getLinks(BuildEntityManager.LINK_TO_DEPENDENCIES)) {
                        try {
                            dependency.delete();
                        } catch (Throwable th) {
                            System.out.println(th);
                            th.printStackTrace();
                        }
                    }
                    build.delete();
                }
            }
        } finally {
            blobUpdateLock.unlock();
        }

    }

    @Override
    @NotNull
    public List<BuildType> getAllBuildTypes(@NotNull StoreTransaction txn) {
        final EntityIterable btEntities = txn.getAll(ENTITY_TYPE);
        if (btEntities.isEmpty())
            return Collections.emptyList();

        List<BuildType> bts = new ArrayList<>();
        for (Entity btEntity : btEntities) {
            bts.add(BuildTypeBinding.entityToBuildType(btEntity));
        }
        return bts;
    }


    @Override
    public void updateMonitoringStatus(@NotNull StoreTransaction txn, @NotNull String btId, boolean status) {
        Entity build = getBuildTypeEntity(txn, btId);
        if (build != null) {
            build.setProperty(PROPERTY_BT_MONITORED, status);
        }
    }

    @Override
    public void updatePatterns(@NotNull StoreTransaction txn, @NotNull String btId, @NotNull List<String> patterns) {
        Entity build = getBuildTypeEntity(txn, btId);
        if (build != null) {
            build.setBlob(PROPERTY_BT_PATTERNS, CollectionConverter.toInputStream(StringBinding.BINDING, patterns));
        }
    }

    @Override
    @NotNull
    public List<TestBuild> getAllBuilds(@NotNull StoreTransaction txn, @NotNull String bt) {
        Entity entity = getBuildTypeEntity(txn, bt);
        if (entity == null) return Collections.emptyList();

        final EntityIterable entBuilds = entity.getLinks(LINK_TO_BUILDS);
        if (entBuilds.isEmpty()) return Collections.emptyList();

        List<TestBuild> builds = new ArrayList<>();
        for (Entity entBuild : entBuilds) {
            TestBuild build = BuildBinding.entityToTestBuild(entBuild);
            builds.add(build);
        }
        return builds;
    }

    public EntityIterable getAllBuildEntities(@NotNull Entity entity) {
        return entity.getLinks(LINK_TO_BUILDS);
    }

    @Override
    public void addBuildEntity(@NotNull StoreTransaction txn, @NotNull TestBuild build) {
        Entity bt = getBuildTypeEntity(txn, build.getID().getBuildTypeID());
        if (bt == null) return;

        bt.setBlobString(BLOB_BT_LAST_BUILD_ID, build.getID().getBuildID());

        Entity testEntity = BuildBinding.createEntity(txn, build);

        Collection<String> artifacts = build.getArtifacts();
        if (!artifacts.isEmpty())
            testEntity.setBlob(BuildEntityManager.BLOB_ARTIFACT_NAMES, CollectionConverter.<String>toInputStream(StringBinding.BINDING, artifacts));

        bt.addLink(LINK_TO_BUILDS, testEntity);
        testEntity.addLink(BuildEntityManager.LINK_BUILD_TO_BUILD_TYPE, bt);
    }

    @Nullable
    public Entity getBuildTypeEntity(@NotNull StoreTransaction txn, @NotNull String btID) {
        EntityIterable it = txn.find(ENTITY_TYPE, PROPERTY_BT_ID, btID);
        return it.getFirst();
    }
}
