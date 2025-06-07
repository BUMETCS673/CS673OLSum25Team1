package com.getactive.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Activity {
    @Id @GeneratedValue
    private Long id;
    private String title;
    private String location;
    private LocalDateTime datetime;

    // Getters and Setters
}
