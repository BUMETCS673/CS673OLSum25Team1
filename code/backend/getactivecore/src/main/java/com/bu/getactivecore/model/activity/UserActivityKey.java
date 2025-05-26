package com.bu.getactivecore.model.activity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;


//https://www.baeldung.com/jpa-many-to-many

@Embeddable
class UserActivityKey implements Serializable {

    @Column(name = "user_id")
    Long userId;

    @Column(name = "activity_id")
    Long activityId;
}