package com.bu.getactivecore.repository;

import com.bu.getactivecore.model.activity.Activity;
import com.bu.getactivecore.model.activity.ActivityParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActivityParticipantRepository extends JpaRepository<Activity, String> {

    @Query("SELECT new com.bu.getactivecore.model.activity.ActivityParticipant(a.id, a.name, a.description, a.location, a.startDateTime, a.endDateTime, ua.role) " +
           "FROM activity a, UserActivity ua " +
           "WHERE a.id = ua.activityId AND ua.userId = :userId")
    List<ActivityParticipant> findActivitiesByUserId(@Param("userId") String userId);
}
