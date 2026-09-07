package pe.com.imperioperu.catalog.auth.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {
    private static final int LIMIT = 10;
    private static final long WINDOW_SECONDS = 60;
    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        if (!"/api/v1/auth/login".equals(request.getRequestURI()) || !"POST".equals(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }
        String key = request.getRemoteAddr();
        long now = Instant.now().getEpochSecond();
        Window value = windows.compute(key, (ignored, current) -> current == null || now - current.startedAt >= WINDOW_SECONDS
            ? new Window(now, 1) : new Window(current.startedAt, current.count + 1));
        if (value.count > LIMIT) {
            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
            response.getWriter().write("{\"title\":\"Too Many Requests\",\"detail\":\"Intente nuevamente en un minuto\"}");
            return;
        }
        chain.doFilter(request, response);
    }
    private record Window(long startedAt, int count) {}
}

