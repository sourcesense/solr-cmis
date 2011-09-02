Intro

The CMIS - SOLR Connector is a System prototype who aims to attend to the need of a connection between a generic CMIS[1] Repository and a Solr[2] server.

The aim of the proto is to :

- retrieve Documents from one ( or more) Repository that support CMIS

- extract the content, the name and other useful information from the retrieved documents

- index this information in a Solr Server .

In the first place, the system has to interface with a CMIS Repository and with a Solr instance.

The Chemistry[3] library is used for the connection with the CMIS Repository while the SOLRJ[4] library is used for the connection with Solr.

To retrieve the documents needed, the system will open a connection with the CMIS Repository in input and query the Repository with a specific CMIS query.

The CMIS Documents retrieved will be converted in Solr Documents with a fixed standard schema (independent from the repository ) :

the schema is defined to collect all the useful information coming from the CMIS source, maintaining uniformity in the content to index.

Then the system will open a connection with the Solr server in input and create a new Solr Core in the server (specific for CMIS-converted documents).

This Core will index the CMIS-converted documents making possible the search in Solr.

Input

The input of the system are:

+ one or more repository, CMIS compatible (the connection to the repository is identified by the Url, the username and the password)

+ a Solr Server (the Url of the Solr Server and the path to the Solr Home, note: the Solr Server has to be running with multi-core active )

Architecture

Let's see a brief view of the system architecture:

                                        /  Input Loader

CmisSolrConnector -  |  Cmis Docs Retriever

                                       |  Solr Docs Creator       - -  Content Extractor

                                       \  Sorl Docs Indexer

Input Loader

This module has the responsibility of load the input of the system from an input source. 

Cmis Docs Retriever

This module uses Chemistry to :

- connect to the input repository

- make the query to the repository ( for instance , a simple query that retrieves all the documents in the Repository)

Solr Docs Creator

This module operates the conversion between Cmis:Document and Solr Document.

The Information extracted from the Cmis Document is saved in specific Solr Fields:

- id

- title

- author

- version

- content_type (the mime type of the Document)

- text (the content extracted from the Document)

- repositoryPath (the Path of the Document, in the repository)

Content Extractor

This module extracts the textual content from the content stream of a CMIS:Document.

To extract text from different mime type, this module interfaces with the Apache Tika[5] Libraries.

On the other hand , to extract info from xml (or html ) documents , this module uses the Xerces and Nekohtml[6] libraries.

At the time of writing this brief system description, the extraction of info from html is very simple:

the entire body is extracted.

Solr Docs Indexer

This module has to index the early created Solr Documents in a running Solr Server.

To avoid Schema-conflict , a new Core is created (specific for the Cmis-converted documents).

Obviously the input Solr server must support the multi-core.

This core is created with a configuration suitable for the Cmis-converted documents.

After this quick procedure of core creation, the Documents are indexed and the core is ready to serve search request!

This prototype has been tested with Alfresco Repository (3.3 supporting CMIS 1.0) and Solr( 1.4.1).

TO DO :

- New Directions

[1] [Content Management Interoperability Services - Wikipedia|http://en.wikipedia.org/wiki/Content_Management_Interoperability_Services]

﻿[2] [Welcome to Solr|http://lucene.apache.org/solr/]

[3] [Apache Chemistry|http://incubator.apache.org/chemistry/]

[4] [Solrj|http://wiki.apache.org/solr/Solrj]

[5][Apache Tika|http://tika.apache.org/]

[6] [Xerces|http://en.wikipedia.org/wiki/Xerces] , [NekoHtml|http://nekohtml.sourceforge.net/]