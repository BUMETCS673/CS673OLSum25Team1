package com.bu.getactivecore.service.activity.entity;

import lombok.Builder;
import lombok.Value;

/**
 * DTO for updating an existing activity.
 */
@Value
@Builder
public class ActivityDeleteRequestDto {

    private boolean force = Boolean.FALSE;

}
