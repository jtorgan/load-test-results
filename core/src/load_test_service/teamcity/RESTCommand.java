package load_test_service.teamcity;

import org.jetbrains.annotations.NotNull;

public interface RESTCommand {
    @NotNull
    <T> T execute(@NotNull final RESTHttpClient client, final Object ... params);
}
