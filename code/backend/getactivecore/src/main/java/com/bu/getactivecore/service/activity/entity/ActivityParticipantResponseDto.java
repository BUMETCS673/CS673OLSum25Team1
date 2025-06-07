package com.bu.getactivecore.service.activity.entity;

import lombok.Value;

import java.util.List;


@Value
public class ActivityParticipantResponseDto {

    List<UserActivityDto> activities;
}
