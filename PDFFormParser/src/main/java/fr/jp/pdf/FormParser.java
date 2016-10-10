﻿package fr.jp.pdf;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.Arrays;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormParser
{
  public static final String VERSION = "16.10.8.1";
  private static final Logger LOGGER = LoggerFactory.getLogger(FormParser.class);
  public static final String PARAM_KEY__GET_LIST = "-LIST_FIELDS";
  private static final String MSG_HELP = "usage:\n> java -cp $CLASSPATH fr.jp.pdf.FormParser -LIST_FIELDS pdf_file\n";

  public static void main(String[] args)
    throws IOException
  {
    LOGGER.debug("Start, v{}", "16.10.8.1");
    FormParser formParser = new FormParser();

    String str = formParser.getMode(args); int i = -1; switch (str.hashCode()) { case 2083228461:
      if (str.equals("-LIST_FIELDS")) i = 0;  }
    switch (i)
    {
    case 0:
      int idx_file_name = (args[0].equals("-LIST_FIELDS")) ? 1 : 0;
      formParser.printFields(args[idx_file_name]);
      break;
    default:
      printHelp();
    }
    LOGGER.trace("Invoke. Argument with");
    LOGGER.debug("Finish");
  }

  private String getMode(String[] args) {
    LOGGER.debug("Start with {}", Arrays.asList(args));
    String mode = "UNKNOWN";
    if (args.length > 0)
      if ((Arrays.binarySearch(args, "-LIST_FIELDS") >= 0) && (args.length == 2))
        mode = "-LIST_FIELDS";
      else
        LOGGER.warn("Invoke with unknown params: {}", Arrays.asList(args));

    else
      LOGGER.warn("Invoke with empty arguments! Don't run.");

    LOGGER.debug("Finish, return: [{}]", mode);
    return mode;
  }

  private void printFields(String file_name)
    throws IOException
  {
    int i;
    LOGGER.debug("Start for [{}]", file_name);
    LOGGER.trace("Try open PDF file: [{}]", file_name);

    URL fileURL = super.getClass().getClassLoader().getResource(file_name);
    if (fileURL == null) throw new IOException("NOT FOUND File \"" + file_name + "\"");
    File file = new File(fileURL.getFile());

    PDDocument doc = null;
    try {
      doc = PDDocument.load(file);
      if (doc == null) throw new IOException("Problem with load PDDocument!");
      LOGGER.trace("PDDocument open succ");

      PDDocumentCatalog documentCatalog = doc.getDocumentCatalog();
      LOGGER.trace("PDDocumentCatalog getted: [{}]", documentCatalog);
      PDAcroForm acroForm = documentCatalog.getAcroForm();
      LOGGER.trace("PDAcroForm getted: [{}]", acroForm);
      if (acroForm == null) throw new IOException("The PDF Doc \"" + file_name + "\" hasn't a FORM FIELDS!");
      i = 0;
      for (PDField field : acroForm.getFields())
        LOGGER.info("{}. Get PDField: [{}]", Integer.valueOf(++i), field);
    }
    catch (IOException e)
    {
      LOGGER.error("Problem: ", e);
    } finally {
      if (doc != null)
        try {
          doc.close();
          LOGGER.trace("PDDocument closed");
        } catch (IOException e) {
          LOGGER.error("Can't close PDDocument:", e);
        }

    }

    LOGGER.debug("Finish processing PDF doc [{}]", file_name);
  }

  private static void printHelp() {
    LOGGER.debug("Invoke");
    System.out.println("usage:\n> java -cp $CLASSPATH fr.jp.pdf.FormParser -LIST_FIELDS pdf_file\n");
  }
}