package com.sourcesense.cmis.cmis_solr_connector.functional_tests;

import java.io.InputStream;

import junit.framework.TestCase;

import org.junit.Test;

import com.sourcesense.cmis.cmis_solr_connector.ContentExtractor;

public class ContentExtractorTest extends TestCase {

  private ContentExtractor extractor;
  
  public void setUp(){
    this.extractor=new ContentExtractor();
  }
  
  @Test
  public void testExtractionFromRtf(){
    String rtf="/exampleDocs/doc.rtf";
    InputStream is = getClass().getResourceAsStream(rtf );
    String expected="I'm a RTF file. \n\n";
      String actual=extractor.extractFromFileStream(is, "doc.rtf");
      assertEquals(expected,actual);
  }
  
  @Test
  public void testExtractionFromTxt(){
    String rtf="/exampleDocs/doc1.txt";
    InputStream is = getClass().getResourceAsStream(rtf );
    String expected="I'm a txt Document!\n\n";
      String actual=extractor.extractFromTxtStream(is, "doc.rtf");
      assertEquals(expected,actual);
  }
  
  @Test
  public void testExtractionFromPdf(){
    String pdf="/exampleDocs/doc.pdf";
    InputStream is = getClass().getResourceAsStream(pdf );
    String expected="I'm a PDF file.\n\n";
    String actual=extractor.extractFromFileStream(is, "doc.pdf");
    assertEquals(expected,actual);  
  }
  
  public void testExtractionFromHtml(){
    String rtf="/exampleDocs/example.html";
    InputStream is = getClass().getResourceAsStream(rtf );
    String expected="I'm an HTML file.\n";
    String actual=extractor.extractTextFromHtmlStream(is);
    assertEquals(expected,actual);  
  }
}
