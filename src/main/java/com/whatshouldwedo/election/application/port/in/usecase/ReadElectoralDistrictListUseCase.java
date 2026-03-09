package com.whatshouldwedo.election.application.port.in.usecase;

import com.whatshouldwedo.election.application.port.in.output.result.ElectoralDistrictResult;

import java.util.List;

public interface ReadElectoralDistrictListUseCase {
    List<ElectoralDistrictResult> execute(String electionId);
}
