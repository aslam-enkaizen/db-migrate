package com.exrade.runtime.filemanagement;

import com.exrade.core.ExLogger;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExException;
import com.exrade.platform.exception.ExPersistentException;
import com.exrade.platform.persistence.ConnectionManager;
import com.exrade.platform.persistence.IConnectionManager;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.timer.TimeProvider;
import com.exrade.util.ObjectsUtil;
import com.google.common.base.Joiner;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.record.impl.ORecordBytes;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class DBFileStorageController implements IFileStorageController {
	private static Logger logger = ExLogger.get();

	public static final String FILE_CLASS = "Files";
	public static final String FILE_UUID = "fileUUID";
	private static final String FILE_CONTENT = "content";
	
	private static final String FIELDS = Joiner.on(",").join(FileMetadata.FILE_UUID,FileMetadata.FILE_SIZE,FileMetadata.FILE_EXTENSION,FileMetadata.CREATION_DATE,FileMetadata.LAST_MODIFIED,
			FileMetadata.ORIGINAL_NAME,FileMetadata.AUTHOR,FileMetadata.CONTENT_TYPE,FileMetadata.MIME_TYPE,FileMetadata.HASH,
			FileMetadata.NEGOTIATION_UUID,FileMetadata.MESSAGE_UUID,FileMetadata.COMMENT_UUID,FileMetadata.CONTRACT_UUID,
			FileMetadata.INFORMATION_MODEL_DOCUMENT_UUID,FileMetadata.NEGOTIATION_SIGNATURE_CONTAINER_UUID,FileMetadata.POST_UUID,
			FileMetadata.REVIEW_UUID,FileMetadata.WORKGROUP_UUID,FileMetadata.BLOCKCHAIN_TX, FileMetadata.ASSET_UUID, FileMetadata.DESCRIPTION,
			FileMetadata.VARIABLE_NAME, FileMetadata.DATA_FILE, FileMetadata.DATA_FILE_SOURCE).toString();

	@Override
	public String storeFile(byte[] content, String fileExtension, Map<String, Object> metaData) {
		Map<String, Object> metaDataWithFileExtension = new HashMap<>();
		metaDataWithFileExtension.put(FileMetadata.FILE_EXTENSION, fileExtension);

		if(metaData != null) {
			metaDataWithFileExtension.putAll(metaData);
		}

		String fileName = ObjectsUtil.generateUniqueID();
		if(fileExtension != null && !fileExtension.isEmpty()){
			fileName += "." + fileExtension;
		}

		storeFileWithName(fileName, content, metaDataWithFileExtension);

		return fileName;
	}

	@Override
	public String storeFile(File file, Map<String, Object> metaData) {
		Map<String, Object> metaDataWithFileName = new HashMap<>();
		metaDataWithFileName.put(FileMetadata.ORIGINAL_NAME, file.getName());

		if(metaData != null)
			metaDataWithFileName.putAll(metaData);

		String filename = null;

		try {
			filename = storeFile(Files.toByteArray(file), Files.getFileExtension(file.getName()), metaDataWithFileName);
		} catch (IOException e) {
			throw new ExException(e);
		}

		return filename;
	}

	@Override
	public String storeFile(InputStream stream,String fileName,Map<String, Object> metaData) {
		Map<String, Object> metaDataWithFileName = new HashMap<>();
		metaDataWithFileName.put(FileMetadata.ORIGINAL_NAME, fileName);

		if(metaData != null)
			metaDataWithFileName.putAll(metaData);

		String fileUUID = null;

		try {
			fileUUID = storeFile(ByteStreams.toByteArray(stream),Files.getFileExtension(fileName), metaDataWithFileName);
		} catch (IOException e) {
			throw new ExException(e);
		}

		return fileUUID;
	}

	@Override
	public String storeFile(URL url, String fileName,
			Map<String, Object> metaData) {
		try {
			return storeFile(url.openStream(), fileName, metaData);
		} catch (IOException e) {
			throw new ExException(e);
		}
	}

	@Override
	public byte[] retrieveFileAsByte(String fileUUID) {
		IConnectionManager persistenceManager = ConnectionManager.getInstance();
		OObjectDatabaseTx db = persistenceManager.getObjectConnection();

		try {
			List<ODocument> result = db.getUnderlying().query(
					new OSQLSynchQuery<ODocument>("select * from " + FILE_CLASS + " where " + FILE_UUID + " = '" + fileUUID + "'"));
			if (result != null && result.size() == 1){
				if (result.get(0).field(FILE_CONTENT) instanceof byte[]){
					// handle old way of serialized file storage
					return result.get(0).field(FILE_CONTENT);
				}
				else {
					ORecordBytes record = result.get(0).field(FILE_CONTENT);
					return record.toStream();
				}
			}

			return null;
		} catch (Exception ex) {
			throw new ExPersistentException(ErrorKeys.FILE_CANNOT_LOAD, ex);
		} finally {
			db.close();
		}
	}

	@Override
	public File retrieveFile(String fileUUID) {
		byte[] fileContent = retrieveFileAsByte(fileUUID);
		if (fileContent != null) {
			File file = new File(fileUUID);
			try {
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(retrieveFileAsByte(fileUUID));
				fos.close();
			} catch (Exception ex) {
				throw new ExException(ErrorKeys.FILE_CANNOT_LOAD, ex);
			}
			return file;
		}
		return null;
	}

	@Override
	public Map<String, Object> getFileMetadata(String fileUUID) {
		IConnectionManager persistenceManager = ConnectionManager.getInstance();
		OObjectDatabaseTx db = persistenceManager.getObjectConnection();

		try {
			
			List<ODocument> result = db.getUnderlying().query(
					new OSQLSynchQuery<ODocument>(String.format("select %s from %s where %s = '%s'",
							FIELDS,FILE_CLASS,FILE_UUID,fileUUID)));

			if (result != null && result.size() == 1){
				ODocument document = result.get(0);
				Map<String, Object> metaData = new HashMap<String, Object>();
				metaData.put("FILE_UUID", fileUUID);
				for(String fieldName : document.fieldNames()){
					if(!fieldName.equals(FILE_UUID) && !fieldName.equals(FILE_CONTENT))
						metaData.put(fieldName, document.field(fieldName));
				}
				return metaData;
			}

			return null;
		} catch (Exception ex) {
			throw new ExPersistentException(ErrorKeys.FILEMETA_CANNOT_LOAD, ex);
		} finally {
			db.close();
		}
	}

	public Integer getMembershipSpaceUsage(Negotiator iActor) {
		IConnectionManager persistenceManager = ConnectionManager.getInstance();
		OObjectDatabaseTx db = persistenceManager.getObjectConnection();

		try {
			String selectField = "sum("+FileMetadata.FILE_SIZE+") as bytes";

			List<ODocument> result = db.getUnderlying().query(
					new OSQLSynchQuery<ODocument>(String.format("select %s from %s where %s = '%s'",
							selectField,FILE_CLASS,FileMetadata.AUTHOR,iActor.getIdentifier())));

			if (result != null && result.size() == 1){
				ODocument document = result.get(0);
				return (Integer) document.field("bytes");
			}
			return 0;
		} catch (Exception ex) {
			throw new ExPersistentException(ErrorKeys.FILEMETA_CANNOT_LOAD, ex);
		} finally {
			db.close();
		}
	}



	@Override
	public void updateMetadata(String fileUUID, Map<String, Object> metaData){
		if(metaData != null){
			IConnectionManager persistenceManager = ConnectionManager.getInstance();
			OObjectDatabaseTx db = persistenceManager.getObjectConnection();

			try {
				List<ODocument> result = db.getUnderlying().query(
						new OSQLSynchQuery<ODocument>("select * from " + FILE_CLASS + " where " + FILE_UUID + " = '" + fileUUID + "'"));

				if (result != null && result.size() == 1){
					ODocument document = result.get(0);
					Map<String, Object> metaDataWithTime = new HashMap<String, Object>();
					metaDataWithTime.put(FileMetadata.LAST_MODIFIED, TimeProvider.now());
					metaDataWithTime.putAll(metaData);

					// append metadata about message uuid if exists
					if(metaDataWithTime.containsKey(FileMetadata.MESSAGE_UUID) && document.field(FileMetadata.MESSAGE_UUID) != null){
						Joiner joiner = Joiner.on(",").skipNulls();
						String existingMessageUUIDs = document.field(FileMetadata.MESSAGE_UUID);
						String newMessageUUIDs = (String) metaDataWithTime.get(FileMetadata.MESSAGE_UUID);
						String concatenatedMessageUUIDs = joiner.join(existingMessageUUIDs, newMessageUUIDs);
					    HashSet<String> concatenatedMessageUUIDsSet = new HashSet<String>(Arrays.asList(concatenatedMessageUUIDs.split(",")));

						metaDataWithTime.put(FileMetadata.MESSAGE_UUID, String.join(",", concatenatedMessageUUIDsSet));
					}
					document.fields(metaDataWithTime);
					document.save();
				}

			} catch (Exception ex) {
				throw new ExPersistentException(ErrorKeys.FILEMETA_CANNOT_UPDATE, ex);
			} finally {
				db.close();
			}
		}
	}

	private String storeFileWithName(String fileName, byte[] content, Map<String, Object> metaData) {
		IConnectionManager persistenceManager = ConnectionManager.getInstance();
		OObjectDatabaseTx db = persistenceManager.getObjectConnection();

		try {
			Map<String, Object> metaDataWithTime = new HashMap<String, Object>();
			metaDataWithTime.put(FileMetadata.CREATION_DATE, TimeProvider.now());
			metaDataWithTime.put(FileMetadata.LAST_MODIFIED, TimeProvider.now());
			metaDataWithTime.put(FileMetadata.FILE_SIZE, content.length);
			metaDataWithTime.put(FileMetadata.HASH, Hashing.sha256().hashBytes(content).toString());

			if (metaData != null)
				metaDataWithTime.putAll(metaData);

			ODocument doc = db.getUnderlying().newInstance(FILE_CLASS);
			//ODocument doc = new ODocument(FILE_CLASS);
			doc.field(FILE_UUID, fileName);

			ORecordBytes record = new ORecordBytes(content);
			doc.field(FILE_CONTENT, record);

			//doc.field(FILE_CONTENT, content, OType.BINARY);

			//doc.field(FILE_METADATA, metaDataWithTime, OType.LINKMAP);
			doc.fields(metaDataWithTime);

			doc.save();
		} catch (Exception ex) {
			throw new ExPersistentException(ErrorKeys.FILE_CANNOT_SAVE, ex);
		} finally {
			db.close();
		}
		return fileName;
	}

	@Override
	public void deleteFile(String fileUUID) {
		IConnectionManager persistenceManager = ConnectionManager.getInstance();
		OObjectDatabaseTx db = persistenceManager.getObjectConnection();

		try {
			List<ODocument> result = db.getUnderlying().query(
					new OSQLSynchQuery<ODocument>("select * from " + FILE_CLASS + " where " + FILE_UUID + " = '" + fileUUID + "'"));
			if (result != null){
				for(ODocument file : result)
					file.delete();
			}
		} catch (Exception ex) {
			throw new ExPersistentException(ErrorKeys.FILE_CANNOT_LOAD, ex);
		} finally {
			db.close();
		}
	}

	@Override
	public List<Map<String, Object>> getFilesMetadata(QueryFilters iFilters) {
		if(iFilters == null || iFilters.size() < 1)
			return null;

		List<Map<String, Object>> filesMetadata = new ArrayList<Map<String,Object>>();
		IConnectionManager persistenceManager = ConnectionManager.getInstance();
		OObjectDatabaseTx db = persistenceManager.getObjectConnection();

		try {
			String query = new FileQuery().buildQuery(iFilters);
			logger.debug(query);
			List<ODocument> result = db.getUnderlying().query(
					new OSQLSynchQuery<ODocument>(query));

			if (result != null && result.size() > 0){
				for(ODocument document : result){
					Map<String, Object> metaData = new HashMap<String, Object>();
					for(String fieldName : document.fieldNames()){
						if(!fieldName.equals(FILE_CONTENT))
							metaData.put(fieldName, document.field(fieldName));
					}

					filesMetadata.add(metaData);
				}
			}

			return filesMetadata;
		} catch (Exception ex) {
			throw new ExPersistentException(ErrorKeys.FILEMETA_CANNOT_LOAD, ex);
		} finally {
			db.close();
		}
	}

	class FileQuery extends OrientSqlBuilder {

		@Override
		protected String buildQuery(QueryFilters iFilters) {

			String nquery = String.format("select %s from %s where 1 = 1 ",
					FIELDS, FILE_CLASS);

			if (iFilters.isNotNull(FileMetadata.WORKGROUP_UUID)){
				nquery += andEq(FileMetadata.WORKGROUP_UUID, iFilters.get(FileMetadata.WORKGROUP_UUID));
			}

			if (iFilters.isNotNull(FileMetadata.POST_UUID)){
				nquery += andEq(FileMetadata.POST_UUID, iFilters.get(FileMetadata.POST_UUID));
			}

			if (iFilters.isNotNull(FileMetadata.COMMENT_UUID)){
				nquery += andEq(FileMetadata.COMMENT_UUID, iFilters.get(FileMetadata.COMMENT_UUID));
			}

			if (iFilters.isNotNull(FileMetadata.ASSET_UUID)){
				nquery += andEq(FileMetadata.ASSET_UUID, iFilters.get(FileMetadata.ASSET_UUID));
			}

			if (iFilters.isNotNull(FileMetadata.AUTHOR)){
				nquery += andEq(FileMetadata.AUTHOR, iFilters.get(FileMetadata.AUTHOR));
			}

			if (iFilters.isNotNull(FileMetadata.BLOCKCHAIN_TX)){
				nquery += andEq(FileMetadata.BLOCKCHAIN_TX, iFilters.get(FileMetadata.BLOCKCHAIN_TX));
			}

			if (iFilters.isNotNull(FileMetadata.DESCRIPTION)){
				nquery += andEq(FileMetadata.DESCRIPTION, iFilters.get(FileMetadata.DESCRIPTION));
			}

			if (iFilters.isNotNull(FileMetadata.INFORMATION_MODEL_DOCUMENT_UUID)){
				nquery += andEq(FileMetadata.INFORMATION_MODEL_DOCUMENT_UUID, iFilters.get(FileMetadata.INFORMATION_MODEL_DOCUMENT_UUID));
			}

			if (iFilters.isNotNull(FileMetadata.MESSAGE_UUID)){
				nquery += andEq(FileMetadata.MESSAGE_UUID, iFilters.get(FileMetadata.MESSAGE_UUID));
			}

			if (iFilters.isNotNull(FileMetadata.NEGOTIATION_SIGNATURE_CONTAINER_UUID)){
				nquery += andEq(FileMetadata.NEGOTIATION_SIGNATURE_CONTAINER_UUID, iFilters.get(FileMetadata.NEGOTIATION_SIGNATURE_CONTAINER_UUID));
			}

			if (iFilters.isNotNull(FileMetadata.NEGOTIATION_UUID)){
				nquery += andEq(FileMetadata.NEGOTIATION_UUID, iFilters.get(FileMetadata.NEGOTIATION_UUID));
			}

			if (iFilters.isNotNull(FileMetadata.REVIEW_UUID)){
				nquery += andEq(FileMetadata.REVIEW_UUID, iFilters.get(FileMetadata.REVIEW_UUID));
			}
			
			if (iFilters.isNotNull(FileMetadata.CONTRACT_UUID)){
				nquery += andEq(FileMetadata.CONTRACT_UUID, iFilters.get(FileMetadata.CONTRACT_UUID));
			}
			
			if (iFilters.isNotNull(FileMetadata.VARIABLE_NAME)){
				nquery += andEq(FileMetadata.VARIABLE_NAME, iFilters.get(FileMetadata.VARIABLE_NAME));
			}
			
			if (iFilters.isNotNull(FileMetadata.DATA_FILE)){
				nquery += andEq(FileMetadata.DATA_FILE, iFilters.get(FileMetadata.DATA_FILE));
			}
			
			if (iFilters.isNotNull(FileMetadata.DATA_FILE_SOURCE)){
				nquery += andEq(FileMetadata.DATA_FILE_SOURCE, iFilters.get(FileMetadata.DATA_FILE_SOURCE));
			}

			return nquery;
		}
	}
}

