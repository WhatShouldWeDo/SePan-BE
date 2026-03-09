package com.whatshouldwedo.election.domain;

import com.whatshouldwedo.core.BaseId;

import java.util.UUID;

public class ElectoralDistrictHjdongId extends BaseId<UUID> {

    private ElectoralDistrictHjdongId(UUID value) {
        super(value);
    }

    public static ElectoralDistrictHjdongId generate() {
        return new ElectoralDistrictHjdongId(generateUUID());
    }

    public static ElectoralDistrictHjdongId of(UUID value) {
        return new ElectoralDistrictHjdongId(value);
    }

    public static ElectoralDistrictHjdongId of(String value) {
        return new ElectoralDistrictHjdongId(UUID.fromString(value));
    }
}
