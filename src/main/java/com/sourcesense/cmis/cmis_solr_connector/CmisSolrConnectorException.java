package com.sourcesense.cmis.cmis_solr_connector;

public class CmisSolrConnectorException extends Exception {

  /**
   * This exception is the standard exception for the Connector Tool.
   * When something bad happens, the exception will contain the relative error message
   */
  private static final long serialVersionUID = 1L;

  public CmisSolrConnectorException() {
    // TODO Auto-generated constructor stub
  }

  public CmisSolrConnectorException(String message) {
    super(message);
    // TODO Auto-generated constructor stub
  }

  public CmisSolrConnectorException(Throwable cause) {
    super(cause);
    // TODO Auto-generated constructor stub
  }

  public CmisSolrConnectorException(String message, Throwable cause) {
    super(message, cause);
    // TODO Auto-generated constructor stub
  }

}
