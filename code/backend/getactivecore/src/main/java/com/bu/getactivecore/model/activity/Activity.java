package com.bu.getactivecore.model.activity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import org.hibernate.annotations.UuidGenerator;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "activity")
@Table(name = "activity")
public class Activity {
    @Id
    @UuidGenerator
    @Column(name = "id")
    private String id;

    @NotBlank(message = "Name cannot be blank")
    @Column(name = "name")
    private String name;

<<<<<<< HEAD:code/backend/getactivecore/src/main/java/com/bu/getactivecore/model/Activity.java
    @Column(name = "description")
    private String description;

    @NotBlank(message = "Location cannot be blank")
    @Column(name = "location")
    private String location;

    @NotNull
    @Column(name = "start_date_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm", iso = ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startDateTime;

    @NotNull
    @Column(name = "end_date_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm", iso = ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endDateTime;
=======
    /**
     * The start time of the activity in milliseconds since epoch.
     */
    @Column(name = "start_time")
    private Long startTimeMs;
>>>>>>> 89b24c05759778e50c71afa62f82337825c1b6ba:code/backend/getactivecore/src/main/java/com/bu/getactivecore/model/activity/Activity.java
}
