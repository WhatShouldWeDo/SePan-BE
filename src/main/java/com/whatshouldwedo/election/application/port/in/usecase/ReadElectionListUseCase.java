package com.whatshouldwedo.election.application.port.in.usecase;

import com.whatshouldwedo.election.application.port.in.output.result.ElectionResult;

import java.util.List;

public interface ReadElectionListUseCase {
    List<ElectionResult> execute();
}
