package com.exrade.runtime.filemanagement;

import com.exrade.core.ExLogger;
import com.exrade.models.asset.Asset;
import com.exrade.models.common.Image;
import com.exrade.models.contract.Contract;
import com.exrade.models.informationmodel.IInformationModel;
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
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExAuthorizationException;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.platform.security.Security;
import com.exrade.runtime.blockchain.NotarizationSmartContract;
import com.exrade.runtime.filemanagement.converter.ExcelToJsonConverter;
import com.exrade.util.ContextHelper;
import com.exrade.util.ExCollections;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import org.apache.poi.EncryptedDocumentException;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class FileManager implements IFileManager {

	private static final Logger LOGGER = ExLogger.get();
	protected IFileStorageController fileStorageController = FileStorageProvider.getFileStorageController();
	private NotarizationSmartContract notarizationSmartContract = new NotarizationSmartContract();

	public void setFileStorageController(IFileStorageController fileStorageController) {
		this.fileStorageController = fileStorageController;
	}

	public byte[] getFileAsByteArray(String fileName) {
		Security.checkFileAccess(fileName);
		return fileStorageController.retrieveFileAsByte(fileName);
	}

	public String storeFileFromByteArray(byte[] content, String fileExtension, Map<String, Object> metaData) {
		Security.checkStorageQuota(content.length);
		return fileStorageController.storeFile(content, fileExtension, metaData);
	}

	public String storeFile(File file, Map<String, Object> metaData) throws IOException {
		Security.checkStorageQuota(file.length());
		return fileStorageController.storeFile(file, metaData);
	}

	public File getFile(String fileName) throws IOException {
		if (fileName == null)
			return null;
		File file = new File(fileName);
		Files.write(getFileAsByteArray(fileName), file);
		return file;
	}

	public String copyFile(String fileName) {
		if (Strings.isNullOrEmpty(fileName))
			return null;

		String copyFileUUID = null;
		// Files.write(getFileAsByteArray(fileName), file);
		Map<String, Object> metaData = getMetadata(fileName);
		if (metaData != null) {
			copyFileUUID = storeFileFromByteArray(getFileAsByteArray(fileName),
					(String) metaData.get(FileMetadata.FILE_EXTENSION), metaData);
		}

		return copyFileUUID;
	}

	public Image copyImage(Image iImage) {
		String fileUUIDcopy = copyFile(iImage.getFileUUID());
		Image imageCopy = new Image(fileUUIDcopy, iImage.getOrder());
		return imageCopy;
	}

	public Map<String, Object> getMetadata(String fileName) {
		Security.checkFileAccess(fileName);
		return fileStorageController.getFileMetadata(fileName);
	}

	public void updateMetadata(String fileName, Map<String, Object> metaData) {
		Security.checkFileAccess(fileName);
		fileStorageController.updateMetadata(fileName, metaData);
	}

	public void deleteFile(String fileName) {
		// if file is associated with negotiation or message then user not allowed to
		// delete
		if (canDeleteFile(fileName)) {
			fileStorageController.deleteFile(fileName);
			ExLogger.get().info("File deleted: {}", fileName);
		}
	}

	public String replaceFile(String fileName, byte[] content, String fileExtension, Map<String, Object> metaData) {
		deleteFile(fileName);
		return storeFileFromByteArray(content, fileExtension, metaData);
	}

	private boolean canDeleteFile(String fileName) {
		Map<String, Object> metadata = getMetadata(fileName);
		if (metadata != null) {
			if (metadata.containsKey(FileMetadata.NEGOTIATION_UUID) || metadata.containsKey(FileMetadata.MESSAGE_UUID)
					|| metadata.containsKey(FileMetadata.REVIEW_UUID) || metadata.containsKey(FileMetadata.COMMENT_UUID)
					|| metadata.containsKey(FileMetadata.WORKGROUP_UUID) || metadata.containsKey(FileMetadata.POST_UUID)
					|| metadata.containsKey(FileMetadata.INFORMATION_MODEL_DOCUMENT_UUID)
					|| metadata.containsKey(FileMetadata.CONTRACT_UUID)
					|| metadata.containsKey(FileMetadata.NEGOTIATION_SIGNATURE_CONTAINER_UUID)) {
				return false;
			}
//			if (metadata.containsKey(FileMetadata.NEGOTIATION_UUID)) {
//				NegotiationManager negotiationManager = new NegotiationManager();
//
//				if (!NegotiationMeta.FILES.isEditable(
//						negotiationManager.getNegotiation((String) metadata.get(FileMetadata.NEGOTIATION_UUID))))
//					return false;
//			} else if (metadata.containsKey(FileMetadata.MESSAGE_UUID)) {
//				return false;
//			}
		}
		return true;
	}

	@Override
	public List<Map<String, Object>> getFilesMetadata(QueryFilters iFilters) {
		// TODO enforce access control
		if(iFilters.isEmpty())
			throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);
		return fileStorageController.getFilesMetadata(iFilters);
	}

	@Override
	public boolean isExists(String fileName) {
		return fileStorageController.retrieveFileAsByte(fileName) != null;
	}

	@Override
	public void updateFileMetadata(Negotiation negotiation) {
		if (negotiation != null) {
			if (negotiation.getImages() != null) {
				for (Image image : negotiation.getImages()) {
					try {
						Map<String, Object> metaData = new HashMap<>();
						metaData.put(FileMetadata.NEGOTIATION_UUID, negotiation.getUuid());

						updateMetadata(image.getFileUUID(), metaData);
					} catch (Exception e) {
						LOGGER.warn("Unable to update file metadata. File: {}, Negotiation: {}, error: {}",
								image.getFileUUID(), negotiation.getUuid(), e.getStackTrace());
					}
				}
			}

			if (negotiation.getFiles() != null) {
				for (String file : negotiation.getFiles()) {
					try {
						Map<String, Object> metaData = new HashMap<>();
						metaData.put(FileMetadata.NEGOTIATION_UUID, negotiation.getUuid());

						updateMetadata(file, metaData);
					} catch (Exception e) {
						LOGGER.warn("Unable to update file metadata. File: {}, Negotiation: {}, error: {}", file,
								negotiation.getUuid(), e.getStackTrace());
					}
				}
			}

			if (negotiation.getInformationModelDocument() != null
					&& !Strings.isNullOrEmpty(negotiation.getInformationModelDocument().getTemplate())) {
				updateFileMetadataFromTemplate(negotiation.getUuid(), FileMetadata.INFORMATION_MODEL_DOCUMENT_UUID,
						negotiation.getInformationModelDocument().getUuid(),
						negotiation.getInformationModelDocument());
			}
		}
	}

	@Override
	public void updateFileMetadata(NegotiationComment negotiationComment) {
		if (negotiationComment != null && negotiationComment.getFiles() != null) {
			for (String file : negotiationComment.getFiles()) {
				try {
					Map<String, Object> metaData = new HashMap<>();
					metaData.put(FileMetadata.COMMENT_UUID, negotiationComment.getUuid());
					metaData.put(FileMetadata.NEGOTIATION_UUID, negotiationComment.getNegotiation().getUuid());

					updateMetadata(file, metaData);
				} catch (Exception e) {
					LOGGER.warn(
							"Unable to update file metadata. File: {}, NegotiationComment: {}, Negotiation: {}, error: {}",
							file, negotiationComment.getUuid(), negotiationComment.getNegotiation().getUuid(),
							e.getStackTrace());
				}
			}
		}
	}

	@Override
	public void updateFileMetadata(Negotiation negotiation, Information informationMessage) {
		if (informationMessage != null && informationMessage.getFiles() != null) {

			for (String file : informationMessage.getFiles()) {
				try {
					Map<String, Object> metaData = new HashMap<>();
					metaData.put(FileMetadata.MESSAGE_UUID, informationMessage.getUuid());
					metaData.put(FileMetadata.NEGOTIATION_UUID, negotiation.getUuid());

					updateMetadata(file, metaData);
				} catch (Exception e) {
					LOGGER.warn("Unable to update file metadata.  File: {}, NegotiationMessage: {}, error: {}", file,
							informationMessage.getUuid(), e.getStackTrace());
				}
			}
		}
	}

	@Override
	public void updateFileMetadata(Negotiation negotiation, Offer offer) {
		if (offer != null) {
			updateFileMetadataFromTemplate(negotiation.getUuid(), FileMetadata.MESSAGE_UUID, offer.getUuid(), offer);
		}
	}

	@Override
	public void updateFileMetadata(Negotiation negotiation, Agreement agreement) {
		if (agreement != null) {
			try {
				Map<String, Object> metaData = new HashMap<>();
				metaData.put(FileMetadata.MESSAGE_UUID, agreement.getUuid());
				metaData.put(FileMetadata.NEGOTIATION_UUID, negotiation.getUuid());

				updateMetadata(agreement.getAgreementDocumentID(), metaData);
				notarizationSmartContract.notarize(agreement.getAgreementDocumentID(), getMetadata(agreement.getAgreementDocumentID()), negotiation.getUuid(), ContextHelper.getMembershipUUID());
			} catch (Exception e) {
				LOGGER.warn("Unable to update file metadata.  File: {}, NegotiationMessage: {}, error: {}",
						agreement.getAgreementDocumentID(), agreement.getUuid(), e.getStackTrace());
			}
		}
	}

	@Override
	public void updateFileMetadata(Negotiation negotiation, Agreement agreement,
			NegotiationSignatureContainer negotiationSignatureContainer, String fileName) {
		if (agreement != null) {
			try {
				Map<String, Object> metaData = new HashMap<>();
				metaData.put(FileMetadata.MESSAGE_UUID, agreement.getUuid());
				metaData.put(FileMetadata.NEGOTIATION_UUID, negotiation.getUuid());
				metaData.put(FileMetadata.NEGOTIATION_SIGNATURE_CONTAINER_UUID,
						negotiationSignatureContainer.getUuid());

				updateMetadata(fileName, metaData);
				notarizationSmartContract.notarize(fileName, getMetadata(fileName), negotiation.getUuid(), ContextHelper.getMembershipUUID());
			} catch (Exception e) {
				LOGGER.warn("Unable to update file metadata.  File: {}, NegotiationSignatureContainer: {}, error: {}",
						fileName, negotiationSignatureContainer.getUuid(), e.getStackTrace());
			}
		}
	}

	@Override
	public void updateFileMetadata(Review review) {
		if (review != null && review.getFiles() != null) {
			for (String file : review.getFiles()) {
				try {
					Map<String, Object> metaData = new HashMap<>();
					metaData.put(FileMetadata.REVIEW_UUID, review.getUuid());
					metaData.put(FileMetadata.NEGOTIATION_UUID, review.getNegotiationUUID());
					metaData.put(FileMetadata.MESSAGE_UUID, review.getOfferUUID());

					updateMetadata(file, metaData);
				} catch (Exception e) {
					LOGGER.warn(
							"Unable to update file metadata.  File: {}, Review: {}, Negotiation: {}, NegotiationMessage: {}, error: {}",
							file, review.getUuid(), review.getNegotiationUUID(), review.getOfferUUID(),
							e.getStackTrace());
				}
			}
		}
	}

	@Override
	public void updateFileMetadata(WorkGroup workGroup) {
		if (workGroup != null && workGroup.getLogo() != null) {
			List<String> files = Arrays.asList(workGroup.getLogo());
			for (String file : files) {
				try {
					Map<String, Object> metaData = new HashMap<>();
					metaData.put(FileMetadata.WORKGROUP_UUID, workGroup.getUuid());

					updateMetadata(file, metaData);
				} catch (Exception e) {
					LOGGER.warn("Unable to update file metadata.  File: {}, Workgroup: {}, error: {}", file,
							workGroup.getUuid(), e.getStackTrace());
				}
			}
		}
	}

	@Override
	public void updateFileMetadata(Post post) {
		if (post != null && post.getFiles() != null) {
			for (String file : post.getFiles()) {
				try {
					Map<String, Object> metaData = new HashMap<>();
					metaData.put(FileMetadata.POST_UUID, post.getUuid());
					metaData.put(FileMetadata.WORKGROUP_UUID, post.getWorkGroup().getUuid());

					updateMetadata(file, metaData);
				} catch (Exception e) {
					LOGGER.warn("Unable to update file metadata.  File: {}, Post: {}, Workgroup: {}, error: {}", file,
							post.getUuid(), post.getWorkGroup().getUuid(), e.getStackTrace());
				}
			}
		}
	}

	@Override
	public void updateFileMetadata(WorkGroupComment workgroupComment) {
		if (workgroupComment != null && workgroupComment.getFiles() != null) {
			for (String file : workgroupComment.getFiles()) {
				try {
					Map<String, Object> metaData = new HashMap<>();
					metaData.put(FileMetadata.COMMENT_UUID, workgroupComment.getUuid());
					metaData.put(FileMetadata.POST_UUID, workgroupComment.getPost().getUuid());
					metaData.put(FileMetadata.WORKGROUP_UUID, workgroupComment.getPost().getWorkGroup().getUuid());

					updateMetadata(file, metaData);
				} catch (Exception e) {
					LOGGER.warn(
							"Unable to update file metadata.  File: {}, Comment: {}, Post: {}, Workgroup: {}, error: {}",
							file, workgroupComment.getUuid(), workgroupComment.getPost().getUuid(),
							workgroupComment.getPost().getWorkGroup().getUuid(), e.getStackTrace());
				}
			}
		}
	}

	private void updateFileMetadataFromTemplate(String negotiationUuid, String metaKey, String metaValue,
			IInformationModel informationModel) {
		List<String> files = InformationModelUtil.getFileFieldValues(informationModel);
		
		if (ExCollections.isNotEmpty(files)) {
			for (String file : files) {
				try {
					Map<String, Object> metaData = new HashMap<>();
					metaData.put(FileMetadata.NEGOTIATION_UUID, negotiationUuid);
					metaData.put(metaKey, metaValue);

					updateMetadata(file, metaData);
				} catch (Exception e) {
					LOGGER.warn("Unable to update file metadata.  File: {}, {}: {}, Negotiation: {}, error: {}", file,
							metaKey, metaValue, negotiationUuid, e.getStackTrace());
				}
			}
			
			Map<String, String> dataFiles = InformationModelUtil.getDataFileFieldsFromTemplate(informationModel.getTemplate());
			if(dataFiles != null) {
				for(Entry<String, String> file: dataFiles.entrySet()) {
					try {
						Map<String, Object> metaData = new HashMap<>();
						metaData.put(FileMetadata.NEGOTIATION_UUID, negotiationUuid);
						metaData.put(metaKey, metaValue);
						metaData.put(FileMetadata.VARIABLE_NAME, file.getValue());
						
						updateMetadata(file.getKey(), metaData);
					} catch (Exception e) {
						LOGGER.warn("Unable to update file metadata.  File: {}, {}: {}, Negotiation: {}, error: {}", file,
								metaKey, metaValue, negotiationUuid, e.getStackTrace());
					}
				}
			}
		}
	}

	@Override
	public void updateFileMetadata(Contract contract) {
		if (contract != null) {
			List<String> files = new ArrayList<>();
			if (ExCollections.isNotEmpty(contract.getContractFiles())) {
				files.addAll(contract.getContractFiles());
			}
			if (ExCollections.isNotEmpty(contract.getAttachments())) {
				files.addAll(contract.getAttachments());
			}

			for (String file : files) {
				try {
					Map<String, Object> metaData = new HashMap<>();
					metaData.put(FileMetadata.CONTRACT_UUID, contract.getUuid());

					updateMetadata(file, metaData);
				} catch (Exception e) {
					LOGGER.warn("Unable to update file metadata. File: {}, Contract: {}, error: {}", file,
							contract.getUuid(), e.getStackTrace());
				}
			}

		}
	}

	@Override
	public void updateFileMetadata(Asset asset) {
		if (asset != null) {
			if (asset.getImages() != null) {
				for (Image image : asset.getImages()) {
					try {
						Map<String, Object> metaData = new HashMap<>();
						metaData.put(FileMetadata.ASSET_UUID, asset.getUuid());

						updateMetadata(image.getFileUUID(), metaData);
					} catch (Exception e) {
						LOGGER.warn("Unable to update file metadata. File: {}, Asset: {}, error: {}",
								image.getFileUUID(), asset.getUuid(), e.getStackTrace());
					}
				}
			}

			if (asset.getFiles() != null) {
				for (String file : asset.getFiles()) {
					try {
						Map<String, Object> metaData = new HashMap<>();
						metaData.put(FileMetadata.ASSET_UUID, asset.getUuid());

						updateMetadata(file, metaData);
					} catch (Exception e) {
						LOGGER.warn("Unable to update file metadata. File: {}, Asset: {}, error: {}", file,
								asset.getUuid(), e.getStackTrace());
					}
				}
			}

			if (asset.getVideos() != null) {
				for (String video : asset.getVideos()) {
					try {
						Map<String, Object> metaData = new HashMap<>();
						metaData.put(FileMetadata.ASSET_UUID, asset.getUuid());

						updateMetadata(video, metaData);
					} catch (Exception e) {
						LOGGER.warn("Unable to update file metadata. File: {}, Asset: {}, error: {}", video,
								asset.getUuid(), e.getStackTrace());
					}
				}
			}
		}
	}

	@Override
	public String convertToDataFile(String fileUuid) {
		String jsonFileUuid = "";
		Map<String, Object> fileMetadata = this.getMetadata(fileUuid);
		if (fileMetadata.get(FileMetadata.DATA_FILE) == null) {
			byte[] byteArray = this.getFileAsByteArray(fileUuid);
			try {
				File file = File.createTempFile(fileMetadata.get(FileMetadata.ORIGINAL_NAME).toString(), String.format(".%s", fileMetadata.get(FileMetadata.FILE_EXTENSION).toString()));
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				fileOutputStream.write(byteArray);
				fileOutputStream.flush();
				fileOutputStream.close();
				
				String jsonString = ExcelToJsonConverter.convert(file);
				
				File jsonFile = File.createTempFile(fileUuid.replace(fileMetadata.get(FileMetadata.FILE_EXTENSION).toString(), ""), ".json");
				FileWriter writer = new FileWriter(jsonFile);
				writer.write(jsonString);
				writer.flush();
				
				Map<String, Object> jsonFileMetadata = new HashMap<String, Object>();
				
				jsonFileMetadata.put(FileMetadata.DATA_FILE_SOURCE, fileUuid);
				jsonFileMetadata.put(FileMetadata.AUTHOR, fileMetadata.get(FileMetadata.AUTHOR));
				if (fileMetadata.containsKey(fileMetadata.get(FileMetadata.NEGOTIATION_UUID))) {
					jsonFileMetadata.put(FileMetadata.NEGOTIATION_UUID,
							fileMetadata.get(FileMetadata.NEGOTIATION_UUID));
				}
				if (fileMetadata.containsKey(fileMetadata.get(FileMetadata.MESSAGE_UUID))) {
					jsonFileMetadata.put(FileMetadata.MESSAGE_UUID, fileMetadata.get(FileMetadata.MESSAGE_UUID));
				}
				if (fileMetadata.containsKey(fileMetadata.get(FileMetadata.INFORMATION_MODEL_DOCUMENT_UUID))) {
					jsonFileMetadata.put(FileMetadata.INFORMATION_MODEL_DOCUMENT_UUID,
							fileMetadata.get(FileMetadata.INFORMATION_MODEL_DOCUMENT_UUID));
				}
				if (fileMetadata.containsKey(fileMetadata.get(FileMetadata.WORKGROUP_UUID))) {
					jsonFileMetadata.put(FileMetadata.WORKGROUP_UUID, fileMetadata.get(FileMetadata.WORKGROUP_UUID));
				}

				if (fileMetadata.containsKey(fileMetadata.get(FileMetadata.POST_UUID))) {
					jsonFileMetadata.put(FileMetadata.POST_UUID, fileMetadata.get(FileMetadata.POST_UUID));
				}
				if (fileMetadata.containsKey(fileMetadata.get(FileMetadata.REVIEW_UUID))) {
					jsonFileMetadata.put(FileMetadata.REVIEW_UUID, fileMetadata.get(FileMetadata.REVIEW_UUID));
				}

				if (fileMetadata.containsKey(fileMetadata.get(FileMetadata.CONTRACT_UUID))) {
					jsonFileMetadata.put(FileMetadata.CONTRACT_UUID, fileMetadata.get(FileMetadata.CONTRACT_UUID));
				}
				if (fileMetadata.containsKey(fileMetadata.get(FileMetadata.NEGOTIATION_SIGNATURE_CONTAINER_UUID))) {
					jsonFileMetadata.put(FileMetadata.NEGOTIATION_SIGNATURE_CONTAINER_UUID,
							fileMetadata.get(FileMetadata.NEGOTIATION_SIGNATURE_CONTAINER_UUID));
				}
				if (fileMetadata.containsKey(fileMetadata.get(FileMetadata.ASSET_UUID))) {
					jsonFileMetadata.put(FileMetadata.ASSET_UUID, fileMetadata.get(FileMetadata.ASSET_UUID));
				}
				
				jsonFileUuid = this.storeFile(jsonFile, jsonFileMetadata);
				
				writer.close();
				
				fileMetadata.put(FileMetadata.DATA_FILE, jsonFileUuid);
				this.updateMetadata(fileUuid, fileMetadata);
			} catch (EncryptedDocumentException e) {
				LOGGER.error("Error converting to data file: " + fileUuid, e);
			} catch (IOException e) {
				LOGGER.error("Error converting to data file: " + fileUuid, e);
			}
		}
		return jsonFileUuid;
	}
}
