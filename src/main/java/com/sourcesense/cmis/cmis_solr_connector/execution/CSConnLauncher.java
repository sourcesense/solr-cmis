package com.sourcesense.cmis.cmis_solr_connector.execution;

import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Document;

import com.sourcesense.cmis.cmis_solr_connector.CmisSolrConnectorException;
import com.sourcesense.cmis.cmis_solr_connector.ConnectorFacade;
import com.sourcesense.cmis.cmis_solr_connector.model.RepositoryInfo;

public class CSConnLauncher {

  private static final String input="config/input.xml";
  private static final String coreConfig="config/solr";
  /**
   * @param args
   * @throws CmisSolrConnectorException 
   */
  public static void main(String[] args) {
    String inputString;
    String directoryString;
    System.out.println("Welcome");
    if(args==null||args.length==0){
      System.out.println("Default config");
      inputString=input;
      directoryString=coreConfig;
      }else
      {
        inputString=args[0];
        directoryString=args[1];
      }
    try {
      ConnectorFacade facade=new ConnectorFacade();
      System.out.println("Welcome to Cmis-Solr connector");
      System.out.println("The connector will start using this input file: "+inputString);
      facade.initFromInputFile(inputString);
      Map<RepositoryInfo, List<Document>> retrievedAllDocs = null;
        retrievedAllDocs = facade.retrieveAllDocs(null);
        int cuntModified = 0;
        for (RepositoryInfo url : retrievedAllDocs.keySet()) {
          cuntModified += retrievedAllDocs.get(url).size();
          System.out.println("Succesfully Connected to the "+url.getUrl()+" with this detail :");
          System.out.println (url);
        }
        System.out.println(cuntModified + " New Documents Found!");
        facade.initDocsToIndex(retrievedAllDocs);
        System.out.println("Solr Doc Creation complete!");
        System.out.println(facade.getSolrDocsToIndex().size()+" Documents found in the specified folders");
        System.out.println(facade.getSolrDocsToIndex().size()+" Docs Indexing start! ");
        facade.indexDocsFromExternalConfig(directoryString);
        System.out.println("Succesfully Connected to the Solr instance with this detail :");
        System.out.println ("Url : "+facade.getSolrUrl());
        System.out.println("Solr Docs INDEXING complete!");
    } catch (CmisSolrConnectorException e) {
      e.printStackTrace();
    }

  }

}
