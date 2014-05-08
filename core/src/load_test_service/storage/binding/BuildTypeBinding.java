package load_test_service.storage.binding;

import jetbrains.exodus.database.Entity;
import jetbrains.exodus.database.StoreTransaction;
import jetbrains.exodus.database.impl.bindings.StringBinding;
import load_test_service.api.model.BuildType;
import load_test_service.storage.schema.BuildTypeEntity;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

public class BuildTypeBinding {
    public static BuildType entityToBuildType(@NotNull final Entity entity) {
        String buildTypeID = (String) entity.getProperty(BuildTypeEntity.Property.ID.name());
        String projectID = (String) entity.getProperty(BuildTypeEntity.Property.PROJECT_ID.name());
        boolean isMonitored = (boolean) entity.getProperty(BuildTypeEntity.Property.IS_MONITORED.name());

        String buildTypeName = entity.getBlobString(BuildTypeEntity.Blob.BUILD_TYPE_NAME.name());
        String projectName = entity.getBlobString(BuildTypeEntity.Blob.PROJECT_NAME.name());
        String lastBuildID = entity.getBlobString(BuildTypeEntity.Blob.LAST_MONITORED_BUILD_ID.name());

        BuildType bt = new BuildType(buildTypeID);
        bt.setName(buildTypeName);
        bt.setProjectID(projectID);
        bt.setProjectName(projectName);
        bt.setLastBuildID(lastBuildID);
        bt.setMonitored(isMonitored);

        InputStream input =  entity.getBlob(BuildTypeEntity.Blob.PATTERNS.name());
        if (input != null) {
            bt.setPatterns(CollectionConverter.<String>fromInputStream(input, StringBinding.BINDING));
        }

        return bt;
    }

    public static Entity createEntity(@NotNull final BuildType buildType, @NotNull final StoreTransaction txn) {
        Entity entity = txn.newEntity(BuildTypeEntity.TYPE);
        entity.setProperty(BuildTypeEntity.Property.ID.name(), buildType.getID());
        entity.setProperty(BuildTypeEntity.Property.PROJECT_ID.name(), buildType.getProjectID());
        entity.setProperty(BuildTypeEntity.Property.IS_MONITORED.name(), buildType.isMonitored());

        entity.setBlobString(BuildTypeEntity.Blob.BUILD_TYPE_NAME.name(), buildType.getName());
        entity.setBlobString(BuildTypeEntity.Blob.PROJECT_NAME.name(), buildType.getProjectName());
        entity.setBlobString(BuildTypeEntity.Blob.LAST_MONITORED_BUILD_ID.name(), buildType.getLastBuildID());

        if (buildType.getPatterns() != null) {
            entity.setBlob(BuildTypeEntity.Blob.PATTERNS.name(), CollectionConverter.toInputStream(StringBinding.BINDING, buildType.getPatterns()));
        }
        return entity;
    }
}
