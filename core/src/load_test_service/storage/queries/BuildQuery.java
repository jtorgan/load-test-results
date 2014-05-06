package load_test_service.storage.queries;

import jetbrains.exodus.database.Entity;
import jetbrains.exodus.database.StoreTransaction;
import load_test_service.api.model.BuildID;
import load_test_service.api.model.TestBuild;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Collection;

public interface BuildQuery {

    TestBuild getBuild(@NotNull StoreTransaction txn, @NotNull final BuildID buildID);
    void removeBuildDependencies(@NotNull final Entity buildID);

    Collection<String> getBuildArtifactNames(@NotNull StoreTransaction txn, @NotNull final BuildID buildID);
    InputStream loadArtifact(@NotNull StoreTransaction txn, @NotNull final BuildID buildID, @NotNull final String artifactName);

    void addBuildArtifact(@NotNull StoreTransaction txn, @NotNull final BuildID buildID, @NotNull final String artifactName, @NotNull final InputStream artifact);
}
