package com.sourcesense.cmis.cmis_solr_connector.functional_tests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.chemistry.opencmis.client.api.Document;
import org.junit.Test;

import com.sourcesense.cmis.cmis_solr_connector.CmisSolrConnectorException;
import com.sourcesense.cmis.cmis_solr_connector.cmis.CmisDocsRetriever;
/**questa maledetta bestia ancora non funziona*/
public class CmisDocsRetrieverTest extends TestCase {
  protected static final String TEST_REP = "http://cmis.alfresco.com:80/service/cmis";
  protected static final String TEST_USERNAME = "admin";
  protected static final String TEST_PASSWORD = "admin";
  private CmisDocsRetriever retriever;
  private static final String REPOSITORY_ID = "UnitTestRepository";
/*
  static public class UnitTestRepositoryInfo implements RepositoryInfoCreator {

    /*
      public RepositoryInfo createRepositoryInfo() {
          RepositoryCapabilitiesImpl caps = new RepositoryCapabilitiesImpl();
          caps.setAllVersionsSearchable(false);
          caps.setCapabilityAcl(CapabilityAcl.NONE);
          caps.setCapabilityChanges(CapabilityChanges.NONE);
          caps.setCapabilityContentStreamUpdates(CapabilityContentStreamUpdates.ANYTIME);
          caps.setCapabilityJoin(CapabilityJoin.NONE);
          caps.setCapabilityQuery(CapabilityQuery.NONE);
          caps.setCapabilityRendition(CapabilityRenditions.NONE);
          caps.setIsPwcSearchable(false);
          caps.setIsPwcUpdatable(true);
          caps.setSupportsGetDescendants(true);
          caps.setSupportsGetFolderTree(true);
          caps.setSupportsMultifiling(false);
          caps.setSupportsUnfiling(true);
          caps.setSupportsVersionSpecificFiling(false);

          RepositoryInfoImpl repositoryInfo = new RepositoryInfoImpl();
          repositoryInfo.setId(REPOSITORY_ID);
          repositoryInfo.setName("InMemory Repository");
          repositoryInfo.setDescription("InMemory Test Repository");
          repositoryInfo.setCmisVersionSupported("0.7");
          repositoryInfo.setCapabilities(caps);
          repositoryInfo.setRootFolder("/");
          repositoryInfo.setAclCapabilities(null);
          repositoryInfo.setPrincipalAnonymous("anonymous");
          repositoryInfo.setPrincipalAnyone("anyone");
          repositoryInfo.setThinClientUri(null);
          repositoryInfo.setChangesIncomplete(Boolean.TRUE);
          repositoryInfo.setChangesOnType(null);
          repositoryInfo.setLatestChangeLogToken(null);
          repositoryInfo.setVendorName("OpenCMIS");
          repositoryInfo.setProductName("OpenCMIS Client");
          repositoryInfo.setProductVersion("0.1");
          return repositoryInfo;
      }
  }
*//*
  @Before
  public void setUp() throws Exception {
    retriever=new CmisDocsRetriever();
      super.setTypeCreatorClass(RepositoryTestTypeSystemCreator.class.getName());
      super.setUp();
  }

  @After
  public void tearDown() throws Exception {
      super.tearDown();
  }
/*
  @Test
  public void testRepositoryInfo() throws Exception {
    Map<String, String> parameters=new HashMap<String,String>();
    Session cmisSession = null;
      String repositoryId = this.REPOSITORY_ID;
      if (repositoryId != null && !repositoryId.isEmpty()) {
        parameters.put(SessionParameter.REPOSITORY_ID, repositoryId);
      }
      List<Repository> repos = SessionFactoryImpl.newInstance().getRepositories(parameters);
      for(Repository rep:repos){
        cmisSession=rep.createSession();
      }
      cmisSession.toString();
  }*/

  public void setUp() throws Exception {
    retriever=new CmisDocsRetriever();  
  }
  
