package com.sourcesense.cmis.cmis_solr_connector;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.txt.TXTParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.cyberneko.html.filters.Purifier;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class has the responsibility to extract content from the input Stream of a Cmis:Document. To
 * provide this functionality it uses Apache Tika Library and Xerces Dom parsing
 * 
 * @author Alessandro Benedetti
 * 
 */
public class ContentExtractor {

  /**
   * extracts the content from the input stream of a Cmis:Document
   * @param stream- the inputStream extracted from the cmis:document
   * @param filename- the name and extension of the input file
   * @return
   * @throws IOException
   * @throws SAXException
   * @throws TikaException
   */
  public String extractFromFileStream(InputStream stream, String filename) {
    String contentExtracted = "";
    try {
      Parser tikaParser = new AutoDetectParser();
      ContentHandler textHandler = new BodyContentHandler();
      Metadata metadata = new Metadata();
      metadata.set(Metadata.RESOURCE_NAME_KEY, filename);
      ParseContext context = new ParseContext();
      tikaParser.parse(stream, textHandler, metadata, context);
      contentExtracted = textHandler.toString();
    } catch (IOException e) {
     // e.printStackTrace();log
    } catch (SAXException e) {
     // e.printStackTrace();log
    } catch (TikaException e) {
      //e.printStackTrace();log
    }
    return contentExtracted;
  }
  
  /**
   * extracts the content from the input stream of a Cmis:Document
   * @param stream- the inputStream extracted from the cmis:document
   * @param filename- the name and extension of the input file
   * @return
   * @throws IOException
   * @throws SAXException
   * @throws TikaException
   */
  public String extractFromTxtStream(InputStream stream, String filename) {
    String contentExtracted = "";
    try {
      Parser tikaParser = new TXTParser();
      ContentHandler textHandler = new BodyContentHandler();
      Metadata metadata = new Metadata();
      ParseContext context = new ParseContext();
      tikaParser.parse(stream, textHandler, metadata, context);
      contentExtracted = textHandler.toString();
    } catch (IOException e) {
     // e.printStackTrace();log
    } catch (SAXException e) {
     // e.printStackTrace();log
    } catch (TikaException e) {
      //e.printStackTrace();log
    }
    return contentExtracted;
  }

  /**
   * extracts the content from an inputStream of a Xml (Html) page.
   * The information extracted is the Body content of the page.
   * @param stream- the inputStream from the html page.
   * @return
   * @throws SAXException
   * @throws IOException
   */
  public String extractTextFromHtmlStream(InputStream stream)  {
    String contentExtracted = "";
    try {
      org.w3c.dom.Document dom = this.parseHTML(new InputStreamReader(stream));
      Node body = dom.getElementsByTagName("BODY").item(0);
      if(body!=null)
      contentExtracted = body.getTextContent();
    } catch (DOMException e) {
      //e.printStackTrace();log
    } catch (SAXException e) {
      //e.printStackTrace();log
    } catch (IOException e) {
      //e.printStackTrace();log
    }
    return contentExtracted;
  }

  private String getDefaultEncoding() {
    return System.getProperty("file.encoding");
  }

  private EntityResolver getDumbEntityResolver() {
    return new EntityResolver() {
      public InputSource resolveEntity(String publicId, String systemId) {
        if (systemId.endsWith(".dtd")) {
          // this deactivates all DTDs by giving empty XML docs
          final String emptyDoc = "<?xml version=\"1.0\" encoding=\"" + getDefaultEncoding() + "\"?>";
          return new InputSource(new ByteArrayInputStream(emptyDoc.getBytes()));
        }
        System.out.println("publicId: " + publicId);
        System.out.println("systemId: " + systemId);
        return null;
      }
    };
  }

  /**
   * return the dom of the input html document
   * 
   * @param in
   * @return
   * @throws SAXException
   * @throws IOException
   */
  private org.w3c.dom.Document parseHTML(Reader in) throws SAXException, IOException {
    DOMParser parser = createTagBalancingParser();
    parser.parse(new InputSource(in));
    return parser.getDocument();
  }

  /**
   * return a parser that balances the not-well formed input tags
   * 
   * @return
   * @throws SAXException
   */
  private DOMParser createTagBalancingParser() throws SAXException {
    DOMParser parser = createParser();
    parser.setFeature("http://cyberneko.org/html/features/balance-tags", true);
    XMLDocumentFilter[] filters = { new Purifier() };
    parser.setProperty("http://cyberneko.org/html/properties/filters", filters);
    return parser;
  }

  /**
   * return a DOMParser, able to extract the DOM from a page
   * 
   * @return
   * @throws SAXException
   */
  private DOMParser createParser() throws SAXException {
    DOMParser parser = new DOMParser();
    // parser.setProperty("http://apache.org/xml/properties/dom/document-class-name",it.uniroma3.dia.roadrunner.tokenizer.dom.node.DOMNodeFactory.class.getName());
    parser.setProperty("http://cyberneko.org/html/properties/names/elems", "match");
    parser.setFeature("http://cyberneko.org/html/features/scanner/ignore-specified-charset", false);
    parser.setProperty("http://cyberneko.org/html/properties/default-encoding", getDefaultEncoding());
    parser.setFeature("http://apache.org/xml/features/scanner/notify-char-refs", false);
    parser.setFeature("http://apache.org/xml/features/scanner/notify-builtin-refs", false);
    parser.setFeature("http://cyberneko.org/html/features/scanner/notify-builtin-refs", false);
    parser.setFeature("http://xml.org/sax/features/namespaces", false);
    parser.setEntityResolver(getDumbEntityResolver());
    return parser;
  }
}
