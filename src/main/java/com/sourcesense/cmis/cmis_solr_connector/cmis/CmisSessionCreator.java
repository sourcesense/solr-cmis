package com.sourcesense.cmis.cmis_solr_connector.cmis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;

import com.sourcesense.cmis.cmis_solr_connector.CmisSolrConnectorException;
import com.sourcesense.cmis.cmis_solr_connector.model.RepositoryInfo;


/**
 * This class creates a Session to a Repository, CMIS compatible, using the OpenCmis library.
 * @author Alessandro Benedetti
 *
 */
public class CmisSessionCreator {
  public Session createSessionFromRepInfo(RepositoryInfo rep) throws CmisSolrConnectorException{
    Map<String, String> parameters = new HashMap<String, String>();
    parameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
    parameters.put(SessionParameter.ATOMPUB_URL, rep.getUrl());
    parameters.put(SessionParameter.USER, rep.getUsername());
    parameters.put(SessionParameter.PASSWORD, rep.getPassword());
    return this.createSession(parameters);
  }
  /**
   * creates a connection to the Repository specified by the input parameters
   * (Url,username,password..). Then returns the Session related to the connection.
   * 
   * @param parameters
   *          - The connection Information related to a specific Repository
   * @return
   * @throws CmisSolrConnectorException 
   */
  private Session createSession(Map<String, String> parameters) throws CmisSolrConnectorException {
    
    String repositoryId = parameters.get(SessionParameter.REPOSITORY_ID);
    if (repositoryId != null && !repositoryId.isEmpty()) {
      parameters.put(SessionParameter.REPOSITORY_ID, repositoryId);
    }
    List<Repository> repos;
    try {
      repos = SessionFactoryImpl.newInstance().getRepositories(parameters);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      //e.printStackTrace();to log
      throw new CmisSolrConnectorException("Could not retrieve any CMIS repository with the following parameters: " + parameters);
    }
    if (repos == null || repos.size() <= 0) {
      parameters.remove(SessionParameter.PASSWORD);
      parameters.put(SessionParameter.PASSWORD, "*********");
      throw new CmisSolrConnectorException("Could not retrieve any CMIS repository with the following parameters: " + parameters);
    }

    if (!repos.isEmpty()) {
      Repository repo = repos.iterator().next();
      if (repos.size() > 1) {
        throw new CmisRuntimeException(
                "There is more than one repository supported in this realm; you should define a Repository Id in your configuration; currently, the first is used; Repository ID : " + repo.getId());
      }
      return repo.createSession();
    } else {
      throw new CmisSolrConnectorException("Could not retrieve any CMIS repository with the following parameters: " + parameters);
    }
  }
}
