package com.whatshouldwedo.statistics.adapter.in.web.query;

import com.whatshouldwedo.core.dto.ResponseDto;
import com.whatshouldwedo.statistics.application.port.in.output.result.PublicDataSourceResult;
import com.whatshouldwedo.statistics.application.port.in.output.result.StatisticsCategoryResult;
import com.whatshouldwedo.statistics.application.port.in.output.result.StatisticsRecordResult;
import com.whatshouldwedo.statistics.application.service.PublicDataSourceService;
import com.whatshouldwedo.statistics.application.service.StatisticsQueryService;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class StatisticsQueryController {

    private final StatisticsQueryService statisticsQueryService;
    private final PublicDataSourceService publicDataSourceService;

    @GetMapping("/categories")
    public ResponseDto<List<StatisticsCategoryResult>> getCategories() {
        return ResponseDto.ok(statisticsQueryService.getCategories());
    }

    @GetMapping("/category/{category}")
    public ResponseDto<List<StatisticsRecordResult>> getByCategory(
            @PathVariable EStatisticsCategory category,
            @RequestParam String regionCode,
            @RequestParam(required = false) String dataYear) {
        return ResponseDto.ok(statisticsQueryService.getByCategoryAndRegion(category, regionCode, dataYear));
    }

    @GetMapping("/region/{regionCode}")
    public ResponseDto<List<StatisticsRecordResult>> getByRegion(
            @PathVariable String regionCode,
            @RequestParam(required = false) String dataYear) {
        return ResponseDto.ok(statisticsQueryService.getByRegion(regionCode, dataYear));
    }

    @GetMapping("/data-sources")
    public ResponseDto<List<PublicDataSourceResult>> getDataSources() {
        return ResponseDto.ok(
                publicDataSourceService.getAllDataSources().stream()
                        .map(PublicDataSourceResult::from).toList()
        );
    }

    @GetMapping("/data-sources/category/{category}")
    public ResponseDto<List<PublicDataSourceResult>> getDataSourcesByCategory(
            @PathVariable EStatisticsCategory category) {
        return ResponseDto.ok(
                publicDataSourceService.getDataSourcesByCategory(category).stream()
                        .map(PublicDataSourceResult::from).toList()
        );
    }
}
