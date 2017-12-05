package org.fairdom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.SearchResult;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.id.DataSetPermId;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.DataSetFile;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.download.DataSetFileDownload;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.download.DataSetFileDownloadOptions;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.download.DataSetFileDownloadReader;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.fetchoptions.DataSetFileFetchOptions;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.DataSetFilePermId;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.IDataSetFileId;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.search.DataSetFileSearchCriteria;

/**
 * Created by quyennguyen on 13/02/15.
 */
public class DataStoreDownload extends DataStoreStream {

	public DataStoreDownload(String startEndpoint, String startSessionToken) {
		super(startEndpoint, startSessionToken);
	}

	public void downloadDataSetFiles(String permId, String destinationFolder) throws IOException {
		DataSetFileDownloadOptions options = new DataSetFileDownloadOptions();
		options.setRecursive(true);

		DataSetFileSearchCriteria criteria = new DataSetFileSearchCriteria();
		criteria.withDataSet().withCode().thatEquals(permId);

		SearchResult<DataSetFile> result = dss.searchFiles(sessionToken, criteria, new DataSetFileFetchOptions());
		List<DataSetFile> files = result.getObjects();

		List<IDataSetFileId> filesToDownload = new LinkedList<>();
		for (DataSetFile file : files)
			filesToDownload.add(file.getPermId());

		InputStream stream = dss.downloadFiles(sessionToken, filesToDownload, options);
                
                downloadFromDSSStream(stream,destinationFolder);
                /*
		DataSetFileDownloadReader reader = new DataSetFileDownloadReader(stream);
		DataSetFileDownload file = null;

		while ((file = reader.read()) != null) {
			InputStream inputStream = file.getInputStream();
			DataSetFile dataSetFile = file.getDataSetFile();
			if (dataSetFile.isDirectory()) {
				Path dir = Paths.get(destinationFolder, dataSetFile.getPath());
				Files.createDirectories(dir);
			} else {
				File outputFile = new File(destinationFolder, dataSetFile.getPath());
				OutputStream fileOutputStream = new FileOutputStream(outputFile);

				IOUtils.copyLarge(inputStream, fileOutputStream);
				fileOutputStream.close();
			}
		}*/
	}
        
        protected void downloadFromDSSStream(InputStream dssStream, String destinationFolder) throws IOException {
            DataSetFileDownloadReader reader = new DataSetFileDownloadReader(dssStream);
            
            try {
		DataSetFileDownload file;

		while ((file = reader.read()) != null) {
			InputStream inputStream = file.getInputStream();
			DataSetFile dataSetFile = file.getDataSetFile();
			if (dataSetFile.isDirectory()) {
				Path dir = Paths.get(destinationFolder, dataSetFile.getPath());
                                //System.out.println("Making dir: "+dataSetFile.getPath());
                                if (!Files.isDirectory(dir)) {
                                    Files.createDirectories(dir);
                                }
			} else {
                                //System.out.println("Dealing with file: "+dataSetFile.getPath()+", s: "+dataSetFile.getFileLength());
                                if (dataSetFile.getFileLength() == 0) {
                                    //System.out.println("Igoring file with 0 size: "+dataSetFile.getPath());
                                    continue;
                                }
                                Path destPath = Paths.get(destinationFolder, dataSetFile.getPath());
                                Path destDir = destPath.getParent();
                                if (!Files.isDirectory(destDir)) {
                                    Files.createDirectories(destDir);
                                }
                                try (OutputStream fileOutputStream = Files.newOutputStream(destPath)) {
                                    IOUtils.copyLarge(inputStream, fileOutputStream);
                                }
			}
		}
            } finally {
                reader.close();
            }
            
        }

	public void downloadFolder(String permId, String sourceRelativeFolder, String destinationFolder)
			throws IOException {
		DataSetFileDownloadOptions options = new DataSetFileDownloadOptions();
		options.setRecursive(true);
		IDataSetFileId filesToDownload = new DataSetFilePermId(new DataSetPermId(permId), sourceRelativeFolder);

		InputStream stream = dss.downloadFiles(sessionToken, Arrays.asList(filesToDownload), options);
                
                downloadFromDSSStream(stream,destinationFolder);
                
                /*
		DataSetFileDownloadReader reader = new DataSetFileDownloadReader(stream);
		DataSetFileDownload file = null;

		while ((file = reader.read()) != null) {
			InputStream inputStream = file.getInputStream();
			DataSetFile dataSetFile = file.getDataSetFile();
			if (dataSetFile.isDirectory()) {
				Path dir = Paths.get(destinationFolder, dataSetFile.getPath());
                                System.out.println("Making dir: "+dataSetFile.getPath());
                                if (!Files.isDirectory(dir)) {
                                    Files.createDirectories(dir);
                                }
			} else {
                                System.out.println("Dealing with file: "+dataSetFile.getPath()+", s: "+dataSetFile.getFileLength());
                                if (dataSetFile.getFileLength() == 0) {
                                    System.out.println("Igoring file with 0 size: "+dataSetFile.getPath());
                                    continue;
                                }
                                Path destPath = Paths.get(destinationFolder, dataSetFile.getPath());
                                Path destDir = destPath.getParent();
                                if (!Files.isDirectory(destDir)) {
                                    Files.createDirectories(destDir);
                                }
				//File outputFile = new File(destinationFolder, dataSetFile.getPath());
				//OutputStream fileOutputStream = new FileOutputStream(outputFile);
                                try (OutputStream fileOutputStream = Files.newOutputStream(destPath)) {
                                    IOUtils.copyLarge(inputStream, fileOutputStream);
                                }
				//fileOutputStream.close();
			}
		}*/
	}

	public void downloadSingleFile(String permId, String sourceRelative, String destination) throws IOException {
		DataSetFileDownloadOptions options = new DataSetFileDownloadOptions();
		options.setRecursive(false);
		IDataSetFileId fileToDownload = new DataSetFilePermId(new DataSetPermId(permId), sourceRelative);

		InputStream stream = dss.downloadFiles(sessionToken, Arrays.asList(fileToDownload), options);
		DataSetFileDownloadReader reader = new DataSetFileDownloadReader(stream);
                try {
                    DataSetFileDownload file = reader.read();
                    
                    InputStream inputStream = file.getInputStream();

                    File outputFile = new File(destination);
                    try (OutputStream fileOutputStream = new FileOutputStream(outputFile)) {

                        IOUtils.copyLarge(inputStream, fileOutputStream);
                    }
                } finally {
                    reader.close();
                }
	}

}
