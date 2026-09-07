package pe.com.imperioperu.catalog.media.application;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MediaStorageService {
    private static final Map<String, String> EXTENSIONS = Map.of(
        "image/jpeg", "jpg",
        "image/png", "png",
        "image/webp", "webp",
        "image/avif", "avif"
    );
    private final Path root;

    public MediaStorageService(@Value("${app.media.root:./storage}") String root) {
        this.root = Path.of(root).toAbsolutePath().normalize();
    }

    public StoredMedia store(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("Image is required");
        String type = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        String extension = EXTENSIONS.get(type);
        if (extension == null) throw new IllegalArgumentException("Only JPEG, PNG, WebP and AVIF images are accepted");
        byte[] bytes = file.getBytes();
        if (!signatureMatches(type, bytes)) throw new IllegalArgumentException("Image signature does not match its content type");
        String key = "books/" + UUID.randomUUID() + "." + extension;
        Path target = root.resolve(key).normalize();
        if (!target.startsWith(root)) throw new IllegalArgumentException("Invalid storage path");
        Files.createDirectories(target.getParent());
        Files.write(target, bytes, StandardOpenOption.CREATE_NEW);
        return new StoredMedia(key, type, bytes.length);
    }

    private boolean signatureMatches(String type, byte[] value) {
        if ("image/jpeg".equals(type)) return value.length > 3 && (value[0] & 0xff) == 0xff && (value[1] & 0xff) == 0xd8 && (value[2] & 0xff) == 0xff;
        if ("image/png".equals(type)) return value.length > 8 && (value[0] & 0xff) == 0x89 && value[1] == 'P' && value[2] == 'N' && value[3] == 'G';
        if ("image/webp".equals(type)) return value.length > 12 && ascii(value, 0, "RIFF") && ascii(value, 8, "WEBP");
        if ("image/avif".equals(type)) return value.length > 16 && ascii(value, 4, "ftyp") && (ascii(value, 8, "avif") || ascii(value, 8, "avis"));
        return false;
    }
    private boolean ascii(byte[] value, int offset, String expected) {
        if (value.length < offset + expected.length()) return false;
        for (int i = 0; i < expected.length(); i++) if (value[offset + i] != expected.charAt(i)) return false;
        return true;
    }
    public record StoredMedia(String storageKey, String mediaType, long bytes) {}
}

