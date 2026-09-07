package pe.com.imperioperu.catalog.auth.config;

import java.util.stream.Stream;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.com.imperioperu.catalog.auth.domain.AppUserRepository;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {
    private final AppUserRepository users;
    public DatabaseUserDetailsService(AppUserRepository users) { this.users = users; }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = users.findByEmailIgnoreCase(username).orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
        var roleAuthorities = user.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()));
        var permissions = user.getPermissions().stream().map(permission -> new SimpleGrantedAuthority(permission.name()));
        return User.withUsername(user.getEmail())
            .password(user.getPasswordHash())
            .disabled(!user.isEnabled())
            .authorities(Stream.concat(roleAuthorities, permissions).toList())
            .build();
    }
}

