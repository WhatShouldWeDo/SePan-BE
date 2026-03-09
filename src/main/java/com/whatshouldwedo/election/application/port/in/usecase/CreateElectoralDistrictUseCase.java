package com.whatshouldwedo.election.application.port.in.usecase;

import com.whatshouldwedo.election.application.port.in.input.command.CreateElectoralDistrictInput;
import com.whatshouldwedo.election.application.port.in.output.result.ElectoralDistrictResult;

public interface CreateElectoralDistrictUseCase {
    ElectoralDistrictResult execute(String electionId, CreateElectoralDistrictInput input);
}
