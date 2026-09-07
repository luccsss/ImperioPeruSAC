package pe.com.imperioperu.catalog.importer.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pe.com.imperioperu.catalog.importer.application.ImportPreviewService;

@RestController
@RequestMapping("/api/v1/admin/imports")
public class ImportController {
    private final ImportPreviewService service;
    public ImportController(ImportPreviewService service) { this.service = service; }

    @PostMapping(path = "/preview", consumes = "multipart/form-data")
    @PreAuthorize("hasAuthority('IMPORT_PREVIEW')")
    ImportPreviewService.BatchResponse preview(@RequestParam(defaultValue = "AMOLCA_FILE") String sourceCode,
                                               @RequestPart("file") MultipartFile file) throws IOException {
        return service.preview(sourceCode, file);
    }

    @GetMapping @PreAuthorize("hasAuthority('IMPORT_PREVIEW')")
    List<ImportPreviewService.BatchResponse> list() { return service.list(); }

    @GetMapping("/{id}") @PreAuthorize("hasAuthority('IMPORT_PREVIEW')")
    ImportPreviewService.BatchResponse get(@PathVariable UUID id) { return service.get(id); }

    @PostMapping("/{id}/decisions") @PreAuthorize("hasAuthority('IMPORT_APPLY')")
    ImportPreviewService.BatchResponse decide(@PathVariable UUID id, @Valid @RequestBody DecisionsRequest request) {
        return service.decide(id, request.decisions());
    }

    @PostMapping("/{id}/apply") @PreAuthorize("hasAuthority('IMPORT_APPLY')")
    ImportPreviewService.BatchResponse apply(@PathVariable UUID id) { return service.apply(id); }

    public record DecisionsRequest(@NotEmpty List<ImportPreviewService.RowDecision> decisions) {}
}

