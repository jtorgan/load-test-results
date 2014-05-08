package load_test_service.storage.entities;

import jetbrains.exodus.database.Entity;
import jetbrains.exodus.database.EntityIterable;
import jetbrains.exodus.database.StoreTransaction;
import load_test_service.api.exeptions.FileFormatException;
import load_test_service.api.exeptions.LinkNotFound;
import load_test_service.api.model.BuildID;
import load_test_service.api.statistic.StatisticProperties;
import load_test_service.api.statistic.TestBuildStatistic;
import load_test_service.api.statistic.TestID;
import load_test_service.api.statistic.metrics.MetricCounter;
import load_test_service.statistic.readers.RawDataReader;
import load_test_service.statistic.readers.StatisticAggregatedReader;
import load_test_service.storage.queries.StatisticQuery;
import load_test_service.storage.schema.BuildEntity;
import load_test_service.storage.schema.BuildTypeEntity;
import load_test_service.storage.schema.SampleEntity;
import load_test_service.storage.schema.SampleValue;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticEntityManager implements StatisticQuery {

    /**
     * Calculate statistic and save it to store
     * @param txn
     * @param artifact
     * @param properties
     * @throws FileFormatException
     */
//  todo: is need to add link build => sample value
    public void countStatistic(@NotNull StoreTransaction txn, @NotNull final Entity build,
                               @NotNull final InputStream artifact, @NotNull final StatisticProperties properties) throws FileFormatException, LinkNotFound {
        Entity buildType = build.getLink(BuildEntity.Link.TO_BUILD_TYPE.name());
        if (buildType == null)
            throw new LinkNotFound(BuildEntity.Link.TO_BUILD_TYPE.name(), build);
        StatisticAggregatedReader reader = new StatisticAggregatedReader(properties);
        reader.processFile(artifact);

//        create existed samples map
        Map<String, Entity> existedSamples = new HashMap<>();
        for (Entity sample : buildType.getLinks(BuildTypeEntity.Link.TO_SAMPLES.name())) {
            String key = sample.getProperty(SampleEntity.Property.THREAD_GROUP.name()) + (String) sample.getProperty(SampleEntity.Property.SAMPLE_NAME.name());
            existedSamples.put(key, sample);
        }

        saveTestValues(txn, buildType, build, existedSamples, reader.getValuesBySamplers());
        if (properties.isCalculateTotal()) {
            saveTestValues(txn, buildType, build, existedSamples, reader.getTotalValuesByThreadGroups());
        }
    }

    private void saveTestValues(StoreTransaction txn, Entity buildType, Entity build, Map<String, Entity> existedSamples, Map<TestID, List<MetricCounter>> tests) {
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
                    Entity value = createValueEntity(txn, metric.getKey(), singleMetric.getBuildValue(), false, null);

                    value.addLink(SampleValue.Link.TO_SAMPLE.name(), sample);

                    sample.addLink(SampleEntity.Link.TO_SAMPLE_VALUE.name(), value);
                    build.addLink(BuildEntity.Link.TO_SAMPLE_VALUE.name(), value);

                } else if (metric instanceof MetricCounter.MultipleValueMetric) { // Response codes - with several values
                    MetricCounter.MultipleValueMetric multipleValueMetric = (MetricCounter.MultipleValueMetric) metric;
                    Map<String, Long> subValues = multipleValueMetric.getBuildValues();
                    for (String subKey : subValues.keySet()) {
                        Entity value = createValueEntity(txn, metric.getKey(), subValues.get(subKey), true, subKey);

                        value.addLink(SampleValue.Link.TO_SAMPLE.name(), sample);

                        sample.addLink(SampleEntity.Link.TO_SAMPLE_VALUE.name(), value);
                        build.addLink(BuildEntity.Link.TO_SAMPLE_VALUE.name(), value);
                    }
                }
            }
        }
    }

    private Entity createValueEntity(StoreTransaction txn, String metric, long value, boolean hasSubMetric, String subMetric) {
        Entity sampleValue = txn.newEntity(SampleValue.TYPE);
        sampleValue.setProperty(SampleValue.Property.METRIC.name(), metric);

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

    public Map<TestID, TestBuildStatistic> getRawStatistic(@NotNull InputStream artifact) throws FileFormatException {
        RawDataReader reader = new RawDataReader();
        reader.processFile(artifact);

        return reader.getTests();
    }

    public void getCountedStatistic(@NotNull StoreTransaction txn, @NotNull final BuildID buildID, @NotNull final Entity build) {

    }
}