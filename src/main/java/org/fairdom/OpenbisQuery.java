package org.fairdom;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import ch.ethz.sis.openbis.generic.dss.api.v3.IDataStoreServerApi;
import ch.ethz.sis.openbis.generic.dss.api.v3.dto.download.DataSetFileDownload;
import ch.ethz.sis.openbis.generic.dss.api.v3.dto.download.DataSetFileDownloadOptions;
import ch.ethz.sis.openbis.generic.dss.api.v3.dto.download.DataSetFileDownloadReader;
import ch.ethz.sis.openbis.generic.dss.api.v3.dto.entity.datasetfile.DataSetFile;
import ch.ethz.sis.openbis.generic.dss.api.v3.dto.id.datasetfile.DataSetFilePermId;
import ch.ethz.sis.openbis.generic.dss.api.v3.dto.search.DataSetFileSearchCriteria;
import ch.ethz.sis.openbis.generic.shared.api.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.entity.dataset.DataSet;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.entity.experiment.Experiment;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.entity.sample.Sample;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.fetchoptions.dataset.DataSetFetchOptions;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.fetchoptions.experiment.ExperimentFetchOptions;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.fetchoptions.sample.SampleFetchOptions;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.search.DataSetSearchCriteria;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.search.ExperimentSearchCriteria;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.search.SampleSearchCriteria;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.search.SearchResult;
import ch.systemsx.cisd.openbis.generic.shared.api.v3.json.GenericObjectMapper;

/**
 * Created by quyennguyen on 13/02/15.
 */
public class OpenbisQuery {
    private IApplicationServerApi as;
    private IDataStoreServerApi dss;
    private String sessionToken;

    public OpenbisQuery(IApplicationServerApi startAs, IDataStoreServerApi startDss, String startSessionToken ){
        as = startAs;
        dss = startDss;
        sessionToken = startSessionToken;
    }

    public SearchResult query(String type, String property, String propertyValue) throws InvalidOptionException {
    	SearchResult result = null;
        if (type.equals("Experiment")){
            result = experiments(property, propertyValue);
        }else if (type.equals("Sample")){
            result = samples(property, propertyValue);
        }else if (type.equals("DataSet")){
            result = dataSets(property, propertyValue);
        }else{
            throw new InvalidOptionException("Unrecognised type: " + type);
        }
        return result;
    }


    public String jsonResult(SearchResult result){
        GenericObjectMapper mapper = new GenericObjectMapper();
        StringWriter sw = new StringWriter();
        try {
            mapper.writeValue(sw, result);
        }catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return sw.toString();
    }

    public SearchResult <Experiment> experiments(String property, String propertyValue){
        ExperimentSearchCriteria criterion = new ExperimentSearchCriteria();
        criterion.withProperty(property).thatEquals(propertyValue);

        ExperimentFetchOptions options = new ExperimentFetchOptions();
        options.withProperties();

        SearchResult<Experiment> experiments = as.searchExperiments(sessionToken, criterion, options);
        return experiments;
    }

    public SearchResult <Sample> samples(String property, String propertyValue){
        SampleSearchCriteria criterion = new SampleSearchCriteria();
        criterion.withProperty(property).thatEquals(propertyValue);

        SampleFetchOptions options = new SampleFetchOptions();
        options.withProperties();

        SearchResult<Sample> samples = as.searchSamples(sessionToken, criterion, options);
        return samples;
    }

    public SearchResult <DataSet> dataSets(String property, String propertyValue){
        DataSetSearchCriteria criterion = new DataSetSearchCriteria();
        criterion.withProperty(property).thatEquals(propertyValue);

        DataSetFetchOptions options = new DataSetFetchOptions();
        options.withProperties();

        SearchResult <DataSet> dataSets = as.searchDataSets(sessionToken, criterion, options);
        return dataSets;
    }
    
    public List <DataSetFile> dataSetFile(String property, String propertyValue){
    	 DataSetFileSearchCriteria criteria = new DataSetFileSearchCriteria();
    	 criteria.withDataSet().withProperty(property).thatEquals(propertyValue);

         List<DataSetFile> searchFiles = dss.searchFiles(sessionToken, criteria);
         return searchFiles;
    }
    
    public String downloadDataSetFile(List<DataSetFile> files) throws IOException{
    	 DataSetFileDownloadOptions options = new DataSetFileDownloadOptions();
         List<DataSetFilePermId> fileIds = new ArrayList<DataSetFilePermId> ();
         for (int i = 0; i < files.size(); i++) {
         	fileIds.add(files.get(i).getPermId());
         	}
         
 		 InputStream stream = dss.downloadFiles(sessionToken, fileIds, options);
         DataSetFileDownloadReader reader = new DataSetFileDownloadReader(stream);
        

         DataSetFileDownload download = null;
         String content = new String();
         while ((download = reader.read()) != null)         {                         
             content = IOUtils.toString(download.getInputStream());
         }
         return content;
   }

}
