package com.sourcesense.cmis.cmis_solr_connector.functional_tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.CoreContainer;
import org.xml.sax.SAXException;

import com.sourcesense.cmis.cmis_solr_connector.CmisSolrConnectorException;
import com.sourcesense.cmis.cmis_solr_connector.solr.SolrDocsIndexer;

public class SolrDocsIndexerTest extends TestCase {

  private SolrDocsIndexer indexer;

  public void setUp() {
    indexer = new SolrDocsIndexer("", "./testSolrHome1");
    try {
      this.setUpCoreCreationTest();
      this.setUpIndexingtest();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void tearDown() {
    File f = new File("./testSolrHome1");
    File f2 = new File("./testSolrHome2");
    File f3 = new File("./solr");
    this.deleteDir(f);
    this.deleteDir(f2);
    this.deleteDir(f3);
  }

  public void testCoreCreation() {
    try {
      String core="core0";
      String solrHome="./testSolrHome1";
      File home = new File(solrHome);
      File f = new File(home, "solr.xml");
      CoreContainer container = new CoreContainer();
      container.load(solrHome, f);
      EmbeddedSolrServer server = new EmbeddedSolrServer(container, core);
      indexer.createNewCmisCore(server);
      File cmis = new File(solrHome+"/cmis/conf");
      
      assertTrue(cmis.canRead());
      assertEquals(11, cmis.listFiles().length);
      assertTrue(container.getCoreNames().contains("cmis"));
      server.commit();
      
    } catch (CmisSolrConnectorException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      fail();
    } catch (ParserConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      fail();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      fail();
    } catch (SAXException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      fail();
    } catch (SolrServerException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testIndexingDocs() {
      try {
        SolrServer server =this.getEmbeddedServer("./testSolrHome2", "cmis");
        indexer.indexSolrDocuments(this.createSolrMockedDocs(5), server);
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        QueryResponse rsp = server.query(query);
        SolrDocumentList docs = rsp.getResults();
        assertEquals(5, docs.getNumFound());
      } catch (ParserConfigurationException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (SAXException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (SolrServerException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }


  }

  private List<SolrInputDocument> createSolrMockedDocs(int numDocs) {
    List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
    for (int i = 0; i < numDocs; i++) {
      SolrInputDocument solrDoc = new SolrInputDocument();
      solrDoc.addField("id", "a" + i);
      solrDoc.addField("title", "Doc" + i);
      solrDoc.addField("author", "Alessandro");
      solrDoc.addField("version", "1.0");
      solrDoc.addField("content_type", "Pdf");
      solrDoc.addField("text", "I'm an example doc");
      solrDoc.addField("repositoryPath", "simple path");
      docs.add(solrDoc);
    }
    return docs;
  }

  private String getSchemaFile() {
    return getClass().getResource("/solrConfig/schema-replication1.xml").getFile();
  }

  public String getSolrConfigFile() {
    return getClass().getResource("/solrConfig/solrconfig-solcoreproperties.xml").getFile();
  }

  public String getSolrXmlFile() {
    return getClass().getResource("/solrConfig/solr.xml").getFile();
  }

  public void setUpCoreCreationTest() throws IOException {

    String home = "./testSolrHome1";
    File homeDir = new File(home);
    File coreDir = new File(homeDir, "core0/conf");
    File dataDir = new File(homeDir, "data");
    File solr = new File(homeDir, "solr.xml");
    homeDir.mkdir();
    coreDir.mkdirs();
    dataDir.mkdirs();

    File f = new File(coreDir, "solrconfig.xml");
    copyFile(new File(getSolrConfigFile()), f);

    f = new File(coreDir, "schema.xml");
    copyFile(new File(getSchemaFile()), f);
    copyFile(new File(this.getSolrXmlFile()), solr);

  }

  public void setUpIndexingtest() throws IOException {

    String home = "./testSolrHome2";
    File homeDir = new File(home);
    File coreDir = new File(homeDir, "cmis/conf");
    File dataDir = new File(homeDir, "data");
    File solr = new File(homeDir, "solr.xml");
    homeDir.mkdirs();
    coreDir.mkdirs();
    dataDir.mkdirs();

    File confSource = new File(getClass().getResource("/solrConfig/cmis").getFile());
    for (File file : confSource.listFiles()) {
      copyFile(file, new File(coreDir, file.getName()));
    }

    copyFile(new File(getClass().getResource("/solrConfig/solrcmis.xml").getFile()), solr);

  }
  
  private SolrServer getEmbeddedServer(String solrHome,String core) throws ParserConfigurationException, IOException, SAXException{
    File home = new File(solrHome);
    File f = new File(home, "solr.xml");
    CoreContainer container = new CoreContainer();
    container.load(solrHome, f);
    EmbeddedSolrServer server = new EmbeddedSolrServer(container, core);
    return server;
  }

  private void copyFile(File src, File dst) throws IOException {
    BufferedReader in = new BufferedReader(new FileReader(src));
    Writer out = new FileWriter(dst);

    for (String line = in.readLine(); null != line; line = in.readLine()) {
      out.write(line);
    }
    in.close();
    out.close();
  }

  // Deletes all files and subdirectories under dir.
  // Returns true if all deletions were successful.
  // If a deletion fails, the method stops attempting to delete and returns false.
  public boolean deleteDir(File dir) {
    if (dir.isDirectory()) {
      String[] children = dir.list();
      for (int i = 0; i < children.length; i++) {
        boolean success = deleteDir(new File(dir, children[i]));
        if (!success) {
          return false;
        }
      }
    }

    // The directory is now empty so delete it
    return dir.delete();
  }

}
