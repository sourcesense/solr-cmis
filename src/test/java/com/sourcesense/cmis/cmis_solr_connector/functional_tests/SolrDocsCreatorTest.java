package com.sourcesense.cmis.cmis_solr_connector.functional_tests;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.PropertyType;
import org.apache.solr.common.SolrInputDocument;

import com.sourcesense.cmis.cmis_solr_connector.solr.SolrDocsCreator;

public class SolrDocsCreatorTest extends TestCase {

  private SolrDocsCreator creator;
  
  public void setUp(){
    this.creator=new SolrDocsCreator();
  }
  
  public void testDocsConversion(){
    Document cmisDoc1=this.getTestDocument("1");
    Document cmisDoc2=this.getTestDocument("2");
    List<Document> cmisDocs=new ArrayList<Document>();
    cmisDocs.add(cmisDoc1); 
    cmisDocs.add(cmisDoc2);
    List<SolrInputDocument> SolrDocs = creator.fromCmisDocsToSolr(cmisDocs,"");
    assertEquals(2,SolrDocs.size());
    for(int i=1;i<3;i++){
      assertEquals("test"+i+".txt",SolrDocs.get(i-1).getField("title").getFirstValue());
      assertEquals(""+i,SolrDocs.get(i-1).getField("id").getFirstValue());
    }
  }
  
  protected Document getTestDocument(String id )
  {
      ObjectType documentObjectType = mock(ObjectType.class);
      when(documentObjectType.getId()).thenReturn(BaseTypeId.CMIS_DOCUMENT.value());

      Document documentObject = createMockedCmisObject(new Object[][]{
              {PropertyIds.NAME, "Name", "test"+id+".txt"},
              {PropertyIds.CONTENT_STREAM_LENGTH, "Content Stream Length", "210"},
              {PropertyIds.BASE_TYPE_ID, "Base Type Id", "cmis:document"},
              {PropertyIds.OBJECT_TYPE_ID, "Object Type Id", "cmis:document"},
              {PropertyIds.LAST_MODIFICATION_DATE, "Last Modification Date", new Date(0)},
              {PropertyIds.OBJECT_TYPE_ID, "Object Type Id", "cmis:document"},
              {PropertyIds.CONTENT_STREAM_MIME_TYPE, "Content Stream Mime Type", "text/plain"},
              {PropertyIds.OBJECT_ID, "Object Type Id",id}}, Document.class);
      when(documentObject.getId()).thenReturn(id);
      when(documentObject.getBaseType()).thenReturn(documentObjectType);
      return documentObject;
  }
  /**
   * Create a mock CmisObject using Mockito that will contain the provided properties
   *
   * @param properties Array of arrays in the form of {{propertyId, displayName, propertyValue}}
   * @param clazz      Class object in the CmisObject hierarchy that has to be created
   * @return The mocked CmisObject
   */
  protected <T extends CmisObject> T createMockedCmisObject(Object[][] properties, Class<T> clazz)
  {
      T object = mock(clazz);
      List<Property<?>> documentProperties = new ArrayList<Property<?>>();
      for (Object[] mockProp : properties)
      {
          Property prop = createMockedProperty(mockProp[0], mockProp[1], mockProp[2]);
          documentProperties.add(prop);
          when(object.getProperty(mockProp[0].toString())).thenReturn(prop);

          if (PropertyIds.NAME.equals(mockProp[0]))
          {
              when(object.getName()).thenReturn(mockProp[2].toString());
          }
      }
      when(object.getProperties()).thenReturn(documentProperties);

      return object;
  }
  
  @SuppressWarnings("unchecked")
  protected Property<?> createMockedProperty(Object id, Object displayName, Object value)
  {
      Property<String> property = mock(Property.class);

      when(property.getId()).thenReturn(id.toString()).toString();
      when(property.getDisplayName()).thenReturn(displayName.toString());
      when(property.getValueAsString()).thenReturn(value == null ? "" : value.toString());

      PropertyDefinition def = mock(PropertyDefinition.class);
      when(def.getPropertyType()).thenReturn(PropertyType.STRING);
      when(property.getDefinition()).thenReturn(def);

      return property;
  }



}
