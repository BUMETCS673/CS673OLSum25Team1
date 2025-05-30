package com.bu.getactivecore.service.activity;

import com.bu.getactivecore.model.activity.Activity;
import com.bu.getactivecore.service.activity.api.ActivityApi;
import com.bu.getactivecore.service.activity.entity.ActivityDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ActivityRestControllerTest {

    @Autowired
    private MockMvc m_mvc;

    @MockitoBean
    private ActivityService m_activityService;

    @Autowired
    private ActivityApi m_activityApi;

    @WithMockUser
    @Test
    void givenActivities_expectedActivitiesReturned() throws Exception {

        List<ActivityDto> mockedActivities = List.of(
                ActivityDto.builder().name("Running").build(),
                ActivityDto.builder().name("Yoga").build(),
                ActivityDto.builder().name("Rock Climbing").build()
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
    void givenNoActivities_then_200Returned() throws Exception {

        List<ActivityDto> mockedActivities = Collections.emptyList();
        given(m_activityApi.getAllActivities()).willReturn(mockedActivities);
        m_mvc.perform(
                        get("/v1/activities").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("data").isEmpty());
    }

    @WithMockUser
    @Test
    void givenActivityFound_then_200Returned() throws Exception {

        Activity act1 = Activity.builder()
                .name("Rock Climbing")
                .startDateTime(LocalDateTime.now())
                .location("Location")
                .endDateTime(LocalDateTime.now())
                .build();
        List<ActivityDto> mockedActivities = new ArrayList<>();
        mockedActivities.add(ActivityDto.of(act1));
        given(m_activityApi.getActivityByName("Rock Climbing")).willReturn(mockedActivities);
        m_mvc.perform(
                        get("/v1/activity/{name}", "Rock Climbing").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("data[0].name").value("Rock Climbing"));
    }

    @WithMockUser
    @Test
    void givenActivityNotFound_then_200Returned() throws Exception {

        List<ActivityDto> mockedActivities = Collections.emptyList();
        given(m_activityApi.getActivityByName("Rock Climbing")).willReturn(mockedActivities);
        m_mvc.perform(
                        get("/v1/activity/{name}", "Rock Climbing").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        ;
    }

    // TODO: fix tests
    /*@WithMockUser
    @Test
    void givenCreateActivitySuccessfully_then_201Returned() throws Exception {
        UserDetails userDetails = User.withUsername("testuser").password("password").roles("USER").build();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    

        ActivityCreateRequestDto request = ActivityCreateRequestDto.builder()
                    .name("Rock Climbing")
                    .startDateTime(LocalDateTime.now())
                    .location("Location")
                    .endDateTime(LocalDateTime.now())
                    .build();
  
        Activity activity = ActivityCreateRequestDto.from(request); 

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); 

        String user_id = "1" ;          

        given(m_activityApi.createActivity(user_id, activity)).willReturn(activity);

        String json = mapper.writeValueAsString(request);
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
    void givenCreateActivityFailed_then_400Returned() throws Exception {
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