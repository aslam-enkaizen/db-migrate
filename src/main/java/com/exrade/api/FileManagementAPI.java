package com.exrade.api;

import com.exrade.core.ExRequestEnvelope;
import com.exrade.runtime.filemanagement.IFileStorageController;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface FileManagementAPI {

	void setFileStorageController(IFileStorageController fileStorageController);

	byte[] getFileAsByteArray(ExRequestEnvelope request, String fileName);

	String storeFileFromByteArray(ExRequestEnvelope request, byte[] content, String fileExtension,
			Map<String, Object> metaData);

	String storeFile(ExRequestEnvelope request, File file, Map<String, Object> metaData) throws IOException;

	File getFile(ExRequestEnvelope request, String fileName) throws IOException;

	Map<String, Object> getMetadata(ExRequestEnvelope request, String fileName);

	List<Map<String, Object>> listFilesMetadata(ExRequestEnvelope request, Map<String, String> filterParams);

	void updateMetadata(ExRequestEnvelope request, String fileName, Map<String, Object> metaData);

	void deleteFile(ExRequestEnvelope request, String fileName);

	String replaceFile(ExRequestEnvelope request, String fileName, byte[] content, String fileExtension,
			Map<String, Object> metaData);

	String convertToDataFile(ExRequestEnvelope exRequestEnvelope, String fileUuid);
}