package com.sourcesense.cmis.cmis_solr_connector.input;

import java.util.HashMap;
import java.util.Map;

import com.sourcesense.cmis.cmis_solr_connector.CmisSolrConnectorException;
import com.sourcesense.cmis.cmis_solr_connector.cmis.CmisSessionCreator;
import com.sourcesense.cmis.cmis_solr_connector.model.RepositoryInfo;
/**
 * This class has the responsibility of checking if the input parameters of the application
 * fulfill our requirements:
 * -valid and reachable repositories
 * -valid and reachable Solr instance
 * 
 * Deprecated: to check the input parameters we have to try to create a session with them.
 * This will be a redundancy.
 * */
public class CSConnInputChecker {
  private CmisSessionCreator creator;
 
  public CSConnInputChecker(){
    this.creator=new CmisSessionCreator();
  }
 

  public Map<String, RepositoryInfo> checkReps(Map<String, RepositoryInfo> host2rep)  {
    Map<String, RepositoryInfo> goodReps=new HashMap<String, RepositoryInfo>();
    for(RepositoryInfo rep:host2rep.values()){
      try {
        this.checkRep(rep);
        goodReps.put(rep.getUrl(), rep);
      } catch (CmisSolrConnectorException e) {
 e.printStackTrace(); 
      }
    }
    return goodReps;
    
  }

  public void checkRep(RepositoryInfo rep) throws CmisSolrConnectorException {
    creator.createSessionFromRepInfo(rep);
  }
}