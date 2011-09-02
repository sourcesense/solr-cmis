package com.sourcesense.cmis.cmis_solr_connector.failure_tests;

import com.sourcesense.cmis.cmis_solr_connector.CmisSolrConnectorException;
import com.sourcesense.cmis.cmis_solr_connector.input.CSConnInputChecker;
import com.sourcesense.cmis.cmis_solr_connector.model.RepositoryInfo;

import junit.framework.TestCase;

public class InputCheckerTest extends TestCase {
private CSConnInputChecker checker;

public void setUp(){
  checker=new CSConnInputChecker();
}

public void testCheckBadRepository(){
  RepositoryInfo badRep=new RepositoryInfo("http://www.google.it", "admin", "admin");
  RepositoryInfo badRep2=new RepositoryInfo("http://cmis.alfresco.com:80/service/cmis", "admino", "admin");
  RepositoryInfo badRep3=new RepositoryInfo("http://cmis.alfresco.com:80/service/cmis", "admin", "admino");
  RepositoryInfo[] badReps=new RepositoryInfo[]{badRep,badRep2,badRep3};
  for(RepositoryInfo rep:badReps){
    try {
      checker.checkRep(rep);
      fail();
    } catch (CmisSolrConnectorException e) {
      assertEquals("Could not retrieve any CMIS repository with the following parameters: {org.apache.chemistry.opencmis.binding.auth.soap.usernametoken=false, org.apache.chemistry.opencmis.binding.spi.type=atompub, org.apache.chemistry.opencmis.user="+rep.getUsername()+", org.apache.chemistry.opencmis.binding.cache.repositories.size=10, org.apache.chemistry.opencmis.binding.auth.classname=org.apache.chemistry.opencmis.client.bindings.spi.StandardAuthenticationProvider, org.apache.chemistry.opencmis.binding.spi.classname=org.apache.chemistry.opencmis.client.bindings.spi.atompub.CmisAtomPubSpi, org.apache.chemistry.opencmis.binding.atompub.url="+rep.getUrl()+", org.apache.chemistry.opencmis.binding.auth.http.basic=true, org.apache.chemistry.opencmis.binding.cache.types.size=100, org.apache.chemistry.opencmis.password="+rep.getPassword()+", org.apache.chemistry.opencmis.binding.cache.objects.size=400}",e.getMessage());
    }
  }
}

public void testGoodRepository(){
  RepositoryInfo goodRep=new RepositoryInfo("http://cmis.alfresco.com:80/service/cmis", "admin", "admin");
  try {
    checker.checkRep(goodRep);
  } catch (CmisSolrConnectorException e) {
    fail();
  }

}

public void testGoodSolrUrl(){
  
}

public void testBadSolrUrl(){
  
}

public void testGoodSolrHome(){
  
}

public void testBadSolrHome(){
  
}
}
