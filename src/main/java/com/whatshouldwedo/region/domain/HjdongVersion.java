package com.whatshouldwedo.region.domain;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class HjdongVersion {
    private final HjdongVersionId id;
    private final String versionName;
    private final LocalDate effectiveDate;
    private boolean isActive;
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private HjdongVersion(HjdongVersionId id, String versionName, LocalDate effectiveDate) {
        this.id = Objects.requireNonNull(id);
        this.versionName = Objects.requireNonNull(versionName);
        this.effectiveDate = Objects.requireNonNull(effectiveDate);
        this.isActive = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static HjdongVersion create(HjdongVersionId id, String versionName,
                                         LocalDate effectiveDate, String description) {
        HjdongVersion version = new HjdongVersion(id, versionName, effectiveDate);
        version.description = description;
        return version;
    }

    public static HjdongVersion reconstitute(HjdongVersionId id, String versionName,
                                               LocalDate effectiveDate, boolean isActive,
                                               String description,
                                               LocalDateTime createdAt, LocalDateTime updatedAt) {
        HjdongVersion version = new HjdongVersion(id, versionName, effectiveDate);
        version.isActive = isActive;
        version.description = description;
        version.createdAt = createdAt;
        version.updatedAt = updatedAt;
        return version;
    }

    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }
}
