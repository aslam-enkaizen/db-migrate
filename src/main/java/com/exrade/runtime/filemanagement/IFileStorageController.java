package com.exrade.runtime.filemanagement;

import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.persistence.query.QueryFilters;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Interface to store and retrieve files
 * @author john
 *
 */
public interface IFileStorageController {

	/**
	 * Store content as file
	 * @param content
	 * @return unique identity of the file
	 */
	String storeFile(byte[] content, String fileExtension, Map<String, Object> metaData);
	
	/**
	 * Store file
	 * @param file
	 * @return unique identity of the file
	 * @throws IOException 
	 */
	String storeFile(File file, Map<String, Object> metaData);
	
	/**
	 * Store data as input stream
	 * @param inputStream
	 * @param file name
	 * @return unique identity of the file
	 * @throws IOException 
	 */
	String storeFile(InputStream stream,String fileName,Map<String, Object> metaData);
	
	/**
	 * Store data as FTP
	 * @param url
	 * @param file name
	 * @return unique identity of the file
	 * @throws IOException 
	 */
	String storeFile(URL url,String fileName,Map<String, Object> metaData);
	
	/**
	 * Retrieve file as byte array from the file name
	 * @param fileUUID
	 * @return byte array
	 */
	byte[] retrieveFileAsByte(String fileUUID);
	
	File retrieveFile(String fileUUID);
	
	Map<String, Object> getFileMetadata(String fileUUID);
	
	List<Map<String, Object>> getFilesMetadata(QueryFilters iFilters);

	void updateMetadata(String fileUUID, Map<String, Object> metaData);
	
	void deleteFile(String fileUUID);

	Integer getMembershipSpaceUsage(Negotiator iActor);
}
