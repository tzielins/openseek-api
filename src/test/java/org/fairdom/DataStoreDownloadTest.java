package org.fairdom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ch.systemsx.cisd.common.parser.MemorySizeFormatter;
import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 * @author Quyen Nugyen
 * @author Stuart Owen
 */
public class DataStoreDownloadTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
	private static String endpoint;
	private static String sessionToken;
        
        File localFile;

	@Before
	public void setUp() throws AuthenticationException, IOException {
		Authentication au = new Authentication("https://openbis-api.fair-dom.org/openbis/openbis", "apiuser",
				"apiuser");
		endpoint = "https://openbis-api.fair-dom.org/datastore_server";
		sessionToken = au.sessionToken();
                
                localFile = testFolder.newFile();
	}

	@Test
	public void downloadSingleFile() throws Exception {
		DataStoreDownload download = new DataStoreDownload(endpoint, sessionToken);
		String permId = "20151217153943290-5";
		String source = "original/api-test";

		assertEquals(0,localFile.length());
		download.downloadSingleFile(permId, source, localFile.getAbsolutePath());

		assertEquals(25, Files.size(localFile.toPath()));
                

	}

	@Test
	public void downloadUtf8File() throws Exception {
		DataStoreDownload download = new DataStoreDownload(endpoint, sessionToken);
		String permId = "20160210130359377-22";
		String source = "original/utf8.txt";

		assertEquals(0,localFile.length());
		download.downloadSingleFile(permId, source, localFile.getAbsolutePath());

		assertEquals(49, Files.size(localFile.toPath()));

	}

	@Test
	public void downloadChineseCharatersFile() throws Exception {
		DataStoreDownload download = new DataStoreDownload(endpoint, sessionToken);
		String permId = "20160212141703195-28";
		String source = "original/chinese";

		assertEquals(0,localFile.length());
		download.downloadSingleFile(permId, source, localFile.getAbsolutePath());

		assertEquals(44, Files.size(localFile.toPath()));

	}

	@Test
	public void downloadWesternEncodeFile() throws Exception {
		DataStoreDownload download = new DataStoreDownload(endpoint, sessionToken);
		String permId = "20160212140647105-27";
		String source = "original/western.txt";
                
		assertEquals(0,localFile.length());
		download.downloadSingleFile(permId, source, localFile.getAbsolutePath());

		assertEquals(44, Files.size(localFile.toPath()));


	}

	@Test
	public void downloadImageFile() throws Exception {
		DataStoreDownload download = new DataStoreDownload(endpoint, sessionToken);
		String permId = "20160210130454955-23";
		String source = "original/autumn.jpg";
		assertEquals(0,localFile.length());
		download.downloadSingleFile(permId, source, localFile.getAbsolutePath());

		assertEquals("537k", MemorySizeFormatter.format(Files.size(localFile.toPath())));

	}

	@Test
	public void downloadFileWithSpaceInName() throws Exception {
		DataStoreDownload download = new DataStoreDownload(endpoint, sessionToken);
		String permId = "20160322172551664-35";
		String source = "original/Genes List Nature Paper Test.docx";
                
		assertEquals(0,localFile.length());
		download.downloadSingleFile(permId, source, localFile.getAbsolutePath());
		assertEquals("27.4k", MemorySizeFormatter.format(Files.size(localFile.toPath())));

	}

	@Test
	@Ignore("very large file")
	public void downloadSingleLargeFile() throws Exception {
		DataStoreDownload download = new DataStoreDownload(endpoint, sessionToken);
		String permId = "20160212120108123-26";
		String source = "original/SEEK-v0.23.0.ova";
                
		assertEquals(0,localFile.length());
		download.downloadSingleFile(permId, source, localFile.getAbsolutePath());
		assertEquals("2.3gb", MemorySizeFormatter.format(Files.size(localFile.toPath())));

	}

	@Test
	public void downloadFolder() throws Exception {
		DataStoreDownload download = new DataStoreDownload(endpoint, sessionToken);
		String permId = "20160215111736723-31";
		String sourceRelativeFolder = "original/DEFAULT";
                
                File destinationFolder = testFolder.newFolder();
                

		download.downloadFolder(permId, sourceRelativeFolder, destinationFolder.getAbsolutePath());

                Path path = destinationFolder.toPath().resolve(sourceRelativeFolder);

                try (Stream<Path> files = Files.list(path)) {
                    
                    List<String> names = files.map( f ->  f.getFileName().toString())
                                            .collect(Collectors.toList());
                    
                    assertEquals(2,names.size());
                    assertTrue(names.contains("fairdom-logo-compact.svg"));
                    assertTrue(names.contains("Stanford_et_al-2015-Molecular_Systems_Biology.pdf"));
                }
		//System.out.println(filesInFolder.get(0));
		//System.out.println(filesInFolder.get(1));
		// assertEquals("fairdom-logo-compact.svg", filesInFolder.get(0));
		// assertEquals("Stanford_et_al-2015-Molecular_Systems_Biology.pdf",
		// filesInFolder.get(1));
	}

	@Test
	public void downloadDataSetFiles() throws Exception {
		DataStoreDownload download = new DataStoreDownload(endpoint, sessionToken);
		String permId = "20151217153943290-5";
		String sourceRelativeFolder = "original";
                
                File destinationFolder = testFolder.newFolder();
                

		download.downloadDataSetFiles(permId, destinationFolder.getAbsolutePath());

                Path path = destinationFolder.toPath().resolve(sourceRelativeFolder);
                
                try (Stream<Path> files = Files.list(path)) {
                    
                    List<String> names = files.map( f ->  f.getFileName().toString())
                                            .collect(Collectors.toList());
                    
                    assertEquals(1,names.size());
                    assertTrue(names.contains("api-test"));
                    
                }
                
	}
}
