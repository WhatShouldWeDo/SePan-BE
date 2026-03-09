package com.whatshouldwedo.election.application.port.in.usecase;

import com.whatshouldwedo.election.application.port.in.input.command.CreateElectionInput;
import com.whatshouldwedo.election.application.port.in.output.result.ElectionResult;

public interface CreateElectionUseCase {
    ElectionResult execute(CreateElectionInput input);
}
