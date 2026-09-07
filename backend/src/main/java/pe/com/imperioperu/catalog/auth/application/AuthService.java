package pe.com.imperioperu.catalog.auth.application;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.com.imperioperu.catalog.auth.config.SecurityProperties;
import pe.com.imperioperu.catalog.auth.domain.AppUserRepository;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder encoder;
    private final SecurityProperties properties;
    private final AppUserRepository users;

    public AuthService(AuthenticationManager authenticationManager, JwtEncoder encoder, SecurityProperties properties, AppUserRepository users) {
        this.authenticationManager = authenticationManager;
        this.encoder = encoder;
        this.properties = properties;
        this.users = users;
    }

    @Transactional(readOnly = true)
    public TokenResponse login(String email, String password) {
        var authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        var user = users.findByEmailIgnoreCase(email).orElseThrow();
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(properties.accessTokenMinutes(), ChronoUnit.MINUTES);
        List<String> authorities = authentication.getAuthorities().stream().map(value -> value.getAuthority()).sorted().toList();
        var claims = JwtClaimsSet.builder()
            .issuer("imperio-peru-backend")
            .subject(user.getEmail())
            .issuedAt(issuedAt)
            .expiresAt(expiresAt)
            .claim("name", user.getDisplayName())
            .claim("authorities", authorities)
            .claim("tokenVersion", user.getTokenVersion())
            .build();
        var token = encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
        return new TokenResponse(token, expiresAt, user.getDisplayName(), authorities);
    }

    public record TokenResponse(String accessToken, Instant expiresAt, String displayName, List<String> authorities) {}
}

