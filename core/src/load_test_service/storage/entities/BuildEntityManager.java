package load_test_service.storage.entities;

import jetbrains.exodus.database.Entity;
import jetbrains.exodus.database.EntityIterable;
import jetbrains.exodus.database.StoreTransaction;
import load_test_service.api.model.BuildID;
import load_test_service.api.model.TestBuild;
import load_test_service.storage.binding.BuildBinding;
import load_test_service.storage.queries.BuildQuery;
import load_test_service.storage.schema.ArtifactEntity;
import load_test_service.storage.schema.BuildEntity;
import load_test_service.storage.schema.BuildTypeEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

public class BuildEntityManager implements BuildQuery {

    @Override
    @Nullable
    public TestBuild getBuild(@NotNull StoreTransaction txn, @NotNull BuildID buildID) {
        Entity entity = getBuildEntity(txn, buildID);
        return entity == null ? null : BuildBinding.entityToTestBuild(entity);
    }

    @Override
    public void removeBuildDependencies(@NotNull Entity build) {
        Entity bt = build.getLink(BuildEntity.Link.TO_BUILD_TYPE.name());
        if (bt != null)
            bt.deleteLink(BuildTypeEntity.Link.TO_BUILDS.name(), build);
        for (Entity dependency : build.getLinks(BuildEntity.Link.TO_DEPENDENCY.name())) {
            dependency.delete();
        }
    }

    @Override
    public Collection<String> getBuildArtifactNames(@NotNull StoreTransaction txn, @NotNull BuildID buildID) {
        Entity buildEntity = getBuildEntity(txn, buildID);
        if (buildEntity == null) return null;

        Collection<String> artifactNames = new ArrayList<>();
        for (Entity entity : buildEntity.getLinks(BuildEntity.Link.TO_ARTIFACT.name())) {
            artifactNames.add((String) entity.getProperty(ArtifactEntity.Property.NAME.name()));
        }
        return artifactNames;
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
        Entity buildEntity = getBuildEntity(txn, buildID);
        if (buildEntity != null) {
            Entity artifactEntity = txn.newEntity(ArtifactEntity.TYPE);
            artifactEntity.setProperty(ArtifactEntity.Property.NAME.name(), artifactName);
            artifactEntity.setProperty(ArtifactEntity.Property.IS_PROCESSED.name(), false);
            artifactEntity.setBlob(ArtifactEntity.Blob.CONTENT.name(), artifact);

            buildEntity.addLink(BuildEntity.Link.TO_ARTIFACT.name(), artifactEntity);
        }
    }

    @Nullable
    public Entity getBuildEntity(StoreTransaction txn, BuildID buildID) {
        EntityIterable builds = txn.find(BuildEntity.TYPE, BuildEntity.Property.ID.name(), buildID.getBuildID());
        return builds.getFirst();
    }
}
