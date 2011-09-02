package com.sourcesense.cmis.cmis_solr_connector.failure_tests;

import java.io.File;

import com.sourcesense.cmis.cmis_solr_connector.CmisSolrConnectorException;
import com.sourcesense.cmis.cmis_solr_connector.solr.SolrDocsIndexer;

import junit.framework.TestCase;

public class SolrDocsIndexerTest extends TestCase {

private SolrDocsIndexer indexer;

public void setUp(){
  this.indexer=new SolrDocsIndexer();
}

public void testMalformedSolrUrl(){
  indexer.setSolrUrl("hhttp:/wrongUrl.df.ty");
  try {
    indexer.indexNewCore(null);
  } catch (CmisSolrConnectorException e) {
assertEquals("The Solr Url is malformed- Check the Url in the input File of the connector",e.getMessage());
  }
}

public void testNotExistentSolrUrl(){
  indexer.setSolrUrl("http://wrongUrl.df");
  try {
    indexer.indexNewCore(null);
  } catch (CmisSolrConnectorException e) {
assertEquals("The Solr instance is not running!",e.getMessage());
  }
}

public void testNotRunningSolrUrl(){
  indexer.setSolrUrl("http://www.google.com");
  try {
    indexer.indexNewCore(null);
  } catch (CmisSolrConnectorException e) {
assertEquals("The Solr instance is not running!",e.getMessage());
  }
}

public void testNotCoreConfig(){
  indexer.setCoreConfPath("/confi/imnotexist");
  File testDir=new File("testDir");
  testDir.mkdir();
  indexer.setSolrHome(testDir.getAbsolutePath());
  try {
    indexer.createNewCmisCore(null);
  } catch (CmisSolrConnectorException e) {
assertEquals("Core Config not found- check the config directory",e.getMessage());
  }
}

public void testNotExistentSolrHome(){
  indexer.setCoreConfPath("/conf/exampleCoreConfig");
  indexer.setSolrHome("./imnotexist");
  try {
    indexer.createNewCmisCore(null);
  } catch (CmisSolrConnectorException e) {
assertEquals("The Solr instance is not running!",e.getMessage());
  }
}

}
