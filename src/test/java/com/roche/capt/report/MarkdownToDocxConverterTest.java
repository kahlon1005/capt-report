package com.roche.capt.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import com.g2forge.alexandria.command.command.IStandardCommand;
import com.g2forge.alexandria.command.command.IStandardCommand.TestResult;
import com.github.roche.capt.report.MarkdownToDocxConverter;

class MarkdownToDocxConverterTest {

  private final String inputFilePath = "src/test/resources/test_input.md";
  private final String outputFilePath = "test_output.docx";

  @AfterEach
  void tearDown() {
    // Clean up the generated files
    new File(outputFilePath).delete();
  }

  @Test
  void testMarkdownToDocxConversion() throws Throwable {
    // Execute the conversion
    // MarkdownToDocxConverter.main(new String[] {inputFilePath, outputFilePath});
    TestResult result = new MarkdownToDocxConverter().test(new String[] {inputFilePath, outputFilePath});
    assertEquals(IStandardCommand.SUCCESS, result.getExit());

    // Verify the output DOCX file exists
    final File outputFile = new File(outputFilePath);
    assertTrue(outputFile.exists(), "Output DOCX file should exist.");

    // Additional check: Verify content in the DOCX file
    try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(outputFile.toPath()))) {
      final String title = doc.getParagraphs().get(0).getText().trim();
      assertEquals("Sample Title", title, "The title in DOCX should match the Markdown title.");

      // You can add more checks here to verify the content further
    } catch (IOException e) {
      fail("Failed to read the DOCX file: " + e.getMessage());
    }
  }

}
