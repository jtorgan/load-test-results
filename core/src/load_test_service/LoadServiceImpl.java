package load_test_service;

import jetbrains.exodus.database.*;
import load_test_service.api.LoadService;
import load_test_service.api.exeptions.FileFormatException;
import load_test_service.api.model.BuildID;
import load_test_service.api.model.BuildType;
import load_test_service.api.model.TestBuild;
import load_test_service.api.statistic.StatisticProperties;
import load_test_service.api.statistic.TestBuildStatistic;
import load_test_service.api.statistic.TestID;
import load_test_service.api.statistic.metrics.Metric;
import load_test_service.statistic.BaseMetrics;
import load_test_service.storage.entities.BuildEntityManager;
import load_test_service.storage.entities.BuildTypeEntityManager;
import load_test_service.storage.entities.StatisticEntityManager;
import load_test_service.storage.queries.TCAnalyzerInnerQuery;
import load_test_service.teamcity.RESTHttpClient;
import load_test_service.teamcity.TCAnalyzer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class LoadServiceImpl implements LoadService, TCAnalyzerInnerQuery {
    private final Thread treeUpdater;

    private final PersistentEntityStore store;

    private final BuildTypeEntityManager buildTypeManager;
    private final BuildEntityManager buildManager;
    private final StatisticEntityManager statistic;

    private final ConcurrentMap<String, TCAnalyzer> monitoring;

    public LoadServiceImpl() {
        store = PersistentEntityStores.newInstance("c:\\_DB_\\");

        buildManager = new BuildEntityManager();
        buildTypeManager = new BuildTypeEntityManager();
        statistic = new StatisticEntityManager();

        treeUpdater = new Thread(new Runnable() {
            @Override
            public void run() {
                final RESTHttpClient client = RESTHttpClient.newDefaultInstance();
                while (!Thread.currentThread().isInterrupted()) {
                    ProjectTree.ROOT.loadChildren(client);
                    try {
                        Thread.sleep(TimeUnit.MINUTES.toMillis(30));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        treeUpdater.start();

        monitoring = new ConcurrentHashMap<>();
        for(BuildType bt : getAllBuildTypes()) {
            if (bt.isMonitored()) {
                monitoring.put(bt.getID(), new TCAnalyzer(this, bt.getID(), 1));
            }
        }
    }

    public void stop() {
        for (TCAnalyzer analyzer : monitoring.values()) {
            analyzer.stop();
        }
        treeUpdater.interrupt();
        store.close();
    }

    @Nullable
    public List<ProjectTree> getTCProjectTree() {
        return ProjectTree.ROOT.getSubProjects();
    }

    @Override
    public void addBuildType(@NotNull final BuildType buildType) {
        store.executeInTransaction(new StoreTransactionalExecutable() {
            @Override
            public void execute(@NotNull StoreTransaction txn) {
                buildTypeManager.addBuildType(txn, buildType);
            }
        });
    }

    @Override
    public BuildType getBuildType(@NotNull final String btID) {
        return store.computeInReadonlyTransaction(new StoreTransactionalComputable<BuildType>() {
            @Override
            public BuildType compute(@NotNull StoreTransaction txn) {
                return buildTypeManager.getBuildType(txn, btID);
            }
        });
    }

    @Override
    public void removeBuildType(@NotNull final String btID) {
        TCAnalyzer analyzer = monitoring.get(btID);
        if (analyzer != null) {
            Thread analyzerThread = analyzer.stop();
            try {
                analyzerThread.join();
                monitoring.remove(btID);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        store.executeInTransaction(new StoreTransactionalExecutable() {
            @Override
            public void execute(@NotNull StoreTransaction txn) {
                final Entity bt = buildTypeManager.getBuildTypeEntity(txn, btID);
                if (bt != null) {
                    EntityIterable builds = buildTypeManager.getAllBuildEntities(bt);
                    if (builds != null && !builds.isEmpty()) {
                        for (Entity build: builds) {
                            statistic.deleteBuildStatistic(build);
                            buildManager.removeBuildDependencies(build);
                            build.delete();
                        }
                    }
                    bt.delete();
                }
            }
        });
    }

    @Override
    @NotNull
    public List<BuildType> getAllBuildTypes() {
        return store.computeInReadonlyTransaction(new StoreTransactionalComputable<List<BuildType>>() {
            @Override
            public List<BuildType> compute(@NotNull StoreTransaction txn) {
                return buildTypeManager.getAllBuildTypes(txn);
            }
        });
    }


    public void startMonitorBuildType(@NotNull final String buildTypeID) {
        store.executeInTransaction(new StoreTransactionalExecutable() {
            @Override
            public void execute(@NotNull StoreTransaction txn) {
                buildTypeManager.updateMonitoringStatus(txn, buildTypeID, true);
            }
        });
        monitoring.put(buildTypeID, new TCAnalyzer(this, buildTypeID, 1));
    }

    public void stopMonitorBuildType(@NotNull final String buildTypeID) {
        store.executeInTransaction(new StoreTransactionalExecutable() {
            @Override
            public void execute(@NotNull StoreTransaction txn) {
                buildTypeManager.updateMonitoringStatus(txn, buildTypeID, false);
            }
        });
        TCAnalyzer analyzer = monitoring.get(buildTypeID);
        if (analyzer != null) {
            Thread analyzerThread = analyzer.stop();
            try {
                analyzerThread.join();
                monitoring.remove(buildTypeID);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    @NotNull
    public List<TestBuild> getAllBuilds(@NotNull final String btID) {
        return store.computeInReadonlyTransaction(new StoreTransactionalComputable<List<TestBuild>>() {
            @Override
            public List<TestBuild> compute(@NotNull StoreTransaction txn) {
                return buildTypeManager.getAllBuilds(txn, btID);
            }
        });
    }

    @Override
    @Nullable
    public TestBuild getBuild(@NotNull final BuildID buildID) {
        return store.computeInReadonlyTransaction(new StoreTransactionalComputable<TestBuild>() {
            @Override
            public TestBuild compute(@NotNull StoreTransaction txn) {
                return buildManager.getBuild(txn, buildID);
            }
        });
    }

    @Override
    public void removeBuild(@NotNull final BuildID buildID) {
        store.executeInTransaction(new StoreTransactionalExecutable() {
            @Override
            public void execute(@NotNull StoreTransaction txn) {
                Entity build = buildManager.getBuildEntity(txn, buildID);
                if (build != null) {
                    statistic.deleteBuildStatistic(build);
                    buildManager.removeBuildDependencies(build);
                }
            }
        });
    }

    @Override
    public InputStream loadArtifact(@NotNull final BuildID buildID, @NotNull final String artifactName) {
        return store.computeInReadonlyTransaction(new StoreTransactionalComputable<InputStream>() {
            @Override
            public InputStream compute(@NotNull StoreTransaction txn) {
                return buildManager.loadArtifact(txn, buildID, artifactName);
            }
        });
    }

    @Override
    public void setArtifactPatterns(@NotNull final String btID, @NotNull final List<String> patterns) {
        store.executeInTransaction(new StoreTransactionalExecutable() {
            @Override
            public void execute(@NotNull StoreTransaction txn) {
                buildTypeManager.updatePatterns(txn, btID, patterns);
            }
        });
    }



    /**
     * TEAMCITY ANALYZER INNER QUERIES
     */
    @Override
    public void addBuild(@NotNull final TestBuild build) {
        store.executeInTransaction(new StoreTransactionalExecutable() {
            @Override
            public void execute(@NotNull StoreTransaction txn) {
                buildTypeManager.addBuildEntity(txn, build);
            }
        });
    }

    @Override
    public void addBuildArtifact(@NotNull final BuildID buildID, @NotNull final String artifactName, @NotNull final InputStream artifact) {
        store.executeInTransaction(new StoreTransactionalExecutable() {
            @Override
            public void execute(@NotNull StoreTransaction txn) {
                buildManager.addBuildArtifact(txn, buildID, artifactName, artifact);
            }
        });
    }




    /**
     * STATISTIC query implementation
     */

    @Override
    @NotNull
    public Metric[] getBaseMetrics() {
        return BaseMetrics.values();
    }

    @Override
    public void countStatistic(@NotNull final BuildID buildID, @NotNull final String artifactName, @NotNull final StatisticProperties properties) {
        store.executeInTransaction(new StoreTransactionalExecutable() {
            @Override
            public void execute(@NotNull StoreTransaction txn) {
                Entity build = buildManager.getBuildEntity(txn, buildID);
                if (build != null) {
                    InputStream artifact = build.getBlob(artifactName);
                    if (artifact != null)
                        statistic.countStatistic(txn, buildID, build, artifact, properties);
                }
            }
        });
    }

    @Override
    public Map<TestID, TestBuildStatistic> getRawStatistic(@NotNull BuildID buildID, @NotNull String artifactName) throws FileFormatException {
        InputStream artifact = getArtifact(buildID, artifactName);
        return statistic.getRawStatistic(artifact);
    }





    @Override
    public boolean isStatisticCalculated(@NotNull BuildID buildID, @NotNull String artifactName) {
        return false;
    }

    @Override
    public List<String> getArtifactsWithStatistic(@NotNull BuildID buildID) {
        return null;
    }

    @Override
    public List<String> getArtifactsWithoutStatistic(@NotNull BuildID buildID) {
        return null;
    }

    private InputStream getArtifact(final BuildID buildID, final String artifactName) {
        return store.computeInReadonlyTransaction(new StoreTransactionalComputable<InputStream>() {
            @Override
            public InputStream compute(@NotNull StoreTransaction txn) {
                return buildManager.loadArtifact(txn, buildID, artifactName);
            }
        });
    }
}
