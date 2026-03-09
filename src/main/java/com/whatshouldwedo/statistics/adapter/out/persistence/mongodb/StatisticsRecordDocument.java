package com.whatshouldwedo.statistics.adapter.out.persistence.mongodb;

import com.whatshouldwedo.region.domain.type.EAdminLevel;
import com.whatshouldwedo.statistics.domain.StatisticsRecord;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "statistics_records")
@CompoundIndexes({
        @CompoundIndex(name = "idx_category_year_item", def = "{'category_code': 1, 'data_year': 1, 'data.category_item': 1}"),
        @CompoundIndex(name = "idx_region_category_year", def = "{'region_code': 1, 'category_code': 1, 'data_year': 1}"),
        @CompoundIndex(name = "idx_version_region", def = "{'hjdong_version_id': 1, 'region_code': 1}")
})
public class StatisticsRecordDocument {

    @Id
    private String id;

    @Field("dataset_id")
    private String datasetId;

    @Field("category_code")
    private String categoryCode;

    @Field("region_code")
    private String regionCode;

    @Field("admin_level")
    private String adminLevel;

    @Field("hjdong_version_id")
    private String hjdongVersionId;

    @Field("data_year")
    private String dataYear;

    @Field("data")
    private Map<String, Object> data;

    @Field("created_at")
    private LocalDateTime createdAt;

    public static StatisticsRecordDocument fromDomain(StatisticsRecord record) {
        StatisticsRecordDocument doc = new StatisticsRecordDocument();
        doc.id = record.getId();
        doc.datasetId = record.getDatasetId();
        doc.categoryCode = record.getCategoryCode().name();
        doc.regionCode = record.getRegionCode();
        doc.adminLevel = record.getAdminLevel().name();
        doc.hjdongVersionId = record.getHjdongVersionId();
        doc.dataYear = record.getDataYear();
        doc.data = new HashMap<>(record.getData());
        doc.createdAt = record.getCreatedAt();
        return doc;
    }

    public StatisticsRecord toDomain() {
        return StatisticsRecord.reconstitute(
                id, datasetId,
                EStatisticsCategory.valueOf(categoryCode),
                regionCode,
                EAdminLevel.valueOf(adminLevel),
                hjdongVersionId, dataYear, data, createdAt
        );
    }
}
