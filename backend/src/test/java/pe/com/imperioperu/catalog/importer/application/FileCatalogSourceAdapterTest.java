package pe.com.imperioperu.catalog.importer.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

class FileCatalogSourceAdapterTest {
    private final FileCatalogSourceAdapter adapter = new FileCatalogSourceAdapter();

    @Test
    void readsUtf8CsvAndNormalizesHeaders() throws Exception {
        var csv = "ISBN-13,Título,Autores\n9781234567890,Atlas clínico,Ana Pérez\n";
        var rows = adapter.read(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)), 100);
        assertThat(rows).hasSize(1);
        assertThat(rows.getFirst()).containsEntry("isbn13", "9781234567890")
            .containsEntry("title", "Atlas clínico")
            .containsEntry("authors", "Ana Pérez");
    }

    @Test
    void rejectsFormulaCellsInXlsx() throws Exception {
        byte[] workbook;
        try (var source = new XSSFWorkbook(); var output = new ByteArrayOutputStream()) {
            var sheet = source.createSheet("Books");
            var header = sheet.createRow(0);
            header.createCell(0).setCellValue("title");
            var row = sheet.createRow(1);
            row.createCell(0).setCellFormula("1+1");
            source.write(output);
            workbook = output.toByteArray();
        }
        assertThatThrownBy(() -> adapter.read(new ByteArrayInputStream(workbook), 100))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Formula cells");
    }

    @Test
    void enforcesRowLimit() {
        var csv = "title\nA\nB\n";
        assertThatThrownBy(() -> adapter.read(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)), 1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("maximum row count");
    }
}
