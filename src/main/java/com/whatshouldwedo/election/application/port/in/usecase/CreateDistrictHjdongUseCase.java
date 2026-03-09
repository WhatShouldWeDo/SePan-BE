package com.whatshouldwedo.election.application.port.in.usecase;

import com.whatshouldwedo.election.application.port.in.input.command.CreateDistrictHjdongInput;
import com.whatshouldwedo.election.application.port.in.output.result.DistrictHjdongResult;

import java.util.List;

public interface CreateDistrictHjdongUseCase {
    List<DistrictHjdongResult> execute(String electionId, String districtId, CreateDistrictHjdongInput input);
}
