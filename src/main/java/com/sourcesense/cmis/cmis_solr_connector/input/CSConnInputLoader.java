package com.sourcesense.cmis.cmis_solr_connector.input;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;

import com.sourcesense.cmis.cmis_solr_connector.CmisSolrConnectorException;
import com.sourcesense.cmis.cmis_solr_connector.model.RepositoryInfo;

/**
 * This class has the responsibility of loading Configuration from a Config File. There are two
 * different inputs: -Solr -CMIS Repository.
 *  From each one of these Inputs , the Config file provides
 * the needed information (Url,Username,Password ...)
 * 
 * @author Alessandro Benedetti
 * 
 */
public class CSConnInputLoader {

  /**
   * return the Repository-Info for each input-Repository. This Information contains Url,Username
   * and Password.
   * 
   * @param configFileUrl
   *          -The Url of the Config File
   * @param check- if true , an input check is performed
   * @return
   * @throws CmisSolrConnectorException
   */
  public Map<String, RepositoryInfo> loadRep(String configFileUrl, boolean check) throws CmisSolrConnectorException {
    InputStream configFileInputStream = getClass().getResourceAsStream(configFileUrl);
    try {
      return this.loadRep(configFileInputStream, check);
    } catch (IOException e) {
      e.printStackTrace();
      throw new CmisSolrConnectorException("Input File not found : " + configFileUrl);
    }
  }
  
  /**
   * return the Repository-Info for each input-Repository. This Information contains Url,Username
   * and Password.
   * 
   * @param configFileUrl
   *          -The Url of the Config File
   * @param check- if true , an input check is performed
   * @return
   * @throws CmisSolrConnectorException
   */
  public Map<String, RepositoryInfo> loadRepFromFile(String configFileUrl, boolean check) throws CmisSolrConnectorException {  
    try {
      File inputFile=new File(configFileUrl);
      FileInputStream stream=new FileInputStream(inputFile);
      return this.loadRep(stream, check);
    } catch (IOException e) {
      e.printStackTrace();
      throw new CmisSolrConnectorException("Input File not found : " + configFileUrl);
    }
  }

  /**
   * return the Url of the Solr instance in input .
   * 
   * @param configFileUrl
   *          -The Url of the Config File
   * @return
   * @throws CmisSolrConnectorException
   */
  public String loadSolr(String configFileUrl) throws CmisSolrConnectorException {
    InputStream configFileInputStream = getClass().getResourceAsStream(configFileUrl);
    try {
      return this.loadSolr(configFileInputStream);
    } catch (IOException e) {
      e.printStackTrace();
      throw new CmisSolrConnectorException("Input File not found : " + configFileUrl);
    }

  }
  
  /**
   * return the Url of the Solr instance in input .
   * 
   * @param configFileUrl
   *          -The Url of the Config File
   * @return
   * @throws CmisSolrConnectorException
   */
  public String loadSolrFromFile(String configFileUrl) throws CmisSolrConnectorException {
    try {
      File inputFile=new File(configFileUrl);
      FileInputStream stream=new FileInputStream(inputFile);
      return this.loadSolr(stream);
    } catch (IOException e) {
      e.printStackTrace();
      throw new CmisSolrConnectorException("Input File not found : " + configFileUrl);
    }

  }

  /**
   * return the SolrHome of the Solr instance in input .
   * 
   * @param configFileUrl
   *          -The Url of the Config File
   * @return
   * @throws CmisSolrConnectorException
   */
  public String loadSolrHome(String configFileUrl) throws CmisSolrConnectorException {
    InputStream configFileInputStream = getClass().getResourceAsStream(configFileUrl);
    try {
      return this.loadSolrHome(configFileInputStream);
    } catch (IOException e) {
      e.printStackTrace();
      throw new CmisSolrConnectorException("Input File not found : " + configFileUrl);
    }

  }
  
