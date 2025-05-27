package com.bu.getactivecore.service.activity.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.UuidGenerator;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.bu.getactivecore.model.activity.Activity;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

/**
 * Activity DTO for exposing activity data.
 */
@Data
@Builder
public class ActivityDto {

    private String id;

    private String name;

    private String description;

    private String location;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    /**
     * Converts an Activity entity to an ActivityDto.
     *
     * @param activity the Activity entity
     * @return the ActivityDto
     */
    public static ActivityDto of(Activity activity) {
        return ActivityDto.builder()
                .id(activity.getId())
                .location(activity.getLocation())
                .name(activity.getName())
                .startDateTime(activity.getStartDateTime())
                .endDateTime(activity.getEndDateTime())
                .description(activity.getDescription())
                .build();     
    }
}
