package load_test_service.storage.binding;

import jetbrains.exodus.database.Entity;
import jetbrains.exodus.database.EntityIterable;
import jetbrains.exodus.database.StoreTransaction;
import jetbrains.exodus.database.impl.bindings.StringBinding;
import load_test_service.api.model.*;
import load_test_service.storage.schema.BuildEntity;
import load_test_service.storage.schema.DependencyEntity;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.List;

public class BuildBinding {

//  to Entity
    public static Entity createEntity(@NotNull final StoreTransaction txn, @NotNull TestBuild build) {
        final Entity buildEntity = txn.newEntity(BuildEntity.TYPE);
        buildEntity.setProperty(BuildEntity.Property.ID.name(), build.getID());
        buildEntity.setProperty(BuildEntity.Property.STATUS.name(), build.getStatus());
        buildEntity.setProperty(BuildEntity.Property.FINISH_DATE.name(), build.getFinishDate());

        buildEntity.setBlobString(BuildEntity.Blob.NUMBER.name(), build.getBuildNumber());


        List<DependencyBuild> dependencyList = build.getDependencyList();
        if (dependencyList != null) {
            for (DependencyBuild dependency : dependencyList) {
                buildEntity.addLink(BuildEntity.Link.TO_DEPENDENCY.name(), createDependencyEntity(txn, dependency));
            }
        }
        return buildEntity;
    }

    public static Entity createDependencyEntity(@NotNull final StoreTransaction txn, @NotNull DependencyBuild build) {
        final Entity depBuild = txn.newEntity(DependencyEntity.TYPE);

        depBuild.setProperty(DependencyEntity.Property.ID.name(), build.getID());
        depBuild.setProperty(DependencyEntity.Property.STATUS.name(), build.getStatus());
        depBuild.setProperty(DependencyEntity.Property.FINISH_DATE.name(), build.getFinishDate());

        depBuild.setBlobString(DependencyEntity.Blob.NUMBER.name(), build.getBuildNumber());
        depBuild.setBlobString(DependencyEntity.Blob.BUILD_TYPE_NAME.name(), build.getName());
        depBuild.setBlob(DependencyEntity.Blob.CHANGES.name(), CollectionConverter.<Change>toInputStream(ChangeBinding.BINDING, build.getChanges()));

        return depBuild;
    }


//  from Entity
    public static TestBuild entityToTestBuild(@NotNull final Entity entity) {
        BuildID id = (BuildID) entity.getProperty(BuildEntity.Property.ID.name());
        String status = (String) entity.getProperty(BuildEntity.Property.STATUS.name());
        String finish = (String) entity.getProperty(BuildEntity.Property.FINISH_DATE.name());
        String number = entity.getBlobString(BuildEntity.Blob.NUMBER.name());

        TestBuild build = new TestBuild(id);
        build.setBuildNumber(number);
        build.setStatus(status);
        build.setFinishDate(finish);

        final EntityIterable entDependencies = entity.getLinks(BuildEntity.Link.TO_DEPENDENCY.name());
        if (!entDependencies.isEmpty()) {
            for(Entity entDependency : entDependencies) {
                BuildID depID = (BuildID) entity.getProperty(DependencyEntity.Property.ID.name());
                DependencyBuild dependency = entityToDependencyBuild(entDependency);
                build.addDependency(dependency);
            }
        }

        InputStream stream = entity.getBlob(BuildEntity.Blob.ARTIFACT_NAMES.name());
        if (stream != null)
            build.setArtifacts(CollectionConverter.<String>fromInputStream(stream, StringBinding.BINDING));
        return build;
    }

    public static DependencyBuild entityToDependencyBuild(@NotNull final Entity entity) {
        BuildID id = (BuildID) entity.getProperty(DependencyEntity.Property.ID.name());
        String status = (String) entity.getProperty(DependencyEntity.Property.STATUS.name());
        String finish = (String) entity.getProperty(DependencyEntity.Property.FINISH_DATE.name());
        String number = entity.getBlobString(DependencyEntity.Blob.NUMBER.name());

        DependencyBuild build = new DependencyBuild(id);
        build.setBuildNumber(number);
        build.setStatus(status);
        build.setFinishDate(finish);
        build.setName(entity.getBlobString(DependencyEntity.Blob.BUILD_TYPE_NAME.name()));

        InputStream stream = entity.getBlob(DependencyEntity.Blob.CHANGES.name());
        if (stream != null) {
            build.setChanges(CollectionConverter.<Change>fromInputStream(stream, ChangeBinding.BINDING));
        }
        return build;
    }
}
