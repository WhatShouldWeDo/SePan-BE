package com.whatshouldwedo.statistics.application.port.out;

import com.whatshouldwedo.statistics.domain.UploadHistory;

import java.util.List;

public interface UploadHistoryRepository {

    UploadHistory save(UploadHistory history);

    List<UploadHistory> findAll();
}
