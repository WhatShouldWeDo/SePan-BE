package com.whatshouldwedo.statistics.adapter.out.external;

import com.whatshouldwedo.statistics.application.port.out.PublicDataCollector;
import com.whatshouldwedo.statistics.domain.StatisticsRecord;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public abstract class AbstractApiCollector implements PublicDataCollector {

    private final EStatisticsCategory category;
    private final String categoryItemName;

    protected AbstractApiCollector(EStatisticsCategory category, String categoryItemName) {
        this.category = category;
        this.categoryItemName = categoryItemName;
    }

    @Override
    public EStatisticsCategory getCategory() {
        return category;
    }

    @Override
    public String getCategoryItemName() {
        return categoryItemName;
    }

    @Override
    public List<StatisticsRecord> collect(String dataYear) {
        log.info("[{}] {} 수집 시작 - year={}", getProviderName(), categoryItemName, dataYear);
        try {
            List<StatisticsRecord> records = doCollect(dataYear);
            log.info("[{}] {} 수집 완료 - {}건", getProviderName(), categoryItemName, records.size());
            return records;
        } catch (Exception e) {
            log.error("[{}] {} 수집 실패 - error={}", getProviderName(), categoryItemName, e.getMessage(), e);
            throw e;
        }
    }

    protected abstract List<StatisticsRecord> doCollect(String dataYear);

    protected abstract String getProviderName();

    protected void logInfo(String message, Object... args) {
        log.info(message, args);
    }

    protected void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
