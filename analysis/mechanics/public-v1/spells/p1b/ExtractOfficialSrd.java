import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.awt.geom.Rectangle2D;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Analysis-only PDF extraction. PDF page and column remain independent evidence coordinates. */
public final class ExtractOfficialSrd {
    public static void main(String[] arguments) throws Exception {
        if (arguments.length != 2) {
            throw new IllegalArgumentException("Usage: ExtractOfficialSrd input.pdf output-directory");
        }
        Path output = Path.of(arguments[1]);
        Files.createDirectories(output);
        try (PDDocument document = Loader.loadPDF(Path.of(arguments[0]).toFile())) {
            PDFTextStripper notice = new PDFTextStripper();
            notice.setStartPage(1);
            notice.setEndPage(1);
            Files.writeString(output.resolve("legal-notice.txt"), notice.getText(document), StandardCharsets.UTF_8);

            for (int pageIndex = 0; pageIndex < document.getNumberOfPages(); pageIndex++) {
                var page = document.getPage(pageIndex);
                var box = page.getCropBox();
                float halfWidth = box.getWidth() / 2;
                PDFTextStripperByArea columns = new PDFTextStripperByArea();
                columns.setSortByPosition(true);
                columns.addRegion("left", new Rectangle2D.Float(0, 22, halfWidth, box.getHeight() - 48));
                columns.addRegion("right", new Rectangle2D.Float(halfWidth, 22, halfWidth, box.getHeight() - 48));
                columns.extractRegions(page);
                for (String column : new String[]{"left", "right"}) {
                    String filename = String.format("%03d-%s.txt", pageIndex + 1, column);
                    Files.writeString(output.resolve(filename), columns.getTextForRegion(column), StandardCharsets.UTF_8);
                }
            }
            System.out.println(Path.of(arguments[0]).getFileName() + ": " + document.getNumberOfPages() + " pages");
        }
    }
}
