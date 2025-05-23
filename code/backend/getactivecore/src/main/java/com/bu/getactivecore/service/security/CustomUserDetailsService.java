package com.bu.getactivecore.service.security;

import com.bu.getactivecore.model.UserPrincipal;
import com.bu.getactivecore.model.Users;
import com.bu.getactivecore.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository m_userRepo;

    public CustomUserDetailsService(UserRepository userRepo) {
        m_userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<Users> user = m_userRepo.findByUsername(username);
        if (user.isEmpty()) {
            log.warn("User not found with username: {}", username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        // TODO: Get authorities
        return new UserPrincipal(user.get());
    }

}
