package com.bu.getactivecore.service.activity.entity;

import java.time.LocalDateTime;

import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.bu.getactivecore.model.activity.Activity;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

/**
 * The DTO for creating a new activity.
 */
@Builder
public record ActivityCreateRequestDto(
        @NotBlank(message = "Name cannot be blank") @Size(max = 250, message = "The length of name must be less or equal to 250") String name,
        @NotBlank(message = "Description cannot be blank") @Size(max = 250, message = "The length of description must be less or equal to 250") String description,
        @NotBlank(message = "Location cannot be blank") @Size(max = 250, message = "The length of location must be less or equal to 250") String location,
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm", iso = ISO.DATE_TIME) @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm") @NotNull(message = "Start DateTime cannot be blank") LocalDateTime startDateTime,
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm", iso = ISO.DATE_TIME) @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm") @NotNull(message = "End DateTime cannot be blank") LocalDateTime endDateTime) {

    public static Activity from(ActivityCreateRequestDto request) {
        return Activity.builder().location(request.location()).name(request.name())
                .startDateTime(request.startDateTime()).endDateTime(request.endDateTime())
                .description(request.description()).build();
    }
}
