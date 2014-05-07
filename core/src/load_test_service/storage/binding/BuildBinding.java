package load_test_service.storage.binding;

import jetbrains.exodus.database.Entity;
import jetbrains.exodus.database.EntityIterable;
import jetbrains.exodus.database.StoreTransaction;
import jetbrains.exodus.database.impl.bindings.StringBinding;
import load_test_service.api.model.*;
import load_test_service.storage.entities.BuildEntityManager;
import load_test_service.storage.entities.BuildTypeEntityManager;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.List;

public class BuildBinding {

//  to Entity
    public static Entity createEntity(@NotNull final StoreTransaction txn, @NotNull TestBuild build) {
        Entity test = createBaseEntity(txn, build, BuildEntityManager.TEST_ENTITY_TYPE);

        List<DependencyBuild> dependencyList = build.getDependencyList();
        if (dependencyList != null) {
            for (DependencyBuild dependency : dependencyList) {
                test.addLink(BuildEntityManager.LINK_TO_DEPENDENCIES, createDependencyEntity(txn, dependency));
            }
        }
        return test;
    }

    public static Entity createDependencyEntity(@NotNull final StoreTransaction txn, @NotNull DependencyBuild build) {
        final Entity depBuild = createBaseEntity(txn, build, BuildEntityManager.DEP_ENTITY_TYPE);
        depBuild.setBlobString(BuildTypeEntityManager.BLOB_BT_NAME, build.getName());
        depBuild.setBlob(BuildEntityManager.PROPERTY_BUILD_CHANGES, CollectionConverter.<Change>toInputStream(ChangeBinding.BINDING, build.getChanges()));
        return depBuild;
    }

    private static Entity createBaseEntity(StoreTransaction txn, BaseBuildInfo build, String entityType) {
        final Entity entBuild = txn.newEntity(entityType);
        entBuild.setProperty(BuildEntityManager.PROPERTY_BUILD_ID, build.getID().getBuildID());
        entBuild.setProperty(BuildTypeEntityManager.PROPERTY_BT_ID, build.getID().getBuildTypeID());
        entBuild.setProperty(BuildEntityManager.PROPERTY_BUILD_STATUS, build.getStatus());
        entBuild.setProperty(BuildEntityManager.PROPERTY_BUILD_FINISH_DATE, build.getFinishDate());

        entBuild.setBlobString(BuildEntityManager.BLOB_BUILD_NUMBER, build.getBuildNumber());
        return entBuild;
    }


//  from Entity
    public static TestBuild entityToTestBuild(@NotNull final Entity entity) {
        String btID = (String) entity.getProperty(BuildTypeEntityManager.PROPERTY_BT_ID);
        String id = (String) entity.getProperty(BuildEntityManager.PROPERTY_BUILD_ID);

        TestBuild build = new TestBuild(new BuildID(btID, id));
        baseBuildFromEntity(build, entity);

        final EntityIterable entDependencies = entity.getLinks(BuildEntityManager.LINK_TO_DEPENDENCIES);
        if (!entDependencies.isEmpty()) {
            for(Entity entDependency : entDependencies) {
                String dbtID = (String) entity.getProperty(BuildTypeEntityManager.PROPERTY_BT_ID);
                String did = (String) entity.getProperty(BuildEntityManager.PROPERTY_BUILD_ID);
                if (dbtID == null || did == null) {
                    System.out.println("NULL POINTER EXCE: for test id " + id);
                }
                DependencyBuild dependency = entityToDependencyBuild(entDependency);
                build.addDependency(dependency);
            }
        }

        InputStream stream = entity.getBlob(BuildEntityManager.BLOB_ARTIFACT_NAMES);
        if (stream != null)
            build.setArtifacts(CollectionConverter.<String>fromInputStream(stream, StringBinding.BINDING));
        return build;
    }

    public static DependencyBuild entityToDependencyBuild(@NotNull final Entity entity) {
        String btID = (String) entity.getProperty(BuildTypeEntityManager.PROPERTY_BT_ID);
        String id = (String) entity.getProperty(BuildEntityManager.PROPERTY_BUILD_ID);

        DependencyBuild build = new DependencyBuild(new BuildID(btID, id));
        baseBuildFromEntity(build, entity);
        build.setName(entity.getBlobString(BuildTypeEntityManager.BLOB_BT_NAME));

        InputStream stream = entity.getBlob(BuildEntityManager.PROPERTY_BUILD_CHANGES);
        if (stream != null) {
            build.setChanges(CollectionConverter.<Change>fromInputStream(stream, ChangeBinding.BINDING));
        }
        return build;
    }

    private static void baseBuildFromEntity(BaseBuildInfo build, Entity entity) {
        String number = entity.getBlobString(BuildEntityManager.BLOB_BUILD_NUMBER);
        String status = (String) entity.getProperty(BuildEntityManager.PROPERTY_BUILD_STATUS);
        String finish = (String) entity.getProperty(BuildEntityManager.PROPERTY_BUILD_FINISH_DATE);
        build.setBuildNumber(number);
        build.setStatus(status);
        build.setFinishDate(finish);
    }
}
