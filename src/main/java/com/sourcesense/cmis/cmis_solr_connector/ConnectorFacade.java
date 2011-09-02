package com.sourcesense.cmis.cmis_solr_connector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.solr.common.SolrInputDocument;

import com.sourcesense.cmis.cmis_solr_connector.cmis.CmisDocsRetriever;
import com.sourcesense.cmis.cmis_solr_connector.input.CSConnInputLoader;
import com.sourcesense.cmis.cmis_solr_connector.model.RepositoryInfo;
import com.sourcesense.cmis.cmis_solr_connector.solr.SolrDocsCreator;
import com.sourcesense.cmis.cmis_solr_connector.solr.SolrDocsIndexer;
import com.sourcesense.cmis.cmis_solr_connector.util.DateUtils;


/**
 * This class is the facade to the CmisSolrConnector.
 * it offers the access to the features of the Connector.
 * @author Alessandro Benedetti
 *
 */
public class ConnectorFacade {

  private  String xmlConfig = "/config/CSConnectorInput.xml";

  

  private Map<String, RepositoryInfo> inputRepositories;
  
  private String solrUrl;
  private String solrHome;

  private CmisDocsRetriever cmisDocsRetriever;

  private SolrDocsCreator solrDocsCreator;

  private SolrDocsIndexer solrDocsIndexer;

  private List<SolrInputDocument> solrDocsToIndex;

  
  /**
   * initializes all the component of the Connector:
   * @throws CmisSolrConnectorException
   */
  public void init() throws CmisSolrConnectorException {
    solrDocsToIndex = new ArrayList<SolrInputDocument>();
    solrDocsCreator = new SolrDocsCreator();
    cmisDocsRetriever = new CmisDocsRetriever();
    CSConnInputLoader loader = new CSConnInputLoader();
    inputRepositories = loader.loadRep(xmlConfig, false);
    solrUrl=loader.loadSolr(xmlConfig);
    solrHome= loader.loadSolrHome(xmlConfig);
    solrDocsIndexer = new SolrDocsIndexer(solrUrl,solrHome);
  }
  
  /**
   * initializes all the component of the Connector:
   * @throws CmisSolrConnectorException
   */
  public void initFromInputFile(String inputFileUrl) throws CmisSolrConnectorException {
    solrDocsToIndex = new ArrayList<SolrInputDocument>();
    solrDocsCreator = new SolrDocsCreator();
    cmisDocsRetriever = new CmisDocsRetriever();
    CSConnInputLoader loader = new CSConnInputLoader();
    inputRepositories = loader.loadRepFromFile(inputFileUrl, false);
    solrUrl=loader.loadSolrFromFile(inputFileUrl);
    solrHome= loader.loadSolrHomeFromFile(inputFileUrl);
    solrDocsIndexer = new SolrDocsIndexer(solrUrl,solrHome);
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

  /**
   * retrieves all the documents from the input Repositories that satisfy a time constrain.
   * Only the documents modified after the input modification time will be retrieved.
   * @param time-
   * @return
   * @throws CmisSolrConnectorException
   */
  public Map<RepositoryInfo, List<Document>> retrieveAllDocs(String time) throws CmisSolrConnectorException {
    if(time==null)
       time = "1900-08-06T22:22:22.123Z";
    return cmisDocsRetriever.retrieveAllDocs(inputRepositories, time);
  }

  /**
   * operates the conversion between a List of retrieved Cmis:Document into a List of Solr Document.
   * @param retrievedCmisDocs
   */
  public void initDocsToIndex(Map<RepositoryInfo, List<Document>> retrievedCmisDocs) {
    for (RepositoryInfo rep:retrievedCmisDocs.keySet()) {
      solrDocsToIndex.addAll(solrDocsCreator.fromCmisDocsToSolr(retrievedCmisDocs.get(rep),rep.getSpace()));
    }
  }

  /**
   * indexes the early created Solr Documents in the input Solr instance.
   * @throws CmisSolrConnectorException
   */
  public void indexDocs() throws CmisSolrConnectorException {
    solrDocsIndexer.indexNewCore(solrDocsToIndex);
  }
  
  /**
   * indexes the early created Solr Documents in the input Solr instance.
   * @throws CmisSolrConnectorException
   */
  public void indexDocsFromExternalConfig(String externalDir) throws CmisSolrConnectorException {
    solrDocsIndexer.indexNewCore(solrDocsToIndex,externalDir);
  }

  /**
   * @param cycle-number of polling request
   * @param millis- time interval 
   */
  public void polling(int cycle,long millis) {
    try {
      ConnectorFacade connF = new ConnectorFacade();
      connF.init();
      Map<RepositoryInfo, List<Document>> retrievedAllDocs = null;
      String time = "1900-08-06T22:22:22.123Z";
      for (int i = 0; i < cycle; i++) {
        System.out.println("Start : " + i + " Iteration");
        retrievedAllDocs = connF.retrieveAllDocs(time);
        System.out.println("After : " + time);
        int cuntModified = 0;
        for (RepositoryInfo url : retrievedAllDocs.keySet()) {
          cuntModified += retrievedAllDocs.get(url).size();
        }
        System.out.println(cuntModified + " Modified Documents Found!");
        time = DateUtils.yesterday();
        Thread.sleep(millis);
        connF.initDocsToIndex(retrievedAllDocs);
        connF.indexDocs();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String args[]) {
    System.out.println("2010-10-05T21:59:58.123Z".compareTo("2010-10-06T15:59:58.123Z"));
    ConnectorFacade connF = new ConnectorFacade();
    connF.polling(10,80000);

  }

  public String getXmlConfig() {
    return xmlConfig;
  }

  public void setXmlConfig(String xmlConfig) {
    this.xmlConfig = xmlConfig;
  }

  public Map<String, RepositoryInfo> getInputRepositories() {
    return inputRepositories;
  }

  public void setInputRepositories(Map<String, RepositoryInfo> inputRepositories) {
    this.inputRepositories = inputRepositories;
  }

  public CmisDocsRetriever getCmisDocsRetriever() {
    return cmisDocsRetriever;
  }

  public void setCmisDocsRetriever(CmisDocsRetriever cmisDocsRetriever) {
    this.cmisDocsRetriever = cmisDocsRetriever;
  }

  public SolrDocsCreator getSolrDocsCreator() {
    return solrDocsCreator;
  }

  public void setSolrDocsCreator(SolrDocsCreator solrDocsCreator) {
    this.solrDocsCreator = solrDocsCreator;
  }

  public SolrDocsIndexer getSolrDocsIndexer() {
    return solrDocsIndexer;
  }

  public void setSolrDocsIndexer(SolrDocsIndexer solrDocsIndexer) {
    this.solrDocsIndexer = solrDocsIndexer;
  }

  public List<SolrInputDocument> getSolrDocsToIndex() {
    return solrDocsToIndex;
  }

  public void setSolrDocsToIndex(List<SolrInputDocument> solrDocsToIndex) {
    this.solrDocsToIndex = solrDocsToIndex;
  }

}
