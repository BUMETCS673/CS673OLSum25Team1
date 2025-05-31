package com.bu.getactivecore.model.activity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;


@Entity
@Table(
        name = "user_activities",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "activity_id"}),
        indexes = {
                @Index(name = "idx_user_activities_userid_activityid", columnList = "user_id, activity_id")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserActivity {

    @Id
    @UuidGenerator
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "activity_id", nullable = false)
    private String activityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role;
}