  @Test
  public void testRetrieveFromAlfrescoOnlineServer(){
    Map<String, com.sourcesense.cmis.cmis_solr_connector.model.RepositoryInfo> allRepositoriesInfo=new HashMap<String,com.sourcesense.cmis.cmis_solr_connector.model.RepositoryInfo>();
    com.sourcesense.cmis.cmis_solr_connector.model.RepositoryInfo repInfo=new com.sourcesense.cmis.cmis_solr_connector.model.RepositoryInfo(TEST_REP, TEST_USERNAME, TEST_PASSWORD);
    allRepositoriesInfo.put(TEST_REP,repInfo);
    Map<com.sourcesense.cmis.cmis_solr_connector.model.RepositoryInfo, List<Document>> retrievedAllDocs=null;
    try {
      retrievedAllDocs = retriever.retrieveAllDocs(allRepositoriesInfo,"1900-08-06T22:22:22.123Z");
    } catch (CmisSolrConnectorException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      fail();
    }
    assertEquals(1,retrievedAllDocs.values().size());
  }
  /*
   * inutile ed oneroso creare un istanza autocontenuta di Alfresco, sradica l'essenza del tool stesso
  public void setUp() {
    this.startDB();
    // initialise app content
    // initialise app content
    System.out.println("END DB INIT");
    ApplicationContext ctx = ApplicationContextHelper.getApplicationContext();
    // get registry of services
    final ServiceRegistry serviceRegistry = (ServiceRegistry) ctx.getBean(ServiceRegistry.SERVICE_REGISTRY);

    // use TransactionWork to wrap service calls in a user transaction
    TransactionService transactionService = serviceRegistry.getTransactionService();
    RetryingTransactionCallback<Object> exampleWork = new RetryingTransactionCallback<Object>() {
      public Object execute() throws Exception {

        doExample(serviceRegistry, "rtf", "doc11");
        doExample(serviceRegistry, "pdf", "doc22");
        return null;
      }
    };
    transactionService.getRetryingTransactionHelper().doInTransaction(exampleWork);
    System.exit(0);
  }

  private void startDB() {
    try {
      File ourAppDir1 = new File("./mySqlDB/src");
      File databaseDir1 = new File(ourAppDir1, "database");
      int port = 3306;
      String dbName = "alfresco";
      String url = "jdbc:mysql:mxj://localhost:" + port + "/" + dbName //
              + "?" + "server.basedir=" + databaseDir1 //
              + "&" + "createDatabaseIfNotExist=true"//
              + "&" + "server.initialize-user=true" //
      ;
      Connection connection = null;
      Class.forName("com.mysql.jdbc.Driver").newInstance();
      String userName = "alfresco";
      String password = "alfresco";
      connection = DriverManager.getConnection(url, userName, password);
      connection.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void testProva() {
    System.out.println("gdfk");
  }

  public static void doExample(ServiceRegistry serviceRegistry, String type, String title) throws Exception {
    //
    // authenticate
    //
   AuthenticationService authenticationService = serviceRegistry.getAuthenticationService();
   //authenticationService.authenticate("admin", "admin".toCharArray());
   authenticationService.authenticateAsGuest();
    //
    // locate the company home node
    //
    SearchService searchService = serviceRegistry.getSearchService();
    StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
    ResultSet resultSet = searchService.query(storeRef, SearchService.LANGUAGE_LUCENE, "PATH:\"/app:company_home\"");
    NodeRef companyHome = resultSet.getNodeRef(0);
    resultSet.close();

    //
    // create new content node within company home
    //

    // assign name
    String name = title + "." + type;
    Map<QName, Serializable> contentProps = new HashMap<QName, Serializable>();
    contentProps.put(ContentModel.PROP_NAME, name);

    // create content node
    NodeService nodeService = serviceRegistry.getNodeService();
    ChildAssociationRef association = nodeService.createNode(companyHome, ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_PREFIX, name), ContentModel.TYPE_CONTENT,
            contentProps);
    NodeRef content = association.getChildRef();

    // add titled aspect (for Web Client display)
    Map<QName, Serializable> titledProps = new HashMap<QName, Serializable>();
    titledProps.put(ContentModel.PROP_TITLE, name);
    titledProps.put(ContentModel.PROP_DESCRIPTION, name);
    nodeService.addAspect(content, ContentModel.ASPECT_TITLED, titledProps);

    //
    // write some content to new node
    //

    ContentService contentService = serviceRegistry.getContentService();
    ContentWriter writer = contentService.getWriter(content, ContentModel.PROP_CONTENT, true);
    writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
    writer.setEncoding("UTF-8");
    String text = "I'm a txt document.";
    writer.putContent(text);
  }*/
}
