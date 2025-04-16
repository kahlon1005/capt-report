package com.github.roche.capt.report;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.Arrays;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import com.g2forge.alexandria.command.command.IStandardCommand;
import com.g2forge.alexandria.command.exit.IExit;
import com.g2forge.alexandria.command.invocation.CommandInvocation;
import com.g2forge.alexandria.java.io.dataaccess.PathDataSource;
import com.vladsch.flexmark.docx.converter.DocxRenderer;
import com.vladsch.flexmark.ext.definition.DefinitionExtension;
import com.vladsch.flexmark.ext.emoji.EmojiExtension;
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughSubscriptExtension;
import com.vladsch.flexmark.ext.ins.InsExtension;
import com.vladsch.flexmark.ext.superscript.SuperscriptExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.toc.SimTocExtension;
import com.vladsch.flexmark.ext.toc.TocExtension;
import com.vladsch.flexmark.ext.wikilink.WikiLinkExtension;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MarkdownToDocxConverter implements IStandardCommand {

  final private static DataHolder OPTIONS = new MutableDataSet()
      .set(Parser.EXTENSIONS, Arrays.asList(DefinitionExtension.create(), EmojiExtension.create(), FootnoteExtension.create(), StrikethroughSubscriptExtension.create(), InsExtension.create(),
          SuperscriptExtension.create(), TablesExtension.create(), TocExtension.create(), SimTocExtension.create(), WikiLinkExtension.create()))
      .set(DocxRenderer.SUPPRESS_HTML, true);

  public static void main(String[] args) throws Throwable {
    IStandardCommand.main(args, new MarkdownToDocxConverter());
  }

  @Override
  public IExit invoke(CommandInvocation<InputStream, PrintStream> invocation) throws Throwable {

    if (invocation.getArguments().size() != 2)
      throw new IllegalArgumentException("Takes exactly two CLI arguments - the path to the source md file and target docx file!");

    final boolean result = handle(invocation.getArguments().get(0), invocation.getArguments().get(1));
    return result ? IStandardCommand.SUCCESS : IStandardCommand.FAIL;
  }

  public static boolean handle(String inputFilePath, String outputFilePath) {
    final String markdown = new PathDataSource(Paths.get(inputFilePath)).getString();
    System.out.println(markdown);

    // Parse Markdown to Node

    Parser PARSER = Parser.builder(OPTIONS).build();
    DocxRenderer RENDERER = DocxRenderer.builder(OPTIONS).build();

    Node document = PARSER.parse(markdown);

    // or to control the package
    WordprocessingMLPackage template = DocxRenderer.getDefaultTemplate();
    RENDERER.render(document, template);

    File file = new File(outputFilePath);
    try {
      template.save(file, Docx4J.FLAG_SAVE_ZIP_FILE);
      log.info("Conversion completed successfully.");
    } catch (Docx4JException e) {
      log.error("An error occurred: {}", e.getMessage(), e);
      return false;
    }

    return true;
  }

}
