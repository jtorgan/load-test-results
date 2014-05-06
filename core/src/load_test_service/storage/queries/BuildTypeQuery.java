package load_test_service.storage.queries;

import jetbrains.exodus.database.StoreTransaction;
import load_test_service.api.model.BuildType;
import load_test_service.api.model.TestBuild;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface BuildTypeQuery {
    void addBuildType(@NotNull StoreTransaction txn, @NotNull final BuildType buildType);
    BuildType getBuildType(@NotNull StoreTransaction txn, @NotNull final String btID);
    void removeBuildType(@NotNull StoreTransaction txn, @NotNull final String btID);

    List<BuildType> getAllBuildTypes(@NotNull final StoreTransaction txn);

    void updateMonitoringStatus(@NotNull StoreTransaction txn, @NotNull String btId, boolean status);
    void updatePatterns(@NotNull StoreTransaction txn, @NotNull String btId, @NotNull List<String> patterns);

    List<TestBuild> getAllBuilds(@NotNull StoreTransaction txn, @NotNull final String btID);

    void addBuildEntity(@NotNull StoreTransaction txn, @NotNull final TestBuild build);

}
