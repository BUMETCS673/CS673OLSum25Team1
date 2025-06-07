package com.bu.getactivecore.service.activity.entity;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActivityPageableRequest {

    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int size = 10;

    @Builder.Default
    private String sortBy = "id";

    @Builder.Default
    private boolean ascending = Boolean.TRUE;
    
}