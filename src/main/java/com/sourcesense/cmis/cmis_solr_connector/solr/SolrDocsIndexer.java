package com.sourcesense.cmis.cmis_solr_connector.solr;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.SolrException;
import org.xml.sax.SAXException;

import com.sourcesense.cmis.cmis_solr_connector.CmisSolrConnectorException;

/**
 * This class indexes SolrDocs in the input Solr instance. A new core, specific for cmis, is created
 * befor doing indexing procedure. Then , all the Cmis-Docs will be indexed in the new core, with
 * the appropriate schema.
 * 
 * @author Alessandro Benedetti
 * 
 */
public class SolrDocsIndexer {

  private String coreConfPath = "/config/solr";

  private String solrUrl;;

  private String solrHome;

  public SolrDocsIndexer(String url, String solrHome) {
    this.solrHome = solrHome;
    this.solrUrl = url;
  }

  public SolrDocsIndexer() {
    this.solrHome = "";
    this.solrUrl = "";
  }

  /**
   * indexes the Solr Documents in input , converted from the CMIS Documents. A new Core,specific
   * for CMIS content is created and used to index the Documents. The configuration of the new CMIS
   * core are in the project's resource.
   * 
   * @param toIndex
   * @throws CmisSolrConnectorException
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException
   * @throws SolrServerException
   */
  public void indexNewCore(List<SolrInputDocument> toIndex) throws CmisSolrConnectorException {
    SolrServer server;
    try {
      server = new CommonsHttpSolrServer(solrUrl);
      indexDocsInNewCoreServer(toIndex, server);
    } catch (MalformedURLException e) {
      throw new CmisSolrConnectorException("The Solr Url is malformed- Check the Url in the input File of the connector");
    }
  }
  

  public void indexNewCore(List<SolrInputDocument> solrDocsToIndex, String externalDir)throws CmisSolrConnectorException {
    SolrServer server;
    try {
      server = new CommonsHttpSolrServer(solrUrl);
      indexDocsInNewCoreServer(solrDocsToIndex, server,externalDir);
    } catch (MalformedURLException e) {
      throw new CmisSolrConnectorException("The Solr Url is malformed- Check the Url in the input File of the connector");
    }
    
  }

  

  public void indexDocsInNewCoreServer(List<SolrInputDocument> toIndex, SolrServer server) throws CmisSolrConnectorException {
    createNewCmisCore(server);
    SolrServer core;
    try {
      core = new CommonsHttpSolrServer(solrUrl + "/cmis");
      indexSolrDocuments(toIndex, core);
    } catch (MalformedURLException e) {
      e.printStackTrace();
      throw new CmisSolrConnectorException("The Solr Url is malformed! Check input file!");
    } catch (SolrServerException e) {
      e.printStackTrace();
      throw new CmisSolrConnectorException("The Solr instance is not running!");
    } catch (IOException e) {
      e.printStackTrace();
      throw new CmisSolrConnectorException("The Solr Url is not accessible!");
    }
  }
  private void indexDocsInNewCoreServer(List<SolrInputDocument> solrDocsToIndex, SolrServer server, String externalDir) throws CmisSolrConnectorException{
    createNewCmisCore(server,externalDir);
    SolrServer core;
    try {
      core = new CommonsHttpSolrServer(solrUrl + "/cmis");
      indexSolrDocuments(solrDocsToIndex, core);
    } catch (MalformedURLException e) {
      e.printStackTrace();
      throw new CmisSolrConnectorException("The Solr Url is malformed! Check input file!");
    } catch (SolrServerException e) {
      e.printStackTrace();
      throw new CmisSolrConnectorException("The Solr instance is not running!");
    } catch (IOException e) {
      e.printStackTrace();
      throw new CmisSolrConnectorException("The Solr Url is not accessible!");
    }
  }

