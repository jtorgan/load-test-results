package load_test_service.storage.entities;

import jetbrains.exodus.database.Entity;
import jetbrains.exodus.database.EntityIterable;
import jetbrains.exodus.database.StoreTransaction;
import load_test_service.api.exeptions.FileFormatException;
import load_test_service.api.model.BuildID;
import load_test_service.api.statistic.StatisticProperties;
import load_test_service.api.statistic.TestBuildStatistic;
import load_test_service.api.statistic.TestID;
import load_test_service.api.statistic.metrics.MetricCounter;
import load_test_service.statistic.readers.RawDataReader;
import load_test_service.statistic.readers.StatisticAggregatedReader;
import load_test_service.storage.queries.StatisticQuery;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class StatisticEntityManager implements StatisticQuery {
//  ENTITY TYPES
    public static final String SAMPLE_ENTITY_TYPE = "sampler"; // test sampler
    public static final String TOTAL_THREAD_GROUP_ENTITY_TYPE = "totalTG"; // total by thread group

    public static final String RESPONSE_CODE_ENTITY_TYPE = "respCodes"; // test sampler


//  PROPERTIES
    public static final String PROPERTY_TEST_NAME = "tsName";
    public static final String PROPERTY_THREAD_GROUP_NAME = "ttgName";

//  BLOBS

//  LINKS
    public static final String LINK_TO_STAT_RESP_CODES = "stat-to-resp-codes";
    public static final String LINK_TO_STAT_SAMPLER = "to-sample";
    public static final String LINK_TO_STAT_TOTAL = "to-total";

    public void countStatistic(@NotNull StoreTransaction txn, @NotNull final BuildID buildID, @NotNull final Entity build,
                               @NotNull final InputStream artifact, @NotNull final StatisticProperties properties) throws FileFormatException {
        StatisticAggregatedReader reader = new StatisticAggregatedReader(properties);
        reader.processFile(artifact);
        Map<TestID, List<MetricCounter>> tests = reader.getValuesBySamplers();
        for(TestID testID : tests.keySet()) {
            Entity test = txn.newEntity(SAMPLE_ENTITY_TYPE);
            test.setProperty(PROPERTY_TEST_NAME, testID.getTestName());

            test.setProperty(BuildEntityManager.PROPERTY_BUILD_ID, buildID.getBuildID());
            test.setProperty(BuildTypeEntityManager.PROPERTY_BT_ID, buildID.getBuildTypeID());

            if (!testID.getThreadGroup().isEmpty())
                test.setProperty(PROPERTY_THREAD_GROUP_NAME, testID.getThreadGroup());

            setStatisticValues(txn, test, tests.get(testID));

            build.setLink(LINK_TO_STAT_SAMPLER, test);
        }
        if (properties.isCalculateTotal()) {
            Map<TestID, List<MetricCounter>> totals = reader.getTotalValuesByThreadGroups();
            for(TestID testID : totals.keySet()) {
                Entity test = txn.newEntity(TOTAL_THREAD_GROUP_ENTITY_TYPE);

                if (!testID.getThreadGroup().isEmpty())
                    test.setProperty(PROPERTY_THREAD_GROUP_NAME, testID.getThreadGroup());

                test.setProperty(BuildEntityManager.PROPERTY_BUILD_ID, buildID.getBuildID());
                test.setProperty(BuildTypeEntityManager.PROPERTY_BT_ID, buildID.getBuildTypeID());

                setStatisticValues(txn, test, totals.get(testID));

                build.setLink(LINK_TO_STAT_TOTAL, test);
            }
        }
    }

    public void deleteBuildStatistic(@NotNull final Entity build) {
        EntityIterable samplers = build.getLinks(LINK_TO_STAT_SAMPLER);
        if (!samplers.isEmpty()) {
            for (Entity sampler : samplers) {
                deleteLinkedStatisticValues(sampler);
                sampler.delete();
            }
        }
        build.deleteLinks(LINK_TO_STAT_SAMPLER);

        EntityIterable totals = build.getLinks(LINK_TO_STAT_TOTAL);
        if (!totals.isEmpty()) {
            for (Entity total : totals) {
                deleteLinkedStatisticValues(total);
                total.delete();
            }
        }
        build.deleteLinks(LINK_TO_STAT_TOTAL);

    }

//  TODO: queries to extract statistic public void getBuildStatistic


    private void setStatisticValues(StoreTransaction txn, Entity test, List<MetricCounter> counters) {
        for (MetricCounter counter : counters) {
            String key = counter.getKey();
            if (counter instanceof MetricCounter.SingleValueMetric) {
                test.setProperty(key, ((MetricCounter.SingleValueMetric) counter).getBuildValue());
                continue;
            }
            if (counter instanceof MetricCounter.MultipleValueMetric) {
                addLinkedStatisticValues(txn, test, ((MetricCounter.MultipleValueMetric) counter).getBuildValues());
            }
        }
    }

    private void addLinkedStatisticValues(StoreTransaction txn, Entity test, Map<String, Long> values) {
        Entity respCodes = txn.newEntity(RESPONSE_CODE_ENTITY_TYPE);
        for (String subKey : values.keySet()) {
            respCodes.setProperty(subKey, values.get(subKey));
        }
        test.addLink(LINK_TO_STAT_RESP_CODES, respCodes);
    }

    private void deleteLinkedStatisticValues(Entity test) {
        Entity resCode = test.getLink(RESPONSE_CODE_ENTITY_TYPE);
        if (resCode != null)
            resCode.delete();
        test.deleteLinks(RESPONSE_CODE_ENTITY_TYPE);
    }

    public Map<TestID, TestBuildStatistic> getRawStatistic(@NotNull InputStream artifact) throws FileFormatException {
        RawDataReader reader = new RawDataReader();
        reader.processFile(artifact);

        return reader.getTests();
    }

}