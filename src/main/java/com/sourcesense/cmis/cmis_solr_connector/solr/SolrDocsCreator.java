package com.sourcesense.cmis.cmis_solr_connector.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.solr.common.SolrInputDocument;

import com.sourcesense.cmis.cmis_solr_connector.ContentExtractor;
import com.sourcesense.cmis.cmis_solr_connector.util.DateUtils;

/**
 * This class operates the creation of Solr Documents,making a conversion from Cmis:Document to Solr
 * Document.
 * 
 * @author Alessandro Benedetti
 * 
 */
public class SolrDocsCreator {

  private ContentExtractor contentExtractor;

  public SolrDocsCreator() {
    this.contentExtractor = new ContentExtractor();
  }

  /**
   * operates a conversion between Cmis Documents and Solr Documents. The fields of the Solr Doc
   * are: - name - version - type - content
   * 
   * @param cmisDocs
   *          -the input Cmis Documents
   * @param space 
   * @return the list of Solr Documents
   */
  public List<SolrInputDocument> fromCmisDocsToSolr(List<Document> cmisDocs, String space) {
    List<SolrInputDocument> solrDocs = new ArrayList<SolrInputDocument>();
    for (Document cmisDoc : cmisDocs) {
      if(this.checkFolder(cmisDoc.getParents(),space)){
      SolrInputDocument solrCurrent = this.fromCmisToSolr(cmisDoc);
      solrDocs.add(solrCurrent);}
    }
    return solrDocs;
  }

  private boolean checkFolder(List<Folder> list, String space) {
   boolean result=false;
   if(space.equals(""))
     result=true;
   else
   for(Folder folder:list){
     if(folder.getName().equals(space))
       return true;
   }
   return result;
  }

  /**
   * converts a Cmis:Document to a SolrDocument. The fields created in the Solr Doc are :
   * id,title,author,version, content_type,text and repositoryPath!
   * 
   * @param cmisDoc
   * @return
   */
  private SolrInputDocument fromCmisToSolr(Document cmisDoc) {
    SolrInputDocument solrDoc = new SolrInputDocument();
    solrDoc.addField("id", cmisDoc.getId());
    solrDoc.addField("title", cmisDoc.getName());
    solrDoc.addField("author", cmisDoc.getCreatedBy());
    solrDoc.addField("version", cmisDoc.getVersionLabel());
    solrDoc.addField("content_type", cmisDoc.getContentStreamMimeType());
    solrDoc.addField("text", this.extractContent(cmisDoc.getContentStream(), cmisDoc.getName()));
    solrDoc.addField("repositoryPath", cmisDoc.getPaths().toString());
    return solrDoc;
  }

  /**
   * extracts the content from the cmisDocument's contentStream.
   * 
   * @param contentStream
   * @return
   * @throws IOException
   */
  private String extractContent(ContentStream contentStream, String filename) {
    String result = "";
    if (contentStream != null)
      if (contentStream.getMimeType().contains("html")) {
        result = contentExtractor.extractTextFromHtmlStream(contentStream.getStream());
      } else if(contentStream.getMimeType().contains("text/plain")){
        result = contentExtractor.extractFromTxtStream(contentStream.getStream(),filename);
      }else {
        result = contentExtractor.extractFromFileStream(contentStream.getStream(), filename);
      }
    return result;
  }

}
