package com.exrade.util;

import com.exrade.core.ExLogger;
import com.exrade.models.contract.*;
import com.exrade.models.imports.Import;
import com.exrade.runtime.contract.IContractManager;
import com.exrade.runtime.filemanagement.FileMetadata;
import com.exrade.runtime.filemanagement.IFileStorageController;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.io.Files;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.*;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.exrade.runtime.rest.RestParameters.ContractFields.*;

/**
 * @author Rhidoy
 * @created 15/04/2022
 * @package com.exrade.util
 */
public class ExportImportUtil {
    private static final Logger logger = ExLogger.get();
    private static final String TEMP_FILE_PATH = System.getProperty("java.io.tmpdir");

    public static void doContractImport(Import importEvent, IContractManager contractManager, IFileStorageController fileManager) throws Exception {
        Map<String, Object> metaData = fileManager.getFileMetadata(importEvent.getImportFile());
        byte[] fileBye = fileManager.retrieveFileAsByte(importEvent.getImportFile());
        //creating a temp file
        File uploadedFile = new File(new File(TEMP_FILE_PATH), (String) metaData.get(FileMetadata.ORIGINAL_NAME));
        writeByte(fileBye, uploadedFile);
        if (!uploadedFile.exists() || uploadedFile.isDirectory())
            throw new Exception("File doesn't exist");

        //now checking file is csv or xls
        if (uploadedFile.getName().endsWith(".csv")) {
            CsvSchema csv = CsvSchema
                    .emptySchema()
                    .withHeader()
                    //todo update separator for production
//                    .withColumnSeparator(importEvent.getColumnSeparator());
                    .withColumnSeparator(';');

            CsvMapper csvMapper = new CsvMapper();
            MappingIterator<Map<String, ?>> mappingIterator = csvMapper
                    .reader()
                    .forType(Map.class)
                    .with(csv)
                    .readValues(uploadedFile);
            List<Map<String, ?>> list = mappingIterator.readAll();
            Map<String, String> property = importEvent.getPropertyMap();

            //getting and extracting zip file
            if (!StringUtils.isBlank(importEvent.getAttachedFile())) {
                metaData = fileManager.getFileMetadata(importEvent.getAttachedFile());
                fileBye = fileManager.retrieveFileAsByte(importEvent.getAttachedFile());
                //creating a temp file
                File file = new File(new File(TEMP_FILE_PATH), (String) metaData.get(FileMetadata.ORIGINAL_NAME));
                writeByte(fileBye, file);
                unzip(file);
            }

            //get all possible rows now. need to loop through it
            Map<String, String> results = new HashMap<>();
            for (Map<String, ?> map : list) {
                String title = "";
                try {
                    //now loop all the possible key

                    if (map.get(property.get(TITLE)) != null)
                        title = (String) map.get(property.get(TITLE));
                    Contract contract = new Contract();
                    contract.setTitle((String) map.get(property.get(TITLE)));
                    if (map.get(property.get(DESCRIPTION)) != null)
                        contract.setDescription((String) map.get(property.get(DESCRIPTION)));
                    if (map.get(property.get(EFFECTIVE_DATE)) != null)
                        contract.setEffectiveDate(DateUtil.parseDate((String) map.get(property.get(EFFECTIVE_DATE))));
                    if (map.get(property.get(EXPIRY_DATE)) != null)
                        contract.setExpiryDate(DateUtil.parseDate((String) map.get(property.get(EXPIRY_DATE))));
                    if (map.get(property.get(CURRENCY_CODE)) != null)
                        contract.setCurrencyCode((String) map.get(property.get(CURRENCY_CODE)));
                    if (map.get(property.get(VALUE)) != null)
                        contract.setValue(new BigDecimal((String) map.get(property.get(VALUE))));
                    if (map.get(property.get(STATUS)) != null)
                        try {
                            contract.setStatus(ContractStatus.valueOf((String) map.get(property.get(STATUS))));
                        } catch (IllegalArgumentException ignored) {
                        }
                    if (map.get(property.get(GOVERNING_LAW)) != null)
                        contract.setGoverningLaw((String) map.get(property.get(GOVERNING_LAW)));
                    ContractType contractType = ContractType.REGULAR;
                    if (map.get(property.get(CONTRACT_TYPE)) != null)
                        try {
                            contractType = ContractType.valueOf((String) map.get(property.get(CONTRACT_TYPE)));
                        } catch (IllegalArgumentException ignored) {
                        }
                    contract.setContractType(contractType);

                    if (map.get(property.get(CONTRACT_FILES)) != null)
                        contract.setContractFiles(
                                uploadFileFromTempFile((String) map.get(property.get(CONTRACT_FILES)),
                                        fileManager, importEvent)
                        );
                    if (map.get(property.get(ATTACHMENTS)) != null)
                        contract.setAttachments(
                                uploadFileFromTempFile(
                                        (String) map.get(property.get(ATTACHMENTS)),
                                        fileManager, importEvent)
                        );

                    //setting user info
                    contract.setCreator(importEvent.getRequestor());
                    contract.setOwnerProfile(importEvent.getProfile());

                    //adding contracting party
                    ContractingParty ownerParty = new ContractingParty();
                    ownerParty.setPartyType(ContractingPartyType.OWNER);
                    ownerParty.setProfile(contract.getOwnerProfile());
                    ContractUserMember ownerPartyMember = new ContractUserMember();
                    ownerPartyMember.setNegotiator(importEvent.getRequestor());
                    ownerParty.setMembers(Collections.singletonList(ownerPartyMember));

                    //adding owner party data from csv
                    if (map.get(property.get(CATEGORY)) != null)
                        ownerParty.setCategory((String) map.get(property.get(CATEGORY)));
                    if (map.get(property.get(TAGS)) != null)
                        ownerParty.setTags(Arrays.stream(((String) map.get(property.get(TAGS))).split(",")).collect(Collectors.toSet()));
                    if (map.get(property.get(ARCHIVED)) != null) {
                        String value = (String) map.get(property.get(ARCHIVED));
                        if (value.toLowerCase().startsWith("true"))
                            ownerParty.setArchived(true);
                        else if (value.toLowerCase().startsWith("false"))
                            ownerParty.setArchived(false);
                    }
                    if (map.get(property.get(RISK)) != null)
                        try {
                            ownerParty.setRisk(ContractRisk.valueOf((String) map.get(property.get(RISK))));
                        } catch (IllegalArgumentException ignored) {
                        }
                    if (map.get(property.get(REFERENCE_ID)) != null)
                        ownerParty.setReferenceId((String) map.get(property.get(REFERENCE_ID)));
                    if (map.get(property.get(NOTE)) != null)
                        ownerParty.setNote((String) map.get(property.get(NOTE)));
                    contract.setContractingParties(Collections.singletonList(ownerParty));

                    //now store the contract
                    contractManager.createContract(contract);

                    results.put(title, "success");
                } catch (Exception e) {
                    results.put(title, e.toString());
                    logger.warn("ImportJob contract create failed for " + title, e);
                    //error happen in that row, so continue next row
                }
            }
            importEvent.setResults(results);

        } else throw new Exception(String.format("%s file currently don't support", uploadedFile.getName()));
    }

