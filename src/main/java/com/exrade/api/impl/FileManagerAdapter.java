package com.exrade.api.impl;

import com.exrade.api.FileManagementAPI;
import com.exrade.core.ExRequestEnvelope;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.filemanagement.FileManager;
import com.exrade.runtime.filemanagement.FileMetadata;
import com.exrade.runtime.filemanagement.IFileManager;
import com.exrade.runtime.filemanagement.IFileStorageController;
import com.exrade.util.ContextHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class FileManagerAdapter implements FileManagementAPI {

	private IFileManager manager = new FileManager();

	@Override
	public void setFileStorageController(IFileStorageController fileStorageController) {
	}

	@Override
	public byte[] getFileAsByteArray(ExRequestEnvelope request, String fileName) {
		ContextHelper.initContext(request);
		return manager.getFileAsByteArray(fileName);
	}

	@Override
	public String storeFileFromByteArray(ExRequestEnvelope request, byte[] content, String fileExtension,
			Map<String, Object> metaData) {
		ContextHelper.initContext(request);
		return manager.storeFileFromByteArray(content, fileExtension, metaData);
	}

	@Override
	public String storeFile(ExRequestEnvelope request, File file, Map<String, Object> metaData) throws IOException {
		ContextHelper.initContext(request);
		return manager.storeFile(file, metaData);
	}

	@Override
	public File getFile(ExRequestEnvelope request, String fileName) throws IOException {
		ContextHelper.initContext(request);
		return manager.getFile(fileName);
	}

	@Override
	public Map<String, Object> getMetadata(ExRequestEnvelope request, String fileName) {
		ContextHelper.initContext(request);
		return manager.getMetadata(fileName);
	}

	@Override
	public void updateMetadata(ExRequestEnvelope request, String fileName, Map<String, Object> metaData) {
		ContextHelper.initContext(request);
		manager.updateMetadata(fileName, metaData);
		;
	}

	@Override
	public void deleteFile(ExRequestEnvelope request, String fileName) {
		ContextHelper.initContext(request);
		manager.deleteFile(fileName);
	}

	@Override
	public String replaceFile(ExRequestEnvelope request, String fileName, byte[] content, String fileExtension,
			Map<String, Object> metaData) {
		ContextHelper.initContext(request);
		return manager.replaceFile(fileName, content, fileExtension, metaData);
	}

	@Override
	public List<Map<String, Object>> listFilesMetadata(ExRequestEnvelope request, Map<String, String> filterParams) {
		ContextHelper.initContext(request);

		QueryFilters queryFilter = QueryFilters.create(filterParams);
		queryFilter.putIfNotEmpty(FileMetadata.ASSET_UUID, filterParams.get(FileMetadata.ASSET_UUID));
		queryFilter.putIfNotEmpty(FileMetadata.AUTHOR, filterParams.get(FileMetadata.AUTHOR));
		queryFilter.putIfNotEmpty(FileMetadata.BLOCKCHAIN_TX, filterParams.get(FileMetadata.BLOCKCHAIN_TX));
		queryFilter.putIfNotEmpty(FileMetadata.COMMENT_UUID, filterParams.get(FileMetadata.COMMENT_UUID));
		queryFilter.putIfNotEmpty(FileMetadata.CONTRACT_UUID, filterParams.get(FileMetadata.CONTRACT_UUID));
		queryFilter.putIfNotEmpty(FileMetadata.DESCRIPTION, filterParams.get(FileMetadata.DESCRIPTION));
		queryFilter.putIfNotEmpty(FileMetadata.FILE_EXTENSION, filterParams.get(FileMetadata.FILE_EXTENSION));
		queryFilter.putIfNotEmpty(FileMetadata.HASH, filterParams.get(FileMetadata.HASH));
		queryFilter.putIfNotEmpty(FileMetadata.INFORMATION_MODEL_DOCUMENT_UUID,
				filterParams.get(FileMetadata.INFORMATION_MODEL_DOCUMENT_UUID));
		queryFilter.putIfNotEmpty(FileMetadata.MESSAGE_UUID, filterParams.get(FileMetadata.MESSAGE_UUID));
		queryFilter.putIfNotEmpty(FileMetadata.NEGOTIATION_SIGNATURE_CONTAINER_UUID,
				filterParams.get(FileMetadata.NEGOTIATION_SIGNATURE_CONTAINER_UUID));
		queryFilter.putIfNotEmpty(FileMetadata.NEGOTIATION_UUID, filterParams.get(FileMetadata.NEGOTIATION_UUID));
		queryFilter.putIfNotEmpty(FileMetadata.POST_UUID, filterParams.get(FileMetadata.POST_UUID));
		queryFilter.putIfNotEmpty(FileMetadata.REVIEW_UUID, filterParams.get(FileMetadata.REVIEW_UUID));
		queryFilter.putIfNotEmpty(FileMetadata.WORKGROUP_UUID, filterParams.get(FileMetadata.WORKGROUP_UUID));
		queryFilter.putIfNotEmpty(FileMetadata.VARIABLE_NAME, filterParams.get(FileMetadata.VARIABLE_NAME));
		queryFilter.putIfNotEmpty(FileMetadata.DATA_FILE, filterParams.get(FileMetadata.DATA_FILE));
		queryFilter.putIfNotEmpty(FileMetadata.DATA_FILE_SOURCE, filterParams.get(FileMetadata.DATA_FILE_SOURCE));

		return manager.getFilesMetadata(queryFilter);
	}

	@Override
	public String convertToDataFile(ExRequestEnvelope exRequestEnvelope, String fileUuid) {
		ContextHelper.initContext(exRequestEnvelope);
		return manager.convertToDataFile(fileUuid);
	}

}
