package load_test_service.storage.queries;

import load_test_service.api.model.BuildID;
import load_test_service.api.model.BuildType;
import load_test_service.api.model.TestBuild;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

public interface TCAnalyzerInnerQuery {
    BuildType getBuildType(@NotNull final String btID);
    void addBuild(@NotNull final TestBuild build);
    void addBuildArtifact(@NotNull BuildID buildID, @NotNull String artifactName, @NotNull InputStream artifact);
}
