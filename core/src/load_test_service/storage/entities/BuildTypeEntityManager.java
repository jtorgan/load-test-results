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
import load_test_service.storage.schema.BuildEntity;
import load_test_service.storage.schema.BuildTypeEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class BuildTypeEntityManager {

    public static ReentrantLock blobUpdateLock = new ReentrantLock();

    public void addBuildType(@NotNull final StoreTransaction txn, @NotNull final BuildType buildType) {
        BuildTypeBinding.createEntity(buildType, txn);
    }

    @Nullable
    public BuildType getBuildType(@NotNull StoreTransaction txn, @NotNull String btID) {
        Entity btEntity = getBuildTypeEntity(txn, btID);
        return btEntity == null ? null : BuildTypeBinding.entityToBuildType(btEntity);
    }

//  TODO: check remove samples, all builds ( => remove all statistic values; artifacts; dependencies)
    public void removeBuildType(@NotNull StoreTransaction txn, @NotNull String btID) {
        blobUpdateLock.tryLock();
        try {
            final Entity bt = getBuildTypeEntity(txn, btID);
            if (bt == null) return;
            EntityIterable builds = bt.getLinks(BuildTypeEntity.Link.TO_BUILDS.name());
            if (!builds.isEmpty()) {
                for (Entity build : builds) {
                    for (Entity dependency : build.getLinks(BuildEntity.Link.TO_DEPENDENCY.name())) {
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

    @NotNull
    public List<BuildType> getAllBuildTypes(@NotNull StoreTransaction txn) {
        final EntityIterable btEntities = txn.getAll(BuildTypeEntity.TYPE);
        if (btEntities.isEmpty())
            return Collections.emptyList();

        List<BuildType> bts = new ArrayList<>();
        for (Entity btEntity : btEntities) {
            bts.add(BuildTypeBinding.entityToBuildType(btEntity));
        }
        return bts;
    }


    @NotNull
    public List<TestBuild> getAllBuilds(@NotNull StoreTransaction txn, @NotNull String bt) {
        Entity entity = getBuildTypeEntity(txn, bt);
        if (entity == null) return Collections.emptyList();

        final EntityIterable entBuilds = entity.getLinks(BuildTypeEntity.Link.TO_BUILDS.name());
        if (entBuilds.isEmpty()) return Collections.emptyList();

        List<TestBuild> builds = new ArrayList<>();
        for (Entity entBuild : entBuilds) {
            TestBuild build = BuildBinding.entityToTestBuild(entBuild);
            builds.add(build);
        }
        return builds;
    }

    public EntityIterable getAllBuildEntities(@NotNull Entity entity) {
        return entity.getLinks(BuildTypeEntity.Link.TO_BUILDS.name());
    }

    public Entity addBuildEntity(@NotNull StoreTransaction txn, @NotNull TestBuild build) {
        Entity bt = getBuildTypeEntity(txn, build.getID().getBuildTypeID());
        if (bt == null) return null;

        bt.setBlobString(BuildTypeEntity.Blob.LAST_MONITORED_BUILD_ID.name(), build.getID().getBuildID());

        Entity testEntity = BuildBinding.createEntity(txn, build);

        Collection<String> artifacts = build.getArtifacts();
        if (artifacts != null && !artifacts.isEmpty())
            testEntity.setBlob(BuildEntity.Blob.ARTIFACT_NAMES.name(), CollectionConverter.<String>toInputStream(StringBinding.BINDING, artifacts));


        bt.addLink(BuildTypeEntity.Link.TO_BUILDS.name(), testEntity);
        testEntity.addLink(BuildEntity.Link.TO_BUILD_TYPE.name(), bt);

        return testEntity;
    }

    @Nullable
    public Entity getBuildTypeEntity(@NotNull StoreTransaction txn, @NotNull String btID) {
        EntityIterable it = txn.find(BuildTypeEntity.TYPE, BuildTypeEntity.Property.ID.name(), btID);
        return it.getFirst();
    }
}
