package com.bu.getactivecore.service.activity.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

public record ActivityParticipantRequestDto(@NotBlank(message = "Activity ID cannot be blank") String activityId) {

}