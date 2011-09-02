package com.sourcesense.cmis.cmis_solr_connector.failure_tests;

import java.util.Map;

import junit.framework.TestCase;

import com.sourcesense.cmis.cmis_solr_connector.CmisSolrConnectorException;
import com.sourcesense.cmis.cmis_solr_connector.input.CSConnInputLoader;
import com.sourcesense.cmis.cmis_solr_connector.model.RepositoryInfo;

public class InputloaderTest extends TestCase {
  private CSConnInputLoader loader;

  public void setUp() {
    this.loader = new CSConnInputLoader();
  }
  
  public void testInputFileNotFound(){
    String inputFile="/config/notExist.xml";  
    try {
        loader.loadRep(inputFile, false);
        fail();
      } catch (CmisSolrConnectorException e) {
        assertEquals("Input File not found : "+inputFile,e.getMessage());
      }

  }
  
  public void testInputFileNotValidXml(){

      try {
        loader.loadRep("/config/malFormedXml.xml", false);
        fail();
      } catch (CmisSolrConnectorException e) {
        assertEquals("The XML input file is not well formed",e.getMessage());
      }
      
  }
  
  public void testWrongParameters(){
      try {
        loader.loadRep("/config/wrongParameters.xml", false);
        fail();
      } catch (CmisSolrConnectorException e) {
        assertEquals("No one accessible Repository",e.getMessage());
      }
  }
  
  public void testSolrWrongParameters(){
    try {
      loader.loadSolr("/config/wrongParameters.xml");
      fail();
    } catch (CmisSolrConnectorException e) {
      assertEquals("The input file lacks of the required fields : url. For the Solr instance.",e.getMessage());
    }
}
  
  public void testSolrHomeWrongParameters(){
    try {
      loader.loadSolrHome("/config/wrongParameters.xml");
      fail();
    } catch (CmisSolrConnectorException e) {
      assertEquals("The input file lacks of the required fields : corePath. For the Solr instance.",e.getMessage());
    }
}
  
  /**
   * the input file, for this test , contain 2 repository:
   * 1)with the correct parameters
   * 2)with wrong parameters
   * 
   * Only one must be loaded
   * 
   * */
  public void test1badParameters(){

      try {
        Map<String, RepositoryInfo> loadRep = loader.loadRep("/config/1good1wrongparameter.xml",false);
        assertEquals(1,loadRep.size());
      } catch (CmisSolrConnectorException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        fail();
      }
  }
  
  /**
   * the input file, for this test , contain 2 repository:
   * 1)with the correct parameters, but with wrong values 
   * 2)with the correct parameters and values
   * 
   * Only one must be loaded
   * 
   * */
      public void test1goodRep(){

        try {
          Map<String, RepositoryInfo> loadRep = loader.loadRep("/config/1good1badConfig.xml",true);
          assertEquals(1,loadRep.size());
        } catch (CmisSolrConnectorException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
          fail();
        }

}
  
  
  
}
