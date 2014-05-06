package load_test_service.teamcity;

import load_test_service.api.model.BuildType;
import load_test_service.api.model.TestBuild;
import load_test_service.storage.queries.TCAnalyzerInnerQuery;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TCAnalyzer implements Runnable {
    private final Thread thread;

    private final TCAnalyzerInnerQuery store;
    private final String btID;
    private final int delay; //in minutes

    private volatile boolean isStopped = false;

    public TCAnalyzer(TCAnalyzerInnerQuery store, String btID, int delay) {
        this.store = store;
        this.btID = btID;
        this.delay = delay;

        thread = new Thread(this);
        thread.start();
    }

    public Thread stop() {
        isStopped = true;
        thread.interrupt();
        return thread;
    }

    @Override
    public void run() {
        final RESTHttpClient client = RESTHttpClient.newDefaultInstance();

        while (!Thread.currentThread().isInterrupted() && !isStopped) {
            BuildType bt = store.getBuildType(btID);

            final List<TestBuild> builds = "-1".equals(bt.getLastBuildID())
                    ? RESTCommandImpl.GET_ALL_BUILDS.<List<TestBuild>>execute(client, btID)
                    : RESTCommandImpl.GET_BUILDS_FROM_LAST.<List<TestBuild>>execute(client, btID, bt.getLastBuildID());

            Collections.sort(builds);

            if (!builds.isEmpty()) {
                for (final TestBuild build : builds) {
                    final Map<String, String> artifacts = RESTCommandImpl.GET_ARTIFACT_PATHS.execute(client, build.getID().getBuildID(), bt.getPatterns());

                    if (!artifacts.isEmpty()) {
                        build.setArtifacts(artifacts.keySet());
                    }
                    store.addBuild(build);

                    for (final Map.Entry<String, String> artifact: artifacts.entrySet()) {
                        if (!Thread.currentThread().isInterrupted()) {
                            final InputStream stream = RESTCommandImpl.GET_ARTIFACT_STREAM.execute(client, artifact.getValue());
                            store.addBuildArtifact(build.getID(), artifact.getKey(), stream);
                        }
                    }
                }
            }

            try {
                Thread.sleep(TimeUnit.MINUTES.toMillis(delay));
            } catch (InterruptedException ignore) {}
        }
    }
}
