package com.whatshouldwedo.statistics.adapter.in.web.command;

import com.whatshouldwedo.core.dto.ResponseDto;
import com.whatshouldwedo.statistics.application.service.DataCollectionService;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/data-collection")
@RequiredArgsConstructor
public class DataCollectionController {

    private final DataCollectionService dataCollectionService;

    @PostMapping("/all")
    public ResponseDto<Map<String, String>> collectAll(
            @RequestParam(defaultValue = "2024") String dataYear,
            @RequestParam(defaultValue = "false") boolean force) {
        log.info("전체 데이터 수집 요청 - year={}, force={}", dataYear, force);
        dataCollectionService.collectAll(dataYear, force);
        return ResponseDto.ok(Map.of(
                "status", "STARTED",
                "message", "전체 데이터 수집이 비동기로 시작되었습니다.",
                "dataYear", dataYear
        ));
    }

    @PostMapping("/category/{category}")
    public ResponseDto<Map<String, Object>> collectByCategory(
            @PathVariable EStatisticsCategory category,
            @RequestParam(defaultValue = "2024") String dataYear,
            @RequestParam(defaultValue = "false") boolean force) {
        log.info("카테고리별 데이터 수집 요청 - category={}, year={}, force={}", category, dataYear, force);
        Map<String, Object> result = dataCollectionService.collectByCategory(category, dataYear, force);
        return ResponseDto.ok(result);
    }

    @PostMapping("/category/{category}/item/{itemName}")
    public ResponseDto<Map<String, Object>> collectByItem(
            @PathVariable EStatisticsCategory category,
            @PathVariable String itemName,
            @RequestParam(defaultValue = "2024") String dataYear,
            @RequestParam(defaultValue = "false") boolean force) {
        log.info("항목별 데이터 수집 요청 - category={}, item={}, year={}, force={}",
                category, itemName, dataYear, force);
        Map<String, Object> result = dataCollectionService.collectByItem(category, itemName, dataYear, force);
        return ResponseDto.ok(result);
    }

    @PostMapping("/multi-year")
    public ResponseDto<Map<String, String>> collectMultiYear(
            @RequestParam(defaultValue = "2021") int startYear,
            @RequestParam(defaultValue = "2026") int endYear,
            @RequestParam(defaultValue = "false") boolean force) {
        log.info("멀티연도 데이터 수집 요청 - {}~{}, force={}", startYear, endYear, force);
        dataCollectionService.collectMultiYear(startYear, endYear, force);
        return ResponseDto.ok(Map.of(
                "status", "STARTED",
                "message", "멀티연도 데이터 수집이 비동기로 시작되었습니다.",
                "startYear", String.valueOf(startYear),
                "endYear", String.valueOf(endYear)
        ));
    }

    @GetMapping("/collectors")
    public ResponseDto<List<Map<String, String>>> listCollectors() {
        return ResponseDto.ok(dataCollectionService.listCollectors());
    }
}
