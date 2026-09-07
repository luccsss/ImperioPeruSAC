package pe.com.imperioperu.catalog.importer.application;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface CatalogSourceAdapter {
    boolean supports(String filename, String contentType);
    List<Map<String, String>> read(InputStream input, int maxRows) throws IOException;
}

