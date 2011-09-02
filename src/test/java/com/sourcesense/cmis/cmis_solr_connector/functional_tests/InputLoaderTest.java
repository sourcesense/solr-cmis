package com.sourcesense.cmis.cmis_solr_connector.functional_tests;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

import com.sourcesense.cmis.cmis_solr_connector.CmisSolrConnectorException;
import com.sourcesense.cmis.cmis_solr_connector.input.CSConnInputLoader;
import com.sourcesense.cmis.cmis_solr_connector.model.RepositoryInfo;


public class InputLoaderTest extends TestCase{
  private CSConnInputLoader loader;

  public void setUp() {
    this.loader = new CSConnInputLoader();
  }
  @Test
  public void testLoadRep() {
    Map<String, RepositoryInfo> actualMap;
    Map<String, RepositoryInfo> expectedMap = new HashMap<String, RepositoryInfo>();
    String url = "http://localhost:8080/alfresco/service/cmis";
    String user = "admin";
    String pass = "admin";
    RepositoryInfo rep1 = new RepositoryInfo(url, user, pass);
    expectedMap.put(url, rep1);
    try {
      actualMap = loader.loadRep("/config/exampleConfig.xml",false);
      for (String url2 : actualMap.keySet()) {
        assertEquals(url, url2);
        assertEquals(rep1, actualMap.get(url2));
      }
    } catch (CmisSolrConnectorException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testLoadMultiRep() {
    Map<String, RepositoryInfo> actualMap;
    String url = "http://localhost:8080/alfresco/service/cmis";
    String user = "admin";
    String pass = "admin";
    RepositoryInfo rep1 = new RepositoryInfo(url+1, user, pass);
    RepositoryInfo rep2 = new RepositoryInfo(url+2, user, pass);
    RepositoryInfo rep3 = new RepositoryInfo(url+3, user, pass);
    RepositoryInfo rep4 = new RepositoryInfo(url+4, user, pass);
    RepositoryInfo[] expectedReps=new RepositoryInfo[]{rep1,rep2,rep3,rep4};
    int count=1;
    try {
      actualMap = loader.loadRep("/config/exampleConfigMultiRep.xml",false);
      assertEquals(4,actualMap.keySet().size());
      for (String url2 : actualMap.keySet()) {
        assertEquals(url+count, url2);
        assertEquals(expectedReps[count-1], actualMap.get(url2));
        count++;
      }
    } catch (CmisSolrConnectorException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testLoadSolr() {
    String url = "http://localhost:8080/Solr";
    String actualUrl = "";
    
      try {
        actualUrl = loader.loadSolr("/config/exampleConfig.xml");
      } catch (CmisSolrConnectorException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        fail();
      }
      assertEquals(url, actualUrl);
    
  }

}
