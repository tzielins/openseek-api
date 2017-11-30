/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fairdom;

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.SearchResult;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.SampleType;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.fetchoptions.SampleTypeFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.search.SampleTypeSearchCriteria;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.semanticannotation.SemanticAnnotation;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.semanticannotation.fetchoptions.SemanticAnnotationFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.semanticannotation.search.SemanticAnnotationSearchCriteria;
import ch.systemsx.cisd.common.spring.HttpInvokerUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.text.SimpleDateFormat;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tzielins
 */
public class SemanticAnnotationsTest {

    static final int TIMEOUT = 500000;
    String endpoint;
    String sessionToken;
    
    IApplicationServerApi v3;
    
    @Before
    public void setUp() throws AuthenticationException, IOException {
        endpoint = "https://127.0.0.1:8443/openbis/openbis";
	
        Authentication au = new Authentication(endpoint, "seek","seek");
        sessionToken = au.sessionToken();
        
        v3 = HttpInvokerUtils.createServiceStub(IApplicationServerApi.class,
				endpoint + IApplicationServerApi.SERVICE_URL, TIMEOUT);        
        
    }

    @Test
    public void loginWorks() {
        assertNotNull(sessionToken);
        assertNotNull(v3);
    }
    
    @Test
    public void listsSemanticAnnotations() {
        
        SemanticAnnotationSearchCriteria searchCriteria = new SemanticAnnotationSearchCriteria();
        SemanticAnnotationFetchOptions fetchOptions = new SemanticAnnotationFetchOptions();
        fetchOptions.withEntityType();
        fetchOptions.withPropertyType();
        
        SearchResult<SemanticAnnotation> result = v3.searchSemanticAnnotations(sessionToken, searchCriteria, fetchOptions);
         
        for (SemanticAnnotation annotation : result.getObjects())
        {
            
            System.out.println(annotation.getPermId()+"\t,"+annotation.getEntityType()
            +"\t,"+annotation.getPropertyType()
            +"\t,"+annotation.getPredicateOntologyId()+"\t"+annotation.getPredicateAccessionId()
            +"\t,"+annotation.getDescriptorOntologyId()+"\t"+annotation.getDescriptorAccessionId()
            );  
        }
        assertFalse(result.getTotalCount() == 0);
    }
    
    
    @Test
    public void listsTypesWithSemanticAnnotations() throws JsonProcessingException {
        
        String assayDescId = "assay";
        //String isAId = "is_a";
        String isAId = "pa_id";
        
        SampleTypeFetchOptions fetchOptions = new SampleTypeFetchOptions();
        fetchOptions.withSemanticAnnotations();
        fetchOptions.withPropertyAssignments().withSemanticAnnotations();
         
        SampleTypeSearchCriteria searchCriteria = new SampleTypeSearchCriteria();
        
        
        SemanticAnnotationSearchCriteria semCriteria = searchCriteria.withSemanticAnnotations();
        semCriteria.withDescriptorAccessionId().thatEquals(assayDescId);
        semCriteria.withPredicateAccessionId().thatEquals(isAId);
        
        SearchResult<SampleType> result = v3.searchSampleTypes(sessionToken, searchCriteria, fetchOptions);

        result.getObjects().forEach( o -> {
            System.out.println(o.getCode());
           
            o.getPropertyAssignments().forEach( p -> p.setFetchOptions(null));
            o.getSemanticAnnotations().forEach(s -> s.setFetchOptions(null));
            o.setFetchOptions(null);
                });
        
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        mapper.setDateFormat(df);
        
        String json = mapper.writeValueAsString(result.getObjects());
        System.out.println(json);
        
        for (SampleType sampleType : result.getObjects())
        {
            System.out.println(sampleType.getCode()+", pID:"+sampleType.getPermId());  
        }
        assertFalse(result.getTotalCount() == 0);
        assertEquals("UNKNOWN",result.getObjects().get(0).getCode());
        

    }
    
    
}
