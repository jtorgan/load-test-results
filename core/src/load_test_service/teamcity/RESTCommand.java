package load_test_service.teamcity;

import load_test_service.teamcity.exceptions.TCException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RESTCommand {
    @Nullable
    <T> T execute(@NotNull final RESTHttpClient client, final Object ... params) throws TCException;
}