  private void createNewCmisCore(SolrServer server, String externalDir)throws CmisSolrConnectorException {
    try {
      File coreDir = new File(solrHome + "/cmis");
      File outputConfDir = new File(coreDir, "conf");
      coreDir.mkdir();
      outputConfDir.mkdir();
      File inputConfDir = new File(externalDir);
      System.out.println(inputConfDir.getAbsolutePath());
      System.out.println(inputConfDir.isDirectory());
      FileUtils.copyDirectory(inputConfDir, outputConfDir);/*
      for (File f : inputConfDir.listFiles()) {
        File copy = new File(outputConfDir, f.getName());
        copy.createNewFile();
        FileUtils.copyDirectory(srcDir, destDir)
        this.writeContent(copy, getClass().getResourceAsStream(coreConfPath + "/" + f.getName()));
      }*/
      CoreAdminRequest.createCore("cmis", "cmis", server, "solrconfig.xml", "schema.xml");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      throw new CmisSolrConnectorException("Core Config not found- check the config directory");
    } catch (IOException e) {
      e.printStackTrace();
      throw new CmisSolrConnectorException("Impossible to access to the Solr Home Directory");
    } catch (Exception e) {
      e.printStackTrace();
      throw new CmisSolrConnectorException("The Solr instance is not running!");
    }
  }

  public void indexSolrDocuments(List<SolrInputDocument> toIndex, SolrServer core) throws SolrServerException, IOException {
    if (toIndex != null && toIndex.size() > 0){
      core.add(toIndex);
    core.commit();}
  }

  /**
   * creates a new core, specific for Cmis Docs, in the Solr server in input
   * 
   * @param server
   * @throws CmisSolrConnectorException
   * @throws FileNotFoundException
   * @throws SolrServerException
   * @throws IOException
   */
  public void createNewCmisCore(SolrServer server) throws CmisSolrConnectorException {
    try {
      File coreDir = new File(solrHome + "/cmis");
      File outputConfDir = new File(coreDir, "conf");
      coreDir.mkdir();
      outputConfDir.mkdir();
      URL confDir = getClass().getResource(coreConfPath);
      if (confDir == null) {
        throw new FileNotFoundException();
      }
      System.out.println(confDir.toString());
      File inputConfDir = new File(confDir.getFile());
      FileUtils.copyDirectory(inputConfDir, outputConfDir);/*
      for (File f : inputConfDir.listFiles()) {
        File copy = new File(outputConfDir, f.getName());
        copy.createNewFile();
        FileUtils.copyDirectory(srcDir, destDir)
        this.writeContent(copy, getClass().getResourceAsStream(coreConfPath + "/" + f.getName()));
      }*/
      CoreAdminRequest.createCore("cmis", "cmis", server, "solrconfig.xml", "schema.xml");
    } catch (FileNotFoundException e) {
      //e.printStackTrace();log
      throw new CmisSolrConnectorException("Core Config not found- check the config directory");
    } catch (IOException e) {
     // e.printStackTrace();log
      throw new CmisSolrConnectorException("Impossible to access to the Solr Home Directory");
    } catch (Exception e) {
      e.printStackTrace();
      throw new CmisSolrConnectorException("The Solr instance is not running!");
    }
  }

  /**
   * writes the InputStream into the File in input.
   * 
   * @param file
   *          - The File to Write in
   * @param is
   *          - The input Stream to write
   * @throws IOException
   */
  private void writeContent(File file, InputStream is) throws IOException {
    DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
    int c;
    while ((c = is.read()) != -1) {
      out.writeByte(c);
    }
    is.close();
    out.close();

  }

  public String getSolrUrl() {
    return solrUrl;
  }

  public void setSolrUrl(String solrUrl) {
    this.solrUrl = solrUrl;
  }

  public String getSolrHome() {
    return solrHome;
  }

  public void setSolrHome(String solrHome) {
    this.solrHome = solrHome;
  }

  public String getCoreConfPath() {
    return coreConfPath;
  }

  public void setCoreConfPath(String coreConfPath) {
    this.coreConfPath = coreConfPath;
  }


}
