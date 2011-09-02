package com.sourcesense.cmis.cmis_solr_connector.cmis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;

import com.sourcesense.cmis.cmis_solr_connector.CmisSolrConnectorException;
import com.sourcesense.cmis.cmis_solr_connector.model.RepositoryInfo;
import com.sourcesense.cmis.cmis_solr_connector.util.DateUtils;

/**
 * This class retrieves Document from a Repository that implements the CMIS specification.
 * It interfaces with the repository using the OpenCmis library from the Apache Chemistry project.
 * @author Alessandro Benedetti
 *
 */
public class CmisDocsRetriever {

  private final String allDocsQuery = "select * from cmis:Document ";
  private final String lastModificationDateQuery = "select * from cmis:Document where cmis:lastModificationDate > TIMESTAMP '";
  private final String cycleQueryDate = "select * from cmis:Document where cmis:lastModificationDate = TIMESTAMP '";
  private final String spaceLastModificationDateQuery = "select * from cmis:Document where cmis:lastModificationDate > TIMESTAMP '";
  private CmisSessionCreator sessionCreator;
  
  public CmisDocsRetriever(){
    this.sessionCreator=new CmisSessionCreator();
  }

  /**
   * returns all the cmis:Document instances from each Repository in input. The Map returned, links
   * the url of the repository with the List of Docs related.
   * Only the most recent docs, modified after the input date time, are retrieved.
   * 
   * @param allRepositoriesInfo
   *          -A map that contains for each Repository in input, the needed Information
   *          (Url,Username,password...)
   * @return the List of cmis:Documents within each repository
   * @throws CmisSolrConnectorException 
   */
  public Map<RepositoryInfo, List<Document>> retrieveAllDocs(Map<String, RepositoryInfo> allRepositoriesInfo,String time) throws CmisSolrConnectorException {
    HashMap<RepositoryInfo, List<Document>> url2cmisDocuments = new HashMap<RepositoryInfo, List<Document>>();
    Session cmisSession;
    for (RepositoryInfo repository : allRepositoriesInfo.values()) {
      List<Document> cmisDocs;
      cmisSession = sessionCreator.createSessionFromRepInfo(repository);
      cmisDocs = retrieveCmisDocuments(cmisSession,time);
      url2cmisDocuments.put(repository, cmisDocs);
    }
    return url2cmisDocuments;
  }

  /**
   * * returns all the cmis:Document instances from each Repository in input. The Map returned, links
   * the url of the repository with the List of Docs related.
   * Only the most recent docs, modified after the input date time, are retrieved.
   * 
   * @param cmisSession
   * @param time
   * @param space 
   * @return
   */
  private List<Document> retrieveCmisDocuments(Session cmisSession,String time) {
    List<Document> cmisDocs;
    String currentQueryTime=lastModificationDateQuery+time+"'";/*
    if(!space.equals("")){
      System.out.println("I'm searching into "+space);
    currentQueryTime=spaceLastModificationDateQuery+time+"' AND in_folder('"+space+"')";}*/
    ItemIterable<QueryResult> queryResults = cmisSession.query(currentQueryTime, false);
    cmisDocs = this.extractDocsFromQueryResults(queryResults, cmisSession);
    cmisDocs=this.filterByDate(cmisDocs,time);
    return cmisDocs;
  }

  /**
   * filters the input list of Cmis:Document, accepting only the Docs modified after the input date time.
   *si pu˜ modificare introducendo un secondo parametro a yesterday, cos“ da effettuare una selezione migliore
   * @param cmisDocs
   * @param time
   * @return
   */
  private List<Document> filterByDate(List<Document> cmisDocs,String time) {
    List<Document> lastModified=new ArrayList<Document>();
    for(Document cmisDoc:cmisDocs){
      if(DateUtils.render(cmisDoc.getLastModificationDate()).compareTo(time)>=0)
        lastModified.add(cmisDoc);
    }
    return lastModified;
  }

  /**
   * converts each queryResult in input in the related Cmis:Document.
   * 
   * @param queryResults
   *          -The list of QueryResults
   * @param session
   *          -The current Cmis-Session with the Repository
   * @return
   */
  private List<Document> extractDocsFromQueryResults(ItemIterable<QueryResult> queryResults, Session session) {
    List<Document> cmisDocs = new ArrayList<Document>();
    ObjectType type = session.getTypeDefinition("cmis:Document");
    PropertyDefinition<?> objectIdPropDef = type.getPropertyDefinitions().get(PropertyIds.OBJECT_ID);
    String objectIdQueryName = objectIdPropDef.getQueryName();
    for (QueryResult qResult : queryResults) {
      String objectId = qResult.getPropertyValueByQueryName(objectIdQueryName);
      Document cmisCurrent = (Document) session.getObject(session.createObjectId(objectId));
      cmisDocs.add(cmisCurrent);
    }
    return cmisDocs;
  }

  
  

}
