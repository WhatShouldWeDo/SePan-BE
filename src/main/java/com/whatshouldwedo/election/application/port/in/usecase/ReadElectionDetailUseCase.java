package com.whatshouldwedo.election.application.port.in.usecase;

import com.whatshouldwedo.election.application.port.in.output.result.ElectionResult;

public interface ReadElectionDetailUseCase {
    ElectionResult execute(String electionId);
}
