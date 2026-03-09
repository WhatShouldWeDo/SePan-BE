package com.whatshouldwedo.statistics.adapter.out.persistence.mongodb;

import com.whatshouldwedo.statistics.application.port.out.StatisticsRecordRepository;
import com.whatshouldwedo.statistics.domain.StatisticsRecord;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class StatisticsRecordMongoAdapter implements StatisticsRecordRepository {

    private final StatisticsRecordMongoRepository mongoRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public StatisticsRecord save(StatisticsRecord record) {
        return mongoRepository.save(StatisticsRecordDocument.fromDomain(record)).toDomain();
    }

    @Override
    public List<StatisticsRecord> saveAll(List<StatisticsRecord> records) {
        List<StatisticsRecordDocument> docs = records.stream()
                .map(StatisticsRecordDocument::fromDomain).toList();
        return mongoRepository.saveAll(docs).stream()
                .map(StatisticsRecordDocument::toDomain).toList();
    }

    @Override
    public List<StatisticsRecord> findByRegionCodeAndCategoryAndYear(
            String regionCode, EStatisticsCategory category, String dataYear) {
        return mongoRepository.findByRegionCodeAndCategoryCodeAndDataYear(
                regionCode, category.name(), dataYear
        ).stream().map(StatisticsRecordDocument::toDomain).toList();
    }

    @Override
    public List<StatisticsRecord> findByRegionCodeAndYear(String regionCode, String dataYear) {
        return mongoRepository.findByRegionCodeAndDataYear(regionCode, dataYear).stream()
                .map(StatisticsRecordDocument::toDomain).toList();
    }

    @Override
    public List<StatisticsRecord> findByHjdongVersionIdAndRegionCode(
            String hjdongVersionId, String regionCode) {
        return mongoRepository.findByHjdongVersionIdAndRegionCode(hjdongVersionId, regionCode).stream()
                .map(StatisticsRecordDocument::toDomain).toList();
    }

    @Override
    public List<StatisticsRecord> findByCategoryAndYear(EStatisticsCategory category, String dataYear) {
        return mongoRepository.findByCategoryCodeAndDataYear(category.name(), dataYear).stream()
                .map(StatisticsRecordDocument::toDomain).toList();
    }

    @Override
    public List<StatisticsRecord> findByRegionCodesAndCategoryAndYear(
            List<String> regionCodes, EStatisticsCategory category, String dataYear) {
        return mongoRepository.findByRegionCodeInAndCategoryCodeAndDataYear(
                regionCodes, category.name(), dataYear
        ).stream().map(StatisticsRecordDocument::toDomain).toList();
    }

    @Override
    public long deleteByCategoryAndCategoryItemAndYear(
            EStatisticsCategory category, String categoryItemName, String dataYear) {
        Query query = new Query(Criteria.where("category_code").is(category.name())
                .and("data_year").is(dataYear)
                .and("data.category_item").is(categoryItemName));
        return mongoTemplate.remove(query, StatisticsRecordDocument.class).getDeletedCount();
    }
}
