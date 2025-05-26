package com.bu.getactivecore.model.activity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.bu.getactivecore.model.users.Users;


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

    @EmbeddedId
    private UserActivityKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne
    @MapsId("activityId")
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role;


}