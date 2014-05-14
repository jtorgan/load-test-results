package load_test_service.teamcity;

import jetbrains.exodus.database.Entity;
import jetbrains.exodus.database.StoreTransaction;
import jetbrains.exodus.database.StoreTransactionalComputable;
import load_test_service.api.model.BuildType;
import load_test_service.api.model.TestBuild;
import load_test_service.teamcity.exceptions.TCException;
import org.jetbrains.annotations.NotNull;

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

            final List<TestBuild> builds;
            try {
                builds = "-1".equals(bt.getLastBuildID())
                        ? RESTCommandImpl.GET_ALL_BUILDS.<List<TestBuild>>execute(client, btID)
                        : RESTCommandImpl.GET_BUILDS_FROM_LAST.<List<TestBuild>>execute(client, btID, bt.getLastBuildID());

                if (builds != null && !builds.isEmpty()) {
                    Collections.sort(builds);

                    // start process new builds
                    for (final TestBuild build : builds) {
                        final Map<String, String> artifacts = RESTCommandImpl.GET_ARTIFACT_PATHS.execute(client, build.getID().getBuildID(), bt.getPatterns());
                        if (artifacts != null && !artifacts.isEmpty()) {

//                            build.setArtifacts(artifacts.keySet());
                            // todo: if happens some exception  ?? => revert transaction, not to save build
                            // or must load full build info, including all artifacts before show build as loaded
                            final Thread currentThread = Thread.currentThread();

                            boolean isSuccessful = store.getTransactionExecutable().computeInTransaction(new StoreTransactionalComputable<Boolean>() {
                                @Override
                                public Boolean compute(@NotNull StoreTransaction txn) {
                                    Entity buildEntity = store.addBuild(txn, build);
                                    try {
                                        for (final Map.Entry<String, String> artifact : artifacts.entrySet()) {
                                            if (!currentThread.isInterrupted() && !isStopped) {
                                                final InputStream stream = RESTCommandImpl.GET_ARTIFACT_STREAM.execute(client, artifact.getValue());
                                                store.addBuildArtifact(txn, buildEntity, artifact.getKey(), stream);
                                            } else {
                                                txn.revert();
                                                return false;
                                            }
                                        }
                                        return true;
                                    } catch (TCException e) {
                                        txn.revert();
                                        return false;
                                    }
                                }
                            });

                            if (!isSuccessful)
                                break;
                        }
                    }
                }
            } catch (TCException e) {
                System.err.println("Error");
            }

            if (Thread.currentThread().isInterrupted() || isStopped)
                break;

            // sleep few minutes to next poll for new builds
            try {
                Thread.sleep(TimeUnit.MINUTES.toMillis(delay));
            } catch (InterruptedException ignore) {}

        }
    }
}
