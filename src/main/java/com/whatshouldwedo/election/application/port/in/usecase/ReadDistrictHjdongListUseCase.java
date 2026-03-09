package com.whatshouldwedo.election.application.port.in.usecase;

import com.whatshouldwedo.election.application.port.in.output.result.DistrictHjdongResult;

import java.util.List;

public interface ReadDistrictHjdongListUseCase {
    List<DistrictHjdongResult> execute(String electionId, String districtId);
}
