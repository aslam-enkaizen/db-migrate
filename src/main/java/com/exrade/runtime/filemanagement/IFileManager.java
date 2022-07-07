package com.exrade.runtime.filemanagement;

import com.exrade.models.asset.Asset;
import com.exrade.models.contract.Contract;
import com.exrade.models.messaging.Agreement;
import com.exrade.models.messaging.Information;
import com.exrade.models.messaging.Offer;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.negotiation.NegotiationComment;
import com.exrade.models.review.Review;
import com.exrade.models.signatures.NegotiationSignatureContainer;
import com.exrade.models.workgroup.Post;
import com.exrade.models.workgroup.WorkGroup;
import com.exrade.models.workgroup.WorkGroupComment;
import com.exrade.platform.persistence.query.QueryFilters;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IFileManager {

	void setFileStorageController(IFileStorageController fileStorageController);

	byte[] getFileAsByteArray(String fileName);

	String storeFileFromByteArray(byte[] content, String fileExtension,
			Map<String, Object> metaData);

	String storeFile(File file, Map<String, Object> metaData)
			throws IOException;

	File getFile(String fileName) throws IOException;

	Map<String, Object> getMetadata(String fileName);

	List<Map<String, Object>> getFilesMetadata(QueryFilters iFilters);
	
	void updateMetadata(String fileName,
			Map<String, Object> metaData);

	void deleteFile(String fileName);

	String replaceFile(String fileName, byte[] content,
			String fileExtension, Map<String, Object> metaData);
	
	String copyFile(String fileName) throws IOException;
	
	boolean isExists(String fileName);

	void updateFileMetadata(Negotiation negotiation);

	void updateFileMetadata(NegotiationComment negotiationComment);

	void updateFileMetadata(Negotiation negotiation, Offer offer);

	void updateFileMetadata(WorkGroup workGroup);

	void updateFileMetadata(Review review);

	void updateFileMetadata(Post post);

	void updateFileMetadata(WorkGroupComment workgroupComment);
	
	void updateFileMetadata(Contract contract);

	void updateFileMetadata(Negotiation negotiation, Information informationMessage);

	void updateFileMetadata(Negotiation negotiation, Agreement agreement);
	
	void updateFileMetadata(Negotiation negotiation, Agreement agreement, NegotiationSignatureContainer negotiationSignatureContainer, String fileName);

	void updateFileMetadata(Asset asset);
	
	String convertToDataFile(String fileUuid);

}