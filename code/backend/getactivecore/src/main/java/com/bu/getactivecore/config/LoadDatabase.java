package com.bu.getactivecore.config;

import com.bu.getactivecore.model.activity.Activity;
import com.bu.getactivecore.model.users.Users;
import com.bu.getactivecore.repository.ActivityRepository;
import com.bu.getactivecore.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
public class LoadDatabase {
    @Bean
    CommandLineRunner demoPreloadData(ActivityRepository activityRepo, UserRepository userRepo) {
        return args -> {
            Activity act1 = Activity.builder()
                    .activityName("Rock Climbing")
                    .startTimeMs(System.currentTimeMillis())
                    .build();
            Activity act2 = Activity.builder()
                    .activityName("Yoga")
                    .startTimeMs(System.currentTimeMillis())
                    .build();
            Activity act3 = Activity.builder()
                    .activityName("Running")
                    .startTimeMs(System.currentTimeMillis())
                    .build();

            Users user1 = Users.builder()
                    .email("arsh@")
                    .username("arsh")
                    .password("arsh")
                    .build();
            Users user2 = Users.builder()
                    .email("arsh2@")
                    .username("arsh2")
                    .password("arsh2")
                    .build();

            log.info("Preloading {}", activityRepo.save(act1));
            log.info("Preloading {}", activityRepo.save(act2));
            log.info("Preloading {}", activityRepo.save(act3));
            log.info("Preloading {}", userRepo.save(user1));
            log.info("Preloading {}", userRepo.save(user2));
        };
    }
}
