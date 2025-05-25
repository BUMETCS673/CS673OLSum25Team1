package com.bu.getactivecore.service.activity;

import com.bu.getactivecore.service.activity.api.ActivityApi;
import com.bu.getactivecore.model.activity.Activity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.bu.getactivecore.service.activity.ActivityService;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
class ActivityRestControllerTest {

    @Autowired
    private MockMvc m_mvc;

    @MockitoBean
    private ActivityService m_activityService;

    @Autowired
    private ActivityApi m_activityApi;

    @Autowired
	private WebApplicationContext context;

    @BeforeEach
	public void setup() {
		m_mvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(SecurityMockMvcConfigurers.springSecurity()) 
				.build();
	}

    @WithMockUser
    @Test
    public void givenActivities_expectedActivitiesReturned() throws Exception {

        List<Activity> mockedActivities = List.of(
                Activity.builder().name("Running").build(),
                Activity.builder().name("Yoga").build(),
                Activity.builder().name("Rock Climbing").build()
        );
        given(m_activityApi.getAllActivities()).willReturn(mockedActivities);
        m_mvc.perform(
                        get("/v1/activities").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("data[0].name").value("Running"))
                .andExpect(jsonPath("data[1].name").value("Yoga"))
                .andExpect(jsonPath("data[2].name").value("Rock Climbing"));
    }

    @WithMockUser
    @Test
    public void givenNoActivities_then_200Returned() throws Exception {

        List<Activity> mockedActivities = Collections.emptyList();
        given(m_activityApi.getAllActivities()).willReturn(mockedActivities);
        m_mvc.perform(
                        get("/v1/activities").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("data").isEmpty());
    }

    @WithMockUser
    @Test
    public void givenActivityFound_then_200Returned() throws Exception {

        Activity act1 = Activity.builder()
                    .name("Rock Climbing")
                    .startDateTime(LocalDateTime.now())
                    .location("Location")
                    .endDateTime(LocalDateTime.now())
                    .build();
        List<Activity> mockedActivities = new ArrayList<>();
        mockedActivities.add(act1);
        given(m_activityApi.getActivityByName("Rock Climbing")).willReturn(mockedActivities);
        m_mvc.perform(
                        get("/v1/activity/{name}","Rock Climbing").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("data[0].name").value("Rock Climbing"));
    }

    @WithMockUser
    @Test
    public void givenActivityNotFound_then_404Returned() throws Exception {

        List<Activity> mockedActivities = Collections.emptyList();
        given(m_activityApi.getActivityByName("Rock Climbing")).willReturn(mockedActivities);
        m_mvc.perform(
                        get("/v1/activity/{name}","Rock Climbing").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
;
    }

   /* @WithMockUser
    @Test
    public void givenCreateActivitySuccessfully_then_201Returned() throws Exception {
        Activity act1 = Activity.builder()
                    .name("Rock Climbing")
                    .startDateTime(LocalDateTime.now())
                    .location("Location")
                    .endDateTime(LocalDateTime.now())
                    .build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); 

        String user_id = "1" ;          

        given(m_activityApi.createActivity(user_id, act1)).willReturn(act1);

        String json = mapper.writeValueAsString(act1);

          m_mvc.perform( MockMvcRequestBuilders
	      .post("/v1/activity")
          .with(csrf())
	      .content(json)
	      .contentType(MediaType.APPLICATION_JSON)
	      .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isCreated());
    }

    @WithMockUser
    @Test
    public void givenCreateActivityFailed_then_400Returned() throws Exception {
        Activity act1 = Activity.builder()
                    .name("Rock Climbing")
                    .startDateTime(LocalDateTime.now())
                    .endDateTime(LocalDateTime.now())
                    .build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); 
        
        String user_id="1";           

        given(m_activityApi.createActivity(user_id, act1)).willReturn(act1);

        String json = mapper.writeValueAsString(act1);

          m_mvc.perform( MockMvcRequestBuilders
	      .post("/v1/activity")
          .with(csrf())
	      .content(json)
	      .contentType(MediaType.APPLICATION_JSON)
	      .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isBadRequest());
    }*/
    
}