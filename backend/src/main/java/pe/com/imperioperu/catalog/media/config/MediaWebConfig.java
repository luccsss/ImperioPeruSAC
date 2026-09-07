package pe.com.imperioperu.catalog.media.config;

import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MediaWebConfig implements WebMvcConfigurer {
    private final String mediaLocation;
    public MediaWebConfig(@Value("${app.media.root:./storage}") String mediaRoot) {
        this.mediaLocation = Path.of(mediaRoot).toAbsolutePath().normalize().toUri().toString();
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/media/**").addResourceLocations(mediaLocation.endsWith("/") ? mediaLocation : mediaLocation + "/")
            .setCachePeriod(31536000);
    }
}

