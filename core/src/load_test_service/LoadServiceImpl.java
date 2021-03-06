package load_test_service;

import jetbrains.exodus.database.*;
import jetbrains.exodus.database.impl.bindings.StringBinding;
import load_test_service.api.LoadService;
import load_test_service.api.exeptions.FileFormatException;
import load_test_service.api.exeptions.LinkNotFound;
import load_test_service.api.model.BuildID;
import load_test_service.api.model.BuildType;
import load_test_service.api.model.TestBuild;
import load_test_service.api.statistic.StatisticProperties;
import load_test_service.api.statistic.TestID;
import load_test_service.api.statistic.metrics.Metric;
import load_test_service.api.statistic.results.SampleRawResults;
import load_test_service.api.statistic.results.SampleStatistic;
import load_test_service.statistic.BaseMetrics;
import load_test_service.storage.binding.CollectionConverter;
import load_test_service.storage.entities.BuildEntityManager;
import load_test_service.storage.entities.BuildTypeEntityManager;
import load_test_service.storage.entities.StatisticEntityManager;
import load_test_service.storage.schema.ArtifactEntity;
import load_test_service.storage.schema.BuildEntity;
import load_test_service.storage.schema.BuildTypeEntity;
import load_test_service.teamcity.RESTHttpClient;
import load_test_service.teamcity.TCAnalyzer;
import load_test_service.teamcity.TCAnalyzerInnerQuery;
import load_test_service.teamcity.exceptions.TCException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.*;
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

    public LoadServiceImpl(String location) {
        store = PersistentEntityStores.newInstance(location);

        buildManager = new BuildEntityManager();
        buildTypeManager = new BuildTypeEntityManager();
        statistic = new StatisticEntityManager();

        treeUpdater = new Thread(new Runnable() {
            @Override
            public void run() {
                final RESTHttpClient client = RESTHttpClient.newDefaultInstance();
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        ProjectTree.ROOT.loadChildren(client);
                    } catch (TCException ignore) {
                        continue;
                    }
                    try {
                        Thread.sleep(TimeUnit.MINUTES.toMillis(30));
                    } catch (InterruptedException ignore) {
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
        StoreTransaction txn = store.getCurrentTransaction();
        if (txn != null) {
            txn.abort();
        }
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
                Entity buildType = buildTypeManager.getBuildTypeEntity(txn, buildTypeID);
                if (buildType != null)
                    buildType.setProperty(BuildTypeEntity.Property.IS_MONITORED.name(), true);
            }
        });
        monitoring.put(buildTypeID, new TCAnalyzer(this, buildTypeID, 1));
    }

    public void stopMonitorBuildType(@NotNull final String buildTypeID) {
        store.executeInTransaction(new StoreTransactionalExecutable() {
            @Override
            public void execute(@NotNull StoreTransaction txn) {
                Entity buildType = buildTypeManager.getBuildTypeEntity(txn, buildTypeID);
                if (buildType != null)
                    buildType.setProperty(BuildTypeEntity.Property.IS_MONITORED.name(), false);
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
                    buildManager.removeAllArtifacts(build);
                    build.delete();
                }
            }
        });
    }

    @Override
    @Nullable
    public InputStream loadArtifact(@NotNull final BuildID buildID, @NotNull final String artifactName) {
        return store.computeInReadonlyTransaction(new StoreTransactionalComputable<InputStream>() {
            @Override
            public InputStream compute(@NotNull StoreTransaction txn) {
                return buildManager.loadArtifact(txn, buildID, artifactName);
            }
        });
    }

    @Override
    public void setArtifactPatterns(@NotNull final String buildTypeID, @NotNull final List<String> patterns) {
        store.executeInTransaction(new StoreTransactionalExecutable() {
            @Override
            public void execute(@NotNull StoreTransaction txn) {
                Entity buildType = buildTypeManager.getBuildTypeEntity(txn, buildTypeID);
                if (buildType != null)
                    buildType.setBlob(BuildTypeEntity.Blob.PATTERNS.name(), CollectionConverter.toInputStream(StringBinding.BINDING, patterns));
            }
        });
    }


    /**
     * TEAMCITY ANALYZER INNER QUERIES
     */

    @Override
    public PersistentEntityStore getTransactionExecutable() {
        return store;
    }


    @Override
    @Nullable
    public Entity addBuild(@NotNull final StoreTransaction txn, @NotNull final TestBuild build) {
        return buildTypeManager.addBuildEntity(txn, build);
    }

    @Override
    public void addBuildArtifact(@NotNull final StoreTransaction txn, @NotNull final Entity build, @NotNull final String artifactName, @NotNull final InputStream artifact) {
        buildManager.addBuildArtifact(txn, build, artifactName, artifact);
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
    public boolean countStatistic(@NotNull final BuildID buildID, @NotNull final String artifactName, @NotNull final StatisticProperties properties){
        return store.computeInTransaction(new StoreTransactionalComputable<Boolean>() {
            @Override
            public Boolean compute(@NotNull StoreTransaction txn) {
                Entity build = buildManager.getBuildEntity(txn, buildID);
                if (build != null) {
                    for (Entity artifact : build.getLinks(BuildEntity.Link.TO_ARTIFACT.name())) {
                        String entityName = (String) artifact.getProperty(ArtifactEntity.Property.NAME.name());
                        if (entityName != null && entityName.equals(artifactName)) {
                            try {
                                statistic.countStatistic(txn, build, artifact, properties);
                                artifact.setProperty(ArtifactEntity.Property.IS_PROCESSED.name(), true);
                                return true;
                            } catch (FileFormatException | LinkNotFound e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                return false;
            }
        });
    }


    @Override
    public boolean isStatisticCalculated(@NotNull final BuildID buildID, @NotNull final String artifactName) {
        return store.computeInReadonlyTransaction(new StoreTransactionalComputable<Boolean>() {
            @Override
            public Boolean compute(@NotNull StoreTransaction txn) {
                Entity build = buildManager.getBuildEntity(txn, buildID);
                return build != null && build.getProperty(artifactName) != null;
            }
        });
    }

    @NotNull
    @Override
    public Collection<String> getArtifactsWithStat(@NotNull final BuildID buildID) {
        return store.computeInReadonlyTransaction(new StoreTransactionalComputable<Collection<String>>() {
            @Override
            public Collection<String> compute(@NotNull StoreTransaction txn) {
                return buildManager.getBuildArtifactNamesWithStatus(txn, buildID, true);
            }
        });
    }

    @NotNull
    @Override
    public Collection<String> getArtifactsWithoutStat(@NotNull final BuildID buildID) {
        return store.computeInReadonlyTransaction(new StoreTransactionalComputable<Collection<String>>() {
            @Override
            public Collection<String> compute(@NotNull StoreTransaction txn) {
                return buildManager.getBuildArtifactNamesWithStatus(txn, buildID, false);
            }
        });
    }


    @NotNull
    @Override
    public Map<TestID, SampleStatistic> getStatistic(@NotNull final String buildTypeID) {
        return store.computeInReadonlyTransaction(new StoreTransactionalComputable<Map<TestID, SampleStatistic>>() {
            @Override
            public Map<TestID, SampleStatistic> compute(@NotNull StoreTransaction txn) {
                Entity buildType = buildTypeManager.getBuildTypeEntity(txn, buildTypeID);
                if (buildType == null) return Collections.emptyMap();
                return statistic.getStatistic(buildType);
            }
        });
    }

    @Override
    public Map<TestID, SampleStatistic> getStatistic(@NotNull final String buildTypeID, @NotNull final String[] buildIDs) {
        return store.computeInReadonlyTransaction(new StoreTransactionalComputable<Map<TestID, SampleStatistic>>() {
            @Override
            public Map<TestID, SampleStatistic> compute(@NotNull StoreTransaction txn) {
                Entity buildType = buildTypeManager.getBuildTypeEntity(txn, buildTypeID);
                if (buildType == null) return Collections.emptyMap();
                return statistic.getStatistic(buildType, Arrays.asList(buildIDs));
            }
        });
    }

    @Nullable
    @Override
    public SampleStatistic getSampleStatistic(@NotNull final String buildTypeID, @NotNull final TestID testID) {
        return store.computeInReadonlyTransaction(new StoreTransactionalComputable<SampleStatistic>() {
            @Override
            public SampleStatistic compute(@NotNull StoreTransaction txn) {
                Entity buildType = buildTypeManager.getBuildTypeEntity(txn, buildTypeID);
                if (buildType == null) return null;
                return statistic.getSampleStatistic(buildType, testID);
            }
        });
    }



    @NotNull
    @Override
    public Map<TestID, SampleRawResults> getRawStatistic(@NotNull final BuildID buildID, @NotNull final String artifactName) {
        return store.computeInReadonlyTransaction(new StoreTransactionalComputable<Map<TestID, SampleRawResults>>() {
            @Override
            public Map<TestID, SampleRawResults> compute(@NotNull StoreTransaction txn) {
                Entity artifact  = buildManager.getArtifact(txn, buildID, artifactName);
                if (artifact != null)  {
                    try {
                        return statistic.getRawStatistic(buildID, artifactName, artifact);
                    } catch (FileFormatException ignore) { }
                }
                return Collections.emptyMap();
            }
        });
    }

    @Nullable
    @Override
    public SampleRawResults getSampleRawStatistic(@NotNull final BuildID buildID, @NotNull final String artifactName, @NotNull final TestID testID) {
        return store.computeInReadonlyTransaction(new StoreTransactionalComputable<SampleRawResults>() {
            @Override
            public SampleRawResults compute(@NotNull StoreTransaction txn) {
                Entity artifact  = buildManager.getArtifact(txn, buildID, artifactName);
                if (artifact != null)  {
                    try {
                        return statistic.getSampleRawStatistic(buildID, artifactName, artifact, testID);
                    } catch (FileFormatException ignore) { }
                }
                return null;
            }
        });
    }
}
