package com.bu.getactivecore.service.activity.entity;

import com.bu.getactivecore.model.activity.ActivityParticipant;
import com.bu.getactivecore.model.activity.RoleType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Builder
public class ActivityParticipantDto {
    private String id;

    private String name;

    private String description;

    private String location;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private RoleType role;

    /**
     * Converts a ActivityParticipant entity to a ActivityParticipantDto.
     *
     * @param activityParticipant the ActivityParticipant entity
     * @return the ActivityParticipantDto
     */
    public static ActivityParticipantDto of(ActivityParticipant activityParticipant) {
        return ActivityParticipantDto.builder()
                .id(activityParticipant.getId())
                .name(activityParticipant.getName())
                .description(activityParticipant.getDescription())
                .location(activityParticipant.getLocation())
                .startDateTime(activityParticipant.getStartDateTime())
                .endDateTime(activityParticipant.getEndDateTime())
                .role(activityParticipant.getRole())
                .build();
    }
}
