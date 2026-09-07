package pe.com.imperioperu.catalog.seo.api;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.com.imperioperu.catalog.seo.domain.RedirectRuleRepository;

@Validated
@RestController
@RequestMapping("/api/v1/public/redirects")
public class PublicRedirectController {
    private final RedirectRuleRepository redirects;

    public PublicRedirectController(RedirectRuleRepository redirects) { this.redirects = redirects; }

    @GetMapping("/resolve")
    RedirectResponse resolve(@RequestParam @Pattern(regexp = "^/libro/[a-z0-9-]+/$") String path) {
        var rule = redirects.findBySourcePathAndActiveTrue(path).orElseThrow(() -> new EntityNotFoundException("Redirect not found"));
        return new RedirectResponse(rule.getDestinationPath(), rule.getStatusCode());
    }

    public record RedirectResponse(String destinationPath, int statusCode) {}
}