  /**
   * return the SolrHome of the Solr instance in input .
   * 
   * @param configFileUrl
   *          -The Url of the Config File
   * @return
   * @throws CmisSolrConnectorException
   */
  public String loadSolrHomeFromFile(String configFileUrl) throws CmisSolrConnectorException {
    try {
      File inputFile=new File(configFileUrl);
      FileInputStream stream=new FileInputStream(inputFile);
      return this.loadSolrHome(stream);
    } catch (IOException e) {
      //e.printStackTrace();log
      throw new CmisSolrConnectorException("Input File not found : " + configFileUrl);
    }

  }

  /**
   * * return the Repository-Info for each input-Repository. This Information contains Url,Username
   * and Password. This method takes in input the content Stream of the Config File.
   * 
   * @param configFileStream
   * @return
   * @throws CmisSolrConnectorException
   * @throws IOException
   */
  @SuppressWarnings("unchecked")
  private Map<String, RepositoryInfo> loadRep(InputStream configFileStream, boolean check) throws CmisSolrConnectorException, IOException {
    try {
      CSConnInputChecker checker;
      SAXBuilder builder = new SAXBuilder();
      Document configDoc;
      configDoc = builder.build(configFileStream);
      Iterator<Element> repositories = configDoc.getRootElement().getDescendants(new ElementFilter("repos"));
      Map<String, RepositoryInfo> host2rep = new HashMap<String, RepositoryInfo>();
      while (repositories.hasNext()) {
        Element rep = repositories.next();
        String url;
        String username;
        String password;
        String space;
        url = rep.getChildText("url");
        username = rep.getChildText("user");
        password = rep.getChildText("password");
        space = rep.getChildText("space");
        if (url != null && username != null && password != null) {
          RepositoryInfo repDetail = new RepositoryInfo(url, username, password);
          if(space==null)
            space="";
          repDetail.setSpace(space);
          host2rep.put(url, repDetail);
        }
        
      }
      if (host2rep.size() == 0)
        throw new CmisSolrConnectorException("No one accessible Repository");
      if (check) {
        checker = new CSConnInputChecker();
        host2rep = checker.checkReps(host2rep);
      }
      return host2rep;
    } catch (JDOMException e) {
      //e.printStackTrace();log
      throw new CmisSolrConnectorException("The XML input file is not well formed");
    }
  }

  /**
   * * return the url of the Solr instance in input. This method takes in input the content Stream
   * of the Config File.
   * 
   * @param configFileStream
   * @return
   * @throws JDOMException
   * @throws IOException
   * @throws CmisSolrConnectorException
   */
  @SuppressWarnings("unchecked")
  private String loadSolr(InputStream configFileStream) throws IOException, CmisSolrConnectorException {
    try {
      SAXBuilder builder = new SAXBuilder();
      String url = "";
      Document doc;
      doc = builder.build(configFileStream);
      Iterator<Element> reps = doc.getRootElement().getDescendants(new ElementFilter("solr"));
      while (reps.hasNext()) {
        Element rep = reps.next();
        url = rep.getChildText("url");
        if (url == null)
          throw new CmisSolrConnectorException("The input file lacks of the required fields : url. For the Solr instance.");
      }
      return url;
    } catch (JDOMException e) {
     // e.printStackTrace(); log
      throw new CmisSolrConnectorException("The XML input file is not well formed");
    }
  }

  /**
   * * return the solrHome of the Solr instance in input. This method takes in input the content
   * Stream of the Config File.
   * 
   * @param configFileStream
   * @return
   * @throws JDOMException
   * @throws IOException
   * @throws CmisSolrConnectorException
   */
  @SuppressWarnings("unchecked")
  private String loadSolrHome(InputStream configFileStream) throws IOException, CmisSolrConnectorException {
    try {
      SAXBuilder builder = new SAXBuilder();
      String url = "";
      Document doc = builder.build(configFileStream);
      Iterator<Element> reps = doc.getRootElement().getDescendants(new ElementFilter("solr"));
      while (reps.hasNext()) {
        Element rep = reps.next();
        url = rep.getChildText("corePath");
        if (url == null)
          throw new CmisSolrConnectorException("The input file lacks of the required fields : corePath. For the Solr instance.");
      }
      return url;
    } catch (JDOMException e) {
      //e.printStackTrace();log
      throw new CmisSolrConnectorException("The XML input file is not well formed");
    }
  }

 

  

}
