package com.sourcesense.cmis.cmis_solr_connector.functional_tests;

import com.sourcesense.cmis.cmis_solr_connector.CmisSolrConnectorException;
import com.sourcesense.cmis.cmis_solr_connector.cmis.CmisSessionCreator;
import com.sourcesense.cmis.cmis_solr_connector.model.RepositoryInfo;

import junit.framework.TestCase;

public class CmisSessionCreatorTest extends TestCase {

  private CmisSessionCreator creator;
  
  public void setUp(){
    this.creator=new CmisSessionCreator();
  }
  
  public void testGoodRepositoryConnection(){
    RepositoryInfo rep=new RepositoryInfo("http://cmis.alfresco.com:80/service/cmis","admin","admin");
    try {
      creator.createSessionFromRepInfo(rep);
    } catch (CmisSolrConnectorException e) {
     fail();
    }
  }
}
