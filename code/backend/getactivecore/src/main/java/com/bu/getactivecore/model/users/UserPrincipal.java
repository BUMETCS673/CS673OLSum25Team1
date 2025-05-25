package com.bu.getactivecore.model.users;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom implementation of the {@link UserDetails} interface used by Spring Security.
 *
 * <p>This class acts as an adapter between the application's {@link Users} entity and the Spring Security framework.
 * It wraps a {@link Users} instance and provides the necessary user information (e.g., username, password, roles)
 * required for authentication and authorization processes.</p>
 *
 * <p>By implementing {@code UserDetails}, this class enables Spring Security to understand how to retrieve
 * user credentials and authorities for security checks during login and request authorization.</p>
 *
 * <p>Currently, each user is assigned a default authority role of <strong>"USER"</strong>.</p>
 */
public class UserPrincipal implements UserDetails {

    @Getter
    private final Users user;

    private final Collection<? extends GrantedAuthority> m_authorities;

    /**
     * Constructor that initializes the UserPrincipal with a {@link Users} entity.
     *
     * @param user the {@link Users} entity representing the user
     */
    public UserPrincipal(Users user) {
        this.user = user;
        m_authorities = Collections.singleton(new SimpleGrantedAuthority("USER"));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return m_authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }
}
