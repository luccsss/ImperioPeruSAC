package pe.com.imperioperu.catalog.auth.config;

import java.util.EnumSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pe.com.imperioperu.catalog.auth.domain.AdminRole;
import pe.com.imperioperu.catalog.auth.domain.AppUser;
import pe.com.imperioperu.catalog.auth.domain.AppUserRepository;
import pe.com.imperioperu.catalog.auth.domain.Permission;

@Component
public class BootstrapAdminInitializer implements ApplicationRunner {
    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String password;

    public BootstrapAdminInitializer(AppUserRepository users, PasswordEncoder passwordEncoder,
                                     @Value("${app.bootstrap-admin.email}") String email,
                                     @Value("${app.bootstrap-admin.password}") String password) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.password = password;
    }

    @Override @Transactional
    public void run(ApplicationArguments args) {
        if (users.count() > 0) return;
        if (password == null || password.length() < 12) throw new IllegalStateException("ADMIN_PASSWORD must contain at least 12 characters");
        users.save(new AppUser(email, passwordEncoder.encode(password), "Administrador inicial",
            EnumSet.of(AdminRole.ADMIN), EnumSet.allOf(Permission.class)));
    }
}

