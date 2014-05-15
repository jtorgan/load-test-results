package load_service.web;

import load_test_service.statistic.BaseMetrics;

import java.io.*;
import java.util.*;

public class DefaultChartSettings {
    private static final String DEFAULT_SETTINGS_PROPERTIES = "default.chart.settings.properties";

    private final Map<String, Boolean> deselected;
    private final String buildType;

    public DefaultChartSettings(String buildType) {
        this.buildType = buildType;

        deselected = new HashMap<>();
        for (BaseMetrics metric : BaseMetrics.values()) {
            if (metric != BaseMetrics.RESPONSE_CODE)
                deselected.put(metric.getKey(), false);
        }

        if(new File(DEFAULT_SETTINGS_PROPERTIES).exists()) {
            Properties props = new Properties();
            try (InputStream input = new FileInputStream(DEFAULT_SETTINGS_PROPERTIES)) {
                props.load(input);

                String deselectedSeries = props.getProperty(buildType);
                if (deselectedSeries != null) {
                    for (String key : deselectedSeries.split(";")) {
                        deselected.put(key, true);
                    }
                }
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
    }

    public void setDeselectedMetric(BaseMetrics metric) {
        deselected.put(metric.getKey(), true);
    }

    public Map<String, Boolean> getSettings() {
        return deselected;
    }

    public boolean storeSettings() {
        Properties props = new Properties();
        try (OutputStream output = new FileOutputStream(DEFAULT_SETTINGS_PROPERTIES)) {
            StringBuilder builder = new StringBuilder();
            for (String metrics : deselected.keySet()) {
                if (deselected.get(metrics))
                    builder.append(metrics).append(";");
            }
            props.setProperty(buildType, builder.toString());
            props.store(output, null);
            return true;
        } catch (IOException io) {
            io.printStackTrace();
            return false;
        }
    }
}
