package load_test_service.teamcity;

import jetbrains.exodus.database.Entity;
import jetbrains.exodus.database.PersistentEntityStore;
import jetbrains.exodus.database.StoreTransaction;
import load_test_service.api.model.BuildType;
import load_test_service.api.model.TestBuild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;

public interface TCAnalyzerInnerQuery {
    BuildType getBuildType(@NotNull final String btID);

    PersistentEntityStore getTransactionExecutable();



    /**
     * Create new build entity (include only test build info, and dependencies with changes)
     * @param txn
     * @param build
     * @return null if there is no build type
     */
    @Nullable
    Entity addBuild(@NotNull StoreTransaction txn, @NotNull final TestBuild build);

    /**
     * Add new artifact entity (blob with artifact content), link it to build;
     * @param txn
     * @param build
     * @param artifactName
     * @param artifact
     */
    void addBuildArtifact(@NotNull StoreTransaction txn, @NotNull Entity build, @NotNull String artifactName, @NotNull InputStream artifact);
}
