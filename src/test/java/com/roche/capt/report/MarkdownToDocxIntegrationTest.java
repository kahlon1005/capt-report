package com.roche.capt.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.g2forge.alexandria.command.command.IStandardCommand;
import com.g2forge.alexandria.command.command.IStandardCommand.TestResult;
import com.github.roche.capt.report.MarkdownToDocxConverter;

class MarkdownToDocxIntegrationTest {

  private Path tempMarkdownFile;
  private Path tempDocxFile;

  @BeforeEach
  void setUp() throws IOException {
    // Create a temporary Markdown file for testing
    tempMarkdownFile = Files.createTempFile("testMarkdown", ".md");
    final String markdownContent = "# Test Header\nThis is a test paragraph.";
    Files.write(tempMarkdownFile, markdownContent.getBytes());

    // Define the output DOCX file path
    tempDocxFile = Files.createTempFile("testOutput", ".docx");
  }

  @AfterEach
  void tearDown() throws IOException {
    // Clean up temporary files
    Files.deleteIfExists(tempMarkdownFile);
    Files.deleteIfExists(tempDocxFile);
  }

  @Test
  void testMarkdownToDocxConversion() throws IOException, Throwable {
    // Convert the Markdown to DOCX
    final TestResult result = new MarkdownToDocxConverter().test(new String[] {tempMarkdownFile.toString(), tempDocxFile.toString()});
    assertEquals(IStandardCommand.SUCCESS, result.getExit());

    // Verify the output DOCX file
    try (XWPFDocument document = new XWPFDocument(Files.newInputStream(tempDocxFile))) {
      final String firstParagraphText = document.getParagraphs().get(1).getText().trim();
      assertEquals("This is a test paragraph.", firstParagraphText);
    }
  }
}
