package com.whatshouldwedo.region.application.port.in.output.result;

import com.whatshouldwedo.region.domain.HjdongVersion;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class HjdongVersionResult {
    private final String id;
    private final String versionName;
    private final LocalDate effectiveDate;
    private final boolean isActive;
    private final String description;

    private HjdongVersionResult(String id, String versionName, LocalDate effectiveDate,
                                 boolean isActive, String description) {
        this.id = id;
        this.versionName = versionName;
        this.effectiveDate = effectiveDate;
        this.isActive = isActive;
        this.description = description;
    }

    public static HjdongVersionResult from(HjdongVersion version) {
        return new HjdongVersionResult(
                version.getId().getValue().toString(),
                version.getVersionName(),
                version.getEffectiveDate(),
                version.isActive(),
                version.getDescription()
        );
    }
}
