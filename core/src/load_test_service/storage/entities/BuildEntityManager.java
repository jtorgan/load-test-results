package load_test_service.storage.entities;

import jetbrains.exodus.database.Entity;
import jetbrains.exodus.database.EntityIterable;
import jetbrains.exodus.database.StoreTransaction;
import load_test_service.api.model.BuildID;
import load_test_service.api.model.TestBuild;
import load_test_service.storage.binding.BuildBinding;
import load_test_service.storage.schema.ArtifactEntity;
import load_test_service.storage.schema.BuildEntity;
import load_test_service.storage.schema.BuildTypeEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

public class BuildEntityManager{

    @Nullable
    public TestBuild getBuild(@NotNull StoreTransaction txn, @NotNull BuildID buildID) {
        Entity entity = getBuildEntity(txn, buildID);
        return entity == null ? null : BuildBinding.entityToTestBuild(entity);
    }



    // delete build methods
    public void removeBuildDependencies(@NotNull Entity build) {
        Entity bt = build.getLink(BuildEntity.Link.TO_BUILD_TYPE.name());
        if (bt != null)
            bt.deleteLink(BuildTypeEntity.Link.TO_BUILDS.name(), build);
        for (Entity dependency : build.getLinks(BuildEntity.Link.TO_DEPENDENCY.name())) {
            dependency.delete();
        }
    }

    public void removeAllArtifacts(@NotNull Entity build) {
        EntityIterable artifacts = build.getLinks(BuildEntity.Link.TO_ARTIFACT.name());
        if (!artifacts.isEmpty()) {
            for (Entity artifact : artifacts) {
                artifact.delete();
            }
        }
        build.deleteLinks(BuildEntity.Link.TO_ARTIFACT.name());
    }



    // artifacts
    public Collection<String> getBuildArtifactNamesWithStatus(@NotNull StoreTransaction txn, @NotNull final BuildID buildID, boolean isProcessed) {
        Entity buildEntity = getBuildEntity(txn, buildID);
        if (buildEntity == null) return null;

        Collection<String> artifactNames = new ArrayList<>();
        for (Entity entity : buildEntity.getLinks(BuildEntity.Link.TO_ARTIFACT.name())) {
            if (entity.getProperty(ArtifactEntity.Property.IS_PROCESSED.name()) == isProcessed)
                artifactNames.add((String) entity.getProperty(ArtifactEntity.Property.NAME.name()));
        }
        return artifactNames;
    }

    public InputStream loadArtifact(@NotNull StoreTransaction txn, @NotNull BuildID buildID, @NotNull String artifactName) {
        Entity artifact = getArtifact(txn, buildID, artifactName);
        if (artifact == null) return null;
        return artifact.getBlob(ArtifactEntity.Blob.CONTENT.name());
    }

    public Entity getArtifact(@NotNull StoreTransaction txn, @NotNull BuildID buildID, @NotNull String artifactName) {
        Entity build = getBuildEntity(txn, buildID);
        if (build == null) return null;
        EntityIterable artifacts = build.getLinks(BuildEntity.Link.TO_ARTIFACT.name());
        if (artifacts.isEmpty()) return null;
        for (Entity entity : artifacts) {
            if (artifactName.equals(entity.getProperty(ArtifactEntity.Property.NAME.name())))
                return entity;
        }
        return null;
    }






    /**
     * TeamCity Analyzer INNER query
     */
    public void addBuildArtifact(@NotNull StoreTransaction txn, @NotNull Entity build, @NotNull String artifactName, @NotNull InputStream artifact) {
        Entity artifactEntity = txn.newEntity(ArtifactEntity.TYPE);
        artifactEntity.setProperty(ArtifactEntity.Property.NAME.name(), artifactName);
        artifactEntity.setProperty(ArtifactEntity.Property.IS_PROCESSED.name(), false);
        artifactEntity.setBlob(ArtifactEntity.Blob.CONTENT.name(), artifact);

        build.addLink(BuildEntity.Link.TO_ARTIFACT.name(), artifactEntity);
    }

    @Nullable
    public Entity getBuildEntity(StoreTransaction txn, BuildID buildID) {
        EntityIterable builds = txn.find(BuildEntity.TYPE, BuildEntity.Property.BUILD_ID.name(), buildID.getBuildID());
        return builds.getFirst();
    }
}