    //Method which unzip zip file
    public static void unzip(File uploadedFile) throws Exception {
        if (uploadedFile == null || !uploadedFile.exists() || uploadedFile.isDirectory())
            throw new Exception("Zip file doesn't exist");

        File tempPath = new File(TEMP_FILE_PATH);
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(uploadedFile));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = newFile(tempPath, zipEntry);
            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs())
                    throw new IOException("Failed to create directory " + newFile);
            } else {
                // fix for Windows-created archives
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }
                // write file content
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    //Method which create a new file from zip
    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    //Method which return a file from temp folder
    public static File readTempFile(String fileName) {
        return new File(TEMP_FILE_PATH + File.separator + fileName);
    }

    //Method which write the bytes into a file
    public static void writeByte(byte[] bytes, File file) throws IOException {
        // Initialize a pointer
        // in file using OutputStream
        OutputStream os = new FileOutputStream(file);
        // Starts writing the bytes in it
        os.write(bytes);
        // Close the file
        os.close();
    }

    public static List<String> uploadFileFromTempFile(String tempFiles, IFileStorageController manager, Import importEvent) throws Exception {
        List<String> contractFilesUpdated = new ArrayList<>();
        for (String contractFile : tempFiles.split(",")) {
            File tempFile = readTempFile(contractFile);
            if (!tempFile.exists() || tempFile.isDirectory())
                throw new Exception("Temp file not found for Id: " + contractFile);

            Map<String, Object> metaData = new HashMap<>();
            String fileExtension = Files.getFileExtension(tempFile.getName());

            metaData.put(FileMetadata.FILE_EXTENSION, fileExtension);
            metaData.put(FileMetadata.ORIGINAL_NAME, URLDecoder.decode(tempFile.getName(), "UTF-8"));
            metaData.put(FileMetadata.AUTHOR, importEvent.getRequestor().getUuid());
            contractFilesUpdated.add(manager.storeFile(java.nio.file.Files.readAllBytes(tempFile.toPath()), fileExtension, metaData));
        }
        return contractFilesUpdated;
    }
}
