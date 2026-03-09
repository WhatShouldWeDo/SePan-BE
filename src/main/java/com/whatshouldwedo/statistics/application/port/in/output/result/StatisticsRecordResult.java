package com.whatshouldwedo.statistics.application.port.in.output.result;

import com.whatshouldwedo.region.domain.type.EAdminLevel;
import com.whatshouldwedo.region.domain.type.EMappingType;
import com.whatshouldwedo.statistics.domain.StatisticsRecord;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import lombok.Getter;

import java.util.Map;

@Getter
public class StatisticsRecordResult {
    private final String id;
    private final EStatisticsCategory categoryCode;
    private final String regionCode;
    private final EAdminLevel adminLevel;
    private final String dataYear;
    private final Map<String, Object> data;

    // 매핑 메타 정보
    private final boolean isMapped;
    private final EMappingType mappingType;
    private final Double ratio;
    private final String sourceHjdongCode;

    private StatisticsRecordResult(String id, EStatisticsCategory categoryCode, String regionCode,
                                    EAdminLevel adminLevel, String dataYear, Map<String, Object> data,
                                    boolean isMapped, EMappingType mappingType,
                                    Double ratio, String sourceHjdongCode) {
        this.id = id;
        this.categoryCode = categoryCode;
        this.regionCode = regionCode;
        this.adminLevel = adminLevel;
        this.dataYear = dataYear;
        this.data = data;
        this.isMapped = isMapped;
        this.mappingType = mappingType;
        this.ratio = ratio;
        this.sourceHjdongCode = sourceHjdongCode;
    }

    public static StatisticsRecordResult from(StatisticsRecord record) {
        return new StatisticsRecordResult(
                record.getId(), record.getCategoryCode(), record.getRegionCode(),
                record.getAdminLevel(), record.getDataYear(), record.getData(),
                false, null, null, null
        );
    }

    public static StatisticsRecordResult fromMapped(StatisticsRecord record,
                                                      EMappingType mappingType,
                                                      Double ratio, String sourceHjdongCode) {
        return new StatisticsRecordResult(
                record.getId(), record.getCategoryCode(), record.getRegionCode(),
                record.getAdminLevel(), record.getDataYear(), record.getData(),
                true, mappingType, ratio, sourceHjdongCode
        );
    }
}
