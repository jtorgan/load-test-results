package load_test_service.storage.binding;

import jetbrains.exodus.database.Entity;
import jetbrains.exodus.database.StoreTransaction;
import jetbrains.exodus.database.impl.bindings.StringBinding;
import load_test_service.api.model.BuildType;
import load_test_service.storage.entities.BuildTypeEntityManager;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

public class BuildTypeBinding {
    public static BuildType entityToBuildType(@NotNull final Entity entity) {
        String buildTypeID = (String) entity.getProperty(BuildTypeEntityManager.PROPERTY_BT_ID);
        String projectID = (String) entity.getProperty(BuildTypeEntityManager.PROPERTY_BT_PROJECT_ID);
        boolean isMonitored = (boolean) entity.getProperty(BuildTypeEntityManager.PROPERTY_BT_MONITORED);

        String buildTypeName = entity.getBlobString(BuildTypeEntityManager.BLOB_BT_NAME);
        String projectName = entity.getBlobString(BuildTypeEntityManager.BLOB_BT_PROJECT_NAME);
        String lastBuildID = entity.getBlobString(BuildTypeEntityManager.BLOB_BT_LAST_BUILD_ID);

        BuildType bt = new BuildType(buildTypeID);
        bt.setName(buildTypeName);
        bt.setProjectID(projectID);
        bt.setProjectName(projectName);
        bt.setLastBuildID(lastBuildID);
        bt.setMonitored(isMonitored);

        InputStream input =  entity.getBlob(BuildTypeEntityManager.PROPERTY_BT_PATTERNS);
        if (input != null) {
            bt.setPatterns(CollectionConverter.<String>fromInputStream(input, StringBinding.BINDING));
        }

        return bt;
    }

    public static Entity createEntity(@NotNull final BuildType buildType, @NotNull final StoreTransaction txn) {
        Entity entity = txn.newEntity(BuildTypeEntityManager.ENTITY_TYPE);
        entity.setProperty(BuildTypeEntityManager.PROPERTY_BT_ID, buildType.getID());
        entity.setProperty(BuildTypeEntityManager.PROPERTY_BT_PROJECT_ID, buildType.getProjectID());
        entity.setProperty(BuildTypeEntityManager.PROPERTY_BT_MONITORED, buildType.isMonitored());

        entity.setBlobString(BuildTypeEntityManager.BLOB_BT_NAME, buildType.getName());
        entity.setBlobString(BuildTypeEntityManager.BLOB_BT_PROJECT_NAME, buildType.getProjectName());
        entity.setBlobString(BuildTypeEntityManager.BLOB_BT_LAST_BUILD_ID, buildType.getLastBuildID());

        if (buildType.getPatterns() != null) {
            entity.setBlob(BuildTypeEntityManager.PROPERTY_BT_PATTERNS, CollectionConverter.toInputStream(StringBinding.BINDING, buildType.getPatterns()));
        }
        return entity;
    }
}
