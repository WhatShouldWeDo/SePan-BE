package com.whatshouldwedo.statistics.adapter.out.persistence.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StatisticsRecordMongoRepository extends MongoRepository<StatisticsRecordDocument, String> {

    List<StatisticsRecordDocument> findByRegionCodeAndCategoryCodeAndDataYear(
            String regionCode, String categoryCode, String dataYear);

    List<StatisticsRecordDocument> findByRegionCodeAndDataYear(String regionCode, String dataYear);

    List<StatisticsRecordDocument> findByHjdongVersionIdAndRegionCode(
            String hjdongVersionId, String regionCode);

    List<StatisticsRecordDocument> findByCategoryCodeAndDataYear(String categoryCode, String dataYear);

    List<StatisticsRecordDocument> findByDatasetId(String datasetId);

    List<StatisticsRecordDocument> findByRegionCodeInAndCategoryCodeAndDataYear(
            List<String> regionCodes, String categoryCode, String dataYear);
}
