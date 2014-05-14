package load_test_service.storage.entities;

import jetbrains.exodus.database.Entity;
import jetbrains.exodus.database.EntityIterable;
import jetbrains.exodus.database.StoreTransaction;
import load_test_service.api.exeptions.FileFormatException;
import load_test_service.api.exeptions.LinkNotFound;
import load_test_service.api.model.BuildID;
import load_test_service.api.statistic.StatisticProperties;
import load_test_service.api.statistic.TestID;
import load_test_service.api.statistic.metrics.MetricCounter;
import load_test_service.api.statistic.results.SampleRawResults;
import load_test_service.api.statistic.results.SampleStatistic;
import load_test_service.statistic.readers.RawDataReader;
import load_test_service.statistic.readers.StatisticAggregatedReader;
import load_test_service.storage.schema.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticEntityManager {
    private static final RawCache rawCache = new RawCache();

    /**
     * Calculate statistic and save it to store
     * @param txn
     * @param artifact
     * @param properties
     * @throws FileFormatException
     */
//  todo: is need to add link build => sample value
    public void countStatistic(@NotNull StoreTransaction txn, @NotNull final Entity build, @NotNull final Entity artifact,
                               @NotNull final StatisticProperties properties) throws FileFormatException, LinkNotFound {
        Entity buildType = build.getLink(BuildEntity.Link.TO_BUILD_TYPE.name());
        if (buildType == null)
            throw new LinkNotFound(BuildEntity.Link.TO_BUILD_TYPE.name(), build);
        StatisticAggregatedReader reader = new StatisticAggregatedReader(properties);
        reader.processFile(artifact.getBlob(ArtifactEntity.Blob.CONTENT.name()));

//        create existed samples map
        Map<String, Entity> existedSamples = new HashMap<>();
        for (Entity sample : buildType.getLinks(BuildTypeEntity.Link.TO_SAMPLES.name())) {
            String key = sample.getProperty(SampleEntity.Property.THREAD_GROUP.name()) + (String) sample.getProperty(SampleEntity.Property.SAMPLE_NAME.name());
            existedSamples.put(key, sample);
        }

        saveTestValues(txn, buildType, build, artifact, existedSamples, reader.getValuesBySamplers());
        if (properties.isCalculateTotal()) {
            saveTestValues(txn, buildType, build, artifact, existedSamples, reader.getTotalValuesByThreadGroups());
        }
    }

    private void saveTestValues(StoreTransaction txn, Entity buildType, Entity build, Entity artifact, Map<String, Entity> existedSamples, Map<TestID, List<MetricCounter>> tests) {
        for(TestID testID : tests.keySet()) {
            String key = testID.getThreadGroup() + testID.getTestName();
            Entity sample = existedSamples.get(key);
            if (sample == null) {
                sample = txn.newEntity(SampleEntity.TYPE);
                sample.setProperty(SampleEntity.Property.THREAD_GROUP.name(), testID.getThreadGroup());
                sample.setProperty(SampleEntity.Property.SAMPLE_NAME.name(), testID.getTestName());

                buildType.addLink(BuildTypeEntity.Link.TO_SAMPLES.name(), sample);
            }

            for (MetricCounter metric : tests.get(testID)) {
                if (metric instanceof MetricCounter.SingleValueMetric) { // Min, Max, Average, 90Line - with one value
                    MetricCounter.SingleValueMetric singleMetric = (MetricCounter.SingleValueMetric) metric;
                    Entity value = createValueEntity(txn, build, metric.getKey(), singleMetric.getBuildValue(), false, null);

                    value.addLink(SampleValue.Link.TO_SAMPLE.name(), sample);

                    sample.addLink(SampleEntity.Link.TO_SAMPLE_VALUE.name(), value);
                    build.addLink(BuildEntity.Link.TO_SAMPLE_VALUE.name(), value);
                    artifact.addLink(ArtifactEntity.Link.TO_STAT_SAMPLES_VALUES.name(), value);

                } else if (metric instanceof MetricCounter.MultipleValueMetric) { // Response codes - with several values
                    MetricCounter.MultipleValueMetric multipleValueMetric = (MetricCounter.MultipleValueMetric) metric;
                    Map<String, Long> subValues = multipleValueMetric.getBuildValues();
                    for (String subKey : subValues.keySet()) {
                        Entity value = createValueEntity(txn, build, metric.getKey(), subValues.get(subKey), true, subKey);

                        value.addLink(SampleValue.Link.TO_SAMPLE.name(), sample);

                        sample.addLink(SampleEntity.Link.TO_SAMPLE_VALUE.name(), value);
                        build.addLink(BuildEntity.Link.TO_SAMPLE_VALUE.name(), value);
                        artifact.addLink(ArtifactEntity.Link.TO_STAT_SAMPLES_VALUES.name(), value);
                    }
                }
            }
        }
    }

    private Entity createValueEntity(StoreTransaction txn, Entity build, String metric, long value, boolean hasSubMetric, String subMetric) {
        Entity sampleValue = txn.newEntity(SampleValue.TYPE);
        sampleValue.setProperty(SampleValue.Property.METRIC.name(), metric);
        sampleValue.setProperty(SampleValue.Property.BUILD_ID.name(), build.getProperty(BuildEntity.Property.BUILD_ID.name()));
        if (hasSubMetric)
            sampleValue.setProperty(SampleValue.Property.SUB_METRIC.name(), subMetric);

        sampleValue.setBlobString(SampleValue.Blob.VALUE.name(), String.valueOf(value));
        return sampleValue;
    }

    public void deleteBuildStatistic(@NotNull final Entity build) {
        EntityIterable values = build.getLinks(BuildEntity.Link.TO_SAMPLE_VALUE.name());
        if (!values.isEmpty()) {
            for (Entity value : values) {
                Entity sample = value.getLink(SampleValue.Link.TO_SAMPLE.name());
                if (sample != null) {
                    sample.deleteLink(SampleEntity.Link.TO_SAMPLE_VALUE.name(), value);
                    if (sample.getLinks(SampleEntity.Link.TO_SAMPLE_VALUE.name()).isEmpty()) {
                        // if sample has not values - remove sample
                        Entity buildType = sample.getLink(SampleEntity.Link.TO_BUILD_TYPE.name());
                        if (buildType != null) {
                            buildType.deleteLink(BuildTypeEntity.Link.TO_SAMPLES.name(), sample);
                        }
                        sample.delete();
                    }
                }
                build.deleteLink(BuildEntity.Link.TO_SAMPLE_VALUE.name(), value);
                value.delete();
            }
        }
    }

    /**
     * Raw statistic : SRT and RPS
     * @param buildID
     * @param artifactName
     * @param artifact
     * @return
     * @throws FileFormatException
     */
    public Map<TestID, SampleRawResults> getRawStatistic(@NotNull BuildID buildID, @NotNull String artifactName, @NotNull Entity artifact) throws FileFormatException {
        synchronized (rawCache) {
            Map<TestID, SampleRawResults> cached = rawCache.getRawResults(buildID, artifactName);
            if (cached == null) {
                RawDataReader reader = new RawDataReader();
                reader.processFile(artifact.getBlob(ArtifactEntity.Blob.CONTENT.name()));
                cached = reader.getSamples();
                rawCache.addRawStatistic(buildID, artifactName, cached);
            }
            return cached;
        }
    }

    @Nullable
    public SampleRawResults getSampleRawStatistic(@NotNull BuildID buildID, @NotNull String artifactName, @NotNull Entity artifact, @NotNull TestID testID) throws FileFormatException {
        synchronized (rawCache) {
            Map<TestID, SampleRawResults> cached = rawCache.getRawResults(buildID, artifactName);
            if (cached == null) {
                RawDataReader reader = new RawDataReader();
                reader.processFile(artifact.getBlob(ArtifactEntity.Blob.CONTENT.name()));
                cached = reader.getSamples();
                rawCache.addRawStatistic(buildID, artifactName, cached);
            }
            return cached.get(testID);
        }
    }

    /**
     * Aggregated statistic : Average, Min, Max, 90% line
     * @param buildType
     * @return
     */
    public Map<TestID, SampleStatistic> getStatistic(@NotNull Entity buildType) {
        EntityIterable entSamples = buildType.getLinks(BuildTypeEntity.Link.TO_SAMPLES.name());
        EntityIterable entBuilds = buildType.getLinks(BuildTypeEntity.Link.TO_BUILDS.name());
        if (entSamples.isEmpty() || entBuilds.isEmpty()) return Collections.emptyMap();

        Map<TestID, SampleStatistic> results = new HashMap<>();
        for (Entity entSample : entSamples) {
            String name = (String) entSample.getProperty(SampleEntity.Property.SAMPLE_NAME.name());
            String threadGroup = (String) entSample.getProperty(SampleEntity.Property.THREAD_GROUP.name());

            if (name == null || threadGroup == null) {
                continue;
            }
            TestID id = new TestID(threadGroup, name);
            SampleStatistic sample = results.get(id);
            if (sample == null) {
                sample = new SampleStatistic(id);
                results.put(id, sample);
            }

            EntityIterable values = entSample.getLinks(SampleEntity.Link.TO_SAMPLE_VALUE.name());
            for (Entity entValue : values) {
                String buildId = (String) entValue.getProperty(SampleValue.Property.BUILD_ID.name());
                String metric = (String) entValue.getProperty(SampleValue.Property.METRIC.name());
                String subMetric = (String) entValue.getProperty(SampleValue.Property.SUB_METRIC.name());
                long value = Long.valueOf(entValue.getBlobString(SampleValue.Blob.VALUE.name()));

                if (subMetric != null && !subMetric.isEmpty())
                    metric += " ( " + subMetric + " )";
                sample.addMetricValue(metric, Long.valueOf(buildId), value);
            }
        }
        return results;
    }

    @Nullable
    public SampleStatistic getSampleStatistic(@NotNull Entity buildType, @NotNull TestID testID){
        return getStatistic(buildType).get(testID);
    }

}