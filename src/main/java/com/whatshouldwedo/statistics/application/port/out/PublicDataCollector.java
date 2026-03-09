package com.whatshouldwedo.statistics.application.port.out;

import com.whatshouldwedo.statistics.domain.StatisticsRecord;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;

import java.util.List;

public interface PublicDataCollector {

    EStatisticsCategory getCategory();

    String getCategoryItemName();

    List<StatisticsRecord> collect(String dataYear);

    default int getMaxRetries() {
        return 3;
    }

    default long getRetryDelayMs() {
        return 2000L;
    }
}